# BigMap

## Kafka Ingest MongoDB

1. Ingest events of interest (Measurments)
1. Cold storage in MongoDB
1. Kafka stream topic already defines the "schema", since the message published to it is in a JSON format  that is defined by the Producer.


To run this application
============
Review the file `config.js` to ensure the settings are correct for your environment:

 - MongoDB connection 
 - ZooKeeper connection
 - Topic this program will consume

If the topic is not yet created, create it:
```bash
# Creating a topic named 'safecast'
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic safecast
```

Run the application:

```bash
node app.js
```

To see the ingestion at work, push a message to the topic:
```bash
# A producer to push messages into the topic 'safecast'
kafka-console-producer --broker-list localhost:9092 --topic safecast
# Now at the prompt, enter a JSON object and hit [ENTER] to send:
> { "_id" : "abc-123", "DeviceID" : "abc", "CapturedTime" : 123, "Value" : 2, "Unit" : "rad" }

```