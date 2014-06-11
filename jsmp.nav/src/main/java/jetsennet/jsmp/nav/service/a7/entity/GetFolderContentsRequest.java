package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

/**
 * 获取栏目信息
 * 
 * @author 郭祥
 */
public class GetFolderContentsRequest
{
    @IdentAnnocation("clientId")
    private String clientId;

    @IdentAnnocation("zip")
    private String regionCode;

    @IdentAnnocation("deviceId")
    private String deviceId;

    @IdentAnnocation("account")
    private String account;

    @IdentAnnocation(value = "languageCode", def = A7Constants.DEF_LANG)
    private String languageCode;

    @IdentAnnocation(value = "providerId", def = A7Constants.PROVIDER_ID)
    private String providerId;

    @IdentAnnocation("assetId")
    private String assetId;

    @IdentAnnocation(value = "includeFolderProperties", def = A7Constants.BOOLEAN_NO)
    private boolean includeFolderProperties;

    @IdentAnnocation(value = "includeSubFolder", def = A7Constants.BOOLEAN_YES)
    private boolean includeSubFolder;

    @IdentAnnocation(value = "includeSelectableItem", def = A7Constants.BOOLEAN_YES)
    private boolean includeSelectableItem;

    @IdentAnnocation(value = "depth", def = "1")
    private int depth;

    @IdentAnnocation("startAt")
    private String startAt;

    @IdentAnnocation(value = "maxItems", def = A7Constants.DEF_MAX)
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

    public String getRegionCode()
    {
        return regionCode;
    }

    public void setRegionCode(String regionCode)
    {
        this.regionCode = regionCode;
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

    public String getProviderId()
    {
        return providerId;
    }

    public void setProviderId(String providerId)
    {
        this.providerId = providerId;
    }

    public String getAssetId()
    {
        return assetId;
    }

    public void setAssetId(String assetId)
    {
        this.assetId = assetId;
    }

    public boolean getIncludeFolderProperties()
    {
        return includeFolderProperties;
    }

    public void setIncludeFolderProperties(boolean includeFolderProperties)
    {
        this.includeFolderProperties = includeFolderProperties;
    }

    public boolean getIncludeSubFolder()
    {
        return includeSubFolder;
    }

    public void setIncludeSubFolder(boolean includeSubFolder)
    {
        this.includeSubFolder = includeSubFolder;
    }

    public boolean getIncludeSelectableItem()
    {
        return includeSelectableItem;
    }

    public void setIncludeSelectableItem(boolean includeSelectableItem)
    {
        this.includeSelectableItem = includeSelectableItem;
    }

    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public String getStartAt()
    {
        return startAt;
    }

    public void setStartAt(String startAt)
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
