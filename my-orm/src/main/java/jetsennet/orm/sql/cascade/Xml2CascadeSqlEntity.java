package jetsennet.orm.sql.cascade;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.IOUtil;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将XML转换为批量操作实体
 * 
 * @author 郭祥
 */
public class Xml2CascadeSqlEntity
{

    /**
     * 记载SQL操作类型
     */
    public static final String ACTION = "action";
    /**
     * 该SQL操作是否会影响别的SQL操作，主要用于级联删除
     */
    public static final String AFFECTED = "affected";
    /**
     * 是否自动生成主键
     */
    public static final String AUTOKEY = "autokey";
    /**
     * 是否自动生成主键
     */
    public static final String FILTER = "filter";
    /**
     * SQL条件设置
     */
    public static final String FILTER_ELEMENT_NAME = "INFO#WHERE";

    private static final Logger logger = LoggerFactory.getLogger(Xml2CascadeSqlEntity.class);

    public static final CascadeSqlEntity parse(String xml)
    {
        CascadeSqlEntity retval = null;
        BufferedInputStream in = null;
        try
        {
            SAXReader builder = new SAXReader();
            in = new BufferedInputStream(new ByteArrayInputStream(xml.getBytes()));
            Document doc = builder.read(in);

            Element root = doc.getRootElement();
            retval = handleElement(root);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
            throw new UncheckedOrmException(ex);
        }
        finally
        {
            IOUtil.close(in);
            in = null;
        }
        return retval;
    }

    private static CascadeSqlEntity handleElement(Element root)
    {
        //        // 数据库表名
        //        String tableName = root.getName();
        //        // SQL类型
        //        String action = JDomUtil.getAttrString(root, ACTION, null, true);
        //        SqlTypeEnum type = SqlTypeEnum.valueOfIgnoreCase(action);
        //
        //        // 构建实体
        //        CascadeSqlEntity entity = null;
        //        switch (type)
        //        {
        //        case INSERT:
        //            entity = new CascadeSqlInsertEntity();
        //            entity.setTableName(tableName);
        //            entity.setType(type);
        //            boolean autoKey = JDomUtil.getAttrBoolean(root, AUTOKEY, true, false);
        //            ((CascadeSqlInsertEntity) entity).setAutoKey(autoKey);
        //            break;
        //        case UPDATE:
        //            entity = new CascadeSqlUpdateEntity();
        //            entity.setTableName(tableName);
        //            entity.setType(type);
        //            break;
        //        case DELETE:
        //            entity = new CascadeSqlDeleteEntity();
        //            entity.setTableName(tableName);
        //            entity.setType(type);
        //            String filterName = JDomUtil.getAttrString(root, FILTER, null, true);
        //            ((CascadeSqlDeleteEntity) entity).setFilterFiled(filterName);
        //            boolean affect = JDomUtil.getAttrBoolean(root, AFFECTED, false, false);
        //            ((CascadeSqlDeleteEntity) entity).setAffected(affect);
        //            break;
        //        case SELECT:
        //            break;
        //        default:
        //            break;
        //        }
        //
        //        // 填充
        //        List<Element> eles = root.getChildren();
        //        for (Element ele : eles)
        //        {
        //            String fieldName = ele.getName();
        //            String fieldAction = JDomUtil.getAttrString(ele, "action", null, false);
        //
        //            if (fieldAction == null)
        //            {
        //                // 字段
        //                entity.addValue(fieldName, ele.getText());
        //            }
        //            else
        //            {
        //                // 子SQL
        //                entity.addSub(handleElement(ele));
        //            }
        //        }
        //        return entity;
        return null;
    }
}
