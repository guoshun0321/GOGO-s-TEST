package jetsennet.jbmp.dataaccess;

import jetsennet.jbmp.entity.AlarmEventLogEntity;

import org.apache.log4j.Logger;

public class AlarmEventLogDal extends DefaultDal<AlarmEventLogEntity>
{
    private static final Logger logger = Logger.getLogger(AlarmEventLogDal.class);

    public AlarmEventLogDal()
    {
        super(AlarmEventLogEntity.class);
    }
}
