// 枚举=================================================================================================
var gEnumPage = new JetsenWeb.UI.PageBar("divEnumBottomPage");
gEnumPage.onpagechange = function() {
	loadEnumList();
};
gEnumPage.orderBy = "";
gEnumPage.onupdate = function() {
	$('divEnumPage').innerHTML = this.generatePageControl();
};
var enumGridList = new JetsenWeb.UI.GridList();
enumGridList.ondatasort = function(sortfield, desc) {
	gEnumPage.setOrderBy(sortfield, desc);
};
function mibEnum(mibId, mibAlias) {
	$("curMibType").value = mibId;
	gEnumPage.currentPage = 1;
	var dialog = new JetsenWeb.UI.Window("enum-detail-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : false, windowStyle : 1, cancelButtonText : "关闭", maximizeBox : true,
		minimizeBox : true, size : { width : 800, height : 500 }, title : "枚举" });
	dialog.controls = [ "divEnum" ];
	dialog.showDialog();
	loadEnumList();
}
function searchEnum() {
	gEnumPage.currentPage = 1;
	loadEnumList();
}
var curEnumId = -1;
function loadEnumList() {
	curEnumId = -1;
	var mibId = $("curMibType").value;
	
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 1, PageInfo : gEnumPage, KeyId : "VALUE_ID", ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_VALUETABLE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("MIB_ID", mibId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_TYPE", -1, JetsenWeb.SqlLogicType.AndAll, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var enumSch = $("txt_Enum_Desc").value;
	if(enumSch) {
		condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_NAME", enumSch, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Like,
				JetsenWeb.SqlParamType.String));
	}
	sqlQuery.Conditions = condition;
	
	sqlQuery.OrderString = gEnumPage.orderBy;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divEnumList').innerHTML = JetsenWeb.Xml.transformXML("xslt/mibbanks_snmpnode_enum.xslt", ret.resultVal);
		enumGridList.bind($('divEnumList'), $('tabValueTable'));
		gEnumPage.setRowCount($('hid_EnumCount').value);
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function showEnumDetail(keyId) {
	curEnumId = keyId;
	var dialog = new JetsenWeb.UI.Window("enum-list-win");
	JetsenWeb.extend(dialog, { submitBox : false, cancelBox : true, windowStyle : 1, cancelButtonText : "关闭", maximizeBox : true,
		minimizeBox : true, size : { width : 800, height : 500 }, title : "枚举值" });
	dialog.controls = [ "divEnumDetail" ];
	enumDetailList(keyId);
	dialog.showDialog();
}
function enumDetailList(keyId) {
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, PageInfo : null, KeyId : "VALUE_ID", ResultFields : "*",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_VALUETABLE" }) });
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_TYPE", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	sqlQuery.Conditions = condition;

	var gridList = new JetsenWeb.UI.GridList();

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		$('divEnumDetailList').innerHTML = JetsenWeb.Xml.transformXML("xslt/mibbanks_snmpnode_enum_detail.xslt", ret.resultVal);
		gridList.bind($('divEnumDetailList'), $('tabValueDetailTable'));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}
function editEnum(keyId, pwin) {
	var mibId = $("curMibType").value;
	var areaElements = JetsenWeb.Form.getElements('divEnumAdd');
	JetsenWeb.Form.resetValue(areaElements);
	JetsenWeb.Form.clearValidateState(areaElements);

	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "VALUE_ID", PageInfo : null,
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_VALUETABLE" }) });
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		try {
			if (ret == null) {
				return;
			}
			var obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
			$("txt_ATTRIB_VALUE").value = valueOf(obj, "ATTRIB_VALUE", "");
            $("txt_VALUE_NAME").value = valueOf(obj, "VALUE_NAME", "");
			$("txt_VALUE_DESC").value = valueOf(obj, "VALUE_DESC", "");
		} catch (ex) {
			jetsennet.error(ex);
		}
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
	
	$("trAttribValue").style.display = "none";

	var ptitle = "编辑枚举";
    if(pwin == 1) {
    	ptitle = "编辑枚举值";
    }
	var dialog = new JetsenWeb.UI.Window("edit-enum-win");
	JetsenWeb.extend(dialog, { submitBox : true, cancelBox : true, windowStyle : 1, maximizeBox : false, minimizeBox : false,
		size : { width : 400, height : 140 }, title : ptitle });
	dialog.controls = [ "divEnumAdd" ];
	dialog.onsubmit = function() {
		if (JetsenWeb.Form.Validate(areaElements, true)) {
			var outObj = { 
					VALUE_NAME : $("txt_VALUE_NAME").value,
					VALUE_DESC : $("txt_VALUE_DESC").value, 
					VALUE_ID : keyId};
			var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
			ws.soapheader = JetsenWeb.Application.authenticationHeader;
			ws.oncallback = function(ret) {
				if(pwin == 0) {
            		loadEnumList();
            	} else {
            		enumDetailList(curEnumId);
            	}
				JetsenWeb.UI.Windows.close("edit-enum-win");
			};
			ws.onerror = function(ex) {
				jetsennet.error(ex);
			};
			ws.call("bmpObjUpdate", [ "BMP_VALUETABLE", JetsenWeb.Xml.serializer(outObj, "BMP_VALUETABLE") ]);
		}
	};
	dialog.showDialog();
}
//添加
function addEnum(pwin) {
	var mibId = $("curMibType").value;
    var areaElements = JetsenWeb.Form.getElements('divEnumAdd');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    if(pwin == 0) {
    	$("trAttribValue").style.display = "none";
    } else {
    	$("trAttribValue").style.display = "";
    }
    var dialog = new JetsenWeb.UI.Window("new-enum-win");
    var h = 140;
    if(pwin == 1) {
    	h = 160;
    }
    var ptitle = "新建枚举";
    if(pwin == 1) {
    	ptitle = "新建枚举值";
    }
    JetsenWeb.extend(dialog, { 
    	submitBox: true, 
    	cancelBox: true, 
    	windowStyle: 1, 
    	maximizeBox: false, 
    	minimizeBox: false, size: { width: 500, height: h }, 
    	title: ptitle });
    dialog.controls = ["divEnumAdd"];
    dialog.onsubmit = function () {
    	if(pwin == 0) {
    		$("txt_ATTRIB_VALUE").value = 0;
    	}
        if (JetsenWeb.Form.Validate(areaElements, true)) {
        	if(pwin == 0) {
	            var objEnum = {
	            	VALUE_TYPE : -1	
	              , MIB_ID : mibId
	              , VALUE_NAME: $("txt_VALUE_NAME").value
	              , VALUE_DESC: $("txt_VALUE_DESC").value
	            };
        	} else {
        		var objEnum = {
	            	VALUE_TYPE : curEnumId	
	              , MIB_ID : mibId
	              , ATTRIB_VALUE : $("txt_ATTRIB_VALUE").value
	              , VALUE_NAME: $("txt_VALUE_NAME").value
	              , VALUE_DESC: $("txt_VALUE_DESC").value
	            };
        	}
            var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	if(pwin == 0) {
            		loadEnumList();
            	} else {
            		enumDetailList(curEnumId);
            	}
                JetsenWeb.UI.Windows.close("new-enum-win");
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("bmpObjInsert", ["BMP_VALUETABLE", JetsenWeb.Xml.serializer(objEnum, "BMP_VALUETABLE")]);
        }
    }
    dialog.showDialog();
}
//删除
function delEnum(keyId, pwin) {
	jetsennet.confirm("确定删除？", function (){
        var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {
        	if(pwin == 0) {
        		loadEnumList();
        	} else {
        		enumDetailList(curEnumId);
        	}
        };
        ws.onerror = function (ex) { jetsennet.error(ex); };
        ws.call("bmpObjDelete", ["BMP_VALUETABLE", keyId]);
        return true;
    });
}