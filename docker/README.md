For running docker file, you need to install Docker

**For building docker file :**

`docker build -t bigmap .`

**For running it :**

`docker run -it -d -p 80:80 -p 9092:9092 -p 2181:2181 -p 4040:4040 -p 27017:27017 bigmap`

For attaching to terminal in container, you need to check container's id and use the attach command.

`docker ps -a`

`docker attach id`

This version has running Node, MongoDB, Zookeeper, Kafka and Spark. 
When it starts, it creates a topic named "measurement"


We are using SafeCast data. ( https://blog.safecast.org/data/ )

On this producer, we are only serving 300000 lines of data. Total size is over 8 GB.
If you want to work with all dataset, you need to change this line:

`RUN curl "https://api.safecast.org/system/measurements.csv" -s 2>&1 | head -300000 >> /data/fromSensor/measurements.txt`

to this:

`RUN curl "https://api.safecast.org/system/measurements.csv" -s >> /data/fromSensor/measurements.txt`