var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    io = require('socket.io').listen(server),
    mongo = require('mongodb').MongoClient;

server.listen(process.env.PORT || 3000);

app.get('/', function (req, res) {
    res.sendFile(__dirname + '/indexindex.html');
});

mongo.connect('mongodb://127.0.0.1/bigdatatag', function (err, db) {
    if (err) throw err;

    io.sockets.on('connection', function (socket) {

        var collectionClusterCenters = db.collection('clusterCenters');

        collectionClusterCenters.find().toArray(function (err, res) {
            if (err) throw err;
            socket.emit('clusterCenters', res);
        });

        var collectionMeasurements = db.collection('measurements');

        collectionMeasurements.find().toArray(function (err, res) {
            if (err) throw err;
            socket.emit('measurements', res);
        });

        var collectionClusters = db.collection('clusters');

        collectionClusters.find().toArray(function (err, res) {
            if (err) throw err;
            socket.emit('clusters', res);
        });

    });

});
