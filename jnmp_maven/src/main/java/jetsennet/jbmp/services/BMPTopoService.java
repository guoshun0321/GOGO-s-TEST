/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 拓扑图相关的查询及处理方法接口 
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.services;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import jetsennet.jbmp.business.AttribClass;
import jetsennet.jbmp.business.MObject;
import jetsennet.jbmp.business.Obj2Obj;
import jetsennet.jbmp.business.Picture;
import jetsennet.jbmp.business.TSMonitor;
import jetsennet.jbmp.business.TSTopoFile;
import jetsennet.jbmp.business.TopoMap;
import jetsennet.jbmp.business.TopoTemplate;
import jetsennet.jbmp.business.UploadFile;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.juum.business.Role;
import jetsennet.net.WSResult;

/**
 * @author 余灵
 */
@WebService(name = "BMPTopoService", serviceName = "BMPTopoService", targetNamespace = "http://JetsenNet/JNMP/")
public class BMPTopoService
{
    private static Logger logger = Logger.getLogger(BMPTopoService.class);

    /**
     * 查询所有拓扑图
     * @param ifView :是否展现拓扑图时获取，展现时不获取状态为“新建”的图
     * @param field_1 :0代表自建图，不可删除和修改的图；1代表可以修改和删除的图；
     * @param filterId :过滤哪些拓扑图
     * @param userId :登陆用户ID
     * @return 结果
     */
    public WSResult bmpQueryTopoMaps(String field_1, boolean ifView, String filterId, String userId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TopoMap().queryTopoMaps(field_1, ifView, filterId, userId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取拓扑图失败!", ex);
        }

        return retObj;
    }

    /**
     * 获取某拓扑图
     * @param mapId 拓扑图ID
     * @return 结果
     */
    public WSResult bmpQueryTopoMapById(int mapId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TopoMap().queryTopoMapById(mapId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取拓扑图失败!", ex);
        }

        return retObj;
    }

    /**
     * 获取某模板
     * @param tempId 模板ID
     * @return 结果
     */
    public WSResult bmpQueryTempById(int tempId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TopoTemplate().queryTempById(tempId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取模板失败!", ex);
        }

        return retObj;
    }

    /**
     * 获取某分类的所有子分类
     * @param classId 父分类ID
     * @return 返回自定义XML结构字符串
     */
    public WSResult bmpQuerySubClassByParentId(int classId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new AttribClass().querySubClassByParentId(classId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取某分类的所有子分类失败!", ex);
        }

        return retObj;
    }

    /**
     * 获取某对象的所有子对象
     * @param objId 对象ID
     * @return 返回自定义XML结构字符串
     */
    public WSResult bmpQuerySubObjectsByParentId(int objId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new MObject().querySubObjectsByParentId(objId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "获取某对象的所有子对象失败!", ex);
        }

        return retObj;
    }

    /**
     * 读取上传的图片
     * @param uploadPath 上传相对路径
     * @return 返回图片列表XML字符串； -1:发生异常；
     */
    public WSResult bmpReadUploadFile(String uploadPath)
    {
        WSResult retObj = new WSResult();

        try
        {
            String result = new UploadFile().readUploadFile(uploadPath);
            if (!"-1".equals(result))
            {
                retObj.resultVal = result;
            }
            else
            {
                retObj.errorCode = -1;
            }
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取上传的图片失败!", ex);
        }

        return retObj;
    }

    /**
     * 删除指定的上传的文件
     * @param uploadPath 上传相对路径
     * @param fileName 图片名
     * @return false：删除失败或异常； true：删除成功；
     */
    public WSResult bmpDeleteUploadFile(String uploadPath, String fileName)
    {
        WSResult retObj = new WSResult();

        try
        {
            boolean flag = new UploadFile().deleteUploadFile(uploadPath, fileName);
            if (flag)
            {
                retObj.resultVal = "true";
            }
            else
            {
                retObj.resultVal = "false";
            }
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除指定的上传的文件失败!", ex);
        }

        return retObj;
    }

    /**
     * 根据对象ID查询该对象（设备）的模板
     * @param objId 对象id
     * @return 结果
     */
    public WSResult bmpGetTemplateByObjId(int objId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = ClassWrapper.wrap(MObject.class).getTemplateByObjId(objId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "根据对象ID查询该对象的模板失败!", ex);
        }

        return retObj;
    }
    
    /**
     * 针对Evertz机箱的,根据对象ID查询该对象（设备）的模板
     * @param objId 对象id
     * @return 结果
     */
    public WSResult bmpGetEvertzTemplateByObjId(int objId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = ClassWrapper.wrap(Picture.class).instanceEvertz(objId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "根据对象ID查询该对象的模板失败!", ex);
        }

        return retObj;
    }

    /**
     * 查询上行二级拓扑图对象关系
     * @param objID 对象id
     * @return 的结果
     */
    public WSResult bmpGetObjectUpRelations(int objID)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TSMonitor().getObjectUpRelations(objID);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "查询上行二级拓扑图对象关系失败!", ex);
        }

        return retObj;
    }

    /**
     * 查询下行二级拓扑图对象关系
     * @param objID 对象id
     * @return 的结果
     */
    public WSResult bmpGetObjectLowRelations(int objID)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TSMonitor().getObjectLowRelations(objID);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "查询下行二级拓扑图对象关系失败!", ex);
        }

        return retObj;
    }

    /**
     * 插入新二级图关系
     * @param upTSObjId 顶级上行码流ID
     * @param objXml 参数
     * @return 的结果
     */
    public WSResult bmpInsertObj2Obj(int upTSObjId, String objXml)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new Obj2Obj().insertObj2Obj(upTSObjId, objXml);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "插入新二级图关系失败!", ex);
        }

        return retObj;
    }

    /**
     * 读取保存的码流拓扑图
     * @param xmlName 参数
     * @return 结果
     */
    public WSResult bmpReadTSLevelTopo(String xmlName)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TSTopoFile().readTSLevelTopo(xmlName);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "读取码流拓扑图失败!", ex);
        }

        return retObj;
    }

    /**
     * 保存码流拓扑图
     * @param xmlName 参数
     * @param topoXml 参数
     * @return 结果
     */
    public WSResult bmpSaveTSLevelTopo(String xmlName, String topoXml)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TSTopoFile().saveTSLevelTopo(xmlName, topoXml);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "保存码流拓扑图失败!", ex);
        }

        return retObj;
    }

    /**
     * 删除码流拓扑图
     * @param objIds 对象id
     * @return 结果
     */
    public WSResult bmpDeleteTSLevelTopo(String objIds)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new TSTopoFile().deleteTSLevelTopo(objIds);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除码流拓扑图失败!", ex);
        }

        return retObj;
    }
    /**
     * 是否拥有拓扑图设计的权限
     * @param objIds 对象id
     * @return 结果
     */
    public WSResult isHasEidtTopoRight(String roleId)
    {
        WSResult retObj = new WSResult();

        try
        {
            retObj.resultVal = new Role().isHasEditTopoRight(roleId);
        }
        catch (Exception ex)
        {
            errorProcess(retObj, "删除码流拓扑图失败!", ex);
        }

        return retObj;
    }


    /**
     * 记录错误日志
     * @param retObj 发生错误的WSResult对象
     * @param message 相关错误信息
     * @param ex 异常
     */
    private void errorProcess(WSResult retObj, String message, Exception ex)
    {
        logger.error(message, ex);
        retObj.errorCode = -1;
        retObj.errorString = message + ex.getMessage();
    }
}
