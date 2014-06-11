package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetRootContentsRequest
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

    @IdentAnnocation(value = "startAt", def = "0")
    private int startAt;

    @IdentAnnocation(value = "maxItems", def = "10")
    private int maxItems;

    @IdentAnnocation("serviceType")
    private String serviceType;

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

    public String getServiceType()
    {
        return serviceType;
    }

    public void setServiceType(String serviceType)
    {
        this.serviceType = serviceType;
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
