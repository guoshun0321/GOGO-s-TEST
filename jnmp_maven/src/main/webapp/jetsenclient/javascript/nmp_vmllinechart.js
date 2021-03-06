
// lixiaomin 2008/08/13
//=============================================================================
// 监控视图
//=============================================================================

JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("nmp_vmllinechart"));

JetsenWeb.importCss("vmllinechart");

JetsenWeb.UI.VmlLineChart = function(container,width,height)
{
    this.container = container?container:document.body;
    this.container.innerHTML = "";    
    this.chartData = [];
    this.chartColors = ["green","red","blue","purple","maroon","navy","fuchsia","olive","teal","yellow","black"];
    this.maxDataCount = 100;
    this.margin = 25;
    this.charY = 80;
    this.descCharLen = 20;
    this.size = {width:width?width:this.container.offsetWidth,height:height?height:this.container.offsetHeight};
    this.onloaded = null;     
    
    this.xTitles = [];
    this.yTitles = [];
    this.xGridCount = 15;
    this.yGridCount = 10;    
    this.yMaxValue = 100;
    this.displayXTitleCount = 16;    
    this.__isInit = false;   
    this.BeginH = 0;
    this.BeginM = 0;
    this.PxTime = 5;
    this.MinY = 0;
    this.MaxY = 100;
    this.lgType = 0;
}
JetsenWeb.UI.VmlLineChart.prototype.init = function()
{
    if(this.__isInit)
        return;
    if(!JetsenWeb.isIE())
    {
        this.graphics = new jsGraphics(this.container.id);        
        this.graphics.setColor("#000000");               
    }
    
    this.chartSize = {width:this.size.width-this.margin*2-this.charY,height:this.size.height-this.margin*2-50};
    this.chartPositon = {top:this.margin,left:this.margin+this.charY,right:this.size.width-this.margin,bottom:this.chartSize.height+this.margin};
    this.descPostion = {left:this.margin+this.charY,top:this.chartPositon.bottom+30};
    
    //var _left = this.margin+this.charY;
    //var _top = this.margin;
    var _height = this.size.height;
    var _width  = this.size.width;
    //var _currentLeft = _left;
    //var _currentTop = _top;       
   
    this.container.onselectstart = function(e) { return false; }
    this.container.style.position = "absolute";
    this.container.style.width = (_width)+"px";   
    this.container.style.height = (_height)+"px";
    
    this.__isInit = true;
}

JetsenWeb.UI.VmlLineChart.prototype.dispose = function()
{
    var _len = this.container.childNodes.length;
    while(_len>0)
    {   
        _len--;
        this.container.removeChild(this.container.childNodes[0]);
    }
    this.container.innerHTML = "";
}
JetsenWeb.UI.VmlLineChart.prototype.render = function()
{       
    this.init();    
    
    this.renderBackGround();
    this.renderLeft();
    this.renderData();    
        
    if(this.graphics)
        this.graphics.paint();
   
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.VmlLineChart.prototype.renderY = function()
{       
    this.init();    
    
    this.renderBackGround();
    this.renderLeftY();
    this.renderData();    
        
    if(this.graphics)
        this.graphics.paint();
   
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.VmlLineChart.prototype.refresh = function()
{           
    if(this.graphics)
    {        
        this.graphics.clear();
    }
    var objSpan = this.container.getElementsByTagName("SPAN");
    var objCurve = this.container.getElementsByTagName("curve");
        
    var _len = objSpan.length;
    while(objSpan.length>0)
    {  
        this.container.removeChild(objSpan[0]);
        _len--;
    }
    _len = objCurve.length;
    while(_len>0)
    {  
        this.container.removeChild(objCurve[0]);
        _len--;
    }
    if(this.graphics)
    {        
        this.render();
        return;
    }    
    this.renderData();      
        
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.VmlLineChart.prototype.refreshY = function()
{           
    if(this.graphics)
    {        
        this.graphics.clear();
    }
    var objSpan = this.container.getElementsByTagName("SPAN");
    var objCurve = this.container.getElementsByTagName("curve");
    var objDiv = this.container.getElementsByTagName("DIV");
    
    var _len = objSpan.length;
    while(objSpan.length>0)
    {  
        this.container.removeChild(objSpan[0]);
        _len--;
    }
    _len = objCurve.length;
    while(_len>0)
    {  
        this.container.removeChild(objCurve[0]);
        _len--;
    }
    if(this.graphics)
    {        
        this.renderY();
        return;
    }
    _len = objDiv.length;
    while(_len>0)
    {  
        this.container.removeChild(objDiv[0]);
        _len--;
    }
    this.renderLeftY();
    this.renderData();      
        
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.VmlLineChart.prototype.refreshTime = function(/*JetsenWeb.Xml.toObject*/obj,/*string*/tagTime)
{
    var px = [];
    if(obj!=null && obj[tagTime]!=null)
    {            
        if(obj[tagTime].length==null)
            obj[tagTime] = [obj[tagTime]];
        px = obj[tagTime];
        this.BeginH = parseInt(px[0].H,10);
        this.BeginM = parseInt(px[0].M,10);
    }
}
JetsenWeb.UI.VmlLineChart.prototype.refreshPer = function(/*JetsenWeb.Xml.toObject*/obj,/*string*/tagMonitorData)
{
    this.chartData=[];
    if(obj!=null && obj[tagMonitorData]!=null)
    {            
        if(obj[tagMonitorData].length==null)
            obj[tagMonitorData] = [obj[tagMonitorData]]; 
        
        this.chartData=obj[tagMonitorData];
    }	   
    this.refresh();
}
JetsenWeb.UI.VmlLineChart.prototype.refreshZone = function(/*JetsenWeb.Xml.toObject*/obj,/*string*/tagZone,/*string*/tagMonitorData)
{
    this.MinY = 0;
    this.MaxY = 100;
    var _Zone = [];
    if(obj!=null && obj[tagZone]!=null)
    {            
        if(obj[tagZone].length==null)
            obj[tagZone] = [obj[tagZone]];
        _Zone = obj[tagZone];
        this.MinY = parseInt(_Zone[0].N,10);
        this.MaxY = parseInt(_Zone[0].X,10);
    }
    this.chartData=[];
    if(obj!=null && obj[tagMonitorData]!=null)
    {            
        if(obj[tagMonitorData].length==null)
            obj[tagMonitorData] = [obj[tagMonitorData]]; 
        
        this.chartData=obj[tagMonitorData];
    }	   
    this.refreshY();
}
JetsenWeb.UI.VmlLineChart.prototype._getPoint = function(subData, px)
{
    var p = [];       
        
    var _height = this.chartSize.height;
    
    var _currentCharData = subData.D;
    var _dLen = _currentCharData.length;
    var _iPxIndex = this.maxDataCount;
    var _iZone = this.MaxY - this.MinY;

    for(var i=0;i<_dLen && i<=this.maxDataCount;i++)
    {        
        var _sPxTxt = _currentCharData[i].V;
        if(_sPxTxt < 0)
        {
            _sPxTxt = 0;
        }
        var _iY = parseFloat(_sPxTxt) - this.MinY;
        if(this.MaxY != 100)
        {
            _iY = (_iY/_iZone)*100;
        }
        var _yValue = (_height-(_iY*0.01*_height));
        var _sTime = JetsenWeb.Util.left(_currentCharData[i].T, 5);
        var _sHour = JetsenWeb.Util.left(_sTime, 2);
        var _sMinute = JetsenWeb.Util.right(_sTime, 2);
        var _iHour = parseInt(_sHour,10);
        var _iMinute = parseInt(_sMinute,10);
        for(;_iPxIndex>=0;_iPxIndex--)
        {
            var _pointx = px[_iPxIndex];
            if(_pointx.hour == _iHour && _pointx.minute == _iMinute)
            {
                p.push({top:_yValue+this.chartPositon.top,left:_pointx.px,topTxt:_sPxTxt});
                break;
            }
        }
    }
    
    return p;
}

JetsenWeb.UI.VmlLineChart.prototype.renderTime = function()
{
    var p = [];
    var _width = this.chartSize.width;
    var _space = _width*1.0/this.maxDataCount;
    var _px = this.chartPositon.left;
    var _iHour = this.BeginH;
    var _iMinute = this.BeginM;
    for(var i=0;i<=this.maxDataCount;i++)
    {        
        //push
        p.push({hour:_iHour,minute:_iMinute,px:_px});
        //px text
        if((i % this.PxTime) == 0)
        {
            var _sHour = _iHour;
            if(_sHour < 10)
            {
	            _sHour = "0" + _sHour;
            }
            var _sMinute = _iMinute;
            if(_sMinute < 10)
            {
	            _sMinute = "0" + _sMinute;
            }
            if(!this.graphics)
            {
                var _xTitle = document.createElement("SPAN");
                _xTitle.className = "xtitle";
                _xTitle.innerHTML = (_sHour) + ":" + (_sMinute);
                _xTitle.style.left = (_px-15)+"px";
                _xTitle.style.top = this.chartPositon.bottom;
                this.container.appendChild(_xTitle);
            }
            else
            {
                this.graphics.setColor("#000000"); 
                this.graphics.drawString((_sHour) + ":" + (_sMinute),(_px-15),this.chartPositon.bottom);
            }
        }
        //px
        _px = _px+_space;
        //step
        _iMinute = _iMinute + 1;
        if(_iMinute >= 60)
        {
            _iMinute = 0;
            _iHour = _iHour + 1;
        }
        if(_iHour >= 24)
        {
            _iHour = 0;
        }
    }
    return p;
}

JetsenWeb.UI.VmlLineChart.prototype.renderData = function()
{
    var _owner = this;
    var dataStr = [];
    //desc 
    var _divDesc = document.createElement("SPAN");
    _divDesc.style.top = this.descPostion.top+"px";
    _divDesc.style.left = this.descPostion.left+"px";
    _divDesc.style.position = "absolute";    
    var _descStr = "";   
    //""
    this.container.appendChild(_divDesc);
    //px time
    var _px = this.renderTime();
    //px
    var iIndex = -1;
    for(var index=0;index<this.chartData.length;index++)
    {   
        if(this.chartData[index].SubData)
        {
            var _subDatas = this.chartData[index].SubData.length?this.chartData[index].SubData:[this.chartData[index].SubData];
            if(this.lgType == 1)
            {
                var bIsAllZero = true;
                for(var jj=0;jj<_subDatas.length;jj++)       
                {
                    var __points = this._getPoint(_subDatas[jj], _px);
                    var __pLen = __points.length;
                    for(var ii=1;ii<__pLen;ii++)
                    {
                        var point11 = __points[ii-1];
                        var sPyTxt = point11.topTxt;
                        if(sPyTxt > 0)
                        {
                            bIsAllZero = false;
                            break;
                        }
                    }
                    if(!bIsAllZero)
                    {
                        break;
                    }
                }
                if(bIsAllZero)
                {
                    continue;
                }
                iIndex++;
            }
            else
            {
                iIndex = index;
            }
            var _curColor = this.chartColors[iIndex];
            var _sDesc = JetsenWeb.Util.left(this.chartData[index].Desc,this.descCharLen);
            _descStr += "<span style='background:"+ _curColor+";width:15px;height:15px'>&nbsp;&nbsp;</span>&nbsp;"+_sDesc+"&nbsp;&nbsp;";             
            //分段
            for(var j=0;j<_subDatas.length;j++)       
            {
                var _points = this._getPoint(_subDatas[j], _px);
                var _pLen = _points.length;

                for(var i=1;i<_pLen;i++)
                {
                    var point1 = _points[i-1];
                    var point2 = _points[i];
                    if(point1.left<this.chartPositon.left)
                        break;
                    if(!this.graphics)
                    {
                        dataStr.push("<v:curve style=\"Z-INDEX:5;POSITION:absolute;\" from=\""+point1.left+"px,"+point1.top+"px\" to=\""+point2.left+"px,"+point2.top+"px\" strokecolor=\""+_curColor+"\"/>");
                    }
                    else
                    {
                        _owner.graphics.setColor(_curColor);                       
                        _owner.graphics.drawLine(parseInt(point1.left,10),parseInt(point1.top,10),parseInt(point2.left,10),parseInt(point2.top,10));
                    }                    
                }
            }
        }  
    }
    
    _divDesc.innerHTML = _descStr;
    if(!this.graphics)
        this.container.innerHTML = this.container.innerHTML + dataStr.join("");
}
JetsenWeb.UI.VmlLineChart.prototype.padLeft = function(str,len,left)
{
    var _str = new String(str);
    var _iLen = _str.length;
    var _iLeft = left;
    if(_iLen < len)
    {
        _iLeft = _iLeft + 7*(len - _iLen);
    }
    return _iLeft;    
}
JetsenWeb.UI.VmlLineChart.prototype.renderLeft = function()
{
    var _len = this.yTitles.length;
    if(_len==0)
        return;
    var _iLeftPos = this.chartPositon.left-this.charY;
    var _height = this.chartSize.height;
    for(var i=0;i<_len;i++)
    {
        var _top = this.yTitles[i]*1.0/this.yMaxValue*_height;
        _top = _top>_height?this.chartPositon.top-10:_height+this.chartPositon.top-_top-10;
        if(!this.graphics)
        {
            var _yTitle = document.createElement("DIV");
            _yTitle.className = "ytitle";
            _yTitle.innerHTML = this.yTitles[i];
            _yTitle.style.left = this.padLeft(this.yTitles[i],11,_iLeftPos)+"px";            
            _yTitle.style.top = _top+"px";
            this.container.appendChild(_yTitle);
        }
        else
        {
            this.graphics.setColor("#000000");
            this.graphics.drawString(this.yTitles[i],this.padLeft(this.yTitles[i],11,_iLeftPos),_top);  
        }
    }
}
JetsenWeb.UI.VmlLineChart.prototype.renderLeftY = function()
{
    var _iMin = this.MinY;
    var _iMax = this.MaxY;
    var _iZone = _iMax - _iMin;
    if(_iZone < 100)
    {
        _iMin = parseInt(_iMin - (100 - _iZone) / 2);
        if(_iMin < 0)
        {
            _iMin = 0;
        }
        _iMax = 100 + _iMin;
    }
    _iZone = _iMax - _iMin;
    this.MinY = _iMin;
    this.MaxY = _iMax;
    
    var _iSpace = parseInt(_iZone / 10, 10);
    if((_iZone % 10) != 0)
    {
        _iSpace = _iSpace + 1;
        this.MaxY = this.MinY + _iSpace * 10;
    }
    var _iLeftY = this.MinY;
    var _iLeftPos = this.chartPositon.left-this.charY;
    var _height = this.chartSize.height;
    for(var i=0;i<=10;i++)
    {
        var _top = this.yTitles[i]*1.0/this.yMaxValue*_height;
        _top = _top>_height?this.chartPositon.top-10:_height+this.chartPositon.top-_top-10;
        if(!this.graphics)
        {
            var _yTitle = document.createElement("DIV");
            _yTitle.className = "ytitle";        
            _yTitle.innerHTML = _iLeftY;
            _yTitle.style.left = this.padLeft(_iLeftY,11,_iLeftPos)+"px";            
            _yTitle.style.top = _top+"px";
            this.container.appendChild(_yTitle);
        }
        else
        {
            this.graphics.setColor("#000000");
            this.graphics.drawString(_iLeftY,this.padLeft(_iLeftY,11,_iLeftPos),_top);
        }
        _iLeftY = _iLeftY + _iSpace;
    }
}
JetsenWeb.UI.VmlLineChart.prototype.renderBottom = function()
{
    var _len = this.xTitles.length;
    if(_len==0)
        return;
    var _singleLen = parseInt(this.chartSize.width*1.0/(_len-1),10);
    var _width = this.chartPositon.left;
    var _height = this.chartPositon.bottom;
    for(var i=0;i<_len;i++)
    {
        if(i!=0)
        {
            if(!this.graphics)
            {
                var _xTitle = document.createElement("DIV");
                _xTitle.className = "xtitle";
                _xTitle.innerHTML = this.xTitles[i];        
                _xTitle.style.left = _width-10;
                _xTitle.style.top = _height;
                this.container.appendChild(_xTitle);
            }
            else
            {
                this.graphics.drawString(this.xTitles[i],_width-10,_height);
            }
        }
        _width = _width+_singleLen;
    }
}
JetsenWeb.UI.VmlLineChart.prototype.renderBackGround = function()
{
    if(this.graphics)
        this.graphics.setColor("#999999");
    var _bgHTML = "";
    var _xlen = this.xGridCount;
    var _ylen = this.yGridCount;
    if(_xlen==0 || _ylen==0)
        return;
    var _xSingleLen = this.chartSize.width*1.0/_xlen;
    var _ySingleLen = this.chartSize.height*1.0/_ylen;
    var _width = this.chartPositon.left;
    var _height = this.chartPositon.bottom;
    for(var i=0;i<=_xlen;i++)
    {        
        if(!this.graphics)
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+_width+","+this.chartPositon.top+"\" to=\""+_width+","+_height+"\" "+((i==0)?"strokeweight=\"2px\"  strokecolor=\"black\" ":" strokecolor=\"silver\" ")+"/>";
        else
        {
            if(i==0)
                this.graphics.setStroke(2);
            else
                this.graphics.setStroke(1);
            this.graphics.drawLine(_width,this.chartPositon.top,_width,_height);
        }
        _width = _width+_xSingleLen;
    }    
    
    _width = this.chartPositon.right;
    _height = this.chartPositon.bottom;
    for(var i=0;i<=_ylen;i++)
    {       
        if(!this.graphics) 
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+this.chartPositon.left+","+_height+"\" to=\""+_width+","+_height+"\" "+((i==0)?"strokeweight=\"2px\" strokecolor=\"black\" ":" strokecolor=\"silver\" ")+"/>";
        else
        {
            if(i==0)
                this.graphics.setStroke(2);
            else
                this.graphics.setStroke(1);
            this.graphics.drawLine(this.chartPositon.left,_height,_width,_height);    
        }
        _height = _height-_ySingleLen;
    }
    if(!this.graphics)
        this.container.innerHTML = _bgHTML;
}

JetsenWeb.UI.Vml3DHistogram = function(container,width,height)
{
    this.container = container?container:document.body;
    this.container.innerHTML = "";    
    this.chartData = [];
    this.chartColors = ["green","red","blue","purple","maroon","navy","fuchsia","olive","teal","yellow","black"];
    this.maxDataCount = 100;
    this.margin = 25;
    this.charY = 80;
    this.descCharLen = 20;
    this.size = {width:width?width:this.container.offsetWidth,height:height?height:this.container.offsetHeight};
    this.onloaded = null;     
    
    this.xTitles = [];
    this.yTitles = [];
    this.xGridCount = 15;
    this.yGridCount = 10;    
    this.yMaxValue = 100;
    this.displayXTitleCount = 16;    
    this.__isInit = false;   
    this.BeginH = 0;
    this.BeginM = 0;
    this.PxTime = 5;
    this.MinY = 0;
    this.MaxY = 100;
    this.lg3DType = 1;
}
JetsenWeb.UI.Vml3DHistogram.prototype.init = function()
{
    if(this.__isInit)
        return;
    if(!JetsenWeb.isIE())
    {
        this.graphics = new jsGraphics(this.container.id);        
        this.graphics.setColor("#000000");               
    }
    
    this.chartSize = {width:this.size.width-this.margin*2-this.charY,height:this.size.height-this.margin*2-50};
    this.chartPositon = {top:this.margin,left:this.margin+this.charY,right:this.size.width-this.margin,bottom:this.chartSize.height+this.margin};
    this.descPostion = {left:this.margin+this.charY,top:this.chartPositon.bottom+30};
    
    //var _left = this.margin+this.charY;
    //var _top = this.margin;
    var _height = this.size.height;
    var _width  = this.size.width;
    //var _currentLeft = _left;
    //var _currentTop = _top;       
   
    this.container.onselectstart = function(e) { return false; }
    this.container.style.position = "absolute";
    this.container.style.width = (_width)+"px";   
    this.container.style.height = (_height)+"px";
    
    this.__isInit = true;
}

JetsenWeb.UI.Vml3DHistogram.prototype.dispose = function()
{
    var _len = this.container.childNodes.length;
    while(_len>0)
    {   
        _len--;
        this.container.removeChild(this.container.childNodes[0]);
    }
    this.container.innerHTML = "";
}
JetsenWeb.UI.Vml3DHistogram.prototype.render = function()
{       
    this.init();    
    
    this.renderBackGround();
    this.renderLeft();
    this.renderData();    
        
    if(this.graphics)
        this.graphics.paint();
   
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.Vml3DHistogram.prototype.refresh = function()
{           
    if(this.graphics)
    {        
        this.graphics.clear();
    }
    var objSpan = this.container.getElementsByTagName("SPAN");
    var objCurve = this.container.getElementsByTagName("rect");
        
    var _len = objSpan.length;
    while(objSpan.length>0)
    {  
        this.container.removeChild(objSpan[0]);
        _len--;
    }
    _len = objCurve.length;
    while(_len>0)
    {  
        this.container.removeChild(objCurve[0]);
        _len--;
    }
    if(this.graphics)
    {        
        this.render();
        return;
    }    
    this.renderData();      
        
    if(this.onloaded && typeof this.onloaded == "function")
    {
        this.onloaded();
    }
}
JetsenWeb.UI.Vml3DHistogram.prototype.refreshPer = function(/*JetsenWeb.Xml.toObject*/obj,/*string*/tagMonitorData)
{
    this.chartData=[];
    if(obj!=null && obj[tagMonitorData]!=null)
    {            
        if(obj[tagMonitorData].length==null)
            obj[tagMonitorData] = [obj[tagMonitorData]]; 
        
        this.chartData=obj[tagMonitorData];
    }	   
    this.refresh();
}
JetsenWeb.UI.Vml3DHistogram.prototype._getPoint = function(subData)
{
    var p = [];       
        
    var _height = this.chartSize.height;
    
    var _currentCharData = subData.D;
    var _dLen = _currentCharData.length;
    var _iZone = this.MaxY - this.MinY;

    for(var i=0;i<_dLen && i<=this.maxDataCount;i++)
    {        
        var _sPxTxt = _currentCharData[i].V;
        if(_sPxTxt < 0)
        {
            _sPxTxt = 0;
        }
        var _iY = parseFloat(_sPxTxt) - this.MinY;
        if(this.MaxY != 100)
        {
            _iY = (_iY/_iZone)*100;
        }
        var _yValue = (_height-(_iY*0.01*_height));
        p.push({top:_yValue+this.chartPositon.top,topTxt:_sPxTxt});
    }
    
    return p;
}

JetsenWeb.UI.Vml3DHistogram.prototype.renderData = function()
{
    var _owner = this;
    var dataStr = [];
    //desc 
    var _divDesc = document.createElement("SPAN");
    _divDesc.style.top = this.descPostion.top+"px";
    _divDesc.style.left = this.descPostion.left+"px";
    _divDesc.style.position = "absolute";    
    var _descStr = "";   
    //""
    this.container.appendChild(_divDesc);
    //px
    var iIndex = -1;
    for(var index=0;index<this.chartData.length;index++)
    {   
        if(this.chartData[index].SubData)
        {
            var _subDatas = this.chartData[index].SubData.length?this.chartData[index].SubData:[this.chartData[index].SubData];
            if(_subDatas.length <= 0)
            {
                continue;
            }
            var _points = this._getPoint(_subDatas[0]);
            var point1 = _points[0];
            var point2 = _points[0];
            var sPyTxt = point1.topTxt;
            if(this.lg3DType == 1)
            {
                if(sPyTxt <= 0)
                {
                    continue;
                }
                iIndex++;
            }
            else
            {
                iIndex = index;
            }
            var _curColor = this.chartColors[iIndex];
            _descStr += "<span style='background:"+ _curColor+";width:15px;height:15px'>&nbsp;&nbsp;</span>&nbsp;"+  JetsenWeb.Util.left(this.chartData[index].Desc,this.descCharLen)+"&nbsp;&nbsp;";             
            var iLeft = this.chartPositon.left + 40 + iIndex*70;
            var iTop = point1.top;
            var iHeight = this.chartPositon.bottom-iTop;
            var iTop2 = this.chartPositon.top;
            var iHeight2 = iTop - iTop2;
            var sPyTxt2 = 100 - point1.topTxt;
            if(!this.graphics)
            {
                if(sPyTxt2 >= 15)
                {
                    dataStr.push("<v:rect fillcolor='#09f' style='Z-INDEX: 5; POSITION:absolute; LEFT: "+iLeft+"px; WIDTH: 30px; TOP: "+iTop2+"px; HEIGHT: "+iHeight2+"px;' title='"+sPyTxt2+"'>"+"<FONT style='COLOR: black; FONT-FAMILY: verdana;'><br>&nbsp;"+sPyTxt2+"%<br></FONT>"+"<v:Extrusion backdepth='20pt' on='true'/></v:rect>");
                }
                else if(sPyTxt2 > 0)
                {
                    dataStr.push("<v:rect fillcolor='#09f' style='Z-INDEX: 5; POSITION:absolute; LEFT: "+iLeft+"px; WIDTH: 30px; TOP: "+iTop2+"px; HEIGHT: "+iHeight2+"px;' title='"+sPyTxt2+"'>"+"<v:Extrusion backdepth='20pt' on='true'/></v:rect>");
                }
                if(sPyTxt >= 15)
                {
                    dataStr.push("<v:rect fillcolor='"+_curColor+"' style='Z-INDEX: 5; POSITION:absolute; LEFT: "+iLeft+"px; WIDTH: 30px; TOP: "+iTop+"px; HEIGHT: "+iHeight+"px;' title='"+sPyTxt+"'>"+"<FONT style='COLOR: white; FONT-FAMILY: verdana;'><br>&nbsp;"+sPyTxt+"%<br></FONT>"+"<v:Extrusion backdepth='20pt' on='true'/></v:rect>");
                }
                else if(sPyTxt > 0)
                {
                    dataStr.push("<v:rect fillcolor='"+_curColor+"' style='Z-INDEX: 5; POSITION:absolute; LEFT: "+iLeft+"px; WIDTH: 30px; TOP: "+iTop+"px; HEIGHT: "+iHeight+"px;' title='"+sPyTxt+"'>"+"<v:Extrusion backdepth='20pt' on='true'/></v:rect>");
                }
            }
            else
            {
                _owner.graphics.setColor(_curColor);                       
                _owner.graphics.drawLine(parseInt(point1.left,10),parseInt(point1.top,10),parseInt(point2.left,10),parseInt(point2.top,10));
            }                    
        }  
    }
    
    _divDesc.innerHTML = _descStr;
    if(!this.graphics)
        this.container.innerHTML = this.container.innerHTML + dataStr.join("");
}
JetsenWeb.UI.Vml3DHistogram.prototype.padLeft = function(str,len,left)
{
    var _str = new String(str);
    var _iLen = _str.length;
    var _iLeft = left;
    if(_iLen < len)
    {
        _iLeft = _iLeft + 7*(len - _iLen);
    }
    return _iLeft;    
}
JetsenWeb.UI.Vml3DHistogram.prototype.renderLeft = function()
{
    var _len = this.yTitles.length;
    if(_len==0)
        return;
    var _iLeftPos = this.chartPositon.left-this.charY;
    var _height = this.chartSize.height;
    for(var i=0;i<_len;i++)
    {
        var _top = this.yTitles[i]*1.0/this.yMaxValue*_height;
        _top = _top>_height?this.chartPositon.top-10:_height+this.chartPositon.top-_top-10;
        if(!this.graphics)
        {
            var _yTitle = document.createElement("DIV");
            _yTitle.className = "ytitle";
            _yTitle.innerHTML = this.yTitles[i];
            _yTitle.style.left = this.padLeft(this.yTitles[i],11,_iLeftPos)+"px";            
            _yTitle.style.top = _top+"px";
            this.container.appendChild(_yTitle);
        }
        else
        {
            this.graphics.setColor("#000000");
            this.graphics.drawString(this.yTitles[i],this.padLeft(this.yTitles[i],11,_iLeftPos),_top);  
        }
    }
}
JetsenWeb.UI.Vml3DHistogram.prototype.renderLeftY = function()
{
    var _iMin = this.MinY;
    var _iMax = this.MaxY;
    var _iZone = _iMax - _iMin;
    if(_iZone < 100)
    {
        _iMin = parseInt(_iMin - (100 - _iZone) / 2);
        if(_iMin < 0)
        {
            _iMin = 0;
        }
        _iMax = 100 + _iMin;
    }
    _iZone = _iMax - _iMin;
    this.MinY = _iMin;
    this.MaxY = _iMax;
    
    var _iSpace = parseInt(_iZone / 10, 10);
    if((_iZone % 10) != 0)
    {
        _iSpace = _iSpace + 1;
        this.MaxY = this.MinY + _iSpace * 10;
    }
    var _iLeftY = this.MinY;
    var _iLeftPos = this.chartPositon.left-this.charY;
    var _height = this.chartSize.height;
    for(var i=0;i<=10;i++)
    {
        var _top = this.yTitles[i]*1.0/this.yMaxValue*_height;
        _top = _top>_height?this.chartPositon.top-10:_height+this.chartPositon.top-_top-10;
        if(!this.graphics)
        {
            var _yTitle = document.createElement("DIV");
            _yTitle.className = "ytitle";        
            _yTitle.innerHTML = _iLeftY;
            _yTitle.style.left = this.padLeft(_iLeftY,11,_iLeftPos)+"px";            
            _yTitle.style.top = _top+"px";
            this.container.appendChild(_yTitle);
        }
        else
        {
            this.graphics.setColor("#000000");
            this.graphics.drawString(_iLeftY,this.padLeft(_iLeftY,11,_iLeftPos),_top);
        }
        _iLeftY = _iLeftY + _iSpace;
    }
}
JetsenWeb.UI.Vml3DHistogram.prototype.renderBottom = function()
{
    var _len = this.xTitles.length;
    if(_len==0)
        return;
    var _singleLen = parseInt(this.chartSize.width*1.0/(_len-1),10);
    var _width = this.chartPositon.left;
    var _height = this.chartPositon.bottom;
    for(var i=0;i<_len;i++)
    {
        if(i!=0)
        {
            if(!this.graphics)
            {
                var _xTitle = document.createElement("DIV");
                _xTitle.className = "xtitle";
                _xTitle.innerHTML = this.xTitles[i];        
                _xTitle.style.left = _width-10;
                _xTitle.style.top = _height;
                this.container.appendChild(_xTitle);
            }
            else
            {
                this.graphics.drawString(this.xTitles[i],_width-10,_height);
            }
        }
        _width = _width+_singleLen;
    }
}
JetsenWeb.UI.Vml3DHistogram.prototype.renderBackGround = function()
{
    if(this.graphics)
        this.graphics.setColor("#999999");
    var _bgHTML = "";
    var _xlen = this.xGridCount;
    var _ylen = this.yGridCount;
    if(_xlen==0 || _ylen==0)
        return;
    var _xSingleLen = this.chartSize.width*1.0/_xlen;
    var _ySingleLen = this.chartSize.height*1.0/_ylen;
    
    var _width = this.chartPositon.right;
    var _height = this.chartPositon.bottom - 10;
    var iFromX = this.chartPositon.left + 10;
    var iToX = this.chartPositon.right + 10;
    for(var i=0;i<=_ylen;i++)
    {       
        if(!this.graphics)
        { 
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+(iFromX-10)+","+(_height+10)+"\" to=\""+iFromX+","+_height+"\" strokecolor=\"#09f\" />";
            if(i==0)
            {
                _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+(iFromX-10)+","+(_height+10)+"\" to=\""+(iToX-10)+","+(_height+10)+"\" strokeweight=\"2px\" strokecolor=\"black\" />";
                _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+iFromX+","+_height+"\" to=\""+iToX+","+_height+"\" strokecolor=\"#09f\" />";
            }
            else
            {
                _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+iFromX+","+_height+"\" to=\""+iToX+","+_height+"\" strokecolor=\"#09f\" />";
            }
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+(iToX-10)+","+(_height+10)+"\" to=\""+iToX+","+_height+"\" strokecolor=\"#09f\" />";
        }
        else
        {
            if(i==0)
                this.graphics.setStroke(2);
            else
                this.graphics.setStroke(1);
            this.graphics.drawLine(this.chartPositon.left,_height,_width,_height);    
        }
        _height = _height-_ySingleLen;
    }
    
    _width = this.chartPositon.left;
    _height = this.chartPositon.bottom;
    var iFromY = this.chartPositon.top;
    var iToY = this.chartPositon.bottom;
    for(var i=0;i<2;i++)
    {        
        if(!this.graphics)
        {
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+_width+","+iFromY+"\" to=\""+_width+","+iToY+"\" strokeweight=\"2px\"  strokecolor=\"black\" />";
            _bgHTML +="<v:line style=\"Z-INDEX:1;POSITION:absolute;\" from=\""+(_width+10)+","+(iFromY-10)+"\" to=\""+(_width+10)+","+(iToY-10)+"\" strokecolor=\"#09f\" />";
        }
        else
        {
            if(i==0)
                this.graphics.setStroke(2);
            else
                this.graphics.setStroke(1);
            this.graphics.drawLine(_width,this.chartPositon.top,_width,_height);
        }
        _width = _width+_xlen*_xSingleLen;
    } 
    
    if(!this.graphics)
        this.container.innerHTML = _bgHTML;
}