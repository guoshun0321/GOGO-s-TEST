JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
//对象组控件
pageGroup = new JetsenWeb.UI.PageBar("pageGroup");
pageGroup.orderBy = "ORDER BY GROUP_ID";
pageGroup.changePageSize(100);
pageGroup.onpagechange = function () { getSelectGroupData(); }
pageGroup.onupdate = function () {
    $('divSelectGroupPage').innerHTML = this.generatePageControl();
}

var groupGridList = new JetsenWeb.UI.GridList();
groupGridList.ondatasort = function (sortfield, desc) {
    pageGroup.setOrderBy(sortfield, desc);
}
//对象控件
pageObj = new JetsenWeb.UI.PageBar("pageObj");
pageObj.orderBy = "ORDER BY OBJ_ID";
pageObj.changePageSize(100);
pageObj.onpagechange = function () { getSelectObjData(); }
pageObj.onupdate = function () {
    $('divSelectObjPage').innerHTML = this.generatePageControl();
}

var objGridList = new JetsenWeb.UI.GridList();
objGridList.ondatasort = function (sortfield, desc) {
	pageObj.setOrderBy(sortfield, desc);
}
//对象属性控件
pageAttr = new JetsenWeb.UI.PageBar("pageAttr");
pageAttr.orderBy = "ORDER BY OBJATTR_ID";
pageAttr.changePageSize(100);
pageAttr.onpagechange = function () { getSelectAttrData(); }
pageAttr.onupdate = function () {
    $('divSelectAttrPage').innerHTML = this.generatePageControl();
}

var attrGridList = new JetsenWeb.UI.GridList();
attrGridList.ondatasort = function (sortfield, desc) {
    pageAttr.setOrderBy(sortfield, desc);
}

//查询报表
function searchReport() {
	var reportProPath;
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	reportProPath = ret.value;
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("getReportProPath");
    
    document.getElementById("report1Info").reportSrc = reportProPath + "/alarmDataCount.jsp?flag=1";
	//var url = "../../../nmpreport/alarmDataCount.jsp?flag=1";
	var selectGroup = $("showGroupOption");
//	var selectObj = $("showObjOption");
//	var selectAttr = $("showAttrOption");
	var lenGroup = selectGroup.options.length;
//    var lenObj = selectObj.options.length;
//    var lenAttr = selectAttr.options.length;
     
    if(lenGroup > 0){
    	var groupStr = "";
    	for (var i = 0; i < lenGroup; i++) {
    		groupStr += selectGroup.options[i].value + ',';
    	}
    	document.getElementById("report1Info").groupIds = groupStr.substring(0, groupStr.length-1);
    }else{
    	JetsenWeb.alert("请选择对象组！");
    	return;
    } 
       
//    if(lenObj > 0){
//    	var objStr = "";
//    	for (var i = 0; i < lenObj; i++) {
//    		objStr += selectObj.options[i].value + ',';
//    	}
//        document.getElementById("report1Info").objIds = objStr.substring(0, objStr.length-1);
//    }else{
//    	JetsenWeb.alert("请选择对象！");
//    	return;
//    }
//    
//    if(lenAttr > 0){
//    	var atrrStr = "";
//    	for (var i = 0; i < lenAttr; i++) {
//    		atrrStr += selectAttr.options[i].value + ',';
//    	}
//    	document.getElementById("report1Info").objAttrIds = atrrStr.substring(0, atrrStr.length-1);
//    }else{
//    	JetsenWeb.alert("请选择对象属性！");
//    	return;
//    }
//    
    if ($("txtSDate").value != "" && $("txtSTime").value != "" 
    	&& $("txtEDate").value != "" && $("txtETime").value != "") {
    	var startTimeStr = parseDate($("txtSDate").value + " " + $("txtSTime").value).getTime();
    	var endTimeStr = parseDate($("txtEDate").value + " " + $("txtETime").value).getTime();
    	if(startTimeStr > endTimeStr){
    		JetsenWeb.alert("请输入正确的查询时间，开始时间不能大于结束时间！");
    		return;
    	}
		document.getElementById("report1Info").startTime = startTimeStr;
		document.getElementById("report1Info").endTime = endTimeStr;
	}else{
		JetsenWeb.alert("请选择时间！");
		return;
	}
    
    document.getElementById("report1Info").src = "alarmDataCountPost.htm";
}

// 初始化===================================================================================
function pageInit() {
	parent.parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		splitTitle : "divListTitle",
		splitSize : 27
	});
	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divReport1List"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem(
			"divBottom"), {
		size : {
			width : 0,
			height : 30
		}
	}));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), {
		size : {
			width : 0,
			height : 30
		}
	}));
	gFrame.addControl(frameContent);

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();

	var endTime = new Date();
	var startTime = new Date(endTime.getTime() - 7200000);
	$("txtEDate").value = endTime.toDateString();
	$("txtETime").value = endTime.toTimeString();
	$("txtSDate").value = startTime.toDateString();
	$("txtSTime").value = startTime.toTimeString();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

function setParam(){
	var dialog = new JetsenWeb.UI.Window("set-divParam-win");
	//var areaElements = JetsenWeb.Form.getElements('divParam');
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 200 }, title: "设置条件" });
    dialog.controls = ["divParam"];
    dialog.onsubmit = function () {
    	//this.close();
    	var flag = 0;
    	var groupOption = $("showGroupOption");
    	if(groupOption.length <= 0){
    		groupOption.style.borderColor = "rgb(153, 51, 102)";
    		flag++;
    	}else{
    		groupOption.style.borderColor = "";
    	}
//    	var objOption = $("showObjOption");
//    	if(objOption.length <= 0){
//    		objOption.style.borderColor = "rgb(153, 51, 102)";
//    		flag++;
//    	}else{
//    		objOption.style.borderColor = "";
//    	}
//    	var attrOption = $("showAttrOption");
//    	if(attrOption.length <= 0){
//    		attrOption.style.borderColor = "rgb(153, 51, 102)";
//    		flag++;
//    	}else{
//    		attrOption.style.borderColor = "";
//    	}
    	if(flag == 0){
    		JetsenWeb.alert("设置成功！");
    		this.close();
    	}
    };
    dialog.showDialog();
}

//添加对象组
function groupAdd(){
	searchSelectGroupData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-group"), { title: "选择对象组", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectGroup"];
    dialog.onsubmit = function () {
        var obj = document.getElementsByName("chk_SelectGroup");
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked)
                AddGroupItem(obj[i].value, obj[i].getAttribute("itemName"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddGroupItem(groupID, groupName) {
	var select = $("showGroupOption");
    var len = select.options.length;
    var objNewOption = document.createElement("option");
    select.options.add(objNewOption);
    objNewOption.value = groupID;
    objNewOption.innerHTML = groupName;       
}
function searchSelectGroupData() {
    pageGroup.currentPage = 1;
    getSelectGroupData();
}
function getSelectGroupData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "GROUP_ID", PageInfo: pageGroup, ResultFields: "",
        QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_OBJGROUP" })
    });

    sqlQuery.OrderString = pageGroup.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    if($("groupChecked").value != ""){
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", $("groupChecked").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.String));
	}else{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", "1,3,4,0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
	}
    if ($("txtGroupName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_NAME", $("txtGroupName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    //去除已选择的组
    var selectGroup = $("showGroupOption");
    var lenGroup = selectGroup.options.length;
    if(lenGroup > 0){
    	var str = "";
	    for (var i = 0; i < lenGroup; i++) {
	    	str += selectGroup.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }   
    sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectGroupList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmDataCountGroup.xslt", ret.resultVal);
        groupGridList.bind($('divSelectGroupList'), $('tabSelectGroup'));
        pageGroup.setRowCount($('hid_GroupCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'GROUP_ID', '1']);
}
//添加对象
function objAdd(){
	groupNameInit();
	searchSelectObjData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-obj"), { title: "选择对象", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectObj"];
    dialog.onsubmit = function () {
        var obj = document.getElementsByName("chk_SelectObj");
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked)
                AddObjItem(obj[i].value, obj[i].getAttribute("itemName"), obj[i].getAttribute("fatherID"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddObjItem(objID, objName, fatherID) {
	var select = $("showObjOption");
    var len = select.options.length;
    var objNewOption = document.createElement("option");
    select.options.add(objNewOption);
    objNewOption.value = objID;
    objNewOption.setAttribute('fatherID', fatherID);
    objNewOption.innerHTML = objName;
}
function searchSelectObjData() {
    pageObj.currentPage = 1;
    getSelectObjData();
}
function getSelectObjData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var ObjQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	ObjQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID=b.OBJ_ID  or a.PARENT_id=b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	ObjQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "c", "b.GROUP_ID=c.GROUP_ID", JetsenWeb.TableJoinType.Inner));
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "a.OBJ_ID", PageInfo: pageObj, ResultFields: "a.OBJ_ID, a.OBJ_NAME, c.GROUP_ID, c.GROUP_NAME",
        QueryTable: ObjQueryTable
    });

    sqlQuery.OrderString = pageObj.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    //获取已选择组所属对象
    var selectGroup = $("showGroupOption");
    var lenGroup = selectGroup.options.length;
    if(lenGroup > 0){
    	var str = "";
    	for(var i = 0; i < lenGroup; i++){
    		str += selectGroup.options[i].value + ",";
    	}
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
    }else{
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.String));
    }
    
    if ($("txtObjName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_NAME", $("txtObjName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if ($("get_groupName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_ID", $("get_groupName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    }
    //去除已选择的对象
    var selectObj = $("showObjOption");
    var lenObj = selectObj.options.length;
    if(lenObj > 0){
    	var str = "";
	    for (var i = 0; i < lenObj; i++) {
	    	str += selectObj.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectObjList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmDataCountObj.xslt", ret.resultVal);
        objGridList.bind($('divSelectObjList'), $('tabSelectObj'));
        pageObj.setRowCount($('hid_ObjCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//添加对象属性
function attrAdd(){
	searchSelectAttrData();
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-attr"), { title: "选择对象属性", submitBox: true, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: true, minimizeBox: true });
    dialog.controls = ["divSelectAttr"];
    dialog.onsubmit = function () {
        var attr = document.getElementsByName("chk_SelectAttr");
        for (var i = 0; i < attr.length; i++) {
            if (attr[i].checked)
                AddAttrItem(attr[i].value, attr[i].getAttribute("itemName"), attr[i].getAttribute("fatherID"));
        }
        return true;
    };
    dialog.showDialog();
}
function AddAttrItem(attrID, attrName, fatherID) {
	var select = $("showAttrOption");
    var len = select.options.length;
    var attrNewOption = document.createElement("option");
    select.options.add(attrNewOption);
    attrNewOption.value = attrID;
    attrNewOption.setAttribute('fatherID', fatherID);
    attrNewOption.innerHTML = attrName;
}
function searchSelectAttrData() {
    pageAttr.currentPage = 1;
    getSelectAttrData();
}
function getSelectAttrData(){
	var sqlQuery = new JetsenWeb.SqlQuery();
	var attrQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "a");
	attrQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "b", "a.OBJ_ID=b.OBJ_ID", JetsenWeb.TableJoinType.Left));
	
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId: "a.OBJATTR_ID", PageInfo: pageAttr, ResultFields: "a.OBJATTR_ID, a.OBJATTR_NAME, b.OBJ_ID, b.OBJ_NAME",
        QueryTable: attrQueryTable
    });

    sqlQuery.OrderString = pageAttr.orderBy;

    var condition = new JetsenWeb.SqlConditionCollection();
    //取已选择对象的属性
    var selectObj = $("showObjOption");
    var lenObj = selectObj.options.length;
    if(lenObj > 0){
    	var str = "";
    	for(var i=0; i < lenObj; i++){
    		str += selectObj.options[i].value + ",";
    	}
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
    }else{
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.String));
    }
    
    if ($("txtAttrName").value != "") {
        condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJATTR_NAME", $("txtAttrName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ATTRIB_TYPE", "102,103,104,105,107", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
    //去除已选择的属性
    var selectAttr = $("showAttrOption");
    var lenAttr = selectAttr.options.length;
    if(lenAttr > 0){
    	var str = "";
	    for (var i = 0; i < lenAttr; i++) {
	    	str += selectAttr.options[i].value + ",";
	    }
	    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJATTR_ID", str.substring(0, str.length-1), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.String));
    }
    
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $('divSelectAttrList').innerHTML = JetsenWeb.Xml.transformXML("xslt/alarmDataCountAttr.xslt", ret.resultVal);
        attrGridList.bind($('divSelectAttrList'), $('tabSelectAttr'));
        pageAttr.setRowCount($('hid_AttrCount').value);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
//删除选项
function selectOptionsDel(selCtrl)
{
    var itemCount = selCtrl.options.length;
    var selectedItemCount = 0;
    if (itemCount>0)
    {
    	//删除组的时候要同时删除其所属的对象和对象属性
    	if(selCtrl.id == 'showGroupOption'){
    		for(var i=itemCount-1;i>=0;i--)
		    {
			    if(selCtrl.options[i].selected)
			    {	
			    	selectOptionsDelByFather($('showObjOption'), selCtrl.options[i].value);
				    selCtrl.removeChild(selCtrl.options[i]);
				    selectedItemCount++;
			    }
		    }
    	}
    	//删除对象的时候要同时删除其所属的属性
    	if(selCtrl.id == 'showObjOption'){
    		for(var i=itemCount-1;i>=0;i--)
		    {
			    if(selCtrl.options[i].selected)
			    {	
			    	selectOptionsDelByFather($('showAttrOption'), selCtrl.options[i].value);
				    selCtrl.removeChild(selCtrl.options[i]);
				    selectedItemCount++;
			    }
		    }
    	}else{
		    for(var i=itemCount-1;i>=0;i--)
		    {
			    if(selCtrl.options[i].selected)
			    {	
				    selCtrl.removeChild(selCtrl.options[i]);
				    selectedItemCount++;
			    }
		    }
	    }
    }
    if(selectedItemCount == 0) {
    	jetsennet.alert("请选择要删除的项！");
    }
}
function selectOptionsDelByFather(selCtrl, fatherID){
	var len = selCtrl.options.length;
	if(selCtrl.id == 'showObjOption'){
		for(var i = len-1; i >= 0; i--){
			if(selCtrl.options[i].getAttribute('fatherID') == fatherID){
				selectOptionsDelByFather($('showAttrOption'), selCtrl.options[i].value);
				selCtrl.removeChild(selCtrl.options[i]);
			}
		}
	}else{
		for(var i = len-1; i >= 0; i--){
			if(selCtrl.options[i].getAttribute('fatherID') == fatherID){
				selCtrl.removeChild(selCtrl.options[i]);
			}
		}
	}
}


//初始化对象组名称
function groupNameInit() {
	var selectGroup = $("showGroupOption");
	var lenGroup = selectGroup.options.length;
    if(lenGroup > 0){
		var groupStr = "";
		for (var i = 0; i < lenGroup; i++) {
			groupStr += selectGroup.options[i].value + ',';
		}
		var groupIds = groupStr.substring(0, groupStr.length-1);
		var gSqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
		var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("GROUP_ID", groupIds, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.In,JetsenWeb.SqlParamType.String));

		JetsenWeb.extend(gSqlQuery, {
			IsPageResult : 0,
			KeyId : "GROUP_ID",
			QueryTable : gQueryTable,
			Conditions : condition,
			ResultFields : "GROUP_ID,GROUP_NAME"
		});
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			var getGroupName = $("get_groupName");
			getGroupName.length = 0;
			getGroupName.options.add(new Option("请选择", ""));

			var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
			if (records) {
				var length = records.length;
				for ( var i = 0; i < length; i++) {
					var groupInfo = records[i];
					getGroupName.options.add(new Option(
							groupInfo["GROUP_NAME"],
							groupInfo["GROUP_ID"]));
				}
			}
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
	} else {
		var cboAttribute = $("get_groupName");
		cboAttribute.length = 0;
		cboAttribute.options.add(new Option("请选择", ""));
	}
}

