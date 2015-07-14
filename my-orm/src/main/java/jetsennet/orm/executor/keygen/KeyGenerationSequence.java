package jetsennet.orm.executor.keygen;

import java.sql.Types;

import jetsennet.orm.executor.resultset.RowsResultSetExtractor;
import jetsennet.orm.session.SessionBase;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.UncheckedOrmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 利用数据库特性来获取顺序ID，目前只支持ORACLE
 * 
 * @author 郭祥
 */
public class KeyGenerationSequence extends KeyGeneration
{

    private static final Logger logger = LoggerFactory.getLogger(KeyGenerationSequence.class);

    @Override
    public Long[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        Long[] retval = new Long[num];
        try
        {
            TableInfo tableInfo = session.getTableInfo(tableName);
            FieldInfo fieldInfo = tableInfo.getFieldInfo(fieldName);
            if (fieldInfo == null)
            {
                throw new UncheckedOrmException(String.format("表%s不存在字段%s：", tableName, fieldName));
            }
            int fieldType = fieldInfo.getSqlType();
            if (fieldType == Types.INTEGER || fieldType == Types.BIGINT || fieldType == Types.NUMERIC)
            {
                String sql = session.getTransform().getSequenceNextValString(tableName);
                if (session.isDebug())
                {
                    logger.debug(sql);
                }
                for (int i = 0; i < num; i++)
                {
                    retval[i] = session.query(sql, new RowsResultSetExtractor<Long>(Long.class)).get(0).longValue();
                }
            }
            else
            {
                throw new UncheckedOrmException("非整形主键 : " + fieldName);
            }
        }
        catch (Exception e)
        {
            throw new UncheckedOrmException(e);
        }
        return retval;
    }

}
