/*
 * 时间：2012年2月13日
 * 描述：公告管理业务方法
 * 作者：郭世平
 */
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.AnnouncementDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.AnnouncementEntity;

/**
 * @author？
 */
public class Announcement
{
    /**
     * 添加
     * @param entity 实体
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addAnnouncement(AnnouncementEntity entity) throws Exception
    {
        AnnouncementDal dal = new AnnouncementDal();
        return "" + dal.insert(entity);
    }

    /**
     * 更新
     * @param entity 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAnnouncement(AnnouncementEntity entity) throws Exception
    {

        AnnouncementDal dal = new AnnouncementDal();
        dal.update(entity);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateAnnouncement(String objXml) throws Exception
    {
        AnnouncementDal dal = new AnnouncementDal();
        dal.updateXml(objXml);
    }

    /**
     * 更新是否置顶标识
     * @throws Exception 异常
     */
    @Business
    public void updateBySql() throws Exception
    {
        String sql = "UPDATE BMP_ANNOUNCEMENT SET IS_TOP = 0 WHERE IS_TOP = 1";
        AnnouncementDal dal = new AnnouncementDal();
        dal.update(sql);
    }

    /**
     * 删除
     * @param key id
     * @throws Exception 异常
     */
    @Business
    public void deleteAnnouncement(int key) throws Exception
    {
        AnnouncementDal dal = new AnnouncementDal();
        dal.delete(key);
    }
}
