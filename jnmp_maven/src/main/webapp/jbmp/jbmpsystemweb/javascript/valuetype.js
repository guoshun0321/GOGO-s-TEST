//加载参考值列表
function loadValueType() {
	var condition = new JetsenWeb.SqlConditionCollection();
	condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_TYPE", -1, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
	var sqlQuery = new JetsenWeb.SqlQuery();
	var queryTable = JetsenWeb.createQueryTable("BMP_VALUETABLE", "t");
	JetsenWeb.extend(sqlQuery, {
		IsPageResult : 0,
		KeyId : "VALUE_ID",
		PageInfo : null,
		QueryTable : queryTable,
		ResultFields : "t.*",
		OrderString : "ORDER BY t.VALUE_NAME"
	});
	sqlQuery.Conditions = condition;

	var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.cacheLevel = 2;
	ws.oncallback = function(ret) {
		$("divValueTypeContent").innerHTML = JetsenWeb.Xml.transformXML("xslt/valuetypelist.xslt", ret.resultVal);
		var gValueTypeListGrid = new JetsenWeb.UI.GridList();
		gValueTypeListGrid.bind($("divValueTypeContent"), $("tabValueTypeList"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpObjQuery", [ sqlQuery.toXml() ]);
}

//参考值配置对话框
function showValueTypeDig() {
	var curType = $("txt_VALUE_TYPE").value;
	if(curType == "") {
		curType = 0;
	}
	
	var objs = document.getElementsByName('chkAllVT');
    for (var i = 0; i < objs.length; i++) {
    	if(objs[i].value == curType) {
    		objs[i].checked = true;
    	} else {
    		objs[i].checked = false;
    	}
    }
	
	var areaElements = JetsenWeb.Form.getElements("divValueType");
	var dialog = new JetsenWeb.UI.Window("edit-vtsel-win");
	JetsenWeb.extend(dialog, {
		submitBox : true,
		cancelBox : true,
		windowStyle : 1,
		maximizeBox : true,
		minimizeBox : true,
		size : {
			width : 700,
			height : 350
		},
		title : "选择参考值"
	});
	dialog.controls = [ "divValueType" ];
	dialog.onsubmit = function() {
		var curVT = getSingleCheckedValues("chkAllVT");
		if(curVT == null) {
			curVT = 0;
		}
		$("txt_VALUE_TYPE").value = curVT;
		JetsenWeb.UI.Windows.close("edit-vtsel-win");
	};
	dialog.showDialog();
}

//参考值详情
function loadSubValueType(valueId) {
	var objs = document.getElementsByName("chkAllVT");
	var curckb = null;
	if (objs != null && objs.length != 0) {
		for ( var i = 0; i < objs.length; i++) {
			if (objs[i].value == valueId) {
				curckb = objs[i];
				break;
			}
		}
	}
	if (curckb != null) {
		var cha = curckb.checked;
		JetsenWeb.Form.checkAllItems('chkAllVT', false);
		curckb.checked = !cha;
	}
	
	$("divValueTypeDetails").innerHTML = "";
	if(valueId == "") {
		return;
	}
	var vtQuery = new JetsenWeb.SqlQuery();
	var vtTable = JetsenWeb.createQueryTable("BMP_VALUETABLE", "a");
	
	var vtCondition = new JetsenWeb.SqlConditionCollection();
	vtCondition.SqlConditions.push(JetsenWeb.SqlCondition
			.create("a.VALUE_TYPE", valueId, JetsenWeb.SqlLogicType.And,
					JetsenWeb.SqlRelationType.Equal,
					JetsenWeb.SqlParamType.Numeric));
	JetsenWeb.extend(vtQuery, {
		KeyId : "VALUE_ID",
		QueryTable : vtTable,
		Conditions : vtCondition,
		ResultFields : "a.*"
	});
	var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		$("divValueTypeDetails").innerHTML = JetsenWeb.Xml.transformXML("xslt/valuetable.xslt", ret.resultVal);
		var vtGridList = new JetsenWeb.UI.GridList();
		vtGridList.bind($("divValueTypeDetails"), $("tabValueTable"));
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("nmpObjQuery", [ vtQuery.toXml() ]);
}

// 复选框单选操作
function vtCheckChange(cb) {
	var cha = cb.checked;
	JetsenWeb.Form.checkAllItems('chkAllVT',false);
	cb.checked=cha;
}

//编辑
function editVT(keyId, parentId) {
	clearVTDialog();
    $("txt_VALUE_ID1").disabled = true;
    $("txt_ATTRIB_VALUE1").disabled = true;
    $("txt_VALUE_NAME1").disabled = true;
    var areaElements = JetsenWeb.Form.getElements('divValutTablePop');

    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("VALUE_ID", keyId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery, { 
    	IsPageResult: 0, 
    	KeyId: "VALUE_ID", 
    	PageInfo: null, 
    	QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName: "BMP_VALUETABLE" }) });
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
        try {
            var obj = JetsenWeb.Xml.toObject(ret.resultVal, "Record")[0];
            $("txt_VALUE_ID1").value = valueOf(obj, "VALUE_ID", "");
            $("txt_ATTRIB_VALUE1").value = valueOf(obj, "ATTRIB_VALUE", "");
            $("txt_VALUE_NAME1").value = valueOf(obj, "VALUE_NAME", "");
            $("txt_VALUE_DESC1").value = valueOf(obj, "VALUE_DESC", "");
        } catch (ex) {
            jetsennet.error(ex);
        }
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("nmpObjQuery", [sqlQuery.toXml()]);

    var dialog = new JetsenWeb.UI.Window("edit-vt-win");
    JetsenWeb.extend(dialog, { submitBox: true, cancelBox: true, windowStyle: 1, maximizeBox: false, minimizeBox: false, size: { width: 400, height: 200 }, title: "编辑参考值" });
    dialog.controls = ["divValutTablePop"];
    dialog.onsubmit = function () {
        if (JetsenWeb.Form.Validate(areaElements, true)) {
            var outObj = {
                VALUE_ID: keyId,
                VALUE_DESC: $("txt_VALUE_DESC1").value
            };
            var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function (ret) {
            	loadSubValueType(parentId);
                JetsenWeb.UI.Windows.close("edit-vt-win");
            };
            ws.onerror = function (ex) { jetsennet.error(ex); };
            ws.call("nmpObjUpdate", ["BMP_VALUETABLE", JetsenWeb.Xml.serializer(outObj, "BMP_VALUETABLE")]);
        }
    };
    dialog.showDialog();
}
// 清理弹出dialog
function clearVTDialog() {
    var areaElements = JetsenWeb.Form.getElements('divValutTablePop');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
}