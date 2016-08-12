//var starttime = 146964470089
var starttime = Date.now() - 1000;
var endtime = Date.now();
var purgeArray = [];
var chart = c3.generate({
    data: {
        x: 'time',
        xFormat: '%Y-%m-%dT%H:%M:%S.%LZ',
        rows: []
    },
    axis: {
        x: {
            type: 'timeseries',
            tick: {
                format: '%H:%M:%S.%L'
            }
        },
        y: {
            min: 0,
            max: 200
        }
    }
});
var getData = function() {
    setTimeout(function () {
        $.ajax({
            type: "POST",
            url: "/data",
            data: JSON.stringify({"sensor": "button-sensor-1","starttime": starttime, "endtime": endtime}),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(data){
                console.log(data.rows);
                endtime = Date.now();
                if (data.rows.length == 0) {
                    console.log("failure");
                    return;
                }
                starttime = Date.now() - 1000;
                data.rows.unshift(["time", "value"])
                chart.flow({
                    rows: data.rows,
                });
            },
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        getData();
    }, 1000);
}
getData();
