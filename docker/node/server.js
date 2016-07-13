var express = require('express');
var app = express();
var hbase = require('hbase-rpc-client');

app.use('/static', express.static(__dirname + '/public'));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/index.html');
});

app.listen(process.env.PORT, function () {
  console.log('App running on ' + process.env.PORT + "!");
});

var client = hbase({
  zookeeperHosts: [
    process.env.ZOOKEEPER_QUORUM
  ]
})
client.on("error", function(error) {
  console.log("hbase client error " + error);
});

