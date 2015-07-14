/**********************************************************************
 * 日 期: 2013-06-04
 * 作 者: xianll
 * 版 本: v1.0
 * 描 述: OperatorLogDal.java
 * 历 史: 2013-06-04 Create
 *********************************************************************/
package jetsennet.jbmp.dataaccess;
import org.apache.log4j.Logger;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.OperatorLogEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 *  操作日志 Dal
 */
public class OperatorLogDal extends DefaultDal<OperatorLogEntity> {
	private static final Logger logger = Logger
			.getLogger(OperatorLogEntity.class);

	/**
	 * 构造方法
	 */
	public OperatorLogDal() {
		super(OperatorLogEntity.class);
	}

	/**
	 * 删除某个时间区间的操作日志
	 */
	@Transactional
	public int delOperatorLogTimeSlot(String startTime, String endTime)
			throws Exception {
		SqlCondition[] conds = {
				startTime == "" ? new SqlCondition("1", "1", SqlLogicType.And,
						SqlRelationType.Equal, SqlParamType.String)
						: new SqlCondition("LOG_TIME", startTime,
								SqlLogicType.And, SqlRelationType.ThanEqual,
								SqlParamType.DateTime),
				endTime == "" ? new SqlCondition("1", "1", SqlLogicType.And,
						SqlRelationType.Equal, SqlParamType.String)
						: new SqlCondition("LOG_TIME", endTime,
								SqlLogicType.And, SqlRelationType.LessEqual,
								SqlParamType.DateTime) };
		return delete(conds);
	}

}
