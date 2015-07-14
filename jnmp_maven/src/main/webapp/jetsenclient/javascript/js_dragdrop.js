
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_dragdrop"));
JetsenWeb.importCss("dragdrop");

JetsenWeb.UI.DragDrop=function(container,value){
    if(!container){ return; }
    var _this=this;
    this.dropObj = null;
	this.dragObj = null; 
	this.containerObj = $(container); 
	this.drops=[];
	this.dragAreas = [];
	this.dragged = false; 
	this.value=value;
	this.onLoadData=null;
	this.onChanged=null;
	this.onClose=null;
	this.arrangeDirection = 1; //0£º×óÓÒ£¬1£ºÉÏÏÂ
	
	this._moveOffsetLeft = 0;
		
    this.init=function(){
		if(_this.value != ""){
			var subcontainer = _this.value.split("|");
			for(var i=0 ; i < subcontainer.length; i++)
			{
				var subcontainerItem = subcontainer[i].split(":");
				
				if($(subcontainerItem[0]))
				{				   
				    _this.dragAreas.push(subcontainerItem[0]);
				    
					var items = subcontainerItem[1].split(",");
					for(var j=0; j< items.length; j++)
					{
					    if(items[j].length>0)
					    {
					        var newDrop=_this.createDrop(items[j]);
					        if(newDrop)
					        {
						        $(subcontainerItem[0]).appendChild(newDrop);
						        _this.drops.push(items[j]);
						    }
						}
					}
				}
			}
		}
		cleanWhitespace(_this.containerObj);		
		document.onmousemove = _this.drag;
		document.onmouseup = _this.end;		
	};
	this.createDrop=function(dropId)
	{               
        var objDiv = $(dropId);
	    if(!objDiv){return;}	    	    
	           
        var objDrag = $(dropId+"_drag");    
        objDrag = objDrag?objDrag:objDiv;
        objDrag.setAttribute("dragid",dropId);
        objDrag.style.cursor = "move";
        objDrag.onmousedown = _this.start;
        
        objDiv.setAttribute("isdrag","1");
        return objDiv;
	};
	this.loadData=function(){
	
	};
	this.start=function(e){	   
		if(!e) e = window.event;
		var obj = e.target || e.srcElement;
		var dragObj = $(obj.getAttribute("dragid"));
		if(dragObj==null){			
			return;
		}
		_this.dragged = true;
		_this.dropObj = dragObj;
		
		var moveEL = $("div-jetsen-dragdrop-move");
		var copyEL = $("div-jetsen-dragdrop-copy");
        if(moveEL==null)
        {
            moveEL = document.createElement("DIV");
            moveEL.className = "jetsen-drapdrop-move";            
            moveEL.id = "div-jetsen-dragdrop-move";
            moveEL.onselectstart = function(e) { return false; };
            moveEL.style.position = "absolute";            
            document.body.appendChild(moveEL);   
            moveEL.style.display = "none";
            moveEL.style.zIndex = 1000;           
        }
        if(copyEL==null)
        {
            copyEL = document.createElement("DIV");
            copyEL.className = "jetsen-drapdrop-copy";            
            copyEL.id = "div-jetsen-dragdrop-copy";
            copyEL.onselectstart = function(e) { return false; };              
            document.body.appendChild(copyEL);   
            copyEL.style.display = "none";           
        }
        
        JetsenWeb.Util.setClassName(moveEL,"jetsen-drapdrop-move");
        JetsenWeb.Util.setClassName(copyEL,"jetsen-drapdrop-copy");  
             
        var width = (_this.dropObj.clientWidth-10);
        var height = (_this.dropObj.clientHeight-10)
        moveEL.style.width = width + "px";
        moveEL.style.height = height + "px";
        copyEL.style.width = width + "px";
        copyEL.style.height = height + "px";
        
        _this.dropObj.parentNode.insertBefore(copyEL,_this.dropObj);
        
        if(JetsenWeb.isIE()){	        
            _this._moveOffsetLeft = e.clientX-JetsenWeb.Util.getPosition(_this.dropObj,1).left;
            _this.dropObj.setCapture();
        }
        else{
            _this._moveOffsetLeft = e.pageX-JetsenWeb.Util.getPosition(_this.dropObj,1).left;  
        }
	};
	this.drag=function(e){
	
		if(!_this.dragged||_this.dropObj==null)return;
		if(!e) e = window.event;
		
		var moveEL = $("div-jetsen-dragdrop-move");
		
		var mousePos = JetsenWeb.Util.getMousePosition ();
        var currenX = mousePos.left;
		var currenY = mousePos.top;
		currenY = currenY-10;
		
        moveEL.style.left=(currenX - _this._moveOffsetLeft)+"px";
        moveEL.style.top=currenY<0?0:currenY+"px"; 
        moveEL.style.display = "";  

        for(var j=0;j<_this.dragAreas.length;j++)
        {
            var crrentArea = $(_this.dragAreas[j]);
            if(crrentArea)
            {
                var collection = crrentArea.getElementsByTagName("DIV");
                var inAreaFlag = false;
                var currentItem = null;
                var refItem = null;
                var oLeftTop,oRightBottom;
                for(var i = 0; i < collection.length; i++)
		        {
			        var o = collection[i];
			        if(o.getAttribute("isdrag") == "1")
			        {
			            oLeftTop = JetsenWeb.Util.getPosition(o,0);
			            if(oLeftTop.left<currenX && oLeftTop.top<currenY)
			            {
			                oRightBottom = JetsenWeb.Util.getPosition(o,3);			              
			                if(oRightBottom.left>currenX && oRightBottom.top>currenY)
			                {
			                    inAreaFlag = true;
			                    currentItem = o;	
			                    refItem = o;
			                    var afterFlag = _this.arrangeDirection==1?currenY>((oRightBottom.top-oLeftTop.top)/2+oLeftTop.top) : currenX>((oRightBottom.left-oLeftTop.left)/2+oLeftTop.left);
			                    if(afterFlag)
                                {
                                    if(o.nextSibling)
                                        refItem = o.nextSibling; 
                                    else
                                        refItem = null;
                                }		                    	           
					            break;
			                }			        
			            }				
			        }
		        }
		        //areaEmpty
		        if(!inAreaFlag && collection.length==0)
		        {
		            oLeftTop = JetsenWeb.Util.getPosition(crrentArea,0);
		            if(oLeftTop.left<currenX && oLeftTop.top<currenY)
		            {
		                oRightBottom = JetsenWeb.Util.getPosition(crrentArea,3);			              
		                if(oRightBottom.left>currenX && oRightBottom.top>currenY)
		                {
		                    inAreaFlag = true;
		                    currentItem = null;	
		                    refItem = null;
		                }			        
		            }
		        }
		        
		        if(inAreaFlag)
		        {
		            var copyEL = $("div-jetsen-dragdrop-copy");
                    copyEL.style.position = "";  			            
                    if(currentItem==_this.dropObj)
                    {
                        crrentArea.insertBefore(copyEL,currentItem);
                        copyEL.style.display = "none";	
                    }
                    else
                    {
                        copyEL.style.display = "";    
                        refItem?crrentArea.insertBefore(copyEL,refItem):crrentArea.appendChild(copyEL);	                        
                    }
                    break;		
		        }
            }
        }
	};
	this.setValue=function()
	{
	    var allValues=[];
	    
	    for(var j=0; j<_this.dragAreas.length; j++)
	    {
			var crrentArea = $(_this.dragAreas[j]);
            if(crrentArea)
            {
                var areaValues = [];
                var collection = crrentArea.getElementsByTagName("DIV");
                for(var i = 0; i < collection.length; i++)
		        {
			        var o = collection[i];
			        if(o.getAttribute("isdrag") == "1")
			        {
			            areaValues.push(o.id);
			        }
			    }
			    allValues.push(_this.dragAreas[j]+":"+areaValues.join(","));
			}
		}
		_this.value=allValues.join("|");
	};
	this.end=function(e){
		if(!_this.dragged)return;		
		_this.dragged = false;
		
 		var moveEL = $("div-jetsen-dragdrop-move");
 		var copyEL = $("div-jetsen-dragdrop-copy"); 		
		
        if(JetsenWeb.isIE()){
            _this.dropObj.releaseCapture();
        }else{           
        }
        copyEL.parentNode.insertBefore(_this.dropObj,copyEL)
        
	    moveEL.style.display = "none";     
	    copyEL.style.display = "none";   
	    
	    _this.setValue();
	    //_this.timer = _this.repos(150,15);	   
		
		if(_this.onChanged&& typeof(_this.onChanged)=="function")
		{
		    _this.onChanged();
		}
	};
	this.repos=function(aa,ab){	
		var tl=getRealLeft(_this.dragObj);
		var tt=getRealTop(_this.dragObj);
		var kl=(tl-getRealLeft(_this.dropObj))/ab;
		var kt=(tt-getRealTop(_this.dropObj))/ab;
		
		return setInterval(function(){if(ab<1){
			clearInterval(_this.timer);
			_this.dragObj.parentNode.removeChild(_this.dragObj);
			_this.dropObj=null;
			return;
		}
		ab--;
		tl-=kl;
		tt-=kt;
		
		_this.dragObj.style.left=parseInt(tl)+"px";
		_this.dragObj.style.top=parseInt(tt)+"px";		
		}
	,aa/ab)
	};
	this.min=function(e){
		if(!e) e = window.event;
		var obj = e.target || e.srcElement;
		var objValue=obj.innerHTML;
		var rootObj = obj.parentNode.parentNode.parentNode;
		var id = rootObj.id;
		if(objValue=='+'){	
			rootObj.childNodes[1].style.display = '';
			obj.innerHTML="-";			
		}else{			
			rootObj.childNodes[1].style.display = 'none';
			obj.innerHTML="+";	
		}
	};
	this.close=function(e){
		if(!e) e = window.event;
		var obj = e.target || e.srcElement;
		var rootObj = obj.parentNode.parentNode.parentNode;
		var rootObjId=rootObj.id;
		rootObj.parentNode.removeChild(rootObj);
		_this.setValue();
		
		var _drops=[];
		for(var i =0; i<_this.drops.length;i++){
		    if(rootObjId!=_this.drops[i])
		    {
		        _drops.push(_this.drops[i]);
		    }
		}
		_this.drops=_drops;
		
		if(_this.onClose&& typeof(_this.onClose)=="function")
		{
		    _this.onClose();
		}
		if(_this.onChanged&& typeof(_this.onChanged)=="function")
		{
		    _this.onChanged();
		}
	};
	this.addDrop=function(did){
	    if(did.length>0&&_this.containerObj.childNodes.length>0){	    
	        for(var i=0; i<_this.drops.length; i++){
	            if(_this.drops[i]==did)
	            {
	                return;
	            }
	        } 
	        var _Drop=_this.createDrop(did);
	        _Drop.onmousedown = DragDrop.start;
	        _this.containerObj.childNodes[0].appendChild(_Drop);
	        _this.onLoadData(did);
	        _this.setValue();
	        if(_this.onChanged&& typeof(_this.onChanged)=="function")
		    {
		        _this.onChanged();
		    }
	    }
	};
	_this.init();
};
function getRealLeft(o){
	var l = 0;
	while(o){
		l += o.offsetLeft - o.scrollLeft;
		o = o.offsetParent;
	}
	return(l);
};

function getRealTop(o){
	var t = 0;
	while(o){
		t += o.offsetTop - o.scrollTop;
		o = o.offsetParent;
	}
	return(t);
};

function cleanWhitespace(node) {
	 var notWhitespace = /\S/;
	 for (var i=0; i < node.childNodes.length; i++) {
		 var childNode = node.childNodes[i];
		 if ((childNode.nodeType == 3)&&(!notWhitespace.test(childNode.nodeValue))) {
			 node.removeChild(node.childNodes[i]);
			 i--;
		 }
		 if (childNode.nodeType == 1) {
			 cleanWhitespace(childNode);
		 }
	 }
};