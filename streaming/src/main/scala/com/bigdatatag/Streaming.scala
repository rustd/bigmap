package com.bigdatatag

import com.bigdatatag.entity.Measurement
import kafka.serializer.StringDecoder
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.mllib.clustering.StreamingKMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors


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
    val kafkaParams = Map[String, Object]("bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "stream")

    // Create a new stream which can decode byte arrays.
    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topicsSet, kafkaParams)
    )


    // Load and parse the data
    //TODO change to args
    val trainingData = ssc.sparkContext.textFile("/data/fromSensor/measurements.txt")

    val header = trainingData.first()
    val dropHeader = trainingData.filter(row => row != header)

    val parsedtrainingData = dropHeader.map(CSVUtils.parseCsvLine).filter(f => f.getUnit.equals("cpm"))
    //TODO DEBUG
    parsedtrainingData.foreach(println)

    val vectorTrainingData = parsedtrainingData.map(z => Vectors.dense(z.getLatitude, z.getLongitude, z.getValue, z.getHeight))

    val numClusters = 7
    val numIterations = 10
    val clusters = KMeans.train(vectorTrainingData, numClusters, numIterations)

    //TODO save cluster centers to MongoDB


    kafkaStream.foreachRDD((rdd, time) => {

      if (!rdd.isEmpty()) {
        val measurementRDD = rdd.map(z => JsonParser.parseJson(z.value()))

        //TODO DEBUG
        measurementRDD.take(5).foreach(println)

        val filteredMeasurementRDD = measurementRDD.filter(f => f.getUnit.equals("cpm"))

        val result = filteredMeasurementRDD.map(z => (z.getDeviceID + "-" + z.getCapturedTime
          , clusters.predict(Vectors.dense(z.getLatitude, z.getLongitude, z.getValue, z.getHeight))))

        //TODO DEBUG
        result.foreach(println)

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