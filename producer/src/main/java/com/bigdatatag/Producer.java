package com.bigdatatag;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Created by safak on 6/8/17.
 */
public class Producer {
    //TODO parameters required
    public static void main(String[] args) throws Exception {
        while (true) {
            //TODO 8GB file is big!!!
            String csvFile = "/home/safak/measurements.csv";
            CSVUtils.getAndSendData(csvFile, "localhost:9092", "test");
        }
    }
}
