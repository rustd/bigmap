db.createCollection( "clusters", { capped: true, size: 300000 } )
db.createCollection( "clusterCenters", { capped: true, size: 10} )
db.createCollection( "measurements", { capped: true, size: 300000} )
