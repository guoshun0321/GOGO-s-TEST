package jetsennet.jbmp.util;

/**
 * 辅助类
 * @version 1.0 date 2011-12-15下午1:45:29
 * @author xli
 */
public class SMIBean
{
    /**
     * CIMObjectPath
     */
    private String path;

    /**
     * 供查询的WQL
     */
    private String wql;

    /**
     * key值
     */
    private String key;

    /**
     * key名称
     */
    private String keyValue;

    public String getKeyValue()
    {
        return keyValue;
    }

    public void setKeyValue(String keyValue)
    {
        this.keyValue = keyValue;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getWql()
    {
        return wql;
    }

    public void setWql(String wql)
    {
        this.wql = wql;
    }
}
