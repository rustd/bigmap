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

Run the application:

```bash
node app.js
```

