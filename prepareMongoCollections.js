db.createCollection( "clusters", { capped: true, size: 300000000 } )
db.createCollection( "clusterCenters", { capped: true, size: 100} )
db.createCollection( "measurements", { capped: true, size: 300000000} )
