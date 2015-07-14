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
        $("divProjectList").innerHTML = JetsenWeb.Xml.transformXML("xslt/simpleproject.xslt", ret.resultVal);
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
}

function windowResized() {
    var size = JetsenWeb.Util.getWindowViewSize();
    gFrame.size = { width: size.width, height: size.height };
    gFrame.resize();
}

//显示附件列表
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
	
	var tdDownload = attachmentItem.insertCell();
	tdDownload.appendChild(downloadLink);
}