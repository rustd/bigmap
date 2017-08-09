package com.bigdatatag

import kafka.serializer.StringDecoder
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.ml.clustering.KMeans

object Streaming{
  def main(args: Array[String]): Unit = {
    val Array(brokers: String, topics: String) = args


    val sparkConf = new SparkConf()
      .setAppName("StreamingApp")
    val ssc = new StreamingContext(sparkConf, Seconds(2))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    // Create a new stream which can decode byte arrays.
    val messageStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc,
      kafkaParams,
      topicsSet)

    messageStream.foreachRDD(rdd => {

      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)

      if (!rdd.isEmpty()){

      }
    })

    ssc.start()
    ssc.awaitTermination()
  }

  object SQLContextSingleton {
    @transient private var instance: SQLContext = _

    def getInstance(sparkContext: SparkContext): SQLContext = {
      if (instance == null) instance = new SQLContext(sparkContext)
      instance
    }
  }
}