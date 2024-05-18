FROM golang:1.22.3
WORKDIR /app

COPY go.mod .
COPY go.sum .
RUN go mod download

COPY . .
EXPOSE 3336
RUN go build -o bin main.go
ENTRYPOINT [ "/app/bin" ]
