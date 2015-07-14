package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetAssociatedFolderContentsRequest
{

    @IdentAnnocation("clientId")
    private String clientId;
 
    @IdentAnnocation("deviceId")
    private String deviceId;

    @IdentAnnocation("account")
    private String account;

    @IdentAnnocation(value = "languageCode", def = A7Constants.DEF_LANG)
    private String languageCode;

    @IdentAnnocation("quickId")
    private String quickId;

    @IdentAnnocation("startAt")
    private String startAt;

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

    public String getQuickId()
    {
        return quickId;
    }

    public void setQuickId(String quickId)
    {
        this.quickId = quickId;
    }

    public String getStartAt()
    {
        return startAt;
    }

    public void setStartAt(String startAt)
    {
        this.startAt = startAt;
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
