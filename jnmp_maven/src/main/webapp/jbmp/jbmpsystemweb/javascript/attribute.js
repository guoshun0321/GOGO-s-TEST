JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree", "js_tabpane" ]);
// 页面控件，表示当前页
var gFrame;
// tab控件
var gTabPane;
// 页面大小重置函数句柄
var gWindowSizeChangedInterVal;
// 属性分类
var collTaskArray;
// 分页
var gPage = new JetsenWeb.UI.PageBar("MonitorAttribute");
gPage.onpagechange = function() {
	search();
};
gPage.orderBy = "ORDER BY a.ATTRIB_ID";
gPage.onupdate = function() {
	$('divMonitorAttributePage').innerHTML = this.generatePageControl();
};
// 表格渲染
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gPage.setOrderBy(sortfield, desc);
};
// 查询条件
var gCondition = new JetsenWeb.SqlConditionCollection();
var gSqlQuery = new JetsenWeb.SqlQuery();

var gQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "a");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "b", "a.ATTRIB_ID=b.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "c", "b.CLASS_ID=c.CLASS_ID", JetsenWeb.TableJoinType.Left));
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM", "d", "a.ALARM_ID=d.ALARM_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, PageInfo : gPage, QueryTable : gQueryTable, ResultFields : "a.*,b.CLASS_ID,d.ALARM_NAME" });

// 图标路径
var AC_IMG_PATH = "./images/acIcon/";
// 属性分类数组
var attrClsArray;
// 当前分类ID，左侧树
var curClassId = -1;
// 当前子分类ID，右侧下拉框
var curSubClassId = -1;
// 当前子分类类型，右侧tab页
var curSubClassType = 100;
var curTabStr = 100;
// 当前属性ID，当前被选择属性
var curAttrId = -1;

// 页面初始化
window.onload = function() {
	// 构造页面
	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });
	frameContent.addControl(new JetsenWeb.UI.PageItem("divAcList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	var frameContent1 = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent1"), { splitType : 1, fixControlIndex : 0, showSplit : false });
	frameContent1.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAcSearch"), { size : { width : 0, height : 30 } }));
	frameContent1.addControl(frameContent);

	var frameContent2 = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent2"), { splitType : 1, fixControlIndex : 0, showSplit : false });
	frameContent2.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAcTab"), { size : { width : 0, height : 28 } }));
	frameContent2.addControl(frameContent1);

	var divLeftFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaLeft"), { splitType : 1, fixControlIndex : 0, splitBorder : 0,
		size : { width : 210, height : 0 }, showSplit : false });
	divLeftFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divLeftTitle"), { size : { width : 0, height : 27 } }));
	divLeftFrame.addControl(new JetsenWeb.UI.PageItem("divLeft"));
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 0, fixControlIndex : 0, enableResize : true });
	gFrame.addControl(divLeftFrame);
	gFrame.addControl(frameContent2);

	var snmpContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divSnmpContent"), { splitType : 0, fixControlIndex : 0, enableResize : true });
	snmpContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divSnmpTree"), { size : { width : 525, height : 0 } }));
	snmpContent.addControl(new JetsenWeb.UI.PageItem("divSnmpNode"));
	snmpContent.size = { width : 780, height : 375 };
	snmpContent.resize();

	// 报警关联页面
	loadAlarm();
	var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"),
			{ splitType : 0, fixControlIndex : 0, enableResize : true });
	alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"), { size : { width : 210, height : 0 } }));
	alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
	alarmContent.size = { width : 680, height : 280 };
	alarmContent.resize();

	// 参考值页面
	// loadValueType();
	// var vtContent = JetsenWeb.extend(new
	// JetsenWeb.UI.PageFrame("divValueType1"), { splitType: 0, fixControlIndex:
	// 0, enableResize: true });
	// vtContent.addControl(JetsenWeb.extend(new
	// JetsenWeb.UI.PageItem("divValueTypeList"),{ size: { width: 210, height:
	// 0} }));
	// vtContent.addControl(new JetsenWeb.UI.PageItem("divValueTypeDetail"));
	// vtContent.size = { width: 680, height: 280 };
	// vtContent.resize();

	// 页面大小变化的动作
	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	// 初始化tab
	initTabPane();
	// 属性分类树
	var gTree = genAttrClsTree();
	$("divAcTree").innerHTML = gTree;
	gTree.expandAll();
	// 初始化
	freshSub(-1);
	search();
}
// 调节窗口大小
function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}
// 属性分类树================================================================================
// 生成属性类型树
function genAttrClsTree() {
	var gTree = new JetsenWeb.UI.Tree("所有分类", "javascript:selTreeNode('',-1,null)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
	gTree.showTop = true;
	gTree.setBehavior("classic");

	attrClsArray = getAllAttrCls();
	if (attrClsArray) {
		var attrNode = new Array();
		for (i = 0; i < attrClsArray.length; i++) {
			var attrCls = attrClsArray[i];
			var node = null;
			if (attrCls["ICON_SRC"] != null && attrCls["ICON_SRC"].trim() != "") {
				node = new JetsenWeb.UI.TreeItem(attrCls["CLASS_NAME"], "javascript:selTreeNode('" + attrCls["CLASS_TYPE"] + "','"
						+ attrCls["CLASS_ID"] + "',attrClsArray[" + i + "])", null, AC_IMG_PATH + attrCls["ICON_SRC"], AC_IMG_PATH
						+ attrCls["ICON_SRC"]);
			} else {
				node = new JetsenWeb.UI.TreeItem(attrCls["CLASS_NAME"], "javascript:selTreeNode('" + attrCls["CLASS_TYPE"] + "','"
						+ attrCls["CLASS_ID"] + "',attrClsArray[" + i + "])");
			}
			attrNode.push(node);
			if (attrCls["USE_TYPE"] != "" && attrCls["USE_TYPE"] == 1) {
				continue;
			}
			if (attrCls["PARENT_ID"] == 0 || attrCls["PARENT_ID"] == "") {
				gTree.add(node);
			} else {
				for (j = 0; j < attrNode.length; j++) {
					if (attrClsArray[j]["CLASS_ID"] == attrCls["PARENT_ID"]) {
						attrNode[j].add(node);
						break;
					}
				}
			}
		}
	}
	return gTree;
}
// 获取全部属性分类
function getAllAttrCls() {
	var retval;
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.OrderString = "ORDER BY a.CLASS_ID";
	JetsenWeb.extend(sqlQuery, { KeyId : "CLASS_ID", QueryTable : queryTable, ResultFields : "a.*,b.PARENT_ID, b.USE_TYPE" });
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_LEVEL", "99", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Less, JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = queryConditions;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		retval = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	return retval;
}
// 树节点选择
function selTreeNode(type, id, acClass) {
	curSubClassId = -1;
	curClassId = id;
	freshSub();
	search();
}
//选项卡==================================================================================
//初始化
function initTabPane() {
	gTabPane = new JetsenWeb.UI.TabPane($('tabAc'), $('divPages'));
	gTabPane.ontabpagechanged = tabPageChanged;
}
//选项卡单击动作
function tabPageChanged(i, name) {
	$("aTrapImport").style.display = "none";
	if (name == "102" || name == "103" || name == "104,105") {
		$("aUnbondAlarm").style.display = "inline";
		$("aBondAlarm").style.display = "inline";
		if (name == "104") {
			$("aTrapImport").style.display = "inline";
		}
	} else {
		$("aUnbondAlarm").style.display = "none";
		$("aBondAlarm").style.display = "none";
	}
	curTabStr = name;
	curSubClassType = name;
	curSubClassId = -1;
	freshSub();
	search();
	gTabPane.select(gTabPane.selectedIndex);
}
// 右侧下拉框====================================================================================
// 获取子分类
function freshSub() {
	var queryTable = JetsenWeb.createQueryTable("BMP_CLASS2CLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { KeyId : "CLASS_ID", QueryTable : queryTable,
		ResultFields : "distinct a.CLASS_ID, b.CLASS_NAME, b.CLASS_TYPE, b.CLASS_LEVEL" });
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	if (curClassId != -1) {
		queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", curClassId, JetsenWeb.SqlLogicType.AndAll,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL", curTabStr, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = queryConditions;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		retval = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		freshSubOption(retval);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	return retval;
}
// 刷新子分类下拉框
function freshSubOption(clss) {

	curSubClassId = -1;
	var sel = $("selSubAC");
	sel.length = 0;
	if (clss == null || clss.length == 0) {
		changeNagAction();
		return;
	}
	for (i = 0; i < clss.length; i++) {
		var varItem = new Option(clss[i]["CLASS_NAME"], clss[i]["CLASS_ID"]);
		if(curSubClassId == -1) {
			curSubClassType = clss[i]["CLASS_LEVEL"];
			curSubClassId = clss[i]["CLASS_ID"];
		}
		varItem.classType = clss[i]["CLASS_TYPE"];
		varItem.typeId = clss[i]["CLASS_LEVEL"];
		sel.options.add(varItem);
	}
	changeNagAction();
}
// 下拉框选择动作
function comboxChange() {
	var comboxValue = getSelectedValue($("selSubAC"));
	if(comboxValue != null) {
		var classLevel =  getSelectedAttr($("selSubAC"), "typeId");
		curSubClassType = classLevel;
		curSubClassId = comboxValue;
	}
	changeNagAction();
	search();
}
// 搜索栏功能变化
function changeNagAction() {
	$("aTrapImport").style.display = "none";
	$("aUnbondAlarm").style.display = "none";
	$("aBondAlarm").style.display = "none";
	if (curSubClassType == "102" || curSubClassType == "103" || curSubClassType == "104" || curSubClassType == "105") {
		$("aUnbondAlarm").style.display = "inline";
		$("aBondAlarm").style.display = "inline";
		if (curSubClassType == "104") {
			$("aTrapImport").style.display = "inline";
		}
	}
}
// 查找，主页面======================================================================================================
function search() {
	$("divAcList").innerHTML = "";
	gCondition.SqlConditions = [];
	if (curClassId != -1) {
		gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID", curClassId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if (curSubClassId != -1) {
		gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_ID", curSubClassId, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", curTabStr, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));

	gSqlQuery.Conditions = gCondition;
	gSqlQuery.OrderString = gPage.orderBy;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		if (curSubClassType == "102" || curSubClassType == "103" || curSubClassType == "104" || curSubClassType == "105") {
			$("divAcList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute.xslt", ret.resultVal);
		} else {
			$("divAcList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attributenochk.xslt", ret.resultVal);
		}
		gGridList.bind($("divAcList"), $('tabMonitorAttribute'));
		gPage.setRowCount($('hid_Count').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
// 增删改===================================================================================
// 删除=====================================================================================
function deleteMonitorAttribute(keyId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		search();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ATTRIBUTE", keyId ]);
	return true;
	});
}
// 新增=====================================================================================
function newMonitorAttribute() {
	if (curSubClassId < 0) {
		jetsennet.alert("请在下拉框中选择类型！");
		return;
	}
	curAttrId = -1;
	var typeId = getSelectedAttr($("selSubAC"), "typeId");
	var areaElements = JetsenWeb.Form.getElements('divMonitorAttribute');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	$("txt_DATA_ENCODING").value = "ASCII";
	$("txt_VALUE_TYPE").value = "";
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 450 }, title : "新建属性" });
	dialog.controls = [ "divMonitorAttribute" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objMonitorAttribute = getParams(typeId);

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				search();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_ATTRIBUTE", JetsenWeb.Xml.serializer(objMonitorAttribute, "BMP_ATTRIBUTE") ]);
		}
	};
	dialog.showDialog();
	$("txt_ATTRIB_NAME").focus();
}
// 编辑=====================================================================================
function editMonitorAttribute(keyId, typeId) {
	curAttrId = keyId;

	var areaElements = JetsenWeb.Form.getElements('divMonitorAttribute');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ATTRIB_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ATTRIB_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ATTRIBUTE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var oma = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		putParams(oma);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : 450 }, title : "编辑属性" });
	dialog.controls = [ "divMonitorAttribute" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oMonitorAttribute = getParams(typeId);
			oMonitorAttribute["ATTRIB_ID"] = keyId;
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				search();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_ATTRIBUTE", JetsenWeb.Xml.serializer(oMonitorAttribute, "BMP_ATTRIBUTE") ]);
		}
	};
	dialog.showDialog();
}
function getParams(typeId) {
	var attrs = { ATTRIB_NAME : $("txt_ATTRIB_NAME").value, ATTRIB_VALUE : $("txt_ATTRIB_VALUE").value,
		VALUE_TYPE : $("txt_VALUE_TYPE").value == "" ? 0 : $("txt_VALUE_TYPE").value, DATA_ENCODING : $("txt_DATA_ENCODING").value,
		ATTRIB_PARAM : $("txt_ATTRIB_PARAM").value, DATA_TYPE : getSelectedValue($("cbo_DATA_TYPE")), DATA_UNIT : $("txt_DATA_UNIT").value,
		COLL_TIMESPAN : getSelectedValue($("sel_COLL_TIMESPAN")), IS_VISIBLE : getSelectedValue($("sel_IS_VISIBLE")),
		VIEW_TYPE : getSelectedValue($("sel_VIEW_TYPE")), ATTRIB_DESC : $("txt_ATTRIB_DESC").value,

		// 下面的是默认值
		CREATE_USER : JetsenWeb.Application.userInfo.UserName, CLASS_TYPE : getSelectedAttr($("selSubAC"), "classType"), CLASS_ID : curSubClassId,
		ATTRIB_TYPE : typeId, ATTRIB_MODE : 10 };
	return attrs;
}
function putParams(attrs) {
	$("txt_ATTRIB_NAME").value = valueOf(attrs, "ATTRIB_NAME", "");
	$("txt_ATTRIB_VALUE").value = valueOf(attrs, "ATTRIB_VALUE", "");
	$("txt_VALUE_TYPE").value = valueOf(attrs, "VALUE_TYPE", "");
	$("txt_DATA_ENCODING").value = valueOf(attrs, "DATA_ENCODING", "");
	$("txt_ATTRIB_PARAM").value = valueOf(attrs, "ATTRIB_PARAM", "");
	setSelectedValue($("cbo_DATA_TYPE"), valueOf(attrs, "DATA_TYPE", ""));
	$("txt_DATA_UNIT").value = valueOf(attrs, "DATA_UNIT", "");
	setSelectedValue($("sel_COLL_TIMESPAN"), valueOf(attrs, "COLL_TIMESPAN", ""));
	setSelectedValue($("sel_IS_VISIBLE"), valueOf(attrs, "IS_VISIBLE", "1"));
	setSelectedValue($("sel_VIEW_TYPE"), valueOf(attrs, "VIEW_TYPE", ""));
	$("txt_ATTRIB_DESC").value = valueOf(attrs, "ATTRIB_DESC", "");
}
// 属性值编辑============================================================================
// 报警关联==============================================================================
function bond() {
	attrib2alarm(curSubClassType);
}
function unbond() {
	bindNoAlarm(curSubClassType);
}
// 公式验证
function validateFormula() {
	var formula = $("txt_ATTRIB_PARAM").value;
	var str = new String(formula);
	if (str.trim() == "") {
		jetsennet.alert("请输入参数！");
		return;
	}
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var value = ret.resultVal;
		$("txt_VALUE_TYPE").value = value;
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpValidateFormula", [ formula, curSubClassId, curAttrId ]);
}
// 弹出MIB库====================================================================================
function mibDetails() {
	$("curMibType").value = ensureMibType(curSubClassId, curAttrId);
	var dialog = new JetsenWeb.UI.Window("mib-detail-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : true, windowStyle : 1, cancelButtonText : "关闭", maximizeBox : false,
		minimizeBox : false, size : { width : 800, height : 450 }, title : "MIB库" });
	dialog.controls = [ "divSnmp" ];
	dialog.show();

	genSnmpTree();
	$("divSnmpTree").innerHTML = snmpTree;
}
function ensureMibType() {
	var type = 1;
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		if (ret.resultVal != null && ret.resultVal != "") {
			type = ret.resultVal;
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpEnsureMibBank", [ curSubClassId, curAttrId ]);
	return type;
}
var snmpTree;
var snmpList = null;
function genSnmpTree() {
	snmpList = new Array();
	$("curNodeId").value = -3;
	snmpTree = new JetsenWeb.UI.Tree("所有分类", "javascript:clickNode(-3)");
	snmpTree.showTop = false;
	snmpTree.setBehavior("classic");

	gAutoBranch = new JetsenWeb.UI.TreeItem("自动添加的节点", "javascript:clickNode(-1, 0, 0, 0)");
	gManuBranch = new JetsenWeb.UI.TreeItem("手动添加的节点", "javascript:clickNode(-2, 0, 1, 0)");
	gAutoBranch.NODE_OID = "";
	gManuBranch.NODE_OID = "";
	snmpList.push( { key : "-1", value : gAutoBranch });
	snmpList.push( { key : "-2", value : gManuBranch });
	snmpTree.add(gAutoBranch);
	snmpTree.add(gManuBranch);
}
// id 树节点编号
// nodeId 数据库编号
// sourceType 来源
// nodeType 节点类型
function clickNode(id, nodeId, sourceType, nodeType) {
	var node = null;
	$("curNodeId").value = id;
	for (i = 0; i < snmpList.length; i++) {
		if (snmpList[i].key == id) {
			node = snmpList[i].value;
			break;
		}
	}
	if (node != null && node.childNodes.length == 0 && nodeType != 1 && nodeType != 4) {
		genSubNode(nodeId, node, sourceType);
	}
	clearSnmpNode();
	getNodeInfo(nodeId)
}
function genSubNode(parentId, node, sourceType) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NODE_ID", PageInfo : null,
		ResultFields : "NODE_ID,PARENT_ID,NODE_NAME,NODE_OID,SOURCE_TYPE,NODE_TYPE",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", $("curMibType").value, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SOURCE_TYPE", sourceType, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", parentId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var nodes = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (nodes != null) {
			for (i = 0; i < nodes.length; i++) {
				var tempId = nodes[i]["NODE_ID"];
				var branch = new JetsenWeb.UI.TreeItem(nodes[i]["NODE_NAME"], "javascript:clickNode(" + tempId + ", " + tempId + ", "
						+ nodes[i]["SOURCE_TYPE"] + ", " + nodes[i]["NODE_TYPE"] + ")");
				branch.NODE_OID = nodes[i]["NODE_OID"];
				snmpList.push( { key : tempId, value : branch });
				node.add(branch);
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function getNodeInfo(nodeId) {
	if (nodeId <= 0) {
		return;
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NODE_ID", PageInfo : null, ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("NODE_ID", nodeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		if (retObj != null) {
			$("txt_NODE_NAME").value = valueOf(retObj, "NODE_NAME");
			$("txt_NODE_OID").value = valueOf(retObj, "NODE_OID");
			$("txt_MIB_FILE").value = valueOf(retObj, "MIB_FILE");
			$("txt_NODE_DESC").value = valueOf(retObj, "NODE_DESC");
			$("txt_NODE_EXPLAIN").value = valueOf(retObj, "NODE_EXPLAIN");
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function clearSnmpNode() {
	var areaElements = JetsenWeb.Form.getElements('divSnmpNode');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
}
// Trap导入=====================================================================================
function importTrap() {
	if (curSubClassId < 0) {
		jetsennet.alert("请选择属性集！");
		return;
	}
	$("curMibType").value = ensureMibType(curSubClassId, curAttrId);
	var mibId = $("curMibType").value;
	var dialog = new JetsenWeb.UI.Window("trap-import-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, submitButtonText : "导入", cancelButtonText : "关闭",
		maximizeBox : false, minimizeBox : false, size : { width : 800, height : 500 }, title : "TRAP" });
	dialog.controls = [ "divTrap" ];
	dialog.onsubmit = function() {
		trapImport();
	};
	loadTrapList(mibId);
	dialog.showDialog();
}
function loadTrapList(mibId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TRAP_ID", PageInfo : null, ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", mibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var gridList = new JetsenWeb.UI.GridList();

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_trapimport.xslt", ret.resultVal);
		gridList.bind($('divTrapList'), $('tabTrapTable'));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function trapImport() {
	var curTrapIds = JetsenWeb.Form.getCheckedValues("chkAllTrap");
	if (curTrapIds == null || curTrapIds.length == 0) {
		jetsennet.alert("请选择要导入的Trap！");
		return;
	}
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		search();
		JetsenWeb.UI.Windows.close("trap-import-win");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpTrapImport", [ curTrapIds.join(","), JetsenWeb.Application.userInfo.UserName, curSubClassId ]);
}