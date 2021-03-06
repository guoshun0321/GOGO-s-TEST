//=============================================================================
// For WebFX (http://webfx.eae.net/)  
// lixiaomin 2008-06-05   树控件
//=============================================================================
/*
var tree = new JetsenWeb.UI.Tree('Root');
  tree.add(new JetsenWeb.UI.TreeItem('Tree Item 1'));
  tree.add(new JetsenWeb.UI.TreeItem('Tree Item 2'));
  tree.add(new JetsenWeb.UI.TreeItem('Tree Item 3'));
document.getElementById('container').innerHTML = tree.toString();
*/
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_xtree"));
JetsenWeb.importCss("xtree");

JetsenWeb.UI.TreeConfig = {
	rootIcon        : JetsenWeb.baseThemeUrl+'images/tree/foldericon.gif',
	openRootIcon    : JetsenWeb.baseThemeUrl+'images/tree/openfoldericon.gif',
	folderIcon      : JetsenWeb.baseThemeUrl+'images/tree/foldericon.gif',
	openFolderIcon  : JetsenWeb.baseThemeUrl+'images/tree/openfoldericon.gif',
	fileIcon        : JetsenWeb.baseThemeUrl+'images/tree/file.gif',
	iIcon           : JetsenWeb.baseThemeUrl+'images/tree/i.gif',
	lIcon           : JetsenWeb.baseThemeUrl+'images/tree/l.gif',
	lMinusIcon      : JetsenWeb.baseThemeUrl+'images/tree/lminus.gif',
	lPlusIcon       : JetsenWeb.baseThemeUrl+'images/tree/lplus.gif',
	tIcon           : JetsenWeb.baseThemeUrl+'images/tree/t.gif',
	tMinusIcon      : JetsenWeb.baseThemeUrl+'images/tree/tminus.gif',
	tPlusIcon       : JetsenWeb.baseThemeUrl+'images/tree/tplus.gif',
	blankIcon       : JetsenWeb.baseThemeUrl+'images/tree/blank.png',
	defaultText     : 'Tree Item',
	defaultAction   : 'javascript:void(0);',
	defaultBehavior : 'classic',
	usePersistence	: false
};

JetsenWeb.UI.TreeHandler = {
	idCounter : 0,
	idPrefix  : "webfx-tree-object-",
	all       : {},
	behavior  : null,
	selected  : null,
	onSelect  : null, /* should be part of tree, not handler */
	getId     : function() { return this.idPrefix + this.idCounter++; },
	toggle    : function (oItem) { this.all[oItem.id.replace('-plus','')].toggle();},
	select    : function (oItem) { this.all[oItem.id.replace('-icon','')].select(); },
	click     : function (oItem) { this.all[oItem.id.replace('-anchor','')].click(); },
	focus     : function (oItem) { this.all[oItem.id.replace('-anchor','')].focus(); },
	blur      : function (oItem) { this.all[oItem.id.replace('-anchor','')].blur(); },
	keydown   : function (oItem, e) { return this.all[oItem.id].keydown(e.keyCode); },	
	insertHTMLBeforeEnd	:	function (oElement, sHTML) {
		if (oElement.insertAdjacentHTML != null) {
			oElement.insertAdjacentHTML("BeforeEnd", sHTML)
			return;
		}
		var df;	// DocumentFragment
		var r = oElement.ownerDocument.createRange();
		r.selectNodeContents(oElement);
		r.collapse(false);
		df = r.createContextualFragment(sHTML);
		oElement.appendChild(df);
	}
};

/*
 * JetsenWeb.UI.TreeAbstractNode class
 */

JetsenWeb.UI.TreeAbstractNode = function(sText, sAction) {
	this.childNodes  = [];
	this.id     = JetsenWeb.UI.TreeHandler.getId();
	this.text   = sText || JetsenWeb.UI.TreeConfig.defaultText;
	this.action = sAction || JetsenWeb.UI.TreeConfig.defaultAction;
	this._last  = false;
	JetsenWeb.UI.TreeHandler.all[this.id] = this;
}

/*
 * To speed thing up if you're adding multiple nodes at once (after load)
 * use the bNoIdent parameter to prevent automatic re-indentation and call
 * the obj.ident() method manually once all nodes has been added.
 */

JetsenWeb.UI.TreeAbstractNode.prototype.add = function (node, bNoIdent) {
	node.parentNode = this;
	this.childNodes[this.childNodes.length] = node;
	var root = this;
	if (this.childNodes.length >= 2) {
		this.childNodes[this.childNodes.length - 2]._last = false;
	}
	while (root.parentNode) { root = root.parentNode; }
	if (root.rendered) {
		if (this.childNodes.length >= 2) {
			document.getElementById(this.childNodes[this.childNodes.length - 2].id + '-plus').src = ((this.childNodes[this.childNodes.length -2].folder)?((this.childNodes[this.childNodes.length -2].open)?JetsenWeb.UI.TreeConfig.tMinusIcon:JetsenWeb.UI.TreeConfig.tPlusIcon):JetsenWeb.UI.TreeConfig.tIcon);
			this.childNodes[this.childNodes.length - 2].plusIcon = JetsenWeb.UI.TreeConfig.tPlusIcon;
			this.childNodes[this.childNodes.length - 2].minusIcon = JetsenWeb.UI.TreeConfig.tMinusIcon;
			this.childNodes[this.childNodes.length - 2]._last = false;
		}
		this._last = true;
		var foo = this;
		while (foo.parentNode) {
			for (var i = 0; i < foo.parentNode.childNodes.length; i++) {
				if (foo.id == foo.parentNode.childNodes[i].id) { break; }
			}
			if (i == foo.parentNode.childNodes.length - 1) { foo.parentNode._last = true; }
			else { foo.parentNode._last = false; }
			foo = foo.parentNode;
		}
		JetsenWeb.UI.TreeHandler.insertHTMLBeforeEnd(document.getElementById(this.id + '-cont'), node.toString());
		if ((!this.folder) && (!this.openIcon)) {
			this.icon = JetsenWeb.UI.TreeConfig.folderIcon;
			this.openIcon = JetsenWeb.UI.TreeConfig.openFolderIcon;
		}
		if (!this.folder) { this.folder = true; this.collapse(true); }
		if (!bNoIdent) { this.indent(); }
	}
	return node;
}

JetsenWeb.UI.TreeAbstractNode.prototype.toggle = function() {
	if (this.folder) {
		if (this.open) { this.collapse(); }
		else { this.expand(); }
}	}

JetsenWeb.UI.TreeAbstractNode.prototype.select = function() {
    JetsenWeb.cancelEvent();
    if(document.getElementById(this.id + '-anchor'))
	    document.getElementById(this.id + '-anchor').focus();
	
}

JetsenWeb.UI.TreeAbstractNode.prototype.deSelect = function() {
    if(document.getElementById(this.id + '-anchor'))
    {
	    document.getElementById(this.id + '-anchor').className = '';	
	}
	JetsenWeb.UI.TreeHandler.selected = null;
}

JetsenWeb.UI.TreeAbstractNode.prototype.focus = function() {
	if ((JetsenWeb.UI.TreeHandler.selected) && (JetsenWeb.UI.TreeHandler.selected != this)) { JetsenWeb.UI.TreeHandler.selected.deSelect(); }
	JetsenWeb.UI.TreeHandler.selected = this;
	if ((this.openIcon) && (JetsenWeb.UI.TreeHandler.behavior != 'classic')) { document.getElementById(this.id + '-icon').src = this.openIcon; }
	document.getElementById(this.id + '-anchor').className = 'selected';
	try{
	    document.getElementById(this.id + '-anchor').focus();
	}catch(e){};
	if (JetsenWeb.UI.TreeHandler.onSelect) { JetsenWeb.UI.TreeHandler.onSelect(this); }
}

JetsenWeb.UI.TreeAbstractNode.prototype.blur = function() {
	if ((this.openIcon) && (JetsenWeb.UI.TreeHandler.behavior != 'classic')) { document.getElementById(this.id + '-icon').src = this.icon; }
	document.getElementById(this.id + '-anchor').className = 'selected-inactive';
}

JetsenWeb.UI.TreeAbstractNode.prototype.click = function(){
    if(this.onclick && typeof this.onclick=="function")
        this.onclick();
}

JetsenWeb.UI.TreeAbstractNode.prototype.doExpand = function() {
    if(document.getElementById(this.id))
    {
	    if (JetsenWeb.UI.TreeHandler.behavior == 'classic') { document.getElementById(this.id + '-icon').src = this.openIcon; }
	    if (this.childNodes.length) {  document.getElementById(this.id + '-cont').style.display = 'block'; }
	    if (JetsenWeb.UI.TreeConfig.usePersistence) {
		    JetsenWeb.Util.cookie(this.id.substr(18,this.id.length - 18), '1');
        }
	}
	this.open = true;
}

JetsenWeb.UI.TreeAbstractNode.prototype.doCollapse = function() {
    if(document.getElementById(this.id))
    {
        if (JetsenWeb.UI.TreeHandler.behavior == 'classic') { document.getElementById(this.id + '-icon').src = this.icon; }
        if (this.childNodes.length) { document.getElementById(this.id + '-cont').style.display = 'none'; }
        if (JetsenWeb.UI.TreeConfig.usePersistence) {
	        JetsenWeb.Util.cookie(this.id.substr(18,this.id.length - 18), '0');
        }	
    }	
	this.open = false;
}

JetsenWeb.UI.TreeAbstractNode.prototype.expandAll = function() {
	this.expandChildren();
	if ((this.folder) && (!this.open)) { this.expand(); }
}

JetsenWeb.UI.TreeAbstractNode.prototype.expandChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].expandAll();
} }

JetsenWeb.UI.TreeAbstractNode.prototype.collapseAll = function() {
	this.collapseChildren();
	if ((this.folder) && (this.open)) { this.collapse(true); }
}

JetsenWeb.UI.TreeAbstractNode.prototype.collapseChildren = function() {
	for (var i = 0; i < this.childNodes.length; i++) {
		this.childNodes[i].collapseAll();
} }

JetsenWeb.UI.TreeAbstractNode.prototype.indent = function(lvl, del, last, level, nodesLeft) {
	/*
	 * Since we only want to modify items one level below ourself,
	 * and since the rightmost indentation position is occupied by
	 * the plus icon we set this to -2
	 */
	if (lvl == null) { lvl = -2; }
	var state = 0;
	for (var i = this.childNodes.length - 1; i >= 0 ; i--) {
		state = this.childNodes[i].indent(lvl + 1, del, last, level);
		if (state) { return; }
	}
	if (del) {
		if ((level >= this._level) && (document.getElementById(this.id + '-plus'))) {
			if (this.folder) {
				document.getElementById(this.id + '-plus').src = (this.open)?JetsenWeb.UI.TreeConfig.lMinusIcon:JetsenWeb.UI.TreeConfig.lPlusIcon;
				this.plusIcon = JetsenWeb.UI.TreeConfig.lPlusIcon;
				this.minusIcon = JetsenWeb.UI.TreeConfig.lMinusIcon;
			}
			else if (nodesLeft) { document.getElementById(this.id + '-plus').src = JetsenWeb.UI.TreeConfig.lIcon; }
			return 1;
	}	}
	var foo = document.getElementById(this.id + '-indent-' + lvl);
	if (foo) {
		if ((foo._last) || ((del) && (last))) { foo.src =  JetsenWeb.UI.TreeConfig.blankIcon; }
		else { foo.src =  JetsenWeb.UI.TreeConfig.iIcon; }
	}
	return 0;
}

/*
 * Tree class
 */

JetsenWeb.UI.Tree = function(sText, sAction, sBehavior, sIcon, sOpenIcon) {
	this.base = JetsenWeb.UI.TreeAbstractNode;
	this.base(sText, sAction);
	this.icon      = sIcon || JetsenWeb.UI.TreeConfig.rootIcon;
	this.openIcon  = sOpenIcon || JetsenWeb.UI.TreeConfig.openRootIcon;
	/* Defaults to open */
	if (JetsenWeb.UI.TreeConfig.usePersistence) {
		this.open  = (JetsenWeb.Util.cookie(this.id.substr(18,this.id.length - 18)) == '0')?false:true;
	} else { this.open  = true; }
	this.folder    = true;
	this.showTop   = true;
	this.rendered  = false;
	this.onSelect  = null;
	if (!JetsenWeb.UI.TreeHandler.behavior) {  JetsenWeb.UI.TreeHandler.behavior = sBehavior || JetsenWeb.UI.TreeConfig.defaultBehavior; }
}

JetsenWeb.UI.Tree.prototype = new JetsenWeb.UI.TreeAbstractNode();

JetsenWeb.UI.Tree.prototype.setBehavior = function (sBehavior) {
	JetsenWeb.UI.TreeHandler.behavior =  sBehavior;
};

JetsenWeb.UI.Tree.prototype.getBehavior = function (sBehavior) {
	return JetsenWeb.UI.TreeHandler.behavior;
};

JetsenWeb.UI.Tree.prototype.getSelected = function() {
	if (JetsenWeb.UI.TreeHandler.selected) { return JetsenWeb.UI.TreeHandler.selected; }
	else { return null; }
}

JetsenWeb.UI.Tree.prototype.remove = function() { }

JetsenWeb.UI.Tree.prototype.expand = function() {
	this.doExpand();
}

JetsenWeb.UI.Tree.prototype.collapse = function(b) {
	if (!b) { this.focus(); }
	this.doCollapse();
}

JetsenWeb.UI.Tree.prototype.getFirst = function() {
	return null;
}

JetsenWeb.UI.Tree.prototype.getLast = function() {
	return null;
}

JetsenWeb.UI.Tree.prototype.getNextSibling = function() {
	return null;
}

JetsenWeb.UI.Tree.prototype.getPreviousSibling = function() {
	return null;
}

JetsenWeb.UI.Tree.prototype.keydown = function(key) {
	if (key == 39) {
		if (!this.open) { this.expand(); }
		else if (this.childNodes.length) { this.childNodes[0].select(); }
		return false;
	}
	if (key == 37) { this.collapse(); return false; }
	if ((key == 40) && (this.open) && (this.childNodes.length)) { this.childNodes[0].select(); return false; }
	return true;
}

JetsenWeb.UI.Tree.prototype.toString = function() {
    var str = "";
    if(this.showTop)
    {
	    str = "<div id=\"" + this.id + "\" ondblclick=\"JetsenWeb.UI.TreeHandler.toggle(this);\" class=\"webfx-tree-item\" onkeydown=\"return JetsenWeb.UI.TreeHandler.keydown(this, event)\">" +
		"<img id=\"" + this.id + "-icon\" class=\"webfx-tree-icon\" src=\"" + ((JetsenWeb.UI.TreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) + "\" onclick=\"JetsenWeb.UI.TreeHandler.select(this);\">" +
		"<a href=\"" + this.action + "\" id=\"" + this.id + "-anchor\" onfocus=\"JetsenWeb.UI.TreeHandler.focus(this);\" onblur=\"JetsenWeb.UI.TreeHandler.blur(this);\"" +
		(this.target ? " target=\"" + this.target + "\"" : "") +
		">" + this.text + "</a></div>";
	}
	else
	{
	    this.open = true;
	}
	str  +=	"<div id=\"" + this.id + "-cont\" class=\"webfx-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">";
	var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i, this.childNodes.length);
	}
	this.rendered = true;
	return str + sb.join("") + "</div>";
};

/*
 * TreeItem class
 */

JetsenWeb.UI.TreeItem = function(sText, sAction, eParent, sIcon, sOpenIcon) {
	this.base = JetsenWeb.UI.TreeAbstractNode;
	this.base(sText, sAction);
	/* Defaults to close */
	if (JetsenWeb.UI.TreeConfig.usePersistence) {
		this.open = (JetsenWeb.Util.cookie(this.id.substr(18,this.id.length - 18)) == '1')?true:false;
	} else { this.open = false; }
	if (sIcon) { this.icon = sIcon; }
	if (sOpenIcon) { this.openIcon = sOpenIcon; }
	if (eParent) { eParent.add(this); }
}

JetsenWeb.UI.TreeItem.prototype = new JetsenWeb.UI.TreeAbstractNode();

JetsenWeb.UI.TreeItem.prototype.remove = function() {
	var iconSrc = document.getElementById(this.id + '-plus').src;
	var parentNode = this.parentNode;
	var prevSibling = this.getPreviousSibling(true);
	var nextSibling = this.getNextSibling(true);
	var folder = this.parentNode.folder;
	var last = ((nextSibling) && (nextSibling.parentNode) && (nextSibling.parentNode.id == parentNode.id))?false:true;
	this.getPreviousSibling().focus();
	this._remove();
	if (parentNode.childNodes.length == 0) {
		document.getElementById(parentNode.id + '-cont').style.display = 'none';
		parentNode.doCollapse();
		parentNode.folder = false;
		parentNode.open = false;
	}
	if (!nextSibling || last) { parentNode.indent(null, true, last, this._level, parentNode.childNodes.length); }
	if ((prevSibling == parentNode) && !(parentNode.childNodes.length)) {
		prevSibling.folder = false;
		prevSibling.open = false;
		iconSrc = document.getElementById(prevSibling.id + '-plus').src;
		iconSrc = iconSrc.replace('minus', '').replace('plus', '');
		document.getElementById(prevSibling.id + '-plus').src = iconSrc;
		document.getElementById(prevSibling.id + '-icon').src = JetsenWeb.UI.TreeConfig.fileIcon;
	}
	if (document.getElementById(prevSibling.id + '-plus')) {
		if (parentNode == prevSibling.parentNode) {
			iconSrc = iconSrc.replace('minus', '').replace('plus', '');
			document.getElementById(prevSibling.id + '-plus').src = iconSrc;
}	}	}

JetsenWeb.UI.TreeItem.prototype._remove = function() {
	for (var i = this.childNodes.length - 1; i >= 0; i--) {
		this.childNodes[i]._remove();
 	}
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) {
			for (var j = i; j < this.parentNode.childNodes.length; j++) {
				this.parentNode.childNodes[j] = this.parentNode.childNodes[j+1];
			}
			this.parentNode.childNodes.length -= 1;
			if (i + 1 == this.parentNode.childNodes.length) { this.parentNode._last = true; }
			break;
	}	}
	JetsenWeb.UI.TreeHandler.all[this.id] = null;
	var tmp = document.getElementById(this.id);
	if (tmp) { tmp.parentNode.removeChild(tmp); }
	tmp = document.getElementById(this.id + '-cont');
	if (tmp) { tmp.parentNode.removeChild(tmp); }
}

JetsenWeb.UI.TreeItem.prototype.expand = function() {
	this.doExpand();
	if(document.getElementById(this.id))
	    document.getElementById(this.id + '-plus').src = this.minusIcon;
}

JetsenWeb.UI.TreeItem.prototype.collapse = function(b) {
	if (!b) { this.focus(); }
	this.doCollapse();
	if(document.getElementById(this.id))
	    document.getElementById(this.id + '-plus').src = this.plusIcon;
}

JetsenWeb.UI.TreeItem.prototype.getFirst = function() {
	return this.childNodes[0];
}

JetsenWeb.UI.TreeItem.prototype.getLast = function() {
	if (this.childNodes[this.childNodes.length - 1].open) { return this.childNodes[this.childNodes.length - 1].getLast(); }
	else { return this.childNodes[this.childNodes.length - 1]; }
}

JetsenWeb.UI.TreeItem.prototype.getNextSibling = function() {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (++i == this.parentNode.childNodes.length) { return this.parentNode.getNextSibling(); }
	else { return this.parentNode.childNodes[i]; }
}

JetsenWeb.UI.TreeItem.prototype.getPreviousSibling = function(b) {
	for (var i = 0; i < this.parentNode.childNodes.length; i++) {
		if (this == this.parentNode.childNodes[i]) { break; }
	}
	if (i == 0) { return this.parentNode; }
	else {
		if ((this.parentNode.childNodes[--i].open) || (b && this.parentNode.childNodes[i].folder)) { return this.parentNode.childNodes[i].getLast(); }
		else { return this.parentNode.childNodes[i]; }
} }

JetsenWeb.UI.TreeItem.prototype.keydown = function(key) {
	if ((key == 39) && (this.folder)) {
		if (!this.open) { this.expand(); }
		else { this.getFirst().select(); }
		return false;
	}
	else if (key == 37) {
		if (this.open) { this.collapse(); }
		else { this.parentNode.select(); }
		return false;
	}
	else if (key == 40) {
		if (this.open) { this.getFirst().select(); }
		else {
			var sib = this.getNextSibling();
			if (sib) { sib.select(); }
		}
		return false;
	}
	else if (key == 38) { this.getPreviousSibling().select(); return false; }
	return true;
}

JetsenWeb.UI.TreeItem.prototype.toString = function (nItem, nItemCount) {
	var foo = this.parentNode;
	var indent = '';
	if (nItem + 1 == nItemCount) { this.parentNode._last = true; }
	var i = 0;
	while (foo.parentNode) {
		foo = foo.parentNode;
		indent = "<img id=\"" + this.id + "-indent-" + i + "\" src=\"" + ((foo._last)?JetsenWeb.UI.TreeConfig.blankIcon:JetsenWeb.UI.TreeConfig.iIcon) + "\">" + indent;
		i++;
	}
	this._level = i;
	if(this.id=="tree07-0-1")
	jetsennet.alert(this.childNodes.length);
	if (this.childNodes.length) { this.folder = 1; }
	else { this.open = false; }
	if ((this.folder) || (JetsenWeb.UI.TreeHandler.behavior != 'classic')) {
		if (!this.icon) { this.icon = JetsenWeb.UI.TreeConfig.folderIcon; }
		if (!this.openIcon) { this.openIcon = JetsenWeb.UI.TreeConfig.openFolderIcon; }
	}
	else if (!this.icon) { this.icon = JetsenWeb.UI.TreeConfig.fileIcon; }
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;');
	var str = "<div id=\"" + this.id + "\" ondblclick=\"JetsenWeb.UI.TreeHandler.toggle(this);\" class=\"webfx-tree-item\" onkeydown=\"return JetsenWeb.UI.TreeHandler.keydown(this, event)\">" +
		indent +
		"<img id=\"" + this.id + "-plus\" src=\"" + ((this.folder)?((this.open)?((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lMinusIcon:JetsenWeb.UI.TreeConfig.tMinusIcon):((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lPlusIcon:JetsenWeb.UI.TreeConfig.tPlusIcon)):((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lIcon:JetsenWeb.UI.TreeConfig.tIcon)) + "\" onclick=\"JetsenWeb.UI.TreeHandler.toggle(this);JetsenWeb.cancelEvent();\">" +
		"<img id=\"" + this.id + "-icon\" class=\"webfx-tree-icon\" src=\"" + ((JetsenWeb.UI.TreeHandler.behavior == 'classic' && this.open)?this.openIcon:this.icon) + "\" onclick=\"JetsenWeb.UI.TreeHandler.select(this);\">" +
		"<a href=\"" + this.action + "\" id=\"" + this.id + "-anchor\" onfocus=\"JetsenWeb.UI.TreeHandler.focus(this);\" onblur=\"JetsenWeb.UI.TreeHandler.blur(this);\"" +
		(this.target ? " target=\"" + this.target + "\"" : "") +
		" onclick=\"JetsenWeb.UI.TreeHandler.click(this);\">" + label + "</a></div>" +
		"<div id=\"" + this.id + "-cont\" class=\"webfx-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">";
	var sb = [];
	for (var i = 0; i < this.childNodes.length; i++) {
		sb[i] = this.childNodes[i].toString(i,this.childNodes.length);
	}
	this.plusIcon = ((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lPlusIcon:JetsenWeb.UI.TreeConfig.tPlusIcon);
	this.minusIcon = ((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lMinusIcon:JetsenWeb.UI.TreeConfig.tMinusIcon);
	return str + sb.join("") + "</div>";
}

JetsenWeb.UI.CheckBoxTreeItem = function(sID,sText,sAction,bChecked,eParent,sIcon,sOpenIcon) 
{ 
	this.base = JetsenWeb.UI.TreeItem; 
	this.base(sText,sAction,eParent,sIcon,sOpenIcon); 
	this.ItemID = sID;
	this._checked = bChecked;
} 

JetsenWeb.UI.CheckBoxTreeItem.prototype = new JetsenWeb.UI.TreeItem();  
JetsenWeb.UI.CheckBoxTreeItem.prototype.toString = function(nItem,nItemCount) 
{ 
	var foo = this.parentNode; 
	var indent = ''; 
	if(nItem + 1 == nItemCount) 
	{ 
		this.parentNode._last = true; 
	} 
	var i = 0; 
	while (foo.parentNode) 
	{ 
		foo = foo.parentNode; 
		indent = "<img id=\"" + this.id + "-indent-" + i + "\" src=\"" + ((foo._last)?JetsenWeb.UI.TreeConfig.blankIcon:JetsenWeb.UI.TreeConfig.iIcon) + "\">" + indent; 
		i++; 
	} 
	this._level = i; 
	if (this.childNodes.length) 
	{ 
		this.folder = 1; 
	} 
	else 
	{ 
		this.open = false; 
	} 
	if ((this.folder) || (JetsenWeb.UI.TreeConfig.behavior != 'classic')) 
	{ 
		if (!this.icon) { this.icon = JetsenWeb.UI.TreeConfig.folderIcon; } 
		if (!this.openIcon) { this.openIcon = JetsenWeb.UI.TreeConfig.openFolderIcon; } 
	} 
	else if (!this.icon) 
	{ 
		this.icon = JetsenWeb.UI.TreeConfig.fileIcon; 
	} 
	var label = this.text.replace(/</g, '&lt;').replace(/>/g, '&gt;'); 
	var str = "<div id=\"" + this.id + "\" ondblclick=\"JetsenWeb.UI.TreeHandler.toggle(this);\" class=\"webfx-tree-item\" onkeydown=\"return JetsenWeb.UI.TreeHandler.keydown(this, event)\">"; 
	str += indent; 
	str += "<img id=\"" + this.id + "-plus\" src=\"" + ((this.folder)?((this.open)?((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lMinusIcon:JetsenWeb.UI.TreeConfig.tMinusIcon):((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lPlusIcon:JetsenWeb.UI.TreeConfig.tPlusIcon)):((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lIcon:JetsenWeb.UI.TreeConfig.tIcon)) + "\" onclick=\"JetsenWeb.UI.TreeHandler.toggle(this);\">" 
  
	// insert check box 
	str += "<input type=\"checkbox\"" + " id=\"ck_" + this.ItemID + "\" " +
	" class=\"tree-check-box\"" + 
	(this._checked ? " checked=\"checked\"" : "") + 
	" onclick=\"CheckBoxTreeItemClick(JetsenWeb.UI.TreeHandler.all[this.parentNode.id],this.checked);\"" + 
	" />"; 
	// end insert checkbox   

	str += "<img id=\"" + this.id + "-icon\" class=\"webfx-tree-icon\" src=\"" + ((JetsenWeb.UI.TreeConfig.behavior == 'classic' && this.open)?this.openIcon:this.icon) + "\" onclick=\"JetsenWeb.UI.TreeHandler.select(this);\"><a id=\"" + this.id + "-anchor\" onfocus=\"JetsenWeb.UI.TreeHandler.focus(this);\" onblur=\"JetsenWeb.UI.TreeHandler.blur(this);\">" + label + "</a></div>"; 
	str += "<div id=\"" + this.id + "-cont\" class=\"webfx-tree-container\" style=\"display: " + ((this.open)?'block':'none') + ";\">"; 
	for (var i = 0; i < this.childNodes.length; i++) 
	{ 
		str += this.childNodes[i].toString(i,this.childNodes.length); 
	} 
	str += "</div>"; 
	this.plusIcon = ((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lPlusIcon:JetsenWeb.UI.TreeConfig.tPlusIcon); 
	this.minusIcon = ((this.parentNode._last)?JetsenWeb.UI.TreeConfig.lMinusIcon:JetsenWeb.UI.TreeConfig.tMinusIcon); 
	return str; 
}  

JetsenWeb.UI.CheckBoxTreeItem.prototype.getChecked = function () 
{ 
	var divEl = document.getElementById(this.id); 
	var inputEl = divEl.getElementsByTagName("INPUT")[0]; 
	return this._checked = inputEl.checked; 
}; 
  
JetsenWeb.UI.CheckBoxTreeItem.prototype.setChecked = function (bChecked) 
{ 
	var divEl = document.getElementById(this.id); 
	var inputEl = divEl.getElementsByTagName("INPUT")[0]; 
	this._checked = inputEl.checked = bChecked; 
	if (typeof this.onchange == "function") 
	this.onchange();  
};

JetsenWeb.UI.CheckBoxTreeItem.prototype.childAllChecked = function (treeItem) 
{ 	
	return getChecked;
};

CheckBoxTreeItemClick = function(treeItem,bChecked)
{
    treeItem.oncheckboxclick();
	//children
	for (var i=0;i<treeItem.childNodes.length;i++) 
	{ 
		treeItem.childNodes[i].setChecked(bChecked); 
	} 
	//parent
	var returnVal = true;
	var parentItem = treeItem.parentNode;
	if(parentItem && typeof(parentItem.setChecked)!="undefined")
	{
		for (var j=0;j<parentItem.childNodes.length;j++) 
		{ 
			returnVal = returnVal & parentItem.childNodes[j].getChecked(); 
		} 
		parentItem.setChecked(returnVal);
	}	
}
JetsenWeb.UI.CheckBoxTreeItem.prototype.oncheckboxclick = function()
{}