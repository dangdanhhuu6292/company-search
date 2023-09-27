appcontrollers.controller('OverviewTabController', ['$scope', '$rootScope', '$filter', 'ngTableParams', 'DashboardService', function($scope, $rootScope, $filter, ngTableParams, DashboardService) {
	//var myColors = ["#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd", "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"];
	var myColors = ["#1f77b4", "#064877", "#65a4d1", "#085e9a", "#3f8abf", "#043a60", "#266ea1", "#012844", "#095891", "#6aa2c9"];
	d3.scale.myColors = function() {
		return d3.scale.ordinal().range(myColors);
	};

	$scope.nrOfItems2 = 7;

	$scope.piechartoptions = {
		chart: {
			type     : 'pieChart',
			height   : 350,
			x        : function(d) {
				return d.key;
			},
			y        : function(d) {
				return d.y;
			},
			labelType: 'percent'
		}
	};

	$scope.piechartexampleData = [{key: "One", y: 5},
		{key: "Two", y: 2},
		{key: "Three", y: 9},
		{key: "Four", y: 7},
		{key: "Five", y: 4},
		{key: "Six", y: 3},
		{key: "Seven", y: 9}
	];

	nv.addGraph(function() {
		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			.y(function(d) {
				return d.y
			})
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height);

		d3.select("#test1")
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		//         chart.color(function(d) {
		//        	 return "#1f77b4";
		//         });

		chart.dispatch.on('stateChange', function(e) {
			nv.log('New State:', JSON.stringify(e));
		});

		return chart;
	});

	nv.addGraph(function() {
		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			.y(function(d) {
				return d.y
			})
			//.color(d3.scale.category10().range())
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height);

		d3.select("#test3")
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		chart.dispatch.on('stateChange', function(e) {
			nv.log('New State:', JSON.stringify(e));
		});

		return chart;
	});

	nv.addGraph(function() {

		var width = 350,
			height = 350;

		var chart = nv.models.pieChart()
			.x(function(d) {
				return d.key
			})
			//.y(function(d) { return d.value })
			//.labelThreshold(.08)
			//.showLabels(false)
			.color(d3.scale.myColors().range())
			.width(width)
			.height(height)
			.donut(true);

		chart.pie
			.startAngle(function(d) {
				return d.startAngle / 2 - Math.PI / 2
			})
			.endAngle(function(d) {
				return d.endAngle / 2 - Math.PI / 2
			});

		//chart.pie.donutLabelsOutside(true).donut(true);

		d3.select("#test2")
			//.datum(historicalBarChart)
			.datum($scope.piechartexampleData)
			.transition().duration(1200)
			.attr('width', width)
			.attr('height', height)
			.call(chart);

		return chart;
	});

	$scope.xFunction = function() {
		return function(d) {
			return d.key;
		};
	};
	$scope.yFunction = function() {
		return function(d) {
			return d.y;
		};
	};

	$scope.descriptionFunction = function() {
		return function(d) {
			return d.key;
		}
	};

	var data = [{
		shipmentId        : 100,
		htmlUrl           : "/edit/100",
		nameCreator       : "L.T. Late",
		nameLastEditor    : "P.W. Herman",
		exportOrganisation: "WijVerzendenAlles B.V.",
		importOrganisation: "Qatar Juwels",
		description       : "10k watches",
		dateShipment      : "30-06-2014",
		dateCreated       : "10-06-2014",
		dateLastChange    : "15-06-2014",
		documents         : [{
			documentId      : 123,
			htmlUrl         : "/document/123",
			documentType    : "CVO",
			documentStatus  : "Application",
			dateCreated     : "10-06-2014",
			personLastChange: "M. op den Oever",
			indEditable     : true
		},
			{
				documentId      : 124,
				htmlUrl         : "/document/124",
				documentType    : "Attachment",
				documentStatus  : "Uploaded",
				dateCreated     : "14-06-2014",
				personLastChange: "M. op den Oever",
				indEditable     : true
			}]
	},
		{
			shipmentId        : 101,
			htmlUrl           : "/edit/101",
			nameCreator       : "O. Lovely",
			nameLastEditor    : "P.W. Herman",
			exportOrganisation: "Tulip B.V.",
			importOrganisation: "Flowers from Russia",
			description       : "10k watches",
			dateShipment      : "30-06-2014",
			dateCreated       : "10-06-2014",
			dateLastChange    : "15-06-2014",
			documents         : [{
				documentId      : 125,
				htmlUrl         : "/document/125",
				documentType    : "CVO",
				documentStatus  : "Application",
				dateCreated     : "10-06-2014",
				personLastChange: "M. op den Oever",
				indEditable     : true
			},
				{
					documentId      : 126,
					htmlUrl         : "/document/126",
					documentType    : "Attachment",
					documentStatus  : "Uploaded",
					dateCreated     : "14-06-2014",
					personLastChange: "M. op den Oever",
					indEditable     : true
				}]
		}];

	$scope.tableParams = new ngTableParams({
		page : 1,            // show first page
		count: 10           // count per page
	}, {
		total  : data.length, // length of data
		getData: function($defer, params) {
			$defer.resolve(data.slice((params.page() - 1) * params.count(), params.page() * params.count()));
		}
	});

}]);