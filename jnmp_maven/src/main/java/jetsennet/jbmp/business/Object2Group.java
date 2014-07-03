/*************************************************
日 期：2011-12-8
作 者: 郭世平
版 本：v1.3
描 述: 对监控对象对象组关联表的操作
历 史：
 *************************************************/
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.Obj2GroupEntity;

/**
 * @author ?
 */
public class Object2Group
{
    /**
     * 从组中删除对象
     * @param keyId 对象ID
     * @throws Exception 异常
     */
    @Business
    public void deleteObjectById(int keyId) throws Exception
    {
        DefaultDal<Obj2GroupEntity> dal = new DefaultDal<Obj2GroupEntity>(Obj2GroupEntity.class);
        dal.delete("delete from BMP_OBJ2GROUP where OBJ_ID=" + keyId);
    }

    /**
     * 从组中批量删除对象
     * @param groupId 组ID
     * @param objIds 对象ID数组
     * @throws Exception 异常
     */
    @Business
    public void deleteObjectFromGroup(int groupId, int[] objIds) throws Exception
    {
        StringBuilder objIdStrb = new StringBuilder();
        for (int i = 0; i < objIds.length; i++)
        {
            objIdStrb.append(objIds[i] + ",");
            if (i == objIds.length - 1)
            {
                objIdStrb.append(objIds[i] + "");
            }
        }
        DefaultDal<Obj2GroupEntity> dal = new DefaultDal<Obj2GroupEntity>(Obj2GroupEntity.class);
        dal.delete("delete from BMP_OBJ2GROUP where GROUP_ID=" + groupId + " and OBJ_ID in (" + objIdStrb.toString() + ")");
    }

    /**
     * 获取某对象有实际父子关系的组ID
     * @param objId 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String getDefaultGroupIdOfObject(String objId) throws Exception
    {
        DefaultDal<Obj2GroupEntity> dal = new DefaultDal<Obj2GroupEntity>(Obj2GroupEntity.class);

        String groupId = "";
        Obj2GroupEntity ogEntity = dal.get("SELECT * FROM BMP_OBJ2GROUP WHERE USE_TYPE = 1 AND OBJ_ID = " + objId);
        if (ogEntity != null)
        {
            groupId = String.valueOf(ogEntity.getGroupId());
        }

        return groupId;
    }
}
