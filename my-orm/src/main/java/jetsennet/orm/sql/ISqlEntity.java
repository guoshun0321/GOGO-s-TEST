package jetsennet.orm.sql;

import jetsennet.orm.transform.ITransform2Sql;

public interface ISqlEntity
{

    /**
     * 转换为Sql语句
     * 
     * @return
     */
    public String toSql(ITransform2Sql trans);

}
