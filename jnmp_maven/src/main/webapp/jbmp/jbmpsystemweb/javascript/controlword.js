JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gControlWordPage = new JetsenWeb.UI.PageBar("ControlWord");
gControlWordPage.onpagechange = function() {
	loadControlWord();
};
gControlWordPage.orderBy = "ORDER BY CW_ID DESC";
gControlWordPage.onupdate = function() {
	$('divControlWordPage').innerHTML = this.generatePageControl();
};
var gControlWordCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gControlWordPage.setOrderBy(sortfield, desc);
};
var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_CTRLWORD", "");
JetsenWeb.extend(gSqlQuery, {
	IsPageResult : 1,
	KeyId : "CW_ID",
	PageInfo : gControlWordPage,
	QueryTable : gQueryTable,
	ResultFields : "*"
});

// 加载=====================================================================================
function loadControlWord() {
	gSqlQuery.OrderString = gControlWordPage.orderBy;
	gSqlQuery.Conditions = gControlWordCondition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$('divControlWordList').innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/controlword.xslt", ret.resultVal);
		gGridList.bind($('divControlWordList'), $('tabControlWord'));
		gControlWordPage.setRowCount($('hid_ControlWordCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ gSqlQuery.toXml() ]);
}
function searchControlWord() {
	gControlWordCondition.SqlConditions = [];
	if ($('txt_Key').value != "") {
		gControlWordCondition.SqlConditions
				.push(JetsenWeb.SqlCondition.create("CW_NAME",
						$('txt_Key').value, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.ILike,
						JetsenWeb.SqlParamType.String));
	}
	if ($('ddlCWType').value != "") {
		gControlWordCondition.SqlConditions.push(JetsenWeb.SqlCondition
				.create("CW_TYPE", $('ddlCWType').value,
						JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.Equal,
						JetsenWeb.SqlParamType.Numeric));
	}
	gControlWordPage.currentPage = 1;
	loadControlWord();
}
// 删除=====================================================================================
function deleteControlWord(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadControlWord();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_CTRLWORD", keyId ]);
		return true;
	});
}
// 新建=====================================================================================
function newControlWord() {
	var areaElements = JetsenWeb.Form.getElements('divControlWord');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	showRemindWord();
	
	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 500,
			height : 240
		},
		title : "新建受控参数"
	});
	dialog.controls = [ "divControlWord" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (controlWordIsExists("",getSelectedValue($("cbo_CW_TYPE")), $("txt_CW_NAME").value))
			{
			    var txtcwName = $("txt_CW_NAME");
			    txtcwName.setAttribute("isvalidateerror","1");
			    txtcwName.title = "该类型中有重复受控词名称";
			    txtcwName.style.borderColor = jetsennet.form.validateErrorBorderColor;
			    txtcwName.style.backgroundColor = jetsennet.form.validateErrorBgColor;
		        return;
			}
			if (parseInt(getBytesCount($("txt_CW_DESC").value)) > 132)
			{
				jetsennet.alert("参数或描述不能超过66个文字！");
				return;
			}
			var objControlWord = {
				CW_TYPE : getSelectedValue($("cbo_CW_TYPE")),
				CW_NAME : $("txt_CW_NAME").value,
				CW_DESC : $("txt_CW_DESC").value
			};
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				loadControlWord();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_CTRLWORD",
					JetsenWeb.Xml.serializer(objControlWord, "BMP_CTRLWORD") ]);
		}
	};
	dialog.showDialog();
	$("txt_CW_NAME").focus();
}

//双击编辑
function dbclickControlWord(operType,keyId, type, name, desc)
{
	if(operType != 12 && operType != 13)
	{
		editControlWord(keyId, type, name, desc);
	}
}

// 编辑=====================================================================================
function editControlWord(keyId, type, name, desc) {
	var areaElements = JetsenWeb.Form.getElements('divControlWord');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	setSelectedValue($("cbo_CW_TYPE"), type);
	$("txt_CW_NAME").value = name;
	$("txt_CW_DESC").value = desc;
	showRemindWord();

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 500,
			height : 240
		},
		title : "编辑受控参数"
	});
	dialog.controls = [ "divControlWord" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (controlWordIsExists(keyId , getSelectedValue($("cbo_CW_TYPE")), $("txt_CW_NAME").value))
			{
				 var txtcwName = $("txt_CW_NAME");
				 txtcwName.setAttribute("isvalidateerror","1");
				 txtcwName.title = "该类型中有重复受控词名称";
				 txtcwName.style.borderColor = jetsennet.form.validateErrorBorderColor;
				 txtcwName.style.backgroundColor = jetsennet.form.validateErrorBgColor;
			     return;
		    }
			if (parseInt(getBytesCount($("txt_CW_DESC").value)) > 132)
			{
				jetsennet.alert("参数或描述不能超过66个文字！");
				return;
			}
			var oControlWord = {
				CW_ID : keyId,
				CW_TYPE : getSelectedValue($("cbo_CW_TYPE")),
				CW_NAME : $("txt_CW_NAME").value,
				CW_DESC : $("txt_CW_DESC").value
			};
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadControlWord();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_CTRLWORD",
					JetsenWeb.Xml.serializer(oControlWord, "BMP_CTRLWORD") ]);
		}
	};
	dialog.showDialog();
}
// 初始化===================================================================================
function pageInit() {
	searchControlWord();
	parent.document.getElementById("spanWindowName").innerHTML = document.title;
	gFrame = JetsenWeb.extend(new JetsenWeb.UI.PageFrame("divPageFrame"), {
		splitType : 1,
		fixControlIndex : 0,
		enableResize : false,
		splitTitle : "divListTitle",
		splitSize : 27
	});

	var frameContent = JetsenWeb.extend(
			new JetsenWeb.UI.PageFrame("divContent"), {
				splitType : 1,
				fixControlIndex : 1,
				showSplit : false
			});

	frameContent.addControl(new JetsenWeb.UI.PageItem("divControlWordList"));
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
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}


//判断受控词是否已经存在
function controlWordIsExists(keyId,cwType, cwName)
{
	var existsCount = 0;
	
	var queryTable = JetsenWeb.createQueryTable("BMP_CTRLWORD","");
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0, KeyId:"CW_ID", PageInfo:null, QueryTable:queryTable, ResultFields:"COUNT(*) AS COUNT"});
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CW_TYPE",cwType,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CW_NAME",cwName,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.String));
    if (keyId)
    {
    	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("CW_ID",keyId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric));
    }
    sqlQuery.Conditions = condition;        
    
    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.getAuthenticationHeader();
    ws.async = false;
    ws.oncallback = function(ret)
    {
    	existsCount = parseInt(JetsenWeb.Xml.toObject(ret.resultVal,"Record")[0]["COUNT"]);
    };
    ws.onerror = function(ex){jetsennet.error(ex);};
    ws.call("bmpObjQuery",[sqlQuery.toXml()]);
    return existsCount > 0;
}

// 显示剩余文字数
function showRemindWord()
{
	$("remindWord").innerHTML = parseInt((132 - parseInt(getBytesCount($("txt_CW_DESC").value))) / 2);
	if (132 < parseInt(getBytesCount($("txt_CW_DESC").value)))
	{
		$("remindWord").style.color = "red";
		$("remindWord").innerHTML = 0;
	}
	else
	{
		$("remindWord").style.color = "white";
	}
}