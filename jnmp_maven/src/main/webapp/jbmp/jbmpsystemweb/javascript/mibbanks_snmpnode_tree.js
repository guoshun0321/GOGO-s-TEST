// SNMP节点================================================================================
function mibDetails(mibId, mibAlias) {
	$("curMibType").value = mibId;
	var dialog = new JetsenWeb.UI.Window("mib-detail-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : true, windowStyle : 1, cancelButtonText : "关闭", maximizeBox : false,
		minimizeBox : false, size : { width : 800, height : 450 }, title : "MIB库" });
	dialog.controls = [ "divSnmp" ];
	dialog.attachButtons = [ { text : "添加", clickEvent : function() {
		addSnmpNode();
	} }, { text : "删除", clickEvent : function() {
		deleteSnmpNode();
	} }, { text : "修改", clickEvent : function() {
		editSnmpNode();
	} } ];
	$("txt_NODE_NAME").value = "";
	$("txt_NODE_OID").value = "";
	$("txt_MIB_FILE").value = "";
	$("txt_NODE_DESC").value = "";
	$("txt_NODE_EXPLAIN").value = "";
	dialog.showDialog();

	genSnmpTree();
	$("divSnmpTree").innerHTML = gTree;
	gTree.expandAll();
}
var gTree;
var list;
function genSnmpTree() {
	list = new Array();
	$("curNodeId").value = -3;
	gTree = new JetsenWeb.UI.Tree("所有分类", "javascript:clickNode(-3)");
	gTree.showTop = false;
	gTree.setBehavior("classic");

	gAutoBranch = new JetsenWeb.UI.TreeItem("自动添加的节点", "javascript:clickNode(-1, 0, 0, 0)");
	gManuBranch = new JetsenWeb.UI.TreeItem("手动添加的节点", "javascript:clickNode(-2, 0, 1, 0)");
	gAutoBranch.NODE_OID = "";
	gManuBranch.NODE_OID = "";
	list.push( { key : "-1", value : gAutoBranch });
	list.push( { key : "-2", value : gManuBranch });
	gTree.add(gAutoBranch);
	gTree.add(gManuBranch);
	fillManuTree(gManuBranch);
	gManuBranch.expandAll();
}
function fillManuTree(manuNode) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NODE_ID", PageInfo : null,
		ResultFields : "NODE_ID,PARENT_ID,NODE_NAME,NODE_OID,SOURCE_TYPE,NODE_TYPE",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", $("curMibType").value, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SOURCE_TYPE", 1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var nodes = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		var treeNodes = new Array();
		if (nodes != null) {
			for (i = 0; i < nodes.length; i++) {
				var tNode = nodes[i];
				var tempId = nodes[i]["NODE_ID"];
				var branch = new JetsenWeb.UI.TreeItem(nodes[i]["NODE_NAME"], "javascript:clickNode(" + tempId + ", " + tempId + ", "
						+ nodes[i]["SOURCE_TYPE"] + ", " + nodes[i]["NODE_TYPE"] + ")");
				branch.NODE_OID = nodes[i]["NODE_OID"];

				if (tNode["PARENT_ID"] == 0) {
					manuNode.add(branch);
					list.push( { key : tempId, value : branch });
				} else {
					for (j = 0; j < list.length; j++) {
						if (list[j].key == tNode["PARENT_ID"]) {
							list[j].value.add(branch);
							list.push( { key : tempId, value : branch });
							break;
						}
					}
				}
			}
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
// id 树节点编号
// nodeId 数据库编号
// sourceType 来源
// nodeType 节点类型
function clickNode(id, nodeId, sourceType, nodeType) {
	var node = null;
	$("curNodeId").value = id;
	$("curNodeType").value = sourceType;
	for (i = 0; i < list.length; i++) {
		if (list[i].key == id) {
			node = list[i].value;
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
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
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
				list.push( { key : tempId, value : branch });
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
function addSnmpNode() {
	var id = $("curNodeId").value;
	id = (id == -3 ? -2 : id);
	var nodeId = (id <= 0 ? 0 : id);
	var node = null;
	for (i = 0; i < list.length; i++) {
		if (list[i].key == id) {
			node = list[i].value;
			break;
		}
	}
	if (node == null) {
		return;
	}
	var area = JetsenWeb.Form.getElements("divPopSnmp");
	JetsenWeb.Form.resetValue(area);
	JetsenWeb.Form.clearValidateState(area);
	$("txt_NODE_OID1").value = node["NODE_OID"]

	var dialog = new JetsenWeb.UI.Window("add-snmp-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 300 }, title : "新建SNMP节点" });
	dialog.controls = [ "divPopSnmp" ];
	dialog.onsubmit = function() {
		try {
			var areaElements = JetsenWeb.Form.getElements("divPopSnmp");
			if (JetsenWeb.Form.Validate(areaElements, true)) {
				var objElement = { PARENT_ID : nodeId, MIB_ID : $("curMibType").value, NODE_NAME : $("txt_NODE_NAME1").value,
					NODE_OID : $("txt_NODE_OID1").value, NODE_TYPE : getSelectedValue($("sel_NODE_TYPE1")), NODE_INDEX : $("txt_NODE_INDEX1").value,
					VALUE_TYPE : 0, MIB_FILE : "", SOURCE_TYPE : 1, NODE_EXPLAIN : $("txt_NODE_EXPLAIN1").value }
				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.oncallback = function(ret) {
					genSnmpTree();
					$("divSnmpTree").innerHTML = gTree;
					gTree.expandAll();
					JetsenWeb.UI.Windows.close("add-snmp-win");
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpObjInsert", [ "BMP_SNMPNODES", JetsenWeb.Xml.serializer(objElement, "BMP_SNMPNODES") ]);
			}
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	dialog.showDialog();
}
function deleteSnmpNode() {
	var id = $("curNodeId").value;
	if (id <= 0) {
		return;
	}
	var source = $("curNodeType").value;
	if (source == 0) {
		jetsennet.alert("不能删除自动添加的节点！");
		return;
	}
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		clearSnmpNode();
		genSnmpTree();
		$("divSnmpTree").innerHTML = gTree;
		gTree.expandAll();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_SNMPNODES", id ]);
}
function editSnmpNode() {
	var id = $("curNodeId").value;
	if (id <= 0) {
		return;
	}
	var oid = $("txt_NODE_OID").value;
	var value = $("txt_NODE_EXPLAIN").value;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpUpdateNodeByOID", [ oid, value ]);
}