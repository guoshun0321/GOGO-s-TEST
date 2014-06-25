package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetAssociatededProgramsRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("account")
	private String account;

	@IdentAnnocation(value = "languageCode", def = A7Constants.DEF_LANG)
	private String languageCode;

	@IdentAnnocation("regionCode")
	private String regionCode;

	@IdentAnnocation("channelIds")
	private String channelIds;

	@IdentAnnocation("programId")
	private String programId;

	@IdentAnnocation("programName")
	private String programName;

	@IdentAnnocation("startAt")
	private String startAt;

	@IdentAnnocation("days")
	private int days;

	@IdentAnnocation("profile")
	private String profile;

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getAccount()
	{
		return account;
	}

	public void setAccount(String account)
	{
		this.account = account;
	}

	public String getLanguageCode()
	{
		return languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	public String getRegionCode()
	{
		return regionCode;
	}

	public void setRegionCode(String regionCode)
	{
		this.regionCode = regionCode;
	}

	public String getChannelIds()
	{
		return channelIds;
	}

	public void setChannelIds(String channelIds)
	{
		this.channelIds = channelIds;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	public String getStartAt()
	{
		return startAt;
	}

	public void setStartAt(String startAt)
	{
		this.startAt = startAt;
	}

	public int getDays()
	{
		return days;
	}

	public void setDays(int days)
	{
		this.days = days;
	}

	public String getProfile()
	{
		return profile;
	}

	public void setProfile(String profile)
	{
		this.profile = profile;
	}

}
