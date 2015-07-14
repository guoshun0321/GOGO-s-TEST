package jetsennet.orm.tableinfo.mapping;

public class ReaderUtils
{

    private static final IObjectReader objReader = new ObjectReader();

    private static final IXmlReader xmlReader = new XmlReader();

    private static final IJsonReader jsonReader = new JsonReader();

    public static IObjectReader getObjectReader()
    {
        return objReader;
    }

    public static IXmlReader getXmlReader()
    {
        return xmlReader;
    }

    public static IJsonReader getJsonReader()
    {
        return jsonReader;
    }

}
