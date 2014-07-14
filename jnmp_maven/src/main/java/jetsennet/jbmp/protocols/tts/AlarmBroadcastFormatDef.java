package jetsennet.jbmp.protocols.tts;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AlarmEventEntity;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.util.StringUtil;

public class AlarmBroadcastFormatDef implements IAlarmBroadCastFormat
{

	private MObjectDal modal;
	private AttributeDal adal;
	private static final Logger logger = Logger.getLogger(AlarmBroadcastFormatDef.class);

	public AlarmBroadcastFormatDef()
	{
		modal = ClassWrapper.wrapTrans(MObjectDal.class);
		adal = ClassWrapper.wrapTrans(AttributeDal.class);
	}

	@Override
	public String format(AlarmEventEntity alarm)
	{
		String retval = null;
		if (alarm != null)
		{
			if (alarm.getAlarmType() == 1010)
			{
				retval = this.formatTS(alarm);
			}
			else if (alarm.getAlarmType() == 1020)
			{
				retval = alarm.getEventDesc();
			}
			else
			{
				retval = this.formatDev(alarm);
			}
		}
		if (!StringUtil.isNullOrEmpty(retval.trim()))
		{
			if (alarm.getResumeTime() > 0)
			{
				retval += "恢复";
			}
		}
		else
		{
			retval = null;
		}
		return retval;
	}

	private String formatTS(AlarmEventEntity alarm)
	{
		String retval = null;
		try
		{
			MObjectEntity mo = modal.get(alarm.getObjId());
			AttributeEntity attr = adal.get(alarm.getAttribId());
			retval = mo.getObjName() + attr.getAttribName();
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return retval;
	}

	private String formatDev(AlarmEventEntity alarm)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			MObjectEntity mo = modal.get(alarm.getObjId());
			AttributeEntity attr = adal.get(alarm.getAttribId());
			sb.append("设备：");
			sb.append(mo.getObjName());
			if (attr != null)
			{
				sb.append("。属性：");
				sb.append(attr.getAttribName());
			}
			sb.append("。");
			sb.append(alarm.getLevelName());
		}
		catch (Exception ex)
		{
			logger.error("", ex);
		}
		return sb.toString();
	}

}
