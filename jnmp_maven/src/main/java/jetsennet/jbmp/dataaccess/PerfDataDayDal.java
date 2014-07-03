/**********************************************************************
 * 日 期： 2012-3-27
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  PerfDataHourDal.java
 * 历 史： 2012-3-27 创建
 *********************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.PerfDataDayEntity;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.sqlclient.ISqlExecutor;

/**
 * 按天汇总数据操作类
 */
public class PerfDataDayDal extends DefaultDal<PerfDataDayEntity>
{
    private static final Logger logger = Logger.getLogger(PerfDataDayDal.class);

    /**
     * 构造函数
     */
    public PerfDataDayDal()
    {
        super(PerfDataDayEntity.class);
    }

    /**
     * @param objId 对象id
     * @return 结果
     * @throws Exception 异常
     */
    @Transactional
    public long getLastUpdateTime(int objId) throws Exception
    {
        Long retval = 0l;
        String sql = String.format("SELECT MAX(COLL_TIME) FROM BMP_PERFDATADAY WHERE OBJ_ID=%s", objId);
        List lst = getFirstLst(sql);
        if (lst != null && lst.size() > 0)
        {
            Object lastUpdate = lst.get(0);
            retval = ConvertUtil.ObjectToLong(lastUpdate);
            retval = retval == null ? 0 : retval;
        }
        return retval;
    }

    /**
     * @param dayLst 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(List<PerfDataDayEntity> dayLst) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        for (PerfDataDayEntity entity : dayLst)
        {
            String sql = exec.getSqlParser().getInsertCommandString(getTableName(), getSqlFields(entity, true));
            logger.debug(sql);
            exec.executeNonQuery(sql);
        }
    }
}
