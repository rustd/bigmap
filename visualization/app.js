console.log("App.js Starting");

var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    mubsub = require('mubsub'),
    io = require('socket.io')(server),
    mongo = require('mongodb').MongoClient;

server.listen(process.env.PORT || 80);

app.use(express.static('public'));

app.get('/', function (req, res) {
    res.sendFile(__dirname + '/index.html');
});

mongo.connect('mongodb://127.0.0.1/bigdatatag', function (err, db) {
    if (err) throw err;

    io.sockets.on('connection', function (socket) {

        var collectionClusterCenters = db.collection('clusterCenters');

        collectionClusterCenters.find().limit(10).toArray(function (err, res) {
            if (err) throw err;
            socket.emit('clusterCenters', res);
        });
    });
});

io.sockets.on('connection', function (socket) {

    var client = mubsub('mongodb://127.0.0.1/bigdatatag');

    var channelClusters = client.channel('clusters');

    client.on('error', console.error);
    channelClusters.on('error', console.error);

    channelClusters.subscribe('document', function (message) {
        socket.emit('clusters', message);


    });

});