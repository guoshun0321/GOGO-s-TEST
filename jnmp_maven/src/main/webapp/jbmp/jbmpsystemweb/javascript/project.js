JetsenWeb.require(["js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree"]);
var gFrame;
var gWindowSizeChangedInterVal;
var gProjectPage = new JetsenWeb.UI.PageBar("Project");
gProjectPage.onpagechange = function () { searchProject(); };
gProjectPage.orderBy = "";
gProjectPage.onupdate = function () {
    $("divProjectPage").innerHTML = this.generatePageControl();
};
var gProjectCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) {
    gProjectPage.setOrderBy(sortfield, desc);
};

var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_PROJECT", "");
JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "PROJECT_ID", ResultFields:"*", PageInfo: gProjectPage, QueryTable: gQueryTable });

var uploadIntervalId;// 检测上传是否成功的线程

//加载=====================================================================================
function loadProject() {
    gSqlQuery.OrderString = gProjectPage.orderBy;
    gSqlQuery.Conditions = gProjectCondition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        $("divProjectList").innerHTML = JetsenWeb.Xml.transformXML("xslt/project.xslt", ret.resultVal, [ { name:"CurrentUserId", value:JetsenWeb.Application.userInfo.UserId }
        , { name:"IsAdmin", value:JetsenWeb.Application.userInfo.UserRoles.split(",").contains("1") ? 1 : 0 } ]);
        gGridList.bind($("divProjectList"), $("tabProject"));
        gProjectPage.setRowCount($("hid_ProjectCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [gSqlQuery.toXml()]);
}
function searchProject() {
    gProjectCondition.SqlConditions = [];
    gSqlQuery = new JetsenWeb.SqlQuery();
    gQueryTable = JetsenWeb.createQueryTable("BMP_PROJECT", "");
    
    if ($("txtProjectNum").value != "") {
        gProjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROJECT_NUM", $("txtProjectNum").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if ($("txtProjectName").value != "") {
        gProjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROJECT_NAME", $("txtProjectName").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
    }
    if ($("txtReportTime").value != "" && $("chkDate").checked) {
        gProjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("REPORT_TIME", $("txtReportTime").value + " 00:00:00", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ThanEqual, JetsenWeb.SqlParamType.DateTime));
        gProjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("REPORT_TIME", $("txtReportTime").value + " 23:59:59", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.LessEqual, JetsenWeb.SqlParamType.DateTime));
    }
    if (getSelectedValue($("cboProjectType")) != "") {
        gProjectCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROJECT_TYPE", getSelectedValue($("cboProjectType")), JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    }
    JetsenWeb.extend(gSqlQuery, { IsPageResult: 1, KeyId: "PROJECT_ID", ResultFields:"*", PageInfo: gProjectPage, QueryTable: gQueryTable });
    loadProject();
}
//删除=====================================================================================
function deleteProject(keyId) {
jetsennet.confirm("确定删除？", function () {
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        searchProject();
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjDelete", ["BMP_PROJECT", keyId]);
    return true;
    });
}
//新增=====================================================================================
function newProject() {
    var areaElements = JetsenWeb.Form.getElements("divProject");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 800, height: 250 }, title: "新建项目" });
    dialog.controls = ["divProject"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var txtBudgetMoney = $("txt_BUDGET_MONEY");
        	if (jetsennet.util.isNullOrEmpty(txtBudgetMoney.value) || isNaN(txtBudgetMoney.value) || parseFloat(txtBudgetMoney.value) < 1 || parseFloat(txtBudgetMoney.value) > 99999999) {
            	txtBudgetMoney.setAttribute("isvalidateerror","1");
            	txtBudgetMoney.title = "必须在1到99999999之间";
            	txtBudgetMoney.style.borderColor = jetsennet.form.validateErrorBorderColor;
            	txtBudgetMoney.style.backgroundColor = jetsennet.form.validateErrorBgColor;
            	return;
            }
        	
        	var objProject = {
        	  PROJECT_NUM: $("txt_PROJECT_NUM").value
              , PROJECT_NAME: $("txt_PROJECT_NAME").value
              , PROJECT_TYPE: getSelectedValue($("cbo_PROJECT_TYPE"))
              , REPORT_TIME: $("txt_REPORT_TIME").value
              , PROJECT_CONTENT: $("txt_PROJECT_CONTENT").value
              , BUDGET_MONEY: $("txt_BUDGET_MONEY").value
              , PROJECT_ATTACHMENT: ""
              , PROJECT_ATTACHMENT_PATH: ""
            };
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("new-object-win");
                searchProject();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjInsert", ["BMP_PROJECT", JetsenWeb.Xml.serializer(objProject, "BMP_PROJECT")]);
        }
    };
    $("txt_REPORT_TIME").value = new Date().toDateString();
    dialog.showDialog();
}
//编辑=====================================================================================
function editProject(keyId) {
    var areaElements = JetsenWeb.Form.getElements("divProject");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);

    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROJECT_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "PROJECT_ID", PageInfo: null, QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_PROJECT" }) });
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        var objProject = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
        $("txt_PROJECT_NUM").value = valueOf(objProject, "PROJECT_NUM", "");
        $("txt_PROJECT_NAME").value = valueOf(objProject, "PROJECT_NAME", "");
        setSelectedValue($("cbo_PROJECT_TYPE"), valueOf(objProject, "PROJECT_TYPE", ""));
        $("txt_REPORT_TIME").value = valueOf(objProject, "REPORT_TIME", "          ").substring(0, 10);
        var projectContent = valueOf(objProject, "PROJECT_CONTENT", "@@");
        $("txt_PROJECT_CONTENT").value = valueOf(objProject, "PROJECT_CONTENT", "");
        $("txt_BUDGET_MONEY").value = valueOf(objProject, "BUDGET_MONEY", "");
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 800, height: 250 }, title: "编辑项目" });
    dialog.controls = ["divProject"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var txtBudgetMoney = $("txt_BUDGET_MONEY");
        	if (jetsennet.util.isNullOrEmpty(txtBudgetMoney.value) || isNaN(txtBudgetMoney.value) || parseFloat(txtBudgetMoney.value) < 1 || parseFloat(txtBudgetMoney.value) > 99999999) {
            	txtBudgetMoney.setAttribute("isvalidateerror","1");
            	txtBudgetMoney.title = "必须在1到99999999之间";
            	txtBudgetMoney.style.borderColor = jetsennet.form.validateErrorBorderColor;
            	txtBudgetMoney.style.backgroundColor = jetsennet.form.validateErrorBgColor;
            	return;
            }
        	
            var oProject = {
            	PROJECT_ID: keyId
              , PROJECT_NUM: $("txt_PROJECT_NUM").value
              , PROJECT_NAME: $("txt_PROJECT_NAME").value
              , PROJECT_TYPE: getSelectedValue($("cbo_PROJECT_TYPE"))
              , REPORT_TIME: $("txt_REPORT_TIME").value
              , PROJECT_CONTENT: $("txt_PROJECT_CONTENT").value
              , BUDGET_MONEY: $("txt_BUDGET_MONEY").value
            };
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("edit-object-win");
                searchProject();
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjUpdate", ["BMP_PROJECT", JetsenWeb.Xml.serializer(oProject, "BMP_PROJECT")]);
        }
    };
    dialog.showDialog();
}
//初始化===================================================================================
function pageInit() {
    searchProject();

    gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"),{splitType: 1,fixControlIndex: 0, splitBorder: 0, showSplit: true, enableResize: false, splitTitle : "divListTitle", splitSize : 27});

    var frameTop = new JetsenWeb.UI.PageItem("divTop");
    frameTop.size = { width: 0, height: 30 };
    var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divMain"), { splitType: 1, fixControlIndex: 1, showSplit: false });
    frameContent.addControl(new JetsenWeb.UI.PageItem("divProjectList"));
    frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size: { width: 0, height: 30} }));

    gFrame.addControl(frameTop);
    gFrame.addControl(frameContent);

    window.onresize = function () {
        if (gWindowSizeChangedInterVal != null)
            window.clearTimeout(gWindowSizeChangedInterVal);
        gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
    };
    windowResized();
    parent.document.getElementById("spanWindowName").innerHTML = document.title;
}

function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

// 上传文件
function uploadFile(projectId)
{
	
	var urlParts = window.location.href.split("/");
	var webRoot = urlParts[0] + "/" + urlParts[1] + "/" + urlParts[2] + "/" + urlParts[3] + "/";
	var areaElements = JetsenWeb.Form.getElements("divAttachment");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
//    $("hidProjectId").value = projectId;
    $("hidProjectIdUse").value = projectId;

    // 重新初始化上传文件框
    var fileInput = $("fileAttachment");
    fileInput.form.reset();

	var dialog = new JetsenWeb.UI.Window("upload-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 150 }, title: "上传附件" });
    dialog.controls = ["divAttachment"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var filePath = $("fileAttachment").value;
        	var fileName = "";
            if (filePath.length == 0)
            {
            	jetsennet.alert("请选择要上传的文件！");
				return;
            }
            fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            var fileRandomName = new Date().getTime();
        	var form = $("formUpload");
        	form.action = "../../servlet/BMPFilesUploadServlet?fileName=" + fileRandomName + "&uploadPath=attachment/project/" + projectId;
        	form.submit();
        	JetsenWeb.UI.Windows.close("upload-object-win");

			var frameUploadDocument = $("frameUpload").contentWindow.document;
			frameUploadDocument.body.innerHTML = "";
        	var dialog = new JetsenWeb.UI.Window("result-object-win");
            JetsenWeb.extend(dialog, { windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 100 }, title: "上传附件" });
            dialog.controls = ["divUploadTip"];
            uploadIntervalId = setInterval(function()
            {
            	var uploadResult = $("frameUpload").contentWindow.document.body.innerHTML;
            	if (uploadResult != "")
				{
            		if (uploadResult.indexOf("成功") != -1)
            		{
            			newAttachment(fileName, fileRandomName);
            			uploadResult = "上传成功！";
            		}
            		JetsenWeb.UI.Windows.close("result-object-win");
            		clearInterval(uploadIntervalId);
            		jetsennet.alert(uploadResult);
            	}
            }, 1000);
            dialog.showDialog();
        }
    };
    dialog.showDialog();
}

// 新增附件信息
function newAttachment(fileName, fileRandomName)
{
	var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("PROJECT_ID", $("hidProjectIdUse").value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "PROJECT_ID", PageInfo: null, QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_PROJECT" }) });
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function (ret) {
        var objProject = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
        var attachment = valueOf(objProject, "PROJECT_ATTACHMENT", "");
        var attachmentPath = valueOf(objProject, "PROJECT_ATTACHMENT_PATH", "");
        var newAttachmentPath = "attachment/project/" + $("hidProjectIdUse").value + "/" + fileRandomName;
        
        var objAttachment = {
            PROJECT_ID: $("hidProjectIdUse").value
          , PROJECT_ATTACHMENT: attachment == "" ? fileName : attachment + "|" + fileName
          , PROJECT_ATTACHMENT_PATH: attachmentPath == "" ? newAttachmentPath : attachmentPath + "|" +  newAttachmentPath
        };
        var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {
        	searchProject();
        };
        ws.onerror = function (ex) { jetsennet.error(ex); };
        ws.call("bmpObjUpdate", ["BMP_PROJECT", JetsenWeb.Xml.serializer(objAttachment, "BMP_PROJECT")]);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("bmpObjQuery", [sqlQuery.toXml()]);
}

// 显示附件列表
function showAttachmentList(fileNames, filePaths, projectId)
{
	var fileNameList = fileNames.split("|");
	var filePathList = filePaths.split("|");
	var divAttachmentList = $("divAttachmentList");
	divAttachmentList.innerHTML = "";
	var attachmentTable = document.createElement("table");
	attachmentTable.id = "tabAttachment";
	attachmentTable.style.width = "100%";
	attachmentTable.border = 0;
	for (var i = 0; i < fileNameList.length; i++)
	{
		var attachmentItem = attachmentTable.insertRow();
		createAttachmentItem(fileNameList[i], filePathList[i], attachmentItem, projectId);
	}
	divAttachmentList.appendChild(attachmentTable);
	
	var hidProjectId = document.createElement("input");
	hidProjectId.id = "hidProjectId";
	hidProjectId.type = "hidden";
	hidProjectId.value = projectId;
	divAttachmentList.appendChild(hidProjectId);
	
	var hidFileNames = document.createElement("input");
	hidFileNames.id = "hidFileNames";
	hidFileNames.type = "hidden";
	hidFileNames.value = fileNames;
	divAttachmentList.appendChild(hidFileNames);
	
	var hidFilePaths = document.createElement("input");
	hidFilePaths.id = "hidFilePaths";
	hidFilePaths.type = "hidden";
	hidFilePaths.value = filePaths;
	divAttachmentList.appendChild(hidFilePaths);
	
	var dialog = new JetsenWeb.UI.Window("view-object-win");
    JetsenWeb.extend(dialog, { submitBox: false, cancelBox: true, cancelButtonText: "返回", windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 400, height: 65 + 30 * fileNameList.length }, title: "附件列表" });
    dialog.controls = ["divAttachmentList"];
    dialog.showDialog();
}

// 生成附件项
function createAttachmentItem(fileName, filePath, attachmentItem, projectId)
{
	var downloadLink = document.createElement("a");
	downloadLink.href = "../../servlet/BMPFileDownloadServlet?name=" + encodeURIComponent(encodeURIComponent(fileName)) + "&path=" + filePath;
	downloadLink.target = "_blank";
	downloadLink.appendChild(document.createTextNode(fileName));

	var deleteLink = document.createElement("a");
	deleteLink.href = "javascript:deleteAttachment('" + fileName + "', '" + filePath + "', " + projectId + ");";
	deleteLink.appendChild(document.createTextNode("删除"));
	
	var tdDownload = attachmentItem.insertCell();
	tdDownload.appendChild(downloadLink);
	
	var tdDelete = attachmentItem.insertCell();
	tdDelete.appendChild(JetsenWeb.Application.userInfo.UserRoles.split(",").contains("1") ? deleteLink : document.createTextNode(" "));
}

//删除附件
function deleteAttachment(fileName, filePath, projectId)
{
	jetsennet.confirm("确定删除？", function () {
		if (projectId)
		{
			var fileNames = $("hidFileNames").value;
			var filePaths = $("hidFilePaths").value;
			
			var fileNameList = fileNames.split("|");
			var filePathList = filePaths.split("|");
			for (var i = 0; i < fileNameList.length; i++)
			{
				if (fileNameList[i] == fileName)
				{
					fileNameList.splice(i, 1);
					filePathList.splice(i, 1);
				}
			}
			
			fileNames = fileNameList.join("|");
			filePaths = filePathList.join("|");
			
			var oProject = {
            	PROJECT_ID: projectId
              , PROJECT_ATTACHMENT: fileNames
              , PROJECT_ATTACHMENT_PATH: filePaths
            };

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
                JetsenWeb.UI.Windows.close("view-object-win");
                deleteFile(filePath);
				searchProject();
				showAttachmentList(fileNames, filePaths, projectId)
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjUpdate", ["BMP_PROJECT", JetsenWeb.Xml.serializer(oProject, "BMP_PROJECT")]);
		}
	    return true;
    });
}

// 从文件系统中删除文件
function deleteFile(filePath)
{
	var formDeleteFile = $("formDeleteFile");
	formDeleteFile.innerHTML = "";
	var hidType = document.createElement("input");
	hidType.name = "type";
	hidType.type = "hidden";
	hidType.value = "delete";
	formDeleteFile.appendChild(hidType);
	var hidFilePath = document.createElement("input");
	hidFilePath.name = "filePath";
	hidFilePath.type = "hidden";
	hidFilePath.value = filePath;
	formDeleteFile.appendChild(hidFilePath);
	formDeleteFile.action = "../../servlet/BMPOperateFileSystemServlet";
	formDeleteFile.submit();
}