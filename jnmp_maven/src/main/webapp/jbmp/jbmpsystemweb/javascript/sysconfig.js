JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gSysconfigPage = new JetsenWeb.UI.PageBar("Sysconfig");
gSysconfigPage.onpagechange = function() {
	loadSysconfig();
};
gSysconfigPage.orderBy = "";
gSysconfigPage.onupdate = function() {
	$('divSysconfigPage').innerHTML = this.generatePageControl();
};
var gSysconfigCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gSysconfigPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("NET_SYSCONFIG", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, PageInfo : gSysconfigPage, QueryTable : gQueryTable });
// 加载=====================================================================================
function loadSysconfig() {
	gSqlQuery.OrderString = gSysconfigPage.orderBy;
	gSqlQuery.Conditions = gSysconfigCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divSysconfigList').innerHTML = JetsenWeb.Xml.transformXML("xslt/sysconfig.xslt", ret.resultVal);
		gGridList.bind($('divSysconfigList'), $('tabSysconfig'));
		gSysconfigPage.setRowCount($('hid_SysconfigCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function searchSysconfig() {
	gSysconfigCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gSysconfigCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("NAME", $('txt_Key').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	gSysconfigPage.currentPage = 1;
	loadSysconfig();
}
// 删除=====================================================================================
function deleteSysconfig(keyId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadSysconfig();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "NET_SYSCONFIG", keyId ]);
	return true;
	});
}
// 新增=====================================================================================
function newSysconfig() {
	var areaElements = JetsenWeb.Form.getElements('divSysconfig');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 250 }, title : "新建系统参数" });
	showRemindWordCount($("txt_PARAM_DESC").value,$('remindWord'),"60");
	dialog.controls = [ "divSysconfig" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objSysconfig = getParams();
			objSysconfig.NAME=objSysconfig.NAME.trim();
			if(parseInt(getBytesCount($("txt_PARAM_DESC").value))>120){
            	jetsennet.alert("参数描述不能超过60个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadSysconfig();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "NET_SYSCONFIG", JetsenWeb.Xml.serializer(objSysconfig, "NET_SYSCONFIG") ]);
		}
	};
	dialog.showDialog();
	$("txt_PARAM_NAME").disabled = false;
	$("txt_PARAM_NAME").focus();
}
// 编辑=====================================================================================
function editSysconfig(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divSysconfig');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("NAME", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.String));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NAME", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "NET_SYSCONFIG" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var oma = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		putParams(oma);
		showRemindWordCount($("txt_PARAM_DESC").value,$('remindWord'),"60");
		
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 250 }, title : "编辑系统参数" });
	dialog.controls = [ "divSysconfig" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oSysconfig = getParams();
			oSysconfig["NAME"] = keyId;
			if(parseInt(getBytesCount($("txt_PARAM_DESC").value))>120){
            	jetsennet.alert("参数描述不能超过60个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadSysconfig();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "NET_SYSCONFIG", JetsenWeb.Xml.serializer(oSysconfig, "NET_SYSCONFIG") ]);
		}
	};
	dialog.showDialog();
	$("txt_PARAM_NAME").disabled = true;
}
function getParams() {
	var attrs = { NAME : $("txt_PARAM_NAME").value, DATA : $("txt_PARAM_VALUE").value, DESCRIPTION : $("txt_PARAM_DESC").value };
	return attrs;
}
function putParams(attrs) {
	$("txt_PARAM_NAME").value = valueOf(attrs, "NAME", "");
	$("txt_PARAM_VALUE").value = valueOf(attrs, "DATA", "");
	$("txt_PARAM_DESC").value = valueOf(attrs, "DESCRIPTION", "");
}
// 初始化===================================================================================
function pageInit() {
	searchSysconfig();

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });

	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });

	frameContent.addControl(new JetsenWeb.UI.PageItem("divSysconfigList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 30 } }));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
}
// 调节窗口大小
function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}


//得到字符串字节数
function getBytesCount(str) 
{ 
	var bytesCount = 0; 
	if (str != null) 
	{ 
		for (var i = 0; i < str.length; i++) 
		{ 
			var c = str.charAt(i); 
			if (/^[\u0000-\u00ff]$/.test(c)) 
			{ 
				bytesCount += 1; 
			} 
			else 
			{ 
				bytesCount += 2; 
			} 
		} 
	} 
	return bytesCount; 
}

//textarea 作文字控制
function showRemindWordCount(textValue,remindWordHtml,wordCount){
	var countNum = 2*parseInt(wordCount);
	remindWordHtml.innerHTML = parseInt((countNum-parseInt(getBytesCount(textValue)))/2);
	if(countNum<parseInt(getBytesCount(textValue))){
		remindWordHtml.style.color = "red";
		remindWordHtml.innerHTML = 0;
	}else{
		remindWordHtml.style.color = "black";
	}
}