/**
 * 日 期：2012-6-6
 * 作 者: 何岳军
 * 版 本：v
 * 描 述: 采集日志业务方法
 * 历 史：
 */
package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.CollectTaskLogDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jbmp.entity.CollectTaskLogEntity;

/**
 * @author ?
 */
public class CollectTaskLog
{	
    /**
     * 新增
     * @param objXml 参数
     * @return 结果
     * @throws Exception 异常
     */
    @Business
    public String addCollectTaskLog(String objXml) throws Exception
    {
        CollectTaskLogDal dal = new CollectTaskLogDal();
        return "" + dal.insertXml(objXml);
    }

    /**
     * 更新
     * @param objXml 参数
     * @throws Exception 异常
     */
    @Business
    public void updateCollectTaskLog(String objXml) throws Exception
    {
        CollectTaskLogDal dal = new CollectTaskLogDal();
        dal.updateXml(objXml);
    }

    /**
     * 删除
     * @param taskId 任务ID
     * @throws Exception 异常
     */
    @Business
    public void deleteCollectTaskLog(int taskId) throws Exception
    {
        CollectTaskLogDal dal = new CollectTaskLogDal();
        CollectTaskLogEntity task = dal.get(taskId);
        /*
         * if (BMPServletContextListener.getInstance().isOnline(task.getCollId())) {
         * BMPServletContextListener.getInstance().callRemote(task.getCollId(), "stopCollectTaskLog", new Object[]{task}, new
         * Class[]{CollectTaskLogEntity.class}, true); }
         */
        dal.delete(taskId);
    }
    
    /**
     * 删除某个时间段内的
     * add by bzwang 2013.07.24
     * @param startTime 开始时间 	endTime 结束时间
     * @throws Exception 异常
     */
    @Business
	public void delCollectTaskLogSlot(String startTime, String endTime) throws Exception 
	{
		CollectTaskLogDal ctDal = new CollectTaskLogDal();
		ctDal.delCollectTaskLogSlot(startTime, endTime);
	}
}
