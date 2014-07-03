JetsenWeb.require( [ "js_sql", "js_gridlist", "js_pagebar", "js_window", "js_validate", "js_tabpane", "js_pageframe", "js_xtree", "js_autocomplete" ]);
var gFrame;
var AC_IMG_PATH = "./images/acIcon/";
var gWindowSizeChangedInterVal;
var gGridList = new JetsenWeb.UI.GridList("objgroup-grid");
var gCurSelectGrp = { GROUP_ID : "", GROUP_NAME : "", GROUP_TYPE : "101" };
var gIpBegin;
var gIpEnd;
var str_select = "请选择~-1";

var gPage=new JetsenWeb.UI.PageBar();
gPage.onpagechange=function(){loadGroupByParent('','',gCurSelectGrp["GROUP_TYPE"]);}
gPage.orderBy="ORDER BY GROUP_TYPE";
gPage.onupdate=function(){
	$("divContainer").innerHTML=this.generatePageControl();
}
gGridList.ondatasort = function(sortfield, desc) {
	gPage.setOrderBy(sortfield, desc);
};

// 加载
function loadObjGroup() {
	$('divTree').innerHTML = "";
	gGroupTree = new JetsenWeb.UI.Tree("所有分组", "javascript:loadGroupByParent('','','101')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
	gGroupTree.showTop = true;
	gGroupTree.setBehavior("classic");
	var sysTree = new JetsenWeb.UI.TreeItem("系统", "javascript:loadGroupByParent('','','1')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
	var netTree = new JetsenWeb.UI.TreeItem("网段", "javascript:loadGroupByParent('','','4')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
	var collTree = new JetsenWeb.UI.TreeItem("采集组", "javascript:loadGroupByParent('','','3')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
	var commTree = new JetsenWeb.UI.TreeItem("一般组", "javascript:loadGroupByParent('','','0')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
	gGroupTree.add(sysTree);
	gGroupTree.add(collTree);
	gGroupTree.add(netTree);
	gGroupTree.add(commTree);

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT * FROM BMP_GROUP2GROUP WHERE USE_TYPE=0)", "a", "a.GROUP_ID=aa.GROUP_ID",
			JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [ JetsenWeb.SqlCondition.create("aa.GROUP_TYPE", "4", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual,
			JetsenWeb.SqlParamType.Numeric) ];
	sqlQuery.Conditions = condition;
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.GROUP_ID", ResultFields : "aa.GROUP_ID,aa.GROUP_NAME,a.PARENT_ID,aa.GROUP_TYPE",
		PageInfo : null, QueryTable : queryTable });

//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$("divTree").innerHTML = gGroupTree;
		gGroupTree.expandAll();
		loadGroupByParent(gCurSelectGrp.GROUP_ID, gCurSelectGrp.GROUP_NAME, gCurSelectGrp.GROUP_TYPE);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'aa.GROUP_ID', '1']);
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function loadGroupByParent(groupId, groupName, groupType) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "aa");
	var condition = new JetsenWeb.SqlConditionCollection();
	if (groupType == "101") {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("aa.GROUP_TYPE", "1,4,3,0", JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	} else {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("aa.GROUP_TYPE", groupType, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if (groupId == "") {
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "a", "a.GROUP_ID=aa.GROUP_ID", JetsenWeb.TableJoinType.Left));
	} else {
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "a", "a.GROUP_ID=aa.GROUP_ID", JetsenWeb.TableJoinType.Right));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
	}
	sqlQuery.OrderString = gPage.orderBy;
	sqlQuery.Conditions = condition;
	JetsenWeb.extend(sqlQuery,
			{ IsPageResult : 0, KeyId : "aa.GROUP_ID",
				ResultFields : "aa.GROUP_ID,aa.GROUP_NAME,aa.GROUP_TYPE,aa.GROUP_CODE,aa.GROUP_DESC,aa.CREATE_TIME,a.PARENT_ID", PageInfo : gPage,
				QueryTable : queryTable });
	
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		var userId = jetsennet.Application.userInfo.UserId;
		var fagAdmin = isadmin(userId);
    	if(fagAdmin == "false"){
//		if(JetsenWeb.getUserInfo().UserId != 1){
			var changeret = changeParentid(sResult)
			renderGrid(changeret);
		}else{
			renderGrid(sResult.resultVal);
		}
		gCurSelectGrp = { GROUP_ID : groupId, GROUP_NAME : groupName, GROUP_TYPE : groupType };
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'aa.GROUP_ID', '1']);
}

/*
 *显示表格
 */
function renderGrid(xml){
	$("divContainer").innerHTML = "";

	gGridList.columns = [
	 { index: 0, fieldName: "GROUP_TYPE", sortField:"GROUP_TYPE", align: "center", name: "类型"},
	 { index: 1, fieldName: "GROUP_NAME", width: 170, name: "名称"},
     { index: 2, fieldName: "GROUP_CODE", name: "编码"},
     { index: 3, fieldName: "GROUP_DESC", width: 384, name: "描述" },
     { index: 4, fieldName: "CREATE_TIME", width: 140, align: "center", name: "创建时间"},
     { index: 5, fieldName: "GROUP_ID", width: 45, align: "center", name: "编辑" },
     { index: 6, fieldName: "GROUP_ID", width: 45, align: "center", name: "删除"}];

	gGridList.columns[0].format = function (val,vals){
		if(val == 1)
		{
			return "系统";
		}
		else if(val == 2)
		{
			return "设备组";
		}
		else if(val == 3)
		{
			return "采集组";
		}
		else if(val == 4)
		{
			return "网段";
		}
		else 
		{
			return "一般组";
		}
	}
	gGridList.columns[5].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"editObjGroup('"+vals[0]+"')\"><img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>"
		return val;
	}
	gGridList.columns[6].format = function (val,vals){
		val = "<a href='javascript:void(0)' onclick=\"deleteObjGroup('"+vals[0]+"')\"><img src='images/drop.gif' border='0' style='cursor:pointer' title='删除'/></a>"
		return val;
	}
	gGridList.columns[4].format = function (val,vals){return val.substring(0,20) }
	gGridList.parentId = "";
	gGridList.idField = "GROUP_ID";
	gGridList.parentField = "PARENT_ID";
	gGridList.treeControlIndex = 1;
    gGridList.treeOpenLevel = 1;
	gGridList.dataSource = xml;
	gGridList.render("divContainer");
	gGridList.colorSelectedRows();
}

function loadParentGroupByType(grpType, parentId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "a", "aa.GROUP_ID=a.GROUP_ID", JetsenWeb.TableJoinType.Left));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions = [ JetsenWeb.SqlCondition.create("GROUP_TYPE", grpType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric) ];
	sqlQuery.Conditions = condition;
	JetsenWeb.extend(sqlQuery,
			{ IsPageResult : 0, KeyId : "GROUP_ID", ResultFields : "aa.GROUP_ID,GROUP_NAME,PARENT_ID", PageInfo : null, QueryTable : queryTable });

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(sResult) {
		$("divParentGroupTree").innerHTML = "";
		var rootName = "";
		switch(grpType)
		{
			case "1":
				rootName = "系统";
				break;
			case "4":
				rootName = "网段";
				break;
			case "3":
				rootName = "采集组";
				break;
			case "0":
				rootName = "一般组";
				break;
		}
		var gGroupTree = new JetsenWeb.UI.Tree(rootName, "javascript:void(0)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
		gGroupTree.showTop = false;
		gGroupTree.setBehavior("classic");
		var treeNodes = { "0" : gGroupTree};
		var treeNodeChildCounts = {};
		var userId = jetsennet.Application.userInfo.UserId;
		var fagAdmin = isadmin(userId);
    	if(fagAdmin == "false"){
//		if(JetsenWeb.getUserInfo().UserId != 1){
			var changeval = changeParentid(sResult)
			var typeObjs = JetsenWeb.Xml.toObject(changeval, "Record");
		}else{
			var typeObjs = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		}
		if (typeObjs != null) {
			for ( var i = 0; i < typeObjs.length; i++) {
				var nodeParentId = valueOf(typeObjs[i], "PARENT_ID", "");
				if (nodeParentId == "")
				{
					treeNodeChildCounts["0"] = treeNodeChildCounts["0"] ? treeNodeChildCounts["0"] + 1 : 1;
				}
				else
				{
					treeNodeChildCounts[nodeParentId] = treeNodeChildCounts[nodeParentId] ? treeNodeChildCounts[nodeParentId] + 1 : 1;
				}
			}
			for ( var i = 0; i < typeObjs.length; i++) {
				var groupId = valueOf(typeObjs[i], "GROUP_ID", "-1");
				var groupName = valueOf(typeObjs[i], "GROUP_NAME", "");
				treeNodes[groupId] = new JetsenWeb.UI.TreeItem(valueOf(typeObjs[i], "GROUP_NAME", ""), "javascript:$('txtParentGroup').value = '" + groupName + "'; $('hidParentId').value = '" + groupId + "'; void(0);",
						null, AC_IMG_PATH + (treeNodeChildCounts[groupId] > 0 ? "defaulticon.gif" : "pcde_002.gif"), AC_IMG_PATH + (treeNodeChildCounts[groupId] > 0 ? "defaulticon.gif" : "pcde_002.gif"));
				if (valueOf(typeObjs[i], "PARENT_ID", "") == "")
				{
					treeNodes["0"].add(treeNodes[groupId]);
				}
				else
				{
					var parentNode = treeNodes[valueOf(typeObjs[i], "PARENT_ID", "")];
					if (parentNode)
					{
						parentNode.add(treeNodes[groupId]);
					}
				}
			}
		}
		treeNodes["0"].add(new JetsenWeb.UI.TreeItem("没有父级", "javascript:$('txtParentGroup').value = ''; $('hidParentId').value = ''; void(0);",
				null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif"));
		$("divParentGroupTree").innerHTML = gGroupTree;
		$("hidParentId").value = parentId ? parentId : "";
		getParentNameByParentId(parentId);
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'aa.GROUP_ID', '1']);
}
// 新增=====================================================================================
function newObjGroup() {
	if (gCurSelectGrp["GROUP_TYPE"] == "101")
	{
		jetsennet.alert("请选择资源组类型！");
		return;
	}
	var areaElements = JetsenWeb.Form.getElements('divObjGroup');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	loadParentGroupByType(gCurSelectGrp["GROUP_TYPE"]);
	setSelectedValue($("cbo_GROUP_TYPE"), gCurSelectGrp["GROUP_TYPE"]);
	switch (gCurSelectGrp["GROUP_TYPE"]) {
	case "3":
		$("trCollectorId").style.display = "";
		$("trBeginIp").style.display = "none";
		$("trEndIp").style.display = "none";
		break;
	case "4":
		$("trCollectorId").style.display = "none";
		$("trBeginIp").style.display = "";
		$("trEndIp").style.display = "";
		break;
	default:
		$("trCollectorId").style.display = "none";
		$("trBeginIp").style.display = "none";
		$("trEndIp").style.display = "none";
		break;
	}
	showRemindWordCount($("txt_GROUP_DESC").value,$('remindWord'),"120");
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 340 }, title : "新建资源组" });
	dialog.controls = [ "divObjGroup" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjGroup = { GROUP_NAME : $("txt_GROUP_NAME").value,
				PARENT_IDS : $("hidParentId").value
				, GROUP_TYPE : $("cbo_GROUP_TYPE").value, GROUP_CODE : $("txt_GROUP_CODE").value, GROUP_DESC : $("txt_GROUP_DESC").value,
				USE_TYPE : "0", CREATE_USER : JetsenWeb.Application.userInfo.UserName };
			if (gCurSelectGrp["GROUP_TYPE"] == "3") {
				if ($("cbo_COLLECTOR_ID").value == "") {
					jetsennet.alert("请选择采集器！");
					return;
				}
				oObjGroup.NUM_VAL1 = $("cbo_COLLECTOR_ID").value;
			}
			if(parseInt(getBytesCount($("txt_GROUP_DESC").value))>240){
            	jetsennet.alert("描述不能超过120个文字！");
            	return;
            }
			if (gCurSelectGrp["GROUP_TYPE"] == "4") {
				if (!validateIPSection(gIpBegin.getValue(), gIpEnd.getValue())) {
					return null;
				}
				oObjGroup.FIELD_4 = gIpBegin.getValue();
				oObjGroup.FIELD_5 = gIpEnd.getValue();
			}

//			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadObjGroup();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_OBJGROUP", JetsenWeb.Xml.serializer(oObjGroup, "BMP_OBJGROUP") ]);
		}
	};
	dialog.showDialog();
}
// 编辑=====================================================================================
function editObjGroup(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divObjGroup');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("aa.GROUP_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "a", "a.GROUP_ID=aa.GROUP_ID", JetsenWeb.TableJoinType.Left));
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.GROUP_ID", ResultFields : "aa.*,a.PARENT_ID", PageInfo : null, QueryTable : queryTable });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objObjGroup = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_GROUP_NAME").value = valueOf(objObjGroup, "GROUP_NAME", "");
		setSelectedValue($("cbo_GROUP_TYPE"), valueOf(objObjGroup, "GROUP_TYPE", ""));
		$("txt_GROUP_CODE").value = valueOf(objObjGroup, "GROUP_CODE", "");
		$("txt_GROUP_DESC").value = valueOf(objObjGroup, "GROUP_DESC", "");
		showRemindWordCount($("txt_GROUP_DESC").value,$('remindWord'),"120");
		loadParentGroupByType(valueOf(objObjGroup, "GROUP_TYPE", ""), valueOf(objObjGroup, "PARENT_ID", ""));
		switch (gCurSelectGrp["GROUP_TYPE"]) {
		case "3":
			$("trCollectorId").style.display = "";
			$("trBeginIp").style.display = "none";
			$("trEndIp").style.display = "none";
			setSelectedValue($("cbo_COLLECTOR_ID"), valueOf(objObjGroup, "NUM_VAL1", ""));
			break;
		case "4":
			$("trCollectorId").style.display = "none";
			$("trBeginIp").style.display = "";
			$("trEndIp").style.display = "";
			gIpBegin.setValue(valueOf(objObjGroup, "FIELD_4", ""));
			gIpEnd.setValue(valueOf(objObjGroup, "FIELD_5", ""));
			break;
		default:
			$("trCollectorId").style.display = "none";
			$("trBeginIp").style.display = "none";
			$("trEndIp").style.display = "none";
			break;
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 340 }, title : "编辑资源组" });
	dialog.controls = [ "divObjGroup" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oObjGroup = { GROUP_ID : keyId, GROUP_NAME : $("txt_GROUP_NAME").value, PARENT_IDS : $("hidParentId").value,
				GROUP_TYPE : $("cbo_GROUP_TYPE").value, GROUP_CODE : $("txt_GROUP_CODE").value, GROUP_DESC : $("txt_GROUP_DESC").value,
				USE_TYPE : "0" };

			if (gCurSelectGrp["GROUP_TYPE"] == "3") {
				if ($("cbo_COLLECTOR_ID").value == "") {
					jetsennet.alert("请选择采集器！");
					return;
				}
				oObjGroup.NUM_VAL1 = $("cbo_COLLECTOR_ID").value;
			}
			if(parseInt(getBytesCount($("txt_GROUP_DESC").value))>240){
            	jetsennet.alert("描述不能超过120个文字！");
            	return;
            }
			if (gCurSelectGrp["GROUP_TYPE"] == "4") {
				if (!validateIPSection(gIpBegin.getValue(), gIpEnd.getValue())) {
					return null;
				}
				oObjGroup.FIELD_4 = gIpBegin.getValue();
				oObjGroup.FIELD_5 = gIpEnd.getValue();
			}

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadObjGroup();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_OBJGROUP", JetsenWeb.Xml.serializer(oObjGroup, "BMP_OBJGROUP") ]);
		}
	};
	dialog.showDialog();
}
// 删除=====================================================================================
function deleteObjGroup(keyId) {
	jetsennet.confirm("确定删除？", function () {
		jetsennet.confirm("删除资源组,将自动删除关联信息和子资源组,不可恢复,确认吗？", function () {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		loadObjGroup();
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjDelete", [ "BMP_OBJGROUP", keyId ]);
	return true;
		});
	});
}

// 初始化===============================================================
function pageInit() {
	var frameLeft = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaLeft"), { splitType : 1, fixControlIndex : 0, splitBorder : 0,
		size : { width : 210, height : 0 }, showSplit : false });
	var frameRgiht = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divaRight"), { splitType : 1, fixControlIndex : 0, splitBorder : 0,
		showSplit : false });
	frameLeft.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divLeftTitle"), { size : { width : 0, height : 27 } }));
	frameLeft.addControl(new JetsenWeb.UI.PageItem("divTree"));

	frameRgiht.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divRightTitle"), { size : { width : 0, height : 27 } }));
	frameRgiht.addControl(new JetsenWeb.UI.PageItem("divContainer"));

	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 0, fixControlIndex : 0, splitBorder : 0, showSplit : true });
	gFrame.addControl(frameLeft);
	gFrame.addControl(frameRgiht);

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
    parent.document.getElementById("spanWindowName").innerHTML = document.title;
	loadObjGroup();
	//loadGroupByParent('','','101');
	loadTableObject($('cbo_COLLECTOR_ID'), false, "BMP_COLLECTOR", "COLL_NAME", "COLL_ID");
	gIpBegin = new IP("txt_IP_START");
	gIpBegin.init();
	gIpEnd = new IP("txt_IP_END");
	gIpEnd.init();
}

function getParentNameByParentId(id)
{
	if (id && id != 0)
	{
		var condition = new JetsenWeb.SqlConditionCollection();
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_ID",id,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
	    
	    var sqlQuery = new JetsenWeb.SqlQuery();    
	    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"GROUP_ID",PageInfo:null,ResultFields:"GROUP_NAME",               
	           QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"BMP_OBJGROUP"})});
	     
	    sqlQuery.Conditions = condition;
	    
	    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function(ret)
	    {
	        var obj = JetsenWeb.Xml.toObject(ret.resultVal).Record;
	        if (obj != null)
	        {
	        	$("txtParentGroup").value = obj.GROUP_NAME;
	        }            
	    }
	    ws.onerror = function(ex){ jetsennet.error(ex);};
	    ws.call("bmpObjQuery",[sqlQuery.toXml()]);
	}
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}

function changeParentid(sResult){
	var xmlDoc = new JetsenWeb.XmlDoc();
	xmlDoc.loadXML(sResult.resultVal);
     var groupIds = new Array();
     var objs = JetsenWeb.Xml.toObject(sResult.resultVal,"Record");
     if(objs && objs.length>0)
        {	    	    
            for(var i=0;i<objs.length;i++)
            {
            	groupIds.push(objs[i].GROUP_ID);
            }
        }
	var nodes = xmlDoc.documentElement.selectNodes("Record");
	for ( var i = 0; i < nodes.length; i++)
	{
		var pid = valueOf(nodes[i].selectSingleNode("PARENT_ID"),"text", "");
		if(pid != "" || pid != null)
		{
			var fag = true;
			for (var j = 0; j < groupIds.length; j++)
			{
				if(pid == groupIds[j])
				{
					fag = false;
				}				 
			}
			if(fag)
			{
				nodes[i].selectSingleNode("PARENT_ID").text = "";
			}
		}
	}
	return xmlDoc;
}

function isadmin(userId)
{	
	var result = false;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{	
		result = ret.value;
	}
	ws.onerror = function(ex){jetsennet.error(ex);};
	ws.call("isAdministrator",[userId]); 
	return result;
}