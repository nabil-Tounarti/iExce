FROM golang:1.22.3

# Copy your Go application files
COPY . /app
WORKDIR /app

# Install dependencies and build your Go application
RUN go build -o main .

# Copy the wait-for-it.sh script
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Start the application with the wait-for-it script
CMD ["./wait-for-it.sh", "kafka-server:9092", "--", "./main"]

