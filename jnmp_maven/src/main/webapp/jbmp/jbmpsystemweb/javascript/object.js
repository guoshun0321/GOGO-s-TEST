JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree", "js_tabpane", "js_autocomplete"]);
       
/* 分组浏览时的分页 */
var myListPagination = new JetsenWeb.UI.PageBar("mylistPagination");
myListPagination.onpagechange = function() {
	loadObjectListInGroup(currentGroupId, currentGroupType);
}
myListPagination.orderBy = "ORDER BY c.OBJ_ID";
myListPagination.onupdate = function() {
	 $('divListPagination').innerHTML = this.generatePageControl();
}
var myGridList = new JetsenWeb.UI.GridList();
myGridList.ondatasort = function (sortfield, desc) {
	myListPagination.setOrderBy(sortfield, desc);
}
var otherSqlQuery = new JetsenWeb.SqlQuery();
otherSqlQuery.GroupFields = " c.OBJ_ID,c.OBJ_NAME,c.IP_ADDR,c.CLASS_ID,c.CLASS_TYPE,c.RECEIVE_ENABLE,d.CLASS_NAME,e.MAXLEVEL ";
var otherQueryTable = JetsenWeb.createQueryTable("BMP_OBJ2GROUP", "a");
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "b", "a.GROUP_ID = b.GROUP_ID", JetsenWeb.TableJoinType.Inner));
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJECT", "c", "a.OBJ_ID = c.OBJ_ID", JetsenWeb.TableJoinType.Inner));
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "d", "c.CLASS_ID = d.CLASS_ID", JetsenWeb.TableJoinType.Left));
otherQueryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT h.OBJ_ID,MAX(h.ALARM_LEVEL) AS MAXLEVEL FROM BMP_ALARMEVENT h WHERE (h.EVENT_STATE = 0 OR h.EVENT_STATE = 1) AND h.ALARM_LEVEL IN(10,20,30,40) GROUP BY h.OBJ_ID )", 
		"e", "e.OBJ_ID = a.OBJ_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(otherSqlQuery, { IsPageResult: 1, PageInfo: myListPagination, otherQueryTable: otherQueryTable, ResultFields: " c.OBJ_ID,c.OBJ_NAME,c.IP_ADDR,c.CLASS_ID,c.CLASS_TYPE,c.RECEIVE_ENABLE,d.CLASS_NAME,e.MAXLEVEL " });
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
autoDiscSqlQuery.GroupFields = " a.OBJ_ID,a.OBJ_NAME, a.IP ,a.IP_NUM,a.CLASS_ID,a.COLL_ID,a.TASK_ID,a.OBJ_STATUS,a.REC_STATUS,a.USER_NAME,a.PASSWORD,a.VERSION,a.PORT,a.OBJ_DESC,a.FIELD_1,b.CLASS_NAME,c.COLL_NAME,d.IDNUM "
var autoDiscQueryTable = JetsenWeb.createQueryTable("BMP_AUTODISOBJ", "a");
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_COLLECTOR", "c", "a.COLL_ID=c.COLL_ID", JetsenWeb.TableJoinType.Left));
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable(autoDiscSqlQuery2.toXml(), "d", "a.IP=d.IP_ADDR and a.CLASS_ID=d.CLASS_ID", JetsenWeb.TableJoinType.Left));
autoDiscQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "e", "e.NUM_VAL1 = c.COLL_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(autoDiscSqlQuery, { IsPageResult: 1, PageInfo: autoDiscPagination, KeyId: "a.OBJ_ID", QueryTable: autoDiscQueryTable, ResultFields: "a.*,b.CLASS_NAME,c.COLL_NAME,d.IDNUM" });
var autoDiscQueryConditions = new JetsenWeb.SqlConditionCollection();

//自动发现页面调整
var autoFindFag = 1;
//批量删除组对象的标志
var deleteGroupObjFag ;
//var deleteObjChidFag ;
//隐藏的对象id
var hideObjIdlw;
//影城的对象属性类型
var hideClassTypelw =-1 ;
//所有组对象
var objArray ;
//所有对象的子类型
var objClassArray ;
//所有的子对象
var objChildArray ;

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
var gGridListObj = new JetsenWeb.UI.GridList("attribclass-grid");
// 数据列表控件
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gListPagination.setOrderBy(sortfield, desc);
}

//子对象排列
//分页控件
var gListPaginationChild = new JetsenWeb.UI.PageBar("listPaginationChild");
gListPaginationChild.onpagechange = function () {
	searchChildObj();
};
gListPaginationChild.orderBy = "ORDER BY a.OBJ_ID";
gListPaginationChild.onupdate = function () {
    $('divListPagination').innerHTML = this.generatePageControl();
};
var gGridListChild = new JetsenWeb.UI.GridList();
gGridListChild.ondatasort = function (sortfield, desc) {
    gListPaginationChild.setOrderBy(sortfield, desc);
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
var typeFilter = ["SNMP", "SNMP_HOST_PROCESS", "APP", "WEB", "DB","INSPUR_ROOM"];
// 对象具体信息表
var infos = [];
// 查询条件
var gQueryConditions = new JetsenWeb.SqlConditionCollection();
// 查询语句
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Left));
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT d.OBJ_ID,MAX(d.ALARM_LEVEL) AS MAXLEVEL FROM BMP_ALARMEVENT d WHERE (d.EVENT_STATE = 0 OR d.EVENT_STATE = 1) AND d.ALARM_LEVEL IN(10,20,30,40) GROUP BY d.OBJ_ID )", 
		"c", "c.OBJ_ID = a.OBJ_ID", JetsenWeb.TableJoinType.Left));
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: primaryKey, PageInfo: gListPagination, QueryTable: gQueryTable, ResultFields: "a.*,b.CLASS_NAME,c.MAXLEVEL" });

var allClsArray;

//存储属性类型数组
var VtypeArray = [];

// 是否为管理员帐号
var isAdminAccount = false;

// 报警规则配置
var generalAlarmConfig = null;

// 页面初始化========================================================================================================================
window.onload = function () {
	
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	isAdminAccount = jbmp.util.isAdmin(JetsenWeb.Application.userInfo.UserId);
	
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
    gCollIp = new IP('txt_IP_ADDRESS');
    gCollIp.init();
    windowResized();
    
    gTabPane = new JetsenWeb.UI.TabPane($('tabPane'),$('tabPage'));
    gTabPane.ontabpagechanged = tabPageChanged;
    getAllAttrribClass();
    refreshACTree();
    getClassTypeById();
    initQuerySel();
    // 初始化采集器
	getCollector();
	// 显示采集器
    collector2Option();
    // 设置设备厂商
    getAllManufacturers();
    // 初始化报警规则配置模版
    generalAlarmConfig = new jbmp.alarm.AlarmConfig("divGAlarmConfig", "报警规则设置");
	generalAlarmConfig.init();

    JetsenWeb.UI.DropDownList.initOptions('sel_VERSION');
	JetsenWeb.UI.DropDownList.initOptions('sel_OBJ_STATE');
	JetsenWeb.UI.DropDownList.initOptions('checkUseful');

	// loadAlarm($('cbo_Alarm'), false);
}

//窗口大小重置
function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

var tableValue  = 1;
// 选项卡单击事件
function tabPageChanged(i, name){
    if(name =="class"){
    	tableValue = 1;
        $("txt_Key").value = "";
        $("txt_Key").style.display = "";
        $("txt_Key2").style.display = "none";
        $("buttonSearch1").style.display = "";
        $("buttonSearch2").style.display = "none";
        $("txt_IPAddress").style.display = "";
        $("txt_IPAddress2").style.display = "none";
    }else {
    	tableValue = 2;
    	$("txt_Key2").value = "";
        $("txt_Key").style.display = "none";
        $("txt_Key2").style.display = "";
        $("buttonSearch1").style.display = "none";
        $("buttonSearch2").style.display = "";
        $("txt_IPAddress").style.display = "none";
        $("txt_IPAddress2").style.display = "";
    }
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
            var param = {classId:attrCls["CLASS_ID"],fag:true,obj:attrCls};
            node.param = param ;
            attrNode.push(node);
            var attrArrayobj = {classId:attrCls["CLASS_ID"],htmlId:node.id};
            VtypeArray.push(attrArrayobj);
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
// 获取一级属性分类
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
//    queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
    queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
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
	if(!isAdminAccount){
	    if (!attrClsArray) {
	        getAllAttrCls();
	    }
	    tableValue = 2;
	    createGroupTree();
	    $("divAttribClassView").style.display = "none";
	    $("divAttribClass").style.display = "none";
	    $("divAttrNetObj").innerHTML = otherTree;
	    otherTree.expandAll();
	    gTabPane.select(1);
	    showObjList('',-1,null,1);
	    var divs2 = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
	    for(var j = 0; j < divs2.length; j++){
	    	divs2[j].ondblclick = findChildGroup;
	    }
	}else{
	    genAttrClsTree();
	    createGroupTree();
	    $("divAttribClass").innerHTML = gTree;
	    $("divAttrNetObj").innerHTML = otherTree;
	    gTree.expandAll();
	    otherTree.expandAll();
	    showObjList('',-1,null);
	    var divs = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttribClass') } );
	    for(var i = 0; i < divs.length; i++){
	    	divs[i].ondblclick = findChild;
	    }
	    var divs2 = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
	    for(var j = 0; j < divs2.length; j++){
	    	divs2[j].ondblclick = findChildGroup;
	    }
	}
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
	deleteGroupObjFag = 0 ;
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
	if(autoFindFag == 2){
		retsetDefaultsize()
	}
	
    // 设置查询条件为可见
	$("deleteObjectMany").onclick = delElementIds;
	
    $("findObject").style.display = "";
    $("findObjectChild").style.display = "none";   
    $("buttonSearch1").style.display = "";
    $("buttonSearch2").style.display = "none";
	$("findInstance").style.display = "none";
	$("ObjectAttriListLw").style.display = "none";
	
	$("deleteObjectMany").style.display = "";
	$("toMonitorObjAttr").style.display = "none";
	$("addObjectTo").style.display = "";
	$("instanceEquiment").style.display = "none";
	changeBottomButton("", "none", "none", "none", "none", "");
	
	$("divListPagination").style.display = "";
	$("myDivListPagination").style.display = "";
	
	if(classType.startWith("DB"))
	{
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
	else if(classType.startWith("INSPUR_ROOM"))
	{
		$("td_PORT").style.display = "none";
		$("urlAddress").style.display = "";
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("parentId").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("db1").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else
	{
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
		$("oldCreate").style.display = "";
		$("addObjectInGroup").style.display = "";
		$("newObjElement").style.display = "none";
		$("addSubObject").style.display = "none";
	
	}
	else
	{
		$("oldCreate").style.display = "";
		$("newObjElement").style.display = "";
		$("addObjectInGroup").style.display = "none";
		$("addSubObject").style.display = "none";
	}
	
	if(fromGroup)
	{
		$("groupDelete").style.display = "none";
//		$("addObjectTo").style.display = "none";
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
    gQueryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
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
    var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
//    	if(fromGroup)
//    	{
//    		$('divElementList').innerHTML = JetsenWeb.Xml.transformXML("xslt/object_setvalue.xslt", ret.resultVal);
//    	}
//    	else
//    	{
    		$('divElementList').innerHTML = JetsenWeb.Xml.transformXML(xsltPath, ret.resultVal);
//    	}
        gGridList.bind($('divElementList'), $('objTableList'));
        gListPagination.setRowCount($('hidCount').value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
	ws.call("nmpPermissionsQuery", [gSqlQuery.toXml(), 'a.OBJ_ID', '2']);
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
	$("objectgroupcontent").style.display = "";
	$("objectgroupcontrol").style.display = "";
   	$("tdClassGroup").style.display = "none";
   	$("collectorSelectLabel").style.display = "";
   	$("tdClassObject").style.display = "";
   	$("colloctSelector").disabled = "";
   	$("tr2").style.display = "none";
   	$("colloctorLabel").style.display = "";
    curObjId = -1;
    clearObjDialog();
    initSelList();
    setDefaultInfo();
    setClassToNewElement();
    setSelectedValue($("sel_OBJ_STATE"), 100);
    $("txt_CLASS_TYPE").value = curClass["CLASS_NAME"];
    getManufactureByClassId(curClass["CLASS_ID"]);
    $("sel_OBJ_STATE").style.display = "";
    $("collectorInputId").style.display = "none";
    $("collectorSelectId").style.display = "";
    el("showGroupOption").options.length = 0;
    $("btnSelGroup1").style.display = "none";
    $("btnSelGroup2").style.display = "none";
    $("btnDel").style.display = "none";
    showRemindWordCount($("txt_OBJ_DESC").value,$('remindWord'),"30");
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height:550 }, title: dialogTitleNew });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var object = getParams("", "", "");
            object.MAN_ID = $('manufacturer').value;
            if(!validateIP(object.IP_ADDR)) {
                jetsennet.alert("IP填写错误！");
                return;
            }
            var port_adress = parseInt(object.IP_PORT);
            if(port_adress > 65535 || port_adress < 0) {
                jetsennet.alert("端口号只能在0和65535之间！");
                return;
            }
            if(parseInt(getBytesCount($("txt_OBJ_DESC").value))>60){
            	jetsennet.alert("对象描述不能超过30个文字！");
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
            
            var strIp = getAutoObjIpOne(object);
            if(strIp){
           	 jetsennet.confirm("IP是"+strIp+"的对象已存在，确定要新建吗？", function (){
	            objectInstanceByEdit(object);
	            loadObjectList();
	            refreshTreeChange();
	            JetsenWeb.UI.Windows.close("new-object-win");
	            return true;
           	 });
            }else{
	            objectInstanceByEdit(object);
	            loadObjectList();
	            refreshTreeChange();
	            JetsenWeb.UI.Windows.close("new-object-win");
            }

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

    var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
//	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
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
    var usrId = jetsennet.Application.userInfo.UserId;
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpQueryObjGroup", [curObjId, usrId]);
//    ws.call("nmpPermissionsQuery", [curObjId, 'GROUP_ID', '1']);

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
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
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
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'GROUP_ID', '1']);
}
// 编辑
function editElement(keyId, classId, classType, flag) {
	var editdialogHeight = 550 ;
	if(keyId <= 0) {
    	return;
	}
	newObjFlag = false;//编辑对象标识
	if(flag){
		if('10' == flag){
			$("objectgroupcontent").style.display = "none";
			$("objectgroupcontrol").style.display = "none";
			editdialogHeight = 480 ;
		}else{
			$("objectgroupcontent").style.display = "";
			$("objectgroupcontrol").style.display = "";
		}
	}else{
		$("objectgroupcontent").style.display = "";
		$("objectgroupcontrol").style.display = "";
	}
	if(classType.startWith("DB"))
	{
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
	else if(classType.startWith("INSPUR_ROOM"))
	{
		$("urlAddress").style.display = "";
		$("td_PORT").style.display = "none";
		$("selectVersion").style.display = "none";
		$("editVersion").style.display = "";
		$("objectParam").style.display = "none";
		$("parentId").style.display = "none";
		$("username").style.display = "";
		$("community").style.display = "none";
		$("password").style.display = "";
		$("db1").style.display = "none";
		$("tr2").style.display = "none";
		$("trcheckuserful").style.display = "";
		$("td_IPADDRESS").style.display = "";
		$("td_IPADDRESS2").style.display = "none";
	}
	else
	{
		$("td_PORT").style.display = "";
		$("urlAddress").style.display = "none";
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
   	$("btnSelGroup1").style.display = "none";
    $("btnSelGroup2").style.display = "none";
    $("btnDel").style.display = "none";
    $("colloctorLabel").style.display = "none";
    curObjId = keyId;
    clearObjDialog();
    el("showGroupOption").options.length = 0;
    groupEditStr();


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
            	showRemindWordCount($("txt_OBJ_DESC").value,$('remindWord'),"30");
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
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height: editdialogHeight }, title: "编辑对象" });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var updateObj = getParams(keyId, classId, classType, flag);
            updateObj.MAN_ID = getSelectedValue($("manufacturer"));
            if(updateObj.MAN_ID == ""){
            	updateObj.MAN_ID = "0" ;
            }
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
            if(parseInt(getBytesCount($("txt_OBJ_DESC").value))>60){
            	jetsennet.alert("对象描述不能超过30个文字！");
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
              if(flag){                
            	  if('1' == flag) {
                	showObjectList(currentGroupId, currentGroupType);
                	 refreshTreeGroup();
            	  }
            	  if('10' == flag){
                	searchChildObj();
            	  }
              }else {
                	loadObjectList();
                	refreshTreeGroup();
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
    for(var i=0; i<allClsArray.length; i++)
    {
        var oneClassType = allClsArray[i];
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
    $("txt_urlAddress").value = valueOf(retObj, "FIELD_2");
    //setSelectedValue($("sel_VERSION"), valueOf(retObj, "VERSION"));
    JetsenWeb.UI.DropDownList['sel_VERSION'].setValue(retObj.VERSION);
    setSelectedValue($("sel_TRAP_ENABLE"), valueOf(retObj, "RECEIVE_ENABLE"));
    //setSelectedValue($("sel_OBJ_STATE"), valueOf(retObj, "OBJ_STATE"));
    JetsenWeb.UI.DropDownList['sel_OBJ_STATE'].setValue(retObj.OBJ_STATE);
    //setSelectedValue($("checkUseful"), valueOf(retObj, "COLL_TIMESPAN"));
    JetsenWeb.UI.DropDownList['checkUseful'].setValue(retObj.COLL_TIMESPAN);
    $("txt_OBJ_DESC").value = valueOf(retObj, "OBJ_DESC");
    $("manufacturer").value = valueOf(retObj, "MAN_ID");
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
        CHECKUSEFUL: attributeOf($('checkUseful'),"selectedValue",""),
        FIELD_2:$("txt_urlAddress").value
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
    else if(curAttrCls.startWith("INSPUR_ROOM"))
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
        refreshTreeChange();
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
var curObjAttrib = [];
var curTabNum = 1;
function edirObjectAttrib(objId, objName, classId) {
    curObjId = objId;
    curObjName = objName;
    curObjClassId = classId;
    curObjAttrib = [];
    insDatas = null;
    gTabPane = new JetsenWeb.UI.TabPane($('attribPanel'),$('attribPage'));
    $("insObjDivContent").style.display = "";
    var dialog = new JetsenWeb.UI.Window("ins-object-win");
    JetsenWeb.extend(dialog, { 
        submitBox: false, 
        cancelBox: false, 
        windowStyle: 1, 
//        cancelButtonText: "关闭", 
        maximizeBox: true, 
        minimizeBox: true, 
        size: { width: 800, height: 500 }, 
        title: "对象属性" }
    );
    dialog.controls = ["insObjDivContent"];
    
    dialog.showDialog();

    gTabPane.ontabpageselected = function(i, name) {
    	curTabNum = name;
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
            onAttribTypeChange();
//            getObjAttribute(curObjId, '104,105,107', '');
        }
    }
   	getOAList();
}

var gListPaginationAttrOther = new JetsenWeb.UI.PageBar("listPaginationinsConfigDiv");
gListPaginationAttrOther.orderBy = "ORDER BY A.OBJATTR_ID";
gListPaginationAttrOther.onpagechange = function () {
	getOAList();
};
gListPaginationAttrOther.onupdate = function () {
	$('insObjDiv11').innerHTML  = this.generatePageControl();
};
var gGridListAttrOther = new JetsenWeb.UI.GridList();
gGridListAttrOther.ondatasort = function (sortfield, desc) {
	gListPaginationAttrOther.setOrderBy(sortfield, desc);
}

function getOAList() {
	$('insObjDiv11').innerHTML = "";
	
    var sqlQuery = new JetsenWeb.SqlQuery();
    sqlQuery.OrderString = gListPaginationAttrOther.orderBy;
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
    	curObjAttrib[0] = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        //var retObj = JetsenWeb.Xml.toObject(ret.resultVal, "Record1")[0];
        $('insObjDiv11').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectdefinelist.xslt", ret.resultVal);
//        var gGridList1 = new JetsenWeb.UI.GridList();
        gGridListAttrOther.bind($("insObjDiv11"), $("objInsTable1"));
        hideClassTypelw  = "100" ;
        
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
	$("objectgroupcontent").style.display = "";
	$("objectgroupcontrol").style.display = "";
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
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 650, height: 350 }, title: "编辑子对象" });
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
//**********************************************************************************************************
//根据类classId获取类型下的对象
function getobjByclassId(classId){
	var objNameArray ="";
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_ID=b.CLASS_ID", JetsenWeb.TableJoinType.Inner));
	var sqlQuery = new JetsenWeb.SqlQuery();
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.CLASS_ID", classId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    sqlQuery.Conditions = condition;
	sqlQuery.OrderString = "ORDER BY a.OBJ_ID";
	JetsenWeb.extend(sqlQuery, { KeyId: "a.OBJ_ID", QueryTable: queryTable, ResultFields: "a.OBJ_ID,a.OBJ_NAME,a.CLASS_TYPE,b.CLASS_ID,b.CLASS_NAME,b.ICON_SRC" });
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	objNameArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(objNameArray == null) {
    		objNameArray = "";
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.GROUP_ID', '1']);
	return objNameArray;
}


function findChild(){
	var i= 0;
	var nodes = gTree.childNodes;
	var node = repeatFind(gTree, this.id);
	if(node != null)
	{
		if(node.param == null){
			return ;
		}
		//根据对象类型插入对象
		if(node.param.fag)
		{
		    if(node.childNodes.length == 0)
		    {		    	
				var classId = node.param.classId ;
				var obj = node.param.obj;
				var objNameArray = getobjByclassId(classId) ;
				for(var a=0; a<objNameArray.length ; a++)
				{
					i++;
					var objnode =  new JetsenWeb.UI.TreeItem(objNameArray[a]['OBJ_NAME'], "javascript:getobjAttrById(" +objNameArray[a]['OBJ_ID']+")", null, AC_IMG_PATH + objNameArray[a]['ICON_SRC'], AC_IMG_PATH + objNameArray[a]['ICON_SRC']);
					var param = {objId:objNameArray[a]['OBJ_ID'],objFag:true,classId:classId};
					objnode.param = param;
					node.add(objnode);
				}	
				node.expand();
		    }		    
		}
//		插入对象的类型
		else if(node.param.objFag)
		{
		    if(node.childNodes.length == 0)
		    {
				var objId = node.param.objId ;
				var objclassid = node.param.classId ;
				var id = "";
				getobjClass(objId);				
				var classidarr=[];
				if(objClassArray){
					for(var r=0; r<objClassArray.length ; r++)
					{
						classidarr.push(objClassArray[r].CLASS_ID);
					}
					id = classidarr.join(",");
				}else{
					objClassArray = [];
				}
							
				var nodeAttrClass = getSubTypeBYObj(objclassid,id);
				if(nodeAttrClass){
					for(var c=0; c<nodeAttrClass.length ; c++){
						var qobjClass = {CLASS_ID:nodeAttrClass[c].CLASS_ID,
											CLASS_NAME:nodeAttrClass[c].CLASS_NAME,
											ICON_SRC:nodeAttrClass[c].ICON_SRC,
											PARENT_ID:objId};
						objClassArray.push(qobjClass);
					}
				}
				for(var b=0; b<objClassArray.length ; b++)
				{
			    	i++;
			    	var num = getChildObjNum(objClassArray[b]['PARENT_ID'],objClassArray[b]['CLASS_NAME']);
//					var objClsssnode =  new JetsenWeb.UI.TreeItem(objClassArray[b]['CLASS_NAME'], "javascript:loadObjectListInClass(" + objClassArray[b]['PARENT_ID'] +  ",'" + objClassArray[b]['CLASS_NAME'] + "')", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var objClsssnode =  new JetsenWeb.UI.TreeItem(objClassArray[b]['CLASS_NAME']+num, "javascript:loadObjectListInClass(" + objClassArray[b]['PARENT_ID'] +  ",'" + objClassArray[b]['CLASS_NAME'] + "',"+ objClassArray[b]['CLASS_ID'] +","+objId+")", null, AC_IMG_PATH + objClassArray[b]['ICON_SRC'], AC_IMG_PATH + objClassArray[b]['ICON_SRC']);					
					var param = {parentId:objClassArray[b]['PARENT_ID'],className:objClassArray[b]['CLASS_NAME'],objClassfag:true};
					objClsssnode.param = param;
					node.add(objClsssnode);
				}
				node.expand();
		    }
		}
		//对象的子对象
		else if(node.param.objClassfag)
		{
		    if(node.childNodes.length == 0)
		    {
//		    	var nodeName = node.text;
				var parentId = node.param.parentId ;
				var className= node.param.className ;
				getobjChildClass(parentId,className);
				var len = objChildArray.length;
//				if(!node.renameFag)
//				node.text = nodeName+"("+len+")";
//				node.renameFag = true;
				for(var b=0; b<len ; b++)
				{
			    	i++;
					objChildnode = new JetsenWeb.UI.TreeItem(objChildArray[b]['OBJ_NAME'], "javascript:getobjAttrById(" + objChildArray[b]['OBJ_ID'] + ")", null, AC_IMG_PATH + objChildArray[b]['ICON_SRC'], AC_IMG_PATH + objChildArray[b]['ICON_SRC']);
					node.add(objChildnode);
				}			
				node.expand();
		    }
		}					
	}
	if(i>0){
		$("divAttribClass").innerHTML = gTree;
		var divs = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttribClass') } );
	    for(var m = 0; m < divs.length; m++)
	    {
	    	divs[m].ondblclick = findChild;
	    }
	}

}
function findChildGroup(){
	var i= 0;
	var nodes = otherTree.childNodes;
	var node = repeatFind(otherTree, this.id);
	if(node != null)
	{
		if(node.param == null){
			return ;
		}
//		插入对象的类型
	 if(node.param.objFag)
		{
		    if(node.childNodes.length == 0)
		    {
//				var objId = node.param.objId ;
//				getobjClass(objId);
				var objId = node.param.objId ;
				var objclassid = node.param.classId ;
				var id = "";
				getobjClass(objId);				
				var classidarr=[];
				if(objClassArray){
					for(var r=0; r<objClassArray.length ; r++)
					{
						classidarr.push(objClassArray[r].CLASS_ID);
					}
					id = classidarr.join(",");
				}else{
					objClassArray = [];
				}
							
				var nodeAttrClass = getSubTypeBYObj(objclassid,id);
				if(nodeAttrClass){
					for(var c=0; c<nodeAttrClass.length ; c++){
						var qobjClass = {CLASS_ID:nodeAttrClass[c].CLASS_ID,
											CLASS_NAME:nodeAttrClass[c].CLASS_NAME,
											ICON_SRC:nodeAttrClass[c].ICON_SRC,
											PARENT_ID:objId};
						objClassArray.push(qobjClass);
					}
				}
				for(var b=0; b<objClassArray.length ; b++)
				{
			    	i++;
			    	var num = getChildObjNum(objClassArray[b]['PARENT_ID'],objClassArray[b]['CLASS_NAME']);
					var objClsssnode =  new JetsenWeb.UI.TreeItem(objClassArray[b]['CLASS_NAME']+num, "javascript:loadObjectListInClass(" + objClassArray[b]['PARENT_ID'] +  ",'" + objClassArray[b]['CLASS_NAME'] + "',"+ objClassArray[b]['CLASS_ID'] +","+objId+")", null, AC_IMG_PATH + objClassArray[b]['ICON_SRC'], AC_IMG_PATH + objClassArray[b]['ICON_SRC']);					
//					var objClsssnode =  new JetsenWeb.UI.TreeItem(objClassArray[b]['CLASS_NAME'], "javascript:loadObjectListInClass(" + objClassArray[b]['PARENT_ID'] +  ",'" + objClassArray[b]['CLASS_NAME'] + "',"+ objClassArray[b]['CLASS_ID'] +")", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {parentId:objClassArray[b]['PARENT_ID'],className:objClassArray[b]['CLASS_NAME'],objClassfag:true};
					objClsssnode.param = param;
					node.add(objClsssnode);
				}
				node.expand();
		    }
		}
		//对象的子对象
		else if(node.param.objClassfag)
		{
		    if(node.childNodes.length == 0)
		    {
//		    	var nodeName = node.text;
				var parentId = node.param.parentId ;
				var className= node.param.className ;
				getobjChildClass(parentId,className);
				var len = objChildArray.length;
//				if(!node.renameFag)
//					node.text = nodeName+"("+len+")";
//					node.renameFag = true;
				for(var b=0; b< len; b++)
				{
			    	i++;
//					objChildnode = new JetsenWeb.UI.TreeItem(objChildArray[b]['OBJ_NAME'], "javascript:getobjAttrById(" + objChildArray[b]['OBJ_ID'] + ")", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					objChildnode = new JetsenWeb.UI.TreeItem(objChildArray[b]['OBJ_NAME'], "javascript:getobjAttrById(" + objChildArray[b]['OBJ_ID'] + ")", null, AC_IMG_PATH + objChildArray[b]['ICON_SRC'], AC_IMG_PATH + objChildArray[b]['ICON_SRC']);					
					node.add(objChildnode);
				}
				node.expand();
		    }
		}					
		//根据组插入对象
		else if	(node.param.groupFag)
		{
			
		    if(node.childNodes.length == 0)
		    {
				var groupId = node.param.groupId ;
				getobjByGroupId(groupId);
				for(var a=0; a<objArray.length ; a++)
				{
			    	i++;
					var objnode =  new JetsenWeb.UI.TreeItem(objArray[a]['OBJ_NAME'], "javascript:getobjAttrById(" +objArray[a]['OBJ_ID']+")", null, AC_IMG_PATH + objArray[a]['ICON_SRC'], AC_IMG_PATH + objArray[a]['ICON_SRC']);					
//					var objnode =  new JetsenWeb.UI.TreeItem(objArray[a]['OBJ_NAME'], "javascript:getobjAttrById(" + objArray[a]['OBJ_ID'] + ")", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {objId:objArray[a]['OBJ_ID'],objFag:true,classId:objArray[a]['CLASS_ID']};
					objnode.param = param;
					node.add(objnode);
				}	
				node.expand();
		    }else {
		    	var children = node.childNodes;
		    	for(var j=0; j<children.length;j++){
		    		if(children[j].param.objFag){
		    			return;
		    		}
		    	}
				var groupId = node.param.groupId ;
				getobjByGroupId(groupId);
				for(var a=0; a<objArray.length ; a++)
				{
			    	i++;
					var objnode =  new JetsenWeb.UI.TreeItem(objArray[a]['OBJ_NAME'], "javascript:getobjAttrById(" +objArray[a]['OBJ_ID']+")", null, AC_IMG_PATH + objArray[a]['ICON_SRC'], AC_IMG_PATH + objArray[a]['ICON_SRC']);										
//					var objnode =  new JetsenWeb.UI.TreeItem(objArray[a]['OBJ_NAME'], "javascript:getobjAttrById(" + objArray[a]['OBJ_ID'] + ")", null, AC_IMG_PATH + "defaulticon.gif", AC_IMG_PATH + "defaulticon.gif");
					var param = {objId:objArray[a]['OBJ_ID'],objFag:true,classId:objArray[a]['CLASS_ID']};
					objnode.param = param;
					node.add(objnode);
				}	
				node.expand();
		    }
		}
	}
	if(i>0){
		$("divAttrNetObj").innerHTML = otherTree;
		var divs = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
	    for(var m = 0; m < divs.length; m++)
	    {
	    	divs[m].ondblclick = findChildGroup;
	    }
	}

}
function repeatFind(parentNode, id){
	var nodes = parentNode.childNodes;
	for(var i = 0; i < nodes.length; i++){
		if(nodes[i].id == id){
			return nodes[i];
		}else{
			var result = repeatFind(nodes[i], id);
			if(result != null){
				return result;
			}
		}
	}
	return null;
}
//IE8下不支持getElementsByClassName，新建一个这个方法
function getElementsByClassName(className,term){
    var parentEle=null;
    if(term.parentObj){ parentEle = typeof term.parentObj=='string' ? document.getElementById(term.parentObj) : term.parentObj;}
    var rt = [],coll= (parentEle==null?document:parentEle).getElementsByTagName(term.tagName||'*');
    for(var i=0;i<coll.length;i++){
        if(coll[i].className.match(new RegExp('(\\s|^)'+className+'(\\s|$)'))){
            rt[rt.length]=coll[i];
        }
    }
    return rt;
}
//查询所有组中的子对象类型
function getobjClass(objId){
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL",2,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));	
	if(objId){
		conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID",objId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));			
	}
	var sqlQuery = new JetsenWeb.SqlQuery();    
	JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"OBJ_ID",PageInfo:null,ResultFields:"a.PARENT_ID,b.CLASS_ID,b.CLASS_NAME,b.ICON_SRC ",               
		QueryTable:queryTable});	
	sqlQuery.Conditions = conditionCollection;	
	sqlQuery.GroupFields = " a.PARENT_ID,b.CLASS_ID,b.CLASS_NAME,b.ICON_SRC ";
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
		objClassArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(objClassArray == null) {
    		objClassArray = "";
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.PARENT_ID', '2']);
}
//查询所有组中的所有子对象
function getobjChildClass(parentId,className){
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL",2,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));	
	if(parentId){
		conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID",parentId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));		
	}
	if(className){
		conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_NAME",className,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));		
	}
	var sqlQuery = new JetsenWeb.SqlQuery();    
	JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"OBJ_ID",PageInfo:null,ResultFields:"a.OBJ_ID,a.OBJ_NAME,a.PARENT_ID,a.CLASS_ID,b.CLASS_NAME,b.ICON_SRC  ",               
		QueryTable:queryTable});	
	sqlQuery.Conditions = conditionCollection;	
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
		objChildArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(objChildArray == null) {
    		objChildArray = "";
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.PARENT_ID', '2']);
}
//根据对象id获取性能数据树
function getobjAttrById(objId){
	if(autoFindFag == 2){
		retsetDefaultsize()
	}
	$("divListPagination").style.display = "none";
	$("myDivListPagination").style.display = "none";
	$("addObjectTo").style.display = "none";
	$("findObject").style.display = "none";
	$("findObjectChild").style.display = "none";
	$("ObjectAttriListLw").style.display = "";
	$("findInstance").style.display = "none";
	$("oldCreate").style.display = "none";
	$("toMonitorObjAttr").style.display = "";
	$("groupDelete").style.display = "none";
	$("deleteObjectMany").onclick = deleteObjAttrIds;
	
	changeBottomButton("", "", "none", "none", "none", "none");
	
	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	hideObjIdlw = objId;
    	renderGrid(ret.resultVal);
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("bmpGetObjAttri", [objId]);
}

function renderGrid(xml){
	$("divElementList").innerHTML = "";
	gGridListObj.columns = [
	 { index: 0, fieldName: "ATTRIB_ID,OBJATTR_ID,OBJ_ID,CLASS_ID", width: 30, align: "center", isCheck: true, checkName: "chkItem" },
	 { index: 1, fieldName: "OBJATTR_ID", width:60, name: "编号"},
	 { index: 2, fieldName: "OBJATTR_NAME", width:406, name: "属性名称"},
     { index: 3, fieldName: "DATA_ENCODING", align: "center", name: "编码"},
     { index: 4, fieldName: "COLL_TIMESPAN", align: "center", name: "采集间隔(s)" },
     { index: 5, fieldName: "ALARM_NAME", width: 160, name: "报警规则" },
     { index: 6, fieldName: "OBJATTR_ID,ATTRIB_ID,OBJ_ID,CLASS_ID", width: 45, align: "center", name: "性能"},
     { index: 7, fieldName: "OBJATTR_ID,OBJ_ID,ATTRIB_ID", width: 45, align: "center", name: "编辑" }];
	gGridListObj.columns[0].checkName = "chk_program";
	gGridListObj.attachAttributes = [
	                      		{ name: "objId", field: "OBJ_ID"},
	                      		{ name: "objattrid", field: "OBJATTR_ID"},
	                      		{ name: "attrid", field: "ATTRIB_ID"},
	                      		{ name: "objId", field: "OBJ_ID"}];

	
	gGridListObj.columns[0].format = function (val,vals){
		if(vals[0] == 0)
		{
			return "";
		}else{
			var params = vals[1]+","+ vals[2]+","+vals[3] ;
			return params;
		}
	}
	gGridListObj.columns[5].format = function (val,vals){
		if(vals[0] == null || vals[0]=="")
		{
			return "";
		}else{
			return val;
		}
	}
	gGridListObj.columns[6].format = function (val,vals){
		if(vals[1] == 0)
		{
			return "";
		}else{
			
			val = "<a href=\"javascript:toPanelMonitor('"+vals[2]+"','"+vals[0]+"','"+vals[3]+"')\">"+
			"<img src='images/viewdatasingle.png' border='0' style='cursor:pointer' title='性能'/></a>";
			return val;
		}
	}
	gGridListObj.columns[7].format = function (val,vals){
		if(vals[2] == 0)
		{
			return "";
		}else{
			val = "<a href='javascript:void(0)' onclick=\"editObjatt('"+vals[0]+"','"+vals[1]+"')\">"+
			"<img src='images/edit.gif' border='0' style='cursor:pointer' title='编辑'/></a>";
			return val;
		}
	}
     gGridListObj.parentId = "0";
     gGridListObj.idField = "OBJATTR_ID";
     gGridListObj.parentField = "ATTRIB_ID";
     gGridListObj.treeControlIndex = 2;
     gGridListObj.treeOpenLevel = 1;
     gGridListObj.dataSource = xml;
     gGridListObj.render("divElementList");
     gGridListObj.colorSelectedRows();
     gGridListObj.ondoubleclick = function(row,col){
    	 editObjatt(row.getAttribute("objattrid"),row.getAttribute("objid"));
	}
}
function getSelectedProgram() {
	//值顺序:ID,LAYER_ID,PROPER_TITLE,TASK_ID
//    var items = [];
    var objs = document.getElementsByName("chk_program");
    var ids = new Array();
    for (var i = 0; i < objs.length; i++) {
        if (objs[i].checked == true) {
        	var pgmVals = objs[i].value.split(",");
        	ids.push(pgmVals[0]);
        }
    }
    return ids;
}
function getSelectedPrams() {
    var objs = document.getElementsByName("chk_program");
    var parm = "";
    for (var i = 0; i < objs.length; i++) {
        if (objs[i].checked == true) {
        	var pgmVals = objs[i].value.split(",");
        	parm = pgmVals[1]+","+pgmVals[2];
        	break;
        }
    }
    return parm;
}
//批量删除对象属性
function deleteObjAttrIds(){
	var idArray = getSelectedProgram();
	if(idArray.length<1){
		jetsennet.alert("请选择要操作的对象属性");
		return ;
	}
	var ids = idArray.join(",");
	jetsennet.confirm("确定删除?", function () {
		var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function (ret) {
	    	getobjAttrById(hideObjIdlw);
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	    ws.call("bmpObjDeleteMany", ["BMP_OBJATTRIB", ids]);
	    return true;
		});
}
function toManyObjAttri(){
	var idArray = getSelectedProgram();
	if(idArray.length<1){
		jetsennet.alert("请选择要操作的对象属性");
		return ;
	}
	var ids = idArray.join(",");
	var params= getSelectedPrams();
	var pgmArray = params.split(",");
	var objId = pgmArray[0];
	var classId = pgmArray[1];
	toPanelMonitor(objId,ids,classId);
}

function deleteObjattrs(){
	var chkIds = "";
	
	if(hideClassTypelw == '100'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm100");
	}else if(hideClassTypelw == '101,106'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm101106");
	}else if(hideClassTypelw == '103'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm103");
	}else if(hideClassTypelw == '102'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm102");
	}else if(hideClassTypelw == '104'){
		chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm104");
	}else if(hideClassTypelw == '105'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm105");
	}else if(hideClassTypelw == '107'){
		chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarm107");
	}else if(hideClassTypelw == '104,105,107'){
		 chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarmother");
	}else if(hideClassTypelw == '107'){
		chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarmother");
	}else if(hideClassTypelw == ""){
		chkIds = JetsenWeb.Form.getCheckedValues("checkObjAttr2Alarmother");
	}
	
	if(chkIds.length == 0) {
		jetsennet.alert("请选择要操作的对象！");
		return;
	}
	var ids = chkIds.join(",");
	if(hideClassTypelw == '103'){
		 var delAttriObj = document.getElementsByName("checkObjAttr2Alarm103");
	     for (var i = 0; i < delAttriObj.length; i++) {
	         if (delAttriObj[i].checked && delAttriObj[i].getAttribute("itemAttriId")=='40001'){
	     		jetsennet.alert("不能删除设备通断性！");
	    		return;
	         }
	     }
	}
	jetsennet.confirm("确定删除?", function () {
		var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.async = false;
	    ws.oncallback = function (ret) {
	    	if(hideClassTypelw == '101,106')
	    	{
	    		onAttribClassChanged();
	    	}
	    	if(hideClassTypelw == '104,105,107')
	    	{
	    		onAttribTypeChange();
	    	}
	    	else
	    	{
	    		refreshObjAttrList(hideClassTypelw);
	    	}
//	    	getObjAttribute(curObjId, hideClassTypelw, '');

	    };
	    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	    ws.call("bmpObjDeleteMany", ["BMP_OBJATTRIB", ids]);
	    return true;
		});
}

//根据对象的子类型  分页显示子对象类表
var ObjClassTypeId = "" ;
var objParentIdlw = "";
var classNameLw = "";
function loadObjectListInClass(objId,className,classId,currid) {
	if(autoFindFag == 2){
		retsetDefaultsize()
	}
    // 设置查询条件为可见
	$("deleteObjectMany").onclick = delElementIds;
	
	$("divListPagination").style.display = "";
	$("myDivListPagination").style.display = "";
	$("groupDelete").style.display = "none";
    $("findObject").style.display = "none";
    $("findObjectChild").style.display = "";  
    $("buttonSearch1").style.display = "";
    $("buttonSearch2").style.display = "none";
	$("findInstance").style.display = "none";
	$("instanceEquiment").style.display = "none";
	$("addObjectTo").style.display = "none";
	$("ObjectAttriListLw").style.display = "none";
	$("toMonitorObjAttr").style.display = "none";
	$("oldCreate").style.display = "";
	$("addSubObject").style.display = "";
	$("newObjElement").style.display = "none";
	$("addObjectInGroup").style.display = "none";
	changeBottomButton("", "none", "none", "none", "none", "none");
	
	objParentIdlw = objId;
	classNameLw = className ;
	if(classId){
		ObjClassTypeId = classId ;
		}
	if(currid){
		curSubObjId=currid ;
	}
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT d.OBJ_ID,max(d.ALARM_LEVEL) as MAXLEVEL from BMP_ALARMEVENT d where (d.EVENT_STATE = 0 OR d.EVENT_STATE = 1) and d.ALARM_LEVEL in(10,20,30,40) group by d.OBJ_ID )", 
			"c", "c.OBJ_ID = a.OBJ_ID", JetsenWeb.TableJoinType.Left));
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	var searchKey = $("txt_Key3").value;
	if(searchKey) {
		conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("a.OBJ_NAME", searchKey, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", objId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL",2,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));	
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_NAME",className,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));	
	var sqlQuery = new JetsenWeb.SqlQuery();   
	sqlQuery.OrderString = gListPaginationChild.orderBy;
	JetsenWeb.extend(sqlQuery,{IsPageResult:1,KeyId:"a.OBJ_ID",PageInfo:gListPaginationChild,ResultFields:"a.*,b.CLASS_NAME,b.ICON_SRC,c.MAXLEVEL",               
		QueryTable:queryTable});	
	sqlQuery.Conditions = conditionCollection;	
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	$('divElementList').innerHTML = JetsenWeb.Xml.transformXML("xslt/objectChild.xslt", ret.resultVal);
        gGridListChild.bind($('divElementList'), $('objTableList'));
        gListPaginationChild.setRowCount($('hidCountChild').value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.PARENT_ID', '2']);
}
function searchChildObj(){
//	gListPaginationChild.currentPage = 1;
	loadObjectListInClass(objParentIdlw,classNameLw,ObjClassTypeId,curSubObjId);
}

var childObjfag 
//批量删除对象
function delElementIds() {
	var chkIds = "";
	var childObjfag = "" ;
	if(1 == deleteGroupObjFag){
		 chkIds = JetsenWeb.Form.getCheckedValues("chkAllGroupObject");
		 if(chkIds.length ==0){		 
			 chkIds = JetsenWeb.Form.getCheckedValues("chkAllObjectChild");
			 if(chkIds.length >0){
				 childObjfag = 1 ;
				 }
		 }
	}else{
		 chkIds = JetsenWeb.Form.getCheckedValues("chkAllObject");
		 if(chkIds.length ==0){
			 chkIds = JetsenWeb.Form.getCheckedValues("chkAllObjectChild");
			 if(chkIds.length >0){
				 childObjfag = 1 ;
				 }
		 }
	}
	if(chkIds.length == 0) {
		jetsennet.alert("请选择要操作的对象！");
		return;
	}
	jetsennet.confirm("确定删除？", function (){
			var ids = chkIds.join(",");
			var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.async = false;
			ws.oncallback = function (ret) {
			    if(childObjfag == 1){
//			    	loadObjectListInClass(objParentIdlw,classNameLw,ObjClassTypeId,curSubObjId) ;
			    	loadObjectListInClass(objParentIdlw,classNameLw,ObjClassTypeId,curSubObjId);
			    }else{
					if(1 == deleteGroupObjFag) {
			        	showObjectList(currentGroupId, currentGroupType);
			        }else {
			        	loadObjectList();
			        }
			    }
		    	refreshTreeChange();
			};
			ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
			ws.call("bmpObjDeleteMany", ["BMP_OBJECTMANY", ids]);
			return true;      
    });
}

//查询所有组中的对象
function getobjByGroupId(groupId){
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJ2GROUP", "c", "a.OBJ_ID = c.OBJ_ID", JetsenWeb.TableJoinType.Left));
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL",2,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));	
	if(groupId){
		conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("c.GROUP_ID", groupId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	var sqlQuery = new JetsenWeb.SqlQuery();    
	JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"OBJ_ID",PageInfo:null,ResultFields:"a.OBJ_ID,a.OBJ_NAME,a.PARENT_ID,a.CLASS_ID , c.GROUP_ID,b.ICON_SRC ",               
		QueryTable:queryTable});	
	sqlQuery.Conditions = conditionCollection;	
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	objArray = "";
		objArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(objArray == null) {
    		objArray = "";
    	}
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.OBJ_ID', '2']);
}
//编辑属性
function editObjatt(objattrId, attrType){
//	function editTimeSpanObjAttr(objattrId, attrType)
//	{
		$("textParamName").innerHTML = "参数：";
		$("trvisvisable").style.display = "none";
		var areaElements = JetsenWeb.Form.getElements('divEditTimeSpanObjattr');
	    JetsenWeb.Form.resetValue(areaElements);
	    JetsenWeb.Form.clearValidateState(areaElements);    
	    var sqlQuery = new JetsenWeb.SqlQuery();
		var queryTable = JetsenWeb.createQueryTable("BMP_OBJATTRIB", "");
		var conditions = new JetsenWeb.SqlConditionCollection();
		conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("OBJATTR_ID", objattrId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.Numeric));
		JetsenWeb.extend(sqlQuery, { KeyId : "", QueryTable : queryTable, Conditions : conditions });
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			var objClassIdentify = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
			$("hid_OBJATTR_ID1").value = valueOf(objClassIdentify, "OBJATTR_ID", "");
			$("txt_OBJATTR_NAME1").value = valueOf(objClassIdentify, "OBJATTR_NAME", "");
			$("sel_DATA_ENCODING1").value =  valueOf(objClassIdentify, "DATA_ENCODING", "");
			$("txt_OBJATTR_PARM1").value =  valueOf(objClassIdentify, "ATTRIB_PARAM", "");			
//			setSelectedValue($("sel_DATA_ENCODING1"), valueOf(objClassIdentify, "DATA_ENCODING", ""));
			$("txt_COLL_TIMESPAN1").value = valueOf(objClassIdentify, "COLL_TIMESPAN", "");
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

		var dialog = new JetsenWeb.UI.Window("show-EidtTimeSpanObjAttr-win");
		JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 310, height: 180 }, title: "编辑对象属性" });
		dialog.controls = ["divEditTimeSpanObjattr"];
		dialog.onsubmit = function() {
			if (JetsenWeb.Form.Validate(areaElements, true)) {
				var oObjAttr = {
						OBJATTR_ID: $("hid_OBJATTR_ID1").value
						, OBJATTR_NAME: $("txt_OBJATTR_NAME1").value
						, DATA_ENCODING: $("sel_DATA_ENCODING1").value
						, COLL_TIMESPAN: getSelectedValue($("txt_COLL_TIMESPAN1"))
		            }; 
				 var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		         ws.soapheader = JetsenWeb.Application.authenticationHeader;
		         ws.oncallback = function (ret) {
		        	 getobjAttrById(attrType);
		             JetsenWeb.UI.Windows.close("show-EidtTimeSpanObjAttr-win");
		         };
		         ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
		         ws.call("bmpObjUpdate", ["BMP_OBJATTRIB", JetsenWeb.Xml.serializer(oObjAttr, "BMP_OBJATTRIB")]);
			}
		};
		dialog.showDialog();
//	}
}

function addObjectChild()
{ 
	getObjectCollId();
	var subClassId = ObjClassTypeId;
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
//    		showSubObject();
        	refreshTreeChange();
    		JetsenWeb.UI.Windows.close("show-subObject-win");
    		loadObjectListInClass(objParentIdlw,classNameLw,ObjClassTypeId,curSubObjId);
    		
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
		var gGridList = new JetsenWeb.UI.GridList();
        gGridList.bind($("divSubObjectListTable"), $("subObjectList"));
	};
	ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("bmpGetSubObject", [curSubObjId, subClassId, objectCollId]);
}


function addObjInGroup() {
    newObjFlag = true;//新建对象标识
	$("objectgroupcontent").style.display = "";
	$("objectgroupcontrol").style.display = "";
   	$("tdClassGroup").style.display = "none";
   	$("collectorSelectLabel").style.display = "";
   	$("tdClassObject").style.display = "";
   	$("colloctSelector").disabled = "";
   	$("tr2").style.display = "none";
   	$("colloctorLabel").style.display = "";
    curObjId = -1;
    clearObjDialog();
    initSelList();
    setDefaultInfo();
    setClassToNewElement();
    setSelectedValue($("sel_OBJ_STATE"), 100);
    getManufactureByClassId(curClass["CLASS_ID"]);
    $("txt_CLASS_TYPE").value = curClass["CLASS_NAME"];
    $("sel_OBJ_STATE").style.display = "";
    $("collectorInputId").style.display = "none";
    $("collectorSelectId").style.display = "";
    el("showGroupOption").options.length = 0;
    $("btnSelGroup1").style.display = "none";
    $("btnSelGroup2").style.display = "";
    showRemindWordCount($("txt_OBJ_DESC").value,$('remindWord'),"30");
//    var classElement = $('selectClassType');
//    classElement.length = 0;
    $('showGroupOption').options.add(new Option(currentGroup.GROUP_NAME, currentGroup.GROUP_ID));
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 450, height:550 }, title: dialogTitleNew });
    dialog.controls = ["divPopWindow"];
    dialog.onsubmit = function () {
    	gruop_ids = "";
        var areaElements = genValidateForms();
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var object = getParams("", "", "");
            object.MAN_ID = $('manufacturer').value;
            if(!validateIP(object.IP_ADDR)) {
                jetsennet.alert("IP填写错误！");
                return;
            }
            var port_adress = parseInt(object.IP_PORT);
            if(port_adress > 65535 || port_adress < 0) {
                jetsennet.alert("端口号只能在0和65535之间！");
                return;
            }
            if(parseInt(getBytesCount($("txt_OBJ_DESC").value))>60){
            	jetsennet.alert("对象描述不能超过30个文字！");
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
            
            var strIp = getAutoObjIpOne(object);
            if(strIp){
		           	 jetsennet.confirm("IP是"+strIp+"的对象已存在，确定要新建吗？", function (){       
		            objectInstanceByEdit(object);
		//            loadObjectList();
		            showObjectList(currentGroupId,currentGroupType) ;
			    	refreshTreeChange();
		            curClass = null;
		            JetsenWeb.UI.Windows.close("new-object-win");
		            return true;
	           	 });
            }else{
            	objectInstanceByEdit(object);
        		//            loadObjectList();
	            showObjectList(currentGroupId,currentGroupType) ;
		    	refreshTreeChange();
	            curClass = null;
	            JetsenWeb.UI.Windows.close("new-object-win");
            }
        }
    }
    dialog.showDialog();
}

//新增=====================================================================================
function  newElementInGroup(){
	if(currentGroup){
		var areaElements = JetsenWeb.Form.getElements('divShowClassType');
		JetsenWeb.Form.resetValue(areaElements);
		JetsenWeb.Form.clearValidateState(areaElements);
		var dialog = new JetsenWeb.UI.Window("new-object-win");
		JetsenWeb.extend(dialog, {
			submitBox : true,
			cancelBox : true,
			windowStyle : 1,
			maximizeBox : false,
			minimizeBox : false,
			size : {
				width : 350,
				height : 100
			},
			title : "选择对象类型"
		});
		dialog.controls = [ "divShowClassType" ];
		var curClasses =  getClassType() ;
		dialog.onsubmit = function() {
			if (JetsenWeb.Form.Validate(areaElements, true)) {
				var classtypeId = getSelectedValue($("selectClassType"));
				if(classtypeId == "-1")
				{
					jetsennet.alert("请选择对象类型！");
					return;
				}
	        	for(var i=0; i<curClasses.length; i++)
	            {
	            	var object = curClasses[i];
	            	if(object.CLASS_ID == classtypeId ){
	            		curClassGroup = object;
	            		break;
	            	}
	            }
	        	JetsenWeb.UI.Windows.close("new-object-win");
	        	curClass = curClassGroup ;
	        	addObjInGroup();
			}
		};
		dialog.showDialog();
	}else{
		jetsennet.alert("请选择对象组！");
	}
}

var curClassGroup;
//查询子对象类别
function getClassType()
{    
	var arrayClasstType = "";	
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
	//queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", "2", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
	queryConditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_LEVEL", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.Numeric));
	queryConditions.SqlConditions.push(subConditions);
	sqlQuery.Conditions = queryConditions;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
    ws.oncallback = function (ret) {
         arrayClasstType = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        var classElement = $('selectClassType');
        classElement.length = 0;
        classElement.options.add(new Option("请选择", -1));
        if(arrayClasstType != null && arrayClasstType != "" && arrayClasstType.length != 0)
        {
        	for(var i=0; i<arrayClasstType.length; i++)
            {
            	var object = arrayClasstType[i];
            	classElement.options.add(new Option(object.CLASS_NAME, object.CLASS_ID));
            }
        }
    };
    ws.onerror = function (ex) {
        jetsennet.error(ex + "！");
        return;
    };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
    return arrayClasstType;
}

//加载子对象类型
function getSubTypeBYObj(classID,id) {
	var typeNodes = "";
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_ATTRIBCLASS", "aa");
	var condition = new JetsenWeb.SqlConditionCollection();
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_CLASS2CLASS", "a", "a.CLASS_ID=aa.CLASS_ID", JetsenWeb.TableJoinType.Right));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", classID, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.USE_TYPE", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	if(id){
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("aa.CLASS_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn,
				JetsenWeb.SqlParamType.String));
	}
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "aa.CLASS_ID", PageInfo : null, ResultFields : "aa.CLASS_ID as CLASS_ID ,aa.CLASS_NAME as CLASS_NAME,aa.ICON_SRC as ICON_SRC ",
		QueryTable : queryTable });
	sqlQuery.Conditions = condition;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(sResult) {
		typeNodes = JetsenWeb.Xml.toObject(sResult.resultVal, "Record");
		if(typeNodes == null) {
			typeNodes = "";
	  	}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	return typeNodes ;
}


//刷新属性分类树
function refreshTreeGroup(){
    createGroupTree();
    $("divAttrNetObj").innerHTML = otherTree;
    otherTree.expandAll();
    if(tableValue == 2){
    	gTabPane.select(1);
    }else{
    	gTabPane.select(0);
    }
    var divs2 = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
    for(var j = 0; j < divs2.length; j++)
    {
    	divs2[j].ondblclick = findChildGroup;
    }
}


//刷新属性分类树
function refreshTreeChange(){
    attrClsArray = null;
	var userId = jetsennet.Application.userInfo.UserId;
	var fagAdmin = isadmin(userId);
	if(fagAdmin == "false"){
	    if (!attrClsArray) {
	        getAllAttrCls();
	    }
	    createGroupTree();
	    $("divAttribClassView").style.display = "none";
	    $("divAttribClass").style.display = "none";
	    $("divAttrNetObj").innerHTML = otherTree;
	    otherTree.expandAll();
	    gTabPane.select(1);
	    var divs2 = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
	    for(var j = 0; j < divs2.length; j++){
	    	divs2[j].ondblclick = findChildGroup;
	    }
	}else{
	    genAttrClsTree();
	    createGroupTree();
	    $("divAttribClass").innerHTML = gTree;
	    $("divAttrNetObj").innerHTML = otherTree;
	    gTree.expandAll();
	    otherTree.expandAll(); 
	    if(tableValue == 2){
	    	gTabPane.select(1);
	    }else{
	    	gTabPane.select(0);

	    }
	    var divs = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttribClass') } );
	    for(var i = 0; i < divs.length; i++){
	    	divs[i].ondblclick = findChild;
	    }
	    var divs2 = getElementsByClassName("webfx-tree-item", {parentObj :$('divAttrNetObj') } );
	    for(var j = 0; j < divs2.length; j++){
	    	divs2[j].ondblclick = findChildGroup;
	    }
	}  
}

function showDeviceInfo()
{
	var str = "<iframe src=" + "flex/ViewDeviceInfo.html?objId=" +  curObjId + "&classId=" + curObjClassId + " width='100%'" + " height='100%'" + "</iframe>";
	$('divDeveiceInfo').innerHTML = str;
	var dialog = new JetsenWeb.UI.Window("show-deviceinfo-win");
	JetsenWeb.extend(dialog, { 
		submitBox : false, 
		cancelBox : false, 
		windowStyle : 1, 
		cancelButtonText : "关闭", 
		maximizeBox : true,
		minimizeBox : true, size : { width : 730, height : 330 }, 
		title : "设备基本信息" });
	dialog.controls = [ "divDeveiceInfo" ];
	dialog.showDialog();
}


//获取全部属性分类
function getAllAttrribClass() {
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
        allClsArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if(allClsArray == null) {
        	allClsArray = [];
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}


function getChildObjNum(objId,className) {
	var length = "";
	var queryTable = JetsenWeb.createQueryTable("BMP_OBJECT", "a");
	queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_ATTRIBCLASS", "b", "a.CLASS_TYPE=b.CLASS_TYPE", JetsenWeb.TableJoinType.Left));
	queryTable.addJoinTable(JetsenWeb.createJoinTable("(SELECT d.OBJ_ID,max(d.ALARM_LEVEL) as MAXLEVEL from BMP_ALARMEVENT d where (d.EVENT_STATE = 0 OR d.EVENT_STATE = 1) and d.ALARM_LEVEL in(10,20,30,40) group by d.OBJ_ID )", 
			"c", "c.OBJ_ID = a.OBJ_ID", JetsenWeb.TableJoinType.Left));
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("a.PARENT_ID", objId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_LEVEL",2,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));	
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("b.CLASS_NAME",className,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));	
	var sqlQuery = new JetsenWeb.SqlQuery();   
	sqlQuery.OrderString = gListPaginationChild.orderBy;
	JetsenWeb.extend(sqlQuery,{IsPageResult:1,KeyId:"a.OBJ_ID",PageInfo:"",ResultFields:"a.OBJ_ID,b.CLASS_NAME",               
		QueryTable:queryTable});	
	sqlQuery.Conditions = conditionCollection;	
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
    	if(records){
    		length = "("+records.length+")";  
    	}else{
    		length = "(0)";  
    	}
           
    };
    ws.onerror = function (ex) { jetsennet.error(ex + "！"); };
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'a.PARENT_ID', '2']);
	return length;
}

function getEquipType(classId){
	for(var i=0,len = VtypeArray.length;i<len;i++){
		if(VtypeArray[i].classId == classId){						
			$(VtypeArray[i].htmlId).getElementsByTagName('a')[0].style.background="#7093DB";//  #8F8FBD  
		}
		else{
			$(VtypeArray[i].htmlId).getElementsByTagName('a')[0].style.background="";
		}
	}
}





