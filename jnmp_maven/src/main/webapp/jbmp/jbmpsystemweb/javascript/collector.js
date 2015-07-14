JetsenWeb.require( [ "js_gridlist", "js_pagebar", "js_sql", "js_window",
		"js_validate", "js_pageframe", "js_timeeditor", "js_datepicker" ]);
var gFrame;
var gWindowSizeChangedInterVal;
var gCollIp;
var gCollectorPage = new JetsenWeb.UI.PageBar("Collector");
gCollectorPage.onpagechange = function() {
	loadCollector();
};
gCollectorPage.orderBy = "order by COLL_ID";
gCollectorPage.onupdate = function() {
	$('divCollectorPage').innerHTML = this.generatePageControl();
};

var gCollectorCondition = new JetsenWeb.SqlConditionCollection();

var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function(sortfield, desc) {
	gCollectorPage.setOrderBy(sortfield, desc);
};

var gSqlQuery = new JetsenWeb.SqlQuery();
var gQueryTable = JetsenWeb.createQueryTable("BMP_COLLECTOR", "a");
var userId = jetsennet.Application.userInfo.UserId;
var fagAdmin = isadmin(userId);
//if(userId !== "1"){
if(fagAdmin == "false"){
	gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_OBJGROUP", "b",	"a.coll_id = b.num_val1", JetsenWeb.TableJoinType.LEFT));
}
gSqlQuery.GroupFields = " a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2 ";

JetsenWeb.extend(gSqlQuery, {
	IsPageResult : 1,
	KeyId : "COLL_ID",
	PageInfo : gCollectorPage,
	QueryTable : gQueryTable,
	ResultFields : " a.COLL_ID,a.COLL_NAME,a.COLL_TYPE,a.IP_ADDR,a.CREATE_TIME,a.FIELD_1,a.FIELD_2 "
});

// 加载=====================================================================================
function loadCollector() {
	if(fagAdmin == "false"){
//	if(userId !== "1"){
		var groupIds = getCollId();
		gCollectorCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.GROUP_ID", groupIds, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In, JetsenWeb.SqlParamType.String));
	}
	gSqlQuery.Conditions = gCollectorCondition;
	gSqlQuery.OrderString = gCollectorPage.orderBy;
	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
//	var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divCollectorList").innerHTML = JetsenWeb.Xml.transformXML(
				"xslt/collector.xslt", ret.resultVal);
		gGridList.bind($("divCollectorList"), $("tabCollector"));
		gCollectorPage.setRowCount($("hid_CollectorCount").value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
//	ws.call("bmpCollectorList", [ userId ]);
	ws.call("bmpCollectorList", [ gSqlQuery.toXml() ]);
}

function searchCollector() {
	gCollectorCondition.SqlConditions = [];
	if ($("txtCollName").value != "") {
		gCollectorCondition.SqlConditions.push(JetsenWeb.SqlCondition.create(
				"COLL_NAME", $("txtCollName").value,
				JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike,
				JetsenWeb.SqlParamType.String));
	}

	if ($('txtIP').value != "") {
		gCollectorCondition.SqlConditions
				.push(JetsenWeb.SqlCondition.create("IP_ADDR",
						$("txtIP").value, JetsenWeb.SqlLogicType.And,
						JetsenWeb.SqlRelationType.ILike,
						JetsenWeb.SqlParamType.String));
	}
	gCollectorPage.currentPage = 1;
	loadCollector();
}
// 删除=====================================================================================
function deleteCollector(keyId) {
	jetsennet.confirm("确定删除？", function() {
		var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function(ret) {
			loadCollector();
		};
		ws.onerror = function(ex) {
			jetsennet.error(ex);
		};
		ws.call("bmpObjDelete", [ "BMP_COLLECTOR", keyId ]);
		return true;
	});
}
// 新增=====================================================================================
function newCollector() {
	var areaElements = JetsenWeb.Form.getElements('divCollector');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);
	gCollIp.setDisabled(false);

	var dialog = new JetsenWeb.UI.Window("new-object-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 350,
			height : 150
		},
		title : "新建采集器"
	});
	dialog.controls = [ "divCollector" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (gCollIp.getValue() == "")
			{
				jetsennet.alert("IP地址未正确填写！");
				return null;
			}
			var objCollector = {
				COLL_NAME : $("txt_COLL_NAME").value,
				COLL_TYPE : "0",
				IP_ADDR : gCollIp.getValue()
			};
//			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			var ws = new JetsenWeb.Service(BMPSC_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("new-object-win");
				searchCollector();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjInsert", [ "BMP_COLLECTOR",
					JetsenWeb.Xml.serializer(objCollector, "BMP_COLLECTOR") ]);
		}
	};
	dialog.showDialog();
}
// 编辑=====================================================================================
function editCollector(keyId) {
	var areaElements = JetsenWeb.Form.getElements("divCollector");
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("COLL_ID",
			keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	var gQueryTable = JetsenWeb.createQueryTable("BMP_COLLECTOR", "");
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "COLL_ID",
		PageInfo : null,
		QueryTable : gQueryTable
	});
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var objCollector = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
		$("txt_COLL_NAME").value = valueOf(objCollector, "COLL_NAME", "");
		gCollIp.setValue(valueOf(objCollector, "IP_ADDR", ""));
		gCollIp.setDisabled(true);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	var dialog = new JetsenWeb.UI.Window("edit-object-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : false,
		minimizeBox : false,
		size : {
			width : 350,
			height : 150
		},
		title : "编辑采集器"
	});
	dialog.controls = [ "divCollector" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			if (gCollIp.getValue() == "")
			{
				jetsennet.alert("IP地址未正确填写！");
				return null;
			}
			var oCollector = {
				COLL_ID : keyId,
				COLL_NAME : $("txt_COLL_NAME").value,
				IP_ADDR : gCollIp.getValue()
			};

			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				JetsenWeb.UI.Windows.close("edit-object-win");
				loadCollector();
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_COLLECTOR",
					JetsenWeb.Xml.serializer(oCollector, "BMP_COLLECTOR") ]);
		}
	};
	dialog.showDialog();
}

// 初始化===================================================================================
function pageInit() {
	searchCollector();
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

	frameContent.addControl(new JetsenWeb.UI.PageItem("divCollectorList"));
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

	gCollIp = new IP("txt_COLL_IP");
	gCollIp.init();
}

function windowResized() {
	var size = JetsenWeb.Util.getWindowViewSize();
	gFrame.size = {
		width : size.width,
		height : size.height
	};
	gFrame.resize();
}

//获取采集器在对象组中的id
function getCollId() {
	var sqlQuery = new JetsenWeb.SqlQuery();
	sqlQuery.GroupFields = " GROUP_ID ";
	var collQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "");
    JetsenWeb.extend(sqlQuery, { IsPageResult: 1, KeyId : "GROUP_ID",  ResultFields: " GROUP_ID", QueryTable: collQueryTable});
    var condition = new JetsenWeb.SqlConditionCollection();	
    sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
//    var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    var groupId="";
    ws.oncallback = function (ret) {   	
         var objs = JetsenWeb.Xml.toObject(ret.resultVal,"Record");
	     if(objs && objs.length>0)
	        {	    	    
	            for(var i=0;i<objs.length;i++)
	            {
	            	groupId =groupId+objs[i].GROUP_ID+",";
	            }
	        }
	     groupId = groupId.substring(0, groupId.length-1);
    }
    ws.onerror = function (ex) { jetsennet.error(ex); };
//    ws.call("bmpObjQuery", [sqlQuery.toXml()]);   
	ws.call("nmpPermissionsQuery", [sqlQuery.toXml(), 'GROUP_ID', '1']);
    return groupId;	
}

function isadmin(userId)
{	
	var result = false;
	var ws = new JetsenWeb.Service(NMP_PERMISSIONS_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret)
	{	
		result = ret.value;
	}
	ws.onerror = function(ex){jetsennet.error(ex);};
	ws.call("isAdministrator",[userId]); 
	return result;
}
