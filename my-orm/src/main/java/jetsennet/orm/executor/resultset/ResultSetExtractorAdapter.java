package jetsennet.orm.executor.resultset;

import java.sql.ResultSet;

import org.uorm.dao.common.ResultSetExtractor;

public class ResultSetExtractorAdapter<T> extends AbsResultSetHandle<T>
{

    private ResultSetExtractor<T> ext;

    public ResultSetExtractorAdapter(ResultSetExtractor<T> ext)
    {
        this.ext = ext;
    }

    @Override
    public T handle(ResultSet rs) throws Exception
    {
        return ext.extractData(rs);
    }

}
