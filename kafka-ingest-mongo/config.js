"use strict";

const config = {
    stream: {
        zkConStr: "localhost:2181/",
        logger: {
            debug: msg => console.log(msg),
            info: msg => console.log(msg),
            warn: msg => console.log(msg),
            error: msg => console.error(msg)
        },
        groupId: "group1",
        clientName: "mongodbSink",
        workerPerPartition: 1,
        options: {
            sessionTimeout: 8000,
            protocol: ["roundrobin"],
            fromOffset: "earliest", //latest
            fetchMaxBytes: 1024 * 100,
            fetchMinBytes: 1,
            fetchMaxWaitMs: 10,
            heartbeatInterval: 250,
            retryMinTimeout: 250,
            autoCommit: true,
            autoCommitIntervalMs: 1000,
            requireAcks: 0,
        }
    },
    topic: "safecast",
    mongo:{
        url:'mongodb://localhost/bigdatatag',
        sinkCollection: 'measurements'
    }
}

module.exports = config;