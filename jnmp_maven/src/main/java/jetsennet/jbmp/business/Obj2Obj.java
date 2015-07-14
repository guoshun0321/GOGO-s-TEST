/************************************************************************
日 期：2012-3-31
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.Obj2GroupDal;
import jetsennet.jbmp.dataaccess.Obj2ObjDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.Obj2GroupEntity;
import jetsennet.jbmp.entity.Obj2ObjEntity;
import jetsennet.util.StringUtil;

/**
 * @author yl
 */
public class Obj2Obj
{

    /**
     * 新增对象关联
     * @param upTSObjId 参数
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String insertObj2Obj(int upTSObjId, String objXml) throws Exception
    {
        String result = "";
        try
        {
            Obj2ObjDal dal = ClassWrapper.wrapTrans(Obj2ObjDal.class);
            Obj2GroupDal dalGroup = ClassWrapper.wrapTrans(Obj2GroupDal.class);

            // 先删除原来的数据
            deleteObj2ObjByField1(upTSObjId);

            Document doc = DocumentHelper.parseText(objXml);

            // 对象关联关系
            Set<String> lowIdSet = new HashSet<String>(); // 存储下行码流对象ID
            List list = doc.selectNodes("/DataSource/OBJ2OBJ");
            Iterator it = list.iterator();
            while (it.hasNext())
            {
                Element ele = (Element) it.next();
                String objId = ele.attribute("OBJ_ID").getValue();
                String nextId = ele.attribute("NEXT_ID").getValue();
                String field1 = ele.attribute("FIELD_1").getValue();
                String field2 = ele.attribute("FIELD_2").getValue();

                Obj2ObjEntity entity = new Obj2ObjEntity();
                entity.setObjId(Integer.valueOf(objId));
                entity.setNextId(Integer.valueOf(nextId));
                entity.setField_1(Integer.valueOf(field1));

                if (!StringUtil.isNullOrEmpty(field2) && !"NULL".equals(field2))
                {
                    lowIdSet.add(field2);
                    entity.setField_2(Integer.valueOf(field2));
                }

                dal.insert(entity);
            }

            // 对象与组的关联关系
            if (lowIdSet != null && lowIdSet.size() > 0)
            {
                Set<String> objSet;
                int size = 0;
                String objIds;
                for (String lowId : lowIdSet)
                {
                    // 获取默认的组ID
                    String groupId = new Object2Group().getDefaultGroupIdOfObject(lowId);

                    if (!StringUtil.isNullOrEmpty(groupId))
                    {
                        // 删除原来的关联关系
                        dalGroup.delete("DELETE FROM BMP_OBJ2GROUP WHERE GROUP_ID=" + groupId + " AND USE_TYPE=2");

                        // 获取该对象为下行码流的所有对象关系
                        objSet = new HashSet<String>();
                        List<Obj2ObjEntity> lst = dal.getLst("SELECT * FROM BMP_OBJ2OBJ WHERE FIELD_2 = " + lowId);
                        if (lst != null && lst.size() > 0)
                        {
                            for (Obj2ObjEntity entity2 : lst)
                            {
                                objSet.add(String.valueOf(entity2.getObjId()));
                                objSet.add(String.valueOf(entity2.getNextId()));
                            }

                        }

                        if (objSet != null && objSet.size() > 0)
                        {
                            size = 0;
                            objIds = "";
                            for (String objId : objSet)
                            {
                                if (size < (objSet.size() - 1))
                                {
                                    objIds += objId + ",";
                                }
                                else
                                {
                                    objIds += objId;
                                }

                                size++;
                            }

                            // 获取其中为设备的对象ID
                            if (!StringUtil.isNullOrEmpty(objIds))
                            {
                                Set<Integer> obj = new HashSet<Integer>();

                                List<MObjectEntity> objLst =
                                    new DefaultDal<MObjectEntity>(MObjectEntity.class)
                                        .getLst("SELECT * FROM BMP_OBJECT WHERE CLASS_GROUP<>10 AND CLASS_GROUP<>20 AND OBJ_ID IN (" + objIds + ")");
                                if (objLst != null && objLst.size() > 0)
                                {
                                    for (MObjectEntity objEntity : objLst)
                                    {
                                        obj.add(objEntity.getObjId());
                                    }
                                }

                                // 插入新关联到表
                                if (obj != null && obj.size() > 0)
                                {
                                    Obj2GroupEntity og;
                                    for (Integer oid : obj)
                                    {
                                        og = new Obj2GroupEntity();
                                        og.setObjId(oid);
                                        og.setGroupId(Integer.valueOf(groupId));
                                        og.setUseType(2);

                                        dalGroup.insert(og);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据FIELD_1删除对象关系
     * @param field1 参数
     * @throws Exception 异常
     */
    @Business
    public void deleteObj2ObjByField1(int field1) throws Exception
    {
        DefaultDal<Obj2ObjEntity> dal = new DefaultDal<Obj2ObjEntity>(Obj2ObjEntity.class);
        dal.delete("DELETE FROM BMP_OBJ2OBJ WHERE FIELD_1=" + field1);
    }
}
