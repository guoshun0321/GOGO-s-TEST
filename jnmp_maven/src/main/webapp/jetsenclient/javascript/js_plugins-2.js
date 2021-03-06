//lixiaomin 2009-12-10
//=============================================================================
// 浏览器插件
//=============================================================================	 

JetsenWeb.addLoadedUri(JetsenWeb.getloadUri("js_plugins"));

JetsenWeb.registerNamespace("JetsenWeb.Plugins");
JetsenWeb.registerNamespace("JetsenWeb.Plugins.DvnClient");
JetsenWeb.registerNamespace("JetsenWeb.Plugins.MamClient");


JetsenWeb.Plugins.createMamClient = function(container,id)
{
    if (IS_MAC)
    {  
        var objClient = document.createElement("embed");
        objClient.id =id?id: "jetsen-plugins-client";
        //objClient.type="application/jmam_client" ;
        objClient.type="application/jnet-player-plugin";

        objClient.style.width ="1px";
        objClient.style.height ="1px";
        objClient.autoplay = "no";	
        objClient.loop = "no";
        objClient.toolbar = "yes";            
        document.body.appendChild(objClient);
    }               
    else
    {        
//        var objClient = document.createElement("OBJECT");
//        objClient.id = id?id: "jetsen-plugins-client";
//        objClient.classid ="clsid:7B2AD00C-84FE-4140-BFBA-66894BA04D26";
//        objClient.codeBase=JetsenWeb.baseUrl+"../plugins/"+"";
//        $(container).appendChild(objClient);    
        var _id=id?id: "jetsen-plugins-client";
        //var _codeBase=JetsenWeb.baseUrl+"../plugins/"+"mamauxclient.cab#version=1,0,0,1";
        var _codeBase=JetsenWeb.baseUrl+"../plugins/"+"mamauxclient.cab#version=1,0,0,3";
        var _classid="clsid:7B2AD00C-84FE-4140-BFBA-66894BA04D26";
        $(container).innerHTML='<object id="'+_id+'"  classid="'+_classid+'"  codebase="'+_codeBase+'" viewastext="viewastext"></object>';   
    }
};

JetsenWeb.Plugins.createDvnClient = function(container,id)
{
    if (IS_MAC)
    {  
        var objClient = document.createElement("embed");
        objClient.id = id?id: "jetsen-plugins-client";
        //objClient.type="jdvn-client-plugin" ;
        objClient.type="application/jdvn-client-plugin" ;

       
        objClient.style.width ="1px";
        objClient.style.height ="1px";
        objClient.autoplay = "no";	
        objClient.loop = "no";
        objClient.toolbar = "yes";            
        document.body.appendChild(objClient);
    }               
    else
    {        
//        var objClient = document.createElement("OBJECT");
//        objClient.id = id?id: "jetsen-plugins-client";
//        objClient.classid ="clsid:3066CF1D-8B8C-4482-8BF2-A0A22878EB41";
//        objClient.codeBase=JetsenWeb.baseUrl+"../plugins/"+"dvnauxclient.cab#version=1,0,0,16";
//        $(container).appendChild(objClient);  
        var _id = id ? id : "jetsen-plugins-client";
        var _classid = "clsid:FEFC7228-1C63-4560-8307-A6B2261E430A";
        if (!container) {
            container = document.createElement("div");
            container.style.display = "none";
            document.body.appendChild(container);
        }
        container.innerHTML = '<object id="' + _id + '"  classid="' + _classid + '" viewastext="viewastext"></object>';     
    }
};

JetsenWeb.Plugins.DvnClient.setPECallBackUrl = function(serviceUrl, method) 
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.SetPECallBackUrl(serviceUrl,method);
};

JetsenWeb.Plugins.getFileSize = function(filePath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.GetFileSize(filePath);
};
JetsenWeb.Plugins.copyFile = function(srcPath,dstPath)
{
    var fileMoveXml= "<object><objPaths><srcPath>"+srcPath+"</srcPath><dstPath>"+dstPath+"</dstPath></objPaths></object>";
    return JetsenWeb.Plugins.moveFile(fileMoveXml,"20");
};

JetsenWeb.Plugins.getHDInf = function(dstPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.GetHDSize(dstPath);
};

JetsenWeb.Plugins.getPathInf = function(dstPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.BrowsePath(dstPath);
};


JetsenWeb.Plugins.DvnClient.copyFile = function(srcPath,dstPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.CopyFile(srcPath,dstPath);
};
JetsenWeb.Plugins.copyDirectory = function(srcPath,dstPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.CopyDir(srcPath,dstPath);
};
JetsenWeb.Plugins.moveFile = function(fileMoveXml,moveType)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    var _result;
    // fileMoveXml="<object><objPaths><srcPath>/Volumes/MamDisk/TestClips/test.mov</srcPath><dstPath>/volumes/MamDisk/ad/mcmHighBitRate/20081209_161654_d35d96f6-721e-46f7-831a-b9f2205fcec9.mov</dstPath></objPaths></object>" ;
    
    if (IS_MAC){
        fileMoveXml.replaceAll("\\\\","/");
    }

    if(moveType=="10")                  //本地移动
        result = plugin.MoveObject(fileMoveXml);
    else if(moveType=="20")             //本地拷贝
        result = plugin.CopyObject(fileMoveXml,0);
    else if(moveType=="21")             //本地拷贝，并删除
        result = plugin.CopyObject(fileMoveXml,1);
    return result;
};

JetsenWeb.Plugins.deleteFile = function(fileName)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.DeleteFile(fileName);
};

JetsenWeb.Plugins.deleteDirectory = function(directoryName)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.DeleteDir(directoryName);
};
JetsenWeb.Plugins.createDirectory = function(directoryName)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.MakeDir(directoryName);
};

JetsenWeb.Plugins.dragFile = function(fileName,mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
   	if (IS_MAC)
		return plugin.DragFile(fileName);
	else
		return plugin.DragFile(fileName,mode);

};

JetsenWeb.Plugins.openFileDialog = function()
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.openFileDialog();
};
JetsenWeb.Plugins.execCmd = function(cmdText)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.ExecCmd(cmdText);
};
JetsenWeb.Plugins.execProgram = function (cmdText) {
    var plugin = $("jetsen-plugins-client");
    if (plugin == null) {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.ExecProgram(cmdText);
};
JetsenWeb.Plugins.isExistFile = function(fileName)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.IsFileExist(fileName);
};

JetsenWeb.Plugins.importClip2Editor = function(name,filePath,mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.ImportClip2Editor(name,filePath,mode)
};
JetsenWeb.Plugins.newProject = function(prjpath, mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.NewProject(prjpath,mode)
};
JetsenWeb.Plugins.openProject = function(prjpath, mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.OpenProject(prjpath,mode)
};
JetsenWeb.Plugins.compileProject = function(pgmpath,pgmfmt,mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.CompileProject(pgmpath,pgmfmt,mode)
};
JetsenWeb.Plugins.getCurrentProjectPath = function(mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.GetCurrentProjectPath(mode)
};
JetsenWeb.Plugins.saveCurrentProject = function(mode)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
    return plugin.SaveCurrentProject(mode)
};
JetsenWeb.Plugins.getMediaInfo = function(filePath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
	if(IS_MAC)
		return plugin.GetMediaInfo(filePath)
	else 
		return "";
};
JetsenWeb.Plugins.grabMovieFirstFrame = function(filePath,iconPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
	if(IS_MAC)
		return plugin.GrabMovieFirstFrame(filePath,iconPath)
	else 
		return -1;
};
JetsenWeb.Plugins.importClip2Fcp = function(fcpPath,clipPath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
	if(IS_MAC)
		return plugin.ImportClip2Fcp(fcpPath,clipPath)
	else 
		return -1;
};
JetsenWeb.Plugins.listPathFilesByFlag = function(path,includeSubDir,flag)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
	if(IS_MAC)
		return plugin.ListPathFilesByFlag(path,includeSubDir,flag)
	else 
		return false;
};
JetsenWeb.Plugins.doMediaTask = function(filePath,framePath)
{
    var plugin = $("jetsen-plugins-client");
    if(plugin==null)
    {
        jetsennet.alert("缺少插件!");
        return;
    }
	if(IS_MAC)
		return plugin.DoMediaTask(filePath,framePath)
	else 
		return "";
};
