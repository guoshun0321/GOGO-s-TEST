package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.SnmpNodesEntity;

/**
 * @author ？
 */
public class SnmpNodes
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addSnmpNodes(String objXml) throws Exception
    {
        DefaultDal<SnmpNodesEntity> dal = new DefaultDal<SnmpNodesEntity>(SnmpNodesEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateSnmpNodes(String objXml) throws Exception
    {
        DefaultDal<SnmpNodesEntity> dal = new DefaultDal<SnmpNodesEntity>(SnmpNodesEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteSnmpNodes(int keyId) throws Exception
    {
        SnmpNodesDal sndal = new SnmpNodesDal();
        sndal.deleteById(keyId);
    }
}
