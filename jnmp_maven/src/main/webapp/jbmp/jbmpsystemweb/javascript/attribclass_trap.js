// Trap================================================================================
//Trap分页控件
var gTrapPage = new JetsenWeb.UI.PageBar("divTrapBottomPage");
gTrapPage.onpagechange = function() {
	loadTrapList();
};
gTrapPage.orderBy = "";
gTrapPage.onupdate = function() {
	$('divTrapPage').innerHTML = this.generatePageControl();
};
function mibTrap() {
	if(curMibId <= 0) {
		alert("MibId小于0，数据配置错误。")
		return;
	}
	gTrapPage.currentPage = 1;
	var dialog = new JetsenWeb.UI.Window("trap-detail-win");
	JetsenWeb.extend(dialog, { 
		submitBox : false, 
		cancelBox : false, 
		windowStyle : 1, 
		cancelButtonText : "关闭",
		maximizeBox : true,
		minimizeBox : true, 
		size : { width : 800, height : 500 }, title : "Trap" });
	dialog.controls = [ "divTrap" ];
	//$("spanMibName").innerHTML = mibName;
	loadTrapList();
	dialog.showDialog();
}
function loadTrapList() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { 
		IsPageResult : 1, 
		PageInfo : gTrapPage, 
		KeyId : "TRAP_ID", 
		ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", curMibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var oid = $("txt_ATTRIB_VALUE").value;
	if(oid != "") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_OID", oid, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Like,
				JetsenWeb.SqlParamType.String));
	}
	sqlQuery.Conditions = condition;

	var gridList = new JetsenWeb.UI.GridList();

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/traptable.xslt", ret.resultVal);
		gridList.bind($('divTrapList'), $('tabTrapTable'));
		gTrapPage.setRowCount($('hid_TrapCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function newTrap() {
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("new-trap-win");
	JetsenWeb.extend(dialog, { 
		submitBox : true, 
		cancelBox : true, 
		windowStyle : 1, 
		maximizeBox : false, 
		minimizeBox : false,
		size : { width : 400, height : 380 }, 
		title : "新建Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var newObj = { 
					PARENT_ID : -1, 
					MIB_ID : curMibId, 
					TRAP_NAME : $("txt_TRAP_NAME").value, 
					TRAP_OID : $("txt_TRAP_OID").value,
					TRAP_DESC : $("txt_TRAP_DESC").value, 
					TRAP_VERSION : "NOTIFICATION-TYPE", 
					NAME_CN : $("txt_NAME_CN").value,
					DESC_CN : $("txt_DESC_CN").value };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-trap-win");
				loadTrapList();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_TRAPTABLE", JetsenWeb.Xml.serializer(newObj, "BMP_TRAPTABLE") ]);
		}
	}
	dialog.showDialog();
}
function editTrap(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { 
		IsPageResult : 0, 
		KeyId : "TRAP_ID", 
		PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		try {
			if (ret == null) {
				return;
			}
			var obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
			$("txt_TRAP_NAME").value = valueOf(obj, "TRAP_NAME", "");
			$("txt_TRAP_OID").value = valueOf(obj, "TRAP_OID", "");
			$("txt_NAME_CN").value = valueOf(obj, "NAME_CN", "");
			$("txt_TRAP_DESC").value = valueOf(obj, "TRAP_DESC", "");
			$("txt_DESC_CN").value = valueOf(obj, "DESC_CN", "");
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-trap-win");
	JetsenWeb.extend(dialog, { 
		submitBox : true, 
		cancelBox : true, 
		windowStyle : 1, 
		maximizeBox : false, 
		minimizeBox : false,
		size : { width : 400, height : 380 }, 
		title : "编辑Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var outObj = { 
					TRAP_ID : keyId, 
					TRAP_NAME : $("txt_TRAP_NAME").value, 
					TRAP_OID : $("txt_TRAP_OID").value,
					TRAP_DESC : $("txt_TRAP_DESC").value, 
					NAME_CN : $("txt_NAME_CN").value, 
					DESC_CN : $("txt_DESC_CN").value };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				loadTrapList();
				JetsenWeb.UI.Windows.close("edit-trap-win");
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_TRAPTABLE", JetsenWeb.Xml.serializer(outObj, "BMP_TRAPTABLE") ]);
		}
	};
	dialog.showDialog();
}
// 删除
function delTrap(keyId) {
	jetsennet.confirm("确定删除？", function () 
	{
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadTrapList();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_TRAPTABLE", keyId ]);
	return true;
	});
}
var curParentId = -1;
function showSub(trapName, trapId) {
	curParentId = trapId;
	loadSub(trapId);
	$("spanSubTrap").innerHTML = trapName;
	var dialog = new JetsenWeb.UI.Window("list-subTrap-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : true, cancelButtonText : "关闭", windowStyle : 1, maximizeBox : true, minimizeBox : true,
		size : { width : 650, height : 400 }, title : "子节点列表" });
	dialog.controls = [ "divSubTrap" ];
	dialog.showDialog();
}
function loadSub(trapId) {
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", trapId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TRAP_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		try {
			if (ret == null) {
				return;
			}
			$('divSubTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/mibbanks_trapsub.xslt", ret.resultVal);
			var gridList = new JetsenWeb.UI.GridList();
			gridList.bind($('divSubTrapList'), $('tabTrapTableSub'));
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// 添加子节点
function newTrapSub() {
	if (curParentId <= 0) {
		return;
	}
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	var dialog = new JetsenWeb.UI.Window("new-subtrap-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 380 }, title : "新建Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var newObj = { 
					PARENT_ID : curParentId, 
					MIB_ID : curMibId, 
					TRAP_NAME : $("txt_TRAP_NAME").value,
					TRAP_OID : $("txt_TRAP_OID").value, 
					TRAP_DESC : $("txt_TRAP_DESC").value, 
					TRAP_VERSION : "", 
					NAME_CN : $("txt_NAME_CN").value,
					DESC_CN : $("txt_DESC_CN").value };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-subtrap-win");
				loadSub(curParentId);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_TRAPTABLE", JetsenWeb.Xml.serializer(newObj, "BMP_TRAPTABLE") ]);
		}
	}
	dialog.showDialog();
}
// 编辑子节点
function editTrapSub(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TRAP_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }), title : "编辑Trap" });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		try {
			if (ret == null) {
				return;
			}
			var obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
			$("txt_TRAP_NAME").value = valueOf(obj, "TRAP_NAME", "");
			$("txt_TRAP_OID").value = valueOf(obj, "TRAP_OID", "");
			$("txt_NAME_CN").value = valueOf(obj, "NAME_CN", "");
			$("txt_TRAP_DESC").value = valueOf(obj, "TRAP_DESC", "");
			$("txt_DESC_CN").value = valueOf(obj, "DESC_CN", "");
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-subtrap-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 380 }, title : "编辑Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var outObj = { TRAP_ID : keyId, TRAP_NAME : $("txt_TRAP_NAME").value, TRAP_OID : $("txt_TRAP_OID").value,
				TRAP_DESC : $("txt_TRAP_DESC").value, NAME_CN : $("txt_NAME_CN").value, DESC_CN : $("txt_DESC_CN").value };
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-subtrap-win");
				loadSub(curParentId);
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_TRAPTABLE", JetsenWeb.Xml.serializer(outObj, "BMP_TRAPTABLE") ]);
		}
	};
	dialog.showDialog();
}
// 删除子节点
function delTrapSub(keyId) {
	jetsennet.confirm("确定删除？", function () {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadSub(curParentId);
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_TRAPTABLE", keyId ]);
	    return true;
	});
}