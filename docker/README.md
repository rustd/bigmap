For running docker file, you need to install Docker

**For building docker file :**

`docker build -t bigmap .`

**For running it :**

`docker run -it -d -p 9092:9092 -p 2181:2181 -p 4040:4040 bigmap`

For attaching to terminal in container, you need to check container's id and use the attach command.

`docker ps -a`

`docker attach id`

This version has running Zookeeper, Kafka and Spark. When it starts, it creates a topic named "test"