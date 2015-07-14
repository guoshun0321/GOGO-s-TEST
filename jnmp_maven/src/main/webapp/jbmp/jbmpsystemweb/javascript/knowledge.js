JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_jetsentree", "js_htmlbox"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gKnowledgePage = new JetsenWeb.UI.PageBar("Knowledge");
gKnowledgePage.onpagechange = function () { searchKnowledge(); };
gKnowledgePage.orderBy = "order by k.KNOWLEDGE_ID desc";
gKnowledgePage.onupdate = function () {
    $("divKnowledgePage").innerHTML = this.generatePageControl();
};
var gKnowledgeCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gKnowledgePage.setOrderBy(sortfield, desc);
};

var gKnowledgeGridList = new JetsenWeb.UI.GridList("knowledgetype-grid");

var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGE", "k");
gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=k.CREATE_USERID",JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "KNOWLEDGE_ID", ResultFields:"k.*,USER_NAME", PageInfo: gKnowledgePage, QueryTable: gQueryTable });

var knowledgeTypes = {};// 保存要添加的知识库类型（TYPE_ID，TYPE_NAME）
var windowCount = 0;// 窗口的索引
var uploadIntervalId;// 检测上传是否成功的线程

//加载=====================================================================================
function loadKnowledge() {
    gSqlQuery.OrderString = gKnowledgePage.orderBy;
    gSqlQuery.Conditions = gKnowledgeCondition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $("divKnowledgeList").innerHTML = JetsenWeb.Xml.transformXML("xslt/knowledge.xslt", ret.resultVal, [ { name:"CurrentUserId", value:JetsenWeb.Application.userInfo.UserId }
        , { name:"IsAdmin", value:JetsenWeb.Application.userInfo.UserRoles.split(",").contains("1") ? 1 : 0 } ]);
        gGridList.bind($("divKnowledgeList"), $("tabKnowledge"));
        gKnowledgePage.setRowCount($("hid_KnowledgeCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
function searchKnowledge() {
    gKnowledgeCondition.SqlConditions = [];
    gSqlQuery = new JetsenWeb.SqlQuery();
    gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGE", "k");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=k.CREATE_USERID",JetsenWeb.TableJoinType.Inner));
    
    if ($("txtKnowledgeTitle").value != "") {
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("k.KNOWLEDGE_TITLE", $("txtKnowledgeTitle").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if ($("txtKnowledgeSummary").value != "") {
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("k.KNOWLEDGE_SUMMARY", $("txtKnowledgeSummary").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if (getSelectedValue($("cboUser")) != "") {
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("k.CREATE_USERID", getSelectedValue($("cboUser")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    if ($("hidSearchTypeId").value != "") {
        gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_KNOWLEDGE2TYPE","kt","kt.KNOWLEDGE_ID=k.KNOWLEDGE_ID",JetsenWeb.TableJoinType.Inner));
        gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_KNOWLEDGETYPE","t","kt.TYPE_ID=t.TYPE_ID ",JetsenWeb.TableJoinType.Inner));
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("t.TYPE_ID", $("hidSearchTypeId").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
    }
    
    var classid = getSelectedValue($("cboClassId"));
    if(classid){
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("k.CLASS_ID", classid, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
    }
    var alarmid = getSelectedValue($("cboAlarmId"));
    if (alarmid) {
        gKnowledgeCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("k.ALARM_ID", alarmid, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "KNOWLEDGE_ID", ResultFields:"k.*,USER_NAME", PageInfo: gKnowledgePage, QueryTable: gQueryTable });
    loadKnowledge();
}
//删除=====================================================================================
function deleteKnowledge(keyId) {
	jetsennet.confirm("确定删除？", function () {
	    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function (ret) {
	        searchKnowledge();
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex); };
	    ws.call("bmpObjDelete", ["BMP_KNOWLEDGE", keyId]);
	    return true;
    });
}

//新增=====================================================================================
function newKnowledge() {
	
    var areaElements = JetsenWeb.Form.getElements("divKnowledge");
    JetsenWeb.Form.resetValue(areaElements);
    
    // 知识库->筛选模式下->新建文章修改*2013.09.11*
    $('c_class_id').value = $('cboClassId').value  
    $('c_alarm_id').value = $('cboAlarmId').value;
    JetsenWeb.Form.clearValidateState(areaElements);
    
    //	将所有的附件清空
    $("newDivAttachmentList").innerHTML = "" ;
    //	附件个数置为0
    spanNum = 0 ;
    // 	新增一行
    addAttachmentSpan();
    $("haveDivAttachmentList").innerHTML = "";
	$("haveDivAttachmentList").style.display = "none" ;
    
    
    //	剩余字数
    showRemindWordCount($("txt_KNOWLEDGE_SUMMARY").value,$('remindWord'),"60");
    //	弹出对话框
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, 
    						size: { width: 790, height: 600 }, title: "新建知识库文章" });
    //	弹出框加载完成后，渲染在线编辑器
    dialog.onloaded = function(){
    	createEditor('#txt_KNOWLEDGE_CONTENT');
    };
    //	弹出框关闭之前，卸载在线编辑器
    dialog.onclosed = function(){
    	removeEditor('#txt_KNOWLEDGE_CONTENT');
    };
    dialog.controls = ["divKnowledge"]; 
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var date = new Date();
        	var objKnowledge = {
                KNOWLEDGE_TITLE: $("txt_KNOWLEDGE_TITLE").value
              , KNOWLEDGE_SUMMARY: $("txt_KNOWLEDGE_SUMMARY").value
              , KNOWLEDGE_CONTENT: "@" + $("txt_KNOWLEDGE_CONTENT").value + "@"
              , CREATE_USERID: JetsenWeb.Application.userInfo.UserId
              , CREATE_TIME: date.toDateTimeString()
              , UPDATE_TIME: date.toDateTimeString()
              , CLICK_COUNT: 0
              , COMMENT_COUNT: 0
              , KNOWLEDGE_TYPES: getAllValues($("cbo_KNOWLEDGE_TYPE")).join(",")
              , CLASS_ID: getSelectedValue($("c_class_id"))
              , ALARM_ID: getSelectedValue($("c_alarm_id"))
            };
        	if(parseInt(getBytesCount($("txt_KNOWLEDGE_SUMMARY").value))>120){
            	jetsennet.alert("摘要不能超过60个文字！");
            	return;
            }

            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	
            	//	基本信息已经增加完成，接下来是处理上传的文件
            	//	此处增加文件上传的逻辑
            	//	知识库Id
            	var kID = ret.resultVal ;
            	uploadFile(kID) ;
                JetsenWeb.UI.Windows.close("new-object-win");
                searchKnowledge();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjInsert", ["BMP_KNOWLEDGE", JetsenWeb.Xml.serializer(objKnowledge, "BMP_KNOWLEDGE")]);
        }
    };
    $("cbo_KNOWLEDGE_TYPE").length = 0;
    
    dialog.showDialog();
    
}
//编辑=====================================================================================
function editKnowledge(keyId) {
    var areaElements = JetsenWeb.Form.getElements("divKnowledge");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    //	将所有的附件清空
    $("newDivAttachmentList").innerHTML = "" ;
    //	附件个数置为0
    spanNum = 0 ;
    // 	新增一行
    addAttachmentSpan();
    
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("A.KNOWLEDGE_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    
    var queryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGE","A");
    queryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_KNOWLEDGEATTACHMENT","B","A.KNOWLEDGE_ID = B.KNOWLEDGE_ID",JetsenWeb.TableJoinType.Left));
    
    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "A.KNOWLEDGE_ID", PageInfo: null, QueryTable: queryTable, ResultFields:"A.*,B.ATTACHMENT_ID,B.ATTACHMENT_NAME,B.ATTACHMENT_PATH"});
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false ;
    ws.oncallback = function (ret) {
        var objKnowledge = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
        $("txt_KNOWLEDGE_TITLE").value = valueOf(objKnowledge, "KNOWLEDGE_TITLE", "");
        $("txt_KNOWLEDGE_SUMMARY").value = valueOf(objKnowledge, "KNOWLEDGE_SUMMARY", "");
        var knowledgeContent = valueOf(objKnowledge, "KNOWLEDGE_CONTENT", "@@");
        $("txt_KNOWLEDGE_CONTENT").value = knowledgeContent.substring(1, knowledgeContent.length - 1);
        setSelectedValue($("c_class_id"), objKnowledge.CLASS_ID);
        setSelectedValue($("c_alarm_id"), objKnowledge.ALARM_ID);
        loadKnowledgeTypeByKnowledge(keyId);
        showRemindWordCount($("txt_KNOWLEDGE_SUMMARY").value,$('remindWord'),"60");
        
        //	绑定包含的附件信息
        var attachments =  JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        
        //	循环生成多行的已经上传的附件
        var htmlStr = [] ;
        for(var i=0; i<attachments.length; i++)
        {
        	var attach = attachments[i] ;
        	if(!attach.ATTACHMENT_ID)
        	{
        		continue ;
        	}
        	htmlStr.push("<div id=\"haveDivAttachment"+i+"\">");
        		//	获得后缀
	        	var suffix = attach["ATTACHMENT_NAME"].substring(attach["ATTACHMENT_NAME"].lastIndexOf(".")+1, attach["ATTACHMENT_NAME"].length) ;
	        	htmlStr.push("<a href=\"../../servlet/BMPFileDownloadServlet?name="+attach.ATTACHMENT_NAME+"&path="+attach.ATTACHMENT_PATH+"\" target=\"_blank\" style=\"margin-left: 10px;\">");
	        	
	        	htmlStr.push(""+attach.ATTACHMENT_NAME);
	        	htmlStr.push("</a>");
	        	htmlStr.push("<span style=\"float: right; margin-right: 13px;\">");
	        	htmlStr.push("<a href=\"javascript:deleteAttachment('"+attach.ATTACHMENT_PATH+"', "+attach.ATTACHMENT_ID+", 'haveDivAttachment"+i+"');\" style=\"color: red; \" >");
	        	htmlStr.push("删除");
	        	htmlStr.push("</a>");
	        	var visibility = "hidden" ;
	        	if(suffix.toLowerCase() == "pdf"){
	        		visibility = "" ;
	        	}
	        	htmlStr.push("<a href=\"../../"+attach.ATTACHMENT_PATH+"\" target=\"_blank\" style=\"margin-left: 15px; visibility : "+visibility+"; \">");
	        	htmlStr.push("查看");
	        	htmlStr.push("</a>");
		        htmlStr.push("</span>");
        	htmlStr.push("</div>");
        	
        }
        if(htmlStr.length > 0){
        	$("haveDivAttachmentList").innerHTML = htmlStr.join("");
        	$("haveDivAttachmentList").style.display = "" ;
        }
        
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 790, height: 600 }, title: "编辑知识库文章" });
    //	弹出框加载完成后，渲染在线编辑器
    dialog.onloaded = function(){
    	createEditor('#txt_KNOWLEDGE_CONTENT');
    };
    //	弹出框关闭之前，卸载在线编辑器
    dialog.onclosed = function(){
    	removeEditor('#txt_KNOWLEDGE_CONTENT');
    };
    dialog.controls = ["divKnowledge"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var oKnowledge = {
            	KNOWLEDGE_ID: keyId
              , KNOWLEDGE_TITLE: $("txt_KNOWLEDGE_TITLE").value
              , KNOWLEDGE_SUMMARY: $("txt_KNOWLEDGE_SUMMARY").value
              , KNOWLEDGE_CONTENT: "@" + $("txt_KNOWLEDGE_CONTENT").value + "@"
              , UPDATE_TIME: new Date().toDateTimeString()
              , KNOWLEDGE_TYPES: getAllValues($("cbo_KNOWLEDGE_TYPE")).join(",")
              , CLASS_ID: getSelectedValue($("c_class_id"))
              , ALARM_ID: getSelectedValue($("c_alarm_id"))
            };
            
        	if(parseInt(getBytesCount($("txt_KNOWLEDGE_SUMMARY").value))>120){
            	jetsennet.alert("摘要不能超过60个文字！");
            	return;
            }
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	
            	//	保存完成之后，开始上传附件
            	uploadFile(keyId);
            	
                JetsenWeb.UI.Windows.close("edit-object-win");
                searchKnowledge();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjUpdate", ["BMP_KNOWLEDGE", JetsenWeb.Xml.serializer(oKnowledge, "BMP_KNOWLEDGE")]);
        }
    };
    dialog.showDialog();
}
//初始化===================================================================================
function pageInit() {
    loadUser();
    initKnowledgeType();
    
    loadAllClass();
    loadAllAlarm();
    var classId = JetsenWeb.queryString("class_id");
    var alarmId = JetsenWeb.queryString("alarm_id");
    if(classId && alarmId)
    {
    	setSelectedValue($("cboClassId"), classId);
    	setSelectedValue($("cboAlarmId"), alarmId);
    }
    searchKnowledge();

    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType: 1,fixControlIndex: 0, splitBorder: 0, showSplit: true, enableResize: false, splitTitle : "divListTitle", splitSize : 27});

    var frameTop = new JetsenWeb.UI.PageItem("divTop");
    frameTop.size = { width: 0, height: 30 };
    var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameContent.addControl(new JetsenWeb.UI.PageItem("divKnowledgeList"));
    frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));

    gFrame.addControl(frameTop);
    gFrame.addControl(frameContent);

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
}

function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

// 选择知识库类别
function chooseKnowledgeType() {
	var areaElements = JetsenWeb.Form.getElements("divKnowledgeTypeContent");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("divKnowledgeTypeContent").innerHTML = "数据加载中...";

    var dialog = new JetsenWeb.UI.Window("view-knowledge-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 600, height: 400 }, title: "选择知识库类别" });
    dialog.controls = ["divKnowledgeTypeContent"];
    dialog.onsubmit = function () {
    	var cboKnowledgeType = $("cbo_KNOWLEDGE_TYPE");
        var knowledgeIds = JetsenWeb.Form.getCheckedValues("chkKnowledgeType");
        var length = knowledgeIds.length;
        for (var i = 0; i < length; i++) {
        	cboKnowledgeType.options.add(new Option(knowledgeTypes[knowledgeIds[i]], knowledgeIds[i]));
        }
        JetsenWeb.UI.Windows.close("view-knowledge-win");
    };
    loadKnowledgeType();
	dialog.showDialog();
}

// 初始化知识库类别组合框
function initKnowledgeType() {
$("divKnowledgeTypeTree").innerHTML = "";
	
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGETYPE", "");
    var gCondition = new JetsenWeb.SqlConditionCollection();
    JetsenWeb.extend(gSqlQuery, { KeyId: "TYPE_ID", QueryTable: gQueryTable, ResultFields: "*" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
    	var gKnowledgeTypeTree = JetsenWeb.UI.Tree.createTree("parent-tree", ret.resultVal, { parentId: "0", parentField: "PARENT_ID", itemName: "Record", textField: "TYPE_NAME", valueField: "TYPE_ID", showCheck: false, paramFields: "TYPE_ID,TYPE_NAME,PARENT_ID"});
    	gKnowledgeTypeTree.addItem(new JetsenWeb.UI.TreeItem("没有父级", null, null, null,{ID:0, NAME:""}));
    	gKnowledgeTypeTree.onclick = function(item) {
    		$("cboKnowledgeType").value = valueOf(item.treeParam, "TYPE_NAME", "");
    		$("cboKnowledgeType").focus();
            $("hidParentId").value = valueOf(item.treeParam, "TYPE_ID", "");
            var typeIds = getNodeTreeTypeId([ $("hidParentId").value == "" ? 0 : $("hidParentId").value ], $("hidParentId").value == "" ? "" : [ $("hidParentId").value ]);
            $("hidSearchTypeId").value = typeIds.length > 0 ? typeIds.join(",") : "";
        };
        $("divKnowledgeTypeTree").appendChild(gKnowledgeTypeTree.render());
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 加载知识库类别
function loadKnowledgeType() {
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGETYPE", "");
    var gCondition = new JetsenWeb.SqlConditionCollection();
    var knowledgeTypeIds = getAllValues($("cbo_KNOWLEDGE_TYPE")).join(",");
	if (knowledgeTypeIds != "")
	{
		gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("TYPE_ID", knowledgeTypeIds, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotIn, JetsenWeb.SqlParamType.Numeric));
	}
    JetsenWeb.extend(gSqlQuery, { KeyId: "TYPE_ID", QueryTable: gQueryTable, Conditions: gCondition, ResultFields: "*" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	knowledgeTypes = {};
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records)
		{
			var length = records.length;
			for(var i = 0; i < length; i++)
			{
				var knowledgeTypeInfo = records[i];
				knowledgeTypes[knowledgeTypeInfo["TYPE_ID"]] = knowledgeTypeInfo["TYPE_NAME"];
			}
		}
        renderTreeGrid(ret.resultVal);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 显示表格
function renderTreeGrid(xml){
	$("divKnowledgeTypeContent").innerHTML = "";

	gKnowledgeGridList.columns = [
	 { index: 0, fieldName: "TYPE_ID", width:30, align: "center", isCheck: true, checkName: "chkKnowledgeType"},
	 { index: 1, fieldName: "TYPE_NAME", width:150, name: "类别名称"},
     { index: 2, fieldName: "TYPE_DESC", width:395, name: "类别描述" }];

	gKnowledgeGridList.parentId = 0;
	gKnowledgeGridList.idField = "TYPE_ID";
	gKnowledgeGridList.parentField = "PARENT_ID";
	gKnowledgeGridList.treeControlIndex = 1;
	gKnowledgeGridList.treeOpenLevel = 0;
	gKnowledgeGridList.dataSource = xml;
	gKnowledgeGridList.render("divKnowledgeTypeContent");
	gKnowledgeGridList.colorSelectedRows();
}

// 获取节点树的类型ID集合
function getNodeTreeTypeId(parentIds, typeIds)
{
	if (!parentIds)
	{
		parentIds = [ 0 ];
	}
	if (!typeIds)
	{
		typeIds = [];
	}
	var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGETYPE", "");
    var gCondition = new JetsenWeb.SqlConditionCollection();
	if (parentIds.length > 0)
	{
		gCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("PARENT_ID", parentIds.join(","), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.Numeric));
	}
    JetsenWeb.extend(gSqlQuery, { KeyId: "TYPE_ID", QueryTable: gQueryTable, Conditions: gCondition, ResultFields: "TYPE_ID" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records)
		{
			var length = records.length;
			var parentTypeIds = [];
			for(var i = 0; i < length; i++)
			{
				var knowledgeTypeInfo = records[i];
				typeIds.push(knowledgeTypeInfo["TYPE_ID"]);
				parentTypeIds.push(knowledgeTypeInfo["TYPE_ID"]);
			}
			getNodeTreeTypeId(parentTypeIds, typeIds);
		}
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
    return typeIds;
}

// 加载知识库文章对应的知识库类型
function loadKnowledgeTypeByKnowledge(knowledgeId)
{
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGE2TYPE","kt");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_KNOWLEDGETYPE","t","t.TYPE_ID=kt.TYPE_ID",JetsenWeb.TableJoinType.Inner));
	var condition = new JetsenWeb.SqlConditionCollection();
	if (knowledgeId != -1)
	{
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("KNOWLEDGE_ID", knowledgeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	}
	JetsenWeb.extend(gSqlQuery,{KeyId:"",QueryTable:gQueryTable,Conditions:condition,ResultFields:"t.TYPE_ID,t.TYPE_NAME"});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
		var cboKnowledgeType = $("cbo_KNOWLEDGE_TYPE");
		cboKnowledgeType.length = 0;
		var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (records)
		{
			var length = records.length;
			for(var i = 0; i < length; i++)
			{
				var knowledgeTypeInfo = records[i];
				cboKnowledgeType.options.add(new Option(knowledgeTypeInfo["TYPE_NAME"], knowledgeTypeInfo["TYPE_ID"]));
			}
		}
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("bmpObjQuery",[gSqlQuery.toXml()]);
}

// 加载用户列表
function loadUser() {
    var gSqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("UUM_USER", "");
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("STATE", 0, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    JetsenWeb.extend(gSqlQuery, { KeyId: "", QueryTable: gQueryTable, Conditions: condition, ResultFields: "ID,USER_NAME" });
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var cboUser = $("cboUser");
        cboUser.length = 0;
        cboUser.options.add(new Option("请选择", ""));
        var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
        if (records) {
            var length = records.length;
            for (var i = 0; i < length; i++) {
                var userInfo = records[i];
                cboUser.options.add(new Option(userInfo["USER_NAME"], userInfo["ID"]));
            }
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}

// 加载知识库文章内容
function loadKnowledgeDetail(knowledgeId, clickCount, createUserId)
{
	// 增加点击数，并改变修改时间
	var oKnowledge = {
    	KNOWLEDGE_ID: knowledgeId
      , CLICK_COUNT: ++clickCount
      , UPDATE_TIME: new Date().toDateTimeString()
      , ONLY_VIEW: true
    };
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        searchKnowledge();
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjUpdate", ["BMP_KNOWLEDGE", JetsenWeb.Xml.serializer(oKnowledge, "BMP_KNOWLEDGE")]);
    
	// 显示文章标题和内容
	var knowledgeTitle = "";
	$("hidViewKnowledgeId").value = knowledgeId;
	$("hidCreateUserId").value = createUserId;
	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGE","");
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("KNOWLEDGE_ID", knowledgeId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery,{KeyId:"",QueryTable:gQueryTable,Conditions:condition,ResultFields:"KNOWLEDGE_TITLE,KNOWLEDGE_CONTENT"});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(ret)
    {
    	var objKnowledge = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
    	knowledgeTitle = valueOf(objKnowledge, "KNOWLEDGE_TITLE", "");
    	$("divKnowledgeTitle").innerHTML = knowledgeTitle;
    	var knowledgeContent = valueOf(objKnowledge, "KNOWLEDGE_CONTENT", "@@");
        $("divKnowledgeContent").innerHTML = knowledgeContent.substring(1, knowledgeContent.length - 1);
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("bmpObjQuery",[gSqlQuery.toXml()]);
    
    windowCount++;
    var dialog = new JetsenWeb.UI.Window("view-object-win" + windowCount);
    var divContent = $("divKnowledgeContentContent").cloneNode(true);
    divContent.id = "divKnowledgeContentContent" + windowCount;
    for (var i = 0; i < divContent.childNodes.length; i++)
    {
    	if (divContent.childNodes[i].id)
    	{
    		if (divContent.childNodes[i].id == "divKnowledgeComment")
    		{
    			var txtComment = divContent.childNodes[i].getElementsByTagName("textarea")[0];
    			txtComment.id += windowCount;
    		}
    		divContent.childNodes[i].id += windowCount;
    	}
    }
    document.body.appendChild(divContent);
    // 显示附件列表
    loadAttachment(windowCount);
    // 显示评论列表
    loadComment(windowCount);
    JetsenWeb.extend(dialog, { cancelBox: true, windowStyle: 0, maximizeBox: true, minimizeBox: true, size: { width: 790, height: 600 },windowStyle : 1, title: "文章详细内容 - " + (knowledgeTitle.length > 40 ? knowledgeTitle.substring(0, 40) + "……" : knowledgeTitle), cancelButtonText:"关闭" });
    dialog.controls = [divContent.id];
    dialog.show();
}

// 上传文件
function uploadFile(knowledgeId)
{
	
	//	获取到所有需要上传的文件
	var areaElements = JetsenWeb.Form.getElements("newDivAttachmentList");
    var fileRandomName = "" ;
	
	for(var i=0; i<areaElements.length; i++){
		
		var ele = areaElements[i] ;
		var filePath = ele.value ;
		if (filePath.length == 0)
		{
			$("newDivAttachmentList").removeChild(ele.parentElement) ;
			continue ;
		}
		
		var fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
		
		var randomName = new Date().getTime() + i + fileName.substring(fileName.lastIndexOf("."), fileName.length);
		
		fileRandomName = fileRandomName + "," + randomName ;
		
		newAttachment(fileName, randomName, knowledgeId);
	}
	
	//	需要 上传
	if(fileRandomName.length > 0){
		
		fileRandomName = fileRandomName.substring(1, fileRandomName.length);
		var form = $("formUpload");
		form.action = "../../servlet/BMPFilesBatchUploadServlet?fileName=" + fileRandomName + "&uploadPath=upload/knowledge/attachment/knowledge/" + knowledgeId;
		form.submit();
		
	}
}

// 新增附件信息
function newAttachment(fileName, fileRandomName, knowledgeId)
{
    var objAttachment = {
        KNOWLEDGE_ID: knowledgeId
      , ATTACHMENT_NAME: fileName
      , ATTACHMENT_PATH: "upload/knowledge/attachment/knowledge/" + knowledgeId + "/" + fileRandomName
      , CREATE_TIME: new Date().toDateTimeString()
    };
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false ;
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjInsert", ["BMP_KNOWLEDGEATTACHMENT", JetsenWeb.Xml.serializer(objAttachment, "BMP_KNOWLEDGEATTACHMENT")]);
}

// 删除附件
function deleteAttachment(filePaths, attachmentId, windowId)
{
	jetsennet.confirm("确定删除？", function () {
		if (attachmentId)
		{
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		    ws.soapheader = JetsenWeb.Application.authenticationHeader;
		    ws.oncallback = function (ret) {
		    	//	windowId 是数字型的，则表示是查看详细的时候点的删除
		    	if (windowId && (typeof windowId == "number"))
		    	{
		    		loadAttachment(windowId);
		    	}
		    	//	否则的话是在编辑的时候点的删除
		    	else
		    	{
		    		var div = $(windowId) ;
		    		$("haveDivAttachmentList").removeChild(div);
		    	}
		    	var filePathList = filePaths.split("|");
				var formDeleteFile = $("formDeleteFile");
				formDeleteFile.innerHTML = "";
				var hidType = document.createElement("input");
				hidType.name = "type";
				hidType.type = "hidden";
				hidType.value = "delete";
				formDeleteFile.appendChild(hidType);
				for (var i = 0; i < filePathList.length; i++)
				{
					var hidFilePath = document.createElement("input");
					hidFilePath.name = "filePath";
					hidFilePath.type = "hidden";
					hidFilePath.value = filePathList[i];
					formDeleteFile.appendChild(hidFilePath);
				}
				formDeleteFile.action = "../../servlet/BMPOperateFileSystemServlet";
				formDeleteFile.submit();
            };
		    ws.onerror = function (ex) { jetsennet.error(ex); };
		    ws.call("bmpObjDelete", ["BMP_KNOWLEDGEATTACHMENT", attachmentId]);
		}
	    return true;
    });
}

// 提交评论
function newComment(windowId)
{
	var commentContent = $("txtKnowledgeComment" + windowId).value;
	var areaElements = JetsenWeb.Form.getElements("divKnowledgeComment" + windowId);
    JetsenWeb.Form.clearValidateState(areaElements);
	if (!JetsenWeb.Form.Validate(areaElements, true)) {
		return;
	}
	var objComment = {
        KNOWLEDGE_ID: $("hidViewKnowledgeId" + windowId).value
      , COMMENT_CONTENT: "@" + commentContent + "@"
      , CREATE_USERID: JetsenWeb.Application.userInfo.UserId
      , CREATE_TIME: new Date().toDateTimeString()
    };
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
    	JetsenWeb.Form.resetValue(areaElements);
    	loadComment(windowId);
        searchKnowledge();
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjInsert", ["BMP_KNOWLEDGECOMMENT", JetsenWeb.Xml.serializer(objComment, "BMP_KNOWLEDGECOMMENT")]);
}

//加载附件列表
function loadAttachment(windowId)
{
	var divAttachmentList = $("divKnowledgeAttachment" + windowId);
	divAttachmentList.innerHTML = "";

	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGEATTACHMENT","");
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("KNOWLEDGE_ID", $("hidViewKnowledgeId" + windowId).value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery,{KeyId:"",QueryTable:gQueryTable,Conditions:condition,ResultFields:"ATTACHMENT_ID,ATTACHMENT_NAME,ATTACHMENT_PATH"});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(ret)
    {
    	var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
       	if (records) {
       		var length = records.length;
       		if (length > 0)
       		{
	       		var divTitle = document.createElement("div");
	            var bold = document.createElement("b");
	            bold.appendChild(document.createTextNode("附件列表："));
	            divTitle.appendChild(bold);
	            divAttachmentList.appendChild(divTitle);
       		}
            for (var i = 0; i < length; i++) {
            	divAttachmentList.appendChild(createAttachmentItem(records[i], windowId));
            }
       	}
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("bmpObjQuery",[gSqlQuery.toXml()]);
}

//创建附件项
function createAttachmentItem(objAttachment, windowId)
{
	var downloadLink = document.createElement("a");
	downloadLink.href = "../../servlet/BMPFileDownloadServlet?name=" + encodeURIComponent(encodeURIComponent(objAttachment["ATTACHMENT_NAME"])) + "&path=" + objAttachment["ATTACHMENT_PATH"] + "";
	downloadLink.target = "_blank";
	downloadLink.style.marginLeft = "10px";
	downloadLink.appendChild(document.createTextNode(objAttachment["ATTACHMENT_NAME"]));
	
	var onLineLink = document.createElement("a");
	onLineLink.href = "../../" + objAttachment["ATTACHMENT_PATH"] + "";
	onLineLink.target = "_blank";
	onLineLink.style.textDecoration = "underline" ;
	onLineLink.style.marginLeft = "10px";
	onLineLink.appendChild(document.createTextNode("查看"));
	

	var deleteLink = document.createElement("a");
	deleteLink.href = "javascript:deleteAttachment('" + objAttachment["ATTACHMENT_PATH"] + "', " + objAttachment["ATTACHMENT_ID"] + ", " + windowId + ");";
	deleteLink.style.marginLeft = "25px";
	deleteLink.style.textDecoration = "underline" ;
	deleteLink.style.color = "red" ;
	deleteLink.appendChild(document.createTextNode("删除"));
	
	var attachmentItem = document.createElement("div");
	attachmentItem.appendChild(downloadLink);
	
	// 控制删除权限，只有文章的创建用户和管理员可以删除
	if ($("hidCreateUserId" + windowId).value == JetsenWeb.Application.userInfo.UserId
			|| JetsenWeb.Application.userInfo.UserRoles.split(",").contains("1"))
	{
		attachmentItem.appendChild(deleteLink);
	}
	//	pdf支持在线查看
	var suffix = objAttachment["ATTACHMENT_NAME"].substring(objAttachment["ATTACHMENT_NAME"].lastIndexOf(".")+1, objAttachment["ATTACHMENT_NAME"].length) ;
	if(suffix.toLowerCase() == "pdf"){
		attachmentItem.appendChild(onLineLink);
	}
	
	return attachmentItem;
}

// 加载评论列表
function loadComment(windowId)
{
	var divCommentList = $("divKnowledgeCommentList" + windowId);
	divCommentList.innerHTML = "";

	var gSqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_KNOWLEDGECOMMENT","c");
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("UUM_USER","u","u.ID=c.CREATE_USERID",JetsenWeb.TableJoinType.Inner));
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("KNOWLEDGE_ID", $("hidViewKnowledgeId" + windowId).value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(gSqlQuery,{KeyId:"",QueryTable:gQueryTable,Conditions:condition,ResultFields:"COMMENT_ID,COMMENT_CONTENT,USER_NAME,CREATE_USERID,c.CREATE_TIME",OrderString:"ORDER BY CREATE_TIME DESC"});
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(ret)
    {
    	var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
       	if (records) {
       		var length = records.length;
            for (var i = 0; i < length; i++) {
                divCommentList.appendChild(createCommentItem(records[i], windowId));
            }
       	}
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("bmpObjQuery",[gSqlQuery.toXml()]);
}

// 创建评论项
function createCommentItem(objComment, windowId)
{
	var commentContent = valueOf(objComment, "COMMENT_CONTENT", "@@");

	var editLink = document.createElement("a");
	editLink.appendChild(document.createTextNode("编辑"));
	editLink.style.marginRight = "3px";
	editLink.style.cursor = "hand";
	editLink.href = "javascript:loadComment(" + windowId + ");editComment(" + valueOf(objComment, "COMMENT_ID", "0") + ", " + windowId + ")";
	
	var deleteLink = document.createElement("a");
	deleteLink.appendChild(document.createTextNode("删除"));
	deleteLink.style.marginRight = "5px";
	deleteLink.style.cursor = "hand";
	deleteLink.href = "javascript:deleteComment(" + valueOf(objComment, "COMMENT_ID", "0") + ", " + windowId + ")";

	var commentTopLeft = document.createElement("div");
	if(IS_IE){
		commentTopLeft.style.styleFloat = "left";
	}else{
		commentTopLeft.style.cssFloat = "left";
	}
	commentTopLeft.appendChild(document.createTextNode(valueOf(objComment, "USER_NAME", "") + " 于" + valueOf(objComment, "CREATE_TIME", "") + "发表评论："));
	
	var commentTopRight = document.createElement("div");
	if(IS_IE){
		commentTopRight.style.styleFloat = "right";
	}else{
		commentTopRight.style.cssFloat = "right";
	}
	
	
	// 控制删除权限，只有评论的创建用户和管理员可以编辑删除
	if (valueOf(objComment, "CREATE_USERID", "0") == JetsenWeb.Application.userInfo.UserId
		|| JetsenWeb.Application.userInfo.UserRoles.split(",").contains("1"))
	{
    	commentTopRight.appendChild(editLink);
    	commentTopRight.appendChild(deleteLink);
    }
	
	var commentTop = document.createElement("div");
	commentTop.className = "list-title";
	commentTop.style.height = "22px";
	commentTop.style.padding = "4px 0px 4px 10px";
	commentTop.style.borderBottom = "1px solid #545454";
	commentTop.appendChild(commentTopLeft);
	commentTop.appendChild(commentTopRight);
	
	var commentBody = document.createElement("div");
	commentBody.id = "commentBody" + windowId + "_" + valueOf(objComment, "COMMENT_ID", "0");
	commentBody.style.clear = "both";
	commentBody.style.padding = "4px 0px 4px 10px";
	commentBody.appendChild(document.createTextNode(commentContent.substring(1, commentContent.length - 1)));
	
	var commentItem = document.createElement("div");
	commentItem.style.border = "1px solid #545454";
	commentItem.style.marginBottom = "3px";
	commentItem.appendChild(commentTop);
	commentItem.appendChild(commentBody);
	
	return commentItem;
}

// 删除评论
function deleteComment(keyId, windowId)
{
	jetsennet.confirm("确定删除？", function () {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	    ws.soapheader = JetsenWeb.Application.authenticationHeader;
	    ws.oncallback = function (ret) {
	        searchKnowledge();
	        loadComment(windowId);
	    };
	    ws.onerror = function (ex) { jetsennet.error(ex); };
	    ws.call("bmpObjDelete", ["BMP_KNOWLEDGECOMMENT", keyId]);
	    return true;
    });
}

// 编辑评论
function editComment(keyId, windowId)
{
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("COMMENT_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "COMMENT_ID", PageInfo: null, QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_KNOWLEDGECOMMENT" }) });
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        var objComment = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
        var commentContent = valueOf(objComment, "COMMENT_CONTENT", "@@");
        var knowledgeId = valueOf(objComment, "KNOWLEDGE_ID", "0");
        var commentBody = $("commentBody" + windowId + "_" + keyId);
    	commentBody.innerHTML = "";
    	var txtEdit = document.createElement("textarea");
    	txtEdit.id = "txtEditKnowledgeComment" + windowId;
    	txtEdit.style.width = "98%";
    	txtEdit.style.height = "40px";
    	txtEdit.setAttribute("validatetype", "NotEmpty");
    	txtEdit.setAttribute("lengthrange", "500");
    	txtEdit.value = commentContent.substring(1, commentContent.length - 1);
    	commentBody.appendChild(txtEdit);
    	var divButton = document.createElement("div");
    	divButton.style.textAlign = "right";
    	divButton.style.paddingRight = "11px";
    	divButton.style.paddingTop = "3px";
    	var btnSubmit = document.createElement("input");
    	btnSubmit.type = "button";
    	btnSubmit.value = "提交";
    	btnSubmit.className = "button";
    	btnSubmit.style.marginRight = "3px";
    	if (btnSubmit.attachEvent)
    	{
        	btnSubmit.attachEvent("onclick", function(){
        		var areaElements = JetsenWeb.Form.getElements("divKnowledgeCommentList" + windowId);
	            JetsenWeb.Form.clearValidateState(areaElements);

				if (JetsenWeb.Form.Validate(areaElements, true))
        		{
        			var objComment = {
                    	COMMENT_ID: keyId
                      , KNOWLEDGE_ID: knowledgeId
                      , COMMENT_CONTENT: "@" + txtEdit.value + "@"
                      , CREATE_TIME: new Date().toDateTimeString()
                    };
            		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		            ws.soapheader = JetsenWeb.Application.authenticationHeader;
		            ws.oncallback = function (ret) {
		            	searchKnowledge();
		            	loadComment(windowId);
		            };
		            ws.onerror = function (ex) { jetsennet.error(ex); };
                	ws.call("bmpObjUpdate", ["BMP_KNOWLEDGECOMMENT", JetsenWeb.Xml.serializer(objComment, "BMP_KNOWLEDGECOMMENT")]);
            	}
        	});
    	}
		else
		{
			btnSubmit.onclick = function() {
				var areaElements = JetsenWeb.Form.getElements("divKnowledgeCommentList" + windowId);
	            JetsenWeb.Form.clearValidateState(areaElements);

				if (JetsenWeb.Form.Validate(areaElements, true))
        		{
        			var objComment = {
                    	COMMENT_ID: keyId
                   	  , KNOWLEDGE_ID: knowledgeId
                      , COMMENT_CONTENT: "@" + txtEdit.value + "@"
                      , CREATE_TIME: new Date().toDateTimeString()
                    };
            		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		            ws.soapheader = JetsenWeb.Application.authenticationHeader;
		            ws.oncallback = function (ret) {
		            	searchKnowledge();
		            	loadComment(windowId);
		            };
		            ws.onerror = function (ex) { jetsennet.error(ex); };
                	ws.call("bmpObjUpdate", ["BMP_KNOWLEDGECOMMENT", JetsenWeb.Xml.serializer(objComment, "BMP_KNOWLEDGECOMMENT")]);
            	}
			};
		} 
    	
    	var btnCancel = document.createElement("input");
    	btnCancel.type = "button";
    	btnCancel.value = "取消";
    	btnCancel.className = "button";
    	if (btnCancel.attachEvent)
    	{
        	btnCancel.attachEvent("onclick", function(){
        		loadComment(windowId);
        	});
    	}
		else
		{
			btnCancel.onclick = function() {
				loadComment(windowId);
			};
		} 
    	commentBody.appendChild(divButton);
    	divButton.appendChild(btnSubmit);
    	divButton.appendChild(btnCancel);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}


//************************************************
var attrClsArray;//存放所有设备类型

//加载所有设备类型
function loadAllClass()
{
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
      createClassOption();//生成设备类型下拉框
  };
  ws.onerror = function (ex) { jetsennet.error(ex); };
  ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//生成设备类型下拉框
function createClassOption()
{
	var cboClsss = $("cboClassId");
	var c_class = $("c_class_id");
	cboClsss.options.length = 0;
	c_class.options.length = 0;
	if(attrClsArray.length != 0)
	{
		cboClsss.options.add(new Option("请选择", ""));
		c_class.options.add(new Option("请选择", ""));
		for(var i=0; i<attrClsArray.length; i++)
		{
			var attrCls = attrClsArray[i];
			var t = new Option(attrCls["CLASS_NAME"], attrCls["CLASS_ID"]);
			t.title = attrCls["CLASS_NAME"];
			var t1 = new Option(attrCls["CLASS_NAME"], attrCls["CLASS_ID"]);
			t1.title = attrCls["CLASS_NAME"];
			cboClsss.options.add(t);
			c_class.options.add(t1);
		}
	}
	else
	{
		cboClsss.options.add(new Option("请选择", ""));
		c_class.options.add(new Option("请选择", ""));
	}
}

var alarmArray;
//加载所有报警类型
function loadAllAlarm()
{
	var queryTable = JetsenWeb.createQueryTable("BMP_ALARM", "a");
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.OrderString = "ORDER BY a.ALARM_ID";
	JetsenWeb.extend(sqlQuery, { KeyId: "ALARM_ID", QueryTable: queryTable, ResultFields: "a.*" });
	var queryConditions = new JetsenWeb.SqlConditionCollection();
	sqlQuery.Conditions = queryConditions;
	
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
  ws.soapheader = JetsenWeb.Application.authenticationHeader;
  ws.async = false;

  ws.oncallback = function (ret) {
  	alarmArray = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
      if(alarmArray == null) {
      	alarmArray = [];
      }
      createAlarmOption();//填充报警类型下拉框
  };
  ws.onerror = function (ex) { jetsennet.error(ex); };
  ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

//填充报警类型下拉框
function createAlarmOption()
{
	var cboAlarm = $("cboAlarmId");
	var c_alarm = $("c_alarm_id");
	cboAlarm.options.length = 0;
	c_alarm.options.length = 0;
	if(alarmArray.length != 0)
	{
		cboAlarm.options.add(new Option("请选择", ""));
		c_alarm.options.add(new Option("请选择", ""));
		for(var i=0; i< alarmArray.length; i++)
		{
			var alarmObj = alarmArray[i];
			var t =new Option(alarmObj["ALARM_NAME"], alarmObj["ALARM_ID"]);
			t.title = alarmObj["ALARM_NAME"];
			var t1 =new Option(alarmObj["ALARM_NAME"], alarmObj["ALARM_ID"]);
			t1.title = alarmObj["ALARM_NAME"];
			cboAlarm.options.add(t);
			c_alarm.options.add(t1);
		}
	}
	else
	{
		cboAlarm.options.add(new Option("请选择", ""));
		c_alarm.options.add(new Option("请选择", ""));
	}
}


//	用于标记附件个数，每一个附件都放在一个span中，span的id = spanAttachment+spanNum .
var spanNum = 0 ;

//	移除附件的span
function removeAttachmentSpan(obj){
	
	//	只有一条附件记录的话，将当前附件记录清空
	var spanList = $("newDivAttachmentList").children ;
	if(spanList.length == 1){
		spanList[0].children[0].value = "" ;
	    return ;
	}
	//	删除一行
	var span = obj.parentElement.parentElement ;
	$("newDivAttachmentList").removeChild(span);
	
	//	重新获取到所有的集合
	spanList = $("newDivAttachmentList").children ;
	//	取到最下面一行
	var lastSpan = spanList[spanList.length-1];
	if(lastSpan){
		//	让最后一行的添加按钮变亮
		lastSpan.children[2].children[1].style.visibility = "" ;
	}
	
}

//	增加附件的span
function addAttachmentSpan(){
	
	//	新增一行
	var newSpan = $("spanAttachment").cloneNode(true);
	newSpan.id = "spanAttachment" + spanNum ;
	newSpan.style.display = "" ;
	newSpan.children[0].id = "fileAttachment" + spanNum ;
	newSpan.children[2].children[1].id = "addAttachLink" + spanNum ;
	$("newDivAttachmentList").appendChild(newSpan);
	
	//	新增之后控制添加的显示
	for(var i=0; i<spanNum; i++){
		
		var addLink = $("addAttachLink"+i) ;
		if(addLink){
			addLink.style.visibility = "hidden";
		}
	}
	spanNum  ++ ;
}













