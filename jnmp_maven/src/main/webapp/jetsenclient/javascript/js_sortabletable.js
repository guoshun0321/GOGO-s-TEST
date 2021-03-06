//=============================================================================
//For WebFX (http://webfx.eae.net/)
// lixiaomin 2008-06-05
//=============================================================================
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_sortabletable"));

JetsenWeb.SortableTable = function(oTable, oSortTypes) {
	this._sortTypes = oSortTypes || [];
	this.sortColumn = null;
	this.descending = null;
	var oThis = this;
	this._headerOnclick = function (e) {
		oThis.headerOnclick(e);
	};
	if (oTable) {
		this.setTable( oTable );
		this.document = oTable.ownerDocument || oTable.document;
	}
	else {
		this.document = document;
	}
	// only IE needs this
	var win = this.document.defaultView || this.document.parentWindow;
	this._onunload = function () {
		oThis.destroy();
	};
	if (win && typeof win.attachEvent != "undefined") {
		win.attachEvent("onunload", this._onunload);
	}
}

JetsenWeb.SortableTable.gecko = navigator.product == "Gecko";
JetsenWeb.SortableTable.msie = /msie/i.test(navigator.userAgent);
// Mozilla is faster when doing the DOM manipulations on
// an orphaned element. MSIE is not
JetsenWeb.SortableTable.removeBeforeSort = JetsenWeb.SortableTable.gecko;

JetsenWeb.SortableTable.prototype.onsort = function () {};

// default sort order. true -> descending, false -> ascending
JetsenWeb.SortableTable.prototype.defaultDescending = false;

// shared between all instances. This is intentional to allow external files
// to modify the prototype
JetsenWeb.SortableTable.prototype._sortTypeInfo = {};

JetsenWeb.SortableTable.prototype.setTable = function (oTable) {
	if ( this.tHead )
		this.uninitHeader();
	this.element = oTable;
	this.setTBody( oTable.tBodies[0] );
	this.setTHead( oTable.tBodies[0].rows[0] );
};

JetsenWeb.SortableTable.prototype.setTHead = function (oTHead) {
	if (this.tHead && this.tHead != oTHead )
		this.uninitHeader();
	this.tHead = oTHead;
	this.initHeader( this._sortTypes );
};

JetsenWeb.SortableTable.prototype.setTBody = function (oTBody) {
	this.tBody = oTBody;
};

JetsenWeb.SortableTable.prototype.setSortTypes = function ( oSortTypes ) {
	if ( this.tHead )
		this.uninitHeader();
	this._sortTypes = oSortTypes || [];
	if ( this.tHead )
		this.initHeader( this._sortTypes );
};

// adds arrow containers and events
// also binds sort type to the header cells so that reordering columns does
// not break the sort types
JetsenWeb.SortableTable.prototype.initHeader = function (oSortTypes) {
	if (!this.tHead) return;
	var cells = this.tHead.cells;
	var doc = this.tBody.ownerDocument || this.tBody.document;
	this._sortTypes = oSortTypes || [];
	var l = cells.length;
	var img, c;
	for (var i = 0; i < l; i++) {
		c = cells[i];
		if (this._sortTypes[i] != null && this._sortTypes[i] != "None") {
			
			if (this._sortTypes[i] != null)
				c._sortType = this._sortTypes[i];
//			if (typeof c.addEventListener != "undefined")
//				c.addEventListener("click", this._headerOnclick, false);
//			else if (typeof c.attachEvent != "undefined")
//				c.attachEvent("onclick", this._headerOnclick);
//			else
//				c.onclick = this._headerOnclick;
				
		}
		else
		{
			c.setAttribute( "_sortType", oSortTypes[i] );
			c._sortType = "None";
		}
	}
	this.updateHeaderArrows();
};

// remove arrows and events
JetsenWeb.SortableTable.prototype.uninitHeader = function () {
	if (!this.tHead) return;
	var cells = this.tHead.cells;
	var l = cells.length;
	var c;
	for (var i = 0; i < l; i++) {
		c = cells[i];
		if (c._sortType != null && c._sortType != "None") {
//			c.removeChild(c.lastChild);
//			if (typeof c.removeEventListener != "undefined")
//				c.removeEventListener("click", this._headerOnclick, false);
//			else if (typeof c.detachEvent != "undefined")
//				c.detachEvent("onclick", this._headerOnclick);
			c._sortType = null;
			c.removeAttribute( "_sortType" );
		}
	}
};

JetsenWeb.SortableTable.prototype.updateHeaderArrows = function () {
	if (!this.tHead) return;
	var cells = this.tHead.cells;
	var l = cells.length;
	var img;
	for (var i = 0; i < l; i++) {
		if (cells[i]._sortType != null && cells[i]._sortType != "None") {
//			img = cells[i].lastChild;
//			if (i == this.sortColumn)
//				img.className = "sort-arrow " + (this.descending ? "descending" : "ascending");
//			else
//				img.className = "sort-arrow";
		}
	}
};

JetsenWeb.SortableTable.prototype.headerOnclick = function (e) {
	// find TD element
	var el = e.target || e.srcElement;
	while (el.tagName != "TD")
		el = el.parentNode;

	this.sort(JetsenWeb.SortableTable.msie ? JetsenWeb.SortableTable.getCellIndex(el) : el.cellIndex);
};

// IE returns wrong cellIndex when columns are hidden
JetsenWeb.SortableTable.getCellIndex = function (oTd) {
	var cells = oTd.parentNode.childNodes
	var l = cells.length;
	var i;
	for (i = 0; cells[i] != oTd && i < l; i++)
		;
	return i;
};

JetsenWeb.SortableTable.prototype.getSortType = function (nColumn) {
	return this._sortTypes[nColumn] || "String";
};

// only nColumn is required
// if bDescending is left out the old value is taken into account
// if sSortType is left out the sort type is found from the sortTypes array

JetsenWeb.SortableTable.prototype.sort = function (nColumn, bDescending, sSortType) {
	if (!this.tBody) return;
	if (sSortType == null)
		sSortType = this.getSortType(nColumn);

	// exit if None
	if (sSortType == "None")
		return;

	if (bDescending == null) {
		if (this.sortColumn != nColumn)
			this.descending = this.defaultDescending;
		else
			this.descending = !this.descending;
	}
	else
		this.descending = bDescending;

	this.sortColumn = nColumn;

	if (typeof this.onbeforesort == "function")
		this.onbeforesort();

	var f = this.getSortFunction(sSortType, nColumn);
	var a = this.getCache(sSortType, nColumn);
	var _head = a.shift(0);
	var tBody = this.tBody;

	a.sort(f);    
	if (this.descending)
		a.reverse();
	a.unshift(_head);
	
	if (JetsenWeb.SortableTable.removeBeforeSort) {
		// remove from doc
		var nextSibling = tBody.nextSibling;
		var p = tBody.parentNode;
		p.removeChild(tBody);
	}

	// insert in the new order
	var l = a.length;
	for (var i = 0; i < l; i++){
		tBody.appendChild(a[i].element);
    }

	if (JetsenWeb.SortableTable.removeBeforeSort) {
		// insert into doc
		p.insertBefore(tBody, nextSibling);
	}
	
	this.updateHeaderArrows();

	this.destroyCache(a);

	if (typeof this.onsort == "function")
		this.onsort();
};

JetsenWeb.SortableTable.prototype.asyncSort = function (nColumn, bDescending, sSortType) {
	var oThis = this;
	this._asyncsort = function () {
		oThis.sort(nColumn, bDescending, sSortType);
	};
	window.setTimeout(this._asyncsort, 1);
};

JetsenWeb.SortableTable.prototype.getCache = function (sType, nColumn) {
	if (!this.tBody) return [];
	var rows = this.tBody.rows;
	var l = rows.length;
	var a = new Array(l);
	var r;
	for (var i = 0; i < l; i++) {
		r = rows[i];
		a[i] = {
			value:		this.getRowValue(r, sType, nColumn),
			element:	r
		};
	};
	return a;
};

JetsenWeb.SortableTable.prototype.destroyCache = function (oArray) {
	var l = oArray.length;
	for (var i = 0; i < l; i++) {
		oArray[i].value = null;
		oArray[i].element = null;
		oArray[i] = null;
	}
};

JetsenWeb.SortableTable.prototype.getRowValue = function (oRow, sType, nColumn) {
	// if we have defined a custom getRowValue use that
	if (this._sortTypeInfo[sType] && this._sortTypeInfo[sType].getRowValue)
		return this._sortTypeInfo[sType].getRowValue(oRow, nColumn);

	var s;
	var c = oRow.cells[nColumn];
	if(c==null) return "";
	if (typeof c.innerText != "undefined")
		s = c.innerText;
	else
		s = SortableTable.getInnerText(c);
	return this.getValueFromString(s, sType);
};

JetsenWeb.SortableTable.getInnerText = function (oNode) {
	var s = "";
	var cs = oNode.childNodes;
	var l = cs.length;
	for (var i = 0; i < l; i++) {
		switch (cs[i].nodeType) {
			case 1: //ELEMENT_NODE
				s += JetsenWeb.SortableTable.getInnerText(cs[i]);
				break;
			case 3:	//TEXT_NODE
				s += cs[i].nodeValue;
				break;
		}
	}
	return s;
};

JetsenWeb.SortableTable.prototype.getValueFromString = function (sText, sType) {
	if (this._sortTypeInfo[sType])
		return this._sortTypeInfo[sType].getValueFromString( sText );
	return sText;	
	};

JetsenWeb.SortableTable.prototype.getSortFunction = function (sType, nColumn) {
	if (this._sortTypeInfo[sType])
		return this._sortTypeInfo[sType].compare;
	return JetsenWeb.SortableTable.basicCompare;
};

JetsenWeb.SortableTable.prototype.destroy = function () {
	this.uninitHeader();
	var win = this.document.parentWindow;
	if (win && typeof win.detachEvent != "undefined") {	// only IE needs this
		win.detachEvent("onunload", this._onunload);
	}
	this._onunload = null;
	this.element = null;
	this.tHead = null;
	this.tBody = null;
	this.document = null;
	this._headerOnclick = null;
	this.sortTypes = null;
	this._asyncsort = null;
	this.onsort = null;
};

// Adds a sort type to all instance of SortableTable
// sType : String - the identifier of the sort type
// fGetValueFromString : function ( s : string ) : T - A function that takes a
//    string and casts it to a desired format. If left out the string is just
//    returned
// fCompareFunction : function ( n1 : T, n2 : T ) : Number - A normal JS sort
//    compare function. Takes two values and compares them. If left out less than,
//    <, compare is used
// fGetRowValue : function( oRow : HTMLTRElement, nColumn : int ) : T - A function
//    that takes the row and the column index and returns the value used to compare.
//    If left out then the innerText is first taken for the cell and then the
//    fGetValueFromString is used to convert that string the desired value and type

JetsenWeb.SortableTable.prototype.addSortType = function (sType, fGetValueFromString, fCompareFunction, fGetRowValue) {
	this._sortTypeInfo[sType] = {
		type:				sType,
		getValueFromString:	fGetValueFromString || JetsenWeb.SortableTable.idFunction,
		compare:			fCompareFunction || JetsenWeb.SortableTable.basicCompare,
		getRowValue:		fGetRowValue
	};
};

// this removes the sort type from all instances of SortableTable
JetsenWeb.SortableTable.prototype.removeSortType = function (sType) {
	delete this._sortTypeInfo[sType];
};

JetsenWeb.SortableTable.basicCompare = function compare(n1, n2) {
    //return n1.value.localeCompare(n2.value);
	if (n1.value < n2.value)
		return -1;
	if (n2.value < n1.value)
		return 1;
	return 0;
};

JetsenWeb.SortableTable.idFunction = function (x) {
	return x;
};

JetsenWeb.SortableTable.toUpperCase = function (s) {
	return s.toUpperCase();
};

JetsenWeb.SortableTable.toDate = function (s) {
	var parts = s.split("-");
	var d = new Date(0);
	d.setFullYear(parts[0]);
	d.setDate(parts[2]);
	d.setMonth(parts[1] - 1);
	return d.valueOf();
};
// add sort types
JetsenWeb.SortableTable.prototype.addSortType("Number", Number);
JetsenWeb.SortableTable.prototype.addSortType("CaseInsensitiveString", JetsenWeb.SortableTable.toUpperCase);
JetsenWeb.SortableTable.prototype.addSortType("Date", JetsenWeb.SortableTable.toDate);
JetsenWeb.SortableTable.prototype.addSortType("String");
// None is a special case
