JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe", "js_xtree" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var AC_IMG_PATH = "./images/acIcon/";
var gGridList = new JetsenWeb.UI.GridList("attribclass-grid");
var gClassIdentifyGridList = new JetsenWeb.UI.GridList();
var gClassSetGridList = new JetsenWeb.UI.GridList();
var gCurSelectClass = { CLASS_ID : "", CLASS_NAME : "", CLASS_LEVEL : "-1" };

var picturePath = "";
var gPicturePage = new JetsenWeb.UI.PageBar("Picture");
gPicturePage.onpagechange = function () { loadPicture(); };
gPicturePage.changePageSize(20);
gPicturePage.orderBy = "ORDER BY PICTURE_ID";
var gPictureCondition = new JetsenWeb.SqlConditionCollection();
var gpicList = new JetsenWeb.UI.GridList("managerpic-grid");
gPicturePage.onupdate = function () 
{
    $('divSelectPicturePage').innerHTML = this.generatePageControl();
};
gpicList.ondatasort = function (sortfield, desc) 
{
    gPicturePage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_PICTURE", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "PICTURE_ID", PageInfo: gPicturePage, QueryTable: gQueryTable });

//报警规则配置
var generalAlarmConfig = null;
// 加载
function loadClass() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Left));
	
	var subConditions = new JetsenWeb.SqlCondition();
    subConditions.SqlLogicType = JetsenWeb.SqlLogicType.And;
    subConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "0", JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    subConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [
			JetsenWeb.SqlCondition.create("aa.CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual,
					JetsenWeb.SqlParamType.Numeric),
					subConditions,
			JetsenWeb.SqlCondition.create("aa.CLASS_ID", "99999", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
					JetsenWeb.SqlParamType.Numeric) ];
	sqlQuery.Conditions = condition;
	
	sqlQuery.OrderString = "ORDER BY aa.VIEW_POS,aa.CLASS_ID";
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.CLASS_ID",
		ResultFields : "aa.CLASS_ID,aa.CLASS_NAME,aa.CLASS_TYPE,aa.CLASS_LEVEL,aa.CLASS_GROUP,aa.CLASS_DESC,a.PARENT_ID,aa.MIB_ID", PageInfo : null, QueryTable : queryTable });

	// 初始化报警规则配置模版
	generalAlarmConfig = new jbmp.alarm.AlarmConfig("divGAlarmConfig", "报警规则设置");
	generalAlarmConfig.init();
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		renderGrid(ret.resultVal);
		var classes = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (classes != null) {
				gGridList.onrowclick = function (j){
					var  tablerow  =  $('attribclass-grid-tab-body').rows[j.rowIndex];
					var className = tablerow.cells[0].innerText;
					var classLevelcha = tablerow.cells[2].innerText; 
					var classLevel = 0;
					if(classLevelcha=="分类"){
						classLevel = 0;}
					else if(classLevelcha=="对象"){
						classLevel = 1;}
					else{ 
						classLevel = 2;
					}
					var classIdHtml = tablerow.cells[5].innerHTML;
					var classId = classIdHtml.split("'")[1];
					gCurSelectClass = { CLASS_ID : classId, CLASS_NAME : className, CLASS_LEVEL : classLevel};
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function renderGrid(xml){
	$("divContainer").innerHTML = "";
	gGridList.columns = [
	 { index: 0, fieldName: "CLASS_NAME", width:250, name: "名称"},
	 { index: 1, fieldName: "CLASS_TYPE", width:155, name: "别名"},
     { index: 2, fieldName: "CLASS_LEVEL", align: "center", name: "类型"},
     { index: 3, fieldName: "CLASS_GROUP", align: "center", name: "监控类别" },
     { index: 4, fieldName: "CLASS_DESC", width: 290, name: "描述" },
     { index: 5, fieldName: "CLASS_ID,CLASS_NAME,MIB_ID",align: "center", width: 75, name: "属性集" },
     { index: 6, fieldName: "CLASS_ID,MIB_ID",align: "center", width: 45, name: "属性" },
     { index: 7, fieldName: "CLASS_ID,CLASS_NAME", width: 45,  align: "center", name: "标识" },
     { index: 8, fieldName: "CLASS_ID,CLASS_NAME", width: 45, align: "center", name: "导出" },
     { index: 9, fieldName: "CLASS_ID", width: 45, align: "center", name: "编辑" },
     { index: 10, fieldName: "CLASS_ID", width: 45, align: "center", name: "删除"}];

	gGridList.columns[2].format = function (val,vals){
		if(val == 0)
		{
			return "分类";
		}
		else if(val == 1)
		{
			return "对象";
		}
		else if(val == 2)
		{
			return "子对象";
		}
		else
		{
			return "对象";
		}
	}
	gGridList.columns[3].format = function (val,vals){
		if(val == 0)
		{
			return "设备";
		}
		else if(val == 10)
		{
			return "信号";
		}
		else if(val == 20)
		{
			return "节目";
		}
		else
		{
			return "设备";
		}
	}
	
	gGridList.columns[5].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"viewClassSet('"+vals[0]+"','"+vals[1]+"','"+vals[2]+"')\"><img src='images/attribset.gif' border='0' style='cursor:pointer'/></a>"
		return val;
	}
	gGridList.columns[6].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"attrConfig('"+vals[0]+"','"+vals[1]+"')\"><img src='images/attribac.gif' border='0' style='cursor:pointer'/></a>"
		return val;
	}
	gGridList.columns[7].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"viewClassIdentify('"+vals[0]+"','"+vals[1]+"')\"><img src='images/mark.gif' border='0' style='cursor:pointer'/></a>"
		return val;
	}
	gGridList.columns[8].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"exportXml('"+vals[0]+"','"+vals[1]+"')\"><img src='images/export.gif' border='0' style='cursor:pointer' title='导出'/></a>"
		return val;
	}
	gGridList.columns[9].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"editClass('"+vals[0]+"')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	gGridList.columns[10].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"deleteClass('"+vals[0]+"');\">" +
				"<img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}
	gGridList.parentId = "";
	gGridList.idField = "CLASS_ID";
	gGridList.parentField = "PARENT_ID";
	gGridList.treeControlIndex = 0;
	gGridList.treeOpenLevel = 1;
	gGridList.dataSource = xml;
	gGridList.render("divContainer");
	gGridList.colorSelectedRows();
	gGridList.ondoubleclick = function(row,col){
		var rowId = row.id;
		var splitleng = rowId.split("-");
		editClass(splitleng[splitleng.length-1]);
	}
}

function loadClassByParent(classId, className, classLevel) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "aa");
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [
			JetsenWeb.SqlCondition.create("aa.CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual,
					JetsenWeb.SqlParamType.Numeric),
			JetsenWeb.SqlCondition.create("aa.CLASS_ID", "99999", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
					JetsenWeb.SqlParamType.Numeric) ];
	if (classId == "") {
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Left));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", classId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
	} else {
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Right));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", classId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	sqlQuery.Conditions = condition;
	sqlQuery.OrderString = "ORDER BY aa.VIEW_POS,aa.CLASS_ID";
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.CLASS_ID",
		ResultFields : "aa.CLASS_ID,aa.CLASS_NAME,aa.CLASS_TYPE,aa.CLASS_LEVEL,aa.CLASS_GROUP,aa.CLASS_DESC,aa.MIB_ID", PageInfo : null,
		QueryTable : queryTable });

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		el("divContainer").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribclass.xslt", sResult.resultVal);
		gGridList.bind(document.getElementById('divContainer'), document.getElementById('tabAttribClass'));
		gCurSelectClass = { CLASS_ID : classId, CLASS_NAME : className, CLASS_LEVEL : classLevel };
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
//新增=====================================================================================
function newClass() {
	var areaElements = JetsenWeb.Form.getElements('divClass');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	resetView();
	$("txt_CLASS_TYPE").disabled = false;
	$("cbo_CLASS_LEVEL").disabled = false;
	$("txt_ICON_SRC").value = "defaulticon.gif";
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 500, height : 540 }, title : "新建资源类型" });
	showRemindWordCount($("txt_CLASS_DESC").value,$('remindWord'),"30");
	dialog.controls = [ "divClass" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var _subTypeIds = "";
			var len = el("selSubType").options.length;
			for ( var i = 0; i < len; i++) {
				if (_subTypeIds != "")
					_subTypeIds += ",";
				_subTypeIds += el("selSubType").options[i].value;
			}
			var oClass = { PARENT_ID : gCurSelectClass.CLASS_ID, SUB_TYPES : _subTypeIds, CLASS_NAME : $("txt_CLASS_NAME").value,
				CLASS_TYPE : $("txt_CLASS_TYPE").value, CLASS_LEVEL : $("cbo_CLASS_LEVEL").value, MAN_ID : $("cbo_MAN_ID").value,
				CLASS_GROUP : $("cbo_CLASS_GROUP").value, MIB_ID : $("cbo_MIB_ID").value, CLASS_DESC : $("txt_CLASS_DESC").value,
				ICON_SRC : $("txt_ICON_SRC").value, VIEW_POS : $("txt_VIEW_POS").value, CREATE_USER : JetsenWeb.Application.userInfo.UserName ,
				FIELD_1:$("txt_picture_name").value };
			if(oClass.MIB_ID == ""){
				oClass.MIB_ID = "0";
			}
			if(parseInt(getBytesCount($("txt_CLASS_DESC").value))>60){
            	jetsennet.alert("描述不能超过30个文字！");
            	return;
            }
			
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				gCurSelectClass.CLASS_ID="";
				loadClass();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ATTRIBCLASS", JetsenWeb.Xml.serializer(oClass, "BMP_ATTRIBCLASS") ]);
		}
	};
	checkLevel($("cbo_CLASS_LEVEL").value);
	dialog.showDialog();
}
// 编辑=====================================================================================
function editClass(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divClass');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	resetView();
	$("txt_CLASS_TYPE").disabled = true;
	$("cbo_CLASS_LEVEL").disabled = true;

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "a.CLASS_ID", PageInfo : null, ResultFields : "a.*",
		QueryTable : queryTable });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objClass = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_CLASS_NAME").value = valueOf(objClass, "CLASS_NAME", "");
		$("txt_CLASS_TYPE").value = valueOf(objClass, "CLASS_TYPE", "");
		setSelectedValue($("cbo_MAN_ID"), valueOf(objClass, "MAN_ID", ""));
		setSelectedValue($("cbo_CLASS_LEVEL"), valueOf(objClass, "CLASS_LEVEL", ""));
		checkLevel(valueOf(objClass, "CLASS_LEVEL", ""));
		if (valueOf(objClass, "CLASS_LEVEL", "") == 1) {
			getSubType(keyId);
		}
		setSelectedValue($("cbo_CLASS_GROUP"), valueOf(objClass, "CLASS_GROUP", ""));
		setSelectedValue($("cbo_MIB_ID"), valueOf(objClass, "MIB_ID", ""));
		$("txt_ICON_SRC").value = valueOf(objClass, "ICON_SRC", "");
		$("txt_VIEW_POS").value = valueOf(objClass, "VIEW_POS", "");
		$("txt_CLASS_DESC").value = valueOf(objClass, "CLASS_DESC", "");
		$("txt_picture_name").value = valueOf(objClass, "FIELD_1", "");
		showRemindWordCount($("txt_CLASS_DESC").value,$('remindWord'),"30");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 500, height : 540}, title : "编辑资源类型" });
	dialog.controls = [ "divClass" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var _subTypeIds = "";
			var len = el("selSubType").options.length;
			for ( var i = 0; i < len; i++) {
				if (_subTypeIds != "")
					_subTypeIds += ",";
				_subTypeIds += el("selSubType").options[i].value;
			}
			var oClass = { CLASS_ID : keyId, SUB_TYPES : _subTypeIds, CLASS_NAME : $("txt_CLASS_NAME").value, CLASS_TYPE : $("txt_CLASS_TYPE").value,
				MAN_ID : $("cbo_MAN_ID").value, CLASS_LEVEL : $("cbo_CLASS_LEVEL").value, CLASS_GROUP : $("cbo_CLASS_GROUP").value,
				MIB_ID : $("cbo_MIB_ID").value, ICON_SRC : $("txt_ICON_SRC").value, VIEW_POS : $("txt_VIEW_POS").value,
				CLASS_DESC : $("txt_CLASS_DESC").value, FIELD_1:$("txt_picture_name").value };
			if(oClass.MAN_ID == ""){
				oClass.MAN_ID = "0";
			}
			if(oClass.MIB_ID == ""){
				oClass.MIB_ID = "0";
			}
			if(parseInt(getBytesCount($("txt_CLASS_DESC").value))>60){
            	jetsennet.alert("描述不能超过30个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				gCurSelectClass.CLASS_ID="";
				loadClass();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ATTRIBCLASS", JetsenWeb.Xml.serializer(oClass, "BMP_ATTRIBCLASS") ]);
		}
	};
	dialog.showDialog();
}
// 删除=====================================================================================
function deleteClass(keyId) {
	var fagChild = isChild(keyId);
	if(fagChild == "true"){
		jetsennet.alert("该设备类型下有子类型存在，请先删除其子类型。");
		return;
	}
	jetsennet.confirm("删除资源类型,将自动删除关联信息,不可恢复,确认吗？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		gCurSelectClass.CLASS_ID="";
		loadClass();		
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ATTRIBCLASS", keyId ]);
	 return true;
		});

}
// 加载类型标识=============================================================================
function loadClassIdentify(classId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_SNMPOBJTYPE", "");
	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", classId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId : "", QueryTable : queryTable, Conditions : conditions });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divClassIdentifyList").innerHTML = JetsenWeb.Xml.transformXML("xslt/snmpobjtype.xslt", ret.resultVal);
		gClassIdentifyGridList.bind($("divClassIdentifyList"), $("tabClassIdentify"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// 查看类型标识============================================================================
function viewClassIdentify(classId, className) {
	var areaElements = JetsenWeb.Form.getElements("divClassIdentify");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("divClassIdentifyList").innerHTML = "数据加载中...";

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true, size : { width : 600, height : 400 },
		title : "标识", cancelButtonText : "关闭" });
	dialog.controls = [ "divClassIdentify" ];
	dialog.onsubmit = function() {
		return false;
	};
	$("hid_CLASS_ID").value = classId;
	$("spanClassConfig").innerHTML = className;

	loadClassIdentify(classId);
	dialog.showDialog();
}
// 新增类型标识============================================================================
function newClassIdentify() {
	var areaElements = JetsenWeb.Form.getElements("divClassIdentifyEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 180 }, title : "新建标识" });
	dialog.controls = [ "divClassIdentifyEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var classId = $("hid_CLASS_ID").value;
			var objClassIdentify = { CLASS_ID : classId, SNMP_SYSOID : $("txt_SNMP_SYSOID").value, SNMP_VALUE : $("txt_SNMP_VALUE").value,
				CONDITION : getSelectedValue($("cbo_CONDITION")) };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadClassIdentify(classId);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_SNMPOBJTYPE", JetsenWeb.Xml.serializer(objClassIdentify, "BMP_SNMPOBJTYPE") ]);
		}
	};
	dialog.showDialog();
}
// 编辑类型标识============================================================================
function editClassIdentify(typeId) {
	var areaElements = JetsenWeb.Form.getElements("divClassIdentifyEdit");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", typeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TYPE_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPOBJTYPE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objClassIdentify = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("hid_CLASS_ID").value = valueOf(objClassIdentify, "CLASS_ID", "");
		$("txt_SNMP_SYSOID").value = valueOf(objClassIdentify, "SNMP_SYSOID", "");
		$("txt_SNMP_VALUE").value = valueOf(objClassIdentify, "SNMP_VALUE", "");
		setSelectedValue($("cbo_CONDITION"), valueOf(objClassIdentify, "CONDITION", ""));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 180 }, title : "编辑标识" });
	dialog.controls = [ "divClassIdentifyEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oClassIdentify = { TYPE_ID : typeId, CLASS_ID : $("hid_CLASS_ID").value, SNMP_SYSOID : $("txt_SNMP_SYSOID").value,
				SNMP_VALUE : $("txt_SNMP_VALUE").value, CONDITION : getSelectedValue($("cbo_CONDITION")) };

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadClassIdentify($("hid_CLASS_ID").value);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_SNMPOBJTYPE", JetsenWeb.Xml.serializer(oClassIdentify, "BMP_SNMPOBJTYPE") ]);
		}
	};
	dialog.showDialog();
}
// 删除类型标识============================================================================
function deleteClassIdentify(typeId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadClassIdentify($("hid_CLASS_ID").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_SNMPOBJTYPE", typeId ]);
	return true;
	});
}
// 加载属性集
// =============================================================================
function loadClassSet(classId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_CLASS2CLASS", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [
			JetsenWeb.SqlCondition.create("aa.PARENT_ID", classId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
					JetsenWeb.SqlParamType.Numeric),
			JetsenWeb.SqlCondition.create("a.CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Than,
					JetsenWeb.SqlParamType.Numeric) ];
	sqlQuery.Conditions = condition;
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.CLASS_ID", ResultFields : "aa.CLASS_ID,a.CLASS_NAME,a.CLASS_TYPE,a.CLASS_LEVEL",
		PageInfo : null, QueryTable : queryTable });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divClassSetList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribset.xslt", ret.resultVal);
		gClassSetGridList.bind($("divClassSetList"), $("tabClassSet"));
	    $("attribclass-tabClassSet-div-body").style.overflowX = 'hidden';
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// 查看属性集
// ============================================================================
function viewClassSet(classId, className,mibId) {
	var areaElements = JetsenWeb.Form.getElements("divClassSet");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("divClassSetList").innerHTML = "数据加载中...";

	var dialog = new JetsenWeb.UI.Window("view-object-win");
	JetsenWeb.extend(dialog, { cancelBox : true, windowStyle : 1, maximizeBox : true, minimizeBox : true, size : { width : 700, height : 400 },
		title : "属性集", cancelButtonText : "关闭" });
	dialog.controls = [ "divClassSet" ];
	dialog.onsubmit = function() {
		return false;
	};
	$("hid_CLASS_ID_ClassSet").value = classId;
	$("hid_MIB_ID_ClassSet").value = mibId;
	$("spanClassSet").innerHTML = className;

	loadClassSet(classId);
	dialog.showDialog();
}
// 新增属性集
// ============================================================================
function newClassSet() {
	var areaElements = JetsenWeb.Form.getElements('divClassSetEdit');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	resetViewSet(true);
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 280 }, title : "新建属性集" });
    showRemindWordCount($("txt_CLASS_DESC_ClassSet").value,$('remindWord2'),"30");
	dialog.controls = [ "divClassSetEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oClass = { PARENT_ID : $("hid_CLASS_ID_ClassSet").value, CLASS_NAME : $("txt_CLASS_NAME_ClassSet").value,
				CLASS_TYPE : $("txt_CLASS_TYPE_ClassSet").value, CLASS_LEVEL : $("cbo_CLASS_LEVEL_ClassSet2").value,
				CLASS_DESC : $("txt_CLASS_DESC_ClassSet").value, CREATE_USER : JetsenWeb.Application.userInfo.UserName ,
				MIB_ID : $("hid_MIB_ID_ClassSet").value};

			if(parseInt(getBytesCount($("txt_CLASS_DESC_ClassSet").value))>60){
            	jetsennet.alert("描述不能超过30个文字！");
            	return;
            }
			
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadClassSet($("hid_CLASS_ID_ClassSet").value);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ATTRIBCLASS", JetsenWeb.Xml.serializer(oClass, "BMP_ATTRIBCLASS") ]);
		}
	};
	dialog.showDialog();
}
// 编辑属性集
// ============================================================================
function editClassSet(classId) {
	var areaElements = JetsenWeb.Form.getElements('divClassSetEdit');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	resetViewSet(false);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", classId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "");
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "CLASS_ID", PageInfo : null, QueryTable : queryTable });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objClass = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_CLASS_NAME_ClassSet").value = valueOf(objClass, "CLASS_NAME", "");
		$("txt_CLASS_TYPE_ClassSet").value = valueOf(objClass, "CLASS_TYPE", "");
		setSelectedValue($("cbo_CLASS_LEVEL_ClassSet1"), valueOf(objClass, "CLASS_LEVEL", ""));
		setSelectedValue($("cbo_CLASS_LEVEL_ClassSet2"), valueOf(objClass, "CLASS_LEVEL", ""));
		var isAutoIns = objClass["FIELD_1"];
		if(isAutoIns != "1") {
			isAutoIns = "0";
		}
		setSelectedValue($("cbo_IS_AUTO_INS"), isAutoIns);
		$("txt_CLASS_DESC_ClassSet").value = valueOf(objClass, "CLASS_DESC", "");
	    showRemindWordCount($("txt_CLASS_DESC_ClassSet").value,$('remindWord2'),"30");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 280 }, title : "编辑属性集" });
	dialog.controls = [ "divClassSetEdit" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oClass = { 
					CLASS_ID : classId, 
					CLASS_NAME : $("txt_CLASS_NAME_ClassSet").value, 
					CLASS_TYPE : $("txt_CLASS_TYPE_ClassSet").value,
					CLASS_LEVEL : $("cbo_CLASS_LEVEL_ClassSet2").value, 
					CLASS_DESC : $("txt_CLASS_DESC_ClassSet").value,
					FIELD_1 : $("cbo_IS_AUTO_INS").value
			};

			if(parseInt(getBytesCount($("txt_CLASS_DESC_ClassSet").value))>60){
            	jetsennet.alert("描述不能超过30个文字！");
            	return;
            }
			
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadClassSet($("hid_CLASS_ID_ClassSet").value);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ATTRIBCLASS", JetsenWeb.Xml.serializer(oClass, "BMP_ATTRIBCLASS") ]);
		}
	};
	dialog.showDialog();
}
// 删除属性集
// ============================================================================
function deleteClassSet(classId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadClassSet($("hid_CLASS_ID_ClassSet").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ATTRIBCLASSSET", classId ]);
	 return true;
	});
}

// 初始化===================================================================================
function pageInit() {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType:1,fixControlIndex:0,splitBorder:0,showSplit:false});        
    
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divRightTitle"),{size:{width:0,height:27}}));
    gFrame.addControl(new JetsenWeb.UI.PageItem("divContainer"));
    
    // 属性弹出框
    var attrListBottom = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divAttrListBottom"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    attrListBottom.addControl(new JetsenWeb.UI.PageItem("divAttrList"));
    attrListBottom.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAttrBottom"), { size: { height: 30} }));
    attrListBottom.size = { height : 410 };
    
    var attrList = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divAttrListTop"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    attrList.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAttrSearch"), { size: { height: 30}}));
    attrList.addControl(attrListBottom);
    attrList.size = { height : 468 };
    
    var attrCont = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divAttrListPos"), { splitType : 1, fixControlIndex : 0, enableResize : false,showSplit:false });
    attrCont.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAttrTab"), { size : { height : 24 } }));
    attrCont.addControl(attrList);
    attrCont.size = { width : 998, height : 465 };
    attrCont.resize();
    
    var snmpContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divSnmpContent"), { splitType : 0, fixControlIndex : 0, enableResize : true });
	snmpContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divSnmpTree"), { size : { width : 525, height : 0 } }));
	snmpContent.addControl(new JetsenWeb.UI.PageItem("divSnmpNode"));
	snmpContent.size = { width : 780, height : 375 };
	snmpContent.resize();
    
 // 报警关联页面
 //	loadAlarm();
	var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"),
			{ splitType : 0, fixControlIndex : 0, enableResize : true });
	alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"), { size : { width : 210, height : 0 } }));
	alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
	alarmContent.size = { width : 680, height : 280 };
	alarmContent.resize();

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	
	// 选项卡
	initTabPane();

	loadClass();
	loadTableObject($('cbo_MAN_ID'), false, "BMP_MANUFACTURERS", "MAN_NAME", "MAN_ID");
	loadTableObject($('cbo_MIB_ID'), false, "BMP_MIBBANKS", "MIB_NAME", "MIB_ID");
	loadTableObject($('cbo_MIB_ID2'), false, "BMP_MIBBANKS", "MIB_NAME", "MIB_ID");
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}

function resetView() {
	var cboClassLevel = $("cbo_CLASS_LEVEL");
	cboClassLevel.options.length = 0;
	if (gCurSelectClass["CLASS_LEVEL"] == '-1') {
		cboClassLevel.options.add(new Option("分类", "0"));
	} else if (gCurSelectClass["CLASS_LEVEL"] == '0') {
		cboClassLevel.options.add(new Option("分类", "0"));
		cboClassLevel.options.add(new Option("对象", "1"));
		cboClassLevel.options.add(new Option("子对象", "2"));
	} else if (gCurSelectClass["CLASS_LEVEL"] == '1') {
		cboClassLevel.options.add(new Option("对象", "1"));
	} else {
		cboClassLevel.options.add(new Option("子对象", "2"));
	}
	$("selSubType").options.length = 0;
}

function checkLevel(level) {
	if (level != 1) {
		$('btnAdd').disabled = true;
		$('btnDel').disabled = true;
		$('btnAdd').className = "disablebutton";
		$('btnDel').className = "disablebutton";
		$("selSubType").options.length = 0;
	} else {
		$('btnAdd').disabled = false;
		$('btnDel').disabled = false;
		$('btnAdd').className = "button";
		$('btnDel').className = "button";
	}
}

function resetViewSet(isNew) {
	var cboClassLevel = $("cbo_CLASS_LEVEL_ClassSet2");
	cboClassLevel.options.length = 0;
	if (isNew) {
		$("txt_CLASS_TYPE_ClassSet").disabled = false;
		setSelectedValue($("cbo_CLASS_LEVEL_ClassSet1"), "101");
		$("cbo_CLASS_LEVEL_ClassSet2").disabled = false;
		cboClassLevel.options.add(new Option("表格数据", "106"));
		cboClassLevel.options.add(new Option("标量数据", "101"));
	} else {
		$("txt_CLASS_TYPE_ClassSet").disabled = true;
		$("cbo_CLASS_LEVEL_ClassSet2").disabled = true;
		cboClassLevel.options.add(new Option("", "100"));
		cboClassLevel.options.add(new Option("标量数据", "101"));
		cboClassLevel.options.add(new Option("", "102"));
		cboClassLevel.options.add(new Option("", "103"));
		cboClassLevel.options.add(new Option("", "104"));
		cboClassLevel.options.add(new Option("", "105"));
		cboClassLevel.options.add(new Option("表格数据", "106"));
	}
}

// 加载子对象类型
function getSubType(classID) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "aa");
	var condition = new JetsenWeb.SqlConditionCollection();
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Right));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", classID, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.CLASS_ID", PageInfo : null, ResultFields : "aa.CLASS_ID,aa.CLASS_NAME",
		QueryTable : queryTable });
	sqlQuery.Conditions = condition;

	el("selSubType").options.length = 0;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		var _typeNodes = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		if (_typeNodes == null) {
			return;
		}
		for ( var i = 0; i < _typeNodes.length; i++) {
			var objNewOption = document.createElement("option");
			el("selSubType").options.add(objNewOption);
			objNewOption.value = _typeNodes[i].CLASS_ID;
			objNewOption.innerHTML = _typeNodes[i].CLASS_NAME;
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// 选择子对象类型
function selectSubType() {
	getSelectSubTypeData();

	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-subtype"), { title : "选择子对象类型", submitBox : true, cancelBox : true,
		size : { width : 520, height : 300 }, maximizeBox : true, minimizeBox : true });
	dialog.controls = [ "divSelectSubType" ];
	dialog.onsubmit = function() {
		var obj = document.getElementsByName("chk_SelectSubType");
		for ( var i = 0; i < obj.length; i++) {
			if (obj[i].checked) {
				addSubTypeItem(obj[i].value, obj[i].getAttribute("itemName"));
			}
		}
		return true;
	};
	dialog.showDialog();
	dialog.adjustSize();
}
function getSelectSubTypeData() {
	$('divSelectSubTypeList').innerHTML = "数据加载中...";
	
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "CLASS_ID", PageInfo : null, ResultFields : "CLASS_ID,CLASS_NAME,CLASS_DESC",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ATTRIBCLASS" }) });

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", 2, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", getAllValues($("selSubType")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divSelectSubTypeList').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectsubtype.xslt", ret.resultVal);
		var o = new JetsenWeb.UI.GridList();
		var rc = o.bind($('divSelectSubTypeList'), $('tabSelectSubType'));
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function selectOptionsDel(selCtrl) {
	var _itemCount = selCtrl.options.length;
	var selectedItemCount = 0;
	if (_itemCount > 0) {
		for ( var i = _itemCount - 1; i >= 0; i--) {
			if (selCtrl.options[i].selected) {
				selCtrl.removeChild(selCtrl.options[i]);
				selectedItemCount++;
			}
		}
		if (selectedItemCount == 0) {
			jetsennet.alert("请选择要删除的项！");
		}
	} else {
		jetsennet.alert("请选择要删除的项！");
	}
}
function addSubTypeItem(classId, className) {
	var len = $("selSubType").options.length;
	for ( var i = 0; i < len; i++) {
		if ($("selSubType").options[i].value == classId) {
			return;
		}
	}
	var objNewOption = document.createElement("option");
	$("selSubType").options.add(objNewOption);
	objNewOption.value = classId;
	objNewOption.innerHTML = className;
}

//导出成xml
function exportXml(classId,name) {
		var url = "../../servlet/BmpExportResXmlServlet?flag=1";
		var className = name+".xml";
		url = url+"&classId="+classId+"&className="+encodeURI(className);
		window.open(url, "");

}

//导入xml文件
function importResouceXml()
{
	var areaElements = JetsenWeb.Form.getElements("divImportResouXml");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    // 重新初始化文件框
    $("formImportResouXml").reset();  
	var dialog = new JetsenWeb.UI.Window("import-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 170 }, title: "导入资源类型" });
    dialog.controls = ["divImportResouXml"];
    dialog.onsubmit = function () 
    {
        if (JetsenWeb.Form.Validate(areaElements, true))
        {
        	var mibId = $("cbo_MIB_ID2").value;
        	if(mibId == ""){
        		jetsennet.alert("请选择一个MIB类型！");
            	return;
			}
        	var filePath = $("fileResouAttachment").value;
        	var fileName = "";
            if (filePath.length == 0)
            {
            	jetsennet.alert("请选择要导入的文件！");
				return;
            }
            fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            if(!/\.(xml)$/.test(fileName))
            {
            	jetsennet.alert("导入文件格式错误！请重新导入！");
            	return;
            }
        	var form = $("formImportResouXml");
        	var parentId = gCurSelectClass.CLASS_ID;
        	form.action = "../../servlet/BMPImportResServlet?parentId="+parentId+"&mibId="+mibId;
        	form.submit();
        	JetsenWeb.UI.Windows.close("import-object-win");
        	var dialog = new JetsenWeb.UI.Window("result-resxml-win");
            JetsenWeb.extend(dialog, { windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 100 }, title: "导入资源类型" });
            dialog.controls = ["divImportResou"];
            var uploadIntervalId = setInterval(function()
            {
            	var uploadResult = $("frameImportResouXml").contentWindow.document.body.innerHTML;
            	if (uploadResult != "")
				{
            		if (uploadResult.indexOf("导入成功") != -1)
            		{
            			loadClass();
            			uploadResult = "导入成功！"; 
            			$("frameImportResouXml").contentWindow.document.body.innerHTML = "";
            		}
            		else if (uploadResult.indexOf("导入失败") != -1)
            		{
            			uploadResult = "导入失败！";
            			$("frameImportResouXml").contentWindow.document.body.innerHTML = "";
            		}
            		else{
            			uploadResult =uploadResult+" 已存在！不能导入相同别名的资源类型！";
            			$("frameImportResouXml").contentWindow.document.body.innerHTML = "";
            		}
            		JetsenWeb.UI.Windows.close("result-resxml-win");
            		clearInterval(uploadIntervalId);
            		jetsennet.alert(uploadResult);    		
            	}
           	}, 400);
            dialog.showDialog();
        }
    };
    dialog.showDialog();
}



//图标管理
function pictureManage() {
	loadPicture();
  var areaElements = JetsenWeb.Form.getElements('divViewPictureList');
  JetsenWeb.Form.resetValue(areaElements);
  JetsenWeb.Form.clearValidateState(areaElements);
  $("divViewPicture").innerHTML = "数据加载中...";   
  var dialog = new JetsenWeb.UI.Window("picture_manage");
	JetsenWeb.extend(dialog, { submitBox: false,cancelBox : false, windowStyle : 1, maximizeBox : true, minimizeBox : true, size : { width :505, height :550 },
		title : "拓扑图标管理", cancelButtonText : "关闭" });	
	dialog.controls = [ "divViewPictureList" ];	
	dialog.onsubmit = function() {
		JetsenWeb.UI.Windows.close("picture_manage");	
	};
	dialog.showDialog();
}

//加载图片
function loadPicture() 
{    
  gSqlQuery.OrderString = gPicturePage.orderBy;
  gSqlQuery.Conditions = gPictureCondition;     
  var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
  ws.soapheader = JetsenWeb.Application.authenticationHeader;
  ws.oncallback = function (ret) 
  {
      $("divViewPicture").innerHTML = JetsenWeb.Xml.transformXML("xslt/picture.xslt", ret.resultVal);
      gpicList.bind($("divViewPicture"), $("tabUserPicture"));
      gPicturePage.setRowCount($("hid_PictureCount").value);
      $("managerpic-grid-div-body").style.overflowX = 'hidden';
  };
  ws.onerror = function (ex) { jetsennet.error(ex); };
  ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

//上传文件
function uploadFile()
{
	var areaElements = JetsenWeb.Form.getElements("divAttachment");
  JetsenWeb.Form.resetValue(areaElements);
  JetsenWeb.Form.clearValidateState(areaElements);
  // 重新初始化上传文件框
  $("formUpload").reset();  
	var dialog = new JetsenWeb.UI.Window("upload-object-win");
  JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 120 }, title: "上传图片" });
  dialog.controls = ["divAttachment"];
  dialog.onsubmit = function () {
      if (JetsenWeb.Form.Validate(areaElements, true)) {
      	var filePath = $("fileAttachment").value;
      	var fileName = "";
          if (filePath.length == 0)
          {
          	jetsennet.alert("请选择你要上传的图片！");
				return;
          }
          fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
          if(!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(fileName))
          {
          	jetsennet.alert("图片类型必须是.gif,jpeg,jpg,png中的一种！");
          	return;
          }
          var issame = searchSamePicture(fileName);
          if(issame=="false"){ 
          	jetsennet.alert("上传的图片已存在！");
          	return;
			}     
          var fileRandomName = new Date().getTime();
      	var form = $("formUpload");
      	var path = "upload/TopImage";
      	form.action = "../../servlet/BMPFilesUploadServlet?fileName=" + fileRandomName + "&uploadPath="+path ;
      	form.submit();
      	JetsenWeb.UI.Windows.close("upload-object-win");
			var frameUploadDocument = $("frameUpload").contentWindow.document.body.innerHTML = "";
//			frameUploadDocument.body.innerHTML = "";
      	var dialog = new JetsenWeb.UI.Window("result-object-win");
          JetsenWeb.extend(dialog, { windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 100 }, title: "上传图片" });
          dialog.controls = ["divUploadTip"];
          var uploadIntervalId = setInterval(function()
          {
          	var uploadResult = $("frameUpload").contentWindow.document.body.innerHTML;
          	if (uploadResult != "")
				{
          		if (uploadResult.indexOf("成功") != -1)
          		{
          			var width="";
          			var height="";
          			if(uploadResult.indexOf('w=')!=-1){
          				width=uploadResult.substring(uploadResult.indexOf('w=')+2,uploadResult.indexOf('h='));
              			height=uploadResult.substring(uploadResult.indexOf('h=')+2);	
          			}
          			newAttachment(fileName,fileRandomName,path,width,height);
          			uploadResult = "上传成功！";
          			
          		}
          		JetsenWeb.UI.Windows.close("result-object-win");
          		clearInterval(uploadIntervalId);
          		jetsennet.alert(uploadResult);
          		
          	}
          }, 1000);
          dialog.showDialog();
      }
  };
  dialog.showDialog();
}
//新增附件信息
function newAttachment(fileName,fileRandomName,path,width,height )
{

  var objAttachment = {
     PICTURE_NAME: fileName,
     PICTURE_PATH: path+"/" + fileRandomName,
     CREATTIME: new Date().toDateTimeString(),
     FIELD_1: width,
     FIELD_2: height
  };
  var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
  ws.soapheader = JetsenWeb.Application.authenticationHeader;
  ws.onerror = function (ex) { jetsennet.error(ex); };
  ws.oncallback = function(ret) {
  	loadPicture();
  };
  ws.call("bmpObjInsert", ["BMP_PICTURE", JetsenWeb.Xml.serializer(objAttachment, "BMP_PICTURE")]);
}
//得到对象
function getPicture(id)
{  
  var sqlQuery = new JetsenWeb.SqlQuery();    
  JetsenWeb.extend(sqlQuery,{IsPageResult: 0, KeyId: "PICTURE_ID", PageInfo: null, ResultFields: "", QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), {TableName: "BMP_PICTURE"})});	        
  var condition = new JetsenWeb.SqlConditionCollection();
  condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PICTURE_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));        
  sqlQuery.Conditions = condition;
  var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
  ws.soapheader = JetsenWeb.Application.authenticationHeader;
  ws.async = false;
  ws.oncallback = function(PicturesResult)
  {
      var Picture = JetsenWeb.Xml.toObject(PicturesResult.resultVal, "Record")[0];
      picturePath = valueOf(Picture, "PICTURE_PATH", "");
  }
  ws.onerror = function(ex){ jetsennet.error(ex);};
  ws.call("bmpObjQuery",[sqlQuery.toXml()]);
}

//查询图片
function searchPicture()
{
  gPictureCondition.SqlConditions = [];
  if($('PICTURE_NAME_SEARCH').value!="")
  {
      gPictureCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("PICTURE_NAME", $('PICTURE_NAME_SEARCH').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));	       
  }
  gPicturePage.currentPage = 1;
  loadPicture();
}

//设为默认图标
function setDefaultPicture(id){
	   var sqlQuery = new JetsenWeb.SqlQuery();    
	    JetsenWeb.extend(sqlQuery,{IsPageResult: 0, KeyId: "PICTURE_ID", PageInfo: null, ResultFields: "", QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), {TableName: "BMP_PICTURE"})});	        
	    var condition = new JetsenWeb.SqlConditionCollection();
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PICTURE_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));        
	    sqlQuery.Conditions = condition;
	    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function(PicturesResult)
	    {
	        var Picture = JetsenWeb.Xml.toObject(PicturesResult.resultVal, "Record")[0];        
	        $("txt_picture_name").value = valueOf(Picture, "PICTURE_NAME", "");
	        JetsenWeb.UI.Windows.close("picture_manage");	
	    }
	    ws.onerror = function(ex){ jetsennet.error(ex);};
	    ws.call("bmpObjQuery",[sqlQuery.toXml()]);
}
//删除数据库中的图片
function deletePicture(pictureId)
{
  jetsennet.confirm("确定删除？", function () 
  {   	
      var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
      getPicture(pictureId);
      var path = picturePath;
      ws.soapheader = JetsenWeb.Application.authenticationHeader;
      ws.onerror = function(ex){ jetsennet.error(ex);};
      ws.oncallback = function(sResult)
      {	        
         loadPicture();
         deleteAttachment(path);
      }	
      ws.call("bmpObjDelete",["BMP_PICTURE",pictureId]);     
      return true;
  });
}

//删除在服务器上的图片
function deleteAttachment(filePaths)
{
	var filePathList = filePaths.split("|");
	var formDeleteFile = $("formDeleteFile");
	formDeleteFile.innerHTML = "";
	var hidType = document.createElement("input");
	hidType.name = "type";
	hidType.type = "hidden";
	hidType.value = "delete";
	formDeleteFile.appendChild(hidType);
	for (var i = 0; i < filePathList.length; i++)
	{
		var hidFilePath = document.createElement("input");
		hidFilePath.name = "filePath";
		hidFilePath.type = "hidden";
		hidFilePath.value = filePathList[i];
		formDeleteFile.appendChild(hidFilePath);
	}
	formDeleteFile.action = "../../servlet/BMPOperateFileSystemServlet";
	formDeleteFile.submit();         
}
//得到对象
function searchSamePicture(name)
{  
	var result = "true";
  var sqlQuery = new JetsenWeb.SqlQuery();    
  JetsenWeb.extend(sqlQuery,{IsPageResult: 0, KeyId: "PICTURE_ID", PageInfo: null, ResultFields: "", QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), {TableName: "BMP_PICTURE"})});	        
  var condition = new JetsenWeb.SqlConditionCollection();
  condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PICTURE_NAME", name, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));        
  sqlQuery.Conditions = condition;
  var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
  ws.soapheader = JetsenWeb.Application.authenticationHeader;
  ws.async = false;
  ws.oncallback = function(ret)
  {
  	if((ret.resultVal).indexOf("<Record>")!=-1){
  		result = "false" ;
  	}
  }
  ws.onerror = function(ex){ jetsennet.error(ex);};
  ws.call("bmpObjQuery",[sqlQuery.toXml()]);
  return result;
}

function isChild(id)
{	
	var result = false;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{	
		result = ret.value;
	}
	ws.onerror = function(ex){jetsennet.error(ex);};
	ws.call("isEquipmentChild",[id]); 
	return result;
}

