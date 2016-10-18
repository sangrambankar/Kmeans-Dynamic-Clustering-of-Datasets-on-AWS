<!--  
/*
 * Ref: https://www.youtube.com/watch?v=n5NcCoa9dDU&list=PL6il2r9i3BqH9PmbOf5wA5E1wOG3FT22p
 * 		http://d3js.org/
 		https://github.com/adamhoward/websockets-demo/blob/master/websockets- client.html
		http://smoothiecharts.org/ https://weka.wikispaces.com/Converting+CSV+to+ARFF 
		http://www.websocket.org/echo.html
 */
 -->

<!doctype html>
<html>
<head>

<!--  Internal Style Sheet  -->

<style>
body {
	margin: 1px;
	width :100%;
	height :100%;
}

.h, .v {
	stroke-dasharray: 5 5;
	stroke-width: 1;
	stroke-opacity: .5;
}

.axis path, .axis line {
	fill: none;
	stroke: black;
	shape-rendering: crispEdges;
}

.axis text {
	font-family: sans-serif;
	font-size: 12px;
}
</style>


<title>Cluster Visualization</title>
<script src="http://d3js.org/d3.v3.min.js">
	
</script>
<script
	src="https://raw.githubusercontent.com/joewalnes/smoothie/master/smoothie.js">
	
</script>
<body bgcolor="#E6E6FA">
	<div>
		<script>
			var width = window.innerWidth; 
			var height = window.innerHeight;
			var margin = 300;
			//Get the number of clusters
			var clustnumb =	<%=request.getAttribute("clusters")%>;
			//Declaring color array to color each cluster
			var colors = [ "red", "green", "blue", "orange", "yellow",
					"violet", "black" ]
			var ccolors = [ "pink", "black", "violet", "red", "yellow",
					"violet", "black" ]
					
		//  
			var col1name = <%=request.getAttribute("col1name")%>
			var col2name = <%=request.getAttribute("col2name")%>;
		//	var mincol1 = <%=request.getAttribute("mincol1")%>
		//	var maxcol1 = <%=request.getAttribute("maxcol1")%>
		//	var mincol2 = <%=request.getAttribute("mincol2")%>
		//	var maxcol2 = <%=request.getAttribute("maxcol1")%>
			
			//Defining the Scalable Vector Graphics
			var svg = d3.select("body")
						.append("svg")
						.attr("width", width)
						.attr("height", height);
			
			var x = d3.scale.linear().domain([0,100]).range([ margin, width - margin]);
			var y = d3.scale.linear().domain([0,1]).range([ height - 50, 50 ]);

			var xAxis = d3.svg.axis().scale(x).orient("bottom");

			var yAxis = d3.svg.axis().scale(y).orient("left");

			//Appending the x-axis and y-axis to SVG

			svg.append("text") // text label for the x axis
				.attr("x", width/2)
				.attr("y", height-20)
				.style("text-anchor", "bottom")
				.text(col1name);

			svg.append("g")
				.attr("class", "axis")
				.attr("transform","translate(0," + (height - 50) + ")")
				.text("age")
				.call(xAxis);

			svg.append("text")
				.attr("transform", "rotate(-90)")
				.attr("y",margin-50) //text label for the y axis
				.attr("x",-(height/3))
				.attr("dy", "1em")
				.style("text-anchor", "middle")
				.text(col2name);

/*
			svg.append("g").attr("class", "y axis").call(yAxis).append("text")
			.attr("transform", "rotate(0)").attr("y", -200).attr("x", -300).attr("dy", ".71em")
			.style("text-anchor", "top").text("Energy - KWh");
*/

			svg.append("g")
				.attr("class", "axis")
				.attr("transform","translate(" + margin + ",0)")
				.call(yAxis);

			//Fetching the json data
			var D3data = <%=request.getAttribute("jcluster")%>
			var canvas = d3.select("body").append("svg").attr("width", width)
					.attr("height", height);


			//Plotting the cluster points on the graph
			svg.selectAll(".dot")
				.data(D3data)
				.enter()
				.append("circle")
				.attr("cx", function(d) {return x(d.col1);})
				.attr("cy", function(d) {return y(d.col2);})
				.attr("r", 4)
				.attr("y", function(d, i) {return i * 5;})
				.attr("fill", function(d) {return colors[d.totclusters];})
				.attr("class", "dot");
			
			var centroiddata = <%=request.getAttribute("jcentroid")%>
			svg.selectAll('.center')
				.data(centroiddata)
				.enter()
				.append('circle')
				.attr('cx', function(d){return  x(d.ccol1);})
				.attr('cy', function(d){return y(d.ccol2);})
				.attr("y", function(d, i) {return i * 5;})
				.attr('r', 6)
				.attr('style', function(d){return 'fill:' + ccolors[d.cclusters];})
				.attr('class','center')
				.append("svg:title")
				.text(function(d){return d.cclusters + ' cluster centeroid'});
		
			legend = svg.selectAll(".legend")
				.data(centroiddata)
				.enter().append("g")
				.attr("class", "legend")
				.attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; })
				.append("rect")
				.attr("x", width - 200)
				.attr("width", 18)
				.attr("height", 18)
				.style("fill", function(d) { return colors[d.cclusters-1];})
				.append("text")
				.attr("x", width - 180)
				.attr("dy", ".35em")
				.style("text-anchor", "end")
				.text(function(d){return d.cclusters + ' Cluster'})
				.call(legend);
				
				
		</script>
	</div>

</body>
</html>