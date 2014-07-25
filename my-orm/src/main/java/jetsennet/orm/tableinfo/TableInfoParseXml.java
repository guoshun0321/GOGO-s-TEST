package jetsennet.orm.tableinfo;

import java.io.ByteArrayInputStream;
import java.util.List;

import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.Dom4JUtil;
import jetsennet.util.IOUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableInfoParseXml
{

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(TableInfoParseXml.class);

    public static final TableInfo parse(String tblXml)
    {
        TableInfo retval = null;
        ByteArrayInputStream in = null;
        try
        {
            
            
            in = new ByteArrayInputStream(tblXml.getBytes());
            SAXReader reader = new SAXReader();
            Document doc = reader.read(in);

            Element rootE = doc.getRootElement();
            String tableName = Dom4JUtil.getAttrString(rootE, "name", null, true);
            retval = new TableInfo(null, tableName);

            List<Element> fieldEles = rootE.elements();
            for (Element fieldEle : fieldEles)
            {
                String name = Dom4JUtil.getAttrString(fieldEle, "name", null, true);
                String type = Dom4JUtil.getAttrString(fieldEle, "type", null, true);
                boolean isKey = Dom4JUtil.getAttrTrue(fieldEle, "iskey", false);
                String keygen = Dom4JUtil.getAttrString(fieldEle, "keygen", null, false);

                FieldInfo field = null;
                field = new FieldInfo(name, FieldTypeEnum.ensureClz(FieldTypeEnum.valueOf(type)), FieldTypeEnum.valueOf(type), isKey, keygen);
                retval.field(field);
            }

        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            IOUtil.close(in);
        }
        return retval;
    }
}
