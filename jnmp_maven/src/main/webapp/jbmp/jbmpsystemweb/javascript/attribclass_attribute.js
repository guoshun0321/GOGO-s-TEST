//选项卡==================================================================================
//初始化
function initTabPane() {
	gTabPane = new JetsenWeb.UI.TabPane($('tabAttr'), $('divAttrPages'));
	gTabPane.ontabpagechanged = tabPageChanged;
}
// 选项卡单击动作
function tabPageChanged(i, name) {
	curSubClassType = name;
	curSubAttribClass = -1;
	freshSub();
	searchAttrs();
	gTabPane.select(gTabPane.selectedIndex);
}
// 分页
var attrPage = new JetsenWeb.UI.PageBar("attrPage");
attrPage.onpagechange = function() {
	searchAttrs();
};
attrPage.orderBy = "ORDER BY a.ATTRIB_ID";
attrPage.onupdate = function() {
	$('divAttributePage').innerHTML = this.generatePageControl();
};
// 表格渲染
var attrGridList = new JetsenWeb.UI.GridList();
attrGridList.ondatasort = function(sortfield, desc) {
	attrPage.setOrderBy(sortfield, desc);
};
//分页
var trapPage = new JetsenWeb.UI.PageBar("trapPage");
trapPage.onpagechange = function() {
	loadImportTrapListTwo();
};
trapPage.orderBy = "ORDER BY TRAP_ID";
trapPage.changePageSize(20);
trapPage.onupdate = function() {
	$('divTrapPage').innerHTML = this.generatePageControl();
};
//表格渲染
var trapGridList = new JetsenWeb.UI.GridList();
trapGridList.ondatasort = function(sortfield, desc) {
	trapPage.setOrderBy(sortfield, desc);
};

// 查询条件
var attrCondition = new JetsenWeb.SqlConditionCollection();
var attrSqlQuery = new JetsenWeb.SqlQuery();

var attrQueryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "a");
attrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS", "b", "a.ATTRIB_ID=b.ATTRIB_ID", JetsenWeb.TableJoinType.Left));
attrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "c", "b.CLASS_ID=c.CLASS_ID", JetsenWeb.TableJoinType.Left));
attrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM", "d", "a.ALARM_ID=d.ALARM_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(attrSqlQuery, { IsPageResult : 1, PageInfo : attrPage, QueryTable : attrQueryTable, ResultFields : "a.*,b.CLASS_ID,d.ALARM_NAME" });

var curAttribClass = -1;
var curSubClassType = "100";
var curSubAttribClass = -1;
var curClassType = -1;
var curMibId = -1;
var curAttribClassType = "";
// 属性弹出框
function attrConfig(classId, mibId) {
	if(classId <= 0) 
	{
		curAttribClass = -1;
		return;
	}
	curMibId = mibId;
	curAttribClass = classId;
	curAttribClassType = getAttribclassType();
	var dialog = new JetsenWeb.UI.Window("attr-detail-win");
	JetsenWeb.extend(dialog, { 
		submitBox : false, 
		cancelBox : false, 
		windowStyle : 1, 
		cancelButtonText : "关闭", 
		maximizeBox : true,
		minimizeBox : true, 
		size : { width : 1000, height : 500 }, 
		title : "属性配置" });
	dialog.controls = [ "divAttrConfig" ];
	dialog.attachButtons = [ 
    // { text : "添加", clickEvent : function() { addSnmpNode(); } },
	// { text : "删除", clickEvent : function() { deleteSnmpNode(); } },
	// { text : "修改", clickEvent : function() { editSnmpNode(); } }
	];
	dialog.showDialog();
	freshSub();
	reSearchAttrs();
}
function reSearchAttrs() {
	attrPage.currentPage = 1;
	searchAttrs();
}
function searchAttrs() {
	$("divAttrList").innerHTML = "";
	attrCondition.SqlConditions = [];
	if (curAttribClass != -1) {
		attrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.PARENT_ID", curAttribClass, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	attrCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_ID", curSubAttribClass, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	attrSqlQuery.Conditions = attrCondition;
	attrSqlQuery.OrderString = attrPage.orderBy;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		if(curClassType == "100") {
			$("divAttrList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_custom.xslt", ret.resultVal);
		} else if(curClassType == "102" || curClassType == "103") {
			$("divAttrList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute.xslt", ret.resultVal);
		} else if(curClassType == "104" || curClassType == "105" || curClassType == "107") {
			$("divAttrList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_param.xslt", ret.resultVal);
		} else {
			$("divAttrList").innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_config.xslt", ret.resultVal);
		}
		attrGridList.bind($("divAttrList"), $('tabMonitorAttribute'));
		attrPage.setRowCount($('hid_Count').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ attrSqlQuery.toXml() ]);
}
// 属性配置页面下拉框====================================================================================
// 获取子分类
function freshSub() {
	var queryTable = JetsenWeb.createQueryTable("BMP_CLASS2CLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { 
		KeyId : "CLASS_ID", 
		QueryTable : queryTable,
		ResultFields : "distinct a.CLASS_ID, b.CLASS_NAME, b.CLASS_TYPE, b.CLASS_LEVEL" });
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", curAttribClass, JetsenWeb.SqlLogicType.AndAll,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL", curSubClassType, JetsenWeb.SqlLogicType.And,
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

	curSubAttribClass = -1;
	curClassType = -1;
	var sel = $("selSubAC");
	sel.length = 0;
	if (clss == null || clss.length == 0) {
		changeNagAction();
		return;
	}
	for (i = 0; i < clss.length; i++) {
		var varItem = new Option(clss[i]["CLASS_NAME"], clss[i]["CLASS_ID"]);
		if(curSubAttribClass == -1) {
			curSubAttribClass = clss[i]["CLASS_ID"];
			curClassType = clss[i]["CLASS_LEVEL"];
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
		curSubAttribClass = comboxValue;
		curClassType = classLevel;
	}
	changeNagAction();
	reSearchAttrs();
}
// 搜索栏功能变化
function changeNagAction() {
	$("aTrapImport").style.display = "none";
	$("aUnbondAlarm").style.display = "none";
	$("aBondAlarm").style.display = "none";
	$("aNewAttr").style.display = "none";
	if(curClassType == -1) {
		return;
	}
	$("aNewAttr").style.display = "";
	if (curClassType == "102" || curClassType == "103" || curClassType == "104" || curClassType == "105" || curClassType == "107") {
		$("aUnbondAlarm").style.display = "inline";
		$("aBondAlarm").style.display = "inline";
		if (curClassType == "104") {
			$("aTrapImport").style.display = "inline";
		}
	}
}
// 添删改
// 删除=====================================================================================
function deleteMonitorAttribute(keyId) {
	jetsennet.confirm("确定删除？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		searchAttrs();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_ATTRIBUTE", keyId ]);
	return true;
	});
}
function changeRows() {
	$("trAttribValue").style.display = "none";
	$("trAttribParam").style.display = "none";
	$("trValueType").style.display = "none";
	$("trDataType").style.display = "none";
	$("trDataUnit").style.display = "none";
	$("trViewType").style.display = "none";
	$("trCollSpan").style.display = "none";
	$("trIsVisible").style.display = "none";
	$("btnTrap").style.display = "none";
	$("txtAttribValue").innerHTML = "";
	$("txtAttribParam").innerHTML = "";
	$("txtValueType").innerHTML = "";
	var height = 450;
	if(curClassType == 100) {
		height = 230;
	} else if(curClassType == 101) {
		$("trAttribParam").style.display = "";
		$("trValueType").style.display = "";
		$("trDataType").style.display = "";
		$("trDataUnit").style.display = "";
		$("trViewType").style.display = "";
		$("trCollSpan").style.display = "";
		$("trIsVisible").style.display = "";
		$("txtAttribParam").innerHTML = "参数：";
		$("txtValueType").innerHTML = "枚举值：";
		if(curAttribClassType .indexOf('SOBEY_') > -1 || curAttribClassType .indexOf('DAYANG_') > -1){
			$("trAttribValue").style.display = "";
			$("txtAttribValue").innerHTML = "标识：";
		}
		height = 460;
	} else if(curClassType == 102) {
		$("trAttribParam").style.display = "";
		$("trValueType").style.display = "";
		$("trDataType").style.display = "";
		$("trDataUnit").style.display = "";
		$("trViewType").style.display = "";
		$("trCollSpan").style.display = "";
		$("trIsVisible").style.display = "";
		$("txtAttribParam").innerHTML = "参数：";
		$("txtValueType").innerHTML = "枚举值：";
		height = 430;
	} else if(curClassType == 103) {		
		var leng = getLCAttribute();		
		$("trAttribParam").style.display = "";			
		$("trDataType").style.display = "";
		$("trDataUnit").style.display = "";
		$("trViewType").style.display = "";
		$("trCollSpan").style.display = "";
		$("trIsVisible").style.display = "";
		if(leng > 0){
			$("txtAttribParam").innerHTML = "公式：";		
			$("txt_ATTRIB_PARAM_MIB").style.display = "none";
		}else
		{
			$("txtAttribParam").innerHTML = "参数：";
			$("trValueType").style.display = "";
			$("txtValueType").innerHTML = "枚举值：";
			$("txt_ATTRIB_PARAM_MIB").style.display = "";
		}	
		if(curAttribClassType .indexOf('SOBEY_') > -1 || curAttribClassType .indexOf('DAYANG_') > -1){
			$("trAttribValue").style.display = "";
			$("txtAttribValue").innerHTML = "标识：";
		}
		height = 460;
	} else if(curClassType == 104) {
		$("trAttribValue").style.display = "";
		$("txtAttribValue").innerHTML = "标识：";
		$("btnTrap").style.display = "";
		height = 330;
	} else if(curClassType == 105) {
		$("trAttribValue").style.display = "";
		$("txtAttribValue").innerHTML = "标识：";
		height = 330;
	} else if(curClassType == 106) {
		$("trAttribParam").style.display = "";
		$("trValueType").style.display = "";
		$("trDataType").style.display = "";
		$("trDataUnit").style.display = "";
		$("trViewType").style.display = "";
		$("trCollSpan").style.display = "";
		$("trIsVisible").style.display = "";
		$("txtAttribParam").innerHTML = "参数：";
		$("txtValueType").innerHTML = "枚举值：";
		if(curAttribClassType .indexOf('SOBEY_') > -1 || curAttribClassType .indexOf('DAYANG_') > -1){
			$("trAttribValue").style.display = "";
			$("txtAttribValue").innerHTML = "标识：";
		}
		height = 460;
	} else if(curClassType == 107) {
		$("trAttribValue").style.display = "";
		$("txtAttribValue").innerHTML = "标识：";
		height = 280;
	}
	if(isSpecial()) {
		$("trAttribParam").style.display = "none";
		$("trValueType").style.display = "none";
	}
	return height;
}
function isSpecial() {
	if((curAttribClass >=1 && curAttribClass <=42) 
			|| (curAttribClass >= 2115 && curAttribClass <= 2180)
			|| (curAttribClass >= 2205 && curAttribClass <= 2216) 
			|| (curAttribClass >= 10086 && curAttribClass <= 10103)) {
		return true;
	}
	return false;
}
// 新增=====================================================================================
function newMonitorAttribute() {
	if (curSubAttribClass < 0) {
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
	var dheight = changeRows();
	showRemindWordCount($("txt_ATTRIB_DESC").value,$('remindWorddesc'),"600");
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : dheight }, title : "新建属性" });
	dialog.controls = [ "divMonitorAttribute" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objMonitorAttribute = getParams(typeId);
			if(parseInt(getBytesCount($("txt_ATTRIB_DESC").value))>1200){
            	jetsennet.alert("描述不能超过600个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				searchAttrs();
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
		showRemindWordCount($("txt_ATTRIB_DESC").value,$('remindWorddesc'),"600");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dheight = changeRows();
	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 450, height : dheight }, title : "编辑属性" });
	dialog.controls = [ "divMonitorAttribute" ];
	changeRows();
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oMonitorAttribute = getParams(typeId);
			oMonitorAttribute["ATTRIB_ID"] = keyId;
			if(parseInt(getBytesCount($("txt_ATTRIB_DESC").value))>1200){
            	jetsennet.alert("描述不能超过600个文字！");
            	return;
            }
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				searchAttrs();
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
	var attrs = { ATTRIB_NAME : $("txt_ATTRIB_NAME").value, 
			ATTRIB_VALUE : $("txt_ATTRIB_VALUE").value,
			VALUE_TYPE : $("txt_VALUE_TYPE").value == "" ? 0 : $("txt_VALUE_TYPE").value, 
			DATA_ENCODING : $("txt_DATA_ENCODING").value,
			ATTRIB_PARAM : $("txt_ATTRIB_PARAM").value, 
			DATA_TYPE : getSelectedValue($("cbo_DATA_TYPE")), 
			DATA_UNIT : $("txt_DATA_UNIT").value,
			COLL_TIMESPAN : getSelectedValue($("sel_COLL_TIMESPAN")), 
			IS_VISIBLE : getSelectedValue($("sel_IS_VISIBLE")),
			VIEW_TYPE : getSelectedValue($("sel_VIEW_TYPE")), 
			ATTRIB_DESC : $("txt_ATTRIB_DESC").value,
			// 下面的是默认值
			CREATE_USER : JetsenWeb.Application.userInfo.UserName, 
			CLASS_TYPE : getSelectedAttr($("selSubAC"), "classType"), 
			CLASS_ID : curSubAttribClass,
			ATTRIB_TYPE : typeId, 
			ATTRIB_MODE : 10 };
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
// 报警关联==============================================================================
function bond() {
	attrib2alarm(curClassType);
}
function unbond() {
	bindNoAlarm(curClassType);
}
// 公式验证
function validateFormula() {
	var formula = $("txt_ATTRIB_PARAM").value;
	var str = new String(formula);
	if (str.trim() == "") {
		jetsennet.alert("请输入参数！");
		return;
	}
	var len = getLCAttribute();
	if(len>0)
	{
		var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			var value = ret.resultVal;
            if(""!=value){
            	jetsennet.error(value);
            }
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpValidateExpression", [ formula ]);
	}
	else
	{
		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			var value = ret.resultVal;
			$("txt_VALUE_TYPE").value = value;
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpValidateFormula", [ formula, curSubAttribClass, -1 ]);
	}
}
// 弹出MIB库====================================================================================
function mibDetails() {
	$("curMibType").value = ensureMibType();
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
	ws.call("bmpEnsureMibBank", [ curSubAttribClass, -1 ]);
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
			var j = 0;
			for (i = 0; i < nodes.length; i++) {
				j++;
				var tempId = nodes[i]["NODE_ID"];
				var branch = new JetsenWeb.UI.TreeItem(nodes[i]["NODE_NAME"], "javascript:clickNode(" + tempId + ", " + tempId + ", "
						+ nodes[i]["SOURCE_TYPE"] + ", " + nodes[i]["NODE_TYPE"] + ")");
				branch.NODE_OID = nodes[i]["NODE_OID"];
				snmpList.push( { key : tempId, value : branch });
				node.add(branch);
			}
			if(j>0){
				node.expand();
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
	if (curSubAttribClass < 0) {
		jetsennet.alert("请选择属性集！");
		return;
	}
	$("curMibType").value = ensureMibType();
	var mibId = $("curMibType").value;
	loadImportTrapList(mibId);
    var areaElements = JetsenWeb.Form.getElements('divTrap');
	    JetsenWeb.Form.resetValue(areaElements);
	    JetsenWeb.Form.clearValidateState(areaElements);
	    $('divTrapList').innerHTML = "数据加载中...";   
	var dialog = new JetsenWeb.UI.Window("trap-import-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, submitButtonText : "导入",
		maximizeBox : false, minimizeBox : false, size : { width : 800, height : 500 }, title : "Trap" });
	dialog.controls = [ "divTrap" ];
	dialog.onsubmit = function() {
		trapImport();
	};
	dialog.showDialog();	
}

function loadImportTrapList(mibId) {
//	trapPage.currentPage = 1;
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, KeyId : "TRAP_ID", PageInfo: trapPage, ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	sqlQuery.OrderString = trapPage.orderBy;
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", mibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;
	var gridList = new JetsenWeb.UI.GridList();
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
//	ws.async = false;
	ws.oncallback = function(ret) {
		$('divTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_trapimport.xslt", ret.resultVal);
		trapGridList.bind($('divTrapList'), $('tabTrapTable'));
		trapPage.setRowCount($("hid_TrapCountView").value);
		$("attribclass-tabTrapTable-div-body").style.overflowX = 'hidden';
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
		searchAttrs();
		JetsenWeb.UI.Windows.close("trap-import-win");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpTrapImport", [ curTrapIds.join(","), JetsenWeb.Application.userInfo.UserName, curSubAttribClass ]);
}

function loadImportTrapListTwo() {
//	trapPage.currentPage = 1;
	var mibId = ensureMibType();
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, KeyId : "TRAP_ID", PageInfo: trapPage, ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	sqlQuery.OrderString = trapPage.orderBy;
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", mibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;
	var gridList = new JetsenWeb.UI.GridList();
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
//	ws.async = false;
	ws.oncallback = function(ret) {
		$('divTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/attribute_trapimport.xslt", ret.resultVal);
		trapGridList.bind($('divTrapList'), $('tabTrapTable'));
		trapPage.setRowCount($("hid_TrapCountView").value);
		$("attribclass-tabTrapTable-div-body").style.overflowX = 'hidden';
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function getLCAttribute(){
	var leng = -2;
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID",curAttribClass,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_TYPE","INSPUR_ROOM",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
	var sqlQuery = new JetsenWeb.SqlQuery();    
	JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"CLASS_ID",PageInfo:null,ResultFields:"CLASS_ID",               
		QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"BMP_ATTRIBCLASS"})});
	sqlQuery.Conditions = conditionCollection;	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret)
	{		 	
	  leng = ret.resultVal.indexOf("<Record>");
	}
	ws.onerror = function(ex){jetsennet.error(ex);};
	ws.call("bmpObjQuery",[sqlQuery.toXml()]); 
	return leng;
}

function getAttribclassType(){
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", curAttribClass, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ATTRIB_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ATTRIBCLASS" }) });
	sqlQuery.Conditions = condition;

	var attribClassType = "";
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		attribClassType = JetsenWeb.Xml.toObject(ret.resultVal,"Record")[0].CLASS_TYPE;
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	
	return attribClassType;
}