JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_timeeditor", "js_datepicker"]);
var gFrame;
var gWindowSizeChangedInterVal;
//左侧端口控件
var gLeftObjectPage = new JetsenWeb.UI.PageBar("LeftObject");
gLeftObjectPage.onpagechange = function () { loadLeftObject(); };
gLeftObjectPage.orderBy = "ORDER BY OBJ_ID";
gLeftObjectPage.onupdate = function () 
{
    $('divLeftObjectPage').innerHTML = this.generatePageControl();
};
var gLeftObjectCondition = new JetsenWeb.SqlConditionCollection();
var gLeftGridList = new JetsenWeb.UI.GridList();
gLeftGridList.ondatasort = function (sortfield, desc) 
{
    gLeftObjectPage.setOrderBy(sortfield, desc);
};
var gLeftSqlQuery = new JetsenWeb.SqlQuery();
var gLeftQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "");
JetsenWeb.extend(gLeftSqlQuery, { IsPageResult: 0, KeyId: "OBJ_ID", PageInfo: gLeftObjectPage, QueryTable: gLeftQueryTable, ResultFields: "OBJ_ID, OBJ_NAME, IP_ADDR"});
//右侧端口控件
var gRightObjectPage = new JetsenWeb.UI.PageBar("RightObject");
gRightObjectPage.onpagechange = function () { loadRightObject(); };
gRightObjectPage.orderBy = "ORDER BY OBJ_ID";
gRightObjectPage.onupdate = function () 
{
    $('divRightObjectPage').innerHTML = this.generatePageControl();
};
var gRightObjectCondition = new JetsenWeb.SqlConditionCollection();
var gRightGridList = new JetsenWeb.UI.GridList();
gRightGridList.ondatasort = function (sortfield, desc) 
{
    gRightObjectPage.setOrderBy(sortfield, desc);
};
var gRightSqlQuery = new JetsenWeb.SqlQuery();
var gRightQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "");
JetsenWeb.extend(gRightSqlQuery, { IsPageResult: 0, KeyId: "OBJ_ID", PageInfo: gRightObjectPage, QueryTable: gRightQueryTable, ResultFields: "OBJ_ID, OBJ_NAME, IP_ADDR"});
//端口连接控件
var gP2pObjectPage = new JetsenWeb.UI.PageBar("P2pObject");
gP2pObjectPage.onpagechange = function () { loadP2pObject(); };
gP2pObjectPage.orderBy = "";
gP2pObjectPage.onupdate = function () 
{
    $('divP2pObjectPage').innerHTML = this.generatePageControl();
};
var gP2pObjectCondition1 = new JetsenWeb.SqlConditionCollection();
var gP2pObjectCondition2 = new JetsenWeb.SqlConditionCollection();
var gP2pGridList = new JetsenWeb.UI.GridList();
gP2pGridList.ondatasort = function (sortfield, desc) 
{
    gP2pObjectPage.setOrderBy(sortfield, desc);
};
//gP2pSqlQuery由gP2pSqlQuery1 union gP2pSqlQuery2得来
var gP2pSqlQuery = new JetsenWeb.SqlQuery();
JetsenWeb.extend(gP2pSqlQuery, { IsPageResult: 1, KeyId: "ID", PageInfo: gP2pObjectPage});

var gP2pSqlQuery1 = new JetsenWeb.SqlQuery();
var gP2pQueryTable1 = JetsenWeb.createQueryTable("BMP_PORT2PORT", "a");
gP2pQueryTable1.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "b", "a.PORTA_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable1.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "c", "b.PARENT_ID = c.OBJ_ID OR b.OBJ_ID = c.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable1.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "d", "a.PORTB_ID = d.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable1.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "e", "d.PARENT_ID = e.OBJ_ID OR d.OBJ_ID = e.OBJ_ID", JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gP2pSqlQuery1, { QueryTable: gP2pQueryTable1 , ResultFields : "a.ID, a.REL_TYPE, b.OBJ_NAME as PORTA_NAME, b.IP_ADDR as PORTA_IP, c.OBJ_NAME PORTA_PARENT, d.OBJ_NAME as PORTB_NAME, d.IP_ADDR as PORTB_IP, e.OBJ_NAME as PORTB_PARENT"});

var gP2pSqlQuery2 = new JetsenWeb.SqlQuery();
var gP2pQueryTable2 = JetsenWeb.createQueryTable("BMP_PORT2PORT", "a");
gP2pQueryTable2.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "b", "a.PORTA_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable2.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "c", "b.PARENT_ID = c.OBJ_ID OR b.OBJ_ID = c.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable2.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "d", "a.PORTB_ID = d.OBJ_ID", JetsenWeb.TableJoinType.Inner));
gP2pQueryTable2.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "e", "d.PARENT_ID = e.OBJ_ID OR d.OBJ_ID = e.OBJ_ID", JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gP2pSqlQuery2, { QueryTable: gP2pQueryTable2 , ResultFields : "a.ID, a.REL_TYPE, d.OBJ_NAME as PORTA_NAME, d.IP_ADDR as PORTA_IP, e.OBJ_NAME as PORTA_PARENT, b.OBJ_NAME as PORTB_NAME, b.IP_ADDR as PORTB_IP, c.OBJ_NAME PORTB_PARENT"});

function pageInit() 
{
    parent.document.getElementById("spanWindowName").innerHTML = document.title;
    
    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitSize: 0  });
    //左侧端口列表
    var gLeftFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divLeftPageFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitTitle: "divLeftListTitle", splitSize: 27 });
    var frameLeftContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divLeftContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameLeftContent.addControl(new JetsenWeb.UI.PageItem("divLeftObjectList"));
    frameLeftContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divLeftBottom"), { size: { width: 0, height: 0} }));
    gLeftFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divLeftTop"), { size: { width: 0, height:30} }));
    gLeftFrame.addControl(frameLeftContent);
    gLeftFrame.size = {width:603, height: 300};
    //右侧端口列表
    var gRightFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divRightPageFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitTitle: "divRightListTitle", splitSize: 27 });
    var frameRightContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divRightContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameRightContent.addControl(new JetsenWeb.UI.PageItem("divRightObjectList"));
    frameRightContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divRightBottom"), { size: { width: 0, height: 0} }));
    gRightFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divRightTop"), { size: { width: 0, height:30} }));
    gRightFrame.addControl(frameRightContent);
    gRightFrame.size = {width:603, height: 300};
    //上
    var gUpFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divUpPageFrame"), { splitType: 0, splitSize: 4, splitBorder: 0, enableResize: false, fixControlIndex: 0, hideSize: 1, hideButtonStyle: 1 });
    gUpFrame.addControl(gLeftFrame);
    gUpFrame.addControl(gRightFrame);
    gUpFrame.size = {width:0, height: 300};
    //下
    var gP2pFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divP2pPageFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitTitle: "divP2pListTitle", splitSize: 27 });
    var frameP2pContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divP2pContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameP2pContent.addControl(new JetsenWeb.UI.PageItem("divP2pObjectList"));
    frameP2pContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divP2pBottom"), { size: { width: 0, height: 30} }));
    gP2pFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divP2pTop"), { size: { width: 0, height:0} }));
    gP2pFrame.addControl(frameP2pContent);
    
    var groupDownFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divgroupDownFrame"), { splitType: 1, fixControlIndex: 0, enableResize: false, splitSize: 30 ,splitTitle: "leftSplitRight" });
    groupDownFrame.addControl(gUpFrame);
    groupDownFrame.addControl(gP2pFrame);
    
    gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divGroup"), { size: { width: 0, height:30} }));
    gFrame.addControl(groupDownFrame);
    
    window.onresize = function () 
    {
       if (gWindowSizeChangedInterVal != null)
       {
           window.clearTimeout(gWindowSizeChangedInterVal);
       }
       gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
    loadGroupSelect('sel_Group');
    loadDevSelect('sel_LeftFather');
    loadDevSelect('sel_RightFather');
    searchLeftObject();			
    searchRightObject();
    searchP2pObject();
    $('divUpPageFrame_split').style.backgroundColor = 'white';
}

function windowResized()
{
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

//加载左侧端口列表
function loadLeftObject() 
{
    gLeftSqlQuery.OrderString = gLeftObjectPage.orderBy;
    gLeftSqlQuery.Conditions = gLeftObjectCondition;     
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) 
    {
        $("divLeftObjectList").innerHTML = JetsenWeb.Xml.transformXML("xslt/portmanageleftobject.xslt", ret.resultVal);
        gLeftGridList.bind($("divLeftObjectList"), $("tabLeftObject"));
//        gLeftObjectPage.setRowCount($("hid_LeftObjectCount").value);
        $('portmanage-tabLeftObject-div-body').style.overflowX = 'hidden';
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gLeftSqlQuery.toXml()]);
}

function searchLeftObject()
{
    gLeftObjectCondition.SqlConditions = [];
    if($('sel_LeftFather').value == ''){
    	$("divLeftObjectList").innerHTML = "";
    	return;
    }else{
    	var subConditon = new jetsennet.SqlCondition();  
    	subConditon.SqlLogicType = jetsennet.SqlLogicType.Or;  
    	subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", $('sel_LeftFather').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));  
    	subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", 10063, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric)); 
    	
    	gLeftObjectCondition.SqlConditions.push(subConditon);
    	gLeftObjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJ_ID", $('sel_LeftFather').value, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    gLeftObjectPage.currentPage = 1;
    loadLeftObject();
}
//加载右侧端口列表
function loadRightObject() 
{
    gRightSqlQuery.OrderString = gRightObjectPage.orderBy;
    gRightSqlQuery.Conditions = gRightObjectCondition;     
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) 
    {
        $("divRightObjectList").innerHTML = JetsenWeb.Xml.transformXML("xslt/portmanagerightobject.xslt", ret.resultVal);
        gRightGridList.bind($("divRightObjectList"), $("tabRightObject"));
//        gRightObjectPage.setRowCount($("hid_RightObjectCount").value);
        $('portmanage-tabRightObject-div-body').style.overflowX = 'hidden';
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gRightSqlQuery.toXml()]);
}

function searchRightObject()
{
    gRightObjectCondition.SqlConditions = [];
    if($('sel_RightFather').value == ''){
    	$("divRightObjectList").innerHTML = "";
    	return;
    }else{
    	var subConditon = new jetsennet.SqlCondition();  
    	subConditon.SqlLogicType = jetsennet.SqlLogicType.Or;  
    	subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", $('sel_RightFather').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));  
    	subConditon.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID", 10063, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric)); 
    	
    	gRightObjectCondition.SqlConditions.push(subConditon);
    	gRightObjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJ_ID", $('sel_RightFather').value, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    gRightObjectPage.currentPage = 1;
    loadRightObject();
}
//加载端口连接列表
function loadP2pObject() 
{
	gP2pSqlQuery1.OrderString = "";
    gP2pSqlQuery1.Conditions = gP2pObjectCondition1;
    
    gP2pSqlQuery2.OrderString = "";
    gP2pSqlQuery2.Conditions = gP2pObjectCondition2;
    
    gP2pSqlQuery1.UnionQuery = new JetsenWeb.UnionQuery(gP2pSqlQuery2,JetsenWeb.QueryUnionType.Union);
	
    gP2pSqlQuery.OrderString = gP2pObjectPage.orderBy;
    gP2pSqlQuery.QueryTable = JetsenWeb.extend(
			new JetsenWeb.QueryTable(), {
				TableName : gP2pSqlQuery1.toXml(),
				AliasName : "p2p"
			});
	gP2pSqlQuery.ResultFields = "*";
    
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) 
    {
        $("divP2pObjectList").innerHTML = JetsenWeb.Xml.transformXML("xslt/portmanagep2p.xslt", ret.resultVal);
        gP2pGridList.bind($("divP2pObjectList"), $("tabP2pObject"));
        gP2pObjectPage.setRowCount($("hid_P2pObjectCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gP2pSqlQuery.toXml()]);
}

function searchP2pObject()
{
	var left = $('sel_LeftFather').value;
	if(left == ""){
		$("divP2pObjectList").innerHTML = "";
		return;
	}
	var right = $('sel_RightFather').value;
	if(right == ""){
		$("divP2pObjectList").innerHTML = "";
		return;
	}
    gP2pObjectCondition1.SqlConditions = [];
    gP2pObjectCondition1.SqlConditions.push(JetsenWeb.SqlCondition.create("c.OBJ_ID", left, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gP2pObjectCondition1.SqlConditions.push(JetsenWeb.SqlCondition.create("e.OBJ_ID", right, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gP2pObjectCondition2.SqlConditions = [];
    gP2pObjectCondition2.SqlConditions.push(JetsenWeb.SqlCondition.create("c.OBJ_ID", right, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gP2pObjectCondition2.SqlConditions.push(JetsenWeb.SqlCondition.create("e.OBJ_ID", left, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gP2pObjectPage.currentPage = 1;
    loadP2pObject();
}
//加载网段下拉框
function loadGroupSelect(selId){
	var sel = $(selId);
	sel.options.length = 0;
	sel.options[0] = new Option("请选择","");
	sel.options[0].title = "请选择";
	
	var groupSqlQuery = new JetsenWeb.SqlQuery();
	var groupQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP");
	JetsenWeb.extend(groupSqlQuery, { IsPageResult: 0, KeyId: "GROUP_ID", QueryTable: groupQueryTable, ResultFields: "GROUP_ID, GROUP_NAME" });

    var groupCondition = new JetsenWeb.SqlConditionCollection();
    groupCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", 4, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.Int));
    groupSqlQuery.Conditions = groupCondition;
  
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var result = JetsenWeb.xml.toObject(ret.resultVal,'Record');
    	for(var i = 0; i < result.length; i++){
    		sel.options[i+1] = new Option(result[i].GROUP_NAME,result[i].GROUP_ID);
    		sel.options[i+1].title = result[i].GROUP_NAME;
    	}
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [groupSqlQuery.toXml()]);
}
function loadDev(){
	var selLeft = $('sel_LeftFather');
	selLeft.options.length = 0;
	selLeft.options[0] = new Option("请选择","");
	var selRight = $('sel_RightFather');
	selRight.options.length = 0;
	selRight.options[0] = new Option("请选择","");
	
	$("divLeftObjectList").innerHTML = "";
	$("divRightObjectList").innerHTML = "";
	$("divP2pObjectList").innerHTML = "";
	
	loadDevSelect('sel_LeftFather');
    loadDevSelect('sel_RightFather');
}
//加载设备下拉框
function loadDevSelect(selId){
	var sel = $(selId);
	sel.options.length = 0;
	sel.options[0] = new Option("请选择","");
	sel.options[0].title = "请选择";
	var group = $('sel_Group').value;
	if(group == ''){
		return;
	}
	
	var devSqlQuery = new JetsenWeb.SqlQuery();
	var devQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	devQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "b", "a.OBJ_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Inner));
	JetsenWeb.extend(devSqlQuery, { IsPageResult: 0, KeyId: "a.OBJ_ID", QueryTable: devQueryTable, ResultFields: "a.OBJ_ID, a.OBJ_NAME" });

    var devCondition = new JetsenWeb.SqlConditionCollection();
    devCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", group, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.Int));
    devSqlQuery.Conditions = devCondition;
    devSqlQuery.GroupFields = "a.OBJ_ID, a.OBJ_NAME";

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var result = JetsenWeb.xml.toObject(ret.resultVal,'Record');
    	for(var i = 0; i < result.length; i++){
    		sel.options[i+1] = new Option(result[i].OBJ_NAME,result[i].OBJ_ID);
    		sel.options[i+1].title = result[i].OBJ_NAME;
    	}
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [devSqlQuery.toXml()]);
}
//单选
function uncheckAllItems(objName,vip){
	var objs = document.getElementsByName(objName);
    for (var i = 0; i < objs.length; i++) {
    	if(objs[i] != vip)
    		objs[i].checked = false;
    }
}
//添加连接
function addP2p(){
	var left = jetsennet.form.getCheckedValues('chkLeftObject');
	if(left.length == 0){
		jetsennet.alert('请勾选左边设备需要连接的端口！');
		return;
	}
	var right = jetsennet.form.getCheckedValues('chkRightObject');
	if(right.length == 0){
		jetsennet.alert('请勾选右边设备需要连接的端口！');
		return;
	}
	if($('sel_LeftFather').value == $('sel_RightFather').value){
		jetsennet.alert('同一设备端口不能连接！');
		return;
	}
	if(p2pIsExists(left[0],right[0])){
		jetsennet.alert('该连接已存在！');
		return;
	}
	var objP2p = {
			PORTA_ID:left[0],
			PORTB_ID:right[0],
			GROUP_ID:$('sel_Group').value,
			REL_TYPE:3
    	};
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {};
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjInsert", ["BMP_PORT2PORT", JetsenWeb.Xml.serializer(objP2p, "BMP_PORT2PORT")]);
    
    jetsennet.form.checkAllItems("chkLeftObject",false);
    jetsennet.form.checkAllItems("chkRightObject",false);
    
    searchP2pObject();
}
//删除连接
function deleteP2p(keyId) {
	var checkIds = [];
    if(keyId)
    {
        checkIds = [keyId];
    }
    else
    {
        checkIds = JetsenWeb.Form.getCheckedValues("chkP2pObject");
    }
    
    if(checkIds.length==0)
    {
        jetsennet.alert("请选择要删除的项！");
        return;
    }
    
	jetsennet.confirm("确定删除？", function () {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			searchP2pObject();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_PORT2PORT", checkIds.join(',') ]);
		return true;
	});
}
//连接是否存在
function p2pIsExists(portAID,portBID){
	var flag = false;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) 
    {
    	if(ret.resultVal == 1){
    		flag = true;
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("p2pIsExists", [portAID, portBID]);
    
    return flag;
}
//刷新连接
function refreshP2p(){
	var group = $('sel_Group').value;
	if(group == ''){
		jetsennet.alert('请选择网段！');
		return;
	}
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) 
    {
    	searchP2pObject();
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("refreshPortLink", [group]);
}