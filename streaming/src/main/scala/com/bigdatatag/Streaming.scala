package com.bigdatatag

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import com.mongodb.spark._
import com.mongodb.spark.config.WriteConfig
import org.bson.Document


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

    val vectorTrainingData = parsedtrainingData.map(z => Vectors.dense(parseToDouble(z.getLatitude), parseToDouble(z.getLongitude), parseToDouble(z.getValue), parseToDouble(z.getHeight)))

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
          , clusters.predict(Vectors.dense(parseToDouble(z.getLatitude), parseToDouble(z.getLongitude), parseToDouble(z.getValue), parseToDouble(z.getHeight)))))

        //TODO DEBUG
        result.foreach(println)

        result.map(z => Document.parse("{\"_id\":\"" + z._1 +
          "\", \"cluster\":" + z._2 + " }")).saveToMongoDB(WriteConfig(Map("uri" -> "mongodb://127.0.0.1:27017/bigdatatag.measurement")))

      }
    })

    ssc.start()
    ssc.awaitTermination()
  }

  def parseToDouble(s: String): Double = try {
    s.toDouble
  } catch {
    case _: NumberFormatException => 0.0
  }

  // SqlContext hasn't been used in the project but this is a good way to use it in Streaming jobs
  object SQLContextSingleton {
    @transient private var instance: SQLContext = _

    def getInstance(sparkContext: SparkContext): SQLContext = {
      if (instance == null) instance = new SQLContext(sparkContext)
      instance
    }
  }

}