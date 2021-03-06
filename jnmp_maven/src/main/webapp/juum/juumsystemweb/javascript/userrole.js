//获取角色列表
function getRoleList($) {
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ID", PageInfo : null, ResultFields : "", OrderString : "order by id desc",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_ROLE" }) });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(rolesResult) {
		el("divContainer").innerHTML = JetsenWeb.Xml.transformXML("xslt/userrole.xslt", rolesResult.resultVal);
		JetsenWeb.require("js_gridlist");
		var o = new JetsenWeb.UI.GridList();
		var rc = null;
		rc = o.bind(document.getElementById('divContainer'), document.getElementById('tabUserRole'));
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}

//获取角色信息=================================================================
function getRole(id) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ID", PageInfo : null, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_ROLE" }) });

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(rolesResult) {
		var roleXmlDoc = new JetsenWeb.XmlDoc();
		roleXmlDoc.async = false;
		roleXmlDoc.loadXML(rolesResult.resultVal);

		var roleNode = roleXmlDoc.documentElement.selectSingleNode("Record");
		if (roleNode.selectSingleNode("NAME") != null) {
			el("txtRoleName").value = JetsenWeb.Xml.getText(roleNode.selectSingleNode("NAME"));
		}
		if (roleNode.selectSingleNode("DESCRIPTION") != null) {
			el("txtDescription").value = JetsenWeb.Xml.getText(roleNode.selectSingleNode("DESCRIPTION"));
		}
		showRemindWordCount($("txtDescription").value, $('remindWord'), "60");
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}

//获取角色的用户信息===========================================================
function getRoleUser(roleId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ID", PageInfo : null, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_USER" }) });

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_USER.ROLE_ID", roleId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Custom, JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(rolesResult) {
		var roleNodes = JetsenWeb.Xml.toObject(rolesResult.resultVal, "Record");
		if (roleNodes == null) {
			return;
		}
		for ( var i = 0; i < roleNodes.length; i++) {
			var objNewOption = document.createElement("option");
			el("selMember").options.add(objNewOption);
			objNewOption.value = roleNodes[i].ID;
			objNewOption.innerHTML = roleNodes[i].USER_NAME;
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}

//保存角色=====================================================================
function SaveRole(roleId) {
	if (JetsenWeb.Util.trim(el("txtRoleName").value) == "" || el("txtRoleName").value.length > 20) {
		jetsennet.alert("角色名称不可以为空，且长度应不大于20！");
		return;
	}
	//	if(el("txtDescription").value.length>100)
	//	{
	//		jetsennet.alert("描述内容长度应不大于100！");
	//		return;
	//	}
	//	
	if (parseInt(getBytesCount($("txtDescription").value)) > 120) {
		jetsennet.alert("描述信息不能超过60个文字！");
		return;
	}
	var userIds = "";
	var len = el("selMember").options.length;
	for ( var i = 0; i < len; i++) {
		if (userIds != "")
			userIds += ",";
		userIds += el("selMember").options[i].value;
	}

	var checkIds = [];
	gTree.getItem( function(item) {
		if (item.checked == true) {
			checkIds.push(item.checkValue);
		}
	}, true);

	var checkTopoIds = [];
	gTopoTree.getItem( function(item) {
		if (item.checked == true) {
			checkTopoIds.push(nowTopoData[item.checkValue]);
		}
	}, true);
	
	var checkObjGroupIds = getSelSysGroups();

	var roleXml = JetsenWeb.Xml.serialize( { ID : (roleId ? roleId : 0), NAME : $("txtRoleName").value, DESCRIPTION : $("txtDescription").value,
		TYPE : 0, ROLE_USER : userIds, ROLE_FUNCTION : checkIds.join(","), ROLE_TOPO : checkTopoIds.join(",") }, "UUM_ROLE");

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.oncallback = function(sResult) {
		//组权限
		var wsObjGroup = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
		wsObjGroup.soapheader = JetsenWeb.Application.authenticationHeader;
		wsObjGroup.oncallback = function(ret) {
		}
		wsObjGroup.onerror = function(ex) {
			jetsennet.error(ex);
		};
		if (roleId == null || roleId == "") {
			wsObjGroup.call("RoleGroupInsert", [ sResult.resultVal, checkObjGroupIds.join(',') ]);
		} else {
			wsObjGroup.call("RoleGroupUpdate", [ roleId, checkObjGroupIds.join(',') ]);
		}

		JetsenWeb.UI.Windows.close("edit-role");
		getRoleList();
	}

	if (roleId == null || roleId == "") {
		ws.call("uumObjInsert", [ "UUM_ROLE", roleXml ]);
	} else {
		ws.call("uumObjUpdate", [ "UUM_ROLE", roleXml ]);
	}
}
//获取角色权限
function getRoleFunction(roleId) {
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ROLE_ID", roleId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "FUNCTION_ID,ROLE_ID", PageInfo : null, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_ROLEAUTHORITY" }) });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		var roleDoc = new JetsenWeb.XmlDoc();
		roleDoc.loadXML(sResult.resultVal);
		var rootNode = roleDoc.documentElement;

		gTree.getItem( function(item) {

			if (rootNode.selectSingleNode("Record[FUNCTION_ID=" + item.checkValue + "]") != null) {
				item.setCheck(true);
			}
		}, true);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}

//获取角色拓扑权限
function getRoleTopo(roleId) {
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ROLE_ID", roleId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "MAP_ID,ROLE_ID", PageInfo : null, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ROLETOPOAUTHORITY" }) });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		var roleDoc = new JetsenWeb.XmlDoc();
		roleDoc.loadXML(sResult.resultVal);
		var rootNode = roleDoc.documentElement;

		gTopoTree.getItem( function(item) {

			if (rootNode.selectSingleNode("Record[MAP_ID=" + nowTopoData[item.checkValue] + "]") != null) {
				item.setCheck(true);
			}
		}, true);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

//删除角色=====================================================================
function deleteRole(roleId) {
	jetsennet.confirm("确定删除？", function() {
		if (roleId == 1) {
			jetsennet.alert("系统不允许删除系统管理员角色！");
			return false;
		}
		jetsennet.confirm("删除角色,将自动删除关联信息,不可恢复,确认吗？", function() {
			var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.oncallback = function(sResult) {
				//删除角色对象组权限数据
				var wsObjGroup = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
				wsObjGroup.soapheader = JetsenWeb.Application.authenticationHeader;
				wsObjGroup.oncallback = function(ret) {
				}
				wsObjGroup.onerror = function(ex) {
					jetsennet.error(ex);
				};
				wsObjGroup.call("RoleGroupDelete", [ roleId ]);

				getRoleList();
			}
			ws.call("uumObjDelete", [ "UUM_ROLE", roleId ]);
			return true;
		});
	});
}
//删除选项
function selectOptionsDel(selCtrl) {
	var itemCount = selCtrl.options.length;
	var selectedItemCount = 0;
	if (itemCount > 0) {
		for ( var i = itemCount - 1; i >= 0; i--) {
			if (selCtrl.options[i].selected) {
				selCtrl.removeChild(selCtrl.options[i]);
				selectedItemCount++;
			}
		}
	}
	if (selectedItemCount == 0) {
		jetsennet.alert("请选择要删除的项！");
	}
}

//得到字符串字节数
function getBytesCount(str) {
	var bytesCount = 0;
	if (str != null) {
		for ( var i = 0; i < str.length; i++) {
			var c = str.charAt(i);
			if (/^[\u0000-\u00ff]$/.test(c)) {
				bytesCount += 1;
			} else {
				bytesCount += 2;
			}
		}
	}
	return bytesCount;
}

//textarea 作文字控制
function showRemindWordCount(textValue, remindWordHtml, wordCount) {
	var countNum = 2 * parseInt(wordCount);
	remindWordHtml.innerHTML = parseInt((countNum - parseInt(getBytesCount(textValue))) / 2);
	if (countNum < parseInt(getBytesCount(textValue))) {
		remindWordHtml.style.color = "red";
		remindWordHtml.innerHTML = 0;
	} else {
		remindWordHtml.style.color = "black";
	}
}

function editTopo2RF(roleId) {
	createTopo2RFTree();
	showTopoByF();
	getMyTopo(roleId);
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("edit-topo2rf"), { title : "房间拓扑", submitBox : true, cancelBox : true,
		size : { width : 800, height : 550 }, maximizeBox : false, minimizeBox : false });
	dialog.controls = [ "divShowTopo2RF" ];
	dialog.onsubmit = function() {
		var mapIds = [];
		var myTopoChilds = document.getElementById('myTopo').getElementsByTagName('div');

		if (myTopoChilds.length > 0) {
			for ( var i = 0; i < myTopoChilds.length; i++) {
				mapIds.push(myTopoChilds[i].getAttribute('mapid'));
			}
		}

		var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
		}
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("addRFTopo2Role", [ roleId, mapIds.join(',') ]);

		dialog.close();
	};
	dialog.showDialog();
	document.getElementById("edit-topo2rf_resize").style.display = "none";
}

//创建房屋树
function createTopo2RFTree() {
	if (gTopo2RFTree != null) {
		return;
	}

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		gTopo2RFTree = JetsenWeb.UI.Tree.createTree("obj-topo2rf-tree", ret.value, { parentId : "", parentField : "PARENT_ID",
			itemName : "DataTable", textField : "NODE_NAME", valueField : "NODE_ID", isOpen : true });
		gTopo2RFTree.getItem( function(item) {
			if (/^\d+$/.test(item.checkValue))
				item.onclick = showTopoByR;
			else
				item.onclick = showTopoByF;
		}, true);
		$("divTopo2RF").appendChild(gTopo2RFTree.render());
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("getTopo2RFTreeXml");
}

var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gRoomPage.setOrderBy(sortfield, desc);
};
function showTopoByR() {
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.RF_ID", this.checkValue, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.RF_TYPE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_TOPOMAP", "A");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("NMP_TOPO2RF", "B", "A.MAP_ID=B.MAP_ID", JetsenWeb.TableJoinType.Inner));

	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "A.MAP_ID", PageInfo : null, ResultFields : "A.MAP_ID, A.MAP_NAME",
		QueryTable : gQueryTable });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divTopo2RFList").innerHTML = JetsenWeb.Xml.transformXML("xslt/topo2rf.xslt", ret.resultVal);
		gGridList.bind($("divTopo2RFList"), $("tabUserTopo2RF"));
	}
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

function showTopoByF() {
	$("divTopo2RFList").innerHTML = JetsenWeb.Xml.transformXML("xslt/topo2rf.xslt",
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><RecordSet></RecordSet>");
	gGridList.bind($("divTopo2RFList"), $("tabUserTopo2RF"));
}

function toMyTopo() {
	var check = document.getElementsByName('chkAllObjectChild');
	var check = document.getElementById('tabUserTopo2RF').getElementsByTagName('input');
	var myTopoDiv = document.getElementById('myTopo');
	var myTopoChilds = document.getElementById('myTopo').getElementsByTagName('div');
	var flag = false;

	for ( var i = 0; i < check.length; i++) {
		if (check[i].checked) {
			flag = true;
			for ( var j = 0; j < myTopoChilds.length; j++) {
				if (myTopoChilds[j].getAttribute('mapid') == check[i].value) {
					flag = false;
					break;
				}
			}
			if (flag)
				myTopoDiv.innerHTML += "<div name='myTopoChild' style='height:25px;padding:5px;margin:2px;border:1px solid #1E90FF;float:left;background: #87CEFA;' mapid='"
						+ check[i].value
						+ "'>"
						+ check[i].getAttribute("MapName")
						+ "<img border='0' src='images/drop.gif' onclick='this.parentElement.outerHTML = \"\"' style='margin-left:5px;margin-top:5px;cursor: pointer;' title='删除' /></div>";
		}
	}
}

function getMyTopo(roleId) {
	var myTopoDiv = document.getElementById('myTopo');
	myTopoDiv.innerHTML = "";

	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.ROLE_ID", roleId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_TOPOMAP", "A");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("NMP_RFTOPO2ROLE", "B", "A.MAP_ID=B.MAP_ID", JetsenWeb.TableJoinType.Inner));

	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "A.MAP_ID", PageInfo : null, ResultFields : "A.MAP_ID, A.MAP_NAME",
		QueryTable : gQueryTable });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var result = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (result != null && result.length > 0) {
			for ( var i = 0; i < result.length; i++) {
				myTopoDiv.innerHTML += "<div name='myTopoChild' style='height:25px;padding:5px;margin:2px;border:1px solid #1E90FF;float:left;background: #87CEFA;' mapid='"
						+ result[i].MAP_ID
						+ "'>"
						+ result[i].MAP_NAME
						+ "<img border='0' src='images/drop.gif' onclick='this.parentElement.outerHTML = \"\"' style='margin-left:5px;margin-top:5px;cursor: pointer;' title='删除' /></div>";
			}
		}
	}
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_jetsentree", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gPageSelectUser = new JetsenWeb.UI.PageBar("pageSelectUser");
gPageSelectUser.orderBy = "ORDER BY ID";
gPageSelectUser.onpagechange = function() {
	getSelectUserData();
}
gPageSelectUser.onupdate = function() {
	$('divSelectUserPage').innerHTML = this.generatePageControl();
}

var myGridList = new JetsenWeb.UI.GridList();
myGridList.ondatasort = function(sortfield, desc) {
	gPageSelectUser.setOrderBy(sortfield, desc);
}

var gTree = null; //功能树
var gTopoTree = null; //拓扑树
var gTopo2RFTree = null; //房屋树

function searchSelectUserData() {
	gPageSelectUser.currentPage = 1;
	getSelectUserData();
}
getSelectUserData = function() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, KeyId : "ID", PageInfo : gPageSelectUser, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_USER" }) });

	sqlQuery.OrderString = gPageSelectUser.orderBy;

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID", getAllValues($("selMember")), JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));
	if ($("txtLoginName").value != "") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("LOGIN_NAME", $("txtLoginName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if ($("txtUserName").value != "") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("USER_NAME", $("txtUserName").value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divSelectUserList').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectuser.xslt", ret.resultVal);
		//var o = new JetsenWeb.UI.GridList();
		myGridList.bind($('divSelectUserList'), $('tabSelectUser'));
		gPageSelectUser.setRowCount($('hid_SUTotalCount').value);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}
function selectUser() {
	searchSelectUserData();
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-user"), { title : "选择用户", submitBox : true, cancelBox : true,
		size : { width : 500, height : 331 }, maximizeBox : true, minimizeBox : true });
	dialog.controls = [ "divSelectUser" ];
	dialog.onsubmit = function() {
		var obj = document.getElementsByName("chk_SelectUser");
		for ( var i = 0; i < obj.length; i++) {
			if (obj[i].checked)
				AddUserItem(obj[i].value, obj[i].getAttribute("itemName"));
		}
		return true;
	};
	dialog.showDialog();
	$('txtLoginName').value = "";
	$('txtUserName').value = "";
	$('divSelectUserList').innerHTML = "";
	$('divSelectUserPage').innerHTML = "";
	//getSelectUserData();
}
function createTree() {
	if (gTree != null)
		return;

	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("STATE", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ID", PageInfo : null, ResultFields : "", OrderString : "Order By PARENT_ID,VIEW_POS",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "UUM_FUNCTION" }) });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
	ws.async = false;
	ws.cacheLevel = 2;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		gTree = JetsenWeb.UI.Tree.createTree("role-tree", ret.resultVal, { parentId : "0", parentField : "PARENT_ID", itemName : "Record",
			textField : "NAME", valueField : "ID", showCheck : true });
		$("divFunction").appendChild(gTree.render());
	}
	ws.call("uumObjQuery", [ sqlQuery.toXml() ]);
}

var nowTopoData = {};
//创建拓扑图树
function createTopoTree() {
	if (gTopoTree != null) {
		return;
	}

	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.USE_TYPE", "2", JetsenWeb.SqlLogicType.Or,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.USE_TYPE", "", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("A.MAP_STATE", "0", JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_TOPOMAP", "A");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "B", "A.GROUP_ID=B.GROUP_ID", JetsenWeb.TableJoinType.Left));

	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "MAP_ID", PageInfo : null, ResultFields : "A.*,B.PARENT_ID",
		OrderString : "ORDER BY A.MAP_ID", QueryTable : gQueryTable });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.cacheLevel = 2;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		//将GROUP_ID和MAP_ID的关系保存起来，树里的值是GROUP_ID，存的时候需要MAP_ID，到时候转换。
		var result = JetsenWeb.xml.toObject(ret.resultVal, "Record");
		var json = "{";
		for ( var i = 0; i < result.length; i++) {
			json += "\"" + result[i].GROUP_ID + "\":\"" + result[i].MAP_ID + "\",";
		}
		if (json.indexOf(',') > -1) {
			json = json.substring(0, json.length - 1);
		}
		json += "}";
		nowTopoData = eval("(" + json + ")");

		gTopoTree = JetsenWeb.UI.Tree.createTree("topo-tree", ret.resultVal, { parentId : "", parentField : "PARENT_ID", itemName : "Record",
			textField : "MAP_NAME", valueField : "GROUP_ID", showCheck : true });
		gTopoTree.getItem( function(item) {
			item.clickChange = true;
		}, true);
		$("divTopo").appendChild(gTopoTree.render());
	}
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

/**
 * 新增角色
 * 
 * @return
 */
function addUserRole() {
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("edit-role"), { title : "新建角色", submitBox : true, cancelBox : true,
		size : { width : 400, height : 450 }, maximizeBox : false, minimizeBox : false });
	dialog.controls = [ "divRole" ];
	dialog.onsubmit = function() {
		SaveRole(null);
	};
	dialog.showDialog();
	$('selMember').options.length = 0;
	$('txtDescription').value = "";
	$('txtRoleName').value = "";
	showRemindWordCount($("txtDescription").value, $('remindWord'), "60");
	createTree();
	createTopoTree();
	createObjGroupTree(-1);

	gTree.getItem( function(item) {
		item.setCheck(false);
	}, false);
	gTopoTree.getItem( function(item) {
		item.setCheck(false);
	}, false);
}
function editUserRole(Id) {
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("edit-role"), { title : "编辑角色", submitBox : true, cancelBox : true,
		size : { width : 400, height : 450 }, maximizeBox : false, minimizeBox : false });
	dialog.controls = [ "divRole" ];
	dialog.onsubmit = function() {
		SaveRole(Id);
	};
	$('selMember').options.length = 0;
	getRole(Id);
	getRoleUser(Id);

	dialog.showDialog();
	createTree();
	createTopoTree();
	createObjGroupTree(Id);

	gTree.getItem( function(item) {
		item.setCheck(false);
	}, false);
	gTopoTree.getItem( function(item) {
		item.setCheck(false);
	}, true);

	getRoleFunction(Id);
	getRoleTopo(Id);
}
//添加用户成员
function AddUserItem(userID, userName) {
	var len = $("selMember").options.length;
	for ( var i = 0; i < len; i++) {
		if ($("selMember").options[i].value == userID)
			return;
	}
	var objNewOption = document.createElement("option");
	$("selMember").options.add(objNewOption);
	objNewOption.value = userID;
	objNewOption.innerHTML = userName;
}
//初始化===============================================================
function pageInit() {
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, showSplit : false });
	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divListTitle"), { size : { width : 0, height : 27 } }));
	gFrame.addControl(new JetsenWeb.UI.PageItem("divContainer"));

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	getRoleList();
	var tempTabPane = new JetsenWeb.UI.TabPane($('tabPane'), $('tabPage'));
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}