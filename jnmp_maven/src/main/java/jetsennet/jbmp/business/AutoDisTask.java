/************************************************************************
日 期：2011-12-02
作 者: 郭祥
版 本：v1.3
描 述: 数据库操作，自动发现任务表
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.HashMap;

import jetsennet.jbmp.dataaccess.AutoDisTaskDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.util.SerializerUtil;

/**
 * 数据库操作，自动发现任务表
 * @author 郭祥
 */
public class AutoDisTask
{
    /**
     * 添加
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAutoDisTask(String objXml) throws Exception
    {
        AutoDisTaskDal dal = new AutoDisTaskDal();
        return Integer.toString(dal.insertXml(objXml));
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteAutoDisTask(int keyId) throws Exception
    {
        AutoDisTaskDal dal = new AutoDisTaskDal();
        dal.delete(keyId);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAutoDisTask(String objXml) throws Exception
    {
        HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");
        AutoDisTaskDal dal = new AutoDisTaskDal();
        dal.update(model);
    }
}
