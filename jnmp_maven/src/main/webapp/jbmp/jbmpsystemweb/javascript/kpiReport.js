JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gSysConfigs = {};
var gFunctions = {
	delReportFile : false
}; // 权限：删除报表文件

// 初始化===================================================================================
function pageInit() {
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		splitTitle : "divListTitle",
		splitSize : 27
	});
	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReportFileList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
		}
	}));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), {
		size : {
			width : 0,
			height : 135
		}
	}));
	gFrame.addControl(frameContent);
	
	var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true });
    alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"),{ size: { width: 200, height: 0} }));
    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
	/*
	var templateContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjContent"), { splitType: 1, fixControlIndex: 0, showSplit: false });
	templateContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmList"), { size: { width: 200, height: 0} }));
	templateContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom")));
	var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true });
    alarmContent.addControl(templateContent);
    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
    */
    alarmContent.size = { width: 695, height: 280 };
    alarmContent.resize();  

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	
	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("detailReportEDate").value = endTime.toDateString();
	$("detailReportETime").value = endTime.toTimeString();
	$("detailReportSDate").value = startTime.toDateString();
	$("detailReportSTime").value = startTime.toTimeString();
	$("dailyReportSDate").value = endTime.toDateString();
	$("weekReportSDate").value = endTime.toDateString();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

//改变报表类型
function onShowTypeChange()
{
	var reportType = getSelectedValue($('reportType'));
	$('detailReportSDate').style.display = reportType == "1" ? "inline" : "none";
	$('detailReportSTime').style.display = reportType == "1" ? "inline" : "none";
	$('detailReportEDate').style.display = reportType == "1" ? "inline" : "none";
	$('detailReportETime').style.display = reportType == "1" ? "inline" : "none";
	$('detailReportSpan').style.display = reportType == "1" ? "inline" : "none";
	
	$('dailyReportSDate').style.display = reportType == "2" ? "inline" : "none";
	
	$('weekReportSDate').style.display = reportType == "3" ? "inline" : "none";
	$('weekReportSpan').style.display = reportType == "3" ? "inline" : "none";
	
	$('monthReportSpan').style.display = reportType == "4" ? "inline" : "none";
	
	$('yearReportSpan').style.display = reportType == "5" ? "inline" : "none";
	if(reportType == "3")
	{
		var date = new Date();
		getYearWeek(date.getFullYear(), date.getMonth() + 1, date.getDate());
	}
	if(reportType == "4")
	{
		var date = new Date();
		$('inputYear').value = date.getFullYear();
		var currentMonth = date.getMonth();
		setSelectedValue($('inputMonth'), currentMonth);
	}
	if(reportType == "5")
	{
		var date = new Date();
		$('inputYearReport').value = date.getFullYear();
	}
}

//选择指标
function onSelectAttrib()
{
	var dialog = new JetsenWeb.UI.Window("select-attribute-win");
	JetsenWeb.extend(dialog, {
		submitBox : false,
		cancelBox : false,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 870,
			height : 530
		},
		title : "选择指标"
	});
	dialog.controls = [ "divAttribSelect" ];
	dialog.onsubmit = function() {
//		operateSelectedAttrb();
//		deleteObjAttrIdsFromGrid = [];
//		JetsenWeb.UI.Windows.close("select-attribute-win");
	};
	$('divObjList').innerHTML = "";
	$('divAttrList').innerHTML = "";
	dialog.showDialog();
	var selectedAttrb = $('seletedObjAttb');
	if(selectedAttrb.options.length == 0)
	{
		$('divSeletedAttr').innerHTML = "";
		selectedAttrbArray = [];
	}
	else
	{
		$('divSeletedAttr').innerHTML = "";
//		selectedAttrbArray = [];
//		for (var i=0; i<statArray.length; i++)
//		{
//			selectedAttrbArray.push(statArray[i]);
//		}
		array2xml();
	}
	queryAllGroup();
}

function onOK()
{
	operateSelectedAttrb();
	deleteObjAttrIdsFromGrid = [];
	JetsenWeb.UI.Windows.close("select-attribute-win");
}

function onCancle()
{
	JetsenWeb.UI.Windows.close("select-attribute-win");
}

var statArray = [];
function operateSelectedAttrb()
{
//	for (var i=0; i<selectedAttrbArray.length; i++)
//	{
//		if(!objectIsInArray(selectedAttrbArray[i], statArray))
//		{
//			statArray.push(selectedAttrbArray[i]);
//		}
//	}
//	for (var i=0; i<statArray.length; i++)
//	{
//		for (var j=0; j<deleteObjAttrIdsFromGrid.length; j++)
//		{
//			if(statArray[i]["OBJATTR_ID"] == deleteObjAttrIdsFromGrid[j])
//			{
//				statArray.splice(i, 1);
//			}
//		}
//	}
	var reportAttrDom = $('seletedObjAttb');
	reportAttrDom.options.length = 0;
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		reportAttrDom.options.add(new Option(selectedAttrbArray[i]["OBJ_NAME"] + "-----" + selectedAttrbArray[i]["OBJATTR_NAME"], selectedAttrbArray[i]["OBJATTR_ID"]));
	}
}

function queryAllGroup()
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT * FROM BMP_GROUP2GROUP WHERE USE_TYPE=0)", "a", "a.GROUP_ID=aa.GROUP_ID",
			JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [ JetsenWeb.SqlCondition.create("aa.GROUP_TYPE", "4", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual,
			JetsenWeb.SqlParamType.Numeric) ];
	sqlQuery.Conditions = condition;
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.GROUP_ID", ResultFields : "aa.GROUP_ID,aa.GROUP_NAME,a.PARENT_ID,aa.GROUP_TYPE",
		PageInfo : null, QueryTable : queryTable });

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var gAlarmLevelGridList2 = new JetsenWeb.UI.GridList();
		$("divObjGroup").innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/objgroupInReport.xslt", ret.resultVal);
		gAlarmLevelGridList2.bind($("divObjGroup"), $("tabSelectAttr"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'aa.GROUP_ID', '1']);
}

var currentGroupId;
var currentGroupName;
function queryObject(objGroupId, objGroupName)
{
	currentGroupId = objGroupId;
	currentGroupName = objGroupName;
	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		renderObjectGrid(ret.resultVal);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("queryAllObjectByGroupId", [objGroupId]);
}

var gGridList = new JetsenWeb.UI.GridList("object-grid");
function renderObjectGrid(xml)
{
	$("divObjList").innerHTML = "";

	gGridList.columns = [{ index: 0, fieldName: "OBJ_NAME", width:273, align: "left", name: "资源"}];
	gGridList.parentId = "0";
	gGridList.idField = "OBJ_ID";
	gGridList.parentField = "PARENT_ID";
	gGridList.treeControlIndex = 0;
    gGridList.treeOpenLevel = 0;
	gGridList.dataSource = xml;
	gGridList.render("divObjList");
	gGridList.colorSelectedRows();
	gGridList.onrowclick = function(row,col){
		var rowId = row.id;
		var splitleng = rowId.split("-");
		queryAttribute(splitleng[splitleng.length-1]);
	}
	gGridList.ondoubleclick = function(row,col){
		var rowId = row.id;
		var splitleng = rowId.split("-");
		queryAllAttribByObjId(splitleng[splitleng.length-1]);
	}
}

var currentObjId;
function queryAttribute(objId)
{
	if(objId != null && objId != "")
	{
		currentObjId = objId;
		var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function (ret) {
	    	hideObjIdlw = objId;
	    	renderAttributeGrid(ret.resultVal);
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
		ws.call("bmpGetKpi", [objId, currentGroupId]);
	}
}

var gGridListAttrb = new JetsenWeb.UI.GridList("attrib-grid");
function renderAttributeGrid(xml)
{
	$("divAttrList").innerHTML = "";
	gGridListAttrb.columns = [{ index: 0, fieldName: "OBJATTR_NAME", width:310, name: "指标"}];
	gGridListAttrb.attachAttributes = [
	 	                      		{ name: "objattrid", field: "OBJATTR_ID"},
	 	                      		{ name: "attrid", field: "ATTRIB_ID"},
	 	                      		{ name: "objId", field: "OBJ_ID"}];
	gGridListAttrb.parentId = "0";
	gGridListAttrb.idField = "OBJATTR_ID";
	gGridListAttrb.parentField = "ATTRIB_ID";
	gGridListAttrb.treeControlIndex = 0;
	gGridListAttrb.treeOpenLevel = 0;
	gGridListAttrb.dataSource = xml;
	gGridListAttrb.render("divAttrList");
	gGridListAttrb.colorSelectedRows();
	gGridListAttrb.ondoubleclick = function(row,col){
		show(row.getAttribute("objattrid"),row.getAttribute("objid"),row.getAttribute("attrid"));
	}
}
var selectedAttrbArray = [];
var chartID = 1;
function show(objattrid, objid, attrid)
{
//	alert("objid = " + objid + " objattrid = " + objattrid + " attrid = " + attrid);
	if(attrid != 0 && objattrid != "")//双击的是指标
	{
		var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "A");
//		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "B", "A.OBJ_ID = B.OBJ_ID", JetsenWeb.TableJoinType.Left));
//		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "C", "B.GROUP_ID = C.GROUP_ID", JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "D", "D.OBJ_ID = A.OBJ_ID", JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE", "E", "E.ATTRIB_ID = D.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions = [ JetsenWeb.SqlCondition.create("D.OBJATTR_ID", objattrid, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric)];
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "D.OBJATTR_ID", ResultFields : "A.OBJ_ID,A.OBJ_NAME,E.ATTRIB_ID,E.ATTRIB_NAME,D.OBJATTR_ID,D.OBJATTR_NAME",
			PageInfo : null, QueryTable : queryTable });
		sqlQuery.Conditions = condition;
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var attrbTemp = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			for (var i=0; i<attrbTemp.length; i++)
			{
				attrbTemp[i]['GROUP_ID'] = currentGroupId;
				attrbTemp[i]['GROUP_NAME'] = currentGroupName;
			}
			addAttrb2Array(attrbTemp);
			array2xml();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	}
	else if(attrid == 0 && objattrid != "")
	{
		var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "A");
//		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "B", "A.OBJ_ID = B.OBJ_ID", JetsenWeb.TableJoinType.Left));
//		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "C", "B.GROUP_ID = C.GROUP_ID", JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "D", "D.OBJ_ID = A.OBJ_ID", JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE", "E", "E.ATTRIB_ID = D.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions = [ JetsenWeb.SqlCondition.create("E.ATTRIB_ID", objattrid, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric),
		                            JetsenWeb.SqlCondition.create("A.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric)];
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "D.OBJATTR_ID", ResultFields : "A.OBJ_ID,A.OBJ_NAME,E.ATTRIB_ID,E.ATTRIB_NAME,D.OBJATTR_ID,D.OBJATTR_NAME",
			PageInfo : null, QueryTable : queryTable });
		sqlQuery.Conditions = condition;
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var attrbTemp = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			for (var i=0; i<attrbTemp.length; i++)
			{
				attrbTemp[i]['GROUP_ID'] = currentGroupId;
				attrbTemp[i]['GROUP_NAME'] = currentGroupName;
			}
			addAttrb2Array(attrbTemp);
			array2xml();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	}
}

function queryAllAttribByObjId(objId)
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "A");
	//queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "B", "A.OBJ_ID = B.OBJ_ID", JetsenWeb.TableJoinType.Left));
	//queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "C", "B.GROUP_ID = C.GROUP_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "D", "D.OBJ_ID = A.OBJ_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBUTE", "E", "E.ATTRIB_ID = D.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [ JetsenWeb.SqlCondition.create("A.OBJ_ID", objId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric),
	                            JetsenWeb.SqlCondition.create("E.ATTRIB_TYPE", "103", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric)];
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "A.OBJATTR_ID", ResultFields : "A.OBJ_ID,A.OBJ_NAME,E.ATTRIB_ID,E.ATTRIB_NAME,D.OBJATTR_ID,D.OBJATTR_NAME",
		PageInfo : null, QueryTable : queryTable });
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var attrbTemp = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		for (var i=0; i<attrbTemp.length; i++)
		{
			attrbTemp[i]['GROUP_ID'] = currentGroupId;
			attrbTemp[i]['GROUP_NAME'] = currentGroupName;
		}
		addAttrb2Array(attrbTemp);
		array2xml();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

//不同图表显示
function showInTheDifferent()
{
	var selAttrbIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
	if(selAttrbIds.length == 0)
	{
		jetsennet.alert("请选择要操作的指标！");
	}
	else
	{
		for (var i=0; i<selAttrbIds.length; i++)
		{
			for (var j=0; j<selectedAttrbArray.length; j++)
			{
				if(selectedAttrbArray[j]["OBJATTR_ID"] == selAttrbIds[i])
				{
					selectedAttrbArray[j]["CHARTID"] = chartID;
					chartID ++;
				}
			}
		}
		array2xml();
	}
}

//同一图表显示(待完善)
function showInTheSame()
{
	var flag = true;
	var flag2 = true;
	var objIdArray = [];
	var attrbArr = [];
	var selAttrbIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
	if(selAttrbIds.length == 0)
	{
		jetsennet.alert("请选择要操作的指标！");
	}
	else
	{
		for (var i=0; i<selAttrbIds.length; i++)
		{
			for (var j=0; j<selectedAttrbArray.length; j++)
			{
				if(selectedAttrbArray[j]["OBJATTR_ID"] == selAttrbIds[i])
				{
					objIdArray.push(selectedAttrbArray[j]["OBJ_ID"]);
					attrbArr.push(selectedAttrbArray[j]["ATTRIB_ID"]);
				}
			}
		}
	}
	for(var i=0; i<objIdArray.length; i++)
	{
		var temp0 = objIdArray[0];
		if(temp0 != objIdArray[i])
		{
			flag = false;
			break;
		}
	}
	if(flag == true)
	{
		for (var j=0; j<attrbArr.length; j++)
		{
			var temp0 = attrbArr[0];
			if(temp0 != attrbArr[j])
			{
				flag2 = false;
				break;
			}
		}
		if(flag2 == true)
		{
			for (var i=0; i<selAttrbIds.length; i++)
			{
				for (var j=0; j<selectedAttrbArray.length; j++)
				{
					if(selectedAttrbArray[j]["OBJATTR_ID"] == selAttrbIds[i])
					{
						selectedAttrbArray[j]["CHARTID"] = chartID;
					}
				}
			}
			array2xml();
			chartID++;
		}
		else
		{
			jetsennet.alert("所选指标不属于同一个属性！");
		}
	}
	else
	{
		jetsennet.alert("所选指标不属于同一个对象！");
	}
}

var attrbArray = [];
function addAttrb2Array(attrbTempArray)
{
	if(attrbTempArray != null && attrbTempArray.length > 0)
	{
		if(attrbTempArray.length == 1)
		{
			if(isExist(attrbTempArray[0]))
			{
				jetsennet.alert("该指标已存在！");
			}
			else
			{
				if(judgeAttrIdIsExist(attrbTempArray[0]["ATTRIB_ID"]))
				{
					attrbTempArray[0]["CHARTID"] = getChartIdByAttrbId(attrbTempArray[0]["OBJ_ID"], attrbTempArray[0]["ATTRIB_ID"]);
				}
				else
				{
					attrbTempArray[0]["CHARTID"] = chartID;
					attrbArray.push({objId: attrbTempArray[0]["OBJ_ID"], attrbId: attrbTempArray[0]["ATTRIB_ID"], chartId: chartID});
					chartID++;
				}
				selectedAttrbArray.unshift(attrbTempArray[0]);
			}
		}
		else
		{
			for (var i=0; i<attrbTempArray.length; i++)
			{
				if(!isExist(attrbTempArray[i]))
				{
					if(judgeAttrIdIsExist(attrbTempArray[i]["ATTRIB_ID"]))
					{
						attrbTempArray[i]["CHARTID"] = getChartIdByAttrbId(attrbTempArray[i]["OBJ_ID"], attrbTempArray[i]["ATTRIB_ID"]);
					}
					else
					{
						attrbTempArray[i]["CHARTID"] = chartID;
						attrbArray.push({objId:attrbTempArray[i]["OBJ_ID"], attrbId: attrbTempArray[i]["ATTRIB_ID"], chartId: chartID});
						chartID++;
					}
					selectedAttrbArray.unshift(attrbTempArray[i]);
				}
			}
		}
	}
}

function getChartIdByAttrbId(objId, attrId)
{
	var flag = false;
	var tempChartId;
	for (var i=0; i<attrbArray.length; i++)
	{
		if(attrbArray[i]["objId"] == objId && attrbArray[i]["attrbId"] == attrId)
		{
			flag = true;
			tempChartId = attrbArray[i]["chartId"];
			break;
		}
	}
	if(flag == true)
	{
		return tempChartId;
	}
	else
	{
		return chartID;
	}
}

function judgeAttrIdIsExist(attrId)
{
	var flag = false;
	for (var i=0; i<attrbArray.length; i++)
	{
		if(attrbArray[i]["attrbId"] == attrId)
		{
			flag = true;
			tempChartId = attrbArray[i]["chartId"];
			break;
		}
	}
	return flag;
}

function isExist(attrbObject)
{
	var flag = false;
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		var temp = selectedAttrbArray[i];
		if(temp["OBJATTR_ID"] == attrbObject["OBJATTR_ID"])
		{
			flag = true;
			break;
		}
	}
	return flag;
}

function array2xml()
{
	var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	xml += "<RecordSet>";
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		xml += "<Record>";
		xml += "<GROUP_ID>";
		xml += selectedAttrbArray[i]["GROUP_ID"];
		xml += "</GROUP_ID>";
		xml += "<GROUP_NAME>";
		xml += selectedAttrbArray[i]["GROUP_NAME"];
		xml += "</GROUP_NAME>";
		xml += "<OBJ_ID>";
		xml += selectedAttrbArray[i]["OBJ_ID"];
		xml += "</OBJ_ID>";
		xml += "<OBJ_NAME>";
		xml += selectedAttrbArray[i]["OBJ_NAME"];
		xml += "</OBJ_NAME>";
		xml += "<ATTRIB_ID>";
		xml += selectedAttrbArray[i]["ATTRIB_ID"];
		xml += "</ATTRIB_ID>";
		xml += "<ATTRIB_NAME>";
		xml += selectedAttrbArray[i]["ATTRIB_NAME"];
		xml += "</ATTRIB_NAME>";
		xml += "<OBJATTR_ID>";
		xml += selectedAttrbArray[i]["OBJATTR_ID"];
		xml += "</OBJATTR_ID>";
		xml += "<OBJATTR_NAME>";
		xml += selectedAttrbArray[i]["OBJATTR_NAME"];
		xml += "</OBJATTR_NAME>";
		xml += "<CHARTID>";
		xml += selectedAttrbArray[i]["CHARTID"];
		xml += "</CHARTID>";
		xml += "</Record>";
	}
	xml += "</RecordSet>";
	var selectedAttrGird = new JetsenWeb.UI.GridList();
	$("divSeletedAttr").innerHTML = JetsenWeb.Xml.transformXML(
			"xslt/selectedAttrib.xslt", xml);
	selectedAttrGird.bind($("divSeletedAttr"), $("attrbTableList"));
}

var deleteObjAttrIdsFromGrid = [];
function delAttrb(objAttrId)
{
	deleteObjAttrIdsFromGrid.push(objAttrId);
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		if(selectedAttrbArray[i]["OBJATTR_ID"] == objAttrId)
		{
			selectedAttrbArray.splice(i, 1);
		}
	}
	array2xml();
}

function saveTemplate()
{
	if(selectedAttrbArray.length == 0)
	{
		jetsennet.alert("请选择指标！");
	}
	else
	{
		var dialog = new JetsenWeb.UI.Window("save-template-win");
		JetsenWeb.extend(dialog, {
			submitBox : true,
			cancelBox : true,
			windowStyle : 1,
			maximizeBox : false,
			minimizeBox : false,
			size : {
				width : 380,
				height : 110
			},
			title : "模板另存为"
		});
		dialog.controls = [ "saveTemplateDiv" ];
		dialog.onsubmit = function() {
			var templateName = $('templateName').value;
			checkTemplateNameIsExsit(templateName);
			JetsenWeb.UI.Windows.close("save-template-win");
		};
		$('templateName').value = "";
		showRemindWordCount(this.value,$('remindWord'),20);
		dialog.showDialog();
	}
}
//判断模板名是不是已经存在
function checkTemplateNameIsExsit(templateName)
{
	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var number = parseInt(ret.resultVal);
		if(number > 0)
		{
			jetsennet.alert("模板名称已经存在！");
		}
		else
		{
			opreateSaveTemplate();
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("checkTemplateNameIsExsit", [templateName]);
}

function getTemplateXml()
{
	var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	xml += "<RecordSet>";
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		xml += "<Record>";
		xml += "<GROUP_ID>";
		xml += selectedAttrbArray[i]["GROUP_ID"];
		xml += "</GROUP_ID>";
		xml += "<GROUP_NAME>";
		xml += selectedAttrbArray[i]["GROUP_NAME"];
		xml += "</GROUP_NAME>";
		xml += "<OBJ_ID>";
		xml += selectedAttrbArray[i]["OBJ_ID"];
		xml += "</OBJ_ID>";
		xml += "<OBJ_NAME>";
		xml += selectedAttrbArray[i]["OBJ_NAME"];
		xml += "</OBJ_NAME>";
		xml += "<ATTRIB_ID>";
		xml += selectedAttrbArray[i]["ATTRIB_ID"];
		xml += "</ATTRIB_ID>";
		xml += "<ATTRIB_NAME>";
		xml += selectedAttrbArray[i]["ATTRIB_NAME"];
		xml += "</ATTRIB_NAME>";
		xml += "<OBJATTR_ID>";
		xml += selectedAttrbArray[i]["OBJATTR_ID"];
		xml += "</OBJATTR_ID>";
		xml += "<OBJATTR_NAME>";
		xml += selectedAttrbArray[i]["OBJATTR_NAME"];
		xml += "</OBJATTR_NAME>";
		xml += "<CHARTID>";
		xml += selectedAttrbArray[i]["CHARTID"];
		xml += "</CHARTID>";
		xml += "</Record>";
	}
	xml += "</RecordSet>";
	return xml;
}

function opreateSaveTemplate()
{
	var templateName = $('templateName').value;
	if(templateName == "")
	{
		jetsennet.alert("模板名称不能为空！");
		return;
	}
	var flag = false;
	for (var i=0; i<templateName.length; i++)
	{
		if(templateName.charAt(i) != " ")
		{
			flag = true;
		}
	}
	if(!flag)
	{
		jetsennet.alert("模板名称不能全部为空格！");
		return;
	}
	if(templateName.length > 20)
	{
		jetsennet.alert("模板名称超过字数！");
		return;
	}
	var templateXml = getTemplateXml();
	var intertObject = {TEMPLATE_NAME: templateName, TEMPLATE_XML: templateXml};
	
	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	if (ret.errorString != null && ret.errorString.trim() != "") 
    	{
            jetsennet.alert(ret.errorString + "！");
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("bmpObjInsert", ["BMP_KPITEMPLATE", JetsenWeb.Xml.serializer(intertObject, "BMP_KPITEMPLATE")]);
}

function importTemplate()
{
	var dialog = new JetsenWeb.UI.Window("edit-divObjAttrib2Alarm-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 700,
			height : 350
		},
		title : "选择模板"
	});
	dialog.controls = [ "showTemplateDiv" ];
	dialog.onsubmit = function() {
		addTemplateToArray();
		JetsenWeb.UI.Windows.close("edit-divObjAttrib2Alarm-win");
	};
	$("divAlarmLevel").innerHTML = "";
	dialog.showDialog();
	queryAllTemplate();
}

var templateInformation = [];
function queryAllTemplate()
{
	$("divAlarmLevel").innerHTML = "";
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_KPITEMPLATE", "A");
	var condition = new JetsenWeb.SqlConditionCollection();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "A.TEMPLATE_ID", ResultFields : "A.*",
		PageInfo : null, QueryTable : queryTable });
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var gridList = new JetsenWeb.UI.GridList();
		$("divAlarmList").innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/kpiTemplate.xslt", ret.resultVal);
		gridList.bind($("divAlarmList"), $("tabAlarmList"));
		templateInformation = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

var selectedTemplate;//当前选中的模板
function selectTemplate(templateId)
{
	for (var i=0; i<templateInformation.length; i++)
	{
		if(templateInformation[i]["TEMPLATE_ID"] == templateId)
		{
			var gridList = new JetsenWeb.UI.GridList();
			$("divAlarmLevel").innerHTML = JetsenWeb.Xml.transformXML(
					"xslt/kpiFromTemplate.xslt", templateInformation[i]["TEMPLATE_XML"]);
			gridList.bind($("divAlarmLevel"), $("kaiListFromTemp"));
			selectedTemplate = templateInformation[i];
			break;
		}
	}
}

function addTemplateToArray()
{
	if(selectedTemplate != null)
	{
		var kpiObjAttrIdArray = [];
		var kpiTemplateArray = JetsenWeb.Xml.toObject(selectedTemplate["TEMPLATE_XML"], "Record");
		for (var i=0; i<kpiTemplateArray.length; i++)
		{
			kpiObjAttrIdArray.push(kpiTemplateArray[i]["OBJATTR_ID"]);
		}
		
		var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "A");
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "B", "B.OBJ_ID = A.OBJ_ID", JetsenWeb.TableJoinType.Left));
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions = [ JetsenWeb.SqlCondition.create("A.OBJATTR_ID", kpiObjAttrIdArray.join(","), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric) ];
		JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "A.OBJATTR_ID", ResultFields : "A.OBJATTR_ID,A.OBJATTR_NAME,B.OBJ_ID,B.OBJ_NAME",
			PageInfo : null, QueryTable : queryTable });
		sqlQuery.Conditions = condition;
		var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			statArray = [];
			var attrbTemp = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			findDeleteKpi(kpiTemplateArray, attrbTemp);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), "B.OBJ_ID", "2"]);
	}
	
}

function findDeleteKpi(kpiFromTemplate, kpiFromDatabase)
{
	if(kpiFromTemplate != null && kpiFromDatabase != null)
	{
		selectedAttrbArray = [];
		var deleteKpiArray = [];
		var chartIdArray = [];
		for (var i=0; i<kpiFromTemplate.length; i++)
		{
			if(!objectIsInArray(kpiFromTemplate[i], kpiFromDatabase))
			{
				deleteKpiArray.push(kpiFromTemplate[i]);
			}
		}
		if(deleteKpiArray.length > 0)
		{
			jetsennet.alert("模板中的有些指标已经不存在，或没有权限！");
		}
		for (var i=0; i<kpiFromTemplate.length; i++)
		{
			if(!objectIsInArray(kpiFromTemplate[i], deleteKpiArray))
			{
				selectedAttrbArray.push(kpiFromTemplate[i]);
			}
			chartIdArray.push(kpiFromTemplate[i]["CHARTID"]);
		}
		chartIdArray.sort();
		chartID = parseInt(chartIdArray[chartIdArray.length - 1]) + 1;
		array2xml();
	}
}

function objectIsInArray(object, array)
{
	var flag = false;
	for (var i=0; i<array.length; i++)
	{
		if(array[i]["OBJATTR_ID"] == object["OBJATTR_ID"])
		{
			flag = true;
			break;
		}
	}
	return flag;
}

function getYearWeek(a, b, c)
{
	var date1 = new Date(a, parseInt(b) - 1, c), date2 = new Date(a, 0, 1), 
	d = Math.round((date1.valueOf() - date2.valueOf()) / 86400000); 
	$('year').innerHTML = new Date().getFullYear();
	$('week').innerHTML = Math.ceil((d + ((date2.getDay() + 1) - 1)) / 7);
	/*
	var weekStartDate = new Date();
	weekStartDate.setDate(new Date().getDate() - new Date().getDay() + 1);
	var weekEndDate = new Date();
	weekEndDate.setDate(new Date().getDate() + (7 - new Date().getDay()));
	$('weekStartDate').innerHTML = weekStartDate.toDateString();
	$('weekEndDate').innerHTML = weekEndDate.toDateString();
	*/
	var weekStartDate = new Date();
	weekStartDate.setFullYear(a, b - 1, c);
	weekStartDate.setDate(weekStartDate.getDate() - weekStartDate.getDay() + 1);
	var weekEndDate = new Date();
	weekEndDate.setFullYear(a, b - 1, c);
	weekEndDate.setDate(weekEndDate.getDate() + (7 - weekEndDate.getDay()));
	$('weekStartDate').innerHTML = weekStartDate.toDateString();
	$('weekEndDate').innerHTML = weekEndDate.toDateString();
}

function searchReport()
{
	var reportProPath;
	var __report = "reports/kpichart.rptdesign";
	var __format = "HTML";
	var reportType = getSelectedValue($('reportType'));
	var showType;
	var startTime;
	var endTime;
	var params;
	var rdo1Dom = $('rdo1');
	var rdo2Dom = $('rdo2');
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	reportProPath = ret.value + "/frameset";
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("getReportProPath");
	if(rdo1Dom.checked == true)
	{
		showType = rdo1Dom.value;
	}
	else if(rdo2Dom.checked == true)
	{
		showType = rdo2Dom.value;
	}
	if(reportType == "1")
	{
		startTime = parseDate($("detailReportSDate").value + " " + $("detailReportSTime").value).getTime();
		endTime = parseDate($("detailReportEDate").value + " " + $("detailReportETime").value).getTime();
	}
	else if(reportType == "2")
	{
		startTime = parseDate($("dailyReportSDate").value + " " + "00:00:00").getTime();
		endTime = parseDate($("dailyReportSDate").value + " " + "23:59:59").getTime();
	}
	else if(reportType == "3")
	{
		var weekStartDate = new Date();
		weekStartDate.setDate(new Date().getDate() - new Date().getDay() + 1);
		weekStartDate.setHours(0, 0, 0);
		var weekEndDate = new Date();
		weekEndDate.setDate(new Date().getDate() + (7 - new Date().getDay()));
		weekEndDate.setHours(23, 59, 59);
		startTime = weekStartDate.getTime();
		endTime = weekEndDate.getTime();
	}
	else if(reportType == "4")
	{
		var inputYear = $('inputYear').value;
		var inputMonth = getSelectedValue($('inputMonth'));
		var startdate = new Date();
		startdate.setFullYear(inputYear, inputMonth, 1);
		startdate.setHours(0, 0, 0);
		startTime = startdate.getTime();
		var enddate = new Date();
		enddate.setFullYear(inputYear, inputMonth, getDays(inputMonth,inputYear));
		enddate.setHours(23, 59, 59);
		endTime = enddate.getTime();
	}
	else if(reportType == "5")
	{
		var inputYear = $('inputYearReport').value;
		var startdate = new Date();
		startdate.setFullYear(inputYear, 0, 1);
		startdate.setHours(0, 0, 0);
		startTime = startdate.getTime();
		var enddate = new Date();
		enddate.setFullYear(inputYear, 11, getDays(11, inputYear));
		enddate.setHours(23, 59, 59);
		endTime = enddate.getTime();
	}
	params = getRequestParam();
	if(showType == 1)
	{
		document.getElementById("report1Info").__report = 'reports/kpichart.rptdesign';
	}
	else if(showType == 2)
	{
		document.getElementById("report1Info").__report = 'reports/kpitable.rptdesign';
	}
	document.getElementById("report1Info").reportSrc = reportProPath;
	document.getElementById("report1Info").__format = 'HTML';
	document.getElementById("report1Info").reportType = reportType;
	document.getElementById("report1Info").startTime = startTime;
	document.getElementById("report1Info").endTime = endTime;
	document.getElementById("report1Info").params = params;
	document.getElementById("report1Info").src = "";
	if(showType == 1)
	{
		document.getElementById("report1Info").src = "kpiTable.html";
	}
	else if(showType == 2)
	{
		document.getElementById("report1Info").src = "kpiChart.html";
	}
}

//将“yyyy-MM-dd HH:mm:ss”或者“yyyy-MM-dd”字符串转换成Date
function parseDate(dateString)
{
	var dateReg = /^([1-9]\d{3})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\d|3[01])( ((0?|1)\d|2[0-3]):((0?|[1-5])\d):((0?|[1-5])\d))?$/;
	if (!dateReg.test(dateString))
	{
		return null;
	}
	var childgroups = dateString.match(dateReg);
	var execFuncs = ["", "setFullYear", "setMonth", "setDate", "", "setHours", "", "setMinutes", "", "setSeconds", ""];
	var date = new Date(0);
	for (var i = 0; i < childgroups.length; i++)
	{
		if (execFuncs[i] != "" && childgroups[i] != "")
		{
			var num = Number(childgroups[i]);
			if (execFuncs[i] == "setMonth")
			{
				num -= 1;
			}
			date[execFuncs[i]](num);
		}
	}
	return date;
}

function leapYear(year) 
{
	if (year % 4 == 0)
	{
		return true;
	}
	return false; 
}

function getDays(month, year) 
{
	var ar = new Array(12);
	ar[0] = 31; // January
	ar[1] = (leapYear(year)) ? 29 : 28; // February
	ar[2] = 31; // March
	ar[3] = 30; // April
	ar[4] = 31; // May
	ar[5] = 30; // June
	ar[6] = 31; // July
	ar[7] = 31; // August
	ar[8] = 30; // September
	ar[9] = 31; // October
	ar[10] = 30; // November
	ar[11] = 31; // December
	return ar[month];
}

function getRequestParam()
{
	var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	xml += "<params>";
	for (var i=0; i<selectedAttrbArray.length; i++)
	{
		xml += "<param>";
		xml += "<objGroupId>";
		xml += selectedAttrbArray[i]["GROUP_ID"];
		xml += "</objGroupId>";
		xml += "<objGroupName>";
		xml += selectedAttrbArray[i]["GROUP_NAME"];
		xml += "</objGroupName>";
		xml += "<objId>";
		xml += selectedAttrbArray[i]["OBJ_ID"];
		xml += "</objId>";
		xml += "<objName>";
		xml += selectedAttrbArray[i]["OBJ_NAME"];
		xml += "</objName>";
		xml += "<attrId>";
		xml += selectedAttrbArray[i]["ATTRIB_ID"];
		xml += "</attrId>";
		xml += "<attrName>";
		xml += selectedAttrbArray[i]["ATTRIB_NAME"];
		xml += "</attrName>";
		xml += "<objAttrId>";
		xml += selectedAttrbArray[i]["OBJATTR_ID"];
		xml += "</objAttrId>";
		xml += "<objAttrName>";
		xml += selectedAttrbArray[i]["OBJATTR_NAME"];
		xml += "</objAttrName>";
		xml += "<flag>";
		xml += selectedAttrbArray[i]["CHARTID"];
		xml += "</flag>";
		xml += "</param>";
	}
	xml += "</params>";
	return xml;
}

//模板删除
function delTemplate(templateId)
{
	if(templateId != null && templateId != "")
	{
		var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			queryAllTemplate();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDeleteMany", ["BMP_KPITEMPLATE", templateId]);
	}
}

function showChange()
{
	var selectedDateText = $('weekReportSDate').value;
	var selectedDate = parseDate($("weekReportSDate").value);
	getYearWeek(selectedDate.getFullYear(), selectedDate.getMonth() + 1, selectedDate.getDate());
}

function batchDelete()
{
	var selAttrbIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
	if(selAttrbIds.length == 0)
	{
		jetsennet.alert("请选择要删除的指标！");//selectedAttrbArray
	}
	else
	{
		for (var i=0; i<selAttrbIds.length; i++)
		{
			var ttAttrbId = selAttrbIds[i];
			deleteObjAttrIdsFromGrid.push(ttAttrbId);
			for (var j=0; j<selectedAttrbArray.length; j++)
			{
				if(selectedAttrbArray[j]['OBJATTR_ID'] == ttAttrbId)
				{
					selectedAttrbArray.splice(j, 1);
				}
			}
		}
	}
	array2xml();
}

















