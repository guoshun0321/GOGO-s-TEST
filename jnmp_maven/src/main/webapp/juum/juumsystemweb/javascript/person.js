//=============================================================================
//  person application
//=============================================================================
        
//用户组===================================================================
function loadGroupTree()
{
    $('divTree').innerHTML = "";
    gDepartTree = new JetsenWeb.UI.Tree("depart-tree");
    var _topTree = new JetsenWeb.UI.TreeItem("所有分组","javascript:searchPerson(0)");
    _topTree.isOpen = true;
    gDepartTree.addItem(_topTree);
    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",ResultFields:"ID,NAME,PARENT_ID,TYPE",PageInfo:null,
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_USERGROUP"})});
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions = [JetsenWeb.SqlCondition.create("ID","0",JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.NotEqual,JetsenWeb.SqlParamType.Numeric)];
    sqlQuery.Conditions = condition;

    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.cacheLevel = 2;
    ws.oncallback = function(ret)
    {
         var _obj = JetsenWeb.Xml.toObject(ret.resultVal,"Record");
         if(_obj)
         {
             var _len = _obj.length;
             for(var i=0;i<_len;i++)
             {
                if(_obj[i].PARENT_ID==null || _obj[i].PARENT_ID=="0")
                {
                    var _subItem = new JetsenWeb.UI.TreeItem(_obj[i].NAME,"javascript:searchPerson("+_obj[i].ID+")");
                    _topTree.addItem(_subItem);
                    createSubTree(_subItem,_obj,_obj[i].ID);                            
                }
             }
         }
        var _control = gDepartTree.render();
        if(_control!=null)
            $('divTree').appendChild(_control); 
        searchPerson(0);
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
}
function createSubTree(treeItem,obj,parentId)
{
    if(obj==null)
        return;
    var _len = obj.length;
     for(var i=0;i<_len;i++)
     {
        if(obj[i].PARENT_ID==parentId)
        {
            var _subItem = new JetsenWeb.UI.TreeItem(obj[i].NAME,"javascript:searchPerson("+obj[i].ID+")");
            treeItem.addItem(_subItem);
            createSubTree(_subItem,obj,obj[i].ID);
        }
     }

}
//加载用户=====================================================================================
function loadPerson()
{    
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{IsPageResult:1,KeyId:"ID",PageInfo:gPersonPage,ResultFields:"",               
            QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_PERSON"})});
    sqlQuery.Conditions = gPersonCondition;
    sqlQuery.OrderString = gPersonPage.orderBy;        
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        $('divPersonList').innerHTML = JetsenWeb.Xml.transformXML("xslt/person.xslt",ret.resultVal);
        gGridList.bind($('divPersonList'), $('tabPerson'));
        gPersonPage.setRowCount($('hid_PersonCount').value);
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);		
}
function searchPerson(groupId)
{
    gGroupId = groupId==null?gGroupId:groupId;
    gPersonCondition.SqlConditions = [];
    if($('txtUserName').value!="")
    {
         var _txtCondition = new JetsenWeb.SqlCondition();
        _txtCondition.SqlLogicType = JetsenWeb.SqlLogicType.And;
        _txtCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("NAME",$('txtUserName').value,JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Like,JetsenWeb.SqlParamType.String));
        _txtCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("USER_CODE",$('txtUserName').value,JetsenWeb.SqlLogicType.Or,JetsenWeb.SqlRelationType.Like,JetsenWeb.SqlParamType.String));
        gPersonCondition.SqlConditions.push(_txtCondition);
    }
    if(gGroupId!=0)
    {
        gPersonCondition.SqlConditions.push(JetsenWeb.SqlCondition.create("UUM_PERSON.GROUP_ID",gGroupId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Custom,JetsenWeb.SqlParamType.String));
    }
    gPersonPage.currentPage = 1;
    loadPerson();
}
//删除=====================================================================================
function deletePerson(keyId)
{
    var checkIds = [];
    if(keyId)
    {
        checkIds = [keyId];
    }
    else
    {
        checkIds = JetsenWeb.Form.getCheckedValues("chkPerson");
    }
    
    if(checkIds.length==0)
    {
        jetsennet.alert("请选择要删除的项！");
        return;
    }   

    jetsennet.confirm("确定删除？", function () 
    {
    
        var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
        ws.soapheader = JetsenWeb.Application.authenticationHeader;
        ws.oncallback = function(ret)
        {
            loadPerson();
        };
        ws.onerror = function(ex){ jetsennet.error(ex);};
        ws.call("uumObjDelete",["UUM_PERSON","<Request><Item><Id>"+checkIds.join("</Id></Item><Item><Id>")+"</Id></Item></Request>"]);
     return true;
    });
}
//新建=====================================================================================
function  newPerson()
{
    var areaElements = JetsenWeb.Form.getElements('divPerson');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("new-person"),{title:"新建人员",submitBox:true,cancelBox:true,size:{width:620,height:385},maximizeBox:false,minimizeBox:false});    
    dialog.controls =["divPerson"];
    dialog.onsubmit = function()
    {
        if(JetsenWeb.Form.Validate(areaElements,true))
        {
            var _Person = {                     
              NAME:$("txt_NAME").value
              ,USER_CODE:$("txt_CODE").value
              ,SEX:$("ddl_SEX").value
              ,DUTY_TITLE:$("txt_DUTY_TITLE").value
              ,ADDRESS:$("txt_ADDRESS").value
              ,EMAIL:$("txt_EMAIL").value
              ,OFFICE_PHONE:$("txt_OFFICE_PHONE").value
              ,HOME_PHONE:$("txt_HOME_PHONE").value
              ,MOBILE_PHONE:$("txt_MOBILE_PHONE").value
              ,JOIN_DATE:$("txt_JOIN_DATE").value                     
              ,USER_CARD:$("txt_USER_CARD").value                 
              ,DESCRIPTION:$("txt_DESCRIPTION").value
              ,BIRTHDAY:$("txt_BIRTHDAY").value
              ,STATE:$("ddl_STATE").value
              ,GROUP_ID:gGroupId
              };
            var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("new-person");
                loadPerson();                    
            };
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjInsert",["UUM_PERSON",JetsenWeb.Xml.serialize(_Person,"UUM_PERSON")]);
        }
    };
    dialog.showDialog();
}
//编辑=====================================================================================
function  editPerson(keyId)
{
    var areaElements = JetsenWeb.Form.getElements('divPerson');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    
    var condition = new JetsenWeb.SqlConditionCollection();
    condition.SqlConditions.push(JetsenWeb.SqlCondition.create("ID",keyId,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    
    var sqlQuery = new JetsenWeb.SqlQuery();
    JetsenWeb.extend(sqlQuery,{IsPageResult:0,KeyId:"ID",PageInfo:null,QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"UUM_PERSON"})});
    sqlQuery.Conditions = condition;
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.oncallback = function(ret)
    {
        var _Person = JetsenWeb.Xml.toObject(ret.resultVal,"Record")[0];              
        $("txt_NAME").value = valueOf(_Person,"NAME","");
        $("txt_CODE").value = valueOf(_Person,"USER_CODE","");
        $("ddl_SEX").value = valueOf(_Person,"SEX","0");
        $("txt_DUTY_TITLE").value = valueOf(_Person,"DUTY_TITLE","");
        $("txt_ADDRESS").value = valueOf(_Person,"ADDRESS","");
        $("txt_EMAIL").value = valueOf(_Person,"EMAIL","");
        $("txt_OFFICE_PHONE").value = valueOf(_Person,"OFFICE_PHONE","");
        $("txt_HOME_PHONE").value = valueOf(_Person,"HOME_PHONE","");
        $("txt_MOBILE_PHONE").value = valueOf(_Person,"MOBILE_PHONE","");
        $("txt_JOIN_DATE").value = valueOf(_Person,"JOIN_DATE","").substr(0,10);                
        $("txt_USER_CARD").value = valueOf(_Person,"USER_CARD","");           
        $("txt_BIRTHDAY").value = valueOf(_Person,"BIRTHDAY","").substr(0,10); 
        $("txt_DESCRIPTION").value = valueOf(_Person,"DESCRIPTION","");
        $("ddl_STATE").value = valueOf(_Person,"STATE","0");  
    };
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
    
    var dialog = JetsenWeb.extend(new JetsenWeb.UI.Window("edit-person"),{title:"编辑人员",submitBox:true,cancelBox:true,size:{width:620,height:385},maximizeBox:false,minimizeBox:false});    
    dialog.controls =["divPerson"];       
    dialog.onsubmit = function()
    {
        if(JetsenWeb.Form.Validate(areaElements,true))
        {
            var _Person = {
              ID:keyId
              ,NAME:$("txt_NAME").value
              ,USER_CODE:$("txt_CODE").value
              ,SEX:$("ddl_SEX").value
              ,DUTY_TITLE:$("txt_DUTY_TITLE").value
              ,ADDRESS:$("txt_ADDRESS").value
              ,EMAIL:$("txt_EMAIL").value
              ,OFFICE_PHONE:$("txt_OFFICE_PHONE").value
              ,HOME_PHONE:$("txt_HOME_PHONE").value
              ,MOBILE_PHONE:$("txt_MOBILE_PHONE").value
              ,JOIN_DATE:$("txt_JOIN_DATE").value                     
              ,USER_CARD:$("txt_USER_CARD").value                 
              ,DESCRIPTION:$("txt_DESCRIPTION").value
              ,STATE:$("ddl_STATE").value                
              };
            var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret)
            {
                JetsenWeb.UI.Windows.close("edit-person");
                loadPerson();
            };
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjUpdate",["UUM_PERSON",JetsenWeb.Xml.serialize(_Person,"UUM_PERSON")]);
        }
    };
    dialog.showDialog();
}