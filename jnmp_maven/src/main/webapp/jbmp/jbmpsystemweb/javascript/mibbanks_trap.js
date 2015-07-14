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
var trapGridList = new JetsenWeb.UI.GridList();
trapGridList.ondatasort = function(sortfield, desc) {
	gTrapPage.setOrderBy(sortfield, desc);
};
var curMibId = -1;
function mibTrap(mibId, mibName, mibAlias) {
	$("curMibType").value = mibId;
	gTrapPage.currentPage = 1;
	var dialog = new JetsenWeb.UI.Window("trap-detail-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : false, windowStyle : 1, cancelButtonText : "关闭", maximizeBox : true,
		minimizeBox : true, size : { width : 800, height : 500 }, title : "Trap" });
	dialog.controls = [ "divTrap" ];
	$("spanMibName").innerHTML = mibName;
	dialog.showDialog();
	loadTrapList();
}
function loadTrapList() {
	var mibId = $("curMibType").value;
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, PageInfo : gTrapPage, KeyId : "TRAP_ID", ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", mibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;
	sqlQuery.OrderString = gTrapPage.orderBy;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divTrapList').innerHTML = JetsenWeb.Xml.transformXML("xslt/traptable.xslt", ret.resultVal);
		trapGridList.bind($('divTrapList'), $('tabTrapTable'));
		gTrapPage.setRowCount($('hid_TrapCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function newTrap() {
	var mibId = $("curMibType").value;
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	showRemindWordCount($("txt_TRAP_DESC").value,$('remindWorddesca'),"600");
	showRemindWordCount($("txt_DESC_CN").value,$('remindWorddescb'),"600");
	var dialog = new JetsenWeb.UI.Window("new-trap-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 380 }, title : "新建Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var NAME_CN = $("txt_NAME_CN").value,
				DESC_CN = $("txt_DESC_CN").value;
			if(parseInt(getBytesCount($("txt_TRAP_DESC").value))>1200){
            	jetsennet.alert("英文描述不能超过600个文字！");
            	return;
            }
			if(parseInt(getBytesCount($("txt_DESC_CN").value))>1200){
            	jetsennet.alert("中文描述不能超过600个文字！");
            	return;
            }
			
			//中文名称和中文描述没有填写的话,就默认取数据库中已经存在的同一OID数据的值;若填写了就按填写值更新所有该OID数据的对应值.
			if(NAME_CN == '' && DESC_CN == ''){
				//取该oid数据
				var condition = new JetsenWeb.SqlConditionCollection();
				condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_OID",  $("txt_TRAP_OID").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.String));
				var sqlQuery = new JetsenWeb.SqlQuery();
				JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "TRAP_ID", PageInfo : null,
					QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_TRAPTABLE" }) });
				sqlQuery.Conditions = condition;

				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.async = false;
				ws.oncallback = function(ret) {
					try {
						if (ret == null || ret.resultVal == null) {
							return;
						}
						var objs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
						if(objs == null){
							return;
						}
						var obj = objs[0];
						NAME_CN = valueOf(obj, "NAME_CN", "");
						DESC_CN = valueOf(obj, "DESC_CN", "");
					} catch (ex) {
						jetsennet.error(ex);
					}
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
			}else{
				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.oncallback = function(ret) {
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpUpdateMibTrapNodeByOID", [ $("txt_TRAP_OID").value, NAME_CN, DESC_CN ]);
			}
			
			var newObj = { PARENT_ID : -1, MIB_ID : mibId, TRAP_NAME : $("txt_TRAP_NAME").value, TRAP_OID : $("txt_TRAP_OID").value,
				TRAP_DESC : $("txt_TRAP_DESC").value, TRAP_VERSION : "NOTIFICATION-TYPE", NAME_CN : NAME_CN,
				DESC_CN : DESC_CN };
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
	var mibId = $("curMibType").value;
	var areaElements = JetsenWeb.Form.getElements('divTrapNew');
	var NAME_CN = '', DESC_CN = '', NAME_CN_NEW = '', DESC_CN_NEW = '';
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
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
			var objs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if(objs == null){
				return;
			}
			var obj = objs[0];
			$("txt_TRAP_NAME").value = valueOf(obj, "TRAP_NAME", "");
			$("txt_TRAP_OID").value = valueOf(obj, "TRAP_OID", "");
			$("txt_NAME_CN").value = valueOf(obj, "NAME_CN", "");
			NAME_CN = valueOf(obj, "NAME_CN", "");
			$("txt_TRAP_DESC").value = valueOf(obj, "TRAP_DESC", "");
			$("txt_DESC_CN").value = valueOf(obj, "DESC_CN", "");
			DESC_CN = valueOf(obj, "DESC_CN", "");
			
			showRemindWordCount($("txt_TRAP_DESC").value,$('remindWorddesca'),"600");
			showRemindWordCount($("txt_DESC_CN").value,$('remindWorddescb'),"600");
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-trap-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 380 }, title : "编辑Trap" });
	dialog.controls = [ "divTrapNew" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var outObj = { TRAP_ID : keyId, TRAP_NAME : $("txt_TRAP_NAME").value, TRAP_OID : $("txt_TRAP_OID").value,
				TRAP_DESC : $("txt_TRAP_DESC").value, NAME_CN : $("txt_NAME_CN").value, DESC_CN : $("txt_DESC_CN").value };
			if(parseInt(getBytesCount($("txt_TRAP_DESC").value))>1200){
            	jetsennet.alert("英文描述不能超过600个文字！");
            	return;
            }
			if(parseInt(getBytesCount($("txt_DESC_CN").value))>1200){
            	jetsennet.alert("中文描述不能超过600个文字！");
            	return;
            }
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

			NAME_CN_NEW = $("txt_NAME_CN").value;
			DESC_CN_NEW = $("txt_DESC_CN").value;
			//若中文名称或中文描述改变了,则改变所有该OID的数据的相应项.
			if(NAME_CN != NAME_CN_NEW || DESC_CN != DESC_CN_NEW){
				ws.call("bmpUpdateMibTrapNodeByOID", [ $("txt_TRAP_OID").value, NAME_CN_NEW, DESC_CN_NEW ]);
			}
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
			loadTrapList($("curMibType").value);
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
			var NAME_CN = $("txt_NAME_CN").value,
			DESC_CN = $("txt_DESC_CN").value;
			//中文名称和中文描述没有填写的话,就默认取数据库中已经存在的同一OID数据的值;若填写了就按填写值更新所有该OID数据的对应值.
			if(NAME_CN == '' && DESC_CN == ''){
				//取该oid数据
				var condition = new JetsenWeb.SqlConditionCollection();
				condition.SqlConditions.push(JetsenWeb.SqlCondition.create("TRAP_OID",  $("txt_TRAP_OID").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.String));
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
						var objs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
						if(objs == null){
							return;
						}
						var obj = objs[0];
						NAME_CN = valueOf(obj, "NAME_CN", "");
						DESC_CN = valueOf(obj, "DESC_CN", "");
					} catch (ex) {
						jetsennet.error(ex);
					}
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
			}else{
				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.oncallback = function(ret) {
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpUpdateMibTrapNodeByOID", [ $("txt_TRAP_OID").value, NAME_CN, DESC_CN ]);
			}
			
			var newObj = { PARENT_ID : curParentId, MIB_ID : $("curMibType").value, TRAP_NAME : $("txt_TRAP_NAME").value,
				TRAP_OID : $("txt_TRAP_OID").value, TRAP_DESC : $("txt_TRAP_DESC").value, TRAP_VERSION : "", NAME_CN : NAME_CN,
				DESC_CN : DESC_CN };
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
	var NAME_CN = '', DESC_CN = '', NAME_CN_NEW = '', DESC_CN_NEW = '';
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
			var objs = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if(objs == null){
				return;
			}
			var obj = objs[0];
			$("txt_TRAP_NAME").value = valueOf(obj, "TRAP_NAME", "");
			$("txt_TRAP_OID").value = valueOf(obj, "TRAP_OID", "");
			$("txt_NAME_CN").value = valueOf(obj, "NAME_CN", "");
			NAME_CN = valueOf(obj, "NAME_CN", "");
			$("txt_TRAP_DESC").value = valueOf(obj, "TRAP_DESC", "");
			$("txt_DESC_CN").value = valueOf(obj, "DESC_CN", "");
			DESC_CN = valueOf(obj, "DESC_CN", "");
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
			
			NAME_CN_NEW = $("txt_NAME_CN").value;
			DESC_CN_NEW = $("txt_DESC_CN").value;
			//若中文名称或中文描述改变了,则改变所有该OID的数据的相应项.
			if(NAME_CN != NAME_CN_NEW || DESC_CN != DESC_CN_NEW){
				ws.call("bmpUpdateMibTrapNodeByOID", [ $("txt_TRAP_OID").value, NAME_CN_NEW, DESC_CN_NEW ]);
			}
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