package jetsennet.jsmp.nav.service.a7.entity;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class GetSelectionStartRequest
{

	@IdentAnnocation("clientId")
	private String clientId;

	@IdentAnnocation("deviceId")
	private String deviceId;

	@IdentAnnocation("titleProviderId")
	private String titleProviderId;

	@IdentAnnocation("titleAssetId")
	private String titleAssetId;

	@IdentAnnocation("audioLanguage")
	private String audioLanguage;

	@IdentAnnocation("folderAssetId")
	private String folderAssetId;

	@IdentAnnocation("subtitleLanguage")
	private String subtitleLanguage;

	@IdentAnnocation("format")
	private String format;

	@IdentAnnocation("indefiniteRental")
	private String indefiniteRental;

	@IdentAnnocation("rentalPeriod")
	private int rentalPeriod;

	@IdentAnnocation("price")
	private String price;

	@IdentAnnocation("playPreview")
	private String playPreview;

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

	public String getAudioLanguage()
	{
		return audioLanguage;
	}

	public void setAudioLanguage(String audioLanguage)
	{
		this.audioLanguage = audioLanguage;
	}

	public String getFolderAssetId()
	{
		return folderAssetId;
	}

	public void setFolderAssetId(String folderAssetId)
	{
		this.folderAssetId = folderAssetId;
	}

	public String getSubtitleLanguage()
	{
		return subtitleLanguage;
	}

	public void setSubtitleLanguage(String subtitleLanguage)
	{
		this.subtitleLanguage = subtitleLanguage;
	}

	public String getFormat()
	{
		return format;
	}

	public void setFormat(String format)
	{
		this.format = format;
	}

	public String getIndefiniteRental()
	{
		return indefiniteRental;
	}

	public void setIndefiniteRental(String indefiniteRental)
	{
		this.indefiniteRental = indefiniteRental;
	}

	public int getRentalPeriod()
	{
		return rentalPeriod;
	}

	public void setRentalPeriod(int rentalPeriod)
	{
		this.rentalPeriod = rentalPeriod;
	}

	public String getPrice()
	{
		return price;
	}

	public void setPrice(String price)
	{
		this.price = price;
	}

	public String getPlayPreview()
	{
		return playPreview;
	}

	public void setPlayPreview(String playPreview)
	{
		this.playPreview = playPreview;
	}

}
