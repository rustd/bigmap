FROM centos:7
RUN yum -y install wget
RUN yum -y install java-1.8.0-openjdk-devel
RUN yum -y install net-tools
RUN wget -q -O - http://ftp.ps.pl/pub/apache/zookeeper/zookeeper-3.4.10/zookeeper-3.4.10.tar.gz | tar -xzf - -C /opt
RUN wget -q -O - http://ftp.ps.pl/pub/apache/kafka/0.10.2.1/kafka_2.11-0.10.2.1.tgz | tar -xzf - -C /opt
RUN wget -q -O - ftp://ftp.task.gda.pl/pub/www/apache/dist/spark/spark-2.1.1/spark-2.1.1-bin-hadoop2.7.tgz | tar -xzf - -C /opt
RUN mv /opt/zookeeper-3.4.10 /opt/zookeeper
RUN mv /opt/kafka_2.11-0.10.2.1 /opt/kafka
RUN mv /opt/spark-2.1.1-bin-hadoop2.7 /opt/spark
RUN mv /opt/zookeeper/conf/zoo_sample.cfg /opt/zookeeper/conf/zoo.cfg
RUN mkdir /opt/bigmap
RUN mkdir /opt/bigmap/producer
RUN yum -y install epel-release; yum clean all
RUN yum -y install mongodb mongodb-server; yum clean all
RUN mkdir -p /data/db

ENV PATH /opt/kafka/bin:/opt/zookeeper/bin:/opt/spark/bin:$PATH

EXPOSE 9092
EXPOSE 2181
EXPOSE 4040
EXPOSE 27017

CMD echo "*******start******" ;\
zkServer.sh start ;\
`nohup /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties >> /opt/kafka/out.txt &` ;\
echo "kafka started" ;\
kafka-topics.sh --create --topic test --zookeeper localhost:2181 --replication-factor 1 --partitions 1 ;\
`nohup mongod >> mongod_out.txt` ; \
/bin/bash
