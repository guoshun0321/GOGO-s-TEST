// 注册名称空间
// jetsennet.registerNamespace("jetsennet.ui.portal");
var jetsennet = {};
jetsennet.ui = {};

// 页面布局：
// {
// 	// 页面填充方式。fix，列固定宽度；percent，列百分比宽度；mix，混合。
// 	fill_type: "fix",
// 	// 页面大小，建议在fix时使用
// 	total_width: 800,
// 	layout: [[200, 300, 400],[500, 100]]
// }
// portlet信息
// {
// 	id : "id",
// 	layout : {
// 		row : "0",
// 		x : 0,
// 		y : 1
// 	},
// 	portletConfig : portletConfig,
// 	params : params
// }

jetsennet.ui.portal = function(containDiv, portalConfig) {

	// portal整体配置
	this.portalConfig = portalConfig;

	// 根div
	this.containDiv = containDiv;

	// row
	this.rowNum = this.portalConfig.layout.length;
	this.rows = new Array();

	// portlets
	this.portlets = new Array();

	// 初始化
	this.init();
}

// 初始化contain布局
jetsennet.ui.portal.prototype.init = function() {
	// 设置根div属性
	this.containDiv.css("float", "left");
	this.containDiv.css("width", this.portalConfig.total_width);

	// 设置行列
	for(var i = 0; i < this.rowNum; i++) {
		// 创建row
		var row = $("<div>");
		row.attr("id", "jetsennet_portal_row_" + i);
		row.css("float", "left");

		// 创建column
		var columnConfig = this.portalConfig.layout[i];
		for(var j = 0, length = columnConfig.length; j < length; j++) {
			var column = $("<div>");
			column.attr("class", "ui-portlet-column ui-sortable");
			column.css("width", columnConfig[j]);
			row.append(column);
		}

		// 将row添加到containDiv
		this.containDiv.append(row);
		this.rows.push(row);
		if(this.firstRow == null) {
			this.firstRow = row;
		}

		// portlet
		row.portlet({sortable : true});
	}
}

// 添加portlet
jetsennet.ui.portal.prototype.addPortlet = function(row, position, portletConfig, params) {
	if(!position) {
		position = 'last';
	}

	// 确定ID
	var id = "jetsennet_portal_" + this.portlets.length;
	portletConfig.attrs = portletConfig.attrs || {};
	portletConfig.attrs.id = id;

	// 添加portlet
	row.portlet('option', 'add', {
		position: position,
		portlet: portletConfig
	});

	// portlet相关信息
	var temp = {
		id : id,
		layout : "",
		portletConfig : portletConfig,
		params : params
	};
	this.portlets.push(temp);
}

// 刷新缓存中portlet的位置
jetsennet.ui.portal.prototype.refreshPosition = function() {
	var allPosition = {};
	for(row in this.rows) {
		var indexs = this.rows[row].portlet('index');	
		for(indexPos in indexs) {
			allPosition[indexPos] = {};
			allPosition[indexPos].id = indexPos;
			allPosition[indexPos].row = row;
			var index = indexs[indexPos];
			for(obj in index) {
				if(typeof index[obj] != "function") {
					allPosition[indexPos][obj] = index[obj];
				}
			}
		}
	}

	for(pos in this.portlets) {
		var portlet = this.portlets[pos];
		if(portlet) {
			portlet.layout = allPosition[portlet.id];
		}
	}
	console.log(this.portlets);
}

// 重新加载
jetsennet.ui.portal.prototype.reload = function(iPortlets) {
	for(pos in iPortlets) {
		var portlet = iPortlets[pos];
		this.addPortlet(portlet.layout.row, { x : portlet.layout.x, y : portlet.layout.y}, portlet.portletConfig, portlet.params);
	}
}