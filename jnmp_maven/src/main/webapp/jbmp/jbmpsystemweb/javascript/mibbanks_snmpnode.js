var snmpGridList = new jetsennet.ui.GridList("snmpGridlist");
snmpGridList.columns = [ 
        { index : 0, fieldName : "NODE_NAME", width : 485, name : "名称", align : "left" },
		{ index : 1, fieldName : "NODE_OID", width : 200, name : "OID" },
		{ index : 2, fieldName : "NODE_ID,SOURCE_TYPE", width : 45, align : "center", name : "新建" },
		{ index : 3, fieldName : "NODE_ID,SOURCE_TYPE", width : 45, align : "center", name : "删除"}];
snmpGridList.attachAttributes = [{name : "nodeId", field : "NODE_ID"}];
snmpGridList.parentId = 0;
snmpGridList.idField = "NODE_ID";
snmpGridList.parentField = "PARENT_ID";
snmpGridList.treeControlIndex = 0;
snmpGridList.treeOpenLevel = 6;
snmpGridList.columns[2].format = function (val,vals){
	val = "<a href='javascript:void(0)' onclick=\"addSnmpNode('"+vals[0]+"')\"><img src='images/new.gif' border='0' style='cursor:pointer' title='添加子节点'/></a>"
	return val;
}
snmpGridList.columns[3].format = function (val,vals){
	if(vals[1] == 1) {
		val = "<a href='javascript:void(0)' onclick=\"deleteSnmpNode('"+vals[0]+"')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
	} else {
		val = "&nbsp;";
	}
	return val;
}
snmpGridList.onrowclick = function(pel, el) {
	$("txt_NODE_EXPLAIN").disabled = false;
	$("btnSnmpNode").disabled = false;
	var nodeId = parseInt(pel.getAttribute("nodeId"));
	if(nodeId > 0) {
		$("curNodeId").value = nodeId;
		getNodeInfo(nodeId);
	} else {
		$("txt_NODE_DESC").value = "";
		$("txt_NODE_EXPLAIN").value = "";
	}
}
/**
 * 渲染树状表
 * @return
 */
function renderOidTree(snmpData) {

	$('divSnmpTree').innerHTML = "";
	snmpGridList.dataSource = snmpData;
	snmpGridList.render("divSnmpTree");
}
function mibDetails(mibId, mibAlias) {
	$("curMibType").value = mibId;
	$("curNodeId").value = -1;
	var dialog = new JetsenWeb.UI.Window("mib-detail-win");
	JetsenWeb.extend(dialog, { submitBox : false, 
		cancelBox : true, 
		windowStyle : 1, 
		cancelButtonText : "关闭", 
		maximizeBox : false,
		minimizeBox : true, 
		size : { width : 800, height : 500 }, 
		title : "MIB详细信息" });
	dialog.controls = [ "divSnmp" ];
	dialog.attachButtons = [ 
    //{ text : "添加", clickEvent : function() { addSnmpNode(); } }, 
	//{ text : "删除", clickEvent : function() { deleteSnmpNode(); } }, 
//	{ text : "修改", clickEvent : function() { editSnmpNode(); } } 
	];
	$("txt_NODE_NAME").value = "";
	$("txt_NODE_OID").value = "";
	$("txt_MIB_FILE").value = "";
	$("txt_NODE_DESC").value = "";
	$("txt_NODE_EXPLAIN").value = "";
	$("txt_NODE_EXPLAIN").disabled = true;
	$("btnSnmpNode").disabled = true;
	dialog.showDialog();

	refreshSnmpTree();
}
function refreshSnmpTree() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NODE_ID", PageInfo : null,
		ResultFields : "NODE_ID,PARENT_ID,NODE_NAME,NODE_OID,SOURCE_TYPE",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", $("curMibType").value, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
	//condition.SqlConditions.push(JetsenWeb.SqlCondition.create("SOURCE_TYPE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = true;
	ws.oncallback = function(ret) {
		if(ret != null && ret.resultVal != null) {
			renderOidTree(ret.resultVal);
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
	ws.async = false;
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
function addSnmpNode(nodeId) {
	if (nodeId <= 0) {
		return;
	}
	
	var node = null;
	
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "NODE_ID", PageInfo : null, ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("NODE_ID", nodeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		if (retObj != null) {
			node = retObj;
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	
	if(node == null) {
		jetsennet.alert("找不到节点" + nodeId + "对应的记录！");
		return ;
	}
	
	var area = JetsenWeb.Form.getElements("divPopSnmp");
	JetsenWeb.Form.resetValue(area);
	JetsenWeb.Form.clearValidateState(area);
	$("txt_NODE_OID1").value = node["NODE_OID"]

	var dialog = new JetsenWeb.UI.Window("add-snmp-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 300 }, title : "新建SNMP节点" });
    showRemindWordCount($("txt_NODE_EXPLAIN1").value,$('remindWord'),"150");
	dialog.controls = [ "divPopSnmp" ];
	dialog.onsubmit = function() {
		try {
			var areaElements = JetsenWeb.Form.getElements("divPopSnmp");
			if (JetsenWeb.Form.Validate(areaElements, true)) {
				var objElement = { 
						PARENT_ID : nodeId, 
						MIB_ID : $("curMibType").value, 
						NODE_NAME : $("txt_NODE_NAME1").value,
						NODE_OID : $("txt_NODE_OID1").value, 
						NODE_TYPE : getSelectedValue($("sel_NODE_TYPE1")), 
						NODE_INDEX : $("txt_NODE_INDEX1").value,
						VALUE_TYPE : 0, 
						MIB_FILE : "", 
						SOURCE_TYPE : 1, 
						NODE_EXPLAIN : $("txt_NODE_EXPLAIN1").value 
				};
				if(parseInt(getBytesCount($("txt_NODE_EXPLAIN1").value))>300){
	            	jetsennet.alert("分类描述不能超过150个文字！");
	            	return;
	            }

				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.oncallback = function(ret) {
					refreshSnmpTree();
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
function deleteSnmpNode(nodeId) {
	if (nodeId <= 0) {
		return ;
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { 
		IsPageResult : 0, 
		KeyId : "NODE_ID", 
		PageInfo : null, 
		ResultFields : "NODE_ID",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_SNMPNODES" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", nodeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var isLeaf = true;
	var isWrong = false;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (retObj != null && retObj.length > 0) {
			isLeaf = false;
		}
	};
	ws.onerror = function(ex) {
		alert(ex);
		isWrong = true;
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	
	if(isWrong) {
		return;
	}
	
	jetsennet.confirm("确定删除？", function () {
		if(!isLeaf) {
			jetsennet.confirm("删除该节点，将自动删除关联的子节点，不可恢复，确认？", function () {
				var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
				ws.soapheader = JetsenWeb.Application.authenticationHeader;
				ws.oncallback = function(ret) {
					refreshSnmpTree();
				};
				ws.onerror = function(ex) {
					jetsennet.error(ex);
				};
				ws.call("bmpObjDelete", [ "BMP_SNMPNODES", nodeId ]);
				return true;
			});
		} else {
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				refreshSnmpTree();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjDelete", [ "BMP_SNMPNODES", nodeId ]);
			return true;
		}
	});
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