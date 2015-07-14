package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.ImageClassEntity;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author?
 */
public class ImageClass
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addImageClass(String objXml) throws Exception
    {
        DefaultDal<ImageClassEntity> dal = new DefaultDal<ImageClassEntity>(ImageClassEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateImageClass(String objXml) throws Exception
    {
        DefaultDal<ImageClassEntity> dal = new DefaultDal<ImageClassEntity>(ImageClassEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId id
     * @throws Exception 异常
     */
    @Business
    public void deleteImageClass(int keyId) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        exec.executeNonQuery("UPDATE BMP_TOPOIMAGE SET CLASS_ID=NULL WHERE CLASS_ID=" + keyId);
        DefaultDal<ImageClassEntity> dal = new DefaultDal<ImageClassEntity>(ImageClassEntity.class);
        dal.delete(keyId);
    }
}
