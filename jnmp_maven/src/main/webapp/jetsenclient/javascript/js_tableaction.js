
// lixiaomin 2008/09/03
//=============================================================================
// 表格操作
//=============================================================================

JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_tableaction"));

function Frame_InsertTableColumnAfter(ftbName) {Frame_InsertColumn(ftbName,true);}
function Frame_InsertTableColumnBefore(ftbName) {Frame_InsertColumn(ftbName,false);}
function Frame_InsertTableRowAfter(ftbName) {Frame_InsertTableRow(ftbName,true);}
function Frame_InsertTableRowBefore(ftbName) {Frame_InsertTableRow(ftbName,false);}
function Frame_InsertTableCell(ftbName){var editor = Frame_GetIFrame(ftbName);	var td = Frame_GetClosest(ftbName,"td");if(!td){return;}var otd = editor.document.createElement("td");tr = td.parentNode;	var ref = td.nextSibling;tr.insertBefore(otd, ref);}
function Frame_DeleteTableColumn(ftbName) { var td = this.Frame_GetClosest(ftbName,"td");	if (!td) {return;}	var table = td.parentNode.parentNode;var index = td.cellIndex;	Frame_SelectNextNode(td);	var rows = table.rows;	for (var i = rows.length; --i >= 0;)	{	var tr = rows[i];if(tr.cells[index]!=null) tr.removeChild(tr.cells[index]);	if(tr.cells[length]==0) table.removeChild(tr);	}	if(table!=null) if(table["rows"]["length"]==0)table["parentNode"].removeChild(table) ;}
function Frame_DeleteTableRow(ftbName) {var tr = Frame_GetClosest(ftbName,"tr");if (!tr) {return;}var par = tr.parentNode;Frame_SelectNextNode(tr);par.removeChild(tr);if(par!=null) 	if(par["rows"]["length"]==0) par["parentNode"].removeChild(par) ;}	
function Frame_ClearRow(tr) {var tds = tr.getElementsByTagName("td");for (var i = tds.length; --i >= 0;) {var td = tds[i];td.rowSpan = 1;td.innerHTML = (isIE) ? "" : "<br />";	}}
function Frame_DeleteTableCell(ftbName){var Ox60;var table;var Oxca;  Ox60=Frame_GetClosest(ftbName,"TD") ;if (!Ox60) {return;} Oxca=Ox60.parentNode; table=Oxca.parentNode; if(table!=null){if(Oxca["cells"]["length"]<=0x1){ table.deleteRow(Oxca.rowIndex) ; if(table["rows"]["length"]==0x0){ table["parentNode"].removeChild(table) ;} ;}  else { Oxca.deleteCell(Ox60.cellIndex) ;} ;} ;}  ;
function Frame_SplitRow(td) {	var n = parseInt("" + td.rowSpan);	var nc = parseInt("" + td.colSpan);
	td.rowSpan = 1;	tr = td.parentNode;	var itr = tr.rowIndex;	var trs = tr.parentNode.rows;	var index = td.cellIndex;
	while (--n > 0) {tr = trs[++itr];var otd = editor._doc.createElement("td");	otd.colSpan = td.colSpan;tr.insertBefore(otd, tr.cells[index]);
	}//editor.forceRedraw();editor.updateToolbar();
}
function Frame_SplitCol(td) {	var nc = parseInt("" + td.colSpan);	td.colSpan = 1;	tr = td.parentNode;	var ref = td.nextSibling;
	while (--nc > 0) {	var otd = document.createElement("td");tr.insertBefore(otd, ref);	}
	//editor.forceRedraw();editor.updateToolbar();
}
function Frame_SplitTableCell(ftbName)
{
	var editor = Frame_GetIFrame(ftbName);
	var td = Frame_GetClosest(ftbName,"td");
	if (!td) {	return;	}
	var nc = parseInt("" + td.colSpan);	tr = td.parentNode;	var ref = td.nextSibling;
	if (--nc > 0) {	var otd = editor.document.createElement("td");otd.rowSpan=td.rowSpan;tr.insertBefore(otd, ref);td.colSpan = nc;	}
}
function Frame_SplitTableRow(ftbName)
{
	var editor = Frame_GetIFrame(ftbName);
	var td = Frame_GetClosest(ftbName,"td");
	if (!td) {	return;	}
	var n = parseInt("" + td.rowSpan);	var nc = parseInt("" + td.colSpan);	
	tr = td.parentNode;	
	var rindex = tr.rowIndex;
	var ref = tr.parentNode.rows[rindex+n-1]
	if(ref==null) return;
	var index = td.cellIndex;
	var items = ref.cells;
	while(items[index]==null)
	{ if(items.length>0){index = items.length-1;}else{return ;}
	}
	if (--n > 0) {var otd = editor.document.createElement("td");	otd.colSpan = td.colSpan;ref.insertBefore(otd, items[index]);td.rowSpan =n}
}
function Frame_MergeRight(ftbName)
{
	//ExecCommand_MergeRight(ftbName);return;
	var td = Frame_GetClosest(ftbName,"td");
	if (!td) {	return;	}	
	var ref = td.nextSibling;
	if(ref==null) return;
	td.innerHTML = td.innerHTML+ref.innerHTML;
	td.colSpan = td.colSpan+ref.colSpan;
	td.parentNode.removeChild(ref);	
}
function Frame_MergeBottom(ftbName)
{	
	var td = Frame_GetClosest(ftbName,"td");
	if (!td) {	return;	}	
	var index = td.cellIndex;
	tr = td.parentNode;	
	var rindex = tr.rowIndex;
	var span = td.rowSpan;	
	var ref = tr.parentNode.rows[rindex+span]
	if(ref==null) return;
	var items = ref.cells;
	index = CreateTableArrayData(ftbName,rindex,index,rindex+span);
	if(index==null) return;
	while(items[index]==null)
	{ if(items.length>0){index = items.length-1;}else{return ;}	}
	td.innerHTML = td.innerHTML+items[index].innerHTML;
	td.rowSpan = span+items[index].rowSpan;ref.removeChild(items[index]); //if(ref.cells.length==0) ref.parentNode.removeChild(ref);
}
function Frame_SplitCell(td) {	var nc = parseInt("" + td.colSpan);	Frame_SplitCol(td);	var items = td.parentNode.cells;	var index = td.cellIndex;	while (nc-- > 0) {	Frame_SplitRow(items[index++]);	}}
function Frame_InsertColumn(ftbName,after) {  editor = Frame_GetIFrame(ftbName);   var td = Frame_GetClosest(ftbName,"td");
   if (!td) {   return; }
   var rows = td.parentNode.parentNode.rows;  var index = td.cellIndex;
   for (var i = rows.length; --i >= 0;) {
      var tr = rows[i];      var otd = editor.document.createElement("td");
      otd.innerHTML = (isIE) ? "" : "<br />";
      //if last column and insert column after is select append child
      if (index==tr.cells.length-1 && after) {       tr.appendChild(otd);    } else {      var ref = tr.cells[index + ((after) ? 1 : 0)]; // 0 
         tr.insertBefore(otd, ref);     } 
   }
}
function Frame_InsertTableRow(ftbName,after) {/* if (Frame_IsHtmlMode(ftbName)) return;*/	var tr = Frame_GetClosest(ftbName,"tr");
	if (!tr) {	return;	}	var otr = tr.cloneNode(true);	Frame_ClearRow(otr);	tr.parentNode.insertBefore(otr, ((after) ? tr.nextSibling : tr));}
function Frame_CreateTable(ftbName,cols,rows,width,widthUnit,align,cellpadding,cellspacing,border) {
	var editor = Frame_GetIFrame(ftbName);
	var sel = Frame_GetSelection(ftbName);
	var range = Frame_CreateRange(ftbName,sel);		
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
			if (!isIE) td.appendChild(doc.createElement("br"));
		}
	}	
	if (isIE) {	range.pasteHTML(table.outerHTML);	} else {Frame_InsertNodeAtSelection(ftbName,table);	}	
	return true;
}

function ExecCommand_MergeRight(ftbName){
var Ox60;var Oxca;var Ox2c9; Ox60=Frame_GetClosest(ftbName,"TD") ; Oxca=get_previous_object(Ox60,"TR") ;
if(Oxca!=null&&Ox60["cellIndex"]<Oxca["cells"]["length"]-0x1)
{ Ox2c9=Oxca["cells"][Ox60["cellIndex"]+0x1] ;
 Ox60["innerHTML"]+=Ox2c9["innerHTML"] ; 
Ox60["colSpan"]+=Ox2c9["colSpan"] ; Oxca.deleteCell(Ox60["cellIndex"]+0x1) ;} ;}  ; 

function ExecCommand_SplitHor(){var Ox84=editwin.getSelection();var Ox179=Ox84.getRangeAt(0x0);var Ox2bd=Ox179["startContainer"];var Ox60;var Oxca; Ox60=get_previous_object(Ox2bd.parentNode,"TD") ; Oxca=get_previous_object(Ox60,"TR") ;if(Oxca!=null&&Ox60["colSpan"]>0x1){ Ox60["colSpan"]-- ; Oxca.insertCell(Ox60["cellIndex"]+0x1) ;var Ox2c7=Oxca["cells"][Ox60["cellIndex"]+0x1]; Ox2c7["innerHTML"]=OxO434f[0xae] ;var Ox83=Oxca["cells"][Ox60["cellIndex"]][OxO434f[0x48]]; Ox2c7.setAttribute(OxO434f[0x48],Ox83) ;} ;}  ; 
function ExecCommand_SplitVer(){
var Ox84=editwin.document.selection;var Ox179=Ox84.getRangeAt(0x0);var Ox2bd=Ox179["startContainer"];
var Ox60;var Oxca;var Ox2c9; Ox60=get_previous_object(Ox2bd.parentNode,"TD") ;
 Oxca=get_previous_object(Ox60,"TR") ; table=get_previous_object(Ox60,"TABLE") ;
 if(table!=null&&Oxca!=null&&(Ox60["rowSpan"]>0x1)){ Ox60["rowSpan"]-- ;
 var Ox2ccOx2c7; Ox2cc=table["rows"][Oxca["rowIndex"]+0x1] ; Ox2cc.insertCell(Ox60.cellIndex) ; 
 Ox2c7=table["rows"][Oxca["rowIndex"]+0x1]["cells"][Ox60["cellIndex"]] ; Ox2c7["innerHTML"]=OxO434f[0xae] ;} ;}  ;
 
function ExecCommand_MergeBottom(ftbName)
{var Ox60;var Oxca;	var Ox2c9; Ox60=Frame_GetClosest(ftbName,"TD") ;
	Oxca=get_previous_object(Ox60,"TR") ; table=get_previous_object(Ox60,"TABLE") ;	if(table!=null&&Oxca!=null&&(table["rows"]["length"]-0x1>0x0)){
		var Ox2ccOx2c9; Ox2cc=table["rows"][Oxca["rowIndex"]+0x1] ; Ox2c9=table["rows"][Oxca["rowIndex"]+0x1]["cells"][Ox60["cellIndex"]] ;
		Ox60["innerHTML"]+=Ox2c9["innerHTML"] ; 	Ox60["rowSpan"]+=Ox2c9["rowSpan"] ;	jetsennet.alert(Ox60.cellIndex);
		table["rows"][Oxca["rowIndex"]+0x1].deleteCell(Ox60.cellIndex) ;} ;}  ;
		
function get_previous_object(obj,Ox2cf){if(obj){if(obj["tagName"]!=Ox2cf){ obj=get_previous_object(obj.parentNode,Ox2cf) ;} ;} ;return (obj);}  ;

function GetTableCellsMax(ftbName){	var tab = Frame_GetClosest(ftbName,"table");	var rows  = tab.rows.length;var tr;var Max=0;for(var i=0;i<rows;i++){	tr = tab.rows[i];	if(Max<tr.cells.length) Max=tr.cells.length;	}	return Max;	}
function CreateTableArrayData(ftbName,mergeR,mergeC,newMergeR)
{
	var tab = Frame_GetClosest(ftbName,"table");	var rows  = tab.rows.length;	var cells = GetTableCellsMax(ftbName)
	var arr=  new Array(rows*cells)	;		for(var ai=0;ai<rows*cells;ai++){	arr[ai]=0;	}	var tr;	var index;	var span;
	for(var r = 0;r<rows;r++)	{		tr = tab.rows[r];		for(var c=0;c<tr.cells.length;c++)		{			span = tr.cells[c].rowSpan;	index = c;	var cellindexlen=r*cells+index;			for(var inj=r*cells;inj<=cellindexlen;inj++){	if(arr[inj]==1)  cellindexlen++;	index = index +arr[inj];		}
			for(var i = r+1 ;i<span+r;i++)	{if(i<rows && index<cells)	arr[i*cells+index]=1;}		}	}	
	index = mergeC;	cellindexlen=mergeR*cells+mergeC;
	for(var mergej=mergeR*cells;mergej<=cellindexlen;mergej++){	if(arr[mergej]==1)  cellindexlen++;	index = index +arr[mergej];		}		
	for(var nmergej=newMergeR*cells;nmergej<=newMergeR*cells+index;nmergej++){	 	index = index -arr[nmergej];		}
	//jetsennet.alert(index);
	return index;	
	/*	var str="";	for(var wi=0;wi<rows*cells;wi++)	{			if((wi % cells)==0) str = str+"\n";			str = str+","+arr[wi];		}	jetsennet.alert(str);*/
}
function Frame_SetTableRow(ftbName)
{
    jetsennet.alert("sorry");
}
function Frame_SetTableCell(ftbName)
{
    jetsennet.alert("sorry");
}