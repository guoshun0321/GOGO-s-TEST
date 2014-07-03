/************************************************************************
日 期：2012-03-22
作 者: 
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.List;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.KnowledgeTypeEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ?
 */
public class KnowledgeType
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addKnowledgeType(String objXml) throws Exception
    {
        DefaultDal<KnowledgeTypeEntity> dal = new DefaultDal<KnowledgeTypeEntity>(KnowledgeTypeEntity.class);
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateKnowledgeType(String objXml) throws Exception
    {
        DefaultDal<KnowledgeTypeEntity> dal = new DefaultDal<KnowledgeTypeEntity>(KnowledgeTypeEntity.class);
        dal.updateXml(objXml);
    }

    /**
     * 删除该类别及其该类别下的所有类别
     * 
     * @param keyId
     */
    @Business
    public int deleteKnowledgeType(int keyId) throws Exception
    {
        DefaultDal<KnowledgeTypeEntity> dal = new DefaultDal<KnowledgeTypeEntity>(KnowledgeTypeEntity.class);
        List<KnowledgeTypeEntity> childKnowledgeTypes = dal.getLst(new SqlCondition("PARENT_ID", String.valueOf(keyId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric));
        if (childKnowledgeTypes != null && childKnowledgeTypes.size() > 0)
        {
        	throw new Exception("该类别有下级类别存在，请先删除其子类别!");
        }
        return dal.delete(getAllKnowledgeTypeById(keyId));
    }
    
    private String getAllKnowledgeTypeById(int keyId) throws Exception
    {
    	DefaultDal<KnowledgeTypeEntity> dal = new DefaultDal<KnowledgeTypeEntity>(KnowledgeTypeEntity.class);
    	StringBuilder delId = new StringBuilder(); 
    	delId.append(keyId + "");
    	delId.append(",");
    	String sql = "SELECT * FROM BMP_KNOWLEDGETYPE WHERE PARENT_ID = " + keyId;
    	List<KnowledgeTypeEntity> temps = dal.getLst(sql);
    	StringBuilder sb;
    	while(temps != null && temps.size() != 0)
    	{
    		sb = new StringBuilder();
    		for(KnowledgeTypeEntity entity : temps)
    		{
    			delId.append(entity.getTypeId() + "");
    			delId.append(",");
    			sb.append(entity.getTypeId());
    			sb.append(",");
    		}
    		if(sb.length() > 0)
    		{
    			sb.deleteCharAt(sb.length() - 1);
    		}
    		String lsql = "SELECT * FROM BMP_KNOWLEDGETYPE WHERE PARENT_ID IN (" + sb.toString() + ")";
    		temps = dal.getLst(lsql);
    	}
    	String typeIds = delId.deleteCharAt(delId.length() - 1).toString();
    	return "DELETE FROM BMP_KNOWLEDGETYPE WHERE TYPE_ID IN (" + typeIds + ")";
    }
}
