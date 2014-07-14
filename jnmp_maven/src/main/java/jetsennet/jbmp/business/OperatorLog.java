package jetsennet.jbmp.business;

import jetsennet.jbmp.dataaccess.OperatorLogDal;
import jetsennet.jbmp.dataaccess.base.annotation.Business;

/**
 * @author xianll
 * 2013-6-4 操作日志
 */
public class OperatorLog
{

    /**
     * 构造函数
     */
    public OperatorLog()
    {
    }

    
    /**
     * 删除某个时间区间内的
     * @param startTime开始时间 endTime结束时间
     * @throws Exception
     */
    @Business
    public void delOperatorLogTimeSlot(String startTime,String endTime) throws Exception
    {
    	OperatorLogDal opDal = new OperatorLogDal();
    	opDal.delOperatorLogTimeSlot(startTime,endTime);
    }

}
