package com.example.ipfsdemon.kafka;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducerIpfs {

    private final Producer<String, String> producer;

    public KafkaProducerIpfs() {
        KafkaProducerConfig config = new KafkaProducerConfig();
        Properties props = config.getProducerProperties();
        this.producer = new KafkaProducer<>(props);
    }

    public void sendMessage(String topic, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);

        try {
            // Synchronously send the record and get the metadata
            RecordMetadata metadata = producer.send(record).get();
            System.out.printf("Sent message to topic %s partition %d with offset %d%n",
                    metadata.topic(), metadata.partition(), metadata.offset());
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
