
# BigMap 

## Outline
1.  Big Data Landscape 
1.  Big Data in containers 
1.  Big data in cloud
1.  Problem description
1.  Description of project
1.  Why we chose what tech 
    1. Spark
    1. Mongo    
    1. Kafka
    1. Docker 
    1. Kafka-MongoDB connector
1. Implementation  

## Big Data Landscape 

## Big Data in containers 

## Big data in cloud

## Problem description

## Description of project

## Why we chose what tech 
### Spark 

### MongoDB
Data needs to be ingested, stored, and analyzed as part of the big-data pipelines. We say "stored" to mean that data should be made available to later processing and re-processing as needed. We say "analyzed" to mean that data should be readily queried and dissected by other parts of the pipeline. Queries may be well defined or ad-hoc. In either sense, we want the data to be available with least friction. We don't want to have to run repeated parsing of data in order to extract value. We also want queries to perform well at any scale. 
MongoDB offers a good fit here, due to some key characteristics:
- It supports sharding, which allows for horizontal scaling out as needed.
- It offers rich indexing on data fields, including geo indexes, text indexing and compound indexing. 
- It offers flexible document model, which allows for ingestion of virtually any data structure, yet exposes the fields of such document as first-class, strongly typed fields for further processing as needed.

### Kafka 
### Docker 
### Connecting Kafka and MongoDB
In contemporary big-data systems, major infrastructure pieces are implemented as discrete servers. These components are then interconnected using some well known patterns. Among those patterns are:
- Push - Where a source system actively sends a destination system with data.
- Pull - Where a destination system actively queries a source system for new or changed data.

Both patterns are further refined along the lines of the mode in which connectivity occurs:
- Direct: Where one system member must directly connect to the other using API exposed by the target system
- Buffered: Where an intermediary queue or bus acts as an ephemeral store to which both systems connect
- Mediated: Where an intermediary process which connects to both systems and may perform transformation along the way.

## Implementation

### Spark

### Kafka

### MongoDB
MongoDB has been implemented in this project as a stand-alone single instance, with the minimal necessary settings to enable usage. In production environments, a more robust installation would be needed. Specifically, high availability, capacity, performance, and security considerations would surely need to be addressed. These considerations are part of normal architecture and design but are outside the scope of this paper. As with any database or data infrastructure, decisions should be made whether to self-host MongoDB or use database-as-a-service.
For more on preparing MongoDB  for production deployment [read here][1]. 
 
### Connecting Kafka and MongoDB
We wrote a custom connector in Node to connect MongoDB to the Kafka stream. Node is a ubiquitous player in many systems, enjoys an active community and rich component eco-system.
Taking a dependency on 2 key modules (one for MongoDB, one for Kafka) we were able to write a very simple process which can then be scheduled or run continuously to listen on the Kafka stream, then persist the data into MongoDB.
A minimal data-translation is done, to ensure that the result document is uniquely identified, in case we want to  resume the connector in the face of failure or downtime.

A key choice had to be made so that documents in MongoDb have a unique identifier tying the destination data to the source in the Kafka stream. Since MongoDb already enforces unique ID, this gives us nice idempotent resumability.

If the connector becomes a bottleneck, one can easily implement additional measures. 
The connector can easily be extended to listen on one partition of the stream. You can then  run multiple connectors, each processing a subset of the messaged in parallel.

The messages are saved one at a time into MongoDB. It is trivial to have the connector accumulate a batch of messages, then save them in [batch mode][2] into MongoDb. This speeds up the write since it reduces the wire protocol overhead for each document.

----
# Appendix
## Data Flow Diagram
[Online sequence diagram][3]
<link rel="stylesheet" href="https://unpkg.com/mermaid@7.0.8/dist/mermaid.min.css">
<script src="https://unpkg.com/mermaid@7.0.8/dist/mermaid.min.js">
</script>
<script type="text/javascript">
mermaid.initialize({startOnLoad:true});
</script>

<div class="mermaid">
sequenceDiagram
Sensor ->> Sensor: Read Radiation
opt Online
  Sensor ->> Cloud Storage: Send Readings
end
opt Offline
  Sensor --x Cloud Storage: Manual upload
end

Kafka -->Cloud Storage: Load Readings

Kafka ->> Kafka: Expose Stream

Kafka ->> MongoDB: Connector /  ETL

Spark  -->> Kafka: Real Time Analytics

Spark  -->> MongoDB: Cold Path Analytics

Spark --x MongoDB: Save Learned Results

MongoDB --X Visualization : Read / Display

</div>

----

[1]:https://docs.mongodb.com/manual/administration/production-notes
[2]:http://mongodb.github.io/node-mongodb-native/2.2/api/Collection.html#insertMany
[3]:https://mermaidjs.github.io/mermaid-live-editor/#/edit/c2VxdWVuY2VEaWFncmFtClNlbnNvciAtPj4gU2Vuc29yOiBSZWFkIFJhZGlhdGlvbgpvcHQgT25saW5lCiAgU2Vuc29yIC0+PiBDbG91ZCBTdG9yYWdlOiBTZW5kIFJlYWRpbmdzCmVuZApvcHQgT2ZmbGluZQogIFNlbnNvciAtLXggQ2xvdWQgU3RvcmFnZTogTWFudWFsIHVwbG9hZAplbmQKCkthZmthIC0tPkNsb3VkIFN0b3JhZ2U6IExvYWQgUmVhZGluZ3MKCkthZmthIC0+PiBLYWZrYTogRXhwb3NlIFN0cmVhbQoKS2Fma2EgLT4+IE1vbmdvREI6IENvbm5lY3RvciAvICBFVEwKClNwYXJrICAtLT4+IEthZmthOiBSZWFsIFRpbWUgQW5hbHl0aWNzCgpTcGFyayAgLS0+PiBNb25nb0RCOiBDb2xkIFBhdGggQW5hbHl0aWNzCgpTcGFyayAtLXggTW9uZ29EQjogU2F2ZSBMZWFybmVkIFJlc3VsdHMKCk1vbmdvREIgLS1YIFZpc3VhbGl6YXRpb24gOiBSZWFkIC8gRGlzcGxheQo=
