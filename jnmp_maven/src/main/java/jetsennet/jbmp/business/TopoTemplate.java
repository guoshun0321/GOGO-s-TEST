/************************************************************************
日 期：2011-11-2
作 者: 余灵
版 本：v1.3
描 述: 拓扑模板相关
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.dom4j.Document;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.TopoTemplateDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.TopoTemplateEntity;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DbConfig;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlClientObjFactory;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author 余灵
 */
public class TopoTemplate
{
    private ConnectionInfo nmpConnectionInfo;
    private ISqlExecutor sqlExecutor;
    private DefaultDal<TopoTemplateEntity> dal;

    /**
     * 构造函数
     */
    public TopoTemplate()
    {
        nmpConnectionInfo =
            new ConnectionInfo(DbConfig.getProperty("nmp_driver"), DbConfig.getProperty("nmp_dburl"), DbConfig.getProperty("nmp_dbuser"), DbConfig
                .getProperty("nmp_dbpwd"));
        sqlExecutor = SqlClientObjFactory.createSqlExecutor(nmpConnectionInfo);
        dal = new DefaultDal<TopoTemplateEntity>(TopoTemplateEntity.class);
    }

    /**
     * 获取模板
     * @param tempId 模板ID
     * @return 结果
     * @throws Exception 异常
     */
    public String queryTempById(int tempId) throws Exception
    {
        String result = "";
        try
        {
            Document doc = sqlExecutor.fill("SELECT * FROM BMP_TOPOTEMPLATE WHERE TEMP_ID=" + tempId, "DataSource", "BMP_TOPOTEMPLATE");

            List list = doc.selectNodes("/DataSource/BMP_TOPOTEMPLATE");
            Element ele = (Element) list.get(0);
            String filePath = ele.element("TEMP_INFO").getText(); // 文件路径

            String mapInfo = new TopoXmlFile().readTopoMapFile(filePath); // 读取XML文件
            if (!"-1".equals(mapInfo) && !"-2".equals(mapInfo))
            {
                Element e = ele.addElement("XML_INFO"); // 为返回XML添加新节点XML_INFO表示该图的XML信息
                e.setText(mapInfo);
                result = doc.asXML();
            }
            else
            {
                result = mapInfo;
            }
        }
        catch (Exception ex)
        {
            result = "-1";
        }

        return result;
    }

    /**
     * 添加
     * @param objXml 参数
     * @return 返回新的模板ID； -1：发生异常；
     * @throws Exception 异常
     */
    public String addTopoTemplate(String objXml) throws Exception
    {
        String result = "";

        try
        {
            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            SqlCondition cond = new SqlCondition("TEMP_NAME", model.get("TEMP_NAME"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            SqlCondition cond2 = new SqlCondition("TEMP_TYPE", model.get("TEMP_TYPE"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            if (dal.isExist(dal.getTableName(), cond, cond2) > 0)
            {
                result = "-3"; // 已存在同名模板
            }
            else
            {
                String tempNewId = String.valueOf(sqlExecutor.getNewId(TopoTemplateDal.TABLE_NAME));
                model.put(TopoTemplateDal.PRIMARY_KEY, tempNewId);
                model.put("TEMP_ICON", "Temp_" + tempNewId + ".jpg");

                String xmlInfo = model.get("TEMP_INFO");
                String tempType = model.get("TEMP_TYPE"); // 模板类型。10：拓扑图模板；20：分类模板；30：设备模板；

                // 将xml信息保存为文件,并返回保存路径
                String path = new TopoXmlFile().writeTopoMapFile(tempType, tempNewId, xmlInfo);
                if (!"-1".equals(path)) // 如果成功
                {
                    model.put("TEMP_INFO", path); // 更改MAP_INFO的值，存相对路径;

                    dal.insert(model, false);
                }
                else
                {
                    result = path;
                }

                result = tempNewId;
            }
        }
        catch (Exception ex)
        {
            result = "-1";
        }

        return result;
    }

    /**
     * 修改模板信息
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String updateTopoTemplate(String objXml) throws Exception
    {
        String result = "";

        try
        {
            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            SqlCondition cond = new SqlCondition("TEMP_NAME", model.get("TEMP_NAME"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            SqlCondition cond2 = new SqlCondition("TEMP_TYPE", model.get("TEMP_TYPE"), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.String);
            SqlCondition cond3 = new SqlCondition("TEMP_ID", model.get("TEMP_ID"), SqlLogicType.And, SqlRelationType.NotEqual, SqlParamType.Numeric);
            if (dal.isExist(dal.getTableName(), cond, cond2, cond3) > 0)
            {
                result = "-3"; // 已存在同名模板
            }
            else
            {
                // 对缺省激活的模板，先将其他缺省置为激活，同一个类型的模板中，一个关联ID只允许有一个缺省模板
                if (model.get("TEMP_STATE") != null && model.get("TEMP_TYPE") != null && model.get("RELATE_ID") != null)
                {
                    if ("10".equals(model.get("TEMP_STATE")))
                    {
                        sqlExecutor.executeNonQuery("UPDATE BMP_TOPOTEMPLATE SET TEMP_STATE=11 WHERE TEMP_STATE=10 AND TEMP_TYPE='"
                            + model.get("TEMP_TYPE") + "' AND RELATE_ID='" + model.get("RELATE_ID") + "'");
                    }
                }

                result = String.valueOf(dal.update(model));
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

    /**
     * 修改模板XML 若传入objXml中"TEMP_INFO"为空，代表不修改模板XML文件； 若不为空，代表要修改XML文件； 若修改了XML文件，则数据库中的路径也要修改； 若未修改XML文件，则读取原XML文件信息；
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    public String updateTopoTemplateXml(String objXml) throws Exception
    {
        String result = "";

        try
        {
            HashMap<String, String> model = SerializerUtil.deserialize(objXml, "");

            if ((model.get("TEMP_INFO") != null) && (!"".equals(model.get("TEMP_INFO"))))
            {
                String tempInfo = model.get("TEMP_INFO"); // 如果存在新的文件信息，则赋新值
                String tempType = model.get("TEMP_TYPE");
                String path = new TopoXmlFile().writeTopoMapFile(tempType, model.get(TopoTemplateDal.PRIMARY_KEY), tempInfo);

                if (!"-1".equals(path)) // 如果成功
                {
                    model.put("TEMP_INFO", path); // 更改TEMP_INFO的值，存相对路径

                    dal.update(model);
                }
                else
                {
                    result = path;
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

    /**
     * 删除
     * @param keyId 模板ID
     * @return 1：删除成功； -1：删除不成功或异常； -2：图标文件不存在；
     * @throws Exception 异常
     */
    @Business
    public String deleteTopoTemplate(int keyId) throws Exception
    {
        String result = "";

        try
        {
            // 先删除XML文件
            TopoTemplateEntity entity = dal.get(keyId);
            if (entity != null)
            {
                String filePath = entity.getTempInfo();

                if (!StringUtil.isNullOrEmpty(filePath))
                {
                    result = new TopoXmlFile().deleteTopoMapFile(filePath);
                }
            }

            // 删除图标文件
            result = deleteTopoTempIconFile(String.valueOf(keyId));

            // 删除
            result = String.valueOf(dal.delete(keyId));
        }
        catch (Exception ex)
        {
            result = "-1";
        }

        return result;
    }

    /**
     * 在服务器上写拓扑图模板的图标文件
     * @param fileName 文件名
     * @param imgInfo 模板的图片信息
     * @return 返回该模板图片的路径； -1：异常；
     * @throws Exception 异常
     */
    public String writeTopoTempFile(String fileName, String imgInfo) throws Exception
    {
        try
        {
            String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
            String fName = "Temp_" + fileName + ".jpg"; // 文件名
            String sPath = "jnmp/upload/TempImage/"; // 指定保存路径

            File f = new File(sRequestPath + sPath + fName);

            // 将byte数组转化为BufferedImage
            InputStream in = new ByteArrayInputStream(imgInfo.getBytes());
            BufferedImage bImageFromConvert = ImageIO.read(in);

            ImageIO.write(bImageFromConvert, "jpg", f);

            return sPath + fName;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "-1";
        }
    }

    /**
     * 从服务器上删除拓扑图模板的图标文件
     * @param fileId 模板ID
     * @return 1：删除成功； -1：删除不成功或异常； -2：图标文件不存在；
     * @throws Exception 异常
     */
    public String deleteTopoTempIconFile(String fileId) throws Exception
    {
        String sReturn = "";

        try
        {
            String sRequestPath = java.net.URLDecoder.decode(this.getClass().getClassLoader().getResource("../../").getPath(), "UTF-8");
            String fName = "Temp_" + fileId + ".jpg"; // 文件名
            String sPath = "jnmp/upload/TempImage/"; // 指定保存路径
            String path = sRequestPath + sPath + fName;
            File f = new File(path);

            if (f.exists())
            {
                if (f.isFile())
                {
                    if (f.canWrite())
                    {
                        boolean flag = f.delete();
                        if (flag)
                        {
                            sReturn = "1";
                        }
                        else
                        {
                            sReturn = "-1";
                        }
                    }
                }
            }
            else
            {
                sReturn = "-2"; // 文件不存在
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            sReturn = "-1"; // 删除文件异常
        }

        return sReturn;
    }

}
