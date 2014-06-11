package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

/**
 * 获取节目单信息
 * 
 * @author 郭祥
 */
public class GetProgramRequest
{
    @IdentAnnocation("clientId")
    private String clientId;

    @IdentAnnocation("deviceId")
    private String deviceId;

    @IdentAnnocation("account")
    private String account;

    @IdentAnnocation(value = "languageCode", def = "zh-CN")
    private String languageCode;

    @IdentAnnocation("regionCode")
    private String regionCode;

    @IdentAnnocation("channelIds")
    private String channelIds;

    @IdentAnnocation("days")
    private int days;

    @IdentAnnocation(value = "startAt", def = "0")
    private int startAt;

    @IdentAnnocation(value = "maxItems", def = "10")
    private int maxItems;

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

    public int getDays()
    {
        return days;
    }

    public void setDays(int days)
    {
        this.days = days;
    }

    public int getStartAt()
    {
        return startAt;
    }

    public void setStartAt(int startAt)
    {
        this.startAt = startAt;
    }

    public int getMaxItems()
    {
        return maxItems;
    }

    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
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
