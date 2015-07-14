JetsenWeb.require( [ "js_gridlist", "js_window", "js_validate", "js_sql",
		"js_pagebar", "js_pageframe", "js_autocomplete", "js_jetsentree" ]);

var gFrame;
var gWindowSizeChangedInterVal;
var gReportTree;
var gParentId = 0;
var pInfo = new JetsenWeb.UI.PageBar("pageReport");
pInfo.onpagechange = function() {
	loadData();
}
pInfo.onupdate = function() {
	document.getElementById('divPage').innerHTML = this.generatePageControl();
};
pInfo.orderBy = "Order By VIEW_POS";
var conditionCollection = new JetsenWeb.SqlConditionCollection();

function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		showSplit : false
	});
	gFrame.splitTitle = "divListTitle";
	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false,
				splitBorder : 0
			});
	frameContent.addControl(new JetsenWeb.UI.PageItem("divContainer"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
		}
	}));
	gFrame.addControl(JetsenWeb.extend(
			new JetsenWeb.UI.PageItem("divListTitle"), {
				size : {
					width : 0,
					height : 27
				}
			}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	var parentId = JetsenWeb.queryString("PID");
	gParentId = parentId == "" ? 0 : parseInt(parentId);
	loadData();
	// loadNavigation(gParentId);
	loadParentReport();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}