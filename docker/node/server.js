var express = require('express');
var app = express();
var hbase = require('hbase-rpc-client');
var bodyParser = require('body-parser')
app.use(bodyParser.json())

app.use('/static', express.static(__dirname + '/public'));
//Bower dependencies
app.use(express.static(__dirname + '/bower_components'));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/index.html');
});

app.post('/data', function(req, res) {
    console.log(req.body.sensor);
    var sensor = req.body.sensor;
    var timestamp = req.body.timestamp;
    res.setHeader('Content-Type', 'application/json');
    var returnArray = [];
    this.addElement = function(err, row) {
        if (row) {
            returnArray.push([row.row.toString('utf8').split('_')[1], row.columns[0].value.toString('utf8')]);
        }
    }
    this.finish = function() {
       res.json( {"data": returnArray});
       scan.close();
    }
    var scan = client.getScanner("button-events", sensor + "_" + timestamp);
    scan.each (this.addElement, this.finish);
});

app.listen(process.env.PORT || 3001, function () {
  console.log('App running on ' + (process.env.PORT || 3001) + "!");
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


//get = new hbase.Get("button-sensor-1_1469644700893");

//client.get("button-events", get, addElement);
