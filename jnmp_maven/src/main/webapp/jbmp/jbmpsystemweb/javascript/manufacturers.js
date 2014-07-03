JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gManPage = new JetsenWeb.UI.PageBar("Manufacturers");
gManPage.onpagechange = function() {
	loadMan();
};
gManPage.orderBy = "";
gManPage.onupdate = function() {
	$('divManPage').innerHTML = this.generatePageControl();
};
var gManCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gManPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_MANUFACTURERS", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, PageInfo : gManPage, QueryTable : gQueryTable });
// 加载=====================================================================================
function loadMan() {
	gSqlQuery.OrderString = gManPage.orderBy;
	gSqlQuery.Conditions = gManCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divManList').innerHTML = JetsenWeb.Xml.transformXML("xslt/manufacturers.xslt", ret.resultVal);
		gGridList.bind($('divManList'), $('tabMan'));
		gManPage.setRowCount($('hid_ManCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function searchMan() {
	gManCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gManCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("MAN_NAME", $('txt_Key').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	gManPage.currentPage = 1;
	loadMan();
}
// 删除=====================================================================================
function deleteMan(keyId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadMan();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_MANUFACTURERS", keyId ]);
	return true;
	});
}
// 新增=====================================================================================
function newMan() {
	var areaElements = JetsenWeb.Form.getElements('divMan');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 350, height : 220 }, title : "新建厂商" });
	 showRemindWordCount($("txt_MAN_DESC").value,$('remindWord'),"60");
	dialog.controls = [ "divMan" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objMan = getParams();
			if(parseInt(getBytesCount($("txt_MAN_DESC").value))>120){
            	jetsennet.alert("描述不能超过60个文字！");
            	return;
            }

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadMan();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_MANUFACTURERS", JetsenWeb.Xml.serializer(objMan, "BMP_MANUFACTURERS") ]);
		}
	};
	dialog.showDialog();
	$("txt_MAN_NAME").disabled = false;
	$("txt_MAN_NAME").focus();
}
// 编辑=====================================================================================
function editMan(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divMan');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MAN_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.String));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "MAN_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_MANUFACTURERS" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var oma = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		putParams(oma);
		showRemindWordCount($("txt_MAN_DESC").value,$('remindWord'),"60");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 350, height : 220 }, title : "编辑厂商" });
	dialog.controls = [ "divMan" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oMan = getParams();
			oMan["MAN_ID"] = keyId;
			if(parseInt(getBytesCount($("txt_MAN_DESC").value))>120){
            	jetsennet.alert("描述不能超过60个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadMan();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_MANUFACTURERS", JetsenWeb.Xml.serializer(oMan, "BMP_MANUFACTURERS") ]);
		}
	};
	dialog.showDialog();
}
function getParams() {
	var attrs = { MAN_NAME : $("txt_MAN_NAME").value, MAN_DESC : $("txt_MAN_DESC").value };
	return attrs;
}
function putParams(attrs) {
	$("txt_MAN_NAME").value = valueOf(attrs, "MAN_NAME", "");
	$("txt_MAN_DESC").value = valueOf(attrs, "MAN_DESC", "");
}
// 初始化===================================================================================
function pageInit() {
	searchMan();
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });
	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });
	frameContent.addControl(new JetsenWeb.UI.PageItem("divManList"));
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