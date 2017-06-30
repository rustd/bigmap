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
    doc['_id'] = [ doc.DeviceID, doc.CapturedTime].join('-');
    saveDoc('measurement', doc);
}

function saveDoc(collectionName, doc) {
    console.log(doc);
//     MongoClient.connect(url, function (err, db) {
//         assert.equal(null, err);
//         console.log("Connected successfully to server");

//         db.collection(collectionName).insertOne(doc, function (err, r) {
//             assert.equal(null, err);
//             assert.equal(1, r.insertedCount);
//             db.close();
//         });

//    }
}
