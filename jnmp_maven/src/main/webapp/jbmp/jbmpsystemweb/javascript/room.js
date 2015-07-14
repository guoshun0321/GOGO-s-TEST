var gRoomPage = new JetsenWeb.UI.PageBar("Room");
gRoomPage.onpagechange = function () { loadRoom(); };
gRoomPage.orderBy = "";
gRoomPage.onupdate = function () 
{
    $('divRoomPage').innerHTML = this.generatePageControl();
};
var gRoomCondition = new JetsenWeb.SqlConditionCollection();
var gGridList = new JetsenWeb.UI.GridList();
gGridList.ondatasort = function (sortfield, desc) 
{
    gRoomPage.setOrderBy(sortfield, desc);
};
var gRoomSqlQuery = new JetsenWeb.SqlQuery();
var gRoomQueryTable = JetsenWeb.createQueryTable("NMP_ROOM", "a");
gRoomQueryTable.addJoinTable(JetsenWeb.createJoinTable("NMP_ROOM2FLOOR", "b", "a.ROOM_ID = b.ROOM_ID", JetsenWeb.TableJoinType.Inner));
JetsenWeb.extend(gRoomSqlQuery, { IsPageResult: 1, KeyId: "ROOM_ID", PageInfo: gRoomPage, QueryTable: gRoomQueryTable });

var curRoomName;
var curRoomAlias;
//得到对象
function getRoom(id)
{  
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult: 0, KeyId: "ROOM_ID", PageInfo: null, ResultFields: "", QueryTable: JetsenWeb.extend(new JetsenWeb.QueryTable(), {TableName: "NMP_ROOM"})});
		        
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ROOM_ID", id, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.Numeric));        
    sqlQuery.Conditions = condition;
    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(RoomsResult)
    {
        var Room = JetsenWeb.Xml.toObject(RoomsResult.resultVal, "Record")[0];
        curRoomName = valueOf(Room, "ROOM_NAME", "");
        curRoomAlias = valueOf(Room, "ROOM_ALIAS", "");
        $("ROOM_NAME").value = curRoomName;
        $("ROOM_ALIAS").value = curRoomAlias;
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("nmpObjQuery",[sqlQuery.toXml()]);
}
		
//加载
function loadRoom() 
{
    gRoomSqlQuery.OrderString = gRoomPage.orderBy;
    gRoomSqlQuery.Conditions = gRoomCondition;     
    var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function (ret) 
    {
        $("divRoomList").innerHTML = JetsenWeb.Xml.transformXML("xslt/room.xslt", ret.resultVal);
        gGridList.bind($("divRoomList"), $("tabUserRoom"));
        gRoomPage.setRowCount($("hid_RoomCount").value);
    };
    ws.onerror = function (ex) { jetsennet.error(ex); };
    ws.call("nmpObjQuery", [gRoomSqlQuery.toXml()]);
}

function searchRoom()
{
    gRoomCondition.SqlConditions = [];
    if($('ROOM_NAME_SEARCH').value!="")
    {
        gRoomCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ROOM_NAME", $('ROOM_NAME_SEARCH').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));	       
    }
    if($('ROOM_ALIAS_SEARCH').value!="")
    {
        gRoomCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("a.ROOM_ALIAS", $('ROOM_ALIAS_SEARCH').value, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.ILike, JetsenWeb.SqlParamType.String));	       
    }
    gRoomCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("b.FLOOR_ID", curFloorId, JetsenWeb.SqlLogicType.And, JetsenWeb.SqlRelationType.Equel, JetsenWeb.SqlParamType.Int));
    gRoomPage.currentPage = 1;
    loadRoom();
}
       
//新建
function newRoom()
{
    var areaElements = JetsenWeb.Form.getElements('divRoom');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements); 
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("add-Room"), { title: "新建", submitBox: true, cancelBox: true, size: { width: 500, height: 250 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divRoom"];
    curRoomName = "";
    curRoomAlias = "";
    dialog.onsubmit = function ()
    {
       if (JetsenWeb.Form.Validate(areaElements, true)) 
       {
    	   var roomName = $("ROOM_NAME").value;
    	   var roomAlias = $("ROOM_ALIAS").value;
    	   if(roomIsRepeat(roomName, roomAlias)){
    		   JetsenWeb.alert("房间名或房间别名重复！");
    		   return;
    	   }
    	   
          var RoomXml = JetsenWeb.Xml.serialize({
              ROOM_NAME:roomName,
              ROOM_ALIAS:roomAlias
          },"NMP_ROOM"); 
          var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
          ws.soapheader = JetsenWeb.Application.authenticationHeader;
          ws.onerror = function(ex){ jetsennet.alert(ex);};
          ws.oncallback = function(sResult)
          {				
              JetsenWeb.UI.Windows.close("add-Room");	
              loadRoom();
          }
          ws.call("newRoom",[RoomXml, curFloorId]);     
        }
    };
    dialog.showDialog();
}

//编辑
function editRoom(id)
{
    var areaElements = JetsenWeb.Form.getElements('divRoom');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements); 
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("add-Room"), { title: "编辑", submitBox: true, cancelBox: true, size: { width: 500, height: 250 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divRoom"];
    getRoom(id);
    dialog.onsubmit = function () 
    {
       if (JetsenWeb.Form.Validate(areaElements, true)) 
       {
    	   var roomName = $("ROOM_NAME").value;
    	   var roomAlias = $("ROOM_ALIAS").value;
    	   if(roomIsRepeat(roomName, roomAlias)){
    		   JetsenWeb.alert("房间名或房间别名重复！");
    		   return;
    	   }
    	   
           var RoomXml = JetsenWeb.Xml.serialize({
                ROOM_ID:id,
                ROOM_NAME:roomName,
                ROOM_ALIAS:roomAlias
           },"NMP_ROOM");
           var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
           ws.soapheader = JetsenWeb.Application.authenticationHeader;
           ws.onerror = function(ex){ jetsennet.alert(ex);};
           ws.oncallback = function(sResult)
           {				
               JetsenWeb.UI.Windows.close("add-Room");	
               loadRoom();
           }
           ws.call("nmpObjUpdate",["NMP_ROOM",RoomXml]);   
       }
    };
    dialog.showDialog();
}

//房间名和房间别名都不能重复
function roomIsRepeat(roomName, roomAlias){
	var flag = false;
	
	if(!(curRoomName == roomName && curRoomAlias == roomAlias)){
		var conditionCollection = new JetsenWeb.SqlConditionCollection();
		if(roomName != null && roomName != '' && curRoomName != roomName){
			conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ROOM_NAME", roomName, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
		}
		if(roomAlias != null && roomAlias != '' && curRoomAlias != roomAlias){
			conditionCollection.SqlConditions.push(JetsenWeb.SqlCondition.create("ROOM_ALIAS", roomAlias, JetsenWeb.SqlLogicType.Or, JetsenWeb.SqlRelationType.Equal, JetsenWeb.SqlParamType.String));
		}
		
		var sqlQuery = new JetsenWeb.SqlQuery();
		var gQueryTable = JetsenWeb.createQueryTable("NMP_ROOM", "");
		
		JetsenWeb.extend(sqlQuery, { IsPageResult: 0, KeyId: "ROOM_ID", PageInfo: null, ResultFields: "ROOM_ID", QueryTable: gQueryTable });
		
		sqlQuery.Conditions = conditionCollection;
		
		var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
		ws.async = false;
		ws.soapheader = JetsenWeb.Application.authenticationHeader;
		ws.oncallback = function (ret) {
			var result = JetsenWeb.xml.toObject(ret.resultVal,"Record");
			if(result != null){
				flag = true;
			}else{
				flag = false;
			}
		}
		ws.call("nmpObjQuery", [sqlQuery.toXml()]);
	}
    
    return flag;
}

//删除
function deleteRoom(id)
{
    jetsennet.confirm("确定删除？", function () 
    {
        var ws = new JetsenWeb.Service(NMP_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.oncallback = function(sResult)
        {	        
           loadRoom();
        }	
        ws.call("nmpObjDelete",["NMP_ROOM",id]);     
        return true;
    });
}

var curFloorId;
function showRoom(floorId)
{
	curFloorId = floorId;

	searchRoom();
	
	var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("select-room"), { title: "房间", submitBox: false, cancelBox: true, size: { width: 800, height: 500 }, maximizeBox: false, minimizeBox: false });
    dialog.controls = ["divShowListRoom"];
    dialog.onsubmit = function () {
    };
    dialog.showDialog();
}
