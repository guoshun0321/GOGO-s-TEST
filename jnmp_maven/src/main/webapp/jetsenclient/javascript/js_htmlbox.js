
// lixiaomin 2008/09/03
//=============================================================================
// HTML编辑器
//=============================================================================

JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_htmlbox"));

JetsenWeb.importCss("htmlbox");

JetsenWeb.UI.HtmlBoxs = {};

JetsenWeb.UI.HtmlBox = function(/*el*/parent,/*string*/htmlBoxName)
{
    this.htmlBoxName = htmlBoxName?htmlBoxName:"HtmlBox";
    this.parent = parent?parent:document.body;
    this.previewWindow = null;
    this.isIE = JetsenWeb.isIE();
    this.initValue = "";
    
    this.designEditor = null;
    this.htmlEditor = null;
    
    this.supportPath = JetsenWeb.baseThemeUrl +"htmlbox";	
	this.uploadFilePath = "";
	this.initialized = false;
	this.undoArray = new Array();
	this.undoArrayMax = 16;
	this.undoArrayPos = -1;
	this.lastEvent = null;	
	this.pasteMode = 2; //pasteMode: 0:disable; 1:text; 2:default
	this.hasFocus = false;
	this.currentControl = null;
	
	this.buttons = [];	
	this.dropDownLists = [];
	this.themes = [];
	this.scripts = [];
	this.scriptText = "";
	this.styleText = "";
		
    this.onmouseup = null;
    this.onclick = null;
	this.ondblclick = null;
	this.oncontextmenu = null;
    
    JetsenWeb.UI.HtmlBoxs[this.htmlBoxName] = this;
}

JetsenWeb.UI.HtmlBox.prototype.render = function()
{
    var owner = this;
      
    //toolbox
    var toolboxContainer = $(this.htmlBoxName + "_toolbox");
    if(toolboxContainer==null)
    {
        toolboxContainer = document.createElement("DIV");
        toolboxContainer.id = this.htmlBoxName + "_toolbox";
        document.body.appendChild(toolboxContainer);
        this.parent.appendChild(toolboxContainer);
    }
    
	for(var i=0; i<this.dropDownLists.length; i++)
	{
		var dropdownlist = this.dropDownLists[i];
		dropdownlist.htbox = this;
		toolboxContainer.appendChild(dropdownlist.control);
	}		
	
	this.buttons = (this.buttons==null || this.buttons.length==0)?JetsenWeb.UI.HtmlBoxButtons:this.buttons;
	for(var i=0; i<this.buttons.length; i++)
	{
		var button = this.buttons[i];
		button.htbox = this;
		var imgBtn = document.createElement("IMG");
		imgBtn.src = button.imageSrc; 
		button.control = imgBtn;
		imgBtn.title = button.tooltip;
		button.render();
		toolboxContainer.appendChild(button.control);
	}
	
	//html editor
    var htmlControl = $(this.htmlBoxName);
    if(htmlControl==null)
    {
        htmlControl = document.createElement("TEXTAREA");        
        htmlControl.id = this.htmlBoxName;
        htmlControl.name = this.htmlBoxName;    
        htmlControl.style.height = (this.parent.offsetHeight-50)+"px";
        htmlControl.style.width = (this.parent.offsetWidth)+"px";
        htmlControl.style.display = "none";
        document.body.appendChild(htmlControl);
        this.parent.appendChild(htmlControl); 
    }
    this.htmlEditor = htmlControl;
    
    //frame
    var frameControl = $(this.htmlBoxName + "_designEditor");
    if(frameControl==null)
    {
        frameControl = document.createElement("IFRAME");
        frameControl.style.height = (this.parent.offsetHeight-50)+"px";
        frameControl.style.width = (this.parent.offsetWidth-10)+"px";
        frameControl.id = this.htmlBoxName + "_designEditor";
        
        frameControl.frameBorder= "0px";
        frameControl.marginHeight = "0px";    
        frameControl.marginWidth = "1px";
        frameControl.style.border = "1px";
        frameControl.style.margin = "0px";
        frameControl.style.borderColor = "#000000";
        frameControl.style.borderStyle = "solid";
        document.body.appendChild(frameControl);
        this.parent.appendChild(frameControl);	 
    }
	
	if (this.isIE)
	{		
		this.designEditor = eval(this.htmlBoxName + "_designEditor");
		this.designEditor.htbox = this;
		this.designEditor.document.htbox = this;
		$(this.htmlBoxName+ "_designEditor").document.htbox = this;
	} 
	else 
	{		
		this.designEditor = $(this.htmlBoxName + "_designEditor").contentWindow;
		this.designEditor.document.htbox = this;		
	}		
	
	var viewbuttonsContainer = $(this.htmlBoxName + "_viewbutton");
	if(viewbuttonsContainer==null)
	{
	    viewbuttonsContainer = document.createElement("DIV");  
	    viewbuttonsContainer.id = this.htmlBoxName + "_viewbutton";
	    document.body.appendChild(viewbuttonsContainer);  
	    this.parent.appendChild(viewbuttonsContainer);
	}	
    
    this.editorButton = document.createElement("INPUT");
    this.editorButton.type = "button";
    this.editorButton.value = "设计";
    this.editorButton.className = "button";
    this.editorButton.onclick = function()
    {
        this.showEditView();
    };
    this.editorButton.disabled = true;
    
    this.htmlButton = document.createElement("INPUT");
    this.htmlButton.type = "button";
    this.htmlButton.value = "源文件";
    this.htmlButton.className = "button";
    this.htmlButton.onclick = function(){
        owner.showSourceCode();
    };       
     
    viewbuttonsContainer.appendChild(this.editorButton);     
    viewbuttonsContainer.appendChild(this.htmlButton);
        
    this.designEditor.document.designMode = 'on';
	//this.designEditor.contentEditable = "true";
	
	
	var strThemes = "";
	for(var i=0;i<this.themes.length;i++)
	{
	    strThemes += "<link  media=\"all\" type=\"text/css\" rel=\"stylesheet\" href=\""+this.themes[i]+"\" />";
	}
	var strScripts = "";
	for(var i=0;i<this.scripts.length;i++)
	{
	   strScripts += "<script language=\"javascript\" src=\""+this.scripts[i]+"\"></script>";
	}
		
	this.designEditor.document.open();	
	var content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>"+
	//var content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\">" + 	
			"<head>" +strThemes+strScripts+"<script language=\"javascript\">"+this.scriptText+"</script><style>"+this.styleText+"</style></head>" + 
			"<body>" + 
				this.initValue + 
			"</body>" + 
		"</html>";
	this.designEditor.document.write(content);
	this.designEditor.document.close();
	this.designEditor.document.body.style.imeMode = "disabled";
	
	
	JetsenWeb.addEvents(this.designEditor.document,["keypress","mousedown"],function(e) { owner.hasFocus=true; return owner.htmlEditorEvent(e); } );
	JetsenWeb.addEvents(this.designEditor.document,["keydown"],function(e) { owner.hasFocus=true;var ret = true; if(owner.onkeydown) ret=owner.onkeydown(e);if(!ret) return false;return owner.htmlEditorEvent(e); } );
	JetsenWeb.addEvents(this.designEditor.document,"click",function(e) { owner.hasFocus=true; if(owner.onclick) owner.onclick(e); }  );
	JetsenWeb.addEvents(this.designEditor.document,"dblclick",function(e) { owner.hasFocus=true; if(owner.ondblclick) owner.ondblclick(e); } );
	JetsenWeb.addEvents(this.designEditor.document,"mouseup",function(e) { owner.hasFocus=true; if(owner.onmouseup) owner.onmouseup(e); }  );
	JetsenWeb.addEvents(this.designEditor.document,"contextmenu",function(e) { owner.hasFocus=true; if(owner.oncontextmenu) owner.oncontextmenu(e); return false;} );
	this.designEditor.document.body.oncontextmenu = function(){return false;};			
	this.undoArray[0] = this.htmlEditor.value;
	window.setInterval(function(){owner.recordUndoStep();},500);
	this.initialized = true;
	this.focus();
}
JetsenWeb.UI.HtmlBox.prototype.showEditView = function () {
    this.htmlEditor.style.display = "none";
    $(this.htmlBoxName + "_designEditor").style.display = "";
    this.designEditor.document.body.innerHTML = this.htmlEditor.value;
    this.editorButton.disabled = true;
    this.htmlButton.disabled = false;
};

JetsenWeb.UI.HtmlBox.prototype.showSourceCode = function () {
    this.htmlEditor.style.display = ""; 
    $(this.htmlBoxName + "_designEditor").style.display = "none";
    this.editorButton.disabled = false;
    this.htmlButton.disabled = true;
    this.storeHtml();
};

JetsenWeb.UI.HtmlBox.prototype.addButton = function(con)
{
    this.buttons.push(con);
};

JetsenWeb.UI.HtmlBox.prototype.addTheme = function(url)
{
    this.themes.push(url);
};

JetsenWeb.UI.HtmlBox.prototype.addDropDownList = function(con)
{
    this.dropDownLists.push(con);
};

JetsenWeb.UI.HtmlBox.prototype.htmlEditorEvent = function(ev) {    
    
 	var _TAB = 9;	var _ENTER = 13;	var _QUOTE = 222;	var _OPENCURLY = '&#8220;';	var _CLOSECURLY = '&#8221;';
 	if(ev!=null)
 	{
	    if (this.isIE) {	
	        if (ev.ctrlKey && ev.keyCode == 90) {
 				this.undo();
				this.cancelEvent(ev);		
 			} else if (ev.ctrlKey && ev.keyCode == 89) { 			
 				this.redo(); 
				this.cancelEvent();
 			} else if (ev.keyCode == _TAB) {	
		        this.insertHtml("&nbsp;&nbsp;&nbsp;");
			    this.cancelEvent(ev);
		    }
		    // IE defaults to <p>, Mozilla to <br>		
		    if (ev.keyCode == _ENTER) {	
				    var sel = this.getSelection();
				    if (sel.type == 'Control') {	
				        return;		
				    }					
				    var r = sel.createRange();
				    if ((!this.checkTag(r.parentElement(),'LI'))&&(!this.checkTag(r.parentElement(),'H'))) {
					    r.pasteHTML('<br>');
					    this.cancelEvent(ev);
					    r.select();	
					    r.collapse(false);	
					    return false;
					}
		    }		
	    }	
	}	
};

JetsenWeb.UI.HtmlBox.prototype.cancelEvent = function(ev) 
{
	if (this.isIE) {
		ev.cancelBubble = true;
		ev.returnValue = false;
	} else {
		ev.preventDefault();
		ev.stopPropagation();
	}
};

JetsenWeb.UI.HtmlBox.prototype.recordUndoStep = function()
{
	if (!this.initialized) return;
	++this.undoArrayPos;
	if (this.undoArrayPos >= this.undoArrayMax) {
		// remove the first element
		this.undoArray.shift();
		--this.undoArrayPos;
	}
	
	var take = true;
	var html = this.designEditor.document.body.innerHTML;
	if (this.undoArrayPos > 0)
		take = (this.undoArray[this.undoArrayPos - 1] != html);
	if (take) {
		this.undoArray[this.undoArrayPos] = html;
	} else {
		this.undoArrayPos--;
	}
};

JetsenWeb.UI.HtmlBox.prototype.undo = function()
{
	if (this.undoArrayPos > 0) {
		var html = this.undoArray[--this.undoArrayPos];
		if (html)
			this.designEditor.document.body.innerHTML = html;
		else 
			++this.undoArrayPos;
	}
};

JetsenWeb.UI.HtmlBox.prototype.canUndo = function()
{
	return true;
	return (this.undoArrayPos > 0);
};

JetsenWeb.UI.HtmlBox.prototype.redo = function()
{
	if (this.undoArrayPos < this.undoArray.length - 1) {
		var html = this.undoArray[++this.undoArrayPos];
		if (html) 
			this.designEditor.document.body.innerHTML = html;
		else 
			--this.undoArrayPos;
	}
	
};

JetsenWeb.UI.HtmlBox.prototype.canRedo = function()
{
	return true;
	return (this.undoArrayPos < this.undoArray.length - 1);
};

JetsenWeb.UI.HtmlBox.prototype.capturePaste = function()
{
 	switch (this.pasteMode) {
 		case 1:
 			return false;
 		case 2:
 			if (window.clipboardData) {
				var text = window.clipboardData.getData('Text');
				if(!text)
				    return;
				text = text.replace(/<[^>]*>/gi,'');
				this.insertHtml(text);
			} else {
				jetsennet.alert("您的浏览器不支持粘贴此格式.");
			}
			return false; 				
 		default:
 		case 0:
			try {
				this.executeCommand('paste'); 
			} catch (e) {
				jetsennet.alert('您的安全设置不允许使用些命令. 更新信息请访问 http://www.mozilla.org/editor/midasdemo/securityprefs.html.');
			}	
 			return true;
 	}		
};

JetsenWeb.UI.HtmlBox.prototype.paste  = function(){  this.capturePaste();};

JetsenWeb.UI.HtmlBox.prototype.cut    = function(){ try {this.executeCommand('cut'); } catch (e) {jetsennet.alert('您的安全设置不允许使用些命令. 更新信息请访问 http://www.mozilla.org/editor/midasdemo/securityprefs.html.');}};

JetsenWeb.UI.HtmlBox.prototype.copy   = function(){ try {this.executeCommand('copy');} catch (e) {jetsennet.alert('您的安全设置不允许使用些命令. 更新信息请访问 http://www.mozilla.org/editor/midasdemo/securityprefs.html.');}};

JetsenWeb.UI.HtmlBox.resetV



JetsenWeb.UI.HtmlBox.prototype.deleteContents = function()
{     
	jetsennet.confirm("确定删除编辑的所有内容？", function () 
    {	
        this.designEditor.document.body.innerHTML = '';
        if (this.isIE) {	this.designEditor.document.body.innerText = '';}
        return true;
    }); 
    this.focus();
};

JetsenWeb.UI.HtmlBox.prototype.copyDesignToHtml = function()
{
    if (!this.initialized) return;
	 
	if (this.isIE) {
		//if (Frame_IsHtmlMode(ftbName)) {hiddenHtml.value = editor.document.body.innerText;} else {
			this.htmlEditor.value = this.designEditor.document.body.innerHTML;  
		//}		
	} else {
		//if (Frame_IsHtmlMode(ftbName)) {editorContent = editor.document.body.ownerDocument.createRange();editorContent.selectNodeContents(editor.document.body);hiddenHtml.value = editorContent.toString();} else {
			this.htmlEditor.value = this.designEditor.document.body.innerHTML;  
		//}	
	}
	if (this.htmlEditor.value == '<P>&nbsp;</P>' || this.htmlEditor.value == '<br>') {this.htmlEditor.value = '';}
};

JetsenWeb.UI.HtmlBox.prototype.executeCommand = function(commandName, middle, commandValue) {
	this.focus();
	if (commandName == 'backcolor' && !this.isIE) commandName = 'hilitecolor';
	
	if (!this.isIE) {
		this.designEditor.document.execCommand("useCSS",null,true);
	}	
	this.designEditor.document.execCommand(commandName,middle,commandValue);	
};

JetsenWeb.UI.HtmlBox.prototype.insertElement = function(el) {
	var sel = this.GetSelection();
	var range = this.CreateRange(sel);
	
	if (this.isIE) {
		range.pasteHTML(el.outerHTML);
	} else {
		this.InsertNodeAtSelection(el);
	}
};

JetsenWeb.UI.HtmlBox.prototype.insertHtml = function(html)
{	
	this.focus();
	if (this.isIE) {
		var sel = this.designEditor.document.selection.createRange();
		sel.pasteHTML(html);
	} else {

        var selection = this.designEditor.window.getSelection();
		if (selection) {
			range = selection.getRangeAt(0);
		} else {
			range = this.focus();this.designEditor.document.createRange();
		}

        var fragment = this.designEditor.document.createDocumentFragment();
        var div = this.designEditor.document.createElement("div");
        div.innerHTML = html;

        while (div.firstChild) {
            fragment.appendChild(div.firstChild);
        }

        selection.removeAllRanges();
        range.deleteContents();

        var node = range.startContainer;
        var pos = range.startOffset;

        switch (node.nodeType) {
            case 3:
                if (fragment.nodeType == 3) {
                    node.insertData(pos, fragment.data);
                    range.setEnd(node, pos + fragment.length);
                    range.setStart(node, pos + fragment.length);
                } else {
                    node = node.splitText(pos);
                    node.parentNode.insertBefore(fragment, node);
                    range.setEnd(node, pos + fragment.length);
                    range.setStart(node, pos + fragment.length);
                }
                break;

            case 1:
                node = node.childNodes[pos];
                node.parentNode.insertBefore(fragment, node);
                range.setEnd(node, pos + fragment.length);
                range.setStart(node, pos + fragment.length);
                break;
        }
        selection.addRange(range);
	}
};

JetsenWeb.UI.HtmlBox.prototype.focus = function()
{	
	this.designEditor.focus();		
	this.hasFocus = true;
};
JetsenWeb.UI.HtmlBox.prototype.getHtml = function()
{
	this.copyDesignToHtml();		
	return this.htmlEditor.value;
};
JetsenWeb.UI.HtmlBox.prototype.setHtml = function(html)
{
	this.htmlEditor.value = html;
	this.designEditor.document.body.innerHTML = html;
};
JetsenWeb.UI.HtmlBox.prototype.storeHtml = function()
{
	if (!this.initialized) return;	
	this.copyDesignToHtml();		
	return true;
};

JetsenWeb.UI.HtmlBox.prototype.print = function()
{
	if (this.isIE) {
		this.executeCommand('print'); 
	} else {
		this.designEditor.print();
	}		
};

JetsenWeb.UI.HtmlBox.prototype.preview = function()
{
    var owner = this;  
	
	this.copyDesignToHtml();	    
    bodyStr = this.htmlEditor.value;    
   
    if(this.previewWindow!=null)		
        this.previewWindow.close();	
        
    var strThemes = "";
	for(var i=0;i<this.themes.length;i++)
	{
	    strThemes += "<link type=\"text/css\" rel=\"stylesheet\" href=\""+this.themes[i]+"\" />";
	}
	var strScripts = "";
	for(var i=0;i<this.scripts.length;i++)
	{
	   strScripts += "<script language=\"javascript\" src=\""+this.scripts[i]+"\"></script>";
	}
	
    this.previewWindow = window.open("", "Preview","status=no,resizeable=yes,toolbar=no,scrollbars=yes,width=800,height=600");	
	this.previewWindow.document.write("<html><head><title>预览</title>"+strThemes+strScripts+"</head><body>");
	this.previewWindow.document.write(bodyStr);	
	this.previewWindow.document.write("</body></html>");	
	this.previewWindow.document.close();
}

JetsenWeb.UI.HtmlBox.prototype.selectNextNode =function(el)
{
	var node = el.nextSibling;
	while (node && node.nodeType != 1) {node = node.nextSibling;}
	if (!node) {node = el.previousSibling;	while (node && node.nodeType != 1) {node = node.previousSibling;}	}
	if (!node) {node = el.parentNode;}//editor.selectNodeContents(node);
};

JetsenWeb.UI.HtmlBox.prototype.getClosest = function(tagName,el)
{	
	var ancestors = this.getAllAncestors(el);
	var ret = null;tagName = ("" + tagName).toLowerCase();	
	for (var i in ancestors) {	
	    var el = ancestors[i];
	    if (el.tagName.toLowerCase() == tagName) {	ret = el;	break;	}	
	}
	return ret;
};
	
JetsenWeb.UI.HtmlBox.prototype.getAllAncestors = function(el)
{	
    var p = el?el:this.getParentElement();
    var a = [];
	while (p && (p.nodeType == 1) && (p.tagName.toLowerCase() != 'body')) {	a.push(p);	p = p.parentNode;}
	a.push(this.designEditor.document.body);	
	return a;
};

JetsenWeb.UI.HtmlBox.prototype.getParentElement = function()
{	
	var sel = this.getSelection();
	var range = this.createRange(sel);
	if (JetsenWeb.isIE()) {
		switch (sel.type) {
		    case "Text":
		    case "None":
				// It seems that even for selection of type "None",
				// there _is_ a parent element and it's value is not
				// only correct, but very important to us.  MSIE is
				// certainly the buggiest browser in the world and I
				// wonder, God, how can Earth stand it?
				return range.parentElement();
		    case "Control":
				return range.item(0);
		    default:
				return this.designEditor.document.body;
		}
	} else try {
		var p = range.commonAncestorContainer;
		if (!range.collapsed && range.startContainer == range.endContainer &&
		    range.startOffset - range.endOffset <= 1 && range.startContainer.hasChildNodes())
			p = range.startContainer.childNodes[range.startOffset];
		/*
		jetsennet.alert(range.startContainer + ":" + range.startOffset + "\n" +
		      range.endContainer + ":" + range.endOffset);
		*/
		while (p.nodeType == 3) {
			p = p.parentNode;
		}
		return p;
	} catch (e) {
		return null;
	}
};

JetsenWeb.UI.HtmlBox.prototype.insertNodeAtSelection = function(toBeInserted)
{
	if (!JetsenWeb.isIE()) {		
		var sel = this.getSelection();
		var range = this.createRange(sel);
		// remove the current selection
		sel.removeAllRanges();
		range.deleteContents();
		var node = range.startContainer;
		var pos = range.startOffset;
		switch (node.nodeType) {
		    case 3: // Node.TEXT_NODE
			// we have to split it at the caret position.
			if (toBeInserted.nodeType == 3) {
				// do optimized insertion
				node.insertData(pos, toBeInserted.data);
				range = this._createRange();
				range.setEnd(node, pos + toBeInserted.length);
				range.setStart(node, pos + toBeInserted.length);
				sel.addRange(range);
			} else {
				node = node.splitText(pos);
				var selnode = toBeInserted;
				if (toBeInserted.nodeType == 11 /* Node.DOCUMENT_FRAGMENT_NODE */) {
					selnode = selnode.firstChild;
				}
				node.parentNode.insertBefore(toBeInserted, node);
				this.designEditor.selectNodeContents(selnode);
			}
			break;
		    case 1: // Node.ELEMENT_NODE
			var selnode = toBeInserted;
			if (toBeInserted.nodeType == 11 /* Node.DOCUMENT_FRAGMENT_NODE */) {
				selnode = selnode.firstChild;
			}
			node.insertBefore(toBeInserted, node.childNodes[pos]);
			this.selectNodeContents(this.designEditor,selnode);
			break;
		}
	}
};
// Selects the contents inside the given node
JetsenWeb.UI.HtmlBox.prototype.selectNodeContents = function( node, pos) 
{	
	var range;var collapsed = (typeof pos != "undefined");
	if (JetsenWeb.isIE()) 
	{	
	    range = this.designEditor.document.body.createTextRange();	range.moveToElementText(node);	(collapsed) && range.collapse(pos);	range.select();
	} 
	else {
	    var sel = this.getSelection();	
	    range = this.designEditor.document.createRange();	range.selectNodeContents(node);	(collapsed) && range.collapse(pos);	sel.removeAllRanges();	sel.addRange(range);
	}
};
// returns the current selection object
JetsenWeb.UI.HtmlBox.prototype.getSelection = function() 
{ 
    if (JetsenWeb.isIE()) {	return this.designEditor.document.selection;} else {return this.designEditor.getSelection();	}
};
JetsenWeb.UI.HtmlBox.prototype.selectParentNode = function()
{    	
    var pNode = this.getParentElement();
    if(pNode!=null)
    {
        pNode = pNode.parentNode;
        if(pNode!=null)
        {
            var oControlRange = pNode.createControlRange();   
            oControlRange.select();
        }
    } 
};
// returns a range for the current selection
JetsenWeb.UI.HtmlBox.prototype.createRange = function(sel) 
{  
	if (JetsenWeb.isIE()) {	return sel.createRange();} 
	else 
	{	//TODO: this.focusEditor();
		if (typeof sel != "undefined") 
		{
		    try {return sel.getRangeAt(0);} catch(e) {	return this.designEditor.document.createRange();}
		} 
		else {return this.designEditor.document.createRange();}
	}
};

JetsenWeb.UI.HtmlBox.prototype.checkTag = function(item,tagName)
{
    if (item.tagName.search(tagName)!=-1) {	return item;	}	
    if (item.tagName=='BODY') {		return false;	}	
    item=item.parentElement;	
    return this.checkTag(item,tagName);
};

JetsenWeb.UI.HtmlBox.prototype.removeFormat = function(){this.executeCommand('removeformat');};

JetsenWeb.UI.HtmlBox.prototype.formatItalic = function(){this.executeCommand('italic');};

JetsenWeb.UI.HtmlBox.prototype.formatBold = function(){ this.executeCommand('bold');};

JetsenWeb.UI.HtmlBox.prototype.formatUnderline = function(){ this.executeCommand('underline');};

JetsenWeb.UI.HtmlBox.prototype.formatJustifyRight = function(){this.executeCommand('justifyright');}

JetsenWeb.UI.HtmlBox.prototype.formatJustifyCenter = function(){this.executeCommand('justifycenter');}

JetsenWeb.UI.HtmlBox.prototype.formatJustifyLeft = function(){this.executeCommand('justifyleft');}

JetsenWeb.UI.HtmlBox.prototype.formatJustifyFull = function(){this.executeCommand('justifyfull');}

JetsenWeb.UI.HtmlBox.prototype.formatNumberedList = function(){ this.executeCommand('insertorderedlist');};

JetsenWeb.UI.HtmlBox.prototype.formatBulletedList = function(){ this.executeCommand('insertunorderedlist');};

JetsenWeb.UI.HtmlBox.prototype.formatOutdent = function(){ this.executeCommand('outdent');};

JetsenWeb.UI.HtmlBox.prototype.formatIndent = function(){ this.executeCommand('indent');};

JetsenWeb.UI.HtmlBox.prototype.formatStrikeThrough = function(){ this.executeCommand('strikethrough');};

JetsenWeb.UI.HtmlBox.prototype.formatSubScript = function(){ this.executeCommand('subscript');};

JetsenWeb.UI.HtmlBox.prototype.formatSuperScript = function(){ this.executeCommand('superscript');};

JetsenWeb.UI.HtmlBox.prototype.formatUnLink = function(){ this.executeCommand('unlink');};

JetsenWeb.UI.HtmlBox.prototype.createLink = function()
{  
    this.focus();	
    var taga = this.getClosest("a");
    var winLink = window.showModalDialog(this.supportPath+"/insertlink.htm",taga,"dialogwidth:400px;dialogheight:180px;status:no");
    
    if(winLink!=null){if(taga)taga.parentNode.removeChild(taga);this.insertHtml(winLink);}
        return;
        
    if (JetsenWeb.isIE()) 
    {
        this.designEditor.document.execCommand('createlink','1',null);	
    } 
    else 
    {
        var url = prompt('Enter a URL:', 'http://');
        if ((url != null) && (url != ''))  this.designEditor.document.execCommand('createlink',false,url);	
    }
};

JetsenWeb.UI.HtmlBox.prototype.createAnchor = function(){ 
   
    this.focus();
    var _name = prompt('Enter a Anchor Name:', '');
    if ((_name != null) && (_name != '')){ this.insertHtml("<a name='#"+_name+"' ></a>");}
};

JetsenWeb.UI.HtmlBox.prototype.insertDate = function(){    var d = new Date();	 this.insertHtml(d.toDateString());};

JetsenWeb.UI.HtmlBox.prototype.insertTime = function(){    var d = new Date(); this.insertHtml(d.toLocaleTimeString());};

JetsenWeb.UI.HtmlBox.prototype.insertRule = function(){    this.executeCommand('inserthorizontalrule');};

JetsenWeb.UI.HtmlBox.prototype.insertImage = function() {/*if (Frame_IsHtmlMode(ftbName)) return;*/	
  
   var winImage = window.showModalDialog(this.supportPath+"/insertimage.aspx?path="+escape(this.uploadpath),"","dialogwidth:580px;dialogheight:600px;status:no");	
   if(winImage!=null)	{ this.insertHtml(winImage);	}
};

JetsenWeb.UI.HtmlBox.prototype.insertImage2 = function()
{
	var url = prompt('Enter a ImageURL:', 'http://');
	if ((url != null) && (url != '')) {var reg = /^\s*([Hh][tT][tT][pP]:\/\/)([\w-]+\.)+[\w-]+([:\w- .\/?%&=]*)?\s*$/;	
	if(url.match(reg))  
	    this.insertHtml("<img onmousewheel='return ReSizeImg(this);' border=0 src='"+url+"' >"); 
	else jetsennet.alert('The  URL of image that is not valided!');}
};

JetsenWeb.UI.HtmlBox.prototype.insertChar = function()
{
    this.focus();
    var winChar = window.showModalDialog(this.supportPath+"/insertchars.htm","","dialogwidth:500px;dialogheight:360px;status:no");	
    if(winChar!=null)	
        this.insertHtml(winChar);	
};

JetsenWeb.UI.HtmlBox.prototype.insertEmotion = function()
{	
    this.focus();
    var winChar = window.showModalDialog(this.supportPath+"/insertemotion.htm","","dialogwidth:400px;dialogheight:360px;status:no");
    if(winChar!=null)	
        this.insertHtml(winChar);	
};

JetsenWeb.UI.HtmlBox.prototype.setBackColor = function()
{	
    var owner = this;
    if(!IS_IE)
    {      
        JetsenWeb.require("js_colorpicker");
        JetsenWeb.UI.ColorPicker.pickColor(JetsenWeb.getEvent().srcElement,function(color)
        {
            owner.focus();
            owner.designEditor.document.execCommand('backcolor','',color);	
        });
    }
    else
    {
        this.focus();
        var winColor = window.showModalDialog(this.supportPath+"/insertcolor.htm","","dialogwidth:260px;dialogheight:230px;status:no");	
        if(winColor!=null){
            this.designEditor.document.execCommand('backcolor','',winColor);	
        }
    }
};

JetsenWeb.UI.HtmlBox.prototype.setFontFace = function(name,value)
{
    this.focus();
    this.designEditor.document.execCommand('fontname','',value);
};

JetsenWeb.UI.HtmlBox.prototype.setForeColor = function()
{	
    var owner = this;
    if(!IS_IE)
    {      
        JetsenWeb.require("js_colorpicker");
        JetsenWeb.UI.ColorPicker.pickColor(JetsenWeb.getEvent().srcElement,function(color)
        {
            owner.focus();
            owner.designEditor.document.execCommand('forecolor','',color);	
        });
    }
    else
    {
        this.focus();
        var winColor = window.showModalDialog(this.supportPath+"/insertcolor.htm","","dialogwidth:260px;dialogheight:230px;status:no");	
        if(winColor!=null){
            this.designEditor.document.execCommand('forecolor','',winColor);	
        }
    }
};

JetsenWeb.UI.HtmlBox.prototype.setFontSize = function(name,value)
{
    this.focus();
    this.designEditor.document.execCommand('fontsize','',value);
};

JetsenWeb.UI.HtmlBox.prototype.setParagraph = function(name,value)
{
    this.focus();
    if (value == '<body>') {
        this.designEditor.document.execCommand('formatBlock','','Normal');
        this.designEditor.document.execCommand('removeFormat');
        return;
    }
    this.designEditor.document.execCommand('formatBlock','',value);
};

JetsenWeb.UI.HtmlBox.prototype.insertTable = function() 
{	
    this.focus();
    var winTable = window.showModalDialog(this.supportPath+"/inserttable.htm","","dialogwidth:500px;dialogheight:180px;status:no");
    if(winTable!=null)	
        this.insertHtml(winTable);	
};
JetsenWeb.UI.HtmlBox.prototype.insertTableColumnAfter = function()
{
    this.insertColumn(true);
};
JetsenWeb.UI.HtmlBox.prototype.insertTableColumnBefore = function()
{
    this.insertColumn(false);
};
JetsenWeb.UI.HtmlBox.prototype.insertTableRowAfter = function()
{ 
    this.insertTableRow(true);
};
JetsenWeb.UI.HtmlBox.prototype.insertTableRowBefore = function()
{ 
    this.insertTableRow(false);
};
JetsenWeb.UI.HtmlBox.prototype.insertTableCell = function()
{
    this.focus();
    var editor = this.designEditor;	
    var td =  this.getClosest("td");
    if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	tr = td.parentNode;	
    if(tr==null)
        return;
    var otd = editor.document.createElement("td");    
    var ref = td.nextSibling;tr.insertBefore(otd, ref);
};
JetsenWeb.UI.HtmlBox.prototype.deleteTableColumn = function()
{ 
    this.focus();
    var td = this.getClosest("td");	
    if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	if(td.parentNode==null)
	    return;
    var table = td.parentNode.parentNode;
    if(table==null)
        return;
    var index = td.cellIndex;	this.selectNextNode(td);	
    var rows = table.rows;	
    if(rows==null)
        return;
    for (var i = rows.length; --i >= 0;)	
    {	
        var tr = rows[i];if(tr.cells[index]!=null) 
        tr.removeChild(tr.cells[index]);	
        if(tr.cells[length]==0) table.removeChild(tr);	
    }	
    if(table!=null) 
        if(table["rows"]["length"]==0)
            table["parentNode"].removeChild(table) ;
};
JetsenWeb.UI.HtmlBox.prototype.deleteTableRow = function()
{
    this.focus();
    var tr = this.getClosest("tr");
    if(!tr)
    {
        tr = this.getClosest("tr",this.currentControl);
	    if (!tr) {	return;	}	
	}
    var par = tr.parentNode;
    this.selectNextNode(tr);par.removeChild(tr);
    if(par!=null && par["rows"]!=null) 	
        if(par["rows"]["length"]==0) 
            par["parentNode"].removeChild(par) ;
};
JetsenWeb.UI.HtmlBox.prototype.clearRow = function(tr)
{
    this.focus();
    var tds = tr.getElementsByTagName("td");
    for (var i = tds.length; --i >= 0;) 
    {
        var td = tds[i];td.rowSpan = 1;td.innerHTML = (this.isIE) ? "" : "<br />";	
    }
};
JetsenWeb.UI.HtmlBox.prototype.deleteTableCell = function()
{
    this.focus();
    var Ox60;var table;var Oxca;  Ox60=this.getClosest("TD") ;
    if (!Ox60) {return;} Oxca=Ox60.parentNode; table=Oxca.parentNode; 
    if(table!=null){if(Oxca["cells"]["length"]<=0x1){ table.deleteRow(Oxca.rowIndex) ; 
    if(table["rows"]["length"]==0x0){ table["parentNode"].removeChild(table) ;} ;}  
    else { Oxca.deleteCell(Ox60.cellIndex) ;} ;} ;
};
JetsenWeb.UI.HtmlBox.prototype.splitRow = function(td)
{	
    this.focus();
    var n = parseInt("" + td.rowSpan);	
    var nc = parseInt("" + td.colSpan);
	td.rowSpan = 1;	tr = td.parentNode;	var itr = tr.rowIndex;	var trs = tr.parentNode.rows;	var index = td.cellIndex;
	while (--n > 0) {tr = trs[++itr];var otd = editor._doc.createElement("td");	otd.colSpan = td.colSpan;tr.insertBefore(otd, tr.cells[index]);
	}//editor.forceRedraw();editor.updateToolbar();
};
JetsenWeb.UI.HtmlBox.prototype.splitCol = function(td) 
{
    this.focus();
    var nc = parseInt("" + td.colSpan);	td.colSpan = 1;	tr = td.parentNode;	var ref = td.nextSibling;
	while (--nc > 0) {	var otd = document.createElement("td");tr.insertBefore(otd, ref);	}
	//editor.forceRedraw();editor.updateToolbar();
};
JetsenWeb.UI.HtmlBox.prototype.splitTableCell = function()
{
    this.focus();
	var editor = this.designEditor;
	var td = this.getClosest("td");
	if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	var nc = parseInt("" + td.colSpan);	
	tr = td.parentNode;	
	if(tr==null)
	    return;
	var ref = td.nextSibling;
	if (--nc > 0) {	var otd = editor.document.createElement("td");otd.rowSpan=td.rowSpan;tr.insertBefore(otd, ref);td.colSpan = nc;	}
};
JetsenWeb.UI.HtmlBox.prototype.splitTableRow = function()
{
    this.focus();
	var editor = this.designEditor;
	var td = this.getClosest("td");
	if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	var n = parseInt("" + td.rowSpan);	var nc = parseInt("" + td.colSpan);	
	tr = td.parentNode;	
	if(tr==null)
	    return;
	var rindex = tr.rowIndex;
	if(tr.parentNode==null)
	    return;
	if(tr.parentNode.rows==null) return;
	var ref = tr.parentNode.rows[rindex+n-1]
	if(ref==null) return;
	var index = td.cellIndex;
	var items = ref.cells;
	while(items[index]==null)
	{ if(items.length>0){index = items.length-1;}else{return ;}
	}
	if (--n > 0) {var otd = editor.document.createElement("td");	otd.colSpan = td.colSpan;ref.insertBefore(otd, items[index]);td.rowSpan =n}
};
JetsenWeb.UI.HtmlBox.prototype.mergeRight = function()
{
	this.focus();
	var td = this.getClosest("td");
	if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	var ref = td.nextSibling;
	if(ref!=null && ref.tagName!="TD")
	    ref = ref.nextSibling;
	if(ref!=null && ref.tagName!="TD")
	    ref = ref.nextSibling;
	if(ref==null) return;
	td.innerHTML = td.innerHTML+ref.innerHTML;
	td.colSpan = td.colSpan+ref.colSpan;
	td.parentNode.removeChild(ref);	
};
JetsenWeb.UI.HtmlBox.prototype.mergeBottom = function()
{	
    this.focus();
	var td = this.getClosest("td");
	if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}
	var index = td.cellIndex;
	tr = td.parentNode;	
	if(tr==null)
	    return;
	var rindex = tr.rowIndex;
	var span = td.rowSpan;	
	if(tr.parentNode.rows==null) return;
	var ref = tr.parentNode.rows[rindex+span]
	if(ref==null) return;
	var items = ref.cells;
	index = this.createTableArrayData(rindex,index,rindex+span);
	if(index==null) return;
	while(items[index]==null)
	{ if(items.length>0){index = items.length-1;}else{return ;}	}
	td.innerHTML = td.innerHTML+items[index].innerHTML;
	td.rowSpan = span+items[index].rowSpan;ref.removeChild(items[index]); //if(ref.cells.length==0) ref.parentNode.removeChild(ref);
};
JetsenWeb.UI.HtmlBox.prototype.splitCell = function(td)
{
    this.focus();
    var nc = parseInt("" + td.colSpan);	splitCol(td);	
    var items = td.parentNode.cells;	
    var index = td.cellIndex;	
    while (nc-- > 0) {	splitRow(items[index++]);	}
};
JetsenWeb.UI.HtmlBox.prototype.insertColumn = function(after)
{  
    this.focus();
    editor = this.designEditor;   
    var td = this.getClosest("td");
    if(!td)
    {
        td = this.getClosest("td",this.currentControl);
	    if (!td) {	return;	}	
	}	
	if(td.parentNode==null || td.parentNode.parentNode==null)
	    return;
    var rows = td.parentNode.parentNode.rows; 
    if(rows==null)
        return; 
    var index = td.cellIndex;
    for (var i = rows.length-1; i >= 0;i--)
    {
        var tr = rows[i];
        var otd = editor.document.createElement("td");
        otd.innerHTML = (this.isIE) ? "" : "<br />";
        //if last column and insert column after is select append child
        if (index==tr.cells.length-1 && after){
            tr.appendChild(otd);
        } 
        else
        {
            var cellIndex = index + ((after) ? 1 : 0);
            cellIndex = cellIndex<0?0:cellIndex;
            if(tr.cells.length-1<=cellIndex)
                tr.appendChild(otd);
            else
            {
                var ref = tr.cells[cellIndex];
                tr.insertBefore(otd, ref); 
            }   
        } 
    }
};
JetsenWeb.UI.HtmlBox.prototype.insertTableRow = function(after)
{	
    this.focus();
    var tr = this.getClosest("tr");
    if(!tr)
    {
        tr = this.getClosest("tr",this.currentControl);
	    if (!tr) {	return;	}	
	}
	var otr = tr.cloneNode(true);	
	this.clearRow(otr);	
	tr.parentNode.insertBefore(otr, ((after) ? tr.nextSibling : tr));
};
JetsenWeb.UI.HtmlBox.prototype.createTable = function(cols,rows,width,widthUnit,align,cellpadding,cellspacing,border)
{
	var editor = this.designEditor;
	var sel = this.getSelection();
	var range = this.createRange(sel);		
	var doc = editor.document;
	// create the table element
	var table = doc.createElement("table");
	// assign the given arguments
	table.style.width 	= width + widthUnit;table.align	= align;table.border= border;table.cellspacing 	= cellspacing;table.cellpadding 	= cellpadding;	
	var tbody = doc.createElement("tbody");
	table.appendChild(tbody);	
	for (var i = 0; i < rows; ++i) {
		var tr = doc.createElement("tr");
		tbody.appendChild(tr);
		for (var j = 0; j < cols; ++j) {
			var td = doc.createElement("td");
			tr.appendChild(td);
			// Mozilla likes to see something inside the cell.
			if (!IS_IE) td.appendChild(doc.createElement("br"));
		}
	}	
	if (IS_IE) {	range.pasteHTML(table.outerHTML);	} 
	else {this.insertNodeAtSelection(table);	}	
	return true;
};

JetsenWeb.UI.HtmlBox.prototype.getTableCellsMax = function()
{	
    var tab = this.getClosest("table");	
    if(!tab)
    {
        tab = this.getClosest("table",this.currentControl);
	    if (!tab) {	return;	}	
	}
    var rows  = tab.rows.length;
    var tr;var Max=0;for(var i=0;i<rows;i++){	tr = tab.rows[i];	if(Max<tr.cells.length) Max=tr.cells.length;	}	
    return Max;	
};
JetsenWeb.UI.HtmlBox.prototype.createTableArrayData = function(mergeR,mergeC,newMergeR)
{
	var tab = this.getClosest("table");	
	if(!tab)
    {
        tab = this.getClosest("table",this.currentControl);
	    if (!tab) {	return;	}	
	}
	var rows  = tab.rows.length;	var cells = this.getTableCellsMax()
	var arr=  new Array(rows*cells)	;		
	for(var ai=0;ai<rows*cells;ai++){	arr[ai]=0;	}	
	var tr;	var index;	var span;
	for(var r = 0;r<rows;r++)	{		
	    tr = tab.rows[r];		
	    for(var c=0;c<tr.cells.length;c++)
	    {			
	        span = tr.cells[c].rowSpan;	index = c;	var cellindexlen=r*cells+index;			
	        for(var inj=r*cells;inj<=cellindexlen;inj++){	if(arr[inj]==1)  cellindexlen++;	index = index +arr[inj];		}
			for(var i = r+1 ;i<span+r;i++)	{if(i<rows && index<cells)	arr[i*cells+index]=1;}		}	}	
	index = mergeC;	cellindexlen=mergeR*cells+mergeC;
	for(var mergej=mergeR*cells;mergej<=cellindexlen;mergej++){	if(arr[mergej]==1)  cellindexlen++;	index = index +arr[mergej];		}		
	for(var nmergej=newMergeR*cells;nmergej<=newMergeR*cells+index;nmergej++){	 	index = index -arr[nmergej];		}
	//jetsennet.alert(index);
	return index;	
	/*	var str="";	for(var wi=0;wi<rows*cells;wi++)	{			if((wi % cells)==0) str = str+"\n";			str = str+","+arr[wi];		}	jetsennet.alert(str);*/
};

//-Style=======================================================================
HtmlBoxImgMouseOver = function(obj)
{
    obj.className="HtmlBoxImgButtonOver";
};
HtmlBoxImgMouseOut = function(obj)
{
    obj.className="HtmlBoxImgButton";
};
HtmlBoxImgMouseDown = function(obj)
{
    obj.className="HtmlBoxImgButtonDown";
};

//HtmlBoxButton================================================================
JetsenWeb.UI.HtmlBoxButton = function(src,commandIdentifier, customAction,tooltip) {	
	this.imageSrc = src;
	this.htbox = null;	
	this.customAction = customAction;	
	this.commandIdentifier = commandIdentifier;
	this.disabled = false;		
	this.control = null;	
	this.tooltip = tooltip?tooltip:"";
	
};
JetsenWeb.UI.HtmlBoxButton.prototype.render = function() {	
	var owner = this;
	this.control.style.cursor = "hand";
	this.control.className = "HtmlBoxImgButton";
	JetsenWeb.addEvents(this.control,["click"],function(e) { owner.click(e); } );
	JetsenWeb.addEvents(this.control,["mouseover"],function(e) { owner.mouseOver(e); } );
	JetsenWeb.addEvents(this.control,["mouseout"],function(e) { owner.mouseOut(e); } );
};
JetsenWeb.UI.HtmlBoxButton.prototype.click = function(e) {

	if (this.customAction) 			
		this.customAction();	
	else if (this.commandIdentifier != null && this.commandIdentifier != '') 
		this.htbox.executeCommand(this.commandIdentifier);
	this.htbox.htmlEditorEvent(e);	
};
JetsenWeb.UI.HtmlBoxButton.prototype.mouseOver = function(e) {
	if (!this.disabled) this.setButtonBackground("Over");
};
JetsenWeb.UI.HtmlBoxButton.prototype.mouseOut = function(e) {
	if (!this.disabled) this.setButtonBackground("Out");
};
JetsenWeb.UI.HtmlBoxButton.prototype.setButtonBackground = function(mouseState) {
		this.setButtonStyle(mouseState);
}
JetsenWeb.UI.HtmlBoxButton.prototype.setButtonStyle = function(mouseState) {	
	this.control.className ="HtmlBoxImgButton"+((mouseState=="Over")?"Over":"");
}

//HtmlBoxDropDownList==========================================================
JetsenWeb.UI.HtmlBoxDropDownList = function(id, commandIdentifier, customAction) {
	
	this.htbox = null;
	this.commandIdentifier = commandIdentifier;
	this.customAction = customAction;
	this.control = $(id);
	
	var owner = this;
	if (this.control) {		
		JetsenWeb.addEvents(this.control,["change"],function(e) { owner.select(e); } );
	} else {
		jetsennet.alert(id + ' is not setup properly');
	}
};
JetsenWeb.UI.HtmlBoxDropDownList.prototype.select = function(e) {	
	if (this.customAction) 
		this.customAction();		
	else if (this.commandIdentifier != null && this.commandIdentifier != '') 
		this.htbox.executeCommand(this.commandIdentifier, '', this.control.options[this.control.selectedIndex].value);	
	
	this.control.selectedIndex = 0;
	
	this.htbox.htmlEditorEvent(e);
};
JetsenWeb.UI.HtmlBoxDropDownList.prototype.setSelected = function(commandValue) {
	var alue = String(commandValue).toLowerCase();
	for (var i=0; i<this.list.options.length; i++) {
		if (this.control.options[i].value.toLowerCase() == value || this.control.options[i].text.toLowerCase() == value) {
			this.control.selectedIndex = i;
			return;
		}
	}
};

JetsenWeb.UI.HtmlBoxButtons = [
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/cut.gif','cut',function() { this.htbox.cut(); },'剪切'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/copy.gif','copy',function() { this.htbox.copy(); },'复制'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/paste.gif','paste',function() { this.htbox.paste(); },'粘贴'),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/delete.gif','delete',function() { this.htbox.deleteContents();},'删除'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/undo.gif','undo',function() { this.htbox.undo();},'撤消'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/redo.gif','redo',function() { this.htbox.redo();},'重复'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/bold.gif','bold',null,'粗体'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/italic.gif','italic',null,'斜体'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/underline.gif','underline',null,'下划线'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/strikethrough.gif','strikethrough',null,'删除线'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/superscript.gif','superscript',null,'上标'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/subscript.gif','subscript',null,'下标'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/removeformat.gif','removeformat',null,'去除格式'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/justifyleft.gif','justifyleft',null,'靠左'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/justifyright.gif','justifyright',null,'靠右'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/justifycenter.gif','justifycenter',null,'居中'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/bulletedlist.gif','insertunorderedlist',null,'项目符号'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/numberedlist.gif','insertorderedlist',null,'项目符号'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/indent.gif','indent',null,'右缩进'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/outdent.gif','outdent',null,'左缩进'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/backcolor.gif','backcolor',function() { this.htbox.setBackColor();},'背景色'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/forecolor.gif','forecolor',function() { this.htbox.setForeColor();},'前景色'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/insertrule.gif','inserthorizontalrule',null,'分隔线'),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/link.gif','createlink',function() { this.htbox.createLink();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/unlink.gif','unlink',null),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/anchor.gif','',function() { this.htbox.createAnchor();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/insertdate.gif','',function() { this.htbox.insertDate();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/timer.gif','',function() { this.htbox.insertTime();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/insertimage.gif','',function() { this.htbox.insertImage();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/insertimage.gif','',function() { this.htbox.insertImage2();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/specialchar.gif','',function() { this.htbox.insertChar();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/emsmile.gif','',function() { this.htbox.insertEmotion();}),
//new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inserttable.gif','',function() { this.htbox.insertTable();},'新增表格'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inserttablerowbefore.gif','',function() { this.htbox.insertTableRowBefore();},'在此处前插入行'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inserttablerowafter.gif','',function() { this.htbox.insertTableRowAfter();},'在此处后插入行'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inserttablecolumnbefore.gif','',function() { this.htbox.insertTableColumnBefore();},'在此处前插入列'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inserttablecolumnafter.gif','',function() { this.htbox.insertTableColumnAfter();},'在此处后插入列'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/inscell.gif','',function() { this.htbox.insertTableCell();},'新增单元格'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/deletetablerow.gif','',function() { this.htbox.deleteTableRow();},'删除行'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/deletetablecolumn.gif','',function() { this.htbox.deleteTableColumn();},'删除列'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/delcell.gif','',function() { this.htbox.deleteTableCell();},'删除单元格'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/mrgcell_r.gif','',function() { this.htbox.mergeRight();},'合并右边单元格'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/mrgcell_d.gif','',function() { this.htbox.mergeBottom();},'合并下方单元格'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/spltcell_row.gif','',function() { this.htbox.splitTableRow();},'分隔单元格行'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/spltcell_column.gif','',function() { this.htbox.splitTableCell();},'分隔单元格列'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/selectparent.gif','',function() { this.htbox.selectParentNode();},'父节点'),
new JetsenWeb.UI.HtmlBoxButton(JetsenWeb.baseThemeUrl +'htmlbox/images/preview.gif','',function() { this.htbox.preview();},'预览')
];

