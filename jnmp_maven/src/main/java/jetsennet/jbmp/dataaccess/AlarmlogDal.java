/**********************************************************************
 * 日 期: 2012-08-23
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: AlarmlogDal.java
 * 历 史: 2012-08-23 Create
 *********************************************************************/
package jetsennet.jbmp.dataaccess;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AlarmlogEntity;

/**
 * 报警处理日志Dal
 */
public class AlarmlogDal extends DefaultDal<AlarmlogEntity>
{
    private static final Logger logger = Logger.getLogger(AlarmlogDal.class);

    /**
     * 构造方法
     */
    public AlarmlogDal()
    {
        super(AlarmlogEntity.class);
    }
}
