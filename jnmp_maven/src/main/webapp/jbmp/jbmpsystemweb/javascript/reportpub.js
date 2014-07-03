/**
 * 获得报表项目路径
 * 
 * @return
 */

function getReportWebPath() {
	var reportWebPath = "";
	var ws = new JetsenWeb.Service(BMP_RESOURCE_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(ret) {
		reportWebPath = ret.resultVal;
	};
	ws.onerror = function(ex) {
		alert(ex);
	};
	ws.call("bmpGetReportWebPath", "");
	return reportWebPath;
}
/**
 * 返回select参数，如果select中无选中项，且allFlag==true则返回所有值,有选中项时，只返回选中项，其它返-1。
 * 
 * @param select
 * @param paramName
 * @return
 */
function getSelectParam(select, paramName, allFlag) {
	var param = "";
	if (select.value != "") {
		param = paramName + "=" + select.value;
	} else if (allFlag) {
		var allObj = "" + getAllValues(select);
		if (allObj != "") {
		allObj = allObj.replace(",", "");
		param = paramName + "=" + allObj;
		}else{
			param = paramName + "=-1";
		}
	} else {
		param = paramName + "=-1";
	}
	return param;
}
/*
 * //初始化对象组 function objGroupInit() { var gSqlQuery = new JetsenWeb.SqlQuery();
 * var gQueryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "og");
 * 
 * var condition = new JetsenWeb.SqlConditionCollection();
 * 
 * condition.SqlConditions.push(JetsenWeb.SqlCondition.create("GROUP_TYPE",
 * "1,6", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.In,
 * JetsenWeb.SqlParamType.Numeric));
 * 
 * JetsenWeb.extend(gSqlQuery, { IsPageResult : 0, KeyId : "GROUP_ID",
 * QueryTable : gQueryTable, Conditions : condition, ResultFields : "DISTINCT
 * GROUP_ID,GROUP_NAME" }); var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
 * ws.soapheader = JetsenWeb.Application.authenticationHeader; ws.async = false;
 * ws.oncallback = function(ret) { var cboObjGroup = $("cbo_ObjGroup");
 * cboObjGroup.length = 0; cboObjGroup.options.add(new Option("请选择", ""));
 * 
 * var records = JetsenWeb.Xml.toObject(ret.resultVal, "Record"); if (records) {
 * var length = records.length; for ( var i = 0; i < length; i++) { var
 * objGroupInfo = records[i]; cboObjGroup.options.add(new
 * Option(objGroupInfo["GROUP_NAME"], objGroupInfo["GROUP_ID"])); } } };
 * ws.onerror = function(ex) { jetsennet.error(ex); }; ws.call("bmpObjQuery", [
 * gSqlQuery.toXml() ]); }
 */