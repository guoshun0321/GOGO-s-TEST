package jetsennet.jbmp.business;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.IReadHandle;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.Knowledge2TypeEntity;
import jetsennet.jbmp.entity.KnowledgeEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.util.SerializerUtil;
import jetsennet.util.StringUtil;

/**
 * @author ？
 */
public class Knowledge
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addKnowledge(String objXml) throws Exception
    {
        DefaultDal<KnowledgeEntity> dal = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
        int knowledgeId = dal.insertXml(objXml);

        // 给知识库文章添加多个分类
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        Object knowledgeTypes = map.get("KNOWLEDGE_TYPES");
        if (knowledgeTypes != null && !"".equals(knowledgeTypes.toString()))
        {
            String[] knowledgeTypeIds = knowledgeTypes.toString().split(",");
            DefaultDal<Knowledge2TypeEntity> knowledge2TypeDal = new DefaultDal<Knowledge2TypeEntity>(Knowledge2TypeEntity.class);
            for (String knowledgeTypeId : knowledgeTypeIds)
            {
                Knowledge2TypeEntity knowledge2Type = new Knowledge2TypeEntity();
                knowledge2Type.setTypeId(Integer.parseInt(knowledgeTypeId));
                knowledge2Type.setKnowledgeId(knowledgeId);
                knowledge2TypeDal.insert(knowledge2Type);
            }
        }

        return "" + knowledgeId;
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateKnowledge(String objXml) throws Exception
    {
        DefaultDal<KnowledgeEntity> dal = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);
        dal.updateXml(objXml);

        // 先删除知识库文章的所有分类，再给知识库文章添加多个分类
        HashMap<String, String> map = SerializerUtil.deserialize(objXml, "");
        // 若只是查看文章，则不处理分类问题
        if (Boolean.parseBoolean(map.get("ONLY_VIEW")))
        {
            return;
        }
        String knowledgeId = map.get("KNOWLEDGE_ID").toString();
        Object knowledgeTypes = map.get("KNOWLEDGE_TYPES");
        DefaultDal<Knowledge2TypeEntity> knowledge2TypeDal = new DefaultDal<Knowledge2TypeEntity>(Knowledge2TypeEntity.class);
        knowledge2TypeDal.delete(new SqlCondition("KNOWLEDGE_ID", knowledgeId, SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (knowledgeTypes != null && !"".equals(knowledgeTypes.toString()))
        {
            String[] knowledgeTypeIds = knowledgeTypes.toString().split(",");
            for (String knowledgeTypeId : knowledgeTypeIds)
            {
                Knowledge2TypeEntity knowledge2Type = new Knowledge2TypeEntity();
                knowledge2Type.setTypeId(Integer.parseInt(knowledgeTypeId));
                knowledge2Type.setKnowledgeId(Integer.parseInt(knowledgeId));
                knowledge2TypeDal.insert(knowledge2Type);
            }
        }
    }

    /**
     * 删除
     * @param keyId id
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public int deleteKnowledge(int keyId) throws Exception
    {
        DefaultDal<KnowledgeEntity> dal = new DefaultDal<KnowledgeEntity>(KnowledgeEntity.class);

        // 先删除知识库文章的分类
        DefaultDal<Knowledge2TypeEntity> knowledge2TypeDal = new DefaultDal<Knowledge2TypeEntity>(Knowledge2TypeEntity.class);
        knowledge2TypeDal.delete(new SqlCondition("KNOWLEDGE_ID", keyId + "", SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));

        return dal.delete(keyId);
    }

    /**
     * 查询文章
     * @param title文章标题
     * @param summary文章摘要
     * @param type文章类别(例如：计算机,监控)类别最多三个
     * @param typeId设备类型ID
     * @param alarmId报警规则ID
     * @param author文章作者
     * @return
     */
    public String queryKnowledge(String title, String summary, String type, String typeId, String alarmId, String createUserId) throws Exception
    {
        final Document resultDoc = DocumentHelper.parseText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><RecordSet></RecordSet>");
        String sql = "";
        String[] typeArray = type.split(",");
        String titleCondition = "";
        String summaryCondition = "";
        String typeIdCondition = "";
        String alarmCondition = "";
        String userCondition = "";

        if (!jetsennet.util.StringUtil.isNullOrEmpty(title))
        {
            titleCondition = "AND A.KNOWLEDGE_TITLE LIKE '%" + title + "%'";
        }
        if (!jetsennet.util.StringUtil.isNullOrEmpty(summary))
        {
            summaryCondition = "AND A.KNOWLEDGE_SUMMARY LIKE '%" + summary + "%'";
        }
        if (!jetsennet.util.StringUtil.isNullOrEmpty(typeId))
        {
            typeIdCondition = "AND A.CLASS_ID = " + typeId;
        }
        if (!jetsennet.util.StringUtil.isNullOrEmpty(alarmId))
        {
            alarmCondition = "AND A.ALARM_ID = " + alarmId;
        }
        if (!jetsennet.util.StringUtil.isNullOrEmpty(createUserId))
        {
            userCondition = "AND A.CREATE_USERID = " + createUserId;
        }

        if (typeArray.length == 1 && typeArray[0] == "")
        {
            sql =
                MessageFormat.format("SELECT A.*,U.USER_NAME FROM BMP_KNOWLEDGE A INNER JOIN UUM_USER U ON U.ID=A.CREATE_USERID WHERE 1=1 {0} {1} {2} {3} {4} ORDER BY A.UPDATE_TIME DESC",
                    titleCondition,
                    summaryCondition,
                    typeIdCondition,
                    alarmCondition,
                    userCondition);
            System.out.println(sql);
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT A.*,U.USER_NAME FROM BMP_KNOWLEDGE A INNER JOIN UUM_USER U ON U.ID=A.CREATE_USERID WHERE A.KNOWLEDGE_ID IN (");
            for (int i = 0; i < typeArray.length; i++)
            {
                if (i != 0)
                {
                    sb.append(" AND A.KNOWLEDGE_ID IN (");
                }
                String temp =
                    MessageFormat.format("SELECT A.KNOWLEDGE_ID FROM BMP_KNOWLEDGE A INNER JOIN BMP_KNOWLEDGE2TYPE KT ON KT.KNOWLEDGE_ID=A.KNOWLEDGE_ID INNER JOIN BMP_KNOWLEDGETYPE T ON KT.TYPE_ID=T.TYPE_ID INNER JOIN UUM_USER U ON U.ID=A.CREATE_USERID WHERE 1=1 AND TYPE_NAME LIKE ''%"
                        + typeArray[i] + "%''" + "{0} {1} {2} {3} {4}",
                        titleCondition,
                        summaryCondition,
                        typeIdCondition,
                        alarmCondition,
                        userCondition);
                sb.append(temp);
                if (i != 0)
                {
                    sb.append(")");
                }
            }
            sb.append(")");
            sql = sb.toString();
        }
        if (!StringUtil.isNullOrEmpty(sql))
        {

            final String[] field =
                { "KNOWLEDGE_ID", "KNOWLEDGE_TITLE", "KNOWLEDGE_SUMMARY", "USER_NAME", "CREATE_TIME", "UPDATE_TIME", "CLICK_COUNT", "COMMENT_COUNT",
                    "CREATE_USERID" };
            DefaultDal.read(sql, new IReadHandle()
            {
                @Override
                public void handle(ResultSet rs) throws Exception
                {
                    while (rs.next())
                    {
                        Element record = resultDoc.getRootElement().addElement("Record");
                        for (int j = 0; j < field.length; j++)
                        {
                            Element filedObj = record.addElement(field[j]);
                            String value = rs.getString(field[j]);
                            if (value == null)
                            {
                                value = "";
                            }
                            filedObj.addText(value);
                        }
                    }
                }
            });
        }
        return resultDoc.asXML();
    }

    public static void main(String[] args) throws Exception
    {
        new Knowledge().queryKnowledge("", "", "", "", "", "");
    }
}
