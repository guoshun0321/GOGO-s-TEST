package jetsennet.orm.executor.resultset;

import jetsennet.orm.tableinfo.TableInfo;

public class ResultSetHandleFactory
{
    /**
     * 返回List<T>
     * @param cls
     * @param tableInfo
     * @return
     */
    //    public static final <T> ResultSetHandleListPojo<T> getPojoHandle(Class<T> cls, TableInfo tableInfo)
    //    {
    //        return new ResultSetHandleListPojo<T>(cls, tableInfo);
    //    }

    /**
     * 返回T
     * @param cls
     * @param tableInfo
     * @return
     */
    //    public static final <T> ResultSetHandlePojo<T> getPojoSingleHandle(Class<T> cls, TableInfo tableInfo)
    //    {
    //        return new ResultSetHandlePojo<T>(cls, tableInfo);
    //    }
    //
    //    public static final ResultSetHandleJson getJsonHandle()
    //    {
    //        return new ResultSetHandleJson();
    //    }
    //
    public static final ResultSetHandleJson getJsonHandle(int count, int cur)
    {
        return new ResultSetHandleJson(true, count, cur);
    }

    //    public static final ResultSetHandleXml getXmlHandle()
    //    {
    //        return new ResultSetHandleXml();
    //    }

    //
    //    public static final ResultSetHandleDocument getDocumentHandle()
    //    {
    //        return new ResultSetHandleDocument();
    //    }
    //
    public static final ResultSetHandleXml getXmlHandle(int count, int cur)
    {
        return new ResultSetHandleXml(true, count, cur);
    }

    //
    //    public static final ResultSetHandleDocument getDocumentHandle(int count, int cur)
    //    {
    //        return new ResultSetHandleDocument(true, count, cur);
    //    }
    //
    //    public static final ResultSetHandleSingle getSingleHandle()
    //    {
    //        return new ResultSetHandleSingle();
    //    }

    public static final ResultSetHandleListString getStringListHandle()
    {
        return new ResultSetHandleListString();
    }

    //    public static final ResultSetHandleListMapStringObject getListMapStringObjectHandle()
    //    {
    //        return new ResultSetHandleListMapStringObject();
    //    }

    public static final ResultSetHandleListMapStringString getListMapStringStringHandle()
    {
        return new ResultSetHandleListMapStringString();
    }

    //    public static final ResultSetHandleMapStringObject getMapStringObjectHandle()
    //    {
    //        return new ResultSetHandleMapStringObject();
    //    }

    public static final ResultSetHandleMapStringString getMapStringStringHandle()
    {
        return new ResultSetHandleMapStringString();
    }

    //    public static final <T> ResultSetHandleObject<T> getObjectHandle(Class<T> targetClz)
    //    {
    //        return new ResultSetHandleObject<T>(targetClz);
    //    }

}
