var express = require('express');
var app = express();
var hbase = require('hbase-rpc-client');
var bodyParser = require('body-parser')
var path = require('path');
app.use(bodyParser.json())

app.use('/public', express.static(path.join(__dirname, '/public')));
//Bower dependencies
app.use('/bower_components', express.static(path.join(__dirname, '/bower_components')));

app.get('/', function(req, res) {
  res.sendFile(__dirname + '/public/index.html');
});

app.post('/data', function(req, res) {
    console.log(req.body.sensor);
    var sensor = req.body.sensor;
    var starttime = req.body.starttime;
    var endtime = req.body.endtime;
    res.setHeader('Content-Type', 'application/json');
    var returnArray = [];
    this.addElement = function(err, row) {
        if (row) {
            returnArray.push([parseInt(row.row.toString('utf8').split('_')[1]), row.columns[0].value.readFloatBE()]);
        }
    }
    this.finish = function() {
       res.json( {"rows": returnArray});
       scan.close();
    }
    var scan = client.getScanner("button-events", sensor + "_" + starttime, sensor + "_" + endtime);
    scan.each (this.addElement, this.finish);
});

app.listen(process.env.PORT || 3000, function () {
  console.log('App running on ' + (process.env.PORT || 3000) + "!");
});

//Need to implement timing/wait until the client loads.

// HBase
var client = hbase({
  zookeeperHosts: [
    process.env.ZOOKEEPER_HOSTNAME
  ]
})

client.on("error", function(error) {
  console.log("hbase client error " + error);
});