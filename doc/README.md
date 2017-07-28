# BigMap

## Architecture principles

Big Data architectures often include these elements: 

- Ingestion
    - raw data typically comes from IoT, social networks, Web analytics, etc.
    - a broker like Kafka stores data in a scalable way
        - data has typically a retention from a few days to a few months
        - broker consumers can rewind the log. They can provide an offset or a timestamp from which they want to read. Several consumers can read from the broker independantly (consumer groups).
- Hot Path
    - Data is processed on the fly. It goes from the broker to the results. The types of processing include
        - aggregations
        - machine learning predictions
        - alert generations
    - Reasons for preferring this approach over cold path include
        - get results as soon as possible
        - have a regular processing of the data that keeps flowing, and avoid being overwhelmed
    - Processing engines and frameworks include Apache Spark Streaming, Flink, Samza, Beam, Storm, Apex, Kafka Streams.
- Cold Path
    - Data is stored as state (vs event logs like in a broker) in a data lake.
    - Data Lake is a scalable distributed file system that can store any type of files. This allows the schema on read approach (vs schema on write for Business Intelligence / relational database architectures). Typical file formats in a data lake include CSV or JSON, GZipped CSV or JSON, schema aware formats like AVRO, column oriented file formats like ORC or Parquet.
    - Data lake can have several dataset layers of refinement going from a copy of the broker data, until results of complex queries. 
        - On premises, a very important implementation of data lakes is HDFS. In that case, the main rule is to have colocality with compute nodes also being data nodes.
        - In the cloud, object or file storage services like AWS S3, Google File System, Azure Blob storage, Azure Data Lake Store are often used instead of HDFS. In that case, compute is separated from storage. A compute cluster talks to a storage cluster.
    - Processing is done against the data lake in batch mode. Queries can take from a few seconds to several hours or days, depending on the volume of the data to process, the complexity of the query, and the level of preparation of the data. It may be more interesting to run a 2-day query against unprepared CSV data rather than a 2 second query against data that will be prepared for 2 weeks. 
    - Typical engines for cold path processing include Hadoop Map/Reduce, Spark, Hive, Pig, ...
    - One type of cold path processing is machine learning training.
    - Processing often takes data from the data lake and stores it in another folder of the data lake. Being able to tell that the output folder comes from the source folder plus some processing is called lineage.
- Machine Learning
    - it can learn from a prepared dataset that was stored in the data lake. This generates a model. It's done as part of the cold path.
    - A model is then used to predict new values. This can be done in the hot path or in the cold path.
    Machine learning predictions (also called scoring, because this is the same operation used for a model evaluation) can also be called by Web APIs.
    - Egnines and framework include Spark MLLib, H2O, Tensorflow, Caffe, Torch, CNTK, SciKit-Learn, R.
- Serving layer
    - a data lake is not optimized to serve row level data with a low latency. This is the job of a database engine. A database engine has in memory cache, indexing, storage optimizations.
    - databases are generally separated as follows: 
        - SQL / relational can nearly only scale up. They allow versatile types of queries.
        - noSQL scale out
            - documents / JSON like MongoDB
            - wide columns like HBase or Cassandra
            - graphs like Neo4J or TitanDB
            - key/values like Voldemort
            - cache optimized like Redis
- Web API, Applications
    - This is the last link between big data and humans. They typically get their data from the serving layer.
- Business Intelligence, DataViz
    - This is another layer that can be used to show prepared data to humans.

In the case of BigMap, the focus is on the hot path rather than the cold path. The following choices are made: 
- broker: Apache Kafka
- hot path processing engine: Spark Structured Streaming
- Machine Learning: Spark MLLib
- Serving layer: Mongo DB
