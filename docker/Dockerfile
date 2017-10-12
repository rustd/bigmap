FROM centos:7
RUN yum -y install epel-release
RUN rpm -ivh https://kojipkgs.fedoraproject.org//packages/http-parser/2.7.1/3.el7/x86_64/http-parser-2.7.1-3.el7.x86_64.rpm
RUN rpm -ivh https://kojipkgs.fedoraproject.org//packages/http-parser/2.7.1/3.el7/x86_64/http-parser-devel-2.7.1-3.el7.x86_64.rpm
RUN yum -y install wget make gcc* git java-1.8.0-openjdk-devel net-tools mongodb mongodb-server nodejs-devel npm tmux htop
RUN wget -q -O - http://ftp.ps.pl/pub/apache/zookeeper/zookeeper-3.4.10/zookeeper-3.4.10.tar.gz | tar -xzf - -C /opt
RUN wget -q -O - http://ftp.ps.pl/pub/apache/kafka/0.10.2.1/kafka_2.11-0.10.2.1.tgz | tar -xzf - -C /opt
RUN wget -q -O - ftp://ftp.task.gda.pl/pub/www/apache/dist/spark/spark-2.1.1/spark-2.1.1-bin-hadoop2.7.tgz | tar -xzf - -C /opt
RUN mv /opt/zookeeper-3.4.10 /opt/zookeeper
RUN mv /opt/kafka_2.11-0.10.2.1 /opt/kafka
RUN mv /opt/spark-2.1.1-bin-hadoop2.7 /opt/spark
RUN mv /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg
RUN mkdir -p /data/db
RUN mkdir -p /data/fromSensor
RUN curl "https://api.safecast.org/system/measurements.csv" -s 2>&1 | head -300000 >> /data/fromSensor/measurements.txt
RUN git clone https://github.com/rustd/bigmap.git /opt/bigmap
RUN yum -y install maven
RUN mvn clean package -f /opt/bigmap/pom.xml
WORKDIR /opt/bigmap/kafka-ingest-mongo
RUN npm install
WORKDIR /opt/bigmap/visualization
RUN npm install
WORKDIR /

ENV PATH /opt/kafka/bin:/opt/zookeeper/bin:/opt/spark/bin:/opt/maven/bin:$PATH

EXPOSE 80
EXPOSE 2181
EXPOSE 4040
EXPOSE 8888
EXPOSE 9092
EXPOSE 27017

CMD echo "*******start******" ;\
zkServer.sh start ;\
`nohup /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties >> /opt/kafka/kafka_out.txt &` ;\
sleep 3 ;\
echo "kafka started" ;\
kafka-topics.sh --create --topic measurement --zookeeper localhost:2181 --replication-factor 1 --partitions 1 ;\
`nohup mongod --smallfiles >> mongod_out.txt &` ; \
sleep 3 ;\
mongo localhost:27017/bigdatatag /opt/bigmap/prepareMongoCollections.js ;\
`nohup java -jar /opt/bigmap/producer/target/producer-1.0-SNAPSHOT.jar /data/fromSensor/measurements.txt localhost:9092 measurement >> producer_out.txt &`;\
`nohup /opt/spark/bin/spark-submit --master local[*] --class com.bigdatatag.Streaming opt/bigmap/streaming/target/streaming-1.0-SNAPSHOT-jar-with-dependencies.jar localhost:9092 measurement /data/fromSensor/measurements.txt mongodb://127.0.0.1:27017 >> spark_out.txt &`;\
`nohup node /opt/bigmap/kafka-ingest-mongo/app.js >> node_ingest_out.txt &` ; \
`nohup node /opt/bigmap/visualization/app.js >> node_visualization_out.txt &` ; \
/bin/bash
