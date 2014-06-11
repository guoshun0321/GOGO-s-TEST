package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetItemDataRequest
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

    @IdentAnnocation("titleProviderId")
    private String titleProviderId;

    @IdentAnnocation("titleAssetId")
    private String titleAssetId;

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

    public String getTitleProviderId()
    {
        return titleProviderId;
    }

    public void setTitleProviderId(String titleProviderId)
    {
        this.titleProviderId = titleProviderId;
    }

    public String getTitleAssetId()
    {
        return titleAssetId;
    }

    public void setTitleAssetId(String titleAssetId)
    {
        this.titleAssetId = titleAssetId;
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
