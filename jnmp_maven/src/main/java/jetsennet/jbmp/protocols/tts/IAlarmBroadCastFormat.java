package jetsennet.jbmp.protocols.tts;

import jetsennet.jbmp.entity.AlarmEventEntity;

public interface IAlarmBroadCastFormat
{

	public String format(AlarmEventEntity alarm);

}
