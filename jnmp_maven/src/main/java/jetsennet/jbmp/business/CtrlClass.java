/************************************************************************
 *  日期：2012-11-1
 *  作者： 魏儒昆
 *  版本：JDMPV2.0
 *  描述：
 *  历史
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CtrlClassEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.util.SerializerUtil;

/**
 * @author wrk
 */
public class CtrlClass
{

    /**
     * 添加
     * @param objXml
     */
    @Business
    public String addCtrlClass(String objXml) throws Exception
    {
        DefaultDal<CtrlClassEntity> dalCtrlClass = new DefaultDal<CtrlClassEntity>(CtrlClassEntity.class);
        return "" + dalCtrlClass.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml
     * @throws Exception
     */
    @Business
    public void updateCtrlClass(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        DefaultDal<CtrlClassEntity> dalCtrlClass = new DefaultDal<CtrlClassEntity>(CtrlClassEntity.class);
        dalCtrlClass.update(model);
    }

    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteCtrlClass(int keyId) throws Exception
    {
        DefaultDal<CtrlClassEntity> dalCtrlClass = new DefaultDal<CtrlClassEntity>(CtrlClassEntity.class);
        dalCtrlClass.delete(keyId);
    }
}
