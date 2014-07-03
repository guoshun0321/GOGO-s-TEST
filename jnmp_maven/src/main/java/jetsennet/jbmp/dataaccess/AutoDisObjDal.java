/************************************************************************
日 期：2011-12-27
作 者: 郭祥
版 本：v1.3
描 述: 自动发现
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AutoDisObjEntity;

/**
 * @author 郭祥
 */
public class AutoDisObjDal extends DefaultDal<AutoDisObjEntity>
{

    private static final Logger logger = Logger.getLogger(AutoDisObjDal.class);

    /**
     * 构造方法
     */
    public AutoDisObjDal()
    {
        super(AutoDisObjEntity.class);
    }

    /**
     * 添加或更新
     * @param autos 参数
     * @throws Exception 异常
     */
    @Transactional
    public void insertOrUpdate(List<AutoDisObjEntity> autos) throws Exception
    {
        for (AutoDisObjEntity auto : autos)
        {
            if (auto.getObjId() > 0)
            {
                this.update(auto);
            }
            else
            {
                this.insert(auto);
            }
        }
    }

    /**
     * @param key 键
     * @param collId 参数
     * @return 结果
     * @throws Exception 异常
     */
    public List<AutoDisObjEntity> getThirdPartObj(String key, int collId) throws Exception
    {
        String sql = "SELECT * FROM BMP_AUTODISOBJ WHERE FIELD_1='%s' AND COLL_ID=%s";
        sql = String.format(sql, key, collId);
        return this.getLst(sql);
    }

    /**
     * @param taskId 任务
     * @return 结果
     * @throws Exception 异常
     */
    public List<AutoDisObjEntity> getByTaskId(int taskId) throws Exception
    {
        String sql = "SELECT * FROM BMP_AUTODISOBJ WHERE TASK_ID = " + taskId + " ORDER BY IP_NUM ASC";
        return this.getLst(sql);
    }
}
