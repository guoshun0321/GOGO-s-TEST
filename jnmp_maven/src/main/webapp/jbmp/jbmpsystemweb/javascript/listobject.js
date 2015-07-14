//属性的声明=====================================================================================
   var currentGroupId = null;
   var currentGroup = null;
   var currentGroupType = null;
   var curObjId = -1;
   var equipment;
   var currentEquimentId;
   var currentSelectGruop;
   //实例化结果
   var instanceResult;
   //当前自动发现对象的采集器Id
   var currentCollId;
   var subObjects = {};
//===============================================================================================
//获得对象组信息
function getAttrObj() {
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "b", "a.GROUP_ID = b.GROUP_ID", JetsenWeb.TableJoinType.Left));
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.OrderString = "ORDER BY a.GROUP_ID";
	JetsenWeb.extend(sqlQuery, { KeyId: "GROUP_ID", QueryTable: queryTable, ResultFields: "a.GROUP_ID, a.GROUP_TYPE, b.PARENT_ID, a.GROUP_NAME" });
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;

    ws.oncallback = function (ret) {
    	var userId = jetsennet.Application.userInfo.UserId;
    	if(!isAdminAccount) {
//		if(JetsenWeb.getUserInfo().UserId != 1){
	    	var changeresult = changeParentid(ret);
	    	attrGroupArray = JetsenWeb.Xml.toObject(changeresult, "Record");
		}else{
			attrGroupArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		}
    	if(attrGroupArray == null) {
    		attrGroupArray = "";
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.GROUP_ID', '1']);
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//将对象组按照GROUP_TYPE分成(一般组、业务系统、网段、采集)
function createGroupTree() {
	if(!attrGroupArray) {
		getAttrObj();
	}
	otherTree = new JetsenWeb.UI.Tree("所有分组", "javascript:showObjList('', -1, null, 1)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    otherTree.showTop = true;
    otherTree.setBehavior("classic");
    var node4 = new JetsenWeb.UI.TreeItem('系统', "javascript:showObjectList('', '1')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    var node2 = new JetsenWeb.UI.TreeItem('采集组', "javascript:showObjectList('', '3')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    var node3 = new JetsenWeb.UI.TreeItem('网段', "javascript:showObjectList('', '4')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    var node1 = new JetsenWeb.UI.TreeItem('一般组', "javascript:showObjectList(null, '0')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    
    otherTree.add(node4);
    
    if(isAdminAccount) {
    	otherTree.add(node2);
        otherTree.add(node3);
        otherTree.add(node1);
    }
    
    if(attrGroupArray) {
		var treeNode = new Array();
		for(var i=0; i<attrGroupArray.length; i++) {
			var attrObj = attrGroupArray[i];
			var groupType = attrObj['GROUP_TYPE'];
			var groupId = attrObj['GROUP_ID'];
			var parentId = attrObj['PARENT_ID'];
			var groupName = attrObj['GROUP_NAME'];
			var node = null;
			switch (groupType) {
			case "0":
				//node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'0')");
				if(isAdminAccount) {
				if(parentId == 0 || parentId == "") {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'0')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					node1.add(node);
				}else {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'0')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					for(var j=0; j<treeNode.length; j++) {
						if(attrGroupArray[j]['GROUP_ID'] == parentId) {
							treeNode[j].add(node);
							break;
						}
					}
				}
				treeNode.push(node);
				}
				break;
			case "3":
				//node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'3')");
				if(isAdminAccount) {
				if(parentId == 0 || parentId == "") {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'3')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					node2.add(node);
				}else {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'3')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					for(var j=0; j<treeNode.length; j++) {
						if(attrGroupArray[j]['GROUP_ID'] == parentId) {
							treeNode[j].add(node);
							break;
						}
					}
				}
				treeNode.push(node);
				}
				break;
			case "4":
				//node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'4')");
				if(isAdminAccount) {
				if(parentId == 0 || parentId == "") {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'4')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					node3.add(node);
				}else {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'4')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					for(var j=0; j<treeNode.length; j++) {
						if(attrGroupArray[j]['GROUP_ID'] == parentId) {
							treeNode[j].add(node);
							break;
						}
					}
				}
				treeNode.push(node);
				}
				break;
			case "1":
				//node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'1')");
				if(parentId == 0 || parentId == "") {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'1')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					node4.add(node);
				}else {
					node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'1')", null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif");
					var param = {groupId:groupId,type:1,groupFag:true};
					node.param = param ;
					for(var j=0; j<treeNode.length; j++) {
						if(attrGroupArray[j]['GROUP_ID'] == parentId) {
							treeNode[j].add(node);
							break;
						}
					}
				}
				treeNode.push(node);
				break;
			default:
				node = new JetsenWeb.UI.TreeItem(groupName, "javascript:showObjectList(" + groupId + "," + "'0')");
				treeNode.push(node);
				break;
			}
		}
	}
}

//通过编辑实例化自动发现对象
function newObjectElement(obj_id, coll_id,class_id) {
	currentCollId = coll_id;
//   	$("collectorSelectId").style.display = "none";
//   	$("collectorSelectLabel").style.display = "";
//   	$("collectorInputText").disabled="disabled";
//   	$("btnSelGroup2").style.display = "";
//   	$("btnSelGroup1").style.display = "none";
//   	$("collectorInputId").style.display = "";
//   	$("showGroupOption").options.length = 0;
//   	$("tdClassGroup").style.display = "";
//   	$("tdClassObject").style.display = "none";
//   	$("tr2").style.display = "none";
//   	$("colloctorLabel").style.display = "";
//   	$("txt_DB_NAME1").disabled = true;
//    $("txt_DB_NAME1").readonly = true;
	$("objectgroupcontent").style.display = "";
	$("objectgroupcontrol").style.display = "";
    $("parentId").style.display = "none";
    $("community").style.display = "";
    $("tdClassObject").style.display = "none";
    $("selectVersion").style.display = "none";
    $("editVersion").style.display = "";
    $("objectParam").style.display = "";
	$("collectorSelectId").style.display = "";
   	$("collectorSelectLabel").style.display = "";
   	$("db1").style.display = "none";
   	$("tr2").style.display = "none";
   	$("btnSelGroup2").style.display = "";
   	$("btnSelGroup1").style.display = "none";
   	$("tdClassGroup").style.display = "";
   	$("collectorInputId").style.display = "none";
   	$("username").style.display = "none";
   	$("password").style.display = "none";
   	$("colloctorLabel").style.display = "none";
    
   	//清楚对话框的信息
   	clearObjDialog();
   	setClassToNewElement();
   	$('txt_CLASS_TYPE2').value = class_id;
   	setCollector2InputText();
   	$("txt_PARENT_ID").readOnly = true;
    $("txt_PARENT_ID").disabled = true;
    $("txt_PARENT_ID").value = 0;
    currentEquimentId = obj_id;
    getTheEquipmentById(obj_id);
   	var dialog = new JetsenWeb.UI.Window("new-objectGroup-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 580 }, title: "新建对象" });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function() {
    	var areaElements = genValidateForms();
    	var classType;
    	for(var i=0; i<arrAttibClass.length; i++) {
			var eachOne = arrAttibClass[i];
			if(eachOne["CLASS_ID"] == getSelectedValue($("txt_CLASS_TYPE2"))) {
				classType = eachOne["CLASS_TYPE"];
				break;
			}
		}
    	gruop_ids = "";
    	var len = el("showGroupOption").options.length;
        for(var i=0; i<len; i++) {
            if(gruop_ids != "") {
            	gruop_ids += ",";
            }
            gruop_ids += el("showGroupOption").options[i].value;
        }
    	if (JetsenWeb.Form.Validate(areaElements, true)) {
            var object = {
            	PARENT_ID: 0,
            	CLASS_ID: getSelectedValue($("txt_CLASS_TYPE2")),
            	CLASS_TYPE: classType,
            	OBJ_NAME: $("txt_OBJ_NAME").value,
            	//OBJ_STATE: getSelectedValue($("sel_OBJ_STATE")),attributeOf($('sel_OBJ_STATE'),"selectedValue","")
            	OBJ_STATE: attributeOf($('sel_OBJ_STATE'),"selectedValue",""),
            	IP_ADDR: gCollIp.getValue(),
            	IP_PORT: $("txt_IP_PORT").value,
            	USER_NAME: $("txt_USERNAME2").value,
//            	USER_PWD: $("txt_PASSWORD").value,
            	VERSION: $("sel_VERSION2").value,
            	OBJ_PARAM: $("txt_OBJ_PARAM").value,
            	OBJ_DESC: $("txt_OBJ_DESC").value,
            	CREATE_USER: "SYSTEM",
            	COLL_ID: currentCollId,
            	MAN_ID: getSelectedValue($("manufacturer"))
//            	FIELD_1: $("txt_DB_NAME1")
            };
            if(!validateIP(object.IP_ADDR)) {
                jetsennet.alert("IP填写错误！");
                return;
            }
            var port_adress = parseInt(object.IP_PORT);
            if(port_adress > 65535) {
                jetsennet.alert("端口号不能大于65535！");
                return;
            }
            object.GROUP_ID = gruop_ids;
            getGroupIdByCollId(object["COLL_ID"]);
            //如果得不到采集组则不进行实例化
            if(!collectorGroupId) {
            	jetsennet.alert("不能得到采集组，实例化失败！");
            	return;
            }
            object.COLLGROUP_ID = collectorGroupId;
            var strIp = getAutoObjIpOne(object);
            if(strIp){
           	 jetsennet.confirm("IP是"+strIp+"的对象已存在，确定要新建吗？", function (){
                 objectInstanceByEdit(object, currentEquimentId);
                 gruop_ids = "";
                 showAutoDisc();
                 JetsenWeb.UI.Windows.close("new-objectGroup-win");
                 refreshTreeChange();
                 return true;
           	 });
           	 }else{
                 objectInstanceByEdit(object, currentEquimentId);
                 gruop_ids = "";
                 showAutoDisc();
                 JetsenWeb.UI.Windows.close("new-objectGroup-win");
                 refreshTreeChange();
           	 }

        }
    }
    dialog.showDialog();
}

//将采集器回填到输入文本中
function setCollector2InputText() {
	for(var i=0; i<arrColltor.length; i++) {
		var oneColltor = arrColltor[i];
		if(currentCollId == oneColltor['COLL_ID']) {
			$("collectorInputText").value = oneColltor['COLL_NAME'];
		}
	}
}

//将选择的对象组添加的对象组显示框中
function addGroup2GroupDia(group_ids) {
	var groupIdsStr = group_ids;
	var groupIdsArray = groupIdsStr.split(",");
	for(var i=0; i<groupIdsArray.length; i++) {
		for(var j=0; j<attrGroupArray.length; j++) {
			if(groupIdsArray[i] == attrGroupArray[j]['GROUP_ID']) {
				var groupOption = new Option(attrGroupArray[j]['GROUP_NAME'], attrGroupArray[j]['GROUP_ID']);//document.createElement("option");
				$("showGroupOption").options.add(groupOption);
			}
		}
	}
}

//收集在按分组浏览对象时新建对象的表单信息
function getNewObjectGroup() {
	var classId = getSelectedValue($("txt_CLASS_TYPE2"));
	var classType;
	for(var i=0; i<arrAttibClass.length; i++) {
		var classObj = arrAttibClass[i];
		if(classObj["CLASS_ID"] == classId) {
			classType = classObj["CLASS_TYPE"];
		}
	}
	var objElement = {
            //GROUP_ID: $("txt_GROUP_ID2").value,
            PARENT_ID: $("txt_PARENT_ID").value,
            CLASS_ID: classId,
            CLASS_TYPE: classType,
            OBJ_NAME: $("txt_OBJ_NAME").value,
            OBJ_STATE: getSelectedValue($("sel_OBJ_STATE")),
            IP_ADDR: gCollIp.getValue(),
            IP_PORT: $("txt_IP_PORT").value,
            USER_NAME: $("txt_USERNAME").value,
            USER_PWD: $("txt_PASSWORD").value,
            OBJ_PARAM: $("txt_OBJ_PARAM").value,
            OBJ_DESC: $("txt_OBJ_DESC").value,
            RECEIVE_ENABLE: getSelectedValue($("sel_TRAP_ENABLE")),
            VERSION: getSelectedValue($("sel_VERSION")),
            CREATE_USER: JetsenWeb.Application.userInfo.UserName
        }
	return objElement;
}

//将类型信息填充到新建对象对话框
function setClassToNewElement() {
	var element = $("txt_CLASS_TYPE2");
	element.options.length = 0;
	for(var i=0; i<arrAttibClass.length; i++) {
		var obj = arrAttibClass[i];
		element.options.add(new Option(obj["CLASS_NAME"], obj["CLASS_ID"]));
	}
}

//显示监控对象列表
function showObjectList(groupId, groupType) {
	if(autoFindFag == 2){
		retsetDefaultsize()
	}
	deleteGroupObjFag = 1;
	$("divListPagination").style.display = "";
	$("myDivListPagination").style.display = "";
	$("deleteObjectMany").onclick = delElementIds;
	clearObjectInformation();
	$("instanceEquiment").style.display = "none";
	//设置新建按钮可见
//	$("newObjElement").style.display = "none";
//	$("addSubObject").style.display = "none";
//	$("addObjectInGroup").style.display = "";

	$("addObjectTo").style.display = "";
	$("toMonitorObjAttr").style.display = "none";

	$("groupDelete").style.display = "";
	$("findInstance").style.display = "none";
	$("findObject").style.display = "";
	$("buttonSearch1").style.display = "none";
	$("buttonSearch2").style.display = "";
	$("ObjectAttriListLw").style.display = "none";	
	$("oldCreate").style.display = "";
	$("addSubObject").style.display = "none";
	$("newObjElement").style.display = "none";
	$("addObjectInGroup").style.display = "";
	if(!groupId && !groupType) {
		return;
	}
	if(groupId && groupId != -1) {
		$("groupDelete").style.display = "";
	}else {
		$("groupDelete").style.display = "none";
	}
	changeBottomButton("", "none", "none", "none", "none", "");
	currentGroupId = groupId;
	currentGroupType = groupType;
	//获得当前组的信息
	initCurrentGroup(currentGroupId);
	myListPagination.currentPage = 1;
	loadObjectListInGroup(currentGroupId, currentGroupType);
}

//分页显示分组对象类表
function loadObjectListInGroup(currentGroupId, currentGroupType) {
	otherQueryConditions.SqlConditions = [];
	if(currentGroupId) {
		otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", currentGroupId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if(currentGroupType == "101"){
		otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_TYPE", "1,4,3,0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	}
	else
	{
		var groupTypeInt = parseInt(currentGroupType)
		otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_TYPE", groupTypeInt, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	var searchKey = $("txt_Key2").value;
	var searchIPAddress2 = $("txt_IPAddress2").value;
	if(searchKey) {
		otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("c.OBJ_NAME", searchKey, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	if(searchIPAddress2)
	{
		otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("c.IP_ADDR", searchIPAddress2, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	//otherQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("c.OBJ_STATE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric))
	otherSqlQuery.OrderString = myListPagination.orderBy;
	otherSqlQuery.Conditions = otherQueryConditions;
	
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	$('divElementList').innerHTML = "";
        $('divElementList').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectmodel.xslt", ret.resultVal);
        myGridList.bind($('divElementList'), $('objTableList'));
        myListPagination.setRowCount($('hidCount').value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
//    ws.call("bmpObjQuery", [otherSqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [otherSqlQuery.toXml(), 'b.GROUP_ID', '1']);
}

//从组中批量删除对象
function deleteObjFromGroup() {
	if(!currentGroupId) {
		jetsennet.alert("请选择组！");
		return;
	}
	var objectIds = JetsenWeb.Form.getCheckedValues('chkAllGroupObject');
	if(objectIds.length <= 0) {
		jetsennet.alert("请选择要移除的对象！");
		return;
	}
	jetsennet.confirm("确定要从该组中移除对象？", function () 
	{
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function (ret) {
	        showObjectList(currentGroupId, currentGroupType);
	    	refreshTreeGroup();
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	    ws.call("bmpDelObjectFromGroup", [currentGroupId + "_" + objectIds.join(',')]);
	    return true;
	});
}

//列出没有实例化的对象
function showUnInstanceObj() {
	loadObjByState();
	myListPagination.currentPage = 1;
}

//转载对象列表
function loadObjByState() {
	var obj_id = parseInt(obj_id);
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_STATE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric))
	JetsenWeb.extend(sqlQuery,{IsPageResult: 1,PageInfo: myListPagination, KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditions;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divElementList').innerHTML = JetsenWeb.Xml.transformXML(xsltPath, ret.resultVal);
        gGridList.bind($('divElementList'), $('objTableList'));
        myListPagination.setRowCount($('hidCount').value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//通过对象OBJ_ID实例化属性
function instanceAttrClsByObjId(obj_id) {

	var obj_id = parseInt(obj_id);
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "b", "a.CLASS_ID=b.CLASS_ID",JetsenWeb.TableJoinType.Left));
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.OBJ_ID", obj_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery,{KeyId: "a.CLASS_ID", QueryTable: queryTable, ResultFields: "a.*"});
	sqlQuery.Conditions = queryConditions;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	myAttrClsArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(myAttrClsArray) {
    		var attrObj = myAttrClsArray[0];
    		curAttrCls = attrObj['CLASS_TYPE'];
        	curAttrClaId = attrObj['CLASS_ID'];
        	curClass = myAttrClsArray[0];
    	}
    	else
    	{
    		jetsennet.alert("数据为空！");
    		return;
    	}
    	//setDefaultInfo();
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//===========================================================================================
//通过对象OBJ_ID查找这条记录
function getObjetcById(obj_id) {
	var obj;
	if(!obj_id) {
		return;
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", obj_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery,{KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.*"});
	sqlQuery.Conditions = queryConditions;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(obj) {
    		obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
    		curAttrCls = obj['CLASS_TYPE'];
        	curAttrClaId = obj['CLASS_ID'];
    	}
    	else
    	{
    		jetsennet.alert("数据为空！");
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    return obj;
}

//=================================================================================================================
//点击自动发现对象树节点
function showAutoDisc() {
	autoFindFag = 2;
	retsetsize();
	//将查询条件设置为可以看见
	$("findObjectChild").style.display = "none";
	$("findObject").style.display = "none";
	$("ObjectAttriListLw").style.display = "none";
	$("buttonSearch1").style.display = "none";
	$("buttonSearch2").style.display = "none";
	$("findInstance").style.display = "";
	$("instanceEquiment").style.display = "";
	$("addObjectTo").style.display = "none";
	$("toMonitorObjAttr").style.display = "none";
	$("divListPagination").style.display = "";
	$("myDivListPagination").style.display = "";
	//按钮的显示要好好设置一下
	
	//设置标题栏的新建按钮不可见
	$("oldCreate").style.display = "none";
	$("deleteObjectMany").style.display = "none";
	
	$("groupDelete").style.display = "none";
	curObjId = -1;
	clearObjectInformation();
	//获得attribclass数组
	//获得collector数组
	//设置对话框中的类型
	$("txt_instance_findByColl").options.length = 0;
	setCollector2Dialog();
	$("txt_instance_findByClass").options.length = 0;
	setClass2QueryDia();

	//设置
	$('divElementList').innerHTML = "";
	autoDiscPagination.currentPage = 1;
	myListPagination.currentPage = 1;
	searchEquiment();
}

function loadEquimentBySearch()
{
	autoDiscPagination.currentPage = 1;
	searchEquiment();
}

//查找
function searchEquiment() {
	var searchIP = $("txt_instance_findByIP").value;
	var searchType = getSelectedValue($('txt_instance_findByClass'));
	var searchColl = getSelectedValue($('txt_instance_findByColl'));
	var searchState = getSelectedValue($('txt_instance_findByStatus'));
	//searchSelectValue();
	$('divElementList').innerHTML = "";
	autoDiscQueryConditions.SqlConditions = [];
	if(searchType) {
		autoDiscQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_ID", searchType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	if(searchIP) {
		autoDiscQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.IP", searchIP, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Like, JetsenWeb.SqlParamType.String));
	}
	if(searchState) {
		autoDiscQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_STATUS", (parseInt(searchState)-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
	}
	if(searchColl) {
		autoDiscQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.COLL_ID", searchColl, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
	}
	//autoDiscQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.REC_STATUS", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Number));
	autoDiscSqlQuery.OrderString = autoDiscPagination.orderBy;
	autoDiscSqlQuery.Conditions = autoDiscQueryConditions;
	
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var number = 0;
    	var xmlDoc = new JetsenWeb.XmlDoc();
    	xmlDoc.loadXML(ret.resultVal);
    	
    	$('divElementList').innerHTML = "";
        $('divElementList').innerHTML = JetsenWeb.Xml._transformXML("xslt/autoDisObjectList.xslt", xmlDoc);
        autoDiscGridList.bind($('divElementList'), $('autoDiscTable'));
        $('hidCount').value = $('hidCount').value - number;
        autoDiscPagination.setRowCount($('hidCount').value);
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
//    ws.call("bmpObjQuery", [autoDiscSqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [autoDiscSqlQuery.toXml(), 'e.GROUP_ID', '1']);
}

function checkObjId(objsIdArray, objId)
{
	flag = false;
	for(var i=0; i<objsIdArray.length; i++)
	{
		var o = objsIdArray[i];
		if(objId == o)
		{
			flag = true;
		}
	}
	return flag;
}

//批量实例化对象
function objectInstance() {
	var strIp = "";
	var checkIds = JetsenWeb.Form.getCheckedValues("chkMsgLog");
	var insertObject;
	if(checkIds.length < 1) {
		jetsennet.alert("请选择要新建的对象！");
		return;
	}
//	var iparray = new Array();
    var objInstance = new Array();
	 var autObj = document.getElementsByName("chkMsgLog");
     for (var i = 0; i < autObj.length; i++) {
         if (autObj[i].checked){
        	 var ip = autObj[i].getAttribute("itemIP");
        	 var className = autObj[i].getAttribute("itemClassName"); 
        	 var obj = {IP:ip,CLASS_NAME:className};
        	 objInstance.push(obj);
        	 }
     }    
     strIp = getAutoObjIp(objInstance);
     if(strIp){
    	 jetsennet.confirm("IP是"+strIp+"的对象已存在，确定要新建吗？", function ()  {
    			for(var i=0; i<checkIds.length; i++) {
    				insertObject = equipmentToObject(checkIds[i]);
    				addAndInstanceObject(insertObject);
    				changeRecStatus(checkIds[i]);
    			}
    			showAutoDisc();
    			 return true;
    	 });
     }else{
    	 for(var i=0; i<checkIds.length; i++) {
				insertObject = equipmentToObject(checkIds[i]);
				addAndInstanceObject(insertObject);
				changeRecStatus(checkIds[i]);
			}
			showAutoDisc();
     }

}

//通过编辑方式来实例化对象
function objectInstanceByEdit(object, currentEquimentId) {
	addAndInstanceObject(object);
	if(currentEquimentId) {
		changeRecStatus(currentEquimentId);
	}
}

//添加并实例化对象
function addAndInstanceObject(object) {
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        insDatas = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (ret.errorString != null && ret.errorString.trim() != "") {
            jetsennet.alert(ret.errorString + "！");
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpInsertAndInstanceObject", ["BMP_OBJECT", JetsenWeb.Xml.serializer(object, "BMP_OBJECT")]);
}

//将自动发现的设备转化成对象
function equipmentToObject(equipmentId) {
	var insertObject;
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_AUTODISOBJ", "a");
	var queryConditoins = new JetsenWeb.SqlConditionCollection();
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left))
	queryConditoins.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", equipmentId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.*,b.CLASS_TYPE" });
	sqlQuery.Conditions = queryConditoins;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(obj == null) {
    		jetsennet.alert("数据为空！");
    		return;
    	}
    	obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
    	insertObject = {
    			CLASS_ID: obj["CLASS_ID"],
    			CLASS_GROUP: 1,
    			CLASS_TYPE: obj["CLASS_TYPE"],
    			OBJ_NAME: obj["OBJ_NAME"],
    			OBJ_STATE: 0,
    			IP_ADDR: obj["IP"],
    			IP_PORT: obj["PORT"],
    			USER_NAME: obj["USER_NAME"],
    			USER_PWD: obj["PASSWORD"],
    			RECEIVE_ENABLE: 0,
    			VERSION: obj["VERSION"],
    			CREATE_USER: "SYSTEM",
    			COLL_ID: obj["COLL_ID"],
    			PARENT_ID: 0
    	}
    	getGroupIdByCollId(insertObject["COLL_ID"]);
        //如果得不到采集组则不进行实例化
        if(!collectorGroupId) {
        	jetsennet.alert("不能得到采集组，实例化失败！");
        	return;
        }
        insertObject.COLLGROUP_ID = collectorGroupId;
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    return insertObject;
}

//编辑实例化对象信息
function editInstanceObj(obj_id) {
	var objId = obj_id;
	if(!objId) {
		return;
	}
	currentEquimentId = obj_id;
	getTheEquipmentById(obj_id);
	var areaElements = JetsenWeb.Form.getElements('divPopWindow');
	var dialog = new JetsenWeb.UI.Window("edit-instance-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 700, height: 400 }, title: "编辑" });
	dialog.controls = ["divPopWindow"];
	dialog.onsubmit = function() {
		if(JetsenWeb.Form.Validate(areaElements, true)) {
			var classType;
			var equipmentObject = {
					OBJ_NAME: $("txt_OBJ_NAME").value,
					IP: $("txt_instance_ip").value,
					CLASS_ID: getSelectedValue($("txt_instance_type")),
					COLL_ID: getSelectedValue($("txt_instance_coll")),
					OBJ_STATUS: getSelectedValue($("txt_instance_status")),
					REC_STATUS: getSelectedValue($("txt_instance_iscreate")),
					USER_NAME: $("txt_instance_username").value,
					PASSWORD: $("txt_instance_password").value,
					VERSION: $("txt_instance_version").value,
					PORT: $("txt_instance_port").value
			}
			for(var i=0; i<arrAttibClass.length; i++) {
				var eachOne = arrAttibClass[i];
				if(eachOne["CLASS_ID"] == equipmentObject["CLASS_ID"]) {
					classType = eachOne["CLASS_TYPE"];
				}
			}
			var nmpObject = {
					CLASS_ID: equipmentObject["CLASS_ID"],
	    			CLASS_GROUP: 1,
	    			CLASS_TYPE: classType,
	    			OBJ_NAME: equipmentObject["OBJ_NAME"],
	    			OBJ_STATE: 100,
	    			IP_ADDR: equipmentObject["IP"],
	    			IP_PORT: equipmentObject["PORT"],
	    			USER_NAME: equipmentObject["USER_NAME"],
	    			USER_PWD: equipmentObject["PASSWORD"],
	    			RECEIVE_ENABLE: 0,
	    			VERSION: equipmentObject["VERSION"],
	    			CREATE_USER: "SYSTEM"	
			}
			objectInstanceByEdit(nmpObject, currentEquimentId);
			JetsenWeb.UI.Windows.close("edit-instance-win");
			showAutoDisc();
		}
	}
	dialog.showDialog();
}

//初始化得到CLASS_ID,CLASS_TYPE,CLASS_NAME
function getClassTypeById() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
	var queryConditoins = new JetsenWeb.SqlConditionCollection();
	queryConditoins.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_LEVEL", 1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.CLASS_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoins;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.oncallback = function (ret) {
		arrAttibClass = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if(arrAttibClass == null) {
			arrAttibClass = "";
		}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//将类型设置到对话框中
function setClass2Dialog() {
	var classDocument = $("txt_instance_type");
	for(var i=0; i<arrAttibClass.length; i++) {
		var obj = arrAttibClass[i];
		classDocument.options.add(new Option(obj["CLASS_NAME"], obj["CLASS_ID"]));
	}
}

//将类型设置到查询对话框中
function setClass2QueryDia() {
	var queryDocument = $("txt_instance_findByClass");
	queryDocument.options.add(new Option("请选择", ""));
	if(arrAttibClass != null && arrAttibClass.length > 0) {
		for(var i=0; i<arrAttibClass.length; i++) {
			var obj = arrAttibClass[i];
			queryDocument.options.add(new Option(obj["CLASS_NAME"], obj["CLASS_ID"]));
		}
	}
}

//初始化得到采集器
function getCollector() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.GroupFields = " a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2 "; 
	var queryTable = JetsenWeb.createQueryTable("BMP_COLLECTOR", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "b","a.coll_id = b.num_val1", JetsenWeb.TableJoinType.LEFT));
	JetsenWeb.extend(sqlQuery, { KeyId: "COLL_ID", QueryTable: queryTable, ResultFields: "a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2" });
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.oncallback = function (ret) {
		arrColltor = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'b.GROUP_ID', '1']);
}

//将采集器设置到对话框中
function setCollector2Dialog() {
	var collectorDocument = $("txt_instance_coll");
	var searchByCollector = $("txt_instance_findByColl");
	searchByCollector.options.add(new Option("请选择", ""));
	clearObjDialog();
	if(arrColltor != null && arrColltor.length > 0) {
		for(var i=0; i<arrColltor.length; i++) {
			var obj = arrColltor[i];
			collectorDocument.options.add(new Option(obj["COLL_NAME"], obj["COLL_ID"]));
			searchByCollector.options.add(new Option(obj["COLL_NAME"], obj["COLL_ID"]));
		}
	}
}

//根据自动发现设备的ID获取该设备
function getTheEquipmentById(equipmentId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_AUTODISOBJ", "a");
	var queryConditoins = new JetsenWeb.SqlConditionCollection();
	queryConditoins.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", equipmentId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoins;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.oncallback = function (ret) {
		equipment = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (ret.errorString != null && ret.errorString.trim() != "") {
            jetsennet.alert(ret.errorString + "！");
        }
        if(equipment)
        {
        	equipment = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
        }
        else
        {
        	jetsennet.error("数据为空！");
        }
        setEquipmentField();
        
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//将获得的设备显示在编辑对话框中
function setEquipmentField() {
	if(equipment == null) {
		jetsennet.alert("该条设备为空！");
		return;
	}
	$("txt_OBJ_NAME").value = valueOf(equipment, "OBJ_NAME");
	$("txt_PARENT_ID").value = 0;
	//$("txt_IP_ADDRESS").value = valueOf(equipment, "IP");
	gCollIp.setValue(valueOf(equipment, "IP"));
	$("txt_IP_PORT").value = valueOf(equipment, "PORT");
	
	$("txt_USERNAME2").value = valueOf(equipment, "USER_NAME");
//	$("txt_PASSWORD").value = valueOf(equipment, "PASSWORD");
}

//实例化之后改变设备状态
function changeRecStatus(id) {
	if(!id) {
		return;
	}
	changeRecStatusById(id);
}

//改变自动发现设备的状态
function changeRecStatusById(id) {
	var updateObject = {
			OBJ_ID: id,
			REC_STATUS: 1
	}
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        insDatas = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (ret.errorString != null && ret.errorString.trim() != "") {
            jetsennet.alert(ret.errorString + "！");
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjUpdate", ["BMP_AUTODISOBJ", JetsenWeb.Xml.serializer(updateObject, "BMP_AUTODISOBJ")]);
}

//清理弹出dialog
function clearObjDialog() {
    var areaElements = JetsenWeb.Form.getElements('divPopWindow');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
}

//初始化组列表
function initGroupList() {
	var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
    sqlQuery.OrderString = "ORDER BY GROUP_ID";
    JetsenWeb.extend(sqlQuery, { KeyId: "GROUP_ID", QueryTable: queryTable, ResultFields: "GROUP_ID,GROUP_NAME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    groupArray = [];

    ws.oncallback = function (ret) {
        var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (records) {
            for (i = 0; i < records.length; i++) {
                var record = records[i];
                groupArray.push([record["GROUP_ID"], record["GROUP_NAME"]]);
            }
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//设置对话框的默认值
function setDefaultInformation(type) {
	$("txt_DB_NAME").readOnly = true;
    $("txt_DB_NAME").disabled = true;
    $("txt_PARENT_ID").readOnly = true;
    $("txt_PARENT_ID").disabled = true;
    $("txt_PARENT_ID").value = 0;
    if (type.startWith("SNMP")) {
        $("txt_IP_PORT").value = 161;
        $("txt_USERNAME").value = "public";
    } else if (type.startWith("APP")) {
        if (type.equal("APP_TOMCAT")) {
            $("txt_IP_PORT").value = 8080;
        } else if (type.equal("APP_WEBSPHERE")) {
            $("txt_IP_PORT").value = 8880;
        } else if (type.equal("APP_WEBLOGIC")) {
            $("txt_IP_PORT").value = 7001;
        }
    } else if (type.startWith("WEB")) {

    } else if (type.startWith("DB")) {
        $("txt_DB_NAME").disabled = false;
        $("txt_DB_NAME").readOnly = false;
        if (type.equal("DB_SQLSERVER")) {
            $("txt_IP_PORT").value = 1433;
        } else if (type.equal("DB_ORACLE")) {
            $("txt_IP_PORT").value = 1521;
        } else if (type.equal("DB_DB2")) {
            $("txt_IP_PORT").value = 50000;
        }
    }
}

//===================================================================================================

//将新添加的组添加对应的组关联表(BMP_GROUP2GROUP)

//收集表单信息
function validateGroupForm() {
	var forms = JetsenWeb.Form.getElements("groupInfo");
    return forms;
}

function getGroupParams() {
	var parentId = null;
	if(currentGroup) {
		parentId = currentGroup["GROUP_ID"];
	}else {
		parentId = 0;
	}
	var objElement = {
		GROUP_NAME: $("txt_group_name").value,
		PARENT_IDS: parentId,
		GROUP_TYPE: currentGroupType,
		GROUP_DESC: $("txt_group_desc").value,
		USE_TYPE: 2
	};
	return objElement;
}

//清理弹出dialog
function clearGroupDialog() {
    var areaElements = JetsenWeb.Form.getElements('divGroupWindow');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
}

//获得当前组的信息
function initCurrentGroup(currentGroupId) {
	if(!currentGroupId) {
		currentGroup = null;
		return;
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
    sqlQuery.OrderString = "ORDER BY GROUP_ID";
    JetsenWeb.extend(sqlQuery, { KeyId: "GROUP_ID", QueryTable: queryTable, ResultFields: "GROUP_ID,GROUP_NAME,GROUP_TYPE" });
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_ID", currentGroupId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = conditions;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var currentGroupArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(currentGroupArray)
    	{
    		currentGroup = currentGroupArray[0];
    	}
    	else
    	{
    		jetsennet.alert("数据为空！");
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'GROUP_ID', '1']);
}

//当点击按对象分类进行浏览树时要把按组类型分类的信息清空
function clearInformation() {
	currentGroup = null;
	currentGroupId = null;
	currentGroupType = null;
}

//点击按组类型分类时清空按对象分类浏览的信息
function clearObjectInformation() {
	curClass = null;
	curAttrClaId = null;
	curAttrCls = null;
}

//======================================================================================================

//======================================================================================

//将对象批量添加到组中
function addObjectToGroup() {
	var checkObjIds ;
	var ObjIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
	var ObjInGroupIds = JetsenWeb.Form.getCheckedValues("chkAllGroupObject");
	if(ObjIds.length == 0 && ObjInGroupIds == 0) {
		jetsennet.alert("请选择要操作的对象！");
		return;
	}
	if(ObjIds.length >0 ){
		checkObjIds = ObjIds;
	}else{
		checkObjIds = ObjInGroupIds;
	}
	var dialog = new JetsenWeb.UI.Window("show-addObject2Group-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 400, height: 400 }, title: "选择对象组" });
    dialog.controls = ["divGroupSelectPanel2"];
    dialog.onsubmit = function () {
    	var checkGroupIds = JetsenWeb.Form.getCheckedValues("chkSelectGroup");
    	if(checkGroupIds <= 0) {
    		jetsennet.alert('请选择要添加到的组！');
    		return;
    	}
    	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.async = false;
        ws.oncallback = function (ret) {
        	jetsennet.alert("添加成功！");
        	JetsenWeb.UI.Windows.close("show-addObject2Group-win");
//        	createGroupTree();
        	refreshTreeGroup();
        };
        ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
        ws.call("bmpObjInsert", ["BMP_MAYNOJB2MANYGROUP", checkObjIds.join(",") + "_" + checkGroupIds.join(",")]);
    }
    dialog.showDialog();
    showGroupSelect();
}

//生成分组的选择
function showGroupSelect() {
	$('divGroupSelectTable2').innerHTML = "";
	var selectValue = getSelectedValue($("filterGroup"));
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	sqlQuery.OrderString = "ORDER BY a.GROUP_TYPE";
	JetsenWeb.extend(sqlQuery, { KeyId: "a.GROUP_ID", QueryTable: queryTable, ResultFields: "a.GROUP_ID, a.GROUP_NAME, a.GROUP_TYPE" });
	var conditions = new JetsenWeb.SqlConditionCollection();
	if('1' == selectValue) {
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}else if('3' == selectValue) {
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 3, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}else if('4' == selectValue) {
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 4, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}else if('0' == selectValue) {
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}else {
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", "1,3,4,0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric,true));
	}
	sqlQuery.Conditions = conditions;
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('divGroupSelectTable2').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectgroupselect.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divGroupSelectTable2"), $("objectgroupselect"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.GROUP_ID', '1']);
}

//过滤组选择
function filterGroup() {
	showGroupSelect();
}

//在分组浏览对象的情况下删除对象(先将该对象从BMP_OBJ2GROUP中删除，在删除该对象)
function deleteObjectFromOBJ2GROUP(objId) {
	 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	 ws.soapheader = JetsenWeb.Application.authenticationHeader;
	 ws.oncallback = function (ret) {
	        $('divGroupSelectTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectgroupselect.xslt", ret.resultVal);
	        var gGridList = new JetsenWeb.UI.GridList();
	        gGridList.bind($("divGroupSelectTable"), $("objectgroupselect"));
	 };
	 ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	 ws.call("bmpObjDelete", ["BMP_OBJ2GROUP", objId]);
}

//================================================================================================================
//将采集器展示成下拉框
function collector2Option() {
	var collectorDoc = $("colloctSelector");
	if(arrColltor != null && arrColltor.length > 0) {
		for(var i=0; i<arrColltor.length; i++) {
			var obj = arrColltor[i];
			collectorDoc.options.add(new Option(obj["COLL_NAME"], obj["COLL_ID"]));
		}
	}
}

//通过采集器ID获得采集器组
function getGroupIdByCollId(coll_id) {
	if(!coll_id) {
		jetsennet.alert("无法得到采集器！");
		return;
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.NUM_VAL1", coll_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 3, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.GROUP_ID", QueryTable: queryTable, ResultFields: "a.GROUP_ID" });
	sqlQuery.Conditions = conditions;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var object = JetsenWeb.Xml.toObject(ret.resultVal, "Record")
    	if(!object) {
    		collectorGroupId = "";
    	}else {
    		collectorGroupId = object[0]["GROUP_ID"];
    	}
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.GROUP_ID', '1']);
}

var currentObjIdtoattrilw;
var attrTypesattrlw;
var classIdattrilw;
function getObjAttributeSearch(){
	getObjAttribute(currentObjIdtoattrilw,attrTypesattrlw,classIdattrilw);
}


var gListPaginationAttr = new JetsenWeb.UI.PageBar("listPaginationinsConfigDiv");
gListPaginationAttr.orderBy = "ORDER BY A.OBJATTR_ID";
gListPaginationAttr.onpagechange = function () {
	getObjAttributeSearch();
};
gListPaginationAttr.onupdate = function () {
	$('insObjDiv11').innerHTML  = this.generatePageControl();
};
var gGridListAttr = new JetsenWeb.UI.GridList();
gGridListAttr.ondatasort = function (sortfield, desc) {
	gListPaginationAttr.setOrderBy(sortfield, desc);
}

//================================================================================================================
//获取除自定义属性外的对象属性
function getObjAttribute(currentObjId, attrTypes, classId) {
	currentObjIdtoattrilw =currentObjId ;
	attrTypesattrlw =attrTypes ;
	classIdattrilw =classId ;
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.OrderString = gListPaginationAttr.orderBy;
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "A");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBALARM","B","A.OBJATTR_ID=B.OBJATTR_ID",JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ALARM","C","B.ALARM_ID=C.ALARM_ID",JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS","D","A.ATTRIB_ID=D.ATTRIB_ID",JetsenWeb.TableJoinType.Left));
	
	var queryConditoin = new JetsenWeb.SqlConditionCollection();	  
    var subConditon = new jetsennet.SqlCondition();  
	subConditon.SqlLogicType = jetsennet.SqlLogicType.And;  
	if(attrTypes)
	{
	    if(attrTypes == '103'){
	    	subConditon.SqlConditions.push(jetsennet.SqlCondition.create("A.ATTRIB_ID","40001", jetsennet.SqlLogicType.Or,jetsennet.SqlRelationType.Equal,jetsennet.SqlParamType.Numeric)); 
	    }
		subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("A.ATTRIB_TYPE", attrTypes, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric,true));

	}
	else
	{
		subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("A.ATTRIB_TYPE", "104,105,107", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric,true));
	}	
	queryConditoin.SqlConditions.push(subConditon);  
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("A.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	if(classId!=null && classId!="")
	{
		queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("D.CLASS_ID", classId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	
	JetsenWeb.extend(sqlQuery, { KeyId: "A.OBJATTR_ID", QueryTable: queryTable, ResultFields: "A.*,C.ALARM_ID,C.ALARM_NAME,C.IS_VALID" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	switch(attrTypes)
		{
			case "100":
				
				break;
			case "101,106":
				curObjAttrib[1] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				//获取配置信息
				$('insConfigDiv').innerHTML = "";
		        $('insConfigDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/configAttributelist.xslt", ret.resultVal);

		        gGridListAttr.bind($("insConfigDiv"), $("configattributeList"));
		        hideClassTypelw  = "101,106" ;
		        //gGridList.calcSize();
				break;
			case "102":
				curObjAttrib[2] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				 //获取监控信息
				$('insInspectDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/inspectAttributelist.xslt", ret.resultVal);
//		        var gGridList = new JetsenWeb.UI.GridList();
		        gGridListAttr.bind($("insInspectDiv"), $("inspectattributeList"));
		        hideClassTypelw  = "102" ;
				break;
			case "103":
				curObjAttrib[3] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				//获取性能指标信息
				$('insPerformDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/performAttributelist.xslt", ret.resultVal);
//		        var gGridList = new JetsenWeb.UI.GridList();
		        gGridListAttr.bind($("insPerformDiv"), $("performattributeList"));
		        hideClassTypelw  = "103" ;
				break;
			case "104":
				curObjAttrib[4] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				$('insTrapDiv').innerHTML = "";
				$('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/trapAttributelist.xslt", ret.resultVal);
//				var gGridList = new JetsenWeb.UI.GridList();
				gGridListAttr.bind($("insTrapDiv"), $("trapattributeList"));
		        hideClassTypelw  = "104" ;
				break;
			case "105":
				curObjAttrib[4] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				$('insTrapDiv').innerHTML = "";
				$('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/signalAttributelist.xslt", ret.resultVal);
//				var gGridList = new JetsenWeb.UI.GridList();
				gGridListAttr.bind($("insTrapDiv"), $("signalattributeList"));
		        hideClassTypelw  = "105" ;
				break;
			case "107":
				curObjAttrib[4] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				$('insTrapDiv').innerHTML = "";
				$('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/syslogAttributelist.xslt", ret.resultVal);
//				var gGridList = new JetsenWeb.UI.GridList();
				gGridListAttr.bind($("insTrapDiv"), $("syslogattributeList"));
		        hideClassTypelw  = "107" ;
				break;
			case "104,105,107":
				curObjAttrib[4] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				//获取Trap信息、信号信息、Syslog信息
				$('insTrapDiv').innerHTML = "";
				$('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/otherAttributelist.xslt", ret.resultVal);
//		        var gGridList = new JetsenWeb.UI.GridList();
		        gGridListAttr.bind($("insTrapDiv"), $("otherattributeList"));
		        hideClassTypelw  = "104,105,107" ;
				break;
			case "":
				curObjAttrib[4] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
				$('insTrapDiv').innerHTML = "";
				$('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/otherAttributelist.xslt", ret.resultVal);
//				var gGridList = new JetsenWeb.UI.GridList();
				gGridListAttr.bind($("insTrapDiv"), $("otherattributeList"));
		        hideClassTypelw  = "104,105,107" ;
				break;
			default:
				break;
		}
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//列出指定类型的属性
function listAttrib(attribTypes){
	var calssId = $('cbo_CLASS').value;
	var sqlQuery;
	var queryTable;
	if(attribTypes == '101,106' && calssId != '')
	{
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("B.CLASS_ID",calssId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("A.ATTRIB_TYPE",attribTypes,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric,true));
		
		sqlQuery = new JetsenWeb.SqlQuery();
		queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "A");
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS","B","A.ATTRIB_ID = B.ATTRIB_ID",JetsenWeb.TableJoinType.Left));
	}
	else
	{
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("C.PARENT_ID",curObjClassId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("A.ATTRIB_TYPE",attribTypes,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.Numeric,true));
		
		sqlQuery = new JetsenWeb.SqlQuery();
		queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBUTE", "A");
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIB2CLASS","B","A.ATTRIB_ID = B.ATTRIB_ID",JetsenWeb.TableJoinType.Left));
		queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS","C","B.CLASS_ID=C.CLASS_ID",JetsenWeb.TableJoinType.Left));
	}
	sqlQuery.OrderString = "ORDER BY A.ATTRIB_ID";
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "ATTRIB_ID",
		PageInfo : null,
		QueryTable : queryTable,
		ResultFields : "A.*"
	});
	sqlQuery.Conditions = condition;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(ret) {
		$('listAttribByType').innerHTML = "";
        $('listAttribByType').innerHTML = JetsenWeb.Xml.transformXML("xslt/selectattribute.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("listAttribByType"), $("selectattributeid"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex + "！");
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}

var currAttribTypes = null;
//显示属性列表
function showAttribList(attribTypes) {
	currAttribTypes = attribTypes;
	if(attribTypes == '102' || attribTypes == '103')
	{
		addAttributeType.options.length = 0;
		addAttributeType.options.add(new Option("一般方式", '1'));
		addAttributeType.options.add(new Option("求和", '2'));
		addAttributeType.options.add(new Option("平均", '3'));
		$('addTableType').style.display = "";
	}
	else
	{
		addAttributeType.options.length = 0;
		$('addTableType').style.display = "none";
	}
	
	var dialog = new JetsenWeb.UI.Window("show-ConfigAttrib-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 700, height: dialogHeight }, title: "第1步:选择属性" });
    dialog.controls = ["divShowListAttribByType"];
    dialog.onsubmit = function () {
    	var checkAttribId = JetsenWeb.Form.getCheckedValues("selectAttribute");
    	var addAttributeType = $('addAttributeType').value;
    	if(checkAttribId.length <= 0) 
    	{
    		jetsennet.alert("请选择要保存的属性！");
    		return;
    	}
    	if(addAttributeType && (addAttributeType == '2' || addAttributeType == '3'))
    	{
    		if(checkAttribId.length >1)
    		{
    			jetsennet.alert("只能选择一个属性！");
    			return;
    		}
    	}
    	
    	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.async = false;
        ws.oncallback = function (ret) {
        	showInstanceResult();
    		$('divInstanceResultTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/attribInsresult.xslt", ret.resultVal);
    		var gGridList = new JetsenWeb.UI.GridList();
            gGridList.bind($("divInstanceResultTable"), $("InstanceResultTable"));
        	instanceResult = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        	if(instanceResult == null || instanceResult.length == 0) {
        		jetsennet.alert("没有实例化结果！");
        		return;
        	}      	
        	JetsenWeb.UI.Windows.close("show-ConfigAttrib-win");
        };
        ws.onerror = function (ex) { jetsennet.alert("没有实例化结果！"); };
        ws.call("bmpInstanceAttribute", [curObjId, checkAttribId.join(",")]);
    }
    dialog.showDialog();
    listAttrib(attribTypes);
}

//显示其他类型的属性
function showOtherAttribList()
{
	addAttributeType.options.length = 0;
	$('addTableType').style.display = "none";
	var type = $('cbo_TYPE').value;
	
	if(type)
	{
		currAttribTypes = type;
		var dialog = new JetsenWeb.UI.Window("show-ConfigAttrib-win");
	    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 700, height: dialogHeight }, title: "第1步:选择属性" });
	    dialog.controls = ["divShowListAttribByType"];
	    dialog.onsubmit = function () {
	    	var checkAttribId = JetsenWeb.Form.getCheckedValues("selectAttribute");
	    	if(checkAttribId.length <= 0) 
	    	{
	    		jetsennet.alert("请选择要保存的属性！");
	    		return;
	    	}
	    	
	    	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	        ws.soapheader = JetsenWeb.Application.authenticationHeader;
	        ws.async = false;
	        ws.oncallback = function (ret) {
	    		$('divInstanceResultTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/attribInsresult.xslt", ret.resultVal);
	    		var gGridList = new JetsenWeb.UI.GridList();
	            gGridList.bind($("divInstanceResultTable"), $("InstanceResultTable"));
	        	instanceResult = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
	        	if(instanceResult == null || instanceResult.length == 0) {
	        		jetsennet.alert("没有实例化结果！");
	        		return;
	        	}
	        	showInstanceResult();
	        	JetsenWeb.UI.Windows.close("show-ConfigAttrib-win");
	        };
	        ws.onerror = function (ex) { jetsennet.alert("没有实例化结果！"); };
	        ws.call("bmpInstanceAttribute", [curObjId, checkAttribId.join(",")]);
	    }
	    dialog.showDialog();
	    listAttrib(type);
	}
	else
	{
		jetsennet.alert("请选择分类");
		return;
	}
}

//选择实例化的属性点击确定展示实例化结果
function showInstanceResult() {
	var dialog = new JetsenWeb.UI.Window("show-InstanceResult-win");
	JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 700, height: dialogHeight }, title: "第2步:选择对象属性" });
	dialog.controls = ["instanceResult"];
	dialog.onsubmit = function() {
		var checkObjAttrId = JetsenWeb.Form.getCheckedValues("checkInstanceResult");
		var addAttributeType = $('addAttributeType').value;
		if(checkObjAttrId.length==0) {
			jetsennet.alert("请选择要添加的对象属性！");
		}
		var flag = true;
		if(checkObjAttrId!=null && checkObjAttrId.length>0 && instanceResult!=null)
		{
			var objattrXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataSource>";
			if(addAttributeType && (addAttributeType == '2' || addAttributeType == '3'))
			{
				if(addAttributeType == '2')//求和方式
				{
					var attrib_param = "";
					var attrib_param_pre = "";
					var objattr_name = "";
					var selectAttribute = [];
					var mark = 0;
					for(var i=0; i<checkObjAttrId.length; i++)
					{
						var objattrId = checkObjAttrId[i];
						for(var j=0; j<instanceResult.length; j++)
						{
							if(instanceResult[j].OBJATTR_ID == objattrId)
							{
								flag = true;
								if($('isRepeat').checked){
									var array = curObjAttrib[curTabNum];
									for(var k = 0; k < array.length; k++){
										if(instanceResult[j].ATTRIB_PARAM == array[k].ATTRIB_PARAM &&
												instanceResult[j].OBJATTR_NAME == array[k].OBJATTR_NAME){
											flag = false;
											break;
										}
									}
								}
								if(flag){
									mark += 1;
									selectAttribute.push(instanceResult[j]);
									var _objattr_name = instanceResult[j].ATTRIB_PARAM;
									var index = _objattr_name.indexOf(":");
									attrib_param_pre = _objattr_name.substring(0, index + 1);
									_objattr_name = _objattr_name.substring(index + 1, _objattr_name.length);
									attrib_param = attrib_param + _objattr_name + "+";
								}
							}
						}
					}
					if(mark == 1)
					{
						attrib_param = attrib_param_pre + attrib_param.substring(0, attrib_param.length-1);
					}
					else
					{
						attrib_param = attrib_param_pre + "(" + attrib_param.substring(0, attrib_param.length-1) + ")";
					}
					
					for(var k=0; k<selectAttribute.length; k++)
					{
						objattr_name += selectAttribute[k].OBJATTR_NAME + ",";
					}
					
					objattr_name = objattr_name.substring(0, objattr_name.length - 1) + "求和";
					var temp = JetsenWeb.Xml.serializer(selectAttribute[0], "BMP_OBJATTRIB");
					temp = "<RecordSet>" + temp + "</RecordSet>";
					var tempObject = JetsenWeb.Xml.toObject(temp, "BMP_OBJATTRIB")[0];
					tempObject['ATTRIB_PARAM'] = attrib_param;
					tempObject['OBJATTR_NAME'] = objattr_name;
					objattrXml = objattrXml + JetsenWeb.Xml.serializer(tempObject, "BMP_OBJATTRIB");
				}
				else if(addAttributeType == '3')//求平均
				{

					var attrib_param = "";
					var attrib_param_pre = "";
					var objattr_name = "";
					var selectAttribute = [];
					var mark = 0;
					for(var i=0; i<checkObjAttrId.length; i++)
					{
						var objattrId = checkObjAttrId[i];
						for(var j=0; j<instanceResult.length; j++)
						{
							if(instanceResult[j].OBJATTR_ID == objattrId)
							{
								flag = true;
								if($('isRepeat').checked){
									var array = curObjAttrib[curTabNum - 1];
									for(var k = 0; k < array.length; k++){
										if(instanceResult[j].ATTRIB_PARAM == array[k].ATTRIB_PARAM &&
												instanceResult[j].OBJATTR_NAME == array[k].OBJATTR_NAME){
											flag = false;
											break;
										}
									}
								}
								if(flag){
									mark += 1;
									selectAttribute.push(instanceResult[j]);
									var _objattr_name =  instanceResult[j].ATTRIB_PARAM;
									var index = _objattr_name.indexOf(":");
									attrib_param_pre = _objattr_name.substring(0, index + 1);
									_objattr_name = _objattr_name.substring(index + 1, _objattr_name.length);
									attrib_param = attrib_param + _objattr_name + "+";
								}
							}
						}
					}
					if(mark == 1)
					{
						attrib_param = attrib_param_pre + "(" + attrib_param.substring(0, attrib_param.length-1) + "/" + selectAttribute.length + ")";
					}
					else
					{
						attrib_param = attrib_param_pre + "(" + "(" + attrib_param.substring(0, attrib_param.length-1) + ")" + "/" + selectAttribute.length + ")";
					}
					
					for(var k=0; k<selectAttribute.length; k++)
					{
						objattr_name += selectAttribute[k].OBJATTR_NAME + ",";
					}
					
					objattr_name = objattr_name.substring(0, objattr_name.length - 1) + "求平均";
					var temp = JetsenWeb.Xml.serializer(selectAttribute[0], "BMP_OBJATTRIB");
					temp = "<RecordSet>" + temp + "</RecordSet>";
					var tempObject = JetsenWeb.Xml.toObject(temp, "BMP_OBJATTRIB")[0];
					tempObject['ATTRIB_PARAM'] = attrib_param;
					tempObject['OBJATTR_NAME'] = objattr_name;
					objattrXml = objattrXml + JetsenWeb.Xml.serializer(tempObject, "BMP_OBJATTRIB");
				}
			}
			else
			{
				for(var i=0; i<checkObjAttrId.length; i++) {
					var objattrId = checkObjAttrId[i];
					
					for(var j=0; j<instanceResult.length; j++)
					{
						if(instanceResult[j].OBJATTR_ID == objattrId)
						{
							flag = true;
							if($('isRepeat').checked){
								var array = curObjAttrib[curTabNum - 1];
								for(var k = 0; k < array.length; k++){
									if(instanceResult[j].ATTRIB_PARAM == array[k].ATTRIB_PARAM &&
											instanceResult[j].OBJATTR_NAME == array[k].OBJATTR_NAME){
										flag = false;
										break;
									}
								}
							}
							if(flag){
								objattrXml = objattrXml + JetsenWeb.Xml.serializer(instanceResult[j], "BMP_OBJATTRIB");
							}
						}
					}
				}
			}
			objattrXml = objattrXml + "</DataSource>";
			saveInstanceResult(objattrXml);
		}
		
	}
	dialog.showDialog();
}

//选中实例化的结果点击确定把实例化结果存起来
function saveInstanceResult(objattrXml) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	if(currAttribTypes == '101,106')
    	{
    		onAttribClassChanged();
    	}
    	else
    	{
    		refreshObjAttrList(currAttribTypes);
    	}
    	
    	JetsenWeb.UI.Windows.close("show-InstanceResult-win");
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    ws.call("bmpInsertObjAttrib", [curObjClassId, objattrXml]);
}

//重新刷新对象属性列表
function refreshObjAttrList(attrType){
	if(attrType!=null)
	{
		switch(attrType)
		{
			case "100":
				$('insObjDiv11').innerHTML = "";
				getOAList();
				break;
			case "101,106":
				//获取配置信息
	            $('insConfigDiv').innerHTML = "";
	            onAttribClassChanged()
//	            getObjAttribute(curObjId, '101,106', '');
				break;
			case "102":
				 //获取监控信息
	            $('insInspectDiv').innerHTML = "";
	            getObjAttribute(curObjId, '102', '');
				break;
			case "103":
				//获取性能指标信息
	            $('insPerformDiv').innerHTML = "";
	            getObjAttribute(curObjId, '103', '');
				break;
			case "104":
				$('insTrapDiv').innerHTML = "";
	            getObjAttribute(curObjId, '104', '');
				break;
			case "105":
				$('insTrapDiv').innerHTML = "";
	            getObjAttribute(curObjId, '105', '');
				break;
			case "107":
				$('insTrapDiv').innerHTML = "";
	            getObjAttribute(curObjId, '107', '');
				break;
			case "104,105,107":
				//获取Trap信息、信号信息、Syslog信息
	            $('insTrapDiv').innerHTML = "";
	            getObjAttribute(curObjId, '104,105,107', '');
				break;
			default:
				break;
		}
	}
}

//获取监控信息
function getInspectInfomation(currentObjId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", 102, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.ATTRIB_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('insInspectDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/performAttributelist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("insInspectDiv"), $("inspectattributeList"));
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//获取性能指标信息
function getPerformImformation(currentObjId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", 103, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.ATTRIB_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('insPerformDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/performAttributelist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("insPerformDiv"), $("performattributeList"));
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//获取trap信息
function getTrapImformation(currentObjId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", 104, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.ATTRIB_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('insTrapDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/performAttributelist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("insTrapDiv"), $("trapattributeList"));
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//获取信号属性信息
function getSignalImformation(currentObjId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", 105, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.ATTRIB_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('insSignalDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/performAttributelist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("insSignalDiv"), $("signalattributeList"));
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//列出自定义属性信息
function getDefinedAttrib(currentObjId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", 100, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", currentObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(sqlQuery, { KeyId: "a.ATTRIB_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.Conditions = queryConditoin;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        $('insObjDiv').innerHTML = JetsenWeb.Xml.transformXML("xslt/ownAttributelist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("insObjDiv"), $("attributeListid"));
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//新建对象的时候添加对象和对象组之间的关系
function addObject2ObjectGroup(objId, GroupIds) {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpAddObj2ObjGroup", [objId + "_" + GroupIds]);
}

//select Item 删除方法
function selectOptionsDel(selCtrl)
{
    var _itemCount = selCtrl.options.length;
    var checkItemCount = 0;
    if (_itemCount>0)
    {
	    for(var i=_itemCount-1;i>=0;i--)
	    {
		    if(selCtrl.options[i].selected)
		    {	
			    selCtrl.removeChild(selCtrl.options[i]);
			    checkItemCount++;
		    }
	    }
	    if(checkItemCount == 0) {
	    	jetsennet.alert("请选择要删除的项！");
	    }
    }
}

//验证IP是否填写正确
function validateIP(ipstr) 
{
    var ipReg = new RegExp("^((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))\\.((25[0-5])|(2[0-4]\\d)|(1?\\d?\\d))$","g");
    return ipReg.test(ipstr);
}

function showGroupListAdd() {
	$('divGroupListTable').innerHTML = "";
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	JetsenWeb.extend(sqlQuery, { KeyId: "a.GROUP_ID", QueryTable: queryTable, ResultFields: "a.*" });
	sqlQuery.OrderString = "ORDER BY a.GROUP_TYPE";
	var queryConditoin = new JetsenWeb.SqlConditionCollection();
	queryConditoin.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_ID", getAllValues($("showGroupOption")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));
	var subCondition = JetsenWeb.SqlCondition.create();
	subCondition.SqlLogicType = JetsenWeb.SqlLogicType.And;
	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 0, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 1, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	subCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", 100, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	queryConditoin.SqlConditions.push(subCondition);
	sqlQuery.Conditions = queryConditoin;
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	$('divGroupListTable').innerHTML = "";
        $('divGroupListTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectgrouplistadd.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divGroupListTable"), $("tabFunction"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.GROUP_ID', '1']);

    var dialog = new JetsenWeb.UI.Window("show-groupsel-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 600, height: 400 }, title: "管理对象组信息" });
    dialog.controls = ["divGroupListPanel"];
    dialog.onsubmit = function () {
        var groupIdsStr = groupSelStr();
        addGroup2GroupDia(groupIdsStr);
        JetsenWeb.UI.Windows.close("show-groupsel-win");
    };
    dialog.showDialog();
}

//删除一个对象的时候删除该对象的对象属性
function delObjAttribByObjId(objId, flag) {
	if(!objId) {
		jetsennet.alert("该对象不存在！");
		return;
	}
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function (ret) {
		if('1' == flag) {
        	showObjectList(currentGroupId, currentGroupType);
        }else {
        	loadObjectList();
        }
	};
	ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("bmpDeleteObjAndRelation", [objId]);
}

//获取所有的设备厂商并添加到对话框中的下拉列表中
function getAllManufacturers() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_MANUFACTURERS", "a");
	JetsenWeb.extend(sqlQuery, { KeyId: "a.MAN_ID", QueryTable: queryTable, ResultFields: "a.*" });
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var manufacturers = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        var manufacSelect = $("manufacturer");
        if(manufacturers != null && manufacturers.length > 0) {
        	manufacSelect.options[0] = new Option("请选择","");
        	for(var i=0; i<manufacturers.length; i++) {
        		var obj = manufacturers[i];
        		manufacSelect.options.add(new Option(obj["MAN_NAME"], obj["MAN_ID"]));
        	}
        }
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//新建对象时获取设备厂商
function getManufactureByClassId(class_id) {
	if(!class_id) {
		return;
	}
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var man_id = ret.resultVal;
        if(man_id) {
        	setSelectedValue($("manufacturer"), man_id);
        }
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpGetMaunfactureByClassId", [class_id]);
}

//在新建自动发现对象时，如果选择的是数据库类型，则允许填写数据库
function showDateStage() {
	var class_id = getSelectedValue($("txt_CLASS_TYPE2"));
	for(var i=0; i<attrClsArray.length; i++) {
		if(attrClsArray[i]["CLASS_ID"] == class_id) {
			if(attrClsArray[i]["CLASS_TYPE"].startWith("DB")) {
				$("txt_DB_NAME1").disabled = false;
				$("txt_DB_NAME1").readOnly = false;
			}
		}
	}
}

var arraySubClass;
//查询子对象类别
function getSubClass(class_id)
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "aa");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Right));
	JetsenWeb.extend(sqlQuery, { KeyId: "aa.CLASS_ID", QueryTable: queryTable, ResultFields: "aa.CLASS_ID,aa.CLASS_NAME,aa.CLASS_DESC" });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("aa.CLASS_LEVEL", 2, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", class_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var arraySubClass = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        var classElement = $('selectSubClass');
        classElement.length = 0;
        classElement.options.add(new Option("请选择", -1));
        if(arraySubClass != null && arraySubClass != "" && arraySubClass.length != 0)
        {
        	for(var i=0; i<arraySubClass.length; i++)
            {
            	var object = arraySubClass[i];
            	classElement.options.add(new Option(object.CLASS_NAME, object.CLASS_ID));
            }
        }
        
        showSubObject();
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//展示子对象
function showSubObject()
{
	var class_id = getSelectedValue($("selectSubClass"));
	$('divProcListTable').innerHTML = "";
    var gSqlQuery = new JetsenWeb.SqlQuery();
    gSqlQuery.OrderString = "ORDER BY OBJ_ID";
    var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
    JetsenWeb.extend(gSqlQuery, { KeyId: "OBJ_ID", QueryTable: gQueryTable, ResultFields: "a.*,b.CLASS_NAME" });
    var gQueryConditions = new JetsenWeb.SqlConditionCollection();
    gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", curSubObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    if(class_id != -1)
    {
    	gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_ID", class_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    gSqlQuery.Conditions = gQueryConditions;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    //ws.async = false;
    ws.oncallback = function (ret) {
        $('divProcListTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/procobjlist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divProcListTable"), $("procobjtable"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

//删除子对象
function deleteSubElement(subObjectId)
{
	jetsennet.confirm("确定删除？", function ()  {
    	if(subObjectId == null || subObjectId == "")
    	{
    		jetsennet.alert("该对象不存在！");
    		return;
    	}
    	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    	ws.soapheader = JetsenWeb.Application.authenticationHeader;
    	ws.async = false;
    	ws.oncallback = function (ret) {
//    		showSubObject();
    		loadObjectListInClass(objParentIdlw,classNameLw,ObjClassTypeId,curSubObjId);
	    	refreshTreeChange();
    	};
    	ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    	ws.call("bmpDeleteObjAndRelation", [subObjectId]);
        return true;
    });
}

//获得对象的采集器
var objectCollId;
function getObjectCollId()
{
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	JetsenWeb.extend(sqlQuery, { KeyId: "c.GROUP_ID", QueryTable: queryTable, ResultFields: "c.NUM_VAL1" });
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "c.GROUP_ID = b.GROUP_ID", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "d", "c.NUM_VAL1 = d.COLL_ID", JetsenWeb.TableJoinType.Right));
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", curSubObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = queryConditions;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var collIds = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(collIds && collIds.length >0)
    	{
    		objectCollId = collIds[0]['NUM_VAL1'];
    	}
    	else
    	{
    		jetsennet.alert("无法得到采集器！");
    	}
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

function addSubObject()
{ 
	getObjectCollId();
	var subClassId = getSelectedValue($("selectSubClass"));
	if(subClassId == "-1")
	{
		jetsennet.alert("请选择类别！");
		return;
	}
	var dialog = new JetsenWeb.UI.Window("show-subObject-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 600, height: 380 }, title: "子对象信息" });
    dialog.controls = ["divSubObjectList"];
    dialog.onsubmit = function () {
    	var checkObjIds = JetsenWeb.Form.getCheckedValues("chkSubObjectName");
    	var requestString = "<RecordSet>";
    	if(checkObjIds && checkObjIds.length >0)
    	{
    		for(var i=0; i<checkObjIds.length; i++)
        	{
    			var index = checkObjIds[i];
    			requestString += "<Record>";
    			requestString += "<name>";
    			requestString += subObjects[index].name;
    			requestString += "</name>";
    			requestString += "<info>";
    			requestString += subObjects[index].info;
    			requestString += "</info>";
    			requestString += "</Record>";
        	}
    		requestString += "</RecordSet>";
    	}
    	else
    	{
    		jetsennet.alert("请选择子对象！");
    		return;
    	}
    	
    	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    	ws.soapheader = JetsenWeb.Application.authenticationHeader;
    	ws.async = false;
    	ws.oncallback = function (ret) {
    		showSubObject();
    		JetsenWeb.UI.Windows.close("show-subObject-win");
    	};
    	ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
    	var usrId = jetsennet.Application.userInfo.UserId;
    	ws.call("bmpInsSubObject", [requestString, curSubObjId, subClassId, objectCollId, usrId]);
    };
    dialog.showDialog();
	
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function (ret) {
		var xmlDoc = new JetsenWeb.XmlDoc();
		xmlDoc.loadXML(ret.resultVal);
		var nodes = xmlDoc.documentElement.selectNodes("Record");
		var index = 1;
		for(var i=0; i<nodes.length; i++)
		{
			var node = nodes[i];
			var nodeName = node.selectSingleNode("name").text;
			var nodeInfo = node.selectSingleNode("info").text;
			var o = new Object();
			o.name = nodeName;
			o.info = nodeInfo;
			subObjects[i + 1] = o;
			var newNode = xmlDoc.createElement("ID");
			var newNodeValue = xmlDoc.createTextNode(i + 1);
			newNode.appendChild(newNodeValue);
			node.appendChild(newNode);
		}
		$('divSubObjectListTable').innerHTML = JetsenWeb.Xml._transformXML("xslt/subObjectlist.xslt", xmlDoc);
		//$('divSubObjectListTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/subObjectlist.xslt", ret.resultVal);
		var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divSubObjectListTable"), $("subObjectList"));
	};
	ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("bmpGetSubObject", [curSubObjId, subClassId, objectCollId]);
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
		result = ret.resultVal;
	}
	ws.onerror = function(ex){jetsennet.error(ex);};
	ws.call("isAdmin",[userId]); 
	return result;
}

//把divTop调整成60
function retsetsize(){
    var objContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    objContent.addControl(new JetsenWeb.UI.PageItem("divElementList"));
    objContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));
    var objFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObj"), { splitType: 1, fixControlIndex: 0, splitBorder:5, showSplit: false });
    objFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size: { width: 0, height: 60} }));
    objFrame.addControl(objContent); 
    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType:0,fixControlIndex:0,splitBorder:0,showSplit:true, enableResize: true });
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAc"),{ size: { width: 230, height: 0} }));
    gFrame.addControl(objFrame);
    var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true });
    alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"),{ size: { width: 210, height: 0} }));
    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
    alarmContent.size = { width: 680, height: 280 };
    alarmContent.resize();  
    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}
//把divTop调整成60
function retsetsize(){
    var objContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    objContent.addControl(new JetsenWeb.UI.PageItem("divElementList"));
    objContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));
    var objFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObj"), { splitType: 1, fixControlIndex: 0, splitBorder:5, showSplit: false });
    objFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size: { width: 0, height: 60} }));
    objFrame.addControl(objContent); 
    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType:0,fixControlIndex:0,splitBorder:0,showSplit:true, enableResize: true });
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAc"),{ size: { width: 230, height: 0} }));
    gFrame.addControl(objFrame);
    var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true });
    alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"),{ size: { width: 210, height: 0} }));
    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
    alarmContent.size = { width: 680, height: 280 };
    alarmContent.resize();  
    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}
//把divTop调整成30
function retsetDefaultsize(){
    var objContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    objContent.addControl(new JetsenWeb.UI.PageItem("divElementList"));
    objContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));
    var objFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObj"), { splitType: 1, fixControlIndex: 0, splitBorder:5, showSplit: false });
    objFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size: { width: 0, height: 30} }));
    objFrame.addControl(objContent); 
    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType:0,fixControlIndex:0,splitBorder:0,showSplit:true, enableResize: true });
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAc"),{ size: { width: 230, height: 0} }));
    gFrame.addControl(objFrame);
    var alarmContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjAttrib2Alarm1"), { splitType: 0, fixControlIndex: 0, enableResize: true });
    alarmContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divAlarmListContent"),{ size: { width: 210, height: 0} }));
    alarmContent.addControl(new JetsenWeb.UI.PageItem("divAlarmDetail"));
    alarmContent.size = { width: 680, height: 280 };
    alarmContent.resize();  
    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}


function getAutoObjIpOne(obj) {	
	var sameIp = "";
	var strIp = "";
	var ip = obj.IP_ADDR;
	var classType = obj.CLASS_TYPE;
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.IP_ADDR", ip, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.String));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_TYPE", classType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.String));
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "a.OBJ_ID", PageInfo : null, ResultFields : "DISTINCT a.IP_ADDR",
		QueryTable : queryTable });
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(sResult) {
		sameIp = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		if(sameIp != null  && sameIp != "" && sameIp.length != 0){
			for ( var i = 0; i < sameIp.length; i++) {
				 strIp += valueOf(sameIp[i], "IP_ADDR","")+",";
			}
			strIp = strIp.substring(0,strIp.length-1);
		}
		else{
			strIp = "";
			}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.OBJ_ID', '2']);
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	return strIp ;
}

function getAutoObjIp(objarraydis) {
	var sameIp = "";
	var strIp = "";
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE = b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	var condition = new jetsennet.SqlConditionCollection(); 
    for(var i=0,len = objarraydis.length;i<len;i++){
    	var ip = objarraydis[i].IP;
    	var className = objarraydis[i].CLASS_NAME;
    	var conditioni = new JetsenWeb.SqlCondition();
    	conditioni.SqlLogicType = jetsennet.SqlLogicType.Or;
    	conditioni.SqlConditions=[];
    	conditioni.SqlConditions.push(JetsenWeb.SqlCondition.create("a.IP_ADDR", ip, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));;
    	conditioni.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_NAME", className, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));	 
    	condition.SqlConditions.push(conditioni);
    }
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "a.OBJ_ID", PageInfo : null, ResultFields : "DISTINCT a.IP_ADDR",
		QueryTable : queryTable });
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(sResult) {
		sameIp = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		if(sameIp != null  && sameIp != "" && sameIp.length != 0){
			for ( var i = 0; i < sameIp.length; i++) {
				 strIp += valueOf(sameIp[i], "IP_ADDR","")+",";
			}
			strIp = strIp.substring(0,strIp.length-1);
		}
		else{
			strIp = "";
			}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.OBJ_ID', '2']);
//	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	return strIp ;
}



