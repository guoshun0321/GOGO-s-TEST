package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CtrlWordEntity;

/**
 * @author ？
 */
public class CtrlWord
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addCtrlWord(String objXml) throws Exception
    {
        DefaultDal<CtrlWordEntity> dal = new DefaultDal<CtrlWordEntity>(CtrlWordEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateCtrlWord(String objXml) throws Exception
    {
        DefaultDal<CtrlWordEntity> dal = new DefaultDal<CtrlWordEntity>(CtrlWordEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteCtrlWord(int keyId) throws Exception
    {
        DefaultDal<CtrlWordEntity> dal = new DefaultDal<CtrlWordEntity>(CtrlWordEntity.class);
        return dal.delete(keyId);
    }
}
