/************************************************************************
 * 日 期：2011-11-24 
 * 作 者: 余灵 
 * 版 本：v1.3 
 * 描 述: 拓扑图图标相关
 * 历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.sql.ResultSet;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.TopoImageEntity;

/**
 * @author 余灵
 */
public class TopoImage
{

    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int addTopoImage(String objXml) throws Exception
    {
        DefaultDal<TopoImageEntity> dal = new DefaultDal<TopoImageEntity>(TopoImageEntity.class);
        return dal.insertXml(objXml);
    }

    /**
     * 修改
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateTopoImage(String objXml) throws Exception
    {
        DefaultDal<TopoImageEntity> dal = new DefaultDal<TopoImageEntity>(TopoImageEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param keyId 主键ID
     * @throws Exception 异常
     */
    @Business
    public void deleteTopoImage(int keyId) throws Exception
    {
        DefaultDal<TopoImageEntity> dalTopoImage = new DefaultDal<TopoImageEntity>(TopoImageEntity.class);
        try
        {
            String sql = "SELECT IMG_NAME,IS_SYSIMG FROM BMP_TOPOIMAGE WHERE IMG_ID=" + keyId;
            final UploadFile uf = new UploadFile();
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        String imgName = rs.getString("IMG_NAME");
                        int isSysImg = rs.getInt("IS_SYSIMG");
                        if (isSysImg != 1)
                        {
                            uf.deleteUploadFile("jnmp/upload/NodeImage", imgName); // 删除图标文件
                        }
                    }
                }
            });
        }
        catch (Exception ex)
        {
        }
        dalTopoImage.delete(keyId);
    }
}
