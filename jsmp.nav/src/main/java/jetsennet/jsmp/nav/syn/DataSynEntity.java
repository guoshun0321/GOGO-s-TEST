package jetsennet.jsmp.nav.syn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class DataSynEntity
{

	@IdentAnnocation("messageID")
	private String msgId;

	@IdentAnnocation("senderID")
	private String senderId;

	@IdentAnnocation("recvID")
	private String recId;

	@IdentAnnocation("opCode")
	private DataSynOpCodeEnum opCode;

	@IdentAnnocation("time")
	private long time;

	@IdentAnnocation("responseUrl")
	private String responseUrl;

	@IdentAnnocation("msgType")
	private DataSynMsgTypeEnum msgType;

	private List<DataSynContentEntity> contents;

	public DataSynEntity()
	{
		this.contents = new ArrayList<DataSynContentEntity>();
	}

	public static Map<String, Field> getEntityInfo()
	{
		Map<String, Field> retval = new HashMap<String, Field>();
		Field[] fields = DataSynEntity.class.getFields();
		for (Field field : fields)
		{
			IdentAnnocation anno = field.getAnnotation(IdentAnnocation.class);
			if (anno != null)
			{
				field.setAccessible(true);
				retval.put(anno.value(), field);
			}
		}
		return retval;
	}

	/**
	 * 仅用于测试
	 * 
	 * @param sb
	 * @return
	 */
	public StringBuilder toXml(StringBuilder sb)
	{
		if (sb == null)
		{
			sb = new StringBuilder();
		}
		sb.append("<copPortalMsg>");
		sb.append("<header>");
		sb.append("<messageID>").append(this.getMsgId()).append("</messageID>");
		sb.append("<senderID>").append(this.getSenderId()).append("</senderID>");
		sb.append("<recvID>").append(this.getRecId()).append("</recvID>");
		sb.append("<opCode>").append(this.getOpCode().name()).append("</opCode>");
		sb.append("<time>").append(this.getTime()).append("</time>");
		sb.append("<responseUrl>").append(this.getResponseUrl()).append("</responseUrl>");
		sb.append("<msgType>").append(this.getMsgType().name()).append("</msgType>");
		sb.append("</header>");
		sb.append("<body>");
		for (DataSynContentEntity content : contents)
		{
			content.toXml(sb);
		}
		sb.append("</body>");
		sb.append("</copPortalMsg>");
		return sb;
	}

	public void addContent(DataSynContentEntity content)
	{
		this.contents.add(content);
	}

	public String getMsgId()
	{
		return msgId;
	}

	public void setMsgId(String msgId)
	{
		this.msgId = msgId;
	}

	public String getSenderId()
	{
		return senderId;
	}

	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}

	public String getRecId()
	{
		return recId;
	}

	public void setRecId(String recId)
	{
		this.recId = recId;
	}

	public DataSynOpCodeEnum getOpCode()
	{
		return opCode;
	}

	public void setOpCode(DataSynOpCodeEnum opCode)
	{
		this.opCode = opCode;
	}

	public long getTime()
	{
		return time;
	}

	public void setTime(long time)
	{
		this.time = time;
	}

	public String getResponseUrl()
	{
		return responseUrl;
	}

	public void setResponseUrl(String responseUrl)
	{
		this.responseUrl = responseUrl;
	}

	public DataSynMsgTypeEnum getMsgType()
	{
		return msgType;
	}

	public void setMsgType(DataSynMsgTypeEnum msgType)
	{
		this.msgType = msgType;
	}

	public List<DataSynContentEntity> getContents()
	{
		return contents;
	}

	public void setContents(List<DataSynContentEntity> contents)
	{
		this.contents = contents;
	}

}
