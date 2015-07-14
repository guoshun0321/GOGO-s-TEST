/**
 * 日 期：2012-6-6
 * 作 者: 何岳军
 * 版 本：v
 * 描 述: 采集日志数据层方法
 * 历 史：
 */
package jetsennet.jbmp.dataaccess;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.CollectTaskLogEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 * @author ？
 */
public class CollectTaskLogDal extends DefaultDal<CollectTaskLogEntity>
{

    private static final Logger logger = Logger.getLogger(CollectTaskLogDal.class);

    /**
     * 构造方法
     */
    public CollectTaskLogDal()
    {
        super(CollectTaskLogEntity.class);
    }
    
    /**
     * 删除某个时间段内的
     * add by bzwang 2013.07.24
     * @param startTime 开始时间 	endTime 结束时间
     * @throws Exception 异常
     */
    @Transactional
	public int delCollectTaskLogSlot(String startTime, String endTime) throws Exception {
    	SqlCondition[] conds = {
				startTime == "" ? new SqlCondition("1", "1", SqlLogicType.And,
						SqlRelationType.Equal, SqlParamType.String)
						: new SqlCondition("START_TIME", startTime,
								SqlLogicType.And, SqlRelationType.ThanEqual,
								SqlParamType.DateTime),
				endTime == "" ? new SqlCondition("1", "1", SqlLogicType.And,
						SqlRelationType.Equal, SqlParamType.String)
						: new SqlCondition("START_TIME", endTime,
								SqlLogicType.And, SqlRelationType.LessEqual,
								SqlParamType.DateTime) };
		return delete(conds);
	} 
}
