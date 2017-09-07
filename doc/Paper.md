
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

### Big Data Source
How big should our data source be? Certainly large enough to pose a scale problem at some point. Certainly it has to have either exceed or show potential for exceeding thresholds of arity, cardinality, or both.
A common choice is Twitter streams. We considered the three V: Volume, Velocity, Variety. Our solution should be able to address big data along the axis of at least 2 if not all V's.

We chose to use an IoT type source. IoT characteristics naturally supply volume and velocity. This is because the multitude of sources and the frequency of data adds up quickly. Variety in IoT is typically less pronounced. Over time, fixed-deployments may remain operational while new devices change schema or data structures. However it is less likely that _unknown_ or _surprising_ data variety will exist, because device deployment can be made in a planned fashion.

For our project, we chose to use [SafeCast.org's][4] data set. [SafeCast][4] is a wonderful IoT project that collects radiation information form open sourced IoT devices. Those IoT devices were voluntarily made and or deployed by the community and the data produced was collected and made public under open source as well. Having an readily available, well defined, large data set set us free to focus on the platform and processing rather than the "first mile" of collecting IoT information.  

### Architecture Component Choices 
When it comes to choosing the major architecture components, we had many offerings to choose from. This is great, because it means the market and community are supported by many options. It bodes well for the maturity and oncoming innovation in the field. Invariably, some choices offer wider flexibility, different cost structure, and better fit for some solutions - never universally one-size-fits all.

One concern was about the multitude of components we need. Reducing cost and complexity help both rapid development, as well as dev-ops and long term support efforts. To that end, we questioned our choices at every step to consider not only if the component fits, but also whether it is a result of inertia or "automatic" choice vs. a conscious cost-benefit choice. While it is certain that some choices are influenced by our familiarity or comfort level with some technologies, we need not shy away from tried-and-true solutions.

### Schema Considerations
Data is at the core of this project, and data needs to be understood in order to derive value. There was an early concern we had to deal with here: Schema. In what format would data be first introduced into the system? In what format should it be stored? In what format can current and future clients expect to access the data?

We discussed several concepts: Schema On Read, Schema On Write, and their variants. Since this is an IoT exercise at core, the data coming from field devices had an inherent structure (or schema) already. With that understanding in mind, there was no particular advantage in storing the data in HDFS or any other storage as "raw". We interpret the serialization protocol as schema as well, and the notion of "unknown data to be figured out later" was therefore not necessary. This realization led us to directly ingest and transform source data into Kafka. Should source IoT schema change at some point, that piece of the system would need to be reworked, but historic data would remain in it's original format.

Consuming data streams from Kafka presented similar choices. We opted to consume readings as individual line-item structure, with flat fields. Since we use MongoDB to store the data structure, it is in the connector that we would chose the destination cold-path schema for the data. MongoDB allows arbitrary data structure, so again we have the ability to revisit the schema should source data change. However, especially in IoT where devices are deployed once and updated rarely, such schema changes are not expected to be frequent, and can be mitigated at the public API level.


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

Data diagram below shows the flow sequence between main components in this project.

<img src="sequence-diagram.svg"/>

Diagram editor [here][3]. 

<!-- Mermaid diagram source  -->

<script >
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

</script>



[1]:https://docs.mongodb.com/manual/administration/production-notes

[2]:http://mongodb.github.io/node-mongodb-native/2.2/api/Collection.html#insertMany

[3]:https://mermaidjs.github.io/mermaid-live-editor/#/edit/c2VxdWVuY2VEaWFncmFtClNlbnNvciAtPj4gU2Vuc29yOiBSZWFkIFJhZGlhdGlvbgpvcHQgT25saW5lCiAgU2Vuc29yIC0+PiBDbG91ZCBTdG9yYWdlOiBTZW5kIFJlYWRpbmdzCmVuZApvcHQgT2ZmbGluZQogIFNlbnNvciAtLXggQ2xvdWQgU3RvcmFnZTogTWFudWFsIHVwbG9hZAplbmQKCkthZmthIC0tPkNsb3VkIFN0b3JhZ2U6IExvYWQgUmVhZGluZ3MKCkthZmthIC0+PiBLYWZrYTogRXhwb3NlIFN0cmVhbQoKS2Fma2EgLT4+IE1vbmdvREI6IENvbm5lY3RvciAvICBFVEwKClNwYXJrICAtLT4+IEthZmthOiBSZWFsIFRpbWUgQW5hbHl0aWNzCgpTcGFyayAgLS0+PiBNb25nb0RCOiBDb2xkIFBhdGggQW5hbHl0aWNzCgpTcGFyayAtLXggTW9uZ29EQjogU2F2ZSBMZWFybmVkIFJlc3VsdHMKCk1vbmdvREIgLS1YIFZpc3VhbGl6YXRpb24gOiBSZWFkIC8gRGlzcGxheQo=

[4]:https://safecast.org