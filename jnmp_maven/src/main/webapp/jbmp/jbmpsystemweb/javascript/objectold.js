JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree", "js_tabpane", "js_autocomplete"]);
       
/* 分组浏览时的分页 */
var myListPagination = new JetsenWeb.UI.PageBar("mylistPagination");
myListPagination.onpagechange = function() {
	loadObjectListInGroup(currentGroupId, currentGroupType);
}
myListPagination.orderBy = "ORDER BY a.OBJ_ID";
myListPagination.onupdate = function() {
	 $('divListPagination').innerHTML = this.generatePageControl();
}
var myGridList = new JetsenWeb.UI.GridList();
myGridList.ondatasort = function (sortfield, desc) {
	myListPagination.setOrderBy(sortfield, desc);
}
var otherSqlQuery = new JetsenWeb.SqlQuery();
var otherQueryTable = JetsenWeb.createQueryTable("BMP_OBJ2GROUP", "a");
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "b", "a.GROUP_ID = b.GROUP_ID", JetsenWeb.TableJoinType.Inner));
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "c", "a.OBJ_ID = c.OBJ_ID", JetsenWeb.TableJoinType.Inner));
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "d", "c.CLASS_ID = d.CLASS_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(otherSqlQuery, { IsPageResult: 1, PageInfo: myListPagination, otherQueryTable: otherQueryTable, ResultFields: "c.*,d.CLASS_NAME,a.OBJ_ID AS A_OBJ_ID" });
var otherQueryConditions = new JetsenWeb.SqlConditionCollection();

/* 自动发现对象的分页 */
var autoDiscPagination = new JetsenWeb.UI.PageBar("autoDisclistPagination");
autoDiscPagination.onpagechange = function() {
	searchEquiment();
}
autoDiscPagination.orderBy = "ORDER BY a.OBJ_ID";
autoDiscPagination.onupdate = function() {
	 $('divListPagination').innerHTML = this.generatePageControl();
}
var autoDiscGridList = new JetsenWeb.UI.GridList();
autoDiscGridList.ondatasort = function (sortfield, desc) {
	autoDiscPagination.setOrderBy(sortfield, desc);
}

var autoDiscSqlQuery2 = new JetsenWeb.SqlQuery();
var autoDiscQueryTable2 = JetsenWeb.createQueryTable("BMP_OBJECT", "T");
JetsenWeb.extend(autoDiscSqlQuery2, { KeyId: "T.OBJ_ID", QueryTable: autoDiscQueryTable2, ResultFields: "T.IP_ADDR,T.CLASS_ID,COUNT(OBJ_ID) IDNUM", GroupFields: "T.IP_ADDR,T.CLASS_ID" });
//var autoDiscSqlQuery = new JetsenWeb.SqlQuery();
//var autoDiscQueryTable = JetsenWeb.createQueryTable("BMP_AUTODISOBJ", "a");
//autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
//autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "c", "a.COLL_ID=c.COLL_ID", JetsenWeb.TableJoinType.Left));
//autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "d", "a.IP = d.IP_ADDR and a.CLASS_ID = d.CLASS_ID", JetsenWeb.TableJoinType.Left));
//JetsenWeb.extend(autoDiscSqlQuery, { IsPageResult: 1, PageInfo: autoDiscPagination, KeyId: "a.OBJ_ID", QueryTable: autoDiscQueryTable, ResultFields: "a.*,b.CLASS_NAME,c.COLL_NAME,d.OBJ_ID" });
//var autoDiscQueryConditions = new JetsenWeb.SqlConditionCollection();

var autoDiscSqlQuery = new JetsenWeb.SqlQuery();
var autoDiscQueryTable = JetsenWeb.createQueryTable("BMP_AUTODISOBJ", "a");
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "c", "a.COLL_ID=c.COLL_ID", JetsenWeb.TableJoinType.Left));
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable(autoDiscSqlQuery2.toXml(), "d", "a.IP=d.IP_ADDR and a.CLASS_ID=d.CLASS_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(autoDiscSqlQuery, { IsPageResult: 1, PageInfo: autoDiscPagination, KeyId: "a.OBJ_ID", QueryTable: autoDiscQueryTable, ResultFields: "a.*,b.CLASS_NAME,c.COLL_NAME,d.IDNUM" });
var autoDiscQueryConditions = new JetsenWeb.SqlConditionCollection();


var myAttrClsArray;
var newObjFlag;//标识：是否新建对象
var obj;
// 类型信息
var arrAttibClass;
// table名称
var tableName = "BMP_OBJECT";
// 主键
var primaryKey = "a.OBJ_ID";
// 查询键
var searchKey = "a.OBJ_NAME";
// 表格样式
var xsltPath = "xslt/object.xslt";
// 弹出dialog长度
var dialogHeight = 400;
// 弹出dialog标题
var dialogTitleNew = "新建对象";
var dialogtitleUpdate = "更新对象";
var gTabPane;
var attrGroupArray;
var otherTree;
var selectedTree;
var gCollIp;
// 页面控件，表示当前页
var gFrame;
// 页面大小重置函数句柄
var gWindowSizeChangedInterVal;
// 分页控件
var gListPagination = new JetsenWeb.UI.PageBar("listPagination");
gListPagination.onpagechange = function () {
    loadObjectList();
};
gListPagination.orderBy = "ORDER BY a.OBJ_ID";
gListPagination.onupdate = function () {
    $('divListPagination').innerHTML = this.generatePageControl();
};
// 数据列表控件
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gListPagination.setOrderBy(sortfield, desc);
}
// 属性分类树
var gTree;
// 下拉框数据
var parentArray = new Array();
var classArray = new Array();
var groupArray = new Array();
// 属性分类相关集合
var curAttrCls = "";
var curAttrClaId = -1;
var curClass = null;
var attrClsArray;
// 采集器
var arrColltor;
// 采集器组id
var collectorGroupId
// 所有的指定对象的对象属性
var allObjectAttrib
// 指定对象的自定义属性
var customArray = new Array();
// 指定对象的配置属性
var configArray = new Array();
// 指定对象的监测属性
var inpectArray = new Array();
// 指定对象的性能指标属性
var performArray = new Array();
// 指定对象的trap属性
var trapArray = new Array();
// 指定对象的信号属性
    var signalArray = new Array();
  // 指定对象的表格数据属性
    var tableArray = new Array();
   
    var AC_IMG_PATH = "./images/acIcon/";
// 过滤类型集合
var typeFilter = ["SNMP", "SNMP_HOST_PROCESS", "APP", "WEB", "DB"];
// 对象具体信息表
var infos = [];
// 查询条件
var gQueryConditions = new JetsenWeb.SqlConditionCollection();
// 查询语句
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: primaryKey, PageInfo: gListPagination, QueryTable: gQueryTable, ResultFields: "a.*,b.CLASS_NAME" });
// 页面初始化========================================================================================================================
window.onload = function () {
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
    var objContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObjContent"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    objContent.addControl(new JetsenWeb.UI.PageItem("divElementList"));
    objContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));

    var objFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divObj"), { splitType: 1, fixControlIndex: 0, splitBorder:5, showSplit: false });
    objFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size: { width: 0, height: 27} }));
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
    gCollIp = new IP('txt_IP_ADDRESS');
    gCollIp.init();
    windowResized();
    refreshACTree();
    getClassTypeById();
    initQuerySel();
    // 初始化采集器
	getCollector();
	// 显示采集器
    collector2Option();
    // 设置设备厂商
    getAllManufacturers();
    gTabPane = new JetsenWeb.UI.TabPane($('tabPane'),$('tabPage'));
    gTabPane.ontabpagechanged = tabPageChanged;
    
    JetsenWeb.UI.DropDownList.initOptions('sel_VERSION');
	JetsenWeb.UI.DropDownList.initOptions('sel_OBJ_STATE');
	JetsenWeb.UI.DropDownList.initOptions('checkUseful');

    loadAlarm($('cbo_Alarm'), false);
}
// 选项卡单击事件
function tabPageChanged(i, name){
    if(name =="class"){
        $("txt_Key").value = "";
        $("txt_Key").style.display = "";
        $("txt_Key2").style.display = "none";
        $("buttonSearch1").style.display = "";
        $("buttonSearch2").style.display = "none";
        $("txt_IPAddress").style.display = "";
        $("txt_IPAddress2").style.display = "none";
    }else {
    	$("txt_Key2").value = "";
        $("txt_Key").style.display = "none";
        $("txt_Key2").style.display = "";
        $("buttonSearch1").style.display = "none";
        $("buttonSearch2").style.display = "";
        $("txt_IPAddress").style.display = "none";
        $("txt_IPAddress2").style.display = "";
    }
}
// 窗口大小重置
function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

function searchButton2() {
	myListPagination.currentPage = 1;
	loadObjectListInGroup(currentGroupId, currentGroupType);
}
// 属性分类================================================================================
// 生成属性类型树
function genAttrClsTree() {
    if (!attrClsArray) {
        getAllAttrCls();
    }
    gTree = new JetsenWeb.UI.Tree("所有分类", "javascript:showObjList('',-1,null)", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
    gTree.showTop = true;
    gTree.setBehavior("classic");
	
    if(attrClsArray)
    {
        var attrNode = new Array();
        for(var i=0; i<attrClsArray.length; i++)
        {
            var attrCls = attrClsArray[i];
            var node = null;
            if(attrCls["ICON_SRC"] != null && attrCls["ICON_SRC"].trim() != "")
            {
            	node = new JetsenWeb.UI.TreeItem(attrCls["CLASS_NAME"], "javascript:showObjList('" + attrCls["CLASS_TYPE"] + "','" + attrCls["CLASS_ID"] + "',attrClsArray[" + i + "])", null, AC_IMG_PATH + attrCls["ICON_SRC"], AC_IMG_PATH + attrCls["ICON_SRC"]);  
            }else
            {
            	node = new JetsenWeb.UI.TreeItem(attrCls["CLASS_NAME"], "javascript:showObjList('" + attrCls["CLASS_TYPE"] + "','" + attrCls["CLASS_ID"] + "',attrClsArray[" + i + "])");
            }
            attrNode.push(node);
        }
        for(var i=0; i<attrClsArray.length; i++)
        {
            var attrCls = attrClsArray[i];
            var node = attrNode[i];
            if(attrCls["USE_TYPE"] != "" && attrCls["USE_TYPE"] == 1)
            {
                continue;
            }
            else if(attrCls["PARENT_ID"] == 0 || attrCls["PARENT_ID"] == "")
            {
            	 gTree.add(node);
            }
            else
            {
            	for (j = 0; j < attrNode.length; j++) 
				{
                    if (attrClsArray[j]["CLASS_ID"] == attrCls["PARENT_ID"]) 
					{
                        attrNode[j].add(node);
                        break;
                    }
                }
            }
        }
    }
    gTree.add(new JetsenWeb.UI.TreeItem('自动发现对象', "javascript:showAutoDisc()",null, AC_IMG_PATH + "pcde_002.gif", AC_IMG_PATH + "pcde_002.gif"));
}
// 获取全部属性分类
function getAllAttrCls() {
    var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "a");
    queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));

    var sqlQuery = new JetsenWeb.SqlQuery();
    sqlQuery.OrderString = "ORDER BY a.VIEW_POS, a.CLASS_ID";
    JetsenWeb.extend(sqlQuery, { KeyId: "CLASS_ID", QueryTable: queryTable, ResultFields: "a.*,b.PARENT_ID,b.USE_TYPE" });

    var queryConditions = new JetsenWeb.SqlConditionCollection();
    var subConditions = new JetsenWeb.SqlCondition();
    subConditions.SqlLogicType = JetsenWeb.SqlLogicType.And;
    subConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.USE_TYPE", "0", JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    subConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.USE_TYPE", "", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
    queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
    queryConditions.SqlConditions.push(subConditions);
    sqlQuery.Conditions = queryConditions;
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;

    ws.oncallback = function (ret) {
        attrClsArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if(attrClsArray == null) {
        	attrClsArray = [];
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
function initParentSel() {
    $("sel_AC_PARENT").length = 0;
    $("sel_AC_PARENT").options.add(new Option("无", "0"));
    if (attrClsArray) {
        for (i in attrClsArray) {
            var record = attrClsArray[i];
            $("sel_AC_PARENT").options.add(new Option(record["CLASS_NAME"], record["CLASS_ID"]));
        }
    }
}
// 刷新属性分类树
function refreshACTree() {
    attrClsArray = null;
    genAttrClsTree();
    createGroupTree();
    $("divAttribClass").innerHTML = gTree;
    $("divAttrNetObj").innerHTML = otherTree;
    gTree.expandAll();
    otherTree.expandAll();
    showObjList('',-1,null);
}
function getClassByParam(param, value) {
    if (attrClsArray == null) {
        return null;
    }
    for (cls in attrClsArray) {
        if (attrClsArray[cls][param] == value) {
            return attrClsArray[cls];
        }
    }
    return null;
}
// 树节点点击================================================================================================
var fromGroup;
function showObjList(classType, classTypeId, cClass, flag) {
	if(flag)
	{
		fromGroup = flag;
	}
	else
	{
		fromGroup = "";
	}
	if(classType)
	{
		clickFlag = "1";
	}
	else
	{
		clickFlag = "";
	}
    // 设置查询条件为可见
    $("findObject").style.display = "";
    $("buttonSearch1").style.display = "";
    $("buttonSearch2").style.display = "none";
	$("findInstance").style.display = "none";
	$("instanceEquiment").style.display = "none";
	$("addObjectTo").style.display = "";
	$("txt_Key").style.display = "";
	$("txt_Key2").style.display = "none";
	$("txt_IPAddress").style.display = "";
	$("txt_IPAddress2").style.display = "none";
	if(classType.startWith("DB"))
	{
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("parentId").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("db1").style.display = "";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else if(classType.startWith("APP") || classType == "WEB_APACHE" || classType == "WEB_SERVER"|| classType.startWith("FTP"))
	{
		$("parentId").style.display = "none";
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("db1").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else
	{
		$("username").style.display = "none";
		$("community").style.display = "";
		$("password").style.display = "none";
		$("selectVersion").style.display = "";
		$("editVersion").style.display = "none";
		$("objectParam").style.display = "none";
		$("db1").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	/*
	if(classType == "WEB_IIS")
	{
		$("username").style.display = "none";
		$("community").style.display = "";
		$("password").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	*/
	if(classType.startWith("DVB") || classType == "TVPGM" || classType == "FREQ" || classType == "SICH")
	{
		$("trcheckuserful").style.display = "none";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	// 设置新建按钮显示
	if("1" == flag)
	{
		$("oldCreate").style.display = "none";
	}
	else
	{
		$("oldCreate").style.display = "";
	}
	if(fromGroup)
	{
		$("groupDelete").style.display = "none";
		$("addObjectTo").style.display = "none";
	}
	else
	{
		$("groupDelete").style.display = "none";
	}
   	
    curAttrCls = classType;
    curAttrClaId = classTypeId;
    curClass = cClass;
    clearInformation();
    $("txt_Key").value = "";
    gListPagination.currentPage = 1;
    loadObjectList();
}
// 设置查询条件
function setObjCondition() {
    gQueryConditions.SqlConditions = [];
    var cnValue = $('txt_Key').value;
    var searchKey = $('txt_Key2').value;
    var searchIPAddress = $('txt_IPAddress').value;
    var searchIPAddress2 = $('txt_IPAddress2').value;
    if ( cnValue != null && cnValue != "") {
        gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_NAME", cnValue, JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if(searchIPAddress != null && searchIPAddress != "")
    {
    	gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.IP_ADDR", searchIPAddress, JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if(fromGroup && searchKey != null && searchKey != "")
    {
    	gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_NAME", searchKey, JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if(fromGroup && searchIPAddress2 != null && searchIPAddress2 != "")
    {
    	gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.IP_ADDR", searchKey, JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if("1" == clickFlag)
    {
    	if (curAttrCls != "") {
            gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_TYPE", curAttrCls, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    	}
    }
    
}
// 加载对象数据
function loadObjectList() {
    gSqlQuery.OrderString = gListPagination.orderBy;
    setObjCondition();
    gSqlQuery.Conditions = gQueryConditions;
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	if(fromGroup)
    	{
    		$('divElementList').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectmodel.xslt", ret.resultVal);
    	}
    	else
    	{
    		$('divElementList').innerHTML = JetsenWeb.Xml.transformXML(xsltPath, ret.resultVal);
    	}
        gGridList.bind($('divElementList'), $('objTableList'));
        gListPagination.setRowCount($('hidCount').value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
// 按系统，设备，名称查询
// 查询下拉框
function initQuerySel() {
    // loadSystem("sel_GROUP_ID", false);
}
// 对象操作======================================================================================
// 添加
var curObjId = -1;
var gruop_ids = "";
function newObjElement() {
    if (curClass == null || curClass["CLASS_LEVEL"] == 2 || curClass["CLASS_LEVEL"] == 0) {
        jetsennet.alert("当前分类不能添加对象！");
        return;
    }
    newObjFlag = true;//新建对象标识
   	$("tdClassGroup").style.display = "none";
   	$("collectorSelectLabel").style.display = "";
   	$("tdClassObject").style.display = "";
   	$("colloctSelector").disabled = "";
   	$("tr2").style.display = "none";
   	$("colloctorLabel").style.display = "";
   	$("parentId").style.display = "none";
    curObjId = -1;
    clearObjDialog();
    initSelList();
    setDefaultInfo();
    setClassToNewElement();
    setSelectedValue($("sel_OBJ_STATE"), 100);
    $("txt_CLASS_TYPE").value = curClass["CLASS_NAME"];
    $("sel_OBJ_STATE").style.display = "";
    $("collectorInputId").style.display = "none";
    $("collectorSelectId").style.display = "";
    el("showGroupOption").options.length = 0;
    $("btnSelGroup1").style.display = "";
    $("btnSelGroup2").style.display = "none";

    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height:550 }, title: dialogTitleNew });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var object = getParams("", "", "");
            if(!validateIP(object.IP_ADDR)) {
                jetsennet.alert("IP填写错误！");
                return;
            }
            var port_adress = parseInt(object.IP_PORT);
            if(port_adress > 65535 || port_adress < 0) {
                jetsennet.alert("端口号只能在0和65535之间！");
                return;
            }
            var len = el("showGroupOption").options.length;
            for(var i=0; i<len; i++) {
                if(gruop_ids != "") {
                	gruop_ids += ",";
                }
                gruop_ids += el("showGroupOption").options[i].value;
            }
            object.GROUP_ID = gruop_ids;
            getGroupIdByCollId(object["COLL_ID"]);
            // 如果得不到采集组则不进行实例化
            if(!collectorGroupId) {
            	jetsennet.alert("不能得到采集组，实例化失败！");
            	return;
            }
            object.COLLGROUP_ID = collectorGroupId;
            objectInstanceByEdit(object);
            loadObjectList();
            JetsenWeb.UI.Windows.close("new-object-win");
        }
    }
    dialog.showDialog();
}
// 设置新建窗口的默认值
function setDefaultInfo() {
    var type = curClass["CLASS_TYPE"];
    $("txt_PARENT_ID").readOnly = true;
    $("txt_PARENT_ID").disabled = true;
    $("txt_PARENT_ID").value = "";
    if (type.startWith("SNMP") || type == "WEB_IIS") {
        $("txt_IP_PORT").value = 161;
        $("txt_USERNAME2").value = "public";
    } else if (type.startWith("APP")) {
        if (type.equal("APP_TOMCAT")) {
            $("txt_IP_PORT").value = 8080;
        } else if (type.equal("APP_WEBSPHERE")) {
            $("txt_IP_PORT").value = 8880;
        } else if (type.equal("APP_WEBLOGIC")) {
            $("txt_IP_PORT").value = 7001;
        }
    } else if (type == "WEB_APACHE" || type == "WEB_SERVER") {
    	$("txt_IP_PORT").value = 80;
    } else if (type.startWith("DB")) {
        if (type.equal("DB_SQLSERVER")) {
            $("txt_IP_PORT").value = 1433;
        } else if (type.equal("DB_ORACLE")) {
            $("txt_IP_PORT").value = 1521;
        } else if (type.equal("DB_DB2")) {
            $("txt_IP_PORT").value = 50000;
        } else if(type.equal("DB_MYSQL")) {
        	$("txt_IP_PORT").value = 3306;
        }
    }
    if(type == "FTP_SERVER")
    {
    	 $("txt_IP_PORT").value = 21;
    }
    /*
    if(type == "WEB_IIS")
    {
    	 $("txt_IP_PORT").value = 161;
    	 $("txt_USERNAME2").value = "public";
    }
    */
}
// 对象组列表
var groupList = new Array();
function initSelList() {
    initGroupArray();
}
function showGroupList() {
	$('divGroupListTable').innerHTML = "";

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        var xmlDoc = new JetsenWeb.XmlDoc();
        xmlDoc.loadXML(ret.resultVal);
        var nodes = xmlDoc.documentElement.selectNodes("Record");
        var checkedIds = getAllValues($("showGroupOption"));

        for (var i = 0; i < nodes.length; i++)
        {
            if(contain(checkedIds, valueOf(nodes[i].selectSingleNode("GROUP_ID"), "text", "0")))
            {
            	nodes[i].parentNode.removeChild(nodes[i]);
            }
        }
        $('divGroupListTable').innerHTML = JetsenWeb.Xml._transformXML("xslt/objectgroup.xslt", xmlDoc);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divGroupListTable"), $("tabFunction"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpQueryObjGroup", [curObjId]);

    var dialog = new JetsenWeb.UI.Window("show-groupsel-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 400, height: 400 }, title: "选择对象组" });
    dialog.controls = ["divGroupListPanel"];
    dialog.onsubmit = function () {
        var groupIdsStr = groupSelStr();
        addGroup2GroupDia(groupIdsStr);
        JetsenWeb.UI.Windows.close("show-groupsel-win");
    };
    dialog.showDialog();
}
function groupSelChange(obj, groupId) {
    if (obj.checked) {
        if (!contain(groupList, groupId)) {
        	groupList.push(groupId);
        }
    } else {
    	groupList.pop(groupId);
    }
}
function groupSelStr() {
	var elements = document.getElementsByName("chkFunction");
	var str = "";
	for(var i = 0; i < elements.length; i++) {
		var element = elements[i];
		if(element.checked) {
			str = str + element.value + ",";
		}
	}
	if(str.length > 1) {
		str = str.substring(0, str.length-1);
	}
	return str;
}
function groupEditStr() {
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	addGroup2GroupDia(ret.resultVal);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpGetSelGroupStr", [curObjId]);
}
function initGroupArray() {
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
    sqlQuery.OrderString = "ORDER BY GROUP_ID";
    JetsenWeb.extend(sqlQuery, { KeyId: "GROUP_ID", QueryTable: queryTable, ResultFields: "GROUP_ID,GROUP_NAME" });
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE", "100", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Less, JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = conditions;
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
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
// 编辑
function editElement(keyId, classId, classType, flag) {
	if(keyId <= 0) {
    	return;
	}
	newObjFlag = false;//编辑对象标识
	if(classType.startWith("DB"))
	{
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("parentId").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("db1").style.display = "";
		$("tr2").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else if(classType.startWith("APP") || classType == "WEB_APACHE" || classType == "WEB_SERVER" || classType.startWith("FTP"))
	{
		$("parentId").style.display = "none";
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("db1").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("tr2").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else
	{
		$("username").style.display = "none";
		$("community").style.display = "";
		$("password").style.display = "none";
		$("selectVersion").style.display = "";
		$("editVersion").style.display = "none";
		$("objectParam").style.display = "none";
		$("db1").style.display = "none";
		$("tr2").style.display = "";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	if(classType.startWith("DVB") || classType == "TVPGM" || classType == "FREQ" || classType == "SICH")
	{
		$("trcheckuserful").style.display = "none";
		$("td_IPADDRESS").style.display = "none";
		$("td_IPADDRESS2").style.display = "";
	}
	// instanceAttrClsByObjId(keyId);
	// 将采集器的样式设置成是可以选择的
	$("collectorInputId").style.display = "none";
	$("collectorSelectLabel").style.display = "none";
	$("collectorSelectId").style.display = "none";
	$("colloctSelector").disabled = "disabled";
	$("tdClassGroup").style.display = "none";
   	$("tdClassObject").style.display = "";
   	$("btnSelGroup1").style.display = "";
    $("btnSelGroup2").style.display = "none";
    $("colloctorLabel").style.display = "none";
    $("parentId").style.display = "";
    curObjId = keyId;
    clearObjDialog();
    el("showGroupOption").options.length = 0;
    groupEditStr();

    getManufactureByClassId(classId);

    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
    queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIB", "b", "a.OBJ_ID = b.OBJ_ID", JetsenWeb.TableJoinType.Left));
    JetsenWeb.extend(sqlQuery, { KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.*,b.COLL_TIMESPAN" });
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    if(!(classType.startWith("DVB") || classType == "TVPGM" || classType == "FREQ" || classType == "SICH"))
    {
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.ATTRIB_ID", 40001, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    sqlQuery.Conditions = condition;
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        try {
            var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
            if(retObj) {
            	retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
            	setParams(retObj);
            }
            else
            {
            	jetsennet.alert("没有这条记录！");
            }
        } catch (ex) {
            jetsennet.error(ex);
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 550 }, title: "编辑对象" });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var updateObj = getParams(keyId, classId, classType, flag);
            updateObj.MAN_ID = getSelectedValue($("manufacturer"));
            updateObj.FIELD_1 = $("txt_DB_NAME1").value;
            if(!(classType.startWith("DVB") || classType == "TVPGM" || classType == "FREQ" || classType == "SICH"))
            {
            	if(!validateIP(updateObj.IP_ADDR)) {
                    jetsennet.alert("IP填写错误！");
                    return;
                }
            }
            var port_adress = parseInt(updateObj.IP_PORT);
            if(port_adress > 65535) {
                jetsennet.alert("端口号不能大于65535！");
                return;
            }
			var len = el("showGroupOption").options.length;
            for(var i=0; i<len; i++) {
                if(gruop_ids != "") {
                	gruop_ids += ",";
                }
                gruop_ids += el("showGroupOption").options[i].value;
            }
            updateObj.GROUP_ID = gruop_ids;
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("edit-object-win");
              // 如果下面条件为真，则表示是在分组浏览中操作的，动作完成后应该刷新分组浏览
              $("showGroupOption").options.length = 0;
                if('1' == flag) {
                	showObjectList(currentGroupId, currentGroupType);
                }else {
                	loadObjectList();
                }
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjUpdate", [tableName, JetsenWeb.Xml.serializer(updateObj, tableName)]);
        }
    };
    dialog.showDialog();
}
function setParams(retObj) {
    var classType = valueOf(retObj, "CLASS_TYPE");
    var className;
    for(var i=0; i<attrClsArray.length; i++)
    {
        var oneClassType = attrClsArray[i];
        if(classType == oneClassType['CLASS_TYPE'])
        {
        	className = oneClassType['CLASS_NAME'];
        }
    }
    $("txt_OBJ_NAME").value = valueOf(retObj, "OBJ_NAME");
    if(retObj.PARENT_ID == 0 || retObj.PARENT_ID == null || retObj.PARENT_ID == "") {
    	$("txt_PARENT_ID").value = "";
    }else {
    	$("txt_PARENT_ID").value = valueOf(retObj, "PARENT_ID", "");    
    }
    gCollIp.setValue(valueOf(retObj, "IP_ADDR", ""));
    $("txt_IP_ADDRESS2").value = valueOf(retObj, "IP_ADDR", "");
    $("txt_IP_PORT").value = valueOf(retObj, "IP_PORT", "");
    $("txt_CLASS_TYPE").value = className;
    $("txt_USERNAME").value = valueOf(retObj, "USER_NAME");
    $("txt_PASSWORD").value = valueOf(retObj, "USER_PWD");
    $("txt_DB_NAME1").value = valueOf(retObj, "FIELD_1");
    $("txt_OBJ_PARAM").value = valueOf(retObj, "OBJ_PARAM");
    //setSelectedValue($("sel_VERSION"), valueOf(retObj, "VERSION"));
    JetsenWeb.UI.DropDownList['sel_VERSION'].setValue(retObj.VERSION);
    setSelectedValue($("sel_TRAP_ENABLE"), valueOf(retObj, "RECEIVE_ENABLE"));
    //setSelectedValue($("sel_OBJ_STATE"), valueOf(retObj, "OBJ_STATE"));
    JetsenWeb.UI.DropDownList['sel_OBJ_STATE'].setValue(retObj.OBJ_STATE);
    //setSelectedValue($("checkUseful"), valueOf(retObj, "COLL_TIMESPAN"));
    JetsenWeb.UI.DropDownList['checkUseful'].setValue(retObj.COLL_TIMESPAN);
    $("txt_OBJ_DESC").value = valueOf(retObj, "OBJ_DESC");
    if(classType.startWith("DB"))
    {
    	$("sel_VERSION2").value = valueOf(retObj, "VERSION");
    	$("txt_DB_NAME1").value = valueOf(retObj, "FIELD_1");
    }
    else if(classType == "WEB_APACHE" || classType == "WEB_SERVER" || classType.startWith("APP") || classType.startWith("FTP"))
    {
    	$("sel_VERSION2").value = valueOf(retObj, "VERSION");
    }
    else
    {
    	$("txt_USERNAME2").value = valueOf(retObj, "USER_NAME");
    }
}
var clickFlag = "";//"":没有击了类型树进行编辑，1：点击了树类型进行编辑
function getParams(key, classId, classType, flag) {
	if("1" == flag)
	{
		if(classType)
		{
			curAttrCls = classType;
		}
	}

	if(classId)
	{
		curAttrClaId = classId;
	}
	if(!curAttrCls)
	{
		curAttrCls = classType;
	}
    var objElement = {
        GROUP_ID: gruop_ids,
        PARENT_ID: $("txt_PARENT_ID").value,
        OBJ_NAME: $("txt_OBJ_NAME").value,
        //OBJ_STATE: getSelectedValue($("sel_OBJ_STATE")),
        OBJ_STATE: attributeOf($('sel_OBJ_STATE'),"selectedValue",""),
        IP_ADDR: gCollIp.getValue(),
        IP_PORT: $("txt_IP_PORT").value,
        USER_NAME: $("txt_USERNAME").value,
        USER_PWD: $("txt_PASSWORD").value,
        OBJ_PARAM: $("txt_OBJ_PARAM").value,
        OBJ_DESC: $("txt_OBJ_DESC").value,
        RECEIVE_ENABLE: getSelectedValue($("sel_TRAP_ENABLE")),
        //VERSION: getSelectedValue($("sel_VERSION")),
        VERSION: attributeOf($('sel_VERSION'),"selectedValue",""),
        CREATE_USER: JetsenWeb.Application.userInfo.UserName,
        COLL_ID: getSelectedValue($("colloctSelector")),
        //CHECKUSEFUL: getSelectedValue($("checkUseful"))
        CHECKUSEFUL: attributeOf($('checkUseful'),"selectedValue","")
    }
    if(curAttrCls.startWith("DB"))
    {
    	objElement.VERSION = $("sel_VERSION2").value;
    	objElement.FIELD_1 = $("txt_DB_NAME1");
    	objElement.USER_NAME = $("txt_USERNAME").value;
    	objElement.USER_PWD = $("txt_PASSWORD").value;
    }
    else if(curAttrCls == "WEB_APACHE" || curAttrCls == "WEB_SERVER" || curAttrCls.startWith("APP") || curAttrCls.startWith("FTP"))
    {
    	objElement.VERSION = $("sel_VERSION2").value;
    	objElement.USER_NAME = $("txt_USERNAME").value;
    	objElement.USER_PWD = $("txt_PASSWORD").value;
    }
    else 
    {
    	objElement.USER_NAME = $("txt_USERNAME2").value;
    	objElement.USER_PWD = "";
    	objElement.OBJ_PARAM = "";
    }
    
    if(objElement.PARENT_ID == "") {
    	objElement.PARENT_ID = 0;
    }
    if (classId != "" && classType != "") {
        objElement.CLASS_ID = curAttrClaId;
        objElement.CLASS_TYPE = classType;
    }else {
    	objElement.CLASS_ID = curClass["CLASS_ID"];
        objElement.CLASS_TYPE = curClass["CLASS_TYPE"];
    }
    if (key != null) {
        objElement.OBJ_ID = key;
    }
    if(!newObjFlag && (curAttrCls.startWith("DVB") || curAttrCls == "TVPGM" || curAttrCls == "FREQ" || curAttrCls == "SICH"))
    {
    	objElement.IP_ADDR = $("txt_IP_ADDRESS2").value;
    }
	objElement.FIELD_1 = $("txt_DB_NAME1").value;
    return objElement
}
// 清理弹出dialog
function clearObjDialog() {
    var areaElements = JetsenWeb.Form.getElements('divPopWindow');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
}
function genValidateForms() {
    var forms = JetsenWeb.Form.getElements("baseInfo");
    return forms;
}
// 删除
function delElement(keyId, flag) {
	jetsennet.confirm("确定删除？", function ()  {
    	// 在分组浏览对象的情况下删除对象(先将该对象从BMP_OBJ2GROUP中删除，在删除该对象)
        delObjAttribByObjId(keyId, flag);
        return true;
    });
}
function getType() {
    var type = null;
    type = getSelectedValue($("sel_CLASS_TYPE"));
    return type;
}
function getAcByTypeName(classType) {
    for (i in attrClsArray) {
        var record = attrClsArray[i];
        if (record["CLASS_TYPE"] == classType) {
            return record;
        }
    }
    return null;
}
// 对象属性=========================================================================================
// 查看对象属性
var curObjId = null;
var curObjName = null;
var curObjClassId = null;
var curAttrib = null;
function edirObjectAttrib(objId, objName, classId) {
    curObjId = objId;
    curObjName = objName;
    curObjClassId = classId;
    insDatas = null;
    gTabPane = new JetsenWeb.UI.TabPane($('attribPanel'),$('attribPage'));
    $("insObjDivContent").style.display = "";
    var dialog = new JetsenWeb.UI.Window("ins-object-win");
    JetsenWeb.extend(dialog, { 
        submitBox: false, 
        cancelBox: true, 
        windowStyle: 1, 
        cancelButtonText: "关闭", 
        maximizeBox: true, 
        minimizeBox: true, 
        size: { width: 800, height: 500 }, 
        title: "对象属性" }
    );
    dialog.controls = ["insObjDivContent"];
    dialog.showDialog();

    gTabPane.ontabpageselected = function(i, name) {
        if("1" == name) {
            // 自定义信息
        	$('insObjDiv11').innerHTML = "";
        	getOAList(); 
        }else if("2" == name) {
        	// 获取配置信息
            $('insConfigDiv').innerHTML = "";
            getObjAttribute(curObjId, '101,106', '');
        }else if("3" == name) {
        	// 获取监控信息
            $('insInspectDiv').innerHTML = "";
            getObjAttribute(curObjId, '102', '');
        }else if("4" == name) {
        	// 获取性能指标信息
            $('insPerformDiv').innerHTML = "";
            getObjAttribute(curObjId, '103', '');
        }else if("5" == name) {
        	// 获取Trap信息、信号信息、Syslog信息
            $('insTrapDiv').innerHTML = "";
            getObjAttribute(curObjId, '104,105,107', '');
        }
    }
   	getOAList();
}

function getOAList() {
	$('insObjDiv11').innerHTML = "";
	curAttrib = "";
	
    var sqlQuery = new JetsenWeb.SqlQuery();
    var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "A");
    queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJATTRIBVALUE","B","A.OBJATTR_ID=B.OBJATTR_ID",JetsenWeb.TableJoinType.Left));
    
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("A.ATTRIB_TYPE", "100", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("A.OBJ_ID", curObjId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

     sqlQuery.Conditions = conditions;
     JetsenWeb.extend(sqlQuery, { KeyId: "", QueryTable: queryTable, ResultFields: "A.*,B.STR_VALUE" });

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if(retObj != null){
        	for(var i = 0; i < retObj.length; i++){
        		curAttrib += retObj[i].ATTRIB_ID + ",";
        	}
        	curAttrib = curAttrib.substring(0, curAttrib.length-1);
        }
        $('insObjDiv11').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectdefinelist.xslt", ret.resultVal);
        var gGridList1 = new JetsenWeb.UI.GridList();
        gGridList1.bind($("insObjDiv11"), $("objInsTable1"));
        
        return;
    };
    ws.onerror = function (ex) { jetsennet.error(ex); return; };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}
function editObjAttrib(keyId) {
    clearObjAttribDialog();
    var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "");
    JetsenWeb.extend(gSqlQuery, { KeyId: "OBJATTR_ID", QueryTable: gQueryTable });
    var gQueryConditions = new JetsenWeb.SqlConditionCollection();
    gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJATTR_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gSqlQuery.Conditions = gQueryConditions;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var record = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if(record) 
        {
        	record = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
            $("txt_OBJATTR_NAME").value = record.OBJATTR_NAME;
            $("txt_ATTRIB_PARAM").value = record.ATTRIB_PARAM;
            $("txt_DATA_ENCODING").value = record.DATA_ENCODING;
            setSelectedValue($("sel_IS_COLLECT"), record.COLL_TYPE);
        }
        else 
        {
        	jetsennet.alert("没有这条记录！");
            return;
        }
        
    };
    ws.onerror = function (ex) { jetsennet.error(ex); return; };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-oa-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 400, height: 200 }, title: "编辑对象属性信息" });
    dialog.controls = ["oaConfigPopWindow"];
    dialog.onsubmit = function () {
        try {
            var areaElements = JetsenWeb.Form.getElements("oaConfigPopWindow");
            if (JetsenWeb.Form.Validate(areaElements, true)) {
                var objElement = {
                    OBJATTR_ID: keyId,
                    OBJATTR_NAME: $("txt_OBJATTR_NAME").value,
                    ATTRIB_PARAM: $("txt_ATTRIB_PARAM").value,
                    DATA_ENCODING: $("txt_DATA_ENCODING").value,
                    COLL_TYPE: getSelectedValue($("sel_IS_COLLECT"))
                }
                var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
                ws.soapheader = JetsenWeb.Application.authenticationHeader;
                ws.oncallback = function (ret) {
                    getOAList();
                    JetsenWeb.UI.Windows.close("edit-oa-win");
                };
                ws.onerror = function (ex) { jetsennet.error(ex); };
                ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(objElement, "BMP_OBJATTRIB")]);
            }
        } catch (ex) {
            jetsennet.error(ex);
        }
    };
    dialog.showDialog();
}
function getObjAttribById(objattr_id) {
    return insDatas[objattr_id];
}
function clearObjAttribDialog() {
    var areaElements = JetsenWeb.Form.getElements('oaConfigPopWindow');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
}
// 查找按钮动作
function searchButton() {
    gListPagination.currentPage = 1;
    loadObjectList();
}
// 子对象配置=====================================================================================================
var curSubObjId = -1;
var curSubObjName = "";
var curSubGroupId = -1;
var curSubClassType = "";
// 获取对象进程列表
function showProcList(obj_id, obj_name, groupId, class_type, class_id) {
    curSubObjId = obj_id;
    curSubObjName = obj_name;
    curSubGroupId = groupId;
    curSubClassType = class_type;
    curSubClassId = class_id;
    getSubClass(curSubClassId);
    
    $("spanProcListTitle").innerHTML = obj_name;
    /*
	 * loadObjProcList(obj_id);
	 */
    var dialog = new JetsenWeb.UI.Window("list-subobj-win");
    JetsenWeb.extend(dialog, { submitBox: false, cancelBox: true, cancelButtonText: "关闭", windowStyle: 1, maximizeBox: true, minimizeBox: true, size: { width: 650, height: 450 }, title: "子对象" });
    dialog.controls = ["divProcList"];
    dialog.showDialog();
   
}
function loadObjProcList(obj_id) {
    $('divProcListTable').innerHTML = "";
    var gSqlQuery = new JetsenWeb.SqlQuery();
    gSqlQuery.OrderString = "ORDER BY OBJ_ID";
    var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
    JetsenWeb.extend(gSqlQuery, { KeyId: "OBJ_ID", QueryTable: gQueryTable, ResultFields: "a.*,b.CLASS_NAME" });
    var gQueryConditions = new JetsenWeb.SqlConditionCollection();
    gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", obj_id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    gSqlQuery.Conditions = gQueryConditions;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    // ws.async = false;
    ws.oncallback = function (ret) {
        $('divProcListTable').innerHTML = JetsenWeb.Xml.transformXML("xslt/procobjlist.xslt", ret.resultVal);
        var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divProcListTable"), $("procobjtable"));
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
// 删除
function delSubElement(keyId) {
	jetsennet.confirm("确定删除？", function () {
        var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {
            //loadObjProcList(curSubObjId);
        	showSubObject();
        };
        ws.onerror = function (ex) { jetsennet.error(ex); };
        ws.call("bmpObjDelete", [tableName, keyId]);
        return true;
    });
}
// 编辑
function editSubElement(keyId, classId, classType) {
	curObjId = keyId;
    clearObjDialog();
    initSelList();
    // setDefaultInfo();
    el("showGroupOption").options.length = 0;
    $("btnSelGroup1").style.display = "";
    $("btnSelGroup2").style.display = "none";
    $("colloctorLabel").style.display = "none";
    $("tr2").style.display = "";
    groupEditStr();
    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "OBJ_ID", PageInfo: null, QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_OBJECT" }) });
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJ_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        try {
            var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
            if(retObj) 
            {
            	retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
            	setParams(retObj);
            }
            else
            {
            	jetsennet.alert("数据为空！");
                return;
            }
        } catch (ex) {
            jetsennet.error(ex);
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: 550 }, title: "编辑子对象" });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var updateObj = getParams(keyId, classId, classType);
            var len = el("showGroupOption").options.length;
            for(var i=0; i<len; i++) {
                if(gruop_ids != "") {
                	gruop_ids += ",";
                }
                gruop_ids += el("showGroupOption").options[i].value;
            }
            updateObj.GROUP_ID = gruop_ids;
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("edit-object-win");
                //loadObjProcList(curSubObjId);
                loadObjectList();
                showSubObject();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjUpdate", [tableName, JetsenWeb.Xml.serializer(updateObj, tableName)]);
        }
    };
    dialog.showDialog();
}
function getSubObj() {
    selProcess.length = 0;

    var dialog = new JetsenWeb.UI.Window("proc-list-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 900, height: 500 }, title: "CATV子对象列表", cancelButtonText: "关闭" });
    dialog.controls = ["cardListContentDiv"];

    dialog.onsubmit = function () {
    }
    dialog.showDialog();
}



