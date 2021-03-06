//=============================================================================
// JetsenWeb.UI.Marquee;lixiaomin 2008-05-28 滚动控件
//=============================================================================	

JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_marquee"));

JetsenWeb.UI.Marquee = function(eControl,sContent){
    
    this.__typeName = "JetsenWeb.UI.Marquee";
    this.content = sContent;
    this.speed = 15;   
    this._isInit = false; 
    this.control = eControl;
    this.content1 = null;
    this.content2 = null;
    this.interval = null; 
    this.direction = 1;//0:DownToUp 1: RightToLeft
    var owner = this;
    this.marquee = function(){
        if(!owner._isInit) return;
        if(owner.direction == 0){
            var __sTop = parseInt(owner.control.scrollTop);
            if(owner.content2.offsetHeight-__sTop<=0){                
                owner.control.scrollTop = (__sTop - parseInt(owner.content1.offsetHeight));
            }
            else{
                owner.control.scrollTop = ( __sTop+1);
            }
        }else{
            var __sLeft = parseInt(owner.control.scrollLeft);
            if(owner.content2.offsetWidth-__sLeft<=0){
                owner.control.scrollLeft = (__sLeft - parseInt(owner.content1.offsetWidth));
            }
            else{ 
                owner.control.scrollLeft = (__sLeft +1);
            }                
        }
    }
}
JetsenWeb.UI.Marquee.prototype.init = function(){   
    var owner = this; 
    if(this.control==null)
        return;
    if(JetsenWeb.Util.isNullOrEmpty(this.content))
        return;
    if(JetsenWeb.Util.trim(this.control.innerHTML)==''|| this.content1 == null || this.content2 == null){
        this.control.innerHTML = "";        
        
        if(this.direction == 0){
            this.content1 = document.createElement("DIV");
            this.content2 = document.createElement("DIV");
            this.content1.innerHTML = this.content;
            this.content2.innerHTML = this.content;
            this.control.appendChild(this.content1);
            this.control.appendChild(this.content2);
        }
        else{
        
            this.content1 = document.createElement("DIV");
            this.content2 = document.createElement("DIV");
            document.body.appendChild(this.content1);
            this.content1.innerHTML = this.content;
            this.content2.innerHTML = this.content;
            if(IS_IE)
            {
                this.content1.style.styleFloat = "left";
                this.content2.style.styleFloat = "left";
            }
            else
            {
                this.content1.style.cssFloat = "left";
                this.content2.style.cssFloat = "left";
            }
            
            var container = document.createElement("DIV");             
            container.style.width = (parseInt(this.content1.offsetWidth)*2)+"px";  
            container.appendChild(this.content1);
            container.appendChild(this.content2);
            this.control.appendChild(container);           
            
        }    
        this.control.style.overflow  = "hidden";
        this.control.onmouseover = function(){ window.clearInterval(owner.interval);}
        this.control.onmouseout = function(){ owner.interval= window.setInterval(owner.marquee,owner.speed);}       
    }    
    this._isInit = true;   
}
JetsenWeb.UI.Marquee.prototype.start = function(){    
    if(!this._isInit)
        this.init();    
    this.interval = window.setInterval(this.marquee,this.speed);
}
