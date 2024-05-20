package main

import (
	"context"
	"encoding/json"
	"log"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/Shopify/sarama"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

const (
	kafkaTopic   = "HachCode_Topic"
	kafkaBrokers = "kafka-server:9092" // Update if needed
	mongoURI     = "mongodb://mongodb:27017"
	mongoDBName  = "Go_db"
	mongoColl    = "Go_collection"
)

func main() {
	// Set up a connection to MongoDB
	clientOptions := options.Client().ApplyURI(mongoURI)
	mongoClient, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		log.Fatalf("Failed to connect to MongoDB: %v", err)
	}
	defer mongoClient.Disconnect(context.TODO())

	collection := mongoClient.Database(mongoDBName).Collection(mongoColl)

	// Set up a new Sarama consumer group
	config := sarama.NewConfig()
	config.Consumer.Group.Rebalance.Strategy = sarama.BalanceStrategyRoundRobin
	config.Version = sarama.V2_5_0_0 // Set the appropriate Kafka version

	consumerGroup, err := sarama.NewConsumerGroup([]string{kafkaBrokers}, "my-group", config)
	if err != nil {
		log.Fatalf("Failed to create consumer group: %v", err)
	}
	defer consumerGroup.Close()

	// Set up a channel to listen for OS signals
	signals := make(chan os.Signal, 1)
	signal.Notify(signals, syscall.SIGINT, syscall.SIGTERM)

	// Middleware pour gérer les en-têtes CORS
	corsMiddleware := func(next http.Handler) http.Handler {
		return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
			w.Header().Set("Access-Control-Allow-Origin", "*")
			w.Header().Set("Access-Control-Allow-Methods", "GET, OPTIONS")
			w.Header().Set("Access-Control-Allow-Headers", "Content-Type")

			if r.Method == http.MethodOptions {
				w.WriteHeader(http.StatusOK)
				return
			}

			next.ServeHTTP(w, r)
		})
	}

	// Set up HTTP server for API
	http.HandleFunc("/data", func(w http.ResponseWriter, r *http.Request) {
		// Query MongoDB for data
		cur, err := collection.Find(context.Background(), bson.D{})
		if err != nil {
			http.Error(w, "Failed to query MongoDB", http.StatusInternalServerError)
			return
		}
		defer cur.Close(context.Background())

		var data []bson.M
		if err := cur.All(context.Background(), &data); err != nil {
			http.Error(w, "Failed to decode MongoDB documents", http.StatusInternalServerError)
			return
		}

		// Convert data to JSON and write to response
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(data)
	})

	// Utiliser le middleware CORS
	handler := corsMiddleware(http.DefaultServeMux)

	go func() {
		log.Fatal(http.ListenAndServe(":3336", handler))
	}()

	consumer := Consumer{
		ready:      make(chan bool),
		collection: collection,
	}

	ctx := context.Background()
	go func() {
		for {
			if err := consumerGroup.Consume(ctx, []string{kafkaTopic}, &consumer); err != nil {
				log.Fatalf("Error from consumer: %v", err)
			}
			// Check if context was cancelled, signaling that the consumer should stop
			if ctx.Err() != nil {
				return
			}
			consumer.ready = make(chan bool) // Reinitialize ready channel
		}
	}()

	<-consumer.ready // Await until the consumer has been set up
	log.Println("Sarama consumer up and running!")

	<-signals // Wait for a termination signal

	log.Println("Terminating...")
}

type Consumer struct {
	ready      chan bool
	collection *mongo.Collection
}

// Setup is run at the beginning of a new session, before ConsumeClaim
func (consumer *Consumer) Setup(_ sarama.ConsumerGroupSession) error {
	// Mark the consumer as ready
	close(consumer.ready)
	return nil
}

// Cleanup is run at the end of a session, once all ConsumeClaim goroutines have exited
func (consumer *Consumer) Cleanup(_ sarama.ConsumerGroupSession) error {
	return nil
}

// ConsumeClaim must start a consumer loop of ConsumerGroupClaim's Messages().
func (consumer *Consumer) ConsumeClaim(session sarama.ConsumerGroupSession, claim sarama.ConsumerGroupClaim) error {
	for message := range claim.Messages() {
		log.Printf("Message claimed: value = %s, timestamp = %v, topic = %s", string(message.Value), message.Timestamp, message.Topic)

		// Insert the message into MongoDB
		_, err := consumer.collection.InsertOne(context.TODO(), bson.M{
			"topic":     message.Topic,
			"partition": message.Partition,
			"offset":    message.Offset,
			"key":       string(message.Key),
			"value":     string(message.Value),
			"timestamp": message.Timestamp,
		})
		if err != nil {
			log.Printf("Failed to insert message into MongoDB: %v", err)
		}

		session.MarkMessage(message, "")
	}
	return nil
}
