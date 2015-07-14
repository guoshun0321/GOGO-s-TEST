package jetsennet.jbmp.business;

import java.util.Map;
import java.util.TreeMap;

import jetsennet.jbmp.dataaccess.CollectTaskDal;
import jetsennet.jbmp.dataaccess.CollectorDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CollectTaskEntity;
import jetsennet.jbmp.entity.CollectorEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.servlets.BMPServletContextListener;

/**
 * @author ?
 */
public class CollectTask
{
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addCollectTask(String objXml) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateCollectTask(String objXml) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        dal.updateXml(objXml);
    }

    /**
     * @param taskId 任务ID
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String getCollectTaskInfo(int taskId) throws Exception
    {
        CollectTaskDal taskDal = new CollectTaskDal();
        CollectTaskEntity task = taskDal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object callRe =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "getCollectTaskInfo", new Object[] { task },
                    new Class[] { CollectTaskEntity.class }, true);
            if (callRe == null)
            {
                throw new Exception("采集器处理失败。");
            }

            Map<Integer, Integer> stateMap = new TreeMap<Integer, Integer>((Map<Integer, Integer>) callRe);
            MObjectDal objDal = new MObjectDal();
            StringBuilder result = new StringBuilder();
            result.append("<RecordSet>");
            for (Map.Entry<Integer, Integer> entry : stateMap.entrySet())
            {
                int objId = entry.getKey();
                MObjectEntity obj = objDal.get(objId);
                if (obj != null)
                {
                    result.append("<Record>");
                    result.append(String.format("<%1$s>%2$s</%1$s>", "OBJ_ID", objId));
                    result.append(String.format("<%1$s>%2$s</%1$s>", "OBJ_NAME", obj.getObjName()));
                    result.append(String.format("<%1$s>%2$s</%1$s>", "COLL_STATE", entry.getValue()));
                    result.append("</Record>");
                }
            }
            result.append("</RecordSet>");
            return result.toString();
        }
        else
        {
            throw new Exception("无法连接到指定采集器。");
        }
    }

    /**
     * @param taskId 任务id
     * @throws Exception 异常
     */
    @Business
    public void startCollectTask(int taskId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object result =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "startCollectTask", new Object[] { task },
                    new Class[] { CollectTaskEntity.class }, true);
            if (result == null)
            {
                throw new Exception("采集器处理超时，正在处理中，请稍候。");
            }
            else if (result.equals(false))
            {
                throw new Exception("采集器处理失败。");
            }
        }
        else
        {
            throw new Exception("无法连接到指定采集器。");
        }
    }

    /**
     * @param objId 对象ID
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void startCollectTask(int taskId, int objId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object result =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "startCollectTask", new Object[] { task, objId },
                    new Class[] { CollectTaskEntity.class, int.class }, true);
            if (result == null)
            {
                throw new Exception("采集器处理超时，正在处理中，请稍候。");
            }
            else if (result.equals(false))
            {
                throw new Exception("采集器处理失败，或该对象没有配置采集指标。");
            }
        }
        else
        {
            throw new Exception("无法连接到指定采集器。");
        }
    }

    /**
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void stopCollectTask(int taskId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object result =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "stopCollectTask", new Object[] { task },
                    new Class[] { CollectTaskEntity.class }, true);
            if (result == null)
            {
                throw new Exception("采集器处理超时，正在处理中，请稍候。");
            }
            else if (result.equals(false))
            {
                throw new Exception("采集器处理失败。");
            }
        }
        else
        {
            throw new Exception("无法连接到指定采集器。");
        }
    }

    /**
     * @param objId 对象ID
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void stopCollectTask(int taskId, int objId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object result =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "stopCollectTask", new Object[] { task, objId },
                    new Class[] { CollectTaskEntity.class, int.class }, true);
            if (result == null)
            {
                throw new Exception("采集器处理超时，正在处理中，请稍候。");
            }
            else if (result.equals(false))
            {
                throw new Exception("采集器处理失败。");
            }
        }
        else
        {
            throw new Exception("无法连接到指定采集器。");
        }
    }

    /**
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void restartCollectTask(int taskId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            Object result =
                BMPServletContextListener.getInstance().callRemote(task.getCollId(), "restartCollectTask", new Object[] { task },
                    new Class[] { CollectTaskEntity.class }, true);
            if (result == null)
            {

            }
            else if (result.equals(false))
            {
                throw new Exception("采集器处理失败。");
            }
        }
        else
        {
            CollectorDal cdal = new CollectorDal();
            CollectorEntity colltor = cdal.get(task.getCollId());
            throw new Exception("无法连接到指定采集器。" + (colltor == null ? "采集器不存在！" : ("采集器：" + colltor.getCollName())));
        }
    }

    /**
     * 删除
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void deleteCollectTask(int taskId) throws Exception
    {
        CollectTaskDal dal = new CollectTaskDal();
        CollectTaskEntity task = dal.get(taskId);
        if (BMPServletContextListener.getInstance().isOnline(task.getCollId()))
        {
            BMPServletContextListener.getInstance().callRemote(task.getCollId(), "stopCollectTask", new Object[] { task },
                new Class[] { CollectTaskEntity.class }, true);
        }
        dal.delete(taskId);
    }
}
