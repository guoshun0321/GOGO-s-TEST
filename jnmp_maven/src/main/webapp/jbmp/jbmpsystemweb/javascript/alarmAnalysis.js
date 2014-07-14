JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
//报警类型控件
pageAlarmType = new JetsenWeb.UI.PageBar("pageAlarmType");
pageAlarmType.orderBy = "";
pageAlarmType.changePageSize(100);
pageAlarmType.onpagechange = function () { getSelectAlarmTypeData(); }
pageAlarmType.onupdate = function () {
    $('divSelectAlarmTypePage').innerHTML = this.generatePageControl();
}

var alarmTypeGridList = new JetsenWeb.UI.GridList();
alarmTypeGridList.ondatasort = function (sortfield, desc) {
	pageAlarmType.setOrderBy(sortfield, desc);
}
//报警规则控件
pageAlarmRule = new JetsenWeb.UI.PageBar("pageAlarmRule");
pageAlarmRule.orderBy = "";
pageAlarmRule.changePageSize(100);
pageAlarmRule.onpagechange = function () { getSelectAlarmRuleData(); }
pageAlarmRule.onupdate = function () {
    $('divSelectAlarmRulePage').innerHTML = this.generatePageControl();
}

var alarmRuleGridList = new JetsenWeb.UI.GridList();
alarmRuleGridList.ondatasort = function (sortfield, desc) {
	pageAlarmRule.setOrderBy(sortfield, desc);
}
//报警等级控件
pageAlarmLevel = new JetsenWeb.UI.PageBar("pageAlarmLevel");
pageAlarmLevel.orderBy = "";
pageAlarmLevel.changePageSize(100);
pageAlarmLevel.onpagechange = function () { getSelectAlarmLevelData(); }
pageAlarmLevel.onupdate = function () {
    $('divSelectAlarmLevelPage').innerHTML = this.generatePageControl();
}

var alarmLevelGridList = new JetsenWeb.UI.GridList();
alarmLevelGridList.ondatasort = function (sortfield, desc) {
	pageAlarmLevel.setOrderBy(sortfield, desc);
}
//对象控件
pageObj = new JetsenWeb.UI.PageBar("pageObj");
pageObj.orderBy = "ORDER BY OBJ_ID";
pageObj.changePageSize(100);
pageObj.onpagechange = function () { getSelectObjData(); }
pageObj.onupdate = function () {
    $('divSelectObjPage').innerHTML = this.generatePageControl();
}

var objGridList = new JetsenWeb.UI.GridList();
objGridList.ondatasort = function (sortfield, desc) {
	pageObj.setOrderBy(sortfield, desc);
}
//对象属性控件
pageAttr = new JetsenWeb.UI.PageBar("pageAttr");
pageAttr.orderBy = "ORDER BY OBJ_ID";
pageAttr.changePageSize(100);
pageAttr.onpagechange = function () { getSelectAttrData(); }
pageAttr.onupdate = function () {
    $('divSelectAttrPage').innerHTML = this.generatePageControl();
}

var attrGridList = new JetsenWeb.UI.GridList();
attrGridList.ondatasort = function (sortfield, desc) {
    pageAttr.setOrderBy(sortfield, desc);
}

//查询报表
function searchReport() {
	var reportProPath;
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	reportProPath = ret.value;
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("getReportProPath");
    
    document.getElementById("reportInfo").reportSrc = reportProPath + "/alarmAnalysis.jsp?flag=1";
	
	var selectAlarmType = $("showAlarmTypeOption");
//	var selectAlarmRule = $("showAlarmRuleOption");
	var selectAlarmLevel = $("showAlarmLevelOption");
	var selectObj = $("showObjOption");
	var selectAttr = $("showAttrOption");
    var lenAlarmType = selectAlarmType.options.length;
//    var lenAlarmRule = selectAlarmRule.options.length;
    var lenAlarmLevel = selectAlarmLevel.options.length;
    var lenObj = selectObj.options.length;
    var lenAttr = selectAttr.options.length;
    var urlStr = "";
    
    if(lenObj <= 0){
    	JetsenWeb.alert("请选择对象！");
    	return;
    }
    if(lenAttr <= 0){
    	JetsenWeb.alert("请选择对象属性！");
    	return;
    }
    if(!(lenAlarmType > 0 || lenAlarmLevel > 0)){
    	JetsenWeb.alert("至少设置报警类型、报警规则、报警等级中任意一项！");
    	return;
    }
    if(parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime() > parseDate($("txtEDate").value + " " + $("txtETime").value).getTime()){
		jetsennet.alert("结束时间必须大于起始时间！");
		return;
	}
    
    document.getElementById("reportInfo").alarmType = '';
    if(lenAlarmType > 0){
    	var alarmType = '';
    	for (var i = 0; i < lenAlarmType; i++) {
    		alarmType += selectAlarmType.options[i].value + ',';
    	}
    	document.getElementById("reportInfo").alarmType = alarmType.substring(0, alarmType.length-1);
    }
    
    document.getElementById("reportInfo").alarmId = '';
//    if(lenAlarmRule > 0){
//    	var alarmId = ''
//    	for (var i = 0; i < lenAlarmRule; i++) {
//    		alarmId += selectAlarmRule.options[i].value + ',';
//    	}
//    	document.getElementById("reportInfo").alarmId = alarmId.substring(0, alarmId.length-1);
//    }
    
    document.getElementById("reportInfo").alarmLevel = '';
    if(lenAlarmLevel > 0){
    	var alarmLevel = '';
    	for (var i = 0; i < lenAlarmLevel; i++) {
    		alarmLevel += selectAlarmLevel.options[i].value + ',';
    	}
    	document.getElementById("reportInfo").alarmLevel = alarmLevel.substring(0, alarmLevel.length-1);
    }
    
    document.getElementById("reportInfo").objIds = '';
    if(lenObj > 0){
    	var objIds = '';
    	for (var i = 0; i < lenObj; i++) {
    		objIds += selectObj.options[i].value + ',';
    	}
		document.getElementById("reportInfo").objIds = objIds.substring(0, objIds.length-1);
    }
    
    document.getElementById("reportInfo").objAttrIds = '';
    if(lenAttr > 0){
        var attrIds = '';
        for (var i = 0; i < lenAttr; i++) {
        	attrIds += selectAttr.options[i].value + ',';
    	}
    	document.getElementById("reportInfo").objAttrIds = attrIds.substring(0, attrIds.length-1);
    }
    
    if ($("txtSDate").value != "" && $("txtSTime").value != "" 
    	&& $("txtEDate").value != "" && $("txtETime").value != "") {
		var startTimeStr = $("txtSDate").value + " " + $("txtSTime").value + '';
    	var endTimeStr = $("txtEDate").value + " " + $("txtETime").value + '';
		document.getElementById("reportInfo").startTime = startTimeStr;
		document.getElementById("reportInfo").endTime = endTimeStr;
	} else {
		JetsenWeb.alert("请选择时间！");
		return;
	}
    
    document.getElementById("reportInfo").src = "alarmAnalysisPost.htm";
}

// 初始化===================================================================================
function pageInit() {
	parent.parent.document.getElementById("spanWindowName").innerHTML = document.title;
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

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReport1List"));
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
			height : 30
		}
	}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

function setParam(){
	var dialog = new JetsenWeb.UI.Window("set-divParam-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 800, height: 350 }, title: "设置条件" });
    dialog.controls = ["divParam"];
    dialog.onsubmit = function () {
    	var flag = 0;
    	var objOption = $("showObjOption");
    	var attrOption = $("showAttrOption");
    	if(objOption.length <= 0){
    		objOption.style.borderColor = "rgb(153, 51, 102)";
    		flag++;
    	}else{
    		objOption.style.borderColor = "";
    	}
    	if(attrOption.length <= 0){
    		attrOption.style.borderColor = "rgb(153, 51, 102)";
    		flag++;
    	}else{
    		attrOption.style.borderColor = "";
    	}
    	if(flag == 0){
    		JetsenWeb.alert("设置成功！");
    		this.close();
    	}
    };
    dialog.showDialog();
}
//报警类型
function alarmTypeAdd(){
	searchSelectAlarmTypeData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-alarmType"), { title: "选择报警类型", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectAlarmType"];
    dialog.onsubmit = function () {
        var alarmType = document.getElementsByName("chk_SelectAlarmType");
        for (var i = 0; i < alarmType.length; i++) {
            if (alarmType[i].checked)
                AddAlarmTypeItem(alarmType[i].value, alarmType[i].getAttribute("itemName"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddAlarmTypeItem(alarmTypeID, alarmTypeName) {
	var select = $("showAlarmTypeOption");
    var alarmTypeNewOption = document.createElement("option");
    select.options.add(alarmTypeNewOption);
    alarmTypeNewOption.value = alarmTypeID;
    alarmTypeNewOption.innerHTML = alarmTypeName;
}
function searchSelectAlarmTypeData() {
    pageAlarmType.currentPage = 1;
    getSelectAlarmTypeData();
}
function getSelectAlarmTypeData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var alarmTypeQueryTable = JetsenWeb.createQueryTable("BMP_ALARMTYPE");
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "TYPE_ID", PageInfo: pageAlarmType, ResultFields: "TYPE_ID,TYPE_NAME,TYPE_DESC",
        QueryTable: alarmTypeQueryTable
    });

    sqlQuery.OrderString = pageAlarmType.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    
    var txtAlarmTypeName = $('txtAlarmTypeName').value;
    if (txtAlarmTypeName != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_NAME", txtAlarmTypeName, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    
    //去除已选择的
    var selectAlarmType = $("showAlarmTypeOption");
    var lenAlarmType = selectAlarmType.options.length;
    if(lenAlarmType > 0){
    	var str = "";
	    for (var i = 0; i < lenAlarmType; i++) {
	    	str += selectAlarmType.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var resultXml = ret.resultVal;
    	var addPoint = resultXml.indexOf("<Record1>");
    	resultXml = resultXml.substring(0, addPoint) + "<Record><TYPE_ID>-1</TYPE_ID><TYPE_NAME>未分类报警</TYPE_NAME><TYPE_DESC>未分类报警</TYPE_DESC></Record>" + resultXml.substring(addPoint);
    	var pageSizeBeforePoint = resultXml.indexOf("<TotalCount>")+"<TotalCount>".length;
    	var pageSizeAfterPoint = resultXml.indexOf("</TotalCount>");
    	resultXml = resultXml.substring(0, pageSizeBeforePoint) + (parseInt(resultXml.substring(pageSizeBeforePoint, pageSizeAfterPoint))+1) + resultXml.substring(pageSizeAfterPoint);
        $('divSelectAlarmTypeList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisalarmtype.xslt", resultXml);
        alarmTypeGridList.bind($('divSelectAlarmTypeList'), $('tabSelectAlarmType'));
        pageAlarmType.setRowCount($('hid_AlarmTypeCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//报警规则
//function alarmRuleAdd(){
//	searchSelectAlarmRuleData();
//    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-alarmRule"), { title: "选择报警规则", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
//    dialog.controls = ["divSelectAlarmRule"];
//    dialog.onsubmit = function () {
//        var alarmRule = document.getElementsByName("chk_SelectAlarmRule");
//        for (var i = 0; i < alarmRule.length; i++) {
//            if (alarmRule[i].checked)
//                AddAlarmRuleItem(alarmRule[i].value, alarmRule[i].getAttribute("itemName"));
//        }
//        return true;
//    };
//    dialog.showDialog();
//}
//function AddAlarmRuleItem(alarmRuleID, alarmRuleName) {
//	var select = $("showAlarmRuleOption");
//    var alarmRuleNewOption = document.createElement("option");
//    select.options.add(alarmRuleNewOption);
//    alarmRuleNewOption.value = alarmRuleID;
//    alarmRuleNewOption.innerHTML = alarmRuleName;
//}
//function searchSelectAlarmRuleData() {
//    pageAlarmRule.currentPage = 1;
//    getSelectAlarmRuleData();
//}
//function getSelectAlarmRuleData(){
//	var sqlQuery = new JetsenWeb.SqlQuery();
//	var alarmRuleQueryTable = JetsenWeb.createQueryTable("BMP_ALARM");
//    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "ALARM_ID", PageInfo: pageAlarmRule, ResultFields: "*",
//        QueryTable: alarmRuleQueryTable
//    });
//
//    sqlQuery.OrderString = pageAlarmRule.orderBy;
//
//    var condition = new JetsenWeb.SqlConditionCollection();
//    
//    var txtAlarmRuleName = $('txtAlarmRuleName').value;
//    if (txtAlarmRuleName != "") {
//        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_NAME", txtAlarmRuleName, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
//    }
//    
//    //去除已选择的
//    var selectAlarmRule = $("showAlarmRuleOption");
//    var lenAlarmRule = selectAlarmRule.options.length;
//    if(lenAlarmRule > 0){
//    	var str = "";
//	    for (var i = 0; i < lenAlarmRule; i++) {
//	    	str += selectAlarmRule.options[i].value + ",";
//	    }
//	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ALARM_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
//    }
//    
//    sqlQuery.Conditions = condition;
//
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
//    ws.soapheader = JetsenWeb.Application.authenticationHeader;
//    ws.oncallback = function (ret) {
//        $('divSelectAlarmRuleList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisalarmrule.xslt", ret.resultVal);
//        alarmRuleGridList.bind($('divSelectAlarmRuleList'), $('tabSelectAlarmRule'));
//        pageAlarmRule.setRowCount($('hid_AlarmRuleCount').value);
//    }
//    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
//}
//报警等级
function alarmLevelAdd(){
	searchSelectAlarmLevelData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-alarmLevel"), { title: "选择报警等级", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectAlarmLevel"];
    dialog.onsubmit = function () {
        var alarmLevel = document.getElementsByName("chk_SelectAlarmLevel");
        for (var i = 0; i < alarmLevel.length; i++) {
            if (alarmLevel[i].checked)
                AddAlarmLevelItem(alarmLevel[i].value, alarmLevel[i].getAttribute("itemName"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddAlarmLevelItem(alarmLevelID, alarmLevelName) {
	var select = $("showAlarmLevelOption");
    var alarmLevelNewOption = document.createElement("option");
    select.options.add(alarmLevelNewOption);
    alarmLevelNewOption.value = alarmLevelID;
    var showName = "";
    switch(alarmLevelName){
    	case '0':showName="正常";break;
    	case '10':showName="警告报警";break;
    	case '20':showName="一般报警";break;
    	case '30':showName="重要报警";break;
    	case '40':showName="严重报警";break;
    	case '50':showName="离线报警";break;
    	default:showName="未知";
    }
    alarmLevelNewOption.innerHTML = showName;
}
function searchSelectAlarmLevelData() {
    pageAlarmLevel.currentPage = 1;
    getSelectAlarmLevelData();
}
function getSelectAlarmLevelData(){
    var alarmLevelData = '<?xml version="1.0" encoding="UTF-8"?><RecordSet><Record><ALARM_LEVEL>10</ALARM_LEVEL></Record><Record><ALARM_LEVEL>20</ALARM_LEVEL></Record><Record><ALARM_LEVEL>30</ALARM_LEVEL></Record><Record><ALARM_LEVEL>40</ALARM_LEVEL></Record><Record><ALARM_LEVEL type="int">50</ALARM_LEVEL></Record><Record1><TotalCount>5</TotalCount></Record1></RecordSet>';
    $('divSelectAlarmLevelList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisalarmlevel.xslt", alarmLevelData);
    alarmLevelGridList.bind($('divSelectAlarmLevelList'), $('tabSelectAlarmLevel'));
    pageAlarmLevel.setRowCount($('hid_AlarmLevelCount').value);
}
//对象
function objAdd(){
	searchSelectObjData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-obj"), { title: "选择对象", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectObj"];
    dialog.onsubmit = function () {
        var obj = document.getElementsByName("chk_SelectObj");
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked)
                AddObjItem(obj[i].value, obj[i].getAttribute("itemName"), obj[i].getAttribute("fatherID"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddObjItem(objID, objName, fatherID) {
	var select = $("showObjOption");
    var len = select.options.length;
    var objNewOption = document.createElement("option");
    select.options.add(objNewOption);
    objNewOption.value = objID;
    objNewOption.setAttribute('fatherID', fatherID);
    objNewOption.innerHTML = objName;
}
function searchSelectObjData() {
    pageObj.currentPage = 1;
    getSelectObjData();
}
function getSelectObjData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var ObjQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	ObjQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID=b.OBJ_ID or a.PARENT_ID=b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	ObjQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "b.GROUP_ID=c.GROUP_ID", JetsenWeb.TableJoinType.Inner));
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "a.OBJ_ID", PageInfo: pageObj, ResultFields: "a.OBJ_ID, a.OBJ_NAME",
        QueryTable: ObjQueryTable
    });

    sqlQuery.OrderString = pageObj.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    if($("groupChecked").value != ""){
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_TYPE", $("groupChecked").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.String));
	}else{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_TYPE", "1,3,4,0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
	}
    if ($("txtObjName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    //去除已选择的对象
    var selectObj = $("showObjOption");
    var lenObj = selectObj.options.length;
    if(lenObj > 0){
    	var str = "";
	    for (var i = 0; i < lenObj; i++) {
	    	str += selectObj.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;
    sqlQuery.GroupFields = " a.OBJ_ID, a.OBJ_NAME ";

//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectObjList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisobj.xslt", ret.resultVal);
        objGridList.bind($('divSelectObjList'), $('tabSelectObj'));
        pageObj.setRowCount($('hid_ObjCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), "a.OBJ_ID", "2"]);
}
//对象属性
function attrAdd(){
	if($("showObjOption").length <= 0){
		JetsenWeb.alert("请先选择对象！");
		return;
	}
	searchSelectAttrData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-attr"), { title: "选择对象属性", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectAttr"];
    dialog.onsubmit = function () {
        var attr = document.getElementsByName("chk_SelectAttr");
        for (var i = 0; i < attr.length; i++) {
            if (attr[i].checked)
                AddAttrItem(attr[i].value, attr[i].getAttribute("itemName"), attr[i].getAttribute("fatherID"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddAttrItem(attrID, attrName, fatherID) {
	var select = $("showAttrOption");
    var len = select.options.length;
    var attrNewOption = document.createElement("option");
    select.options.add(attrNewOption);
    attrNewOption.value = attrID;
    attrNewOption.setAttribute('fatherID', fatherID);
    attrNewOption.innerHTML = attrName;
}
function searchSelectAttrData() {
    pageAttr.currentPage = 1;
    getSelectAttrData();
}
function getSelectAttrData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var attrQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	attrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "b", "a.OBJ_ID=b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "a.OBJATTR_ID", PageInfo: pageAttr, ResultFields: " a.OBJATTR_ID, a.OBJATTR_NAME, b.OBJ_ID, b.OBJ_NAME ",
        QueryTable: attrQueryTable
    });

    sqlQuery.OrderString = pageAttr.orderBy;
    sqlQuery.GroupFields = " a.OBJATTR_ID, a.OBJATTR_NAME, b.OBJ_ID, b.OBJ_NAME ";

    var condition = new JetsenWeb.SqlConditionCollection();
    //取已选择对象的属性
    var selectObj = $("showObjOption");
    var lenObj = selectObj.options.length;
    if(lenObj > 0){
    	var str = "";
    	for(var i=0; i < lenObj; i++){
    		str += selectObj.options[i].value + ",";
    	}
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
    }else{
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.String));
    }
    
    if ($("txtAttrName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJATTR_NAME", $("txtAttrName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if ($("txtObjectName").value != "") {
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.OBJ_NAME", $("txtObjectName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", "102,103,104,105,107", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
    //去除已选择的属性
    var selectAttr = $("showAttrOption");
    var lenAttr = selectAttr.options.length;
    if(lenAttr > 0){
    	var str = "";
	    for (var i = 0; i < lenAttr; i++) {
	    	str += selectAttr.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJATTR_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectAttrList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmanalysisattr.xslt", ret.resultVal);
        attrGridList.bind($('divSelectAttrList'), $('tabSelectAttr'));
        pageAttr.setRowCount($('hid_AttrCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//删除选项
function selectOptionsDel(selCtrl)
{
    var itemCount = selCtrl.options.length;
    var selectedItemCount = 0;
    if (itemCount>0)
    {
    	var ids = '';
	    for(var i=itemCount-1;i>=0;i--)
	    {
		    if(selCtrl.options[i].selected)
		    {	
		    	ids += selCtrl.options[i].value + ',';
			    selCtrl.removeChild(selCtrl.options[i]);
			    selectedItemCount++;
		    }
	    }
	    ids = ids.substring(0, ids.length-1);
	    if(ids != '' && selCtrl.id == 'showObjOption'){
	    	selObjDelChild(ids);
	    }
    }
    if(selectedItemCount == 0) {
    	jetsennet.alert("请选择要删除的项！");
    }
}
//删除对象相关的属性
function selObjDelChild(objIds)
{
	if(objIds == null || objIds == '')
	{
		return;
	}
	var attrSel = $("showAttrOption");
	var len = attrSel.options.length;
	if(len > 0){
		var objIdArray = objIds.split(',');
		for(var i = 0; i < objIdArray.length; i++)
		{
			var objId = objIdArray[i];
			for(var j = attrSel.options.length - 1; j >= 0; j--)
			{
				if(objId == attrSel.options[j].getAttribute('fatherid'))
				{
					attrSel.removeChild(attrSel.options[j]);
				}
			}
		}
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