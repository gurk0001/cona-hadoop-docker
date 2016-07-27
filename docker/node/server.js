var express = require('express');
var app = express();
var hbase = require('hbase-rpc-client');

app.use('/static', express.static(__dirname + '/public'));
//Bower dependencies
app.use(express.static(__dirname + '/bower_components'));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/index.html');
});

app.listen(process.env.PORT, function () {
  console.log('App running on ' + process.env.PORT + "!");
});

//Need to implement timing/wait until the client loads.

// HBase
var client = hbase({
  zookeeperHosts: [
    "192.168.99.100"
  ]
})

client.on("error", function(error) {
  console.log("hbase client error " + error);
});

var scan = client.getScanner("button-events")
scan.each (function (err, row) {
    if (row) {
        console.log(row.row.toString());
    }
});
