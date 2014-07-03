JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window", "js_validate", "js_pageframe", "js_xtree", "js_tabpane" ]);
var gTabPane;
var gFrame;
var gWindowSizeChangedInterVal;
// 主页面分页控件
var gManPage = new JetsenWeb.UI.PageBar("Manufacturers");
gManPage.onpagechange = function() {
	loadMan();
};
gManPage.orderBy = "ORDER BY MIB_ID";
gManPage.onupdate = function() {
	$('divManPage').innerHTML = this.generatePageControl();
};

var unloaded = new Array();
var loaded = new Array();

var tableName = "BMP_MIBBANKS";
var gManCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gManPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable(tableName, "");
JetsenWeb.extend(gSqlQuery, { IsPageResult : 1, PageInfo : gManPage, QueryTable : gQueryTable });
// 初始化===================================================================================
function init() {
	search();
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), { splitType : 1, fixControlIndex : 0, enableResize : false,
		splitTitle : "divListTitle", splitSize : 27 });
	var frameContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divContent"), { splitType : 1, fixControlIndex : 1, showSplit : false });
	frameContent.addControl(new JetsenWeb.UI.PageItem("divList"));
	frameContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divBottom"), { size : { width : 0, height : 30 } }));

	gFrame.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divTop"), { size : { width : 0, height : 30 } }));
	gFrame.addControl(frameContent);

	var snmpContent = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divSnmpContent"), { splitType : 1, fixControlIndex : 0, enableResize : false });
	snmpContent.addControl(JetsenWeb.extend(new JetsenWeb.UI.PageItem("divSnmpTree"), { size : { width : 798, height : 320 } }));
	snmpContent.addControl(new JetsenWeb.UI.PageItem("divSnmpNode"));
	snmpContent.size = { width : 798, height : 524 };
	snmpContent.resize();

	window.onresize = function() {
		if (gWindowSizeChangedInterVal != null)
			window.clearTimeout(gWindowSizeChangedInterVal);
		gWindowSizeChangedInterVal = window.setTimeout(windowResized, 500);
	};
	windowResized();
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
}
function initTab() {
	gTabPane = new JetsenWeb.UI.TabPane($('tabPanel'), $('tabPage'));
	gTabPane.ontabpagechanged = tabPageChanged;
}
// 调节窗口大小
function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = { width : size.width, height : size.height };
	gFrame.resize();
}
// 加载=====================================================================================
function loadMan() {
	gSqlQuery.OrderString = gManPage.orderBy;
	gSqlQuery.Conditions = gManCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divList').innerHTML = JetsenWeb.Xml.transformXML("xslt/mibbanks.xslt", ret.resultVal);
		gGridList.bind($('divList'), $('tabMibBank'));
		gManPage.setRowCount($('hidListCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function search() {
	gManCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gManCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_NAME", $('txt_Key').value, JetsenWeb.SqlLogicType.And,
				JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));
	}
	gManPage.currentPage = 1;
	loadMan();
}
// 删除=====================================================================================
// 新增=====================================================================================
function newMan() {
	var areaElements = JetsenWeb.Form.getElements('divMan');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
     jetsennet.validate(areaElements, true);
	showRemindWordCount($("txt_MIB_DESC").value,$('remindWorddesc'),"600");
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 350, height : 240 }, title : "新建MIB库" });
	dialog.controls = [ "divMan" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var objMan = getParams();
			if(parseInt(getBytesCount($("txt_MIB_DESC").value))>1200){
            	jetsennet.alert("描述不能超过600个文字！");
            	return;
            }
			var result = getMibByName(objMan.MIB_NAME,objMan.MIB_ALIAS);
			if(result.lengthMibName > 0){
				jetsennet.alert(objMan.MIB_NAME+" 已存在！Mib库的名称必须是唯一的！");
				return;
			}
			if(result.lengthMibAlias > 0){
				jetsennet.alert(objMan.MIB_ALIAS+" 已存在！Mib库的别名必须是唯一的！");
				return;
			}
				
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadMan();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ tableName, JetsenWeb.Xml.serializer(objMan, tableName) ]);
		}
	};
	dialog.showDialog();
	// $("txt_MAN_NAME").disabled = false;
	// $("txt_MAN_NAME").focus();
}
// 编辑=====================================================================================
function editMan(keyId) {
	var areaElements = JetsenWeb.Form.getElements('divMan');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.String));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "MIB_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : tableName }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var oma = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		putParams(oma);
		showRemindWordCount($("txt_MIB_DESC").value,$('remindWorddesc'),"600");
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 350, height : 240 }, title : "编辑MIB库" });
	dialog.controls = [ "divMan" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var oMan = getParams();
			oMan["MIB_ID"] = keyId;
			if(parseInt(getBytesCount($("txt_MIB_DESC").value))>1200){
            	jetsennet.alert("描述不能超过600个文字！");
            	return;
            }
			var result = getMibByName(oMan.MIB_NAME,oMan.MIB_ALIAS,keyId);
			if(result.lengthMibName > 0){
				jetsennet.alert(oMan.MIB_NAME+" 已存在！Mib库的名称必须是唯一的！");
				return;
			}
			if(result.lengthMibAlias > 0){
				jetsennet.alert(oMan.MIB_ALIAS+" 已存在！Mib库的别名必须是唯一的！");
				return;
			}
			
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadMan();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ tableName, JetsenWeb.Xml.serializer(oMan, tableName) ]);
		}
	};
	dialog.showDialog();
}
function getParams() {
	var attrs = { MIB_NAME : $("txt_MIB_NAME").value, MIB_ALIAS : $("txt_MIB_ALIAS").value, MIB_DESC : $("txt_MIB_DESC").value,
		CREATE_USER : JetsenWeb.Application.userInfo.UserName };
	return attrs;
}
function putParams(attrs) {
	$("txt_MIB_NAME").value = valueOf(attrs, "MIB_NAME", "");
	$("txt_MIB_ALIAS").value = valueOf(attrs, "MIB_ALIAS", "");
	$("txt_MIB_DESC").value = valueOf(attrs, "MIB_DESC", "");
}
function del(key) {
	if (key <= 1000) {
		jetsennet.alert("系统自带MIB库，无法删除！");
		return;
	}
	jetsennet.confirm("确定删除？", function () 
	{
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadMan();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_MIBBANKS", key ]);
        
        return true;
    });
}
// 文件加载===================================================================================================
function fileList(mibId) {
	unloaded.length = 0;
	loaded.length = 0;
	var areaElements = JetsenWeb.Form.getElements('divFileList');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		var str = ret.resultVal;
		files = str.split("?");
		unloaded = files[0].split(":");
		loaded = files[1].split(":");
		addToList(unloaded, $("listLeft"));
		addToList(loaded, $("listRight"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpGetMibFileInfo", [ mibId ]);

	var dialog = new JetsenWeb.UI.Window("new-filelist-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 700, height : 510 }, title : "选择MIB文件" });
	dialog.controls = [ "divFileList" ];
	dialog.onsubmit = function() {
		var str = reparseStr();
		jetsennet.confirm("加载MIB文件会删掉原先的MIB库数据，是否继续？", function () 
		{
			var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-filelist-win");
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpParseMib", [ mibId, str ]);
	        
	        return true;
	    });
	};
	dialog.showDialog();
}
function addToList(files, sel) {
	sel.options.length = 0;
	if (files == null || files.length == 0) {
		return;
	}
	for ( var i = 0; i < files.length; i++) {
		if (files[i] != "") {
			sel.options.add(new Option(files[i], files[i]));
		}
	}
}
// type,0,右移；1，左移
function move(type) {
	var move = null;
	var accept = null;
	if (type == 0) {
		move = $("listLeft");
		accept = $("listRight");
	} else {
		move = $("listRight");
		accept = $("listLeft");
	}
	var length = move.options.length;
	var isSelected = false;
	for ( var i = 0; i < length;) {
		if (move.options[i] != null && move.options[i].selected) {
			var value = move.options[i].value;
			move.remove(i);
			accept.options.add(new Option(value, value));
			isSelected = true;
		} else {
			i++;
		}
	}
	if (!isSelected) {
		jetsennet.alert("请选择需要移动的文件！");
	}
}
function reparseStr() {
	var sel = $("listRight");
	var retval = "";
	var length = sel.options.length;
	for ( var i = 0; i < length; i++) {
		var value = sel.options[i].value;
		if (i == 0) {
			retval += value;
		} else {
			retval = retval + ":" + value;
		}
	}
	return retval;
}
// MIB库详情===============================================================================
// MIB文件=================================================================================
function mibFileList() {
	var dialog = new JetsenWeb.UI.Window("mib-list-win");
	JetsenWeb.extend(dialog, { 
		submitBox : false, 
		cancelBox : true, 
		windowStyle : 1, 
		cancelButtonText : "关闭", 
		maximizeBox : true,
		minimizeBox : true, 
		size : { width : 800, height : 500 }, 
		title : "MIB文件" });
	dialog.controls = [ "divMibFile" ];
	loadMibFileList();
	dialog.showDialog();
}
function loadMibFileList() {
	var gridList = new JetsenWeb.UI.GridList();

	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divMibFileList').innerHTML = JetsenWeb.Xml.transformXML("xslt/mibbanks_filelist.xslt", ret.resultVal);
		gridList.bind($('divMibFileList'), $('tabMibFileList'));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpGetMibFileList", []);
}
function delMibFiles() {
	var mibFiles = JetsenWeb.Form.getCheckedValues("chkAllObject");
	if (mibFiles.length <= 0) {
		jetsennet.alert("请选择要删除的文件！")
		return;
	}
	jetsennet.confirm("确定删除？", function () 
	{
		var gridList = new JetsenWeb.UI.GridList();

		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			loadMibFileList();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpDelMibFile", [ mibFiles.join(";") ]);
        
        return true;
    });
}
function delMibFile(fileName) {
	jetsennet.confirm("确定删除？", function () 
	{
		var gridList = new JetsenWeb.UI.GridList();

		var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.async = false;
		ws.oncallback = function(ret) {
			loadMibFileList();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpDeleteMibFile", [ fileName ]);
        
        return true;
    });
}
function mibFileUpload() {
	var dialog = new JetsenWeb.UI.Window("mib-upload-win");
	JetsenWeb.extend(dialog, { 
		submitBox : false, 
		cancelBox : false, 
		windowStyle : 1, 
		cancelButtonText : "关闭", 
		maximizeBox : true,
		minimizeBox : true, size : { width : 800, height : 500 }, 
		title : "上传MIB文件" });
	dialog.controls = [ "divMibFileUpload" ];
	dialog.onclosed = function() {
		loadMibFileList();
	}
	loadMibFileList();
	dialog.showDialog();
}

//导出成xml
function exportXml(mibId,name) {
		var url = "../../servlet/BmpExportMibXmlServlet?flag=1";
		var mibName = name+".xml";
		url = url+"&mibId="+mibId+"&mibName="+encodeURI(mibName);
		window.open(url, "");

}
//导入xml文件
function importMibXml()
{  
	var areaElements = JetsenWeb.Form.getElements("divImportMibXml");
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    // 重新初始化文件框
    $("formImportMibXml").reset();  
	var dialog = new JetsenWeb.UI.Window("import-object-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 120 }, title: "导入Mib库" });
    dialog.controls = ["divImportMibXml"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	var filePath = $("fileAttachment").value;
        	var fileName = "";
            if (filePath.length == 0)
            {
            	jetsennet.alert("请选择你要导入的mib库文件！");
				return;
            }
            fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            if(!/\.(xml)$/.test(fileName))
            {
            	jetsennet.alert("导入文件格式错误！请重新导入！");
            	return;
            }
        	var form = $("formImportMibXml");
        	form.action = "../../servlet/BMPImportMibServlet?fag=1";
        	form.submit();
        	JetsenWeb.UI.Windows.close("import-object-win");

        	var dialog = new JetsenWeb.UI.Window("result-mibxml-win");
            JetsenWeb.extend(dialog, { windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 350, height: 100 }, title: "导入Mib库" });
            dialog.controls = ["divImportMib"];
            var uploadIntervalId = setInterval(function()
            {
            	var uploadResult = $("frameImportMibXml").contentWindow.document.body.innerHTML;
            	if (uploadResult != "")
				{
            		if (uploadResult.indexOf("成功") != -1)
            		{
            			uploadResult = "导入成功！";
            			$("frameImportMibXml").contentWindow.document.body.innerHTML = "";
            			search();          			     			
            		}
            		else if (uploadResult.indexOf("导入失败") != -1)
            		{
            			search();
            			uploadResult = "导入失败！";
            			$("frameImportMibXml").contentWindow.document.body.innerHTML = "";
            		}
            		else{
            			search();
            			if(uploadResult.indexOf("@") > -1){
            				
            				uploadResult =uploadResult.substring(1,uploadResult.length)+" 已存在！不能导入相同名称的Mib库！";
            			}else{
            				uploadResult =uploadResult+" 已存在！不能导入相同别名的Mib库！";
            			}
            			$("frameImportMibXml").contentWindow.document.body.innerHTML = "";
            		}
            		JetsenWeb.UI.Windows.close("result-mibxml-win");
            		clearInterval(uploadIntervalId);
            		jetsennet.alert(uploadResult);        		
            	}
            }, 400);
            dialog.showDialog();
        }
    };
    dialog.showDialog();
}

function getMibByName(mibName,mibAlias,id){
	var result = {lengthMibName:0,lengthMibAlias:0};
	var condition = new JetsenWeb.SqlConditionCollection();
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_NAME", mibName, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.String));
	if(id){
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
				JetsenWeb.SqlParamType.Number));
	}
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "MIB_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : tableName }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.async = false;
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var resultxml = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if(resultxml){
			result.lengthMibName = resultxml.length;
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	
	if(result.lengthMibName == 0){
		var condition2 = new JetsenWeb.SqlConditionCollection();
		condition2.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ALIAS", mibAlias, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
				JetsenWeb.SqlParamType.String));
		if(id){
			condition2.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual,
					JetsenWeb.SqlParamType.Number));
		}
		var sqlQuery2 = new JetsenWeb.SqlQuery();
		JetsenWeb.extend(sqlQuery2, { IsPageResult : 0, KeyId : "MIB_ID", PageInfo : null,
			QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : tableName }) });
		sqlQuery2.Conditions = condition2;
	
		var ws2 = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws2.async = false;
		ws2.soapheader = JetsenWeb.Application.authenticationHeader;
		ws2.oncallback = function(ret2) {
			var resultxml = JetsenWeb.Xml.toObject(ret2.resultVal, "Record");
			if(resultxml){
				result.lengthMibAlias = resultxml.length;
			}
		};
		ws2.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws2.call("bmpObjQuery", [ sqlQuery2.toXml() ]);
	}
    return result;
}

