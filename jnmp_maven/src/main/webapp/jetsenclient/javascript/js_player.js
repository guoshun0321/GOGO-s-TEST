//lixiaomin
//=============================================================================
// JetsenWeb.UI.Player
//=============================================================================	 
JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_player"));
JetsenWeb.registerNamespace("JetsenWeb.UI");
JetsenWeb.UI.Players = {};
var gSysConfigs = {};
//playerType 1Video,2Audio,3Media,4VLC,5CheckPlayer,20:pic,100:EmptyPlayer
JetsenWeb.UI.createPlayer = function(con,playerType,playName)
{ 
    con = con?con:document.body;
    playerType = playerType?parseInt(playerType):0;   
    playerType = JetsenWeb.isIE()?playerType:(navigator.userAgent.toLowerCase().indexOf("windows")>=0?3:4);    
   
    var player;
    if(playerType == 1)
    {
        player = new JetsenWeb.UI.McmPlayer(con);
    }
    else if(playerType == 2)
    {
        player =  new JetsenWeb.UI.AcmPlayer(con);
    }
    else if(playerType == 3)
    {
        player =  new JetsenWeb.UI.MediaPlayer(con);
    }
    else if(playerType == 4)
    {
        player =  new JetsenWeb.UI.ApplePlayer(con);
    }
    else if(playerType == 5)
    {
        player =  new JetsenWeb.UI.CheckPlayer(con);
    }
    else if(playerType == 20)
    {
        player =  new JetsenWeb.UI.PicPlayer(con);
    }
    else if(playerType == 100)
    {
        player =  new JetsenWeb.UI.EmptyPlayer(con);
    }
    else
    {
        player = new JetsenWeb.UI.McmPlayer(con);
    }
    
    player.playerType = playerType;
    playName = playName?playName:"jetsenPlayer";
    JetsenWeb.UI.Players[playName] = player;
    return player;
};
//媒资播放器===================================================================
JetsenWeb.UI.MamPlayerBase = function()
{
    this.frameRate = 25;
    this.container = document.body; 
    this.player = null;   
};
JetsenWeb.UI.MamPlayerBase.prototype.setUrl = function(filePaths)
{
	if(!filePaths)
	{      
		this.player.OpenFile("");
		this.player.CloseFile();
	}
	else
	{	
	    var arrUrl = filePaths.split(';');
        filePaths = replaceMamRoot(arrUrl[0],gSysConfigs);
		if(this.player.GetPlayFile().toLowerCase()!= filePaths.toLowerCase())
		{
			this.player.OpenFile(filePaths);
			this.frameRate=this.player.GetFrameRate();     				
		}
	}
	return true;
};
JetsenWeb.UI.MamPlayerBase.prototype.play = function(){this.player.Play();};

JetsenWeb.UI.MamPlayerBase.prototype.cmBatchImport = function(xmlData){	return this.player.cmBatchImport(xmlData);};

JetsenWeb.UI.MamPlayerBase.prototype.pause = function(){this.player.Pause();};
JetsenWeb.UI.MamPlayerBase.prototype.closeFile = function(){this.player.CloseFile();};
JetsenWeb.UI.MamPlayerBase.prototype.seekTo = function(framePosition)
{	
	this.pause();	
	this.player.SeekTo(framePosition);
};
JetsenWeb.UI.MamPlayerBase.prototype.setPaths = function(paths)
{
	this.CurrentPaths = paths;
	var tempObjXml = new JetsenWeb.XmlDoc();
	tempObjXml.loadXML(paths);
	this.Objtype = tempObjXml.documentElement.getAttribute("type");//获取对象类型//1  视频编目  2 音频编目 3 图片编目 
};
JetsenWeb.UI.MamPlayerBase.prototype.getFileInfo = function(fileInfo){try{return this.player.GetPlayFileInfo(fileInfo);}catch(ex){return "";}};
JetsenWeb.UI.MamPlayerBase.prototype.getMediaInfo = function(fileInfo){try{return this.player.GetMediaInfo(fileInfo);}catch(ex){return "";}};
JetsenWeb.UI.MamPlayerBase.prototype.openFileDialog = function(){return this.player.OpenFileDialog();}
JetsenWeb.UI.MamPlayerBase.prototype.openSingleFileDialog = function(){return this.player.openSingleFileDialog();};
JetsenWeb.UI.MamPlayerBase.prototype.getDiskFreeSpace = function(diskPath){try{return this.player.GetDiskFreeSpace(diskPath);}catch(ex){return 0;}};
JetsenWeb.UI.MamPlayerBase.prototype.grabFrame = function(filePath){this.player.grabFrame(0,filePath,1,0,0);};
JetsenWeb.UI.MamPlayerBase.prototype.getPosition = function(){return this.player.GetPosition();};
JetsenWeb.UI.MamPlayerBase.prototype.setInOutPoint = function(inPoint, outPoint){this.player.SetCtrlRange(inPoint, outPoint);};
JetsenWeb.UI.MamPlayerBase.prototype.getInPoint = function(){return this.player.GetInPoint();};
JetsenWeb.UI.MamPlayerBase.prototype.getOutPoint = function(){ return this.player.GetOutPoint();};
JetsenWeb.UI.MamPlayerBase.prototype.getDuration = function(isPoint){var inPoint = this.getInPoint();var outPoint = this.getOutPoint();return ( outPoint - inPoint + 1);};
JetsenWeb.UI.MamPlayerBase.prototype.setSize = function(width,height){return null;};

//视频播放器===================================================================
JetsenWeb.UI.McmPlayer = function(con)
{
   this.container = con?con:document.body;
};
JetsenWeb.UI.McmPlayer.prototype = new JetsenWeb.UI.MamPlayerBase();

JetsenWeb.UI.McmPlayer.prototype.initPlayer = function(fileName)
{
    fileName = fileName?fileName:"";
    this.container.innerHTML = '<object id="mcmplayer" name="xplayer" width="100%" height="100%" classid="clsid:8996D56A-6465-4F63-91E1-36EC63ACFB15" ></object>';
    this.player = $("mcmplayer");
};

//音频播放器===================================================================
JetsenWeb.UI.AcmPlayer = function(con)
{    
    this.container = con?con:document.body;
    this.frameRate = 1000;
};
JetsenWeb.UI.AcmPlayer.prototype = new JetsenWeb.UI.MamPlayerBase();

JetsenWeb.UI.AcmPlayer.prototype.initPlayer = function(fileName)
{
    fileName = fileName?fileName:"";
    this.container.innerHTML = '<object id="acmplayer" name="xplayer" width="100%" height="100%" classid="clsid:ae38d52e-5219-4e4b-bb14-24f0ce3dff86"  viewastext="viewastext"><param name="autostart" value="0" /><param name="uimode" value="full" /><param name="stretchToFit" value="true" /></object>';
    this.player = $("acmplayer");
};
//JetsenWeb.UI.AcmPlayer.prototype.setInOutPoint = function(inPoint, outPoint){this.player.SetCtrlRange(inPoint, outPoint);};
JetsenWeb.UI.AcmPlayer.prototype.seekTo = function(millsecond){this.pause();this.player.SeekTo(millsecond);};

//Media播放器===================================================================
JetsenWeb.UI.MediaPlayer = function(con)
{    
    this.frameRate = 25;
    this.container = con?con:document.body;
    this.player = null;     
};

JetsenWeb.UI.MediaPlayer.prototype.initPlayer = function(fileName)
{
    fileName = fileName?fileName:"";
    this.container.innerHTML = '<object id="mediaplayer" name="xplayer" width="100%" height="100%" classid="clsid:6bf52a52-394a-11d3-b153-00c04f79faa6" type="application/x-oleobject" viewastext="viewastext"><param name="autostart" value="0" /><param name="uimode" value="full" /><param name="stretchToFit" value="true" /><param name="filename"   value="'+fileName+'"> </object>';
    this.player = $("mediaplayer");
};

JetsenWeb.UI.MediaPlayer.prototype.setUrl = function(filePaths)
{
    if(!IS_IE && this.player.controls==null)
        return;
    this.pause();
    
    filePaths = filePaths?filePaths:"";

    if (filePaths!="")
    {
        var arrUrl = filePaths.split(';');
	    filePaths = arrUrl[0];
    }
    
	if(filePaths=="" || !JetsenWeb.isIE())
	{          
		this.initPlayer(filePaths);		
	}
	else
	{	
	    filePaths = replaceMamRoot(filePaths,gSysConfigs);
		if(this.player.Url.toLowerCase()!= filePaths.toLowerCase())
		{
			this.player.Url = filePaths;    //Media Player
        }
	}
	return true;
};
JetsenWeb.UI.MediaPlayer.prototype.play = function(){this.player.controls.play();};
JetsenWeb.UI.MediaPlayer.prototype.openFileDialog = function(){jetsennet.alert("当前系统不支持!");};
JetsenWeb.UI.MediaPlayer.prototype.openSingleFileDialog = function(){jetsennet.alert("当前系统不支持!");};
JetsenWeb.UI.MediaPlayer.prototype.closeFile = function(){};
JetsenWeb.UI.MediaPlayer.prototype.getDiskFreeSpace = function(diskPath){return 10;};
JetsenWeb.UI.MediaPlayer.prototype.grabFrame = function(){return null;};
JetsenWeb.UI.MediaPlayer.prototype.getPosition = function(){return this.player.controls.currentPosition*this.frameRate;};
JetsenWeb.UI.MediaPlayer.prototype.pause = function(){this.player.controls.stop();};
JetsenWeb.UI.MediaPlayer.prototype.seekTo = function(millsecond)
{
	if(this.player.openState!=13)
	{  //13 open state
		this.play();      
		if(this.player.openState!=6)
		{   //6 open but no flow state		   
			//setTimeout('JetsenWeb.UI.Players[\"jetsenPlayer\"].seekTo(\"'+hmsfOrFrameCount+'\");',500);			   
		}		
	}
	else
	{
		if(this.player.playState == 3)
		{  //3 play state
			this.pause();
		}
	}
	this.player.controls.currentPosition = parseInt(millsecond/this.frameRate); 
};
JetsenWeb.UI.MediaPlayer.prototype.setInOutPoint = function(inPoint, outPoint){/*会出错this.player.SetCtrlRange(inPoint, outPoint);*/};
JetsenWeb.UI.MediaPlayer.prototype.getInPoint = function()
{
    try
    {
        return this.player.GetInPoint();
        
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.MediaPlayer.prototype.getOutPoint = function()
{
    try
    {
        return this.player.GetOutPoint();        
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.MediaPlayer.prototype.getDuration = function(isPoint){
    try
    {
        var inPoint = this.getInPoint();
        var outPoint = this.getOutPoint();
        return ( outPoint - inPoint + 1);  // 实长 = 出点 - 入点 + 1         
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.MediaPlayer.prototype.setSize = function(width,height){return null;};
JetsenWeb.UI.MediaPlayer.prototype.getMediaInfo = function(){return "";};
JetsenWeb.UI.MediaPlayer.prototype.getFileInfo = function(){return "";};

//Apple播放器===================================================================
JetsenWeb.UI.ApplePlayer = function(con)
{   
    this.frameRate = 25;
    this.container = con?con:document.body;
    this.player = null; 
    this.slider = {};   //播放器滚动条
    this.playtime=null; //显示播放时间
    this.frameRate = 25;  
    this.sliderInterval=null;  //setInterval 时间句柄
    this.sleep=1000;
    this.playingState=false; //设置播放状态
	this.setSliderState=false;
	this.playFile = "";
};
JetsenWeb.UI.ApplePlayer.prototype.initPlayer = function(fileName)
{ 
    fileName = fileName?fileName:"";
    var _baseThemeUrl=JetsenWeb.baseThemeUrl;

    var _height=this.container.clientHeight ?this.container.clientHeight-80:252;
    var contentHtml = [];     
    contentHtml.push("<div style='width: 100%;height: 100%;background-color: Black;border:0px solid #999;LETTER-SPACING: 0px;LINE-HEIGHT: 0px;overflow:hidden;background:white; '>"); 
    contentHtml.push("<div style='width:100%;height:" + _height+ "px;background-color: black;' id='div_xplayer' name='div_xplayer' align='center'></div>");
    
    JetsenWeb.require(["js_datetime","js_slider"]);
    contentHtml.push("<div style='min-height: 80px;' id='jetsen-player-control'>");
    contentHtml.push("<div style='width:100%; height:4px'></div><div style='width: 100%; background-color: lightgrey; height: 20px;'>");
    contentHtml.push("    <div class='slider' id='slider-1' tabindex='1' style='width: 99%; height: 14px; float: right'>");
    contentHtml.push("            <input class='slider-input' id='slider-input-1' name='slider-input-1' /></div></div>");
    contentHtml.push("    <div align='center' style='width: 100%; background-color: lightgrey; background-image: url(" + _baseThemeUrl  + "images/playimages/back.gif);");
    contentHtml.push("       min-height: 54px; height: 60px; '>");
    contentHtml.push("     <div style='float: none;'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/gohead.gif' id='imgGoHead' onclick=JetsenWeb.UI.Players['jetsenPlayer'].goHead() title='到头部'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/goinpoint.gif' id='Img3' onclick=JetsenWeb.UI.Players['jetsenPlayer'].goToinPoint() title='到入点'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/previous.gif' id='imgPrevious' onclick=JetsenWeb.UI.Players['jetsenPlayer'].previous() title='快退'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/play.gif' id='imgPlayOrPause' onclick=JetsenWeb.UI.Players['jetsenPlayer'].play()  title='播放'>");
    contentHtml.push("          <input type='text' id='txt_time' value='00:00:00:00' style='width: 70px; text-align: center;height: 16px; border-bottom-style: none; background-color: Black; color: #ffffff;' readonly>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/next.gif' id='imgNext' onclick=JetsenWeb.UI.Players['jetsenPlayer'].next()   title='快进'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/gooutpoint.gif' id='Img4' onclick=JetsenWeb.UI.Players['jetsenPlayer'].goTooutPoint()  title='到出点'>");
    contentHtml.push("          <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/gotail.gif' id='imgGoTail' onclick=JetsenWeb.UI.Players['jetsenPlayer'].goTail()  title='到尾部'><br />");
    contentHtml.push("          <div style='float: left'>");
    contentHtml.push("          <div style='float: left'>");
    contentHtml.push("              <img align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/cliplength1.gif' id='Img5'>");
    contentHtml.push("              <input type='text' id='txt_AP_SumFrames' value='00:00:00:00' style='width: 70px;text-align: center; height: 16px; border-bottom-style: none; background-color: Black;color: #ffffff;'  title='总时长度' readonly>");
    contentHtml.push("              <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/SetInPoint.gif' id='Img1' onclick=JetsenWeb.UI.Players['jetsenPlayer'].setinPoint()  title='设置入点'>");
    contentHtml.push("		        <input type='text' id='txt_AP_InPoint' value='00:00:00:00' style='width: 70px;text-align: center; height: 14px; border-bottom-style: none; background-color: Black;color: #ffffff;'  title='入点位置' readonly>");
    contentHtml.push("           </div>");
    contentHtml.push("          <div style='float: right'>");
    contentHtml.push("              <img style='cursor: pointer' align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/setoutpoint.gif' id='Img2' onclick=JetsenWeb.UI.Players['jetsenPlayer'].setoutPoint()  title='设置出点' readonly>");
    contentHtml.push("              <input type='text' id='txt_AP_OutPoint' value='00:00:00:00' style='width: 70px; text-align: center;height: 14px; border-bottom-style: none; background-color: Black; color: #ffffff;'  title='出点位置' readonly>");
    contentHtml.push("              <img align='absmiddle' src='" + _baseThemeUrl  + "images/playimages/cliplength.gif'id='Img6'>");
    contentHtml.push("              <input type='text' id='txt_AP_DURATION' value='00:00:00:00' style='width: 70px; text-align: center;height: 14px; border-bottom-style: none; background-color: Black; color: #ffffff;' title='素材长度' readonly>");
    contentHtml.push("          </div>");
    contentHtml.push("      </div>");
    contentHtml.push("  </div>");
    contentHtml.push("</div></div>"); 
           
    this.container.innerHTML = contentHtml.join("");
    this.slider = new JetsenWeb.UI.Slider($('slider-1'), $('slider-input-1'));
    this.slider.setMinimum(0);
	
	if(IS_SAFARI)
	{
//        var xplayer = document.createElement("embed");
//        xplayer.type = "video/quicktime";
//        xplayer.id = "xplayer";
//        xplayer.src = "default.mp4";
//        xplayer.style.width ="100%";
//        xplayer.style.height ="100%";	
//        xplayer.autoplay = "no";
//        xplayer.showlog = "false";
//        xplayer.loop = "no";
//        xplayer.toolbar = "no";	
//        $('div_xplayer').appendChild(xplayer);//controller="true" autostart="false" autoplay="true"  showlogo="false" scale="tofit"
	      $('div_xplayer').innerHTML = '<object  type="video/quicktime" id="xplayer" ><embed src="none.mp4" type="video/quicktime"  enablejavascript="true"  postdomevents="true" width="100%" height="100%" /></object>';
    }
    else
    {
        var xplayer = document.createElement("embed");
        xplayer.type = "application/jnet-player-plugin";
	    xplayer.id = "xplayer";
        xplayer.style.width ="100%";
        xplayer.style.height ="100%";	
        xplayer.autostart = "no";
	    xplayer.autoplay = "no";
	    xplayer.loop = "no";
	    xplayer.toolbar = "yes";	  
	    $('div_xplayer').appendChild(xplayer);
	}
	
    this.player = $("xplayer");	   
    this.playtime=$("txt_time");    
    var _slider = this.slider;
    var _player=this.player;
    var _owner = this;
    
    if (fileName!="")
    {
      this.setUrl(fileName);
    }    
	
    this.slider.onchange = function ()
    {
        if (this.setSliderState){
	        return;
        }
        var s_index= parseInt(_slider.getValue());  
        var xplayer_time= _owner.getPosition(); 
        var diff = 10;//IS_SAFARI?1500:10;
        if (Math.abs(s_index- xplayer_time)>=diff || _owner.playingState==false )
        {      
            _owner.pause();
            _owner.seekTo(s_index);				
            _owner.playtime.value=JetsenWeb.Util.convertLongToTime(s_index,_owner.frameRate);    
            if(_owner.playingState)
            {	_owner.playingState=false;
                _owner.setShowImg();
            }
        }
    };  
    if(IS_SAFARI)
	{
	    if(this.sliderInterval!=null)
	    {
	        window.clearInterval(this.sliderInterval);
	    }
        this.sliderInterval=setInterval(function(){_owner.refurbishSlider();},this.sleep/this.frameRate );///this.frameRate   
    }  
};
JetsenWeb.UI.ApplePlayer.prototype.setSize = function(width,height)
{  
    this.player.style.width ="100%";
    $('div_xplayer').style.height =(height-80) +"px";	
    this.player.style.height =(height-80) +"px";
};
JetsenWeb.UI.ApplePlayer.prototype.setUrl = function(url)
{  
    this.playFile = url;
    if(IS_SAFARI)
    {
        this.stop();
        this.player.SetURL(url);        		
		
		$('txt_AP_InPoint').value="00:00:00:00";
	    $('txt_AP_SumFrames').value="00:00:00:00";
	    $('txt_AP_DURATION').value="00:00:00:00";
	    $('txt_AP_OutPoint').value="00:00:00:00";
	    this.playtime.value="00:00:00:00";
		this.playingState=false;		
		this.slider.setValue(0);		
        return;
    }
	if(!url)
	{      
	    this.stop();
		this.player.OpenFile("");
		this.player.CloseFile();
		this.slider.setValue(0);   
		$('txt_AP_SumFrames').value="00:00:00:00";
		$('txt_AP_OutPoint').value="00:00:00:00";
		$('txt_AP_DURATION').value="00:00:00:00";
		$('txt_AP_InPoint').value="00:00:00:00";
		this.player.title="";		
	}
	else
	{	
		if (url.trim().substring(0,1)!="<" )
        {
			if ( url.trim().substring(0,1)!="/")
			{
				 url=replaceMamRoot(url,gSysConfigs);
			}		   
		    url=url.replaceAll("\\\\","/" );
		}
		else
		{
		    var _url = this.getPlayFile();
		    if(_url && _url!="")
		        url = _url;
		}         
		this.slider.setValue(0);   
		
		this.player.title=url;
		var remsg=this.player.OpenFile(url);
		
		if (!remsg) {	
			$('txt_AP_SumFrames').value="00:00:00:00";
			$('txt_AP_OutPoint').value="00:00:00:00";
			$('txt_AP_DURATION').value="00:00:00:00";
			$('txt_AP_InPoint').value="00:00:00:00";				
			return false;
		}
		this.frameRate=this.player.GetFrameRate();  
		var _SumFrames=this.getSumFrames();
					
		$('txt_AP_SumFrames').value=JetsenWeb.Util.convertLongToTime(_SumFrames ,this.frameRate);
		$('txt_AP_InPoint').value="00:00:00:00"; 			
		$('txt_AP_OutPoint').value=JetsenWeb.Util.convertLongToTime(_SumFrames-1 ,this.frameRate); 
		$('txt_AP_DURATION').value=JetsenWeb.Util.convertLongToTime(_SumFrames ,this.frameRate); 
		
		this.playtime.value="00:00:00:00";
		this.playingState=false;
		this.slider.setMaximum(_SumFrames);
	}
	return true;
};
JetsenWeb.UI.ApplePlayer.prototype.refurbishSlider =function ()
{
    var duration = this.getSumFrames();
    if(IS_SAFARI)
    {
//        var control = $('jetsen-player-control');
//        if(control)
//        {
//            if(duration==0)
//            {
//                control.style.display = "none";
//            }
//            else
//            {
//                control.style.display = "";
//            }
//        }
    }
    
    if (this.playing())  //滑动滚动条
    {  
        var duration=parseInt(duration);
        this.slider.setMinimum(0);
        if(IS_SAFARI)
        {
            this.slider.setMaximum(duration);              
            $('txt_AP_DURATION').value=JetsenWeb.Util.convertLongToTime(duration ,this.frameRate); 
            $('txt_AP_OutPoint').value=JetsenWeb.Util.convertLongToTime(duration ,this.frameRate); 
            $('txt_AP_SumFrames').value=JetsenWeb.Util.convertLongToTime(duration ,this.frameRate);      
        }
        var position = this.getPosition();
        this.playtime.value= JetsenWeb.Util.convertLongToTime(position,this.frameRate); //JetsenWeb.Util.convertLongToTime(duration).substring(0,8);
        this.slider.setValue(position);
        if (duration>1 && Math.abs(duration-position)<0.5) {
            this.playingState=false;
	        this.setShowImg();	
	    }	
    }
    else if ( this.playingState  )
    {
		 this.playtime.value= JetsenWeb.Util.convertLongToTime(duration,this.frameRate);
		 this.playingState=false;
		 this.setShowImg();	
    }
};
JetsenWeb.UI.ApplePlayer.prototype.play = function()
{	
    var _owner = this;    
    
    if(this.getPlayFile()=="") {       
        return;    
    }         
    
    if (this.playing()){
        this.pause();  //停止
        return;
    }
    
    var duration = this.getSumFrames();
	if (duration>0 && duration==this.getPosition())
	{  //已经播放到最后1帧        
		this.seekTo(0);		
	}
	
    this.playingState=true;
    //this.player.Play(); //播放
    $("xplayer").Play();
    this.setShowImg();  
	   
	if(!IS_SAFARI)
	{
	    if(this.sliderInterval!=null)
	    {
	        window.clearInterval(this.sliderInterval);
	    }
        this.sliderInterval=setInterval(function(){_owner.refurbishSlider();},this.sleep/this.frameRate );///this.frameRate   
    }
};

JetsenWeb.UI.ApplePlayer.prototype.stop = function()
{  
    if(this.getPlayFile()=="") return; 
	this.player.Stop();  //停止
	this.playingState=false;
	this.setShowImg();	
};

JetsenWeb.UI.ApplePlayer.prototype.closeFile = function()
{  
    if(IS_SAFARI)
    {
        this.player.Stop();
        this.player.SetURL("");
        return;
    }
    
	this.player.CloseFile();  //停止
	this.slider.setValue(0);  
	this.playingState=false;
	this.setShowImg();	
};

JetsenWeb.UI.ApplePlayer.prototype.pause = function()
{ 
    if(this.getPlayFile()=="") return; 
    if(IS_SAFARI)
    {
        this.player.Stop();
    }
    else
    {
	    this.player.Pause();  //暂停
	}
	this.playingState=false;
	this.setShowImg();	
};

JetsenWeb.UI.ApplePlayer.prototype.getPlayFile = function(){if(IS_SAFARI) return this.playFile;return this.player.GetPlayFile();};
JetsenWeb.UI.ApplePlayer.prototype.fullscreen = function(){return null;};
JetsenWeb.UI.ApplePlayer.prototype.seekTo = function(framePosition)
{        
	if(this.getPlayFile()=="") return; 	
	//this.pause();
	
	this.setSliderState=true;		
	this.playingState=false;
	this.setShowImg();		
	
	if(IS_SAFARI)
	    this.player.SetTime(framePosition*1000);
	else
	    this.player.SeekTo(framePosition);
	
	this.slider.setValue(framePosition);	
	this.playtime.value= JetsenWeb.Util.convertLongToTime(framePosition ,this.frameRate);	

	this.setSliderState=false;
};
JetsenWeb.UI.ApplePlayer.prototype.setPaths = function(paths)
{    
	this.CurrentPaths = paths;
	var tempObjXml = new JetsenWeb.XmlDoc();
	tempObjXml.loadXML(paths);
	this.Objtype = 	tempObjXml.documentElement.getAttribute("type");//获取对象类型//1  视频编目  2 音频编目 3 图片编目 
};
JetsenWeb.UI.ApplePlayer.prototype.getFileInfo = function($)
{    
    if(this.getPlayFile()=="") return; 
    try
    {
        return this.player.GetPlayFileInfo($);
    }
    catch(ex)
    {
        return "";
    }
};
JetsenWeb.UI.ApplePlayer.prototype.getMediaInfo = function($)
{
    try
    { 
        return this.player.GetMediaInfo($);
    }
    catch(ex)
    {
        return ex;
    }
};
JetsenWeb.UI.ApplePlayer.prototype.openFileDialog = function(){return this.player.OpenFileDialog();};
JetsenWeb.UI.ApplePlayer.prototype.openSingleFileDialog = function(){return this.player.openSingleFileDialog();};
JetsenWeb.UI.ApplePlayer.prototype.grabFrame = function(filePath)
{	
	this.player.grabFrame(0,filePath,1,0,0);
};
JetsenWeb.UI.ApplePlayer.prototype.getPosition = function(){if(IS_SAFARI){ return this.player.GetTime()/1000;};return this.player.GetPosition();};
JetsenWeb.UI.ApplePlayer.prototype.setInOutPoint = function(inPoint, outPoint)
{    	
	this.setSliderState=true;
	if(!IS_SAFARI)
        this.player.SetInOutPoint(inPoint, outPoint); 
    else
    {
//        this.player.SetStartTime(inPoint*1000);
//        this.player.SetEndTime(outPoint*1000);
    }   
     $('txt_AP_InPoint').value= JetsenWeb.Util.convertLongToTime(inPoint ,this.frameRate); 
     $('txt_AP_OutPoint').value=JetsenWeb.Util.convertLongToTime(outPoint  ,this.frameRate); 
     $('txt_AP_DURATION').value=JetsenWeb.Util.convertLongToTime(outPoint- inPoint  ,this.frameRate);  

    this.playingState=false;
	this.setShowImg();	
	this.setSliderState=false;

};
JetsenWeb.UI.ApplePlayer.prototype.getInPoint = function()
{
    try
    {
        return JetsenWeb.Util.convertTimeToLong($('txt_AP_InPoint').value,this.frameRate);//this.player.GetInPoint();        
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.ApplePlayer.prototype.getOutPoint = function()
{
    try
    {
        return JetsenWeb.Util.convertTimeToLong($('txt_AP_OutPoint').value,this.frameRate);// this.player.GetOutPoint();        
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.ApplePlayer.prototype.getDuration = function()
{
    if(IS_SAFARI) return this.player.GetDuration()/1000;
    try
    {
        var inPoint = this.getInPoint();
        var outPoint = this.getOutPoint();
        return ( outPoint - inPoint + 1);  // 实长 = 出点 - 入点 + 1         
    }
    catch(ex)
    {
        return 0;
    }
};
JetsenWeb.UI.ApplePlayer.prototype.getSumFrames = function(){if(IS_SAFARI){ var duration =  this.player.GetDuration();return duration/1000;}return this.player.GetSumFrames();};
JetsenWeb.UI.ApplePlayer.prototype.goHead = function(){this.seekTo(0);};
JetsenWeb.UI.ApplePlayer.prototype.goTail = function(){this.seekTo(this.getSumFrames());};
JetsenWeb.UI.ApplePlayer.prototype.previous = function()
{  
    //快退 4秒为一个快退
    var position= this.getPosition();
    if (position>0 && position-4*this.frameRate >0 ){
		this.seekTo( position - (4 * this.frameRate) );  
	}else {
	    this.seekTo(0);	
	}
};
JetsenWeb.UI.ApplePlayer.prototype.next = function()
{  
    //快进
    var position= this.getSumFrames();
	var toPosition =this.getPosition() + 4*this.frameRate;
    if (position >toPosition)
    {
		this.seekTo( toPosition);  
	}  
	else
	{
	    this.seekTo(position);	
	}  
};

JetsenWeb.UI.ApplePlayer.prototype.setinPoint = function()
{  
     if(this.getPlayFile()=="") return; 
    //设置入点   
    var position=this.getPosition();
    var outponit=JetsenWeb.Util.convertTimeToLong( $('txt_AP_OutPoint').value,this.frameRate) + 1;  
	if (position>=outponit)
	{
		return;
	}

    this.setInOutPoint(position,outponit);
	this.playtime.value= JetsenWeb.Util.convertLongToTime(position,this.frameRate);	
	this.slider.setValue(position);
};

JetsenWeb.UI.ApplePlayer.prototype.setoutPoint = function()
{  
    if(this.getPlayFile()=="") return; 
    //设置出点
    var _Position=this.getPosition();
    var _inponit=JetsenWeb.Util.convertTimeToLong( $('txt_AP_InPoint').value,this.frameRate);
	if (_inponit>=_Position)
	{
		return;
	}
    this.setInOutPoint(_inponit,_Position);
	this.playtime.value= JetsenWeb.Util.convertLongToTime(_Position,this.frameRate);
	this.slider.setValue(_Position);
};

JetsenWeb.UI.ApplePlayer.prototype.goToinPoint = function()
{ 
   if(this.getPlayFile()=="") return; 
   this.seekTo(JetsenWeb.Util.convertTimeToLong( $('txt_AP_InPoint').value,this.frameRate) );	
};
JetsenWeb.UI.ApplePlayer.prototype.goTooutPoint = function()
{
   if(this.getPlayFile()=="") return; 
   this.seekTo(JetsenWeb.Util.convertTimeToLong( $('txt_AP_OutPoint').value,this.frameRate));	
};
JetsenWeb.UI.ApplePlayer.prototype.getDiskFreeSpace = function (diskPath)
{
    return 10;
};
JetsenWeb.UI.ApplePlayer.prototype.setShowImg = function()
{
    if (this.playing()){
		$('imgPlayOrPause').src=JetsenWeb.baseThemeUrl + "images/playimages/stop.gif";
		$('imgPlayOrPause').title="暂停";
    }else{
		$('imgPlayOrPause').src=JetsenWeb.baseThemeUrl + "images/playimages/play.gif";
		$('imgPlayOrPause').title="播放";    
    }
};
JetsenWeb.UI.ApplePlayer.prototype.playing = function()
{  
    var position = this.getPosition();
    var duration = this.getSumFrames();
	if (this.playingState && position==(duration-1)  || duration<=0 ){
	    this.playingState==false;		
	}
	return this.playingState;
};

// 计审播放器===================================================================
JetsenWeb.UI.CheckPlayer = function(con)
{    
    this.frameRate = 25;
    this.container = con?con:document.body;
    this.player = null; 
};
JetsenWeb.UI.CheckPlayer.prototype.initPlayer = function(fileName)
{
    fileName = fileName?fileName:"";
    this.player = document.createElement("OBJECT");
    this.player.id = "xplayer";
    this.player.style.width ="100%";
    this.player.style.height ="100%";
    this.player.classid ="CLSID:98557C70-1E7B-4D55-A832-F328881E5D1E";
    this.container.appendChild(this.player);
    //this.container.innerHTML =  '<object id="xplayer" style="width:100%;height:100%;" classid="CLSID:98557C70-1E7B-4D55-A832-F328881E5D1E"></object>';
    this.player = $("xplayer");
};
JetsenWeb.UI.CheckPlayer.prototype.setUrl = function(fileName)
{
   if(!fileName)
	{      
		this.player.OpenFile("");
	//	this.player.CloseFile();
	}
	else
	{	
	   fileName=replaceMamRoot(fileName,gSysConfigs);
	   this.player.OpenFile(fileName);
	}
	return true;
};
JetsenWeb.UI.CheckPlayer.prototype.setUrl3 = function(filePath,startTime,endTime)
{
    if(!filePath)
	{      
		this.player.OpenFile("");	
	}
	else
	{	
	   fileName=replaceMamRoot(filePath,gSysConfigs);
	   this.player.OpenFile3(filePath,startTime,endTime);
	}
	return true;
};
JetsenWeb.UI.CheckPlayer.prototype.play = function(){};
JetsenWeb.UI.CheckPlayer.prototype.stop = function(){};
JetsenWeb.UI.CheckPlayer.prototype.pause = function(){};
JetsenWeb.UI.CheckPlayer.prototype.setSize = function(width,height){};
JetsenWeb.UI.CheckPlayer.prototype.closeFile = function(){};

// 图片播放器===================================================================
JetsenWeb.UI.PicPlayer = function(con)
{    
    this.frameRate = 25;
    this.container = con?con:document.body;
    this.player = null; 
};
JetsenWeb.UI.PicPlayer.prototype.initPlayer = function(fileName)
{
    fileName = fileName?fileName:"";
    this.player = document.createElement("DIV");
    this.player.id = "xplayer";
    this.player.style.width ="100%";    
    this.player.style.height ="100%";    
    this.player.style.overflow ="hidden";
//    this.player.style.paddingLeft = "5px";
//    this.player.style.paddingTop = "5px";
//    this.player.style.paddingRight = "5px";
//    this.player.style.paddingBottom = "5px";
    this.player.style.backgroundColor = '#000';
    this.container.appendChild(this.player);
    this.player = $("xplayer");
    this.player.CloseFile = function(){};
    this.imgPath = "";
    
    var containerStyle = JetsenWeb.Util.getCurrentStyle(this.container);
    this.containerPaddingTop = JetsenWeb.Util.parseInt(containerStyle.paddingTop,0);
    this.containerPaddingLeft = JetsenWeb.Util.parseInt(containerStyle.paddingLeft,0);
};
JetsenWeb.UI.PicPlayer.prototype.setUrl = function(fileName)
{
    if(!fileName)
	{      
		this.player.innerHTML = "";
		this.imgPath = "";
	}
	else
	{	
        fileName=replaceMamRoot(fileName,gSysConfigs);
        if(!JetsenWeb.Util.left(fileName,4).equal("http"))
        {
            fileName = "file:///"+fileName;
        }
        this.imgPath  = fileName;  
        this.refresh();
	}	
};
JetsenWeb.UI.PicPlayer.prototype.refresh = function()
{
    if(!this.imgPath)
	{      
		this.player.innerHTML = "";
		return;		
	}
    var img = new Image();
    img.src = this.imgPath;
    var eageSize = JetsenWeb.Util.getControlEdgeSize(this.player);
    var width = this.player.clientWidth-eageSize.left-eageSize.right;
    var height = this.player.clientHeight-eageSize.top-eageSize.bottom;
    width = width?width:200;
    height = height?height:150;
    var imgWidth = width-20;
    var imgHeight = height-20;
   
    if(img && img.width)
    {
        imgHeight = parseInt(1.0*imgWidth*img.height/img.width);     
        if(imgHeight>height)
        {
            imgHeight = height-20;
            imgWidth = parseInt(1.0*imgHeight*img.width/img.height); 
        }    
    }
      
   var imgTop = parseInt((height-imgHeight)/2.0);
   imgTop += this.containerPaddingTop;
   var imgLeft = parseInt((width-imgWidth)/2.0);   
   imgLeft += this.containerPaddingLeft;       
  
   this.player.innerHTML = "<img src='"+this.imgPath+"' imgwidth='"+imgWidth+"' imgheight='"+imgHeight+"' onload='JetsenWeb.UI.PicPlayer.refreshImage(this)' style='position:absolute;top:"+imgTop+"px;left:"+imgLeft+"px;border:solid 1px #fff' width='"+imgWidth+"px' height='"+imgHeight+"px' />";
};
JetsenWeb.UI.PicPlayer.prototype.play = function(){};
JetsenWeb.UI.PicPlayer.prototype.stop = function(){};
JetsenWeb.UI.PicPlayer.prototype.pause = function(){};
JetsenWeb.UI.PicPlayer.prototype.setPaths = function(){};
JetsenWeb.UI.PicPlayer.prototype.setSize = function(width,height){};
JetsenWeb.UI.PicPlayer.prototype.closeFile = function(){return null;};
JetsenWeb.UI.PicPlayer.prototype.getDiskFreeSpace = function(){return 100;};
JetsenWeb.UI.PicPlayer.prototype.getMediaInfo = function(){return "";};
JetsenWeb.UI.PicPlayer.prototype.getFileInfo = function(){return "";};
JetsenWeb.UI.PicPlayer.prototype.openFileDialog = function(){jetsennet.alert("当前系统不支持!");};
JetsenWeb.UI.PicPlayer.prototype.openSingleFileDialog = function(){jetsennet.alert("当前系统不支持!");};
JetsenWeb.UI.PicPlayer.refreshImage = function(img)
{
    var imgH=parseInt(img.getAttribute("imgheight"));
    var imgW=parseInt(img.getAttribute("imgwidth"));
    var imgObj = new Image();
    imgObj.src = img.src;
    if(imgObj && imgObj.width && imgObj.width!=0)
    {
        var tempW=imgW;
        var tempH=parseInt(1.0*imgW*imgObj.height/imgObj.width);
        if(tempH>imgH)
        {
            tempH=imgH;
            tempW=parseInt(1.0*imgH*imgObj.width/imgObj.height);
        };
        if(Math.abs(imgH-tempH)>2 || Math.abs(imgW-tempW)>2)
        {
            img.width = tempW;
            img.height = tempH;
        }
    }
};
//空播放器===================================================================
JetsenWeb.UI.EmptyPlayer = function(con)
{    
    this.frameRate = 25;
    this.player = {}; 
    this.player.CloseFile = function(){};
};
JetsenWeb.UI.EmptyPlayer.prototype.initPlayer = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.setUrl = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.play = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.stop = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.pause = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.setPaths = function(){};
JetsenWeb.UI.EmptyPlayer.prototype.getDiskFreeSpace = function(){return 100;};
JetsenWeb.UI.EmptyPlayer.prototype.getMediaInfo = function(){return "";};
JetsenWeb.UI.EmptyPlayer.prototype.getFileInfo = function(){return "";};
JetsenWeb.UI.EmptyPlayer.prototype.openFileDialog = function(){jetsennet.alert("系统不支持!");};
JetsenWeb.UI.EmptyPlayer.prototype.openSingleFileDialog = function(){jetsennet.alert("系统不支持!");};


//苹果系统和windows 系统mam路径替换
function replaceMamRoot(filepath,sysConfigs)
{    
	if (sysConfigs==null ) 
	    return filepath;

	for (var i=0;i<4;i++)
	{
		if (sysConfigs["MamMacRoot" + i] && sysConfigs["MamWinRoot" + i])
		{
			
		   if (IS_MAC)
		   {
				filepath = JetsenWeb.Util.replacePath(filepath,sysConfigs["MamWinRoot" + i],sysConfigs["MamMacRoot" + i]); 				
		   }
		   else
		   {
		        filepath = JetsenWeb.Util.replacePath(filepath,sysConfigs["MamMacRoot" + i],sysConfigs["MamWinRoot" + i]); 
		   }
		}
	}
	return filepath;
};