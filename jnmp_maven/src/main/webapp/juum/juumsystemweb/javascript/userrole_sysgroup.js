var BMP_SYSTEM_SERVICE = JetsenWeb.appPath + "../../services/BMPSystemService?wsdl";
var selSysGroup = {};

/**
 * 获取选定的系统组
 * @return
 */
function getSelSysGroups() {
	var retval = [];
	for ( var key in selSysGroup) {
		if (typeof (selSysGroup[key]) != "function") {
			retval.push(key);
		}
	}
	return retval;
}

/**
 * 初始化角色组权限页面。
 * roleId > 0时，为编辑； roleId <= 0时，为新建
 * 
 * @param roleId
 * @return
 */
function initGroups(roleId) {

	selSysGroup = {}

	var queryTable = JetsenWeb.createQueryTable("BMP_OBJGROUP", "a");
	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { KeyId : "GROUP_ID", QueryTable : queryTable, ResultFields : "GROUP_ID,GROUP_TYPE,GROUP_NAME" });

	var conditions = new JetsenWeb.SqlConditionCollection();
	conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("a.GROUP_TYPE", "1", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal,
			JetsenWeb.SqlParamType.Numeric));

	sqlQuery.Conditions = conditions;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.async = false;
	ws.oncallback = function(sResult) {
		renderGroupSet(sResult.resultVal, roleId)
	};
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);

	if (roleId > 0) {
		initRoleTable(roleId);
	}
}

var gridList = new JetsenWeb.UI.GridList("groupSys-grid");

function renderGroupSet(xml, roleId) {

	var groupDiv = $("divObjGroup");
	groupDiv.innerHTML = "";

	gridList.columns = [ { index : 0, fieldName : "GROUP_ID, GROUP_NAME, GROUP_TYPE", width : 30, name : "" },
			{ index : 1, fieldName : "GROUP_NAME", width : 350, name : "组名称" } ];

	gridList.columns[0].format = function(val, vals) {
		val = "<input type=\"checkbox\" id=\"cbGroup" + vals[0] + "\" onclick=\"selGroup(" + vals[0] + ",\'" + vals[1] + "\'," + vals[2] + "," + roleId + ")\">";
		return val;
	}

	gridList.idField = "GROUP_ID";
	gridList.dataSource = xml;
	gridList.render("divObjGroup");
	gridList.colorSelectedRows();
}

//创建对象组树
function createObjGroupTree(roleId) {
	initGroups(roleId);
}

// 页面对象组选择操作
function selGroup(groupId, groupName, groupType, roleId) {
	var selGroup = $("cbGroup" + groupId);
	if (selGroup.checked) {
		selSysGroup[groupId] = groupName;
	} else {
		// 是否禁止管理员进行反选操作？
		if(roleId == 1) {
			
		}
		delete selSysGroup[groupId];
	}
}

// 初始化角色组权限
function initRoleTable(roleId) {
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
	conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ROLE_ID", roleId, JetsenWeb.SqlLogicType.And,
			JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

	var sqlQuery = new JetsenWeb.SqlQuery();
	JetsenWeb.extend(sqlQuery, { IsPageResult : 0, KeyId : "ROLE_ID", PageInfo : null, ResultFields : "",
		QueryTable : JetsenWeb.extend(new JetsenWeb.QueryTable(), { TableName : "BMP_ROLE2GROUP" }) });

	sqlQuery.Conditions = conditionCollection;

	var ws = new JetsenWeb.Service(BMP_SYSTEM_SERVICE);
	ws.soapheader = JetsenWeb.Application.authenticationHeader;
	ws.oncallback = function(ret) {
		var groups = JetsenWeb.Xml.toObject(ret.resultVal, "Record");
		if (groups) {
			for (i = 0; i < groups.length; i++) {
				var tempGroup = groups[i]["GROUP_ID"];
				var tempRole = groups[i]["ROLE_ID"];
				selSysGroup[tempGroup] = tempRole;
				var tempCB = $("cbGroup" + tempGroup);
				if (tempCB) {
					$("cbGroup" + tempGroup).checked = true;
				}
			}
		}
	}
	ws.onerror = function(ex) {
		jetsennet.error(ex);
	};
	ws.call("bmpObjQuery", [ sqlQuery.toXml() ]);
}