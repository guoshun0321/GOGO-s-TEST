/* lixiaomin 2008/07/15
//=============================================================================
// Jetsen UI Action
//=============================================================================
*/
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_action"));
//JetsenWeb.importCss("action");
//var _moveE = new JetsenWeb.UI.Action($('imgMove'));
//_moveE.addPoint({x:160,y:160});
//_moveE.addPoint({x:480,y:350});
//_moveE.addPoint({x:300,y:300});
//_moveE.addPoint({x:400,y:400});
//_moveE.addPoint({x:500,y:500});
//_moveE.addPoint({x:900,y:300});
//_moveE.breakPoints = [2];
//_moveE.speed = 100;
//_moveE.start();

JetsenWeb.UI.Action = function(/*element*/con,/*int*/speed)
{  
    this.__typeName = "JetsenWeb.UI.Action";       
    this.speed = speed?speed:5;
    this.points = new Array(); 
    this.control = con;  
    this.control.style.position = "absolute"; 
    this.control.style.zIndex = 100000000;
    this.control.style.display = "";   
    this.currentPointIndex = 0;
    this.repeartAction = true;
    this.Smoothing = true;  
    this.breakPoints = [];
    
    this.moving = false;
};

JetsenWeb.UI.Action.prototype.addPoint = function(p)
{
    this.points.push(p);
};
JetsenWeb.UI.Action.prototype.clear = function()
{
    this.moving = false;
    this.breakPoints = [];
    this.points = [];
};

JetsenWeb.UI.Action.prototype.stop = function()
{
    this.moving = false;
};
  
JetsenWeb.UI.Action.prototype.start = function(){
    
    if(this.points.length>0)
    {
        this.currentPointIndex = 0;
        this.control.style.top = this.points[0].y+"px";
        this.control.style.left = this.points[0].x+"px";
    }
    else
    {
        return;
    }
    this.moving = true;
    this.moveElement();   
 };
  
 JetsenWeb.UI.Action.prototype.moveElement = function()
 {
    if(!this.moving)
        return;
    var _owner = this;
    if(this.currentPointIndex >= this.points.length-1)
    {
        if(this.repeartAction)
        {
            this.start();           
        }
        else
        {
            this.moving = false;           
        }
        return;
    }
    else
    {
        for(var i=0;i<this.breakPoints.length;i++)
        {
            if(this.breakPoints[i] == this.currentPointIndex)
            {
                this.currentPointIndex++;
                if(this.currentPointIndex < this.points.length)
                {
                    this.control.style.top = this.points[this.currentPointIndex].y+"px";
                    this.control.style.left = this.points[this.currentPointIndex].x+"px";
                }
                this.moveElement();
                return;
            }
        }
    }
    var _point = this.points[this.currentPointIndex+1];
    var _x = parseInt(this.control.style.left);
    var _y = parseInt(this.control.style.top);   
    
    var __leny = Math.abs(_y-_point.y);
    var __lenx = Math.abs(_x-_point.x);
   
    if( __leny>this.speed || __lenx>this.speed)
    {                  
        //(Y-y1)/(y2-y1)=(X-x1)/(x2-x1) 
        if(__leny>__lenx)
        {       
            if(_y>_point.y)
                this.control.style.top = parseInt(_y-this.speed)+"px";
            else
                this.control.style.top = parseInt(_y+this.speed)+"px";
                  
            this.control.style.left = parseInt( (parseInt(this.control.style.top)-_point.y)*(this.points[this.currentPointIndex].x-_point.x)/(this.points[this.currentPointIndex].y-_point.y)+_point.x)+"px";
        }
        else
        {
            if(_x>_point.x)
                this.control.style.left = parseInt(_x-this.speed)+"px";
            else
                this.control.style.left = parseInt(_x+this.speed)+"px";
                
            this.control.style.top = parseInt( (parseInt(this.control.style.left)-_point.x)*(this.points[this.currentPointIndex].y-_point.y)/(this.points[this.currentPointIndex].x-_point.x)+_point.y )+"px";  
        }
        
        setTimeout(function(){_owner.moveElement();},50);  
        return;      
    }
    else
    {
        this.currentPointIndex++;        
        this.moveElement();
        return;
    }   
 };