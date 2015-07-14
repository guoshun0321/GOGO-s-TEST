package jetsennet.jbmp.dataaccess;

import java.util.HashMap;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.SysconfigEntity;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;

/**
 * @author ？
 */
public class SysconfigDal extends DefaultDal<SysconfigEntity>
{
    private static final Logger logger = Logger.getLogger(SysconfigDal.class);

    /**
     * 构造方法
     */
    public SysconfigDal()
    {
        super(SysconfigEntity.class);
    }

    @Override
    public int insertXml(String xml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(xml, "");
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        String sql = exec.getSqlParser().getInsertCommandString(getTableName(), getSqlFields(map, false));
        logger.debug(sql);
        int re = exec.executeNonQuery(sql);
        return re;
    }

    @Override
    public int updateXml(String xml) throws Exception
    {
        HashMap<String, String> map = SerializerUtil.deserialize(xml, "");
        return update(getSqlFields(map, false), getKeyCondition(map));
    }

    /**
     * @param name 名称
     * @param defaultValue 默认值
     * @return 结果
     */
    @Transactional
    public String getConfigData(String name, String defaultValue)
    {
        try
        {
            SysconfigEntity entity =
                get(new SqlCondition(tableInfo.keyColumnName, name, SqlLogicType.And, SqlRelationType.Equal, getParamType(tableInfo.keyColumn
                    .getType())));
            if (entity != null && entity.getData() != null)
            {
                return entity.getData();
            }
        }
        catch (Exception e)
        {
            logger.error("获取系统参数异常", e);
        }
        return defaultValue;
    }

    /**
     * @param name 名称
     * @param defaultValue 默认值
     * @return 结果
     */
    @Transactional
    public int getConfigData(String name, int defaultValue)
    {
        try
        {
            SysconfigEntity entity =
                get(new SqlCondition(tableInfo.keyColumnName, name, SqlLogicType.And, SqlRelationType.Equal, getParamType(tableInfo.keyColumn
                    .getType())));
            if (entity != null && entity.getData() != null)
            {
                return Integer.parseInt(entity.getData());
            }
        }
        catch (Exception e)
        {
            logger.error("获取系统参数异常", e);
        }
        return defaultValue;
    }

    /**
     * @param name 名称
     * @param defaultValue 默认值
     * @return 结果
     */
    @Transactional
    public long getConfigData(String name, long defaultValue)
    {
        try
        {
            SysconfigEntity entity =
                get(new SqlCondition(tableInfo.keyColumnName, name, SqlLogicType.And, SqlRelationType.Equal, getParamType(tableInfo.keyColumn
                    .getType())));
            if (entity != null && entity.getData() != null)
            {
                return Long.parseLong(entity.getData());
            }
        }
        catch (Exception e)
        {
            logger.error("获取系统参数异常", e);
        }
        return defaultValue;
    }
}
