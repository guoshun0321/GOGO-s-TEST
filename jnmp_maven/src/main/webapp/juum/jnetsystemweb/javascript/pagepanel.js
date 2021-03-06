//激活页签
function activeContent(index)
{     
    var len = gContentList.getCount();
    var itemIndex = -1; 
    for(var i=0;i<len;i++)
    {            
        var item =gContentList.getAt(i);
        var itemControl =  $('divWindow'+item.index);
        if(itemControl)
        {
            itemControl.style.display = "none";
        }
        
        var headItem = $('ulHead').childNodes[i];
        headItem.className = "headitem-unselect";
        
        if(item.index==index)
        {   
            itemIndex = i;            
        }
    }        
    
    if(itemIndex>=0)
    {            
        var sitem =gContentList.getAt(itemIndex);
        gActiveIndex = sitem.index;
        var sitemControl =  $('divWindow'+sitem.index);
        if(sitemControl)
        {
            sitemControl.style.display = "";
        }
        gContentItem.controlId = 'divWindow'+sitem.index;
        gContentItem.render();
        
        var hitemControl = $('head-'+sitem.index);
        JetsenWeb.Util.setClassName(hitemControl,"headitem-select");        
        
        if(len>1)
        {
            if(hitemControl)
            {   
                var parentNode = $('ulHead');
                var parentNodePos = JetsenWeb.Util.getPosition(parentNode,0);
                var hitemControlPos = JetsenWeb.Util.getPosition(hitemControl,0);               
                if(hitemControlPos.top-20>parentNodePos.top)
                {
                    parentNode.insertBefore(hitemControl,parentNode.firstChild);
                }
            }
        }
        return true;
    }
    return false;
}
//关闭页签
function closeContent(index)
{
    var itemControl =  $('divWindow'+index);
    if(itemControl)
    {
        itemControl.parentNode.removeChild(itemControl);
    }
    var len = gContentList.getCount();    
    
    for(var i=0;i<len;i++)
    {   
        var headItem = $('ulHead').childNodes[i];
        if(headItem.getAttribute("index")==index)
        {
            headItem.parentNode.removeChild(headItem);
            break;
        }        
    }
    for(var i=0;i<len;i++)
    {
        if(gContentList.getAt(i).index==index)
        {  
            gContentList.removeAt(i);
            break;
        }
    } 
    if(len>1 && gActiveIndex == index)
    {
        //关闭了当前页签
        activeContent(gContentList.getAt(len-2).index);
    }   
    else if(len==1)
    {
        //关闭了所有页签
        $('JetsenMain').display = "";    
        gContentItem.controlId = "JetsenMain";       
        gContentItem.render();
    }
}
function closeAllContent()
{
    var len = gContentList.getCount(); 
    $('ulHead').innerHTML = "";
    for(var i=0;i<len;i++)
    {
        var itemControl =  $('divWindow'+gContentList.getAt(i).index);
        if(itemControl)
        {
            itemControl.parentNode.removeChild(itemControl);
        }
    }
    gContentList.clear();
    
    $('JetsenMain').display = "";    
    gContentItem.controlId = "JetsenMain";       
    gContentItem.render();
       
}
//显示页签
function showContent(index,url,title)
{
    var exist = activeContent(index);
    if(exist)
        return;
        
    var newItem = $('JetsenMain').cloneNode(false);
    
    $('JetsenMain').display = "none";    
    $('divFrameContent').appendChild(newItem);
    newItem.id = "divWindow"+index;
    gContentItem.controlId = "divWindow"+index;
    newItem.src = url;
    gContentItem.render();
    
    var headItem = document.createElement("LI");
    headItem.className = "headitem-select";
    headItem.style.position = "relative";
    headItem.id = "head-"+index;
    headItem.onclick = JetsenWeb.bindFunction(headItem,activeContent,index);
    headItem.innerHTML = title+"<img style='position:absolute;right:2px;top:2px' src='images/close.gif' onclick=\"closeContent('"+index+"')\" />";
    headItem.setAttribute("index",index);    
    
    var parentNode = $('ulHead');
    parentNode.appendChild(headItem);
     
    if(gContentList.getCount()>1)
    {
        var parentNodePos = JetsenWeb.Util.getPosition(parentNode,0);
        var hitemControlPos = JetsenWeb.Util.getPosition(headItem,0);               
        if(hitemControlPos.top-20>parentNodePos.top)
        {
            parentNode.insertBefore(headItem,parentNode.firstChild);
        }
    }
    
    gActiveIndex = index;
    gContentList.add({index:index,pageUrl:url,title:title});
}
//覆盖default.js的方法
function addFunctionItems(tree,items)
{  
    if(tree && items)
    {  
        for(var i=0;i<items.length;i++)
        {            
            var subItem = new JetsenWeb.UI.TreeItem(items[i].itemName,null,null,null,{pageUrl:items[i].itemAction,index:items[i].id,title:items[i].itemName});              
            subItem.onclick = function(){ 
                if(this.treeParam && this.treeParam.pageUrl)
                {
                    showContent(this.treeParam.index,this.treeParam.pageUrl,this.treeParam.title);
                }
            };
            subItem.fileIcon = "images/tree-doc.gif"; 
            subItem.closeIcon = "images/tree-close.gif";
            subItem.openIcon = "images/tree-open.gif";
            tree.addItem(subItem);  
            
            if(items[i].items && items[i].items.length>0)
            {
                addFunctionItems(subItem,items[i].items);
            }                                              
        }
    }     
}
//页签菜单
function showPanelMenu(obj)
{
    var len = gContentList.getCount();
    if(len<=1)
        return;
        
    JetsenWeb.UI.PopupBehavior.hideControl($('menu-panel'));    
    var menu = new JetsenWeb.UI.Menu("menu-panel",150);
    menu.addItem(new JetsenWeb.UI.MenuItem("关闭所有选项卡","javascript:closeAllContent();"));
    //menu.addItem(new JetsenWeb.UI.MenuItem("关闭其它选项卡","javascript:closeOtherContent();"));
    menu.addItem(new JetsenWeb.UI.MenuItem("关闭当前选项卡","javascript:closeContent(\""+gActiveIndex+"\");"));
    menu.addItem(new JetsenWeb.UI.MenuSplit());
        
    for(var i=0;i<len;i++)
    {
        var item = gContentList.getAt(i);
        menu.addItem(new JetsenWeb.UI.MenuItem(item.title,"javascript:activeContent(\""+item.index+"\");"));
    }
    
    var menuControl = menu.render();
    menu = null;    
    menuControl.style.display = "none";
    if($('menu-panel')!=null)
    {
       document.body.removeChild($('menu-panel'));
    } 
    menuControl.id = 'menu-panel';
    JetsenWeb.UI.PopupBehavior.popControl($('menu-panel'),obj,1);
}