JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_tablefixed"));

var isIE = (document.all) ? true : false;

var isIE6 = isIE && (navigator.userAgent.indexOf('MSIE 6.0') != -1);
var isIE7 = isIE && (navigator.userAgent.indexOf('MSIE 7.0') != -1);
var isIE6or7 = isIE6 || isIE7;

var isChrome = navigator.userAgent.indexOf('Chrome') != -1;

JetsenWeb.TableFixed = function(otable){
	this._oTable = otable;
	this._nTable = this._oTable.cloneNode(false);
	this._nTable.id = "";
	
	this._oTableLeft = this._oTableTop = this._oTableBottom = 0;
	this._oRowTop = this._oRowBottom = 0;
	this._viewHeight = this._oTableHeight = this._nTableHeight = 0;
	this._nTableViewTop = 0;
	this._selects = [];
	this._style = this._nTable.style;
	//this._style.top = this._nTable.style.top;
	
	this._doc = isChrome ? document.body : document.documentElement;
	
	this._transparent = isChrome ? "rgba(0, 0, 0, 0)" : "transparent";
		
	this._index = 0;
		
	//this.addEventHandler(window, "resize", this.Bind(this, this.Clone));
	this.addEventHandler(this._oTable, "resize", this.Bind(this, this.Clone));
	this.addEventHandler(window, "scroll", this.Bind(this, this.Run));
	this.addEventHandler(this._oTable.parentNode, "scroll", this.Bind(this, this.Run));
	
	this._oTable.parentNode.insertBefore(this._nTable, this._oTable);
	this.Clone();
};
JetsenWeb.TableFixed.prototype = {
 
 
  Clone: function() {
  //jetsennet.alert(this.Clone.caller);	
	this._style.width = this._oTable.offsetWidth + "px";
	this._style.position = isIE6 ? "absolute" : "fixed";
	this._style.zIndex = 100;
	this._oRow = this._oTable.tBodies[0].rows[this._index];
	var oT = this._oRow, nT = oT.cloneNode(true);
	if(oT.parentNode != this._oTable){
		nT = oT.parentNode.cloneNode(false).appendChild(nT).parentNode;
	}
	
	if(this._nTable.firstChild){
		this._nTable.replaceChild(nT, this._nTable.firstChild);
	}else{
		this._nTable.appendChild(nT);
	}
	
		if(this._oTable.border > 0){
		switch (this._oTable.frame) {
			case "above" :
			case "below" :
			case "hsides" :
				this._nTable.frame = "void"; break;
			case "" :
			case "border" :
			case "box" :
				this._nTable.frame = "vsides"; break;
		}
	}
	this._style.borderTopWidth = this._style.borderBottomWidth = 0;
	
	var nTds = this._nTable.rows[0].cells;
	this.forEach(this._oRow.cells, this.Bind(this, function(o, i){
		var css = this.CurrentStyle(o), style = nTds[i].style;
		
		style.backgroundColor = this.GetBgColor(o, css.backgroundColor);
		
		style.width = (document.defaultView ? parseFloat(css.width)
			: (o.clientWidth - parseInt(css.paddingLeft) - parseInt(css.paddingRight))) + "px";
	}));
	
	this._oTableHeight = this._oTable.offsetHeight;
	this._nTableHeight = this._nTable.offsetHeight;
	
	this.SetRect();
	this.SetPos();
  },
  
  GetBgColor: function(node, bgc) {
	
	while (bgc == this._transparent && (node = node.parentNode) != document) {
		bgc = this.CurrentStyle(node).backgroundColor;
	}
	return bgc == this._transparent ? "#fff" : bgc;
  },
  
  SetRect: function() {
	if(this._oTable.getBoundingClientRect){		
		var top = this._doc.scrollTop, rect = this._oTable.getBoundingClientRect();
		this._oTableLeft = rect.left + this._doc.scrollLeft;
		this._oTableTop = rect.top + top;
		this._oTableBottom = rect.bottom + top;
		
		rect = this._oRow.getBoundingClientRect();
		this._oRowTop = rect.top + top;
		this._oRowBottom = rect.bottom + top;
		
	}else{
		
		var o = this._oTable, iLeft = o.offsetLeft, iTop = o.offsetTop;
		while (o.offsetParent) { o = o.offsetParent; iLeft += o.offsetLeft; iTop += o.offsetTop; }
		this._oTableLeft = iLeft;
		this._oTableTop = iTop;
		this._oTableBottom = iTop + this._oTableHeight;
		
		o = this._oRow; iTop = o.offsetTop;
		while (o.offsetParent) { o = o.offsetParent; iTop += o.offsetTop; }
		this._oRowTop = iTop;
		this._oRowBottom = iTop + this._oRow.offsetHeight;
	}
  },
  
  SetPos: function() {	
	this._viewHeight = document.documentElement.clientHeight;
	this._nTableViewTop = (this._viewHeight - this._nTableHeight);
	this.Run();
  },
  
  Run: function() {
		var top = this._doc.scrollTop, left = this._doc.scrollLeft
			
			,outViewTop = this._oRowTop < top, outViewBottom = this._oRowBottom > top + this._viewHeight;
		if(outViewTop || outViewBottom||(this._oTable.scrollTop<this._oTable.offsetTop)){
			var viewTop = outViewTop ? 0 : (this._viewHeight - this._nTableHeight)
				,posTop = viewTop + top;
			
			if(posTop > this._oTableTop && posTop + this._nTableHeight < this._oTableBottom){				
				    if(isIE6){
					    //this._style.top = posTop + "px";
					    this._style.left = this._oTableLeft + "px";
					    setTimeout(this.Bind(this, this.SetSelect), 0);
				    }else{
					    //this._style.top = viewTop + "px";
					    this._style.left = this._oTableLeft - left + "px";
				    }
				    this._style.top = this._nTable.style.top;
				    return;
				}
			}
	
	this._style.top = "-99999px";
	isIE6 && this.ResetSelect();
  },
  
  SetSelect: function() {
	this.ResetSelect();
	var __nTable=this._nTable;
	//var rect = __nTable.getBoundingClientRect();
	
	this._selects = this.Filter(this._oTable.getElementsByTagName("select"), this.Bind(this, function(o){
		var r = o.getBoundingClientRect();
		if(r.top <= __nTable.getBoundingClientRect().bottom && r.bottom >= __nTable.getBoundingClientRect().top){
			o._count ? o._count++ : (o._count = 1);
			
			var visi = o.style.visibility;
			if(visi != "hidden"){ o._css = visi; o.style.visibility = "hidden"; }
			
			return true;
		}
	}))
  },
  
  ResetSelect: function() {
	this.forEach(this._selects, function(o){ !--o._count && (o.style.visibility = o._css); });
	this._selects = [];
  }
};



 JetsenWeb.TableFixed.prototype.CurrentStyle = function(element){
	return element.currentStyle || document.defaultView.getComputedStyle(element, null);
};

 JetsenWeb.TableFixed.prototype.forEach = function(array, callback, thisObject){
	if(array.forEach){
		array.forEach(callback, thisObject);
	}else{
		for (var i = 0, len = array.length; i < len; i++) { callback.call(thisObject, array[i], i, array); }
	}
};

 JetsenWeb.TableFixed.prototype.Filter = function(array, callback, thisObject){
	if(array.filter){
		return array.filter(callback, thisObject);
	}else{
		var res = [];
		for (var i = 0, len = array.length; i < len; i++) { callback.call(thisObject, array[i], i, array) && res.push(array[i]); }
		return res;
	}
};

 JetsenWeb.TableFixed.prototype.Bind = function(object, fun) {
	var args = Array.prototype.slice.call(arguments).slice(2);
	return function() {
		return fun.apply(object, args.concat(Array.prototype.slice.call(arguments)));
	}
};

 JetsenWeb.TableFixed.prototype.addEventHandler=function(oTarget, sEventType, fnHandler) {
	if (oTarget.addEventListener) {
		oTarget.addEventListener(sEventType, fnHandler, false);
	} else if (oTarget.attachEvent) {
		oTarget.attachEvent("on" + sEventType, fnHandler);
	} else {
		oTarget["on" + sEventType] = fnHandler;
	}
};
