
var gCurrentNodeName;  //父结点名称
var gCurrentNodeId;    //父结点的ID

//==========初始化树菜单右键功能========================================================= 
function showClassTypeMenu(classParam){
    gCurrentNodeName = classParam.Name;
    gCurrentNodeId = classParam.ID;
    JetsenWeb.UI.PopupBehavior.hideAllControl();
    var menu = new JetsenWeb.UI.Menu("",120);
    var _subMItem0 = new JetsenWeb.UI.MenuItem("添加分类","javascript:addControlClass(1)");
    var _subMItem1 = new JetsenWeb.UI.MenuItem("修改分类","javascript:editControlClass("+classParam.ID+","+classParam.ParentId+")");
    var _subMItem2 = new JetsenWeb.UI.MenuItem("删除分类","javascript:deleteControlClass("+classParam.ID+","+classParam.ParentId+")");
    menu.addItem(_subMItem0);    
    menu.addItem(_subMItem1);   
    menu.addItem(_subMItem2);      
    if($('divTreeMenu')){
        document.body.removeChild($('divTreeMenu'));
    } 
    if(menu.menuItems.length>0){
        var menuControl = menu.render();	
        menuControl.style.display = "none";         
        menuControl.id = 'divTreeMenu';
        JetsenWeb.UI.PopupBehavior.popControl(menuControl);
    }
}

function createTree(){
    $('treeContent').innerHTML=""; 
    gClassTypeTree = new JetsenWeb.UI.Tree("class-type-tree");
    var nodes = resultXmlDoc.documentElement.childNodes;
    for(var i=0;i<nodes.length;i++){
        var node   = resultXmlDoc.documentElement.childNodes[i];
        var name   = node.selectSingleNode("CLASS_NAME").text;
        var id     = node.selectSingleNode("CLASS_ID").text;
        var pid    = node.selectSingleNode("PARENT_ID").text;
        var gCurrentNodeName  = valueOf(node.selectSingleNode("P_NAME"),"text","");
        var action = "javascript:searchSubCtrClass("+id +");";
        var item = new JetsenWeb.UI.TreeItem(name,action,id,null,{ID:id,Name:name,ParentId:pid,ParentName:gCurrentNodeName});
        item.addItem(new JetsenWeb.UI.TreeItem("","","",null,null));
        item.onopen = function(){
            this.clear();
            createSubTreeItem(this.treeParam.ID);
            this.isRenderItem = false;
            this.renderItem();
            if (this.treeItems.length ==0){
                //jetsennet.alert("无子项");
            }
        }
        item.onclick=function(){
            var temp = [];
            var title = [];
            var p = this;
            while(p){
                temp.push(p.treeText);
                p=p.parent;
            }
            title = temp.reverse();
            $("selectPath").innerText = title.join("\\").substring(1);
            if (this.treeItems.length ==0){
                //jetsennet.alert("无子项");
            }
        }
        item.oncontextmenu = function(){showClassTypeMenu(this.treeParam);};
        gClassTypeTree.addItem(item);
    }
    
    var control = gClassTypeTree.render();
    if(control!=null)
    {
        $('treeContent').appendChild(control);            
    }
}

function createSubTreeItem(id){
    searchSubCtrClass(id);
    var nodeItem = gClassTypeTree.getItem(function(item){if(item.treeParam && item.treeParam.ID==id) return true;return false},true);
    if(nodeItem)
    {
        nodeItem.clear();
        var nodes = subClassTypeDoc.documentElement.childNodes;
        for(var i=0;i<nodes.length;i++){
            var node = subClassTypeDoc.documentElement.childNodes[i];
            var name = node.selectSingleNode("CLASS_NAME").text;
            var id   = node.selectSingleNode("CLASS_ID").text;
            var action = "javascript:searchSubCtrClass("+id +");";
            var pid   = node.selectSingleNode("PARENT_ID").text;
            var gCurrentNodeName   = valueOf(node.selectSingleNode("P_NAME"),"text","");
            var item = new JetsenWeb.UI.TreeItem(name,action,id,null,{ID:id,Name:name,ParentId:pid,ParentName:gCurrentNodeName});
            item.addItem(new JetsenWeb.UI.TreeItem("","","",null,null));
            item.onopen = function(){
                this.clear();
                createSubTreeItem(this.treeParam.ID);
                this.isRenderItem = false;
                this.renderItem();
                if (this.treeItems.length ==0){
                    //jetsennet.alert("无子项");
                }
            }
            item.onclick=function(){
                var temp = [];
                var title = [];
                var p = this;
                while(p){
                    temp.push(p.treeText);
                    p=p.parent;
                }
                title = temp.reverse();
                $("selectPath").innerText = title.join("\\").substring(1);
                if (this.treeItems.length ==0){
                    //jetsennet.alert("无子项");
                }
            }
            item.oncontextmenu = function(){showClassTypeMenu(this.treeParam);};
            nodeItem.addItem(item);
        }
        nodeItem.isOpen = true;
        nodeItem.isRenderItem = false;
        nodeItem.renderItem(); 
    }
}

function deleteControlClass(id,parentId)
{
	jetsennet.confirm("确实要删除该类和该类的相关子类吗？", function () 
	{
		 var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
		    ws.soapheader = JetsenWeb.Application.authenticationHeader;
		    ws.async=false;
		    ws.oncallback = function(result)
		    {
		        if (parentId == 0){
		            searchTopClass();
		        }else{
		            createSubTreeItem(parentId);
		        }
		    }
		    ws.onerror = function(ex){ jetsennet.error(ex);};
		    ws.call("uumObjDelete",["NET_CTRLCLASS",id]);	
        
        return true;
    });	
}

function editControlClass(_id,parentId){
    $("ddlControlClassType2").disabled = true;
    //load data
    var fields = "CLASS_ID,PARENT_ID,CLASS_TYPE,CLASS_NAME,CLASS_LAYER,CLASS_DESC,VIEW_NAME,SUB_CLASSID,FIELD_1,FIELD_2,(SELECT CLASS_NAME FROM NET_CTRLCLASS WHERE CLASS_ID = T.PARENT_ID) P_NAME";
    var conditions = new JetsenWeb.SqlConditionCollection();
    conditions.SqlConditions.push(JetsenWeb.SqlCondition.create("CLASS_ID",_id,JetsenWeb.SqlLogicType.And,JetsenWeb.SqlRelationType.Equal,JetsenWeb.SqlParamType.Numeric));
    var sqlQuery = new JetsenWeb.SqlQuery();    
    JetsenWeb.extend(sqlQuery,{ResultFields:fields,IsPageResult:0,KeyId:"CLASS_ID",PageInfo:null,Conditions:conditions,QueryTable:JetsenWeb.extend(new JetsenWeb.QueryTable(),{TableName:"NET_CTRLCLASS",AliasName:"T"})});
    
    var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
    ws.soapheader = JetsenWeb.Application.authenticationHeader;
    ws.async = false;
    ws.oncallback = function(resultXml)
    {
        var objs = JetsenWeb.Xml.toObject(resultXml.resultVal,"Record");
        var nodeObj = objs[0];       
        if(nodeObj.P_NAME){
            el("txtParentName").value = valueOf(nodeObj,"P_NAME");
        }
        el("hidParentId").value= valueOf(nodeObj,"PARENT_ID","");
        el("txtClassName").value=valueOf(nodeObj,"CLASS_NAME","");
        el("txtClassViewName").value=valueOf(nodeObj,"VIEW_NAME","");
        el("txtClassLayer").value=valueOf(nodeObj,"CLASS_LAYER","");
        el("ddlControlClassType2").value = valueOf(nodeObj,"CLASS_TYPE","");
        el("txtClassDesc").value = valueOf(nodeObj,"CLASS_DESC","");
    }
    ws.onerror = function(ex){ jetsennet.error(ex);};
    ws.call("uumObjQuery",[sqlQuery.toXml()]);
    
    //update date
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog,{maximizeBox:false,minimizeBox:false,submitBox:true,cancelBox:true,size:{width:500,height:0},showScroll:false,title:"编辑分类数据"});           
    dialog.controls = ["divControlClassDetail"];
    dialog.onsubmit = function(){
        var areaElements = JetsenWeb.Form.getElements('divControlClassDetail');
        if(JetsenWeb.Form.Validate(areaElements,true))
        {
            var objCtrlword = {
                CLASS_ID: _id,
                PARENT_ID:$("hidParentId").value,
                VIEW_NAME:$("txtClassViewName").value,
                CLASS_NAME:$("txtClassName").value,
                CLASS_LAYER:$("txtClassLayer").value,
                CLASS_DESC:$("txtClassDesc").value
            };
            var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret){
                JetsenWeb.UI.Windows.close("new-object-win");
                if (parentId == 0){
                    searchTopClass();
                }else{
                    createSubTreeItem(parentId);
                }
            };
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjUpdate",["NET_CTRLCLASS",JetsenWeb.Xml.serialize(objCtrlword,"NET_CTRLCLASS")]);
        }
    }
    dialog.show();
}

function addControlClass(/*追加子级为true*/appendChild){
    var areaElements = JetsenWeb.Form.getElements('divControlClassDetail');
    JetsenWeb.Form.resetValue(areaElements);
    JetsenWeb.Form.clearValidateState(areaElements);
    $("ddlControlClassType2").disabled = false;
    
    var type = $('ddlControlClassType').value;
    var target = $('ddlControlClassType2');
    for(var i=0;i<target.options.length;i++){
        if (target.options[i].value == type){
            target.options[i].selected=true;
            break;
        }
    }
    var parentId = 0;
    if (appendChild){
         $("txtParentName").value = gCurrentNodeName;
        $("hidParentId").value = gCurrentNodeId;
        parentId = gCurrentNodeId;
    }else{
         $("txtParentName").value = "无";
         $("hidParentId").value = 0;
    }
    var dialog = new JetsenWeb.UI.Window("new-object-win");
    JetsenWeb.extend(dialog,{maximizeBox:false,minimizeBox:false,submitBox:true,cancelBox:true,size:{width:500,height:0},title:"新增受控词分类"});           
    dialog.controls = ["divControlClassDetail"];
    dialog.onsubmit = function()
    {
        if(JetsenWeb.Form.Validate(areaElements,true))
        {
            var objCtrlword = {
                PARENT_ID:$("hidParentId").value,
                CLASS_TYPE:$("ddlControlClassType2").value,
                CLASS_NAME:$("txtClassName").value,
                CLASS_LAYER:$("txtClassLayer").value,
                CLASS_DESC:$("txtClassDesc").value,
                VIEW_NAME:$("txtClassViewName").value
            };
            var ws = new JetsenWeb.Service(UUM_SYSTEM_SERVICE);
            ws.soapheader = JetsenWeb.Application.authenticationHeader;
            ws.oncallback = function(ret){
                if (parentId == 0){
                    searchTopClass();
                }else{
                    var newId = ret.resultVal;
                    var pid = $("hidParentId").value;
                    var nodeItem = gClassTypeTree.getItem(function(item){if(item.treeParam && item.treeParam.ID==pid) return true;return false},true);
                    if(nodeItem){
                        nodeItem.clear();
                        createSubTreeItem(pid);
                    }
                }
                JetsenWeb.UI.Windows.close("new-object-win");
            };
            ws.onerror = function(ex){ jetsennet.error(ex);};
            ws.call("uumObjInsert",["NET_CTRLCLASS",JetsenWeb.Xml.serialize(objCtrlword,"NET_CTRLCLASS")]);
        }
    };
    dialog.showDialog();
}