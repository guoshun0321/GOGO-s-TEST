package jetsennet.jsmp.nav.media.jms;

import java.util.List;

public class JmsMsgHandle
{

	public static void syn(DataSynEntity data)
	{
		List<DataSynContentEntity> contents = data.getContents();
		for (DataSynContentEntity content : contents)
		{
			AbsJmsMsgHandle.handle(content);
		}
	}

}
