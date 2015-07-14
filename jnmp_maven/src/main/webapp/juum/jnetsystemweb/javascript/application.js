

//=============================================================================
//  JNetSystemWeb application
//=============================================================================


JetsenWeb.registerNamespace("JetsenWeb.Application");

JetsenWeb.require(JetsenWeb.appPath+"../jetsenclient/javascript/uum_public.js",true);
//JetsenWeb.importCss("Jetsen");

JetsenWeb.Application.getTypeMapping = function(/*string*/ typeName){
    switch(typeName){
        case "":
            break;
    }
    return typeName;
}