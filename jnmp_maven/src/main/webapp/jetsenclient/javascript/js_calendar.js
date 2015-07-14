//copyright (c) by swanky
//mail to:swanky.wu@gmail.com
//msn message:svza@msn.com
//create 3.13.2006
//last modify 3.14.2006
//ver 0.1
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_calendar"));
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_datepicker"));
JetsenWeb.registerNamespace("JetsenWeb.UI.DatePicker");
JetsenWeb.importCss("cmpbase");

JetsenWeb.UI.DatePicker.popCalendar = function(control)
{
    SoCalendar.show(control);
};
var WEEKDAY_NAME = ["日","一","二","三","四","五","六"];
var MONTH_NAME = ["1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"];

_scGetSelectDate = function(sDate,sMonth,sYear){
  el("sc_0320").style.display = "none";
  var _year = sYear ? sYear : parseInt(el("scSelYear").value,10);
  var _month = sMonth ? sMonth : parseInt(el("scSelMonth").value,10);
  var _day = parseInt(sDate,10);
  var formatDay = _scFormatDay(_year,_month,_day);
  if( SoCalendar.input && typeof SoCalendar.input.value != "undefined" ){
    if(SoCalendar.input.value!=formatDay){
      SoCalendar.input.value = formatDay;
      SoCalendar.input.fireEvent("onchange");
      SoCalendar.onafterselecteddate(SoCalendar.input,formatDay);//event;
    }
    SoCalendar.input.focus();
  }
  event.cancelBubble = true;
  return false;
};

_scMakeCalendarHTML = function(){
  if(el("sc_0320") ) return;
  var _inner = '';
  if(IS_IE)
    _inner +=' <iframe border="0" frameBorder="0" style="position:absolute; visibility:inherit; top:0px; left:0px; width:180px; height:160px; z-index:-100;filter:Alpha(Opacity=0);"></iframe><div style="position:absolute; visibility:inherit; top:0px; left:0px; width:100%; height:100%; z-index:100;"></div>';
  _inner+='<div id="scTop">'
        + '<input id="scPrevMonth" name="scPrevMonth" class="scButton" onclick="_sc_pre_month_onclick();event.cancelBubble= true;" type="button" value="<">&nbsp;'
        + '<select id="scSelYear" name="scSelYear"  onchange="_sc_date_onchange();"></select>&nbsp;'
        + '<select id="scSelMonth" name="scSelMonth" onchange="_sc_date_onchange();"></select>&nbsp;'
        + '<input id="scNextMonth" name="scNextMonth" class="scButton" onclick="_sc_next_month_onclick();" type="button" value=">">'
        + '</div>'
        + '<div id="scCenter"></div>'
        + '<div id="scBottom">今天是: '
        + '<span id="scToday" onclick="_scGetSelectDate(this.innerHTML.split(\'-\')[2],this.innerHTML.split(\'-\')[1],this.innerHTML.split(\'-\')[0]);"><span>'
        + '</div>';
      
  var _div = document.createElement("div");
  _div.id = 'sc_0320';
  _div.innerHTML = _inner;
  _div.className = "scAll";
  document.body.appendChild(_div);  
  _div.onclick = function(){
    event.cancelBubble = true; return false;
  }
  
  var _arr = SoCalendar.currentSelectAllDate.split("-");
  var _cYear = parseInt(_arr[0],10);
  var _cMonth = parseInt(_arr[1],10);
  var _cDate = parseInt(_arr[2],10);
  
  var _objMonth = el("scSelMonth");
  for(var i=0;i<12;i++){
    var _option = document.createElement("option");
    _option.innerHTML = MONTH_NAME[i];
    _option.value = i+1;    
    if(_option.value==_cMonth)
      _option.selected = true;
    _objMonth.appendChild(_option);
  }
  
  var _objYear = el("scSelYear");  
  var _startYear = SoCalendar.year + 30;  
  var _endYear = 1900;  
  for(var i=_startYear;i>=_endYear;i--){
     var _option = document.createElement("option");
    _option.innerHTML = i;
    _option.value = i;    
     if(i==_cYear)
      _option.selected = true;
    _objYear.appendChild(_option);
  }
  
  el("scToday").innerHTML = SoCalendar.today;
  el("scToday").title = SoCalendar.today;
  if(SoCalendar.isAutoClose)
    document.attachEvent("onclick",_scCalendarClose); // attach close event  
};
_scMakeDayHTML = function(){
  _scMakeCalendarHTML();
  var _arr = SoCalendar.currentSelectAllDate.split("-");
  var _cYear = parseInt(_arr[0],10);
  var _cMonth = parseInt(_arr[1],10);
  var _cDate = parseInt(_arr[2],10);
  
  var _arr2 =  SoCalendar.inputValue.split("-");
  var _inputYear = parseInt(_arr2[0],10);
  var _inputMonth = parseInt(_arr2[1],10);
  var _inputDate = parseInt(_arr2[2],10); 
  
  el("scSelMonth").value = _cMonth;
  el("scSelYear").value = _cYear;
  
  var _thisMonthFirstDayWeek = _scGetWeekDayByDay(_cYear,_cMonth);
  
  var _dayCount = _scGetDayCountByYearMonth(_cYear,_cMonth);
  
  var _re = '<table border="0" cellspacing="0" cellpadding="0">'; 
  _re += "<tr>";
  for(i=0; i<7; i++)    
    _re += "<td class=\"scWeekCell\">" + WEEKDAY_NAME[i] + "</td>";
  _re +="</tr>";
  
  var j = 1;
  var _over = false;
  for (w = 1; w < 7; w++) {
    _re +="<tr>";
    for (d = 0; d < 7; d++) {     
      if((w==1 && d==_thisMonthFirstDayWeek) || (j>1 && j<=_dayCount)){
        var _class = "scCell";
        if(_inputDate==j && _cMonth==_inputMonth && _cYear==_inputYear)
          _class = "scCellSelected";   
        var _currentAllDate = _cYear + "-" + _cMonth + "-" + j;
        _re +='<td title="'+_currentAllDate+'" class="'+_class+'" onMouseOver="_sc_cell_mouseover(this);" onMouseOut="_sc_cell_mouseout(this);" onclick="return _scGetSelectDate(this.innerHTML);">';
        _re += j;        
        if(j==_dayCount)
          _over = true;
        j ++;
      }else{
        _re +='<td class=\"scNoDateCell\">';
      }  
      _re +="</td>"
    }
    _re +="</tr>";
    if(_over)
      break;
  }
  _re +="</table>";
  el("scCenter").innerHTML = _re;
};

_sc_cell_mouseover = function(sObj){
  sObj.className = sObj.className + " scCellOver";
};

_sc_cell_mouseout = function(sObj){
  sObj.className = sObj.className.split(" scCellOver")[0];
};

_sc_date_onchange = function(){
  var _year = el("scSelYear").value;
  var _month = el("scSelMonth").value;
  SoCalendar.currentSelectAllDate = _year + "-" + _month + "-" + "01";
  _scMakeDayHTML();  
};

_sc_next_month_onclick = function(){
  var _year = parseInt(el("scSelYear").value,10);
  var _month = parseInt(el("scSelMonth").value,10);
  if(_month!=12){
    _month =_month + 1;
  }else{
    _year = _year+ 1 ;
    _month = 1;
  }     
  SoCalendar.currentSelectAllDate = _year + "-" + _month + "-" + "01";
  _scMakeDayHTML();
};

_sc_pre_month_onclick = function(){
  var _year = parseInt(el("scSelYear").value,10);
  var _month = parseInt(el("scSelMonth").value,10);
  if(_month!=1){
    _month =_month - 1;
  }else{
    _year = _year - 1 ;
    _month = 12;
  }
  SoCalendar.currentSelectAllDate = _year + "-" + _month + "-" + "01";
  _scMakeDayHTML();   
};

_scGetWeekDayByDay = function(sYear,sMonth){
  var _first = new Date(sYear,(sMonth-1),1);
  return _first.getDay();
};

_scGetDayCountByYearMonth = function(sYear,sMonth){
  var _last = new Date(sYear,sMonth,0);//get current month's last date number;
  return _last.getDate();
};

_scFormatDay = function(sYear,sMonth,sDay){
  sYear +="";
  sMonth +="";
  sDay +="";
  if(sMonth.length==1)
    sMonth = "0"+sMonth;
  if(sDay.length==1)
   sDay = "0"+sDay;
  return sYear+"-"+sMonth+"-"+sDay;
};

_scCalendarClose = function(){
  var _allDiv = el("sc_0320");
  if(_allDiv&&_allDiv.style.display == ""){
   _allDiv.style.display = "none"; //setHTMLElementFadeOut("sc_0320",null,10); //_allDiv.style.display = "none"; hiden the object
  }  
};
_scCalendarShow = function(sInput){
  var _allDiv = el("sc_0320");
  if(_allDiv&&_allDiv.style.display != ""){
    _allDiv.style.display = "" ;//setHTMLElementFadeIn("sc_0320",null,10); // _allDiv.style.display = "block"; show the object
  }
  _scSetCalendarPosition(sInput); //set clendar position;  
};

_scSetCalendarPosition = function(sInput){
  var _allDiv = el("sc_0320");
  var _obj = JetsenWeb.Util.getPosition(sInput,1);
  var _viewSize = JetsenWeb.Util.getWindowViewSize();
  if(_obj.left+180>_viewSize.width)
  {
        _obj.left = _obj.left-180;
  }
  if(_obj.top+180>_viewSize.height)
  {
        _obj.top = _obj.top-180;
  }
 
  var l=0,t=0;
  if(IS_IE){   
    l=0;//11
    t=0  //14
  }
  _allDiv.style.left = _obj.left +l+ "px";
  _allDiv.style.top = _obj.top + t+ "px";
};

SoCalendar =function(){}
SoCalendar.show = function(sInput){
  
  if(typeof sInput == "string")
    sInput = el(sInput);  
  SoCalendar.inputValue = "";
  if(sInput)
    SoCalendar.inputValue = sInput.value;
  SoCalendar.today = new Date().toDateString();
  var _arr = SoCalendar.today.split("-");
  SoCalendar.year = parseInt(_arr[0],10);
  SoCalendar.month = parseInt(_arr[1],10);
  SoCalendar.day = parseInt(_arr[2],10);
  var _reDataTime = /^([1-9]\d{3}-((0?[1-9])|(1[0-2]))-((0[1-9])|([1-2]?\d)|(3[0-1])))?$/ 	//date ,can empty
  SoCalendar.currentSelectAllDate = (SoCalendar.inputValue!=""&&_reDataTime.test(SoCalendar.inputValue)) 
                                    ? SoCalendar.inputValue : SoCalendar.today;
  SoCalendar.input = sInput;
  _scMakeDayHTML(); //create day html
  _scCalendarShow(sInput); // set calendar is display ;  
  JetsenWeb.cancelEvent();
  return false;
}
SoCalendar.close = _scCalendarClose; //add 2006.5.23
SoCalendar.isAutoClose = true;
SoCalendar.replace = function(sInput){
  if(typeof sInput == "string")
    sInput = el(sInput);  
  var _span = document.createElement("span");
  _span.style.cssText = sInput.style.cssText;
  _span.className = sInput.className;
    
  var _parent = sInput.parentNode;
  _parent.insertBefore(_span,sInput);
  
  _span.appendChild(sInput);
  
  var _button = document.createElement("button");
  _button.innerHTML = "...";
  //_button.style.heigth = sInput.offsetHeight + "px";
  _button.onclick = function(){
    SoCalendar.show(sInput);
    event.cancelBubble = true;  return false;
  }
  _span.appendChild(_button);
  with(sInput){
    style.cssText = "width:"+(_span.offsetWidth-_button.offsetWidth-2)+"px;";
    className = "";
    if(maxLength!=10)
      maxLength = 10;
  }   
}

SoCalendar.onafterselecteddate = function(){}