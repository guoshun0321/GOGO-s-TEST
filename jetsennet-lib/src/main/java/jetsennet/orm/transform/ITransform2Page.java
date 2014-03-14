package jetsennet.orm.transform;

import jetsennet.orm.sql.Sql;

public interface ITransform2Page
{

    public Sql4Page trans(Sql sql, int page, int pageSize);

}
