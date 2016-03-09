<div id="graph" class="aGraph"></div>

<style>
    .axis path,
    .axis line {
        fill: none;
        stroke: #000;
        shape-rendering: crispEdges;
    }
    .x.axis path {
        display: none;
    }
    .line {
        fill: none;
        stroke: steelblue;
        stroke-width: 1.5px;
    }
</style>

<script>
    var doPlot = function(arrData) {
        var margin = {top: 20, right: 20, bottom: 30, left: 50},
                width = 960 - margin.left - margin.right,
                height = 500 - margin.top - margin.bottom;
        //var parseDate = d3.time.format("%Y-%m-%d").parse;
        var x = d3.time.scale()
                .range([0, width]);
        var y = d3.scale.linear()
                .range([height, 0]);
        var xAxis = d3.svg.axis()
                .scale(x)
                .ticks(10)
                .orient("bottom");
        var yAxis = d3.svg.axis()
                .scale(y)
                .ticks(10)
                .orient("left");
        var line = d3.svg.line()
                .x(function(d) { return x(d.date); })
                .y(function(d) { return y(d.close); });
        //TODO fix this hack
        $('#graph').html('')
        var svg = d3.select("#graph").append("svg")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
        var data = arrData.map(function(d) {
            return {
                date: d[0],
                close: d[1]
            };
        });
        console.log(data);
        x.domain(d3.extent(data, function(d) { return d.date; }));
        y.domain(d3.extent(data, function(d) { return d.close; }));
        svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);
        svg.append("g")
                .attr("class", "y axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("Light");
        svg.append("path")
                .datum(data)
                .attr("class", "line")
                .attr("d", line);
    }
    $.getJSON("/photon/light-data-last/50", function(data){
        var addData = [];
        for(var i = 0; i< data['time'].length; i++){
            addData.push([ new Date(data['time'][i]),  data['light'][i]]);
        }
        doPlot(addData);
    });
    setInterval(function(){
        $.getJSON("/photon/light-data-last/100", function(data){
            var addData = [];
            for(var i = 0; i< data['time'].length; i++){
                addData.push([ new Date(data['time'][i]),  data['light'][i]]);
            }
            doPlot(addData);
        });
    }, 10000);
</script>