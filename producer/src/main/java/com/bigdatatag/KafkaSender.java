package com.bigdatatag;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by safak on 6/8/17.
 */
public class KafkaSender {

    //TODO Producer can change
    public static void Sender(String server,String topic,String data) {
        Properties props = new Properties();
        props.put("bootstrap.servers", server);
        props.put("acks", "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);

        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);

        org.apache.kafka.clients.producer.Producer<String, String> producer = new KafkaProducer<>(props);

        //TODO Continuous stream?
        producer.send(new ProducerRecord<String, String>(topic, "measurement", data));

        producer.close();
    }
}
