var curRoomId;
var curRfType;
function showTopo(rfId, rfType)
{
	curRoomId = rfId;
	curRfType = rfType;

	createTopoTree();
	gTopoTree.getItem(function (item) {
        item.setCheck(false);
    }, true);
	setTreeNode();
	
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-topo2rf"), { title: "拓扑", submitBox: true, cancelBox: true, size: { width: 400, height: 500 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divShowListTopo2RF"];
    dialog.onsubmit = function () {
    	var checkIds = [];
    	gTopoTree.getItem(function(item){           
            if(item.checked==true)
            {
                checkIds.push(nowTopoData[item.checkValue]);
            }
        },true);
    	
    	var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
        ws.async = true;
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function (ret) {}
        ws.call("addTopo2rfs", [checkIds.join(","), rfId, rfType]);
        
        dialog.close();
    };
    dialog.showDialog();
}

var gTopoTree = null;
var nowTopoData = {};
//创建拓扑图树
function createTopoTree() {
    if(gTopoTree != null) {
        return;
    }
    
    var conditionCollection = new JetsenWeb.SqlConditionCollection();
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.USE_TYPE", "2", JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.USE_TYPE", "", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.IsNull, JetsenWeb.SqlParamType.Numeric));
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("A.MAP_STATE", "0", JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.NotEqual, JetsenWeb.SqlParamType.Numeric));

    var sqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_TOPOMAP", "A");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("BMP_GROUP2GROUP", "B","A.GROUP_ID=B.GROUP_ID", JetsenWeb.TableJoinType.Left));
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "MAP_ID", PageInfo: null, ResultFields: "A.*,B.PARENT_ID", OrderString: "ORDER BY A.MAP_ID", QueryTable: gQueryTable });
    sqlQuery.Conditions = conditionCollection;

    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.async = false;
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	//将GROUP_ID和MAP_ID的关系保存起来，树里的值是GROUP_ID，存的时候需要MAP_ID，到时候转换。
    	var result = JetsenWeb.xml.toObject(ret.resultVal,"Record");
    	var json = "{";
    	for(var i = 0; i < result.length; i++){
    		json += "\"" + result[i].GROUP_ID + "\":\"" + result[i].MAP_ID + "\",";
    	}
    	if(json.indexOf(',') > -1){
    		json = json.substring(0, json.length - 1);
    	}
    	json += "}";
    	nowTopoData = eval("(" + json + ")");
    	
        gTopoTree = JetsenWeb.UI.Tree.createTree("topo-tree", ret.resultVal, { parentId: "", parentField: "PARENT_ID", itemName: "Record", textField: "MAP_NAME", valueField: "GROUP_ID", showCheck: true });
    	gTopoTree.getItem(function (item) {
            item.clickChange = true;
        }, true);
        $("divTopo").appendChild(gTopoTree.render());
    }
    ws.call("nmpObjQuery", [sqlQuery.toXml()]);
}

function setTreeNode(){
	var conditionCollection = new JetsenWeb.SqlConditionCollection();
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.RF_ID", curRoomId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));
    conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("B.RF_TYPE", curRfType, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));

    var sqlQuery = new JetsenWeb.SqlQuery();
    var gQueryTable = JetsenWeb.createQueryTable("BMP_TOPOMAP", "A");
    gQueryTable.addJoinTable(JetsenWeb.createJoinTable("NMP_TOPO2RF", "B","A.MAP_ID=B.MAP_ID", JetsenWeb.TableJoinType.Inner));
    
    JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "MAP_ID", PageInfo: null, ResultFields: "A.MAP_ID", QueryTable: gQueryTable });

    sqlQuery.Conditions = conditionCollection;

    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.async = false;
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) {
    	var result = JetsenWeb.xml.toObject(ret.resultVal,"Record");
    	if(result != null){
    		gTopoTree.getItem(function(item){
    			for(var i = 0; i < result.length; i++){
    				if(nowTopoData[item.checkValue] == result[i].MAP_ID)
    					item.setCheck(true);
    			}
    		},true);
    	}
    }
    ws.call("nmpObjQuery", [sqlQuery.toXml()]);
}