This producer takes 3 arguments

CSV File of measurements

Kafka Broker address

Kafka port

Example Usage:

`java -jar producer-1.0-SNAPSHOT.jar /data/fromSensor/measurements.txt localhost:9092 measurement`


We are using SafeCast data. ( https://blog.safecast.org/data/ )

On this producer, we are only serving 300000 lines of data. Total size is over 8 GB. 
If you want to test with all data, refer the instructions on docker readme.

