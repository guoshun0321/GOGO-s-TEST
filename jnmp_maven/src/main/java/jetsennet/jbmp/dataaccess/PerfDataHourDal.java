/**********************************************************************
 * 日 期： 2012-3-27
 * 作 者:  梁洪杰
 * 版 本： v1.3
 * 描 述:  PerfDataHourDal.java
 * 历 史： 2012-3-27 创建
 *********************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.PerfDataHourEntity;
import jetsennet.jbmp.util.ConvertUtil;
import jetsennet.sqlclient.ISqlExecutor;

import org.apache.log4j.Logger;

/**
 * 按小时汇总数据操作类
 */
public class PerfDataHourDal extends DefaultDal<PerfDataHourEntity>
{
    private static final Logger logger = Logger.getLogger(PerfDataHourDal.class);

    /**
     * 构造函数
     */
    public PerfDataHourDal()
    {
        super(PerfDataHourEntity.class);
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
        String sql = String.format("SELECT MAX(COLL_TIME) FROM BMP_PERFDATAHOUR WHERE OBJ_ID=%s", objId);
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
     * @param hourLst 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insert(List<PerfDataHourEntity> hourLst) throws Exception
    {
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        for (PerfDataHourEntity entity : hourLst)
        {
            String sql = exec.getSqlParser().getInsertCommandString(getTableName(), getSqlFields(entity, true));
            logger.debug(sql);
            exec.executeNonQuery(sql);
        }
    }

    public static void main(String[] args) throws Exception
    {
        PerfDataHourEntity entity = new PerfDataHourEntity();
        entity.setObjAttrId(29);
        entity.setObjId(1);
        entity.setCollTime(1363161600000l);
        entity.setMinValue(0.0);
        entity.setValue(6.872030651340962E-4);
        entity.setMaxValue(0.04193103448275792);
        PerfDataHourDal dal = new PerfDataHourDal();
        List<PerfDataHourEntity> hourLst = new ArrayList<PerfDataHourEntity>();
        hourLst.add(entity);
        dal.insert(hourLst);
    }
}
