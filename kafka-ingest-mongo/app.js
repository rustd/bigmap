const { KafkaStreams } = require('kafka-streams');
var MongoClient = require('mongodb').MongoClient
var assert = require('assert');

const config = require('./config.js');

const factory = new KafkaStreams(config.stream);

const stream = factory.getKStream(config.topic);

stream.forEach(message => {
    processMessage(message);
})

stream.start().then(() => {
    console.log("Stream::start ok");
},
    error => {
        console.error("Stream::start failed: " + error);
    });

function processMessage(message) {
    console.log(message);
    var doc = JSON.parse(message.value);
    doc['_id'] = [doc.deviceID, doc.capturedTime].join('-');
    saveDoc('measurement', doc);
}


function saveDoc(collectionName, doc) {
    MongoClient.connect(config.mongo.url)
        .then(db => {
            db.collection(config.mongo.sinkCollection)
                .updateOne({ _id: doc._id }, doc, { upsert: true })
                .then(r => {
                    db.close();
                    console.log(r.result);
                });
        }).catch(err => {
            console.log(err);
            throw err;
        });
}
