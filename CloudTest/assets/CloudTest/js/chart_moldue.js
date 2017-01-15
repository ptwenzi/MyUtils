/**
 * 生成饼状图
 * @param {Object} args
 */
var Piechart = {

	//生成基础饼图
	genSimplePieChart: function(args) {
		var defaultArgs = {
			pid: "",
			attrId: "", //整个图表id
			title: "某站点用户访问来源", //标题
			tip: "副标题", //副标题
			seriesData: new Array(), //内容对象 value 对应的值  name 对应的名称;
			bindingClick:function(){},
			colors:['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],//调色盘颜色列表。如果系列没有设置颜色，则会依次循环从该列表中取颜色作为系列颜色。
			//['#c23531','#2f4554', '#61a0a8', '#c4ccd3'] 不传默认用系统
			tooltip:{trigger: 'item',formatter: "{a} <br/>{b} : {c} ({d}%)"},
		};
		var _args = $.extend({}, defaultArgs, args);
		var html = ' <div id="' + _args.attrId + '" style="width: 100%;height:400px;"></div>';
		$(_args.pid).append(html);

		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById(_args.attrId));
		// 指定图表的配置项和数据
		option = {
			title: {
				text: _args.title,
				subtext: _args.tip,
				x: 'center'
			},
			tooltip: _args.tooltip
			,
			/*		    legend: {
					        orient: 'vertical',
					        left: 'left',
					        data: ['直接访问','邮件营销','联盟广告','视频广告','搜索引擎']
					    },*/
			color:_args.colors,
			series: [{
				name: '访问来源',
				type: 'pie',
				radius: '55%',
				center: ['50%', '60%'],
				data: _args.seriesData,
				itemStyle: {
					emphasis: {
						shadowBlur: 30,
						shadowOffsetX: 0,
						shadowColor: 'rgba(0, 0, 0, 0.5)'
					}
				}
			}]
		};

		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(option);
		myChart.on("click",_args.bindingClick);
	},


}


/**
 * 柱状图
 */
var Columnar = {

	/**
	 * 生成基本版柱状图
	 * @param {Object} args
	 */
	genSimpleColumnarChart: function(args) {
		var defaultArgs = {
			basePath:"",
			pid: "",
			attrId: "", //整个图表id
			title: "这是标题", //标题
			legend: new Array(), //['蒸发量','降水量'] 指标数
			xAxisData: new Array(), //x轴横向指标  ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
			seriesData: new Array(), //内容对象 value 对应的值  name 对应的名称;
			//  name:'蒸发量',
			//  type:'bar',
			//data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
			bindingClick:function(){},
			isShowMakePoint:false,//是否显示标注点
			colors:['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],//调色盘颜色列表。如果系列没有设置颜色，则会依次循环从该列表中取颜色作为系列颜色。
			//['#c23531','#2f4554', '#61a0a8', '#c4ccd3'] 不传默认用系统
			commentId:"commentIds",
			shareId:"shareId",
			gridHeigh:200,
			charHigh:300,
			isShowBottom:true,
			tooltip:{trigger: 'axis'},
			legendShow:false,
			
		};
		var _args = $.extend({}, defaultArgs, args);
		var chartLegend={};
		if(_args.legendShow)
		{
			chartLegend= { //是否显示标题提示
				data: _args.legend
			};
		}
		
		var html = '<h4 >' + _args.title + '</h4>' +
			'<div id="' + _args.attrId + '" style="width: 100%;height:'+_args.charHigh+'px;"></div>';
			if(_args.isShowBottom)
			{
				html+='<div class="charts_bottom">'+
				'<div class="grade" id="'+_args.commentId+'"> <img src="'+_args.basePath+'img/pl.png"/></div>'+
				'<div class="line"></div>'+
				'<div class="grade" id="'+_args.shareId+'" > <img src="'+_args.basePath+'img/zf.png"/></div>'+
				'</div>';
			}
	
		$(_args.pid).append(html);

		
		
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById(_args.attrId));
		
		//取出legend
		for (var i = 0; i < _args.seriesData.length; i++) {
			_args.legend.push(_args.seriesData[i].name);
		}

				//是否显示标注点
		if(_args.isShowMakePoint)
		{
			for (var i=0; i<_args.seriesData.length; i++) {
				var serie = _args.seriesData[i];
				var seriesdata = _args.seriesData[i].data;
				var markPoint = new Object();
				markPoint.data = new Array();
				//获取元数组数据
				for(var j=0;j<serie.data.length;j++)
				{
					var data = new Object();
					data.value = serie.data[j];
					data.xAxis = j;
					data.yAxis = serie.data[j];
					data.symbolSize = 50;
			/*		data.label = {normal:{textStyle:{fontSize :5}}};*/
					markPoint.data.push(data);
				}
				_args.seriesData[i].markPoint = markPoint;
			}
		}
		
		
		option = {
			tooltip: _args.tooltip,

			toolbox: {

			},
			grid:{height :_args.gridHeigh,
			
			},
			
			legend: chartLegend,
			xAxis: {
				type: 'category',
				data: _args.xAxisData,
	/*			 axisLabel:{
					rotate:10
				   }*/

			},
			color:_args.colors,
			yAxis: {
				type: 'value',

				/*   {
				       type: 'value',
				       name: '水量',
				       min: 0,
				       max: 250,
				       interval: 50,
				       axisLabel: {
				           formatter: '{value} ml'
				       }
				   }*/
				/*     ,
				     {
				         type: 'value',
				         name: '温度',
				         min: 0,
				         max: 25,
				         interval: 5,
				         axisLabel: {
				             formatter: '{value} °C'
				         }
				     }*/
			},
			series: _args.seriesData,

			/*	   dataZoom:[ {
                show: true,
                start: 0,
                end: _args.xAxisData.length,
          	
            }]*/
		};
		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(option);
	
		myChart.on("click",_args.bindingClick);
		/*$("#"+_args.attrId).css("background","white");*/
	}
}


var Linechart = {

	getSimpleLine: function(args) {
		var defaultArgs = {
			pid: "",
			attrId: "", //整个图表id
			title: "这是标题", //标题
			legend: new Array(), //['蒸发量','降水量'] 指标数
			xAxisData: new Array(), //x轴横向指标  ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
			seriesData: new Array(), //内容对象 value 对应的值  name 对应的名称;
			//  name:'蒸发量',
			//  type:'bar',
			//data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
			bindingClick:function(){},
			colors:['#c23531','#2f4554', '#61a0a8', '#d48265', '#91c7ae','#749f83',  '#ca8622', '#bda29a','#6e7074', '#546570', '#c4ccd3'],//调色盘颜色列表。如果系列没有设置颜色，则会依次循环从该列表中取颜色作为系列颜色。
			tooltip: {
				trigger: 'item',
				triggerOn: "mousemove"

			},
			legendShow: false,

		};
		var _args = $.extend({}, defaultArgs, args);
		var chartLegend = {};
		if(_args.legendShow)
		{
			chartLegend={
				left: 'left',
				data: _args.legend
			};
		}
		
		var html = '<h4 >' + _args.title + '</h4>' +
			'<div id="' + _args.attrId + '" style="width: 100%;height:400px;"></div>';
		$(_args.pid).append(html);
		// 基于准备好的dom，初始化echarts实例
		var myChart = echarts.init(document.getElementById(_args.attrId));

		//取出legend
		for (var i = 0; i < _args.seriesData.length; i++) {
			_args.legend.push(_args.seriesData[i].name);
		}

		option = {

			tooltip: _args.tooltip,
			legend:chartLegend,
			xAxis: {
				type: 'category',
				/*  name: 'x',*/
				splitLine: {
					show: true
				},
				data: _args.xAxisData

			},
			color:_args.colors,
			yAxis: {
				type: 'log',
				/*    name: 'y'*/
			},
			series: _args.seriesData,

		};
		// 使用刚指定的配置项和数据显示图表。
		myChart.setOption(option);
		myChart.on("click",_args.bindingClick);
	}



	
}




