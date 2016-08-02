//var starttime = 146964470089
var starttime = Date.now() - 5000;
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
            max: 1,
            min: -1
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
                    return;
                }
                starttime = Date.now() - 5000;
                data.rows.unshift(["time", "value"])
                chart.load({
                    rows: data.rows,
                    unload: chart.rows,
                });
            },
            failure: function(errMsg) {
                alert(errMsg);
            }
        });
        getData();
    }, 5000);
}
getData();

