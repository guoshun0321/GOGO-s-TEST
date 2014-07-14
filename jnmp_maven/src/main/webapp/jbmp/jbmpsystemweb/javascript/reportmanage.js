JetsenWeb.require( [ "js_pagebar", "js_datepicker", "js_sql", "js_pageframe",
		"js_xtree" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var AC_IMG_PATH = "./images/acIcon/";
// 报表分类树
var gTree;
var reportArray;

// 初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = new JetsenWeb.UI.PageFrame("divPageFrame");
	gFrame.splitType = 0;
	gFrame.fixControlIndex = 0;
	gFrame.enableResize = true;

	var divLeftFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaLeft"),
			{
				splitType : 1,
				fixControlIndex : 0,
				splitBorder : 0,
				size : {
					width : 210,
					height : 0
				},
				showSplit : false
			});
	divLeftFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divLeftTitle"), {
		size : {
			width : 0,
			height : 27
		}
	}));
	divLeftFrame.addControl(new JetsenWeb.UI.PageItem("divReportTreeContent"));
	var frameReportTreeContent = new JetsenWeb.UI.PageItem(
			"divReportTreeContent");
	var frameReportInfoContent = new JetsenWeb.UI.PageItem(
			"divReportInfoContent");

	gFrame.addControl(divLeftFrame);
	gFrame.addControl(frameReportInfoContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	refreshReportTree();
	$("frmTableInfo").src = "../../jbmp/jbmpsystemweb/alarmstatistics.htm?ID=5";
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

// 刷新报表分类树
function refreshReportTree() {
	reportArray = null;
	genReportTree();
	$("divReportTreeContent").innerHTML = gTree;
	gTree.expandAll();
}
// 生成报表类型树
function genReportTree() {
	if (!reportArray) {
		getAllReport();
	}
	gTree = new JetsenWeb.UI.Tree("所有报表", "", null, AC_IMG_PATH
			+ "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
	gTree.showTop = true;
	gTree.setBehavior("classic");

	if (reportArray) {
		var reportNode = new Array();
		for (i = 0; i < reportArray.length; i++) {
			var reportCls = reportArray[i];
			var node = null;
			if (reportCls["PARENT_ID"] == 0 || reportCls["PARENT_ID"] == "") {
				node = new JetsenWeb.UI.TreeItem(reportCls["NAME"],
						"javascript:showObjList('" + reportCls["PARAM"]
								+ "', '" + reportCls["ID"] + "', '"
								+ reportCls["PARENT_ID"] + "')", null,
						AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH
								+ "defaulticon.gif");
				gTree.add(node);
			} else {
				node = new JetsenWeb.UI.TreeItem(reportCls["NAME"],
						"javascript:showObjList('" + reportCls["PARAM"]
								+ "', '" + reportCls["ID"] + "', '"
								+ reportCls["PARENT_ID"] + "')", null,
						AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH
								+ "pcde_002.gif");
				for (j = 0; j < reportNode.length; j++) {
					if (reportArray[j]["ID"] == reportCls["PARENT_ID"]) {
						reportNode[j].add(node);
						break;
					}
				}
			}
			reportNode.push(node);
		}
	}
}
// 获取全部报表分类
function getAllReport() {
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE", 1,
			JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", 0,
			JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("STATE", 0,
			JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "ID",
		PageInfo : null,
		ResultFields : "ID,NAME,PARENT_ID,PARAM",
		OrderString : "Order By PARENT_ID,VIEW_POS",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), {
			TableName : "BMP_REPORT"
		})
	});

	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		reportArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

// 树节点点击
function showObjList(reportType, reportId, parentId) {
	var flag = true;
	if (reportArray) {
		for (i = 0; i < reportArray.length; i++) {
			var reportCls = reportArray[i];
			if (reportCls["PARENT_ID"] == reportId) {
				flag = false;
				break;
			}
		}
	}

	if (reportType != "" && parentId != 0 && flag) {
		$("frmTableInfo").src = reportType + "?ID=" + reportId;
	}
}