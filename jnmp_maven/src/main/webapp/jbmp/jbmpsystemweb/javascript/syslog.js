JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;

var gSyslogPage = new JetsenWeb.UI.PageBar("Syslog");
gSyslogPage.onpagechange = function() {
	loadSyslog();
};
gSyslogPage.orderBy = "Order By COLL_TIME Desc";
gSyslogPage.pageSize = 20;
gSyslogPage.onupdate = function() {
	$('divSyslogPage').innerHTML = this.generatePageControl();
};
var gSyslogCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gSyslogPage.setOrderBy(sortfield, desc);
};
var gSyslogSqlQuery = new JetsenWeb.SqlQuery();
var gSyslogQueryTable = JetsenWeb.createQueryTable("BMP_SYSLOG", "te");
JetsenWeb.extend(gSyslogSqlQuery, {
	IsPageResult : 1,
	KeyId : "",
	PageInfo : gSyslogPage,
	QueryTable : gSyslogQueryTable,
	ResultFields : "te.*"
});

// 加载=====================================================================================
function loadSyslog() {
	gSyslogSqlQuery.OrderString = gSyslogPage.orderBy;
	gSyslogSqlQuery.Conditions = gSyslogCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divSyslogList').innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/syslog.xslt", ret.resultVal);
		gGridList.bind($('divSyslogList'), $('tabSyslog'));
		gSyslogPage.setRowCount($('hid_SyslogCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSyslogSqlQuery.toXml() ]);
}

function searchSyslog() {
	gSyslogCondition.SqlConditions = [];

	if ($("txt_COLL_IP").value != "") {
		gSyslogCondition.SqlConditions
				.push(JetsenWeb.SqlCondition.create("IP_ADDR",
						$("txt_COLL_IP").value, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.ILike,
						JetsenWeb.SqlParamType.String));
	}
	if ($("txt_CONTENT").value != "") {
		gSyslogCondition.SqlConditions
				.push(JetsenWeb.SqlCondition.create("CONTENT",
						$("txt_CONTENT").value, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.ILike,
						JetsenWeb.SqlParamType.String));
	}
	if ($("chkDate").checked && $("txtSDate").value != ""
			&& $("txtSTime").value != "") {
		gSyslogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"COLL_TIME", $("txtSDate").value + " " + $("txtSTime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ThanEqual,
				JetsenWeb.SqlParamType.DateTime));
	}
	if ($("chkDate").checked && $("txtEDate").value != ""
			&& $("txtETime").value != "") {
		gSyslogCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"COLL_TIME", $("txtEDate").value + " " + $("txtETime").value,
				JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.LessEqual,
				JetsenWeb.SqlParamType.DateTime));
	}

	gSyslogPage.currentPage = 1;
	loadSyslog();
}

// 初始化===================================================================================
function pageInit() {
	var date = new Date();
	$("txtEDate").value = date.toDateString();
	$("txtETime").value = date.toTimeString();
	date.setHours(date.getHours() - 2);
	$("txtSDate").value = date.toDateString();
	$("txtSTime").value = date.toTimeString();

	searchSyslog();
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = new JetsenWeb.UI.PageFrame("divPageFrame");
	gFrame.splitType = 1;
	gFrame.splitTitle = "divListTitle";
	gFrame.splitSize = 27;
	var _frameTop = new JetsenWeb.UI.PageItem("divTop");
	_frameTop.size = {
		width : 0,
		height : 30
	};
	var _frameContent = new JetsenWeb.UI.PageFrame("divContent");
	_frameContent.splitType = 1;
	_frameContent.fixControlIndex = 1;
	_frameContent.showSplit = false;
	_frameContent.addControl(new JetsenWeb.UI.PageItem("divSyslogList"));
	_frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
		}
	}));
	gFrame.addControl(_frameTop);
	gFrame.addControl(_frameContent);
	gFrame.fixControlIndex = 0;
	gFrame.enableResize = false;

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}