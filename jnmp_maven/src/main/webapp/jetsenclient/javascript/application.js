//=============================================================================
// 每个系统都应包含的脚本文件，框加启动时自动加载，这个文件仅作为范本
// 如可以在加载时作一些用户登录验证的处理等
//=============================================================================

JetsenWeb.registerNamespace("JetsenWeb.Application");

JetsenWeb.valideLogin();

JetsenWeb.importCss("style");

JetsenWeb.Application.getTypeMapping = function(/*string*/ typeName){
    switch(typeName){
        case "":
            break;
    }
    return typeName;
}