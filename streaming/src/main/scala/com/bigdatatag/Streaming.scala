package com.bigdatatag

import kafka.serializer.StringDecoder
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}


object Streaming extends Serializable {
  private val EXTRA_CLASS_PATH = "spark.executor.extraClassPath"
  private val CURRENT_DIRECTORY = "./"

  def main(args: Array[String]) {

    val Array(brokers: String, topics: String) = args


    // Create context with 1 second batch interval
    val sparkConf = new SparkConf()
      .setAppName("StreamingApp")
      .set(EXTRA_CLASS_PATH, CURRENT_DIRECTORY)
    val ssc = new StreamingContext(sparkConf, Seconds(1))

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    // Create a new stream which can decode byte arrays.
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc,
      kafkaParams,
      topicsSet)


    kafkaStream.foreachRDD((rdd, time) => {

      if (!rdd.isEmpty()) {
        rdd.map(z => println(z))
        println("+++++++++++++" + rdd.count())
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