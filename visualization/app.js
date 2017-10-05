console.log("App.js Starting");

var express = require('express'),
    app = express(),
    server = require('http').createServer(app),
    mubsub = require('mubsub'),
    io = require('socket.io')(server);

server.listen(process.env.PORT || 3000);

app.use(express.static('public'))

app.get('/', function (req, res) {
    res.sendFile(__dirname + '/index.html');
});


var client = mubsub('mongodb://127.0.0.1/bigdatatag');
var channel = client.channel('clusters');

client.on('error', console.error);
channel.on('error', console.error);

channel.subscribe('document', function (message) {
    console.log(message);

    io.on('connection', function (socket) {

        // when the client emits 'new message', this listens and executes
        socket.on('clusters', function () {
            // we tell the client to execute 'new message'
            socket.broadcast.emit('clusters', message);
        });
    });
});
