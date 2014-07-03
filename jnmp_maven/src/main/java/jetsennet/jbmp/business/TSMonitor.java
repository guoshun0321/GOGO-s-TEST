/************************************************************************
日 期：2012-3-31
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.business;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import jetsennet.jbmp.dataaccess.Obj2ObjDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.Obj2ObjEntity;

/**
 * @author yl
 */
public class TSMonitor
{
    private List<Integer> visitedObjs; // 已经访问过的对象

    /**
     * 构造函数
     */
    public TSMonitor()
    {

    }

    /**
     * 查询上行二级拓扑图对象关系
     * @param topObjId 查询起点对象ID,即上行码流ID
     * @return 返回格式如：<?xml version="1.0" encoding="UTF-8"?><Objects><Object startObject="2200" endObject="270"/><Object startObject="270"
     *         endObject="274,275"/><Object startObject="275" endObject="276"/><Object startObject="276" endObject="2276"/></Objects>
     * @throws Exception 异常
     */
    public String getObjectUpRelations(int topObjId) throws Exception
    {
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Objects></Objects>";
        Document doc = DocumentHelper.parseText(result);

        try
        {
            visitedObjs = new ArrayList<Integer>(); // 初始化已访问过的对象集合
            List<List<Integer>> list = new ArrayList<List<Integer>>(); // 存放所有的邻接关系

            List<List<Integer>> sub = getObjectUpSubRelations(topObjId, topObjId);
            if (sub != null && sub.size() > 0)
            {
                for (int k = 0; k < sub.size(); k++)
                {
                    list.add(sub.get(k));
                }
            }

            // 组装返回XML
            if (list != null && list.size() > 0)
            {
                for (List<Integer> l : list)
                {
                    if (l != null && l.size() > 1)
                    {
                        Element e = doc.getRootElement().addElement("Object");
                        String endObjs = "";
                        for (int m = 0; m < l.size(); m++)
                        {
                            if (m == 0)
                            {
                                e.addAttribute("startObject", l.get(m).toString());
                            }
                            else
                            {
                                endObjs += l.get(m).toString() + ",";
                            }

                        }

                        if (endObjs.endsWith(","))
                        {
                            endObjs = endObjs.substring(0, endObjs.length() - 1);
                        }

                        e.addAttribute("endObject", endObjs);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return doc.asXML();
    }

    /**
     * 查询上行二级拓扑图对象关系
     * @param parentObjId 上一级起点对象ID
     * @param topObjId 顶点对象ID，即上行码流ID
     * @throws Exception 异常
     * @return 结果
     */
    public List<List<Integer>> getObjectUpSubRelations(int parentObjId, int topObjId) throws Exception
    {
        List<List<Integer>> result = new ArrayList<List<Integer>>();

        List<Integer> list = new ArrayList<Integer>();
        list.add(parentObjId); // 先添加起点
        visitedObjs.add(parentObjId);

        try
        {
            // 先添加当前的邻接点
            Obj2ObjDal dal = ClassWrapper.wrapTrans(Obj2ObjDal.class);
            List<Obj2ObjEntity> lst = dal.getLst("SELECT * FROM BMP_OBJ2OBJ WHERE OBJ_ID=" + parentObjId + " AND FIELD_1=" + topObjId);
            if (lst != null && lst.size() > 0)
            {
                for (Obj2ObjEntity entity : lst)
                {
                    list.add(entity.getNextId());
                }
            }
            result.add(list);

            // 然后针对每个邻接点，做同样操作，找子邻接关系
            if (list != null && list.size() > 1)
            {
                for (int j = 1; j < list.size(); j++)
                {
                    if (!visitedObjs.contains(list.get(j)))
                    {
                        List<List<Integer>> sub = getObjectUpSubRelations(list.get(j), topObjId);
                        if (sub != null && sub.size() > 0)
                        {
                            for (int k = 0; k < sub.size(); k++)
                            {
                                result.add(sub.get(k));
                            }
                        }
                        visitedObjs.add(list.get(j));
                    }

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
     * 查询下行二级拓扑图对象关系
     * @param topObjId 查询起点对象ID,即下行码流ID
     * @return 返回格式如：<?xml version="1.0" encoding="UTF-8"?><Objects><Object startObject="2200" endObject="270"/><Object startObject="270"
     *         endObject="274,275"/><Object startObject="275" endObject="276"/><Object startObject="276" endObject="2276"/></Objects>
     * @throws Exception 异常
     */
    public String getObjectLowRelations(int topObjId) throws Exception
    {
        String result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Objects></Objects>";
        Document doc = DocumentHelper.parseText(result);

        try
        {
            visitedObjs = new ArrayList<Integer>(); // 初始化已访问过的对象集合
            List<List<Integer>> list = new ArrayList<List<Integer>>(); // 存放所有的邻接关系

            List<List<Integer>> sub = getObjectLowSubRelations(topObjId, topObjId);
            if (sub != null && sub.size() > 0)
            {
                for (int k = 0; k < sub.size(); k++)
                {
                    list.add(sub.get(k));
                }
            }

            // 组装返回XML
            if (list != null && list.size() > 0)
            {
                for (List<Integer> l : list)
                {
                    if (l != null && l.size() > 1)
                    {
                        Element e = doc.getRootElement().addElement("Object");
                        String endObjs = "";
                        for (int m = 0; m < l.size(); m++)
                        {
                            if (m == 0)
                            {
                                e.addAttribute("startObject", l.get(m).toString());
                            }
                            else
                            {
                                endObjs += l.get(m).toString() + ",";
                            }

                        }

                        if (endObjs.endsWith(","))
                        {
                            endObjs = endObjs.substring(0, endObjs.length() - 1);
                        }

                        e.addAttribute("endObject", endObjs);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return doc.asXML();
    }

    /**
     * 查询下行二级拓扑图对象关系
     * @param parentObjId 上一级起点对象ID
     * @param topObjId 顶点对象ID，即下行码流ID
     * @throws Exception 异常
     * @return 结果
     */
    public List<List<Integer>> getObjectLowSubRelations(int parentObjId, int topObjId) throws Exception
    {
        List<List<Integer>> result = new ArrayList<List<Integer>>();

        List<Integer> list = new ArrayList<Integer>();
        list.add(parentObjId); // 先添加起点
        visitedObjs.add(parentObjId);

        try
        {
            // 先添加当前的邻接点
            Obj2ObjDal dal = ClassWrapper.wrapTrans(Obj2ObjDal.class);
            List<Obj2ObjEntity> lst = dal.getLst("SELECT * FROM BMP_OBJ2OBJ WHERE NEXT_ID=" + parentObjId);
            if (lst != null && lst.size() > 0)
            {
                for (Obj2ObjEntity entity : lst)
                {
                    list.add(entity.getObjId());
                }
            }
            result.add(list);

            // 然后针对每个邻接点，做同样操作，找子邻接关系
            if (list != null && list.size() > 1)
            {
                for (int j = 1; j < list.size(); j++)
                {
                    if (!visitedObjs.contains(list.get(j)))
                    {
                        List<List<Integer>> sub = getObjectLowSubRelations(list.get(j), topObjId);
                        if (sub != null && sub.size() > 0)
                        {
                            for (int k = 0; k < sub.size(); k++)
                            {
                                result.add(sub.get(k));
                            }
                        }
                        visitedObjs.add(list.get(j));
                    }

                }
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }

        return result;
    }

}
