/*
 * 日期：2012年2月13日
 * 描述：公告管理的数据库访问层
 * 作者：郭世平
 */
package jetsennet.jbmp.dataaccess;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.entity.AnnouncementEntity;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * @author ？
 */
public class AnnouncementDal extends DefaultDal<AnnouncementEntity>
{
    /**
     * 构造方法
     */
    public AnnouncementDal()
    {
        super(AnnouncementEntity.class);
    }

    private static final Logger logger = Logger.getLogger(AnnouncementDal.class);

    @Override
    public int insert(AnnouncementEntity entity) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getInsertCommandString(getTableName(), getSqlFields(entity, true));
        logger.debug(sql);
        int re = exec.executeNonQuery(sql);
        if (tableInfo.keyColumn != null)
        {
            return (Integer) tableInfo.keyColumn.get(entity);
        }
        return re;
    }

    @Override
    public int delete(int key) throws Exception
    {
        return delete(getKeyCondition(key));
    }

    @Override
    public int update(AnnouncementEntity entity) throws Exception
    {
        return update(entity, getKeyCondition(entity));
    }
}
