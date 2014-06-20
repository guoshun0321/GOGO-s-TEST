package jetsennet.jsmp.nav.service.a7.entity;

import java.util.Date;

import jetsennet.jsmp.nav.util.IdentAnnocation;

public class RequestTestEntity
{

	@IdentAnnocation("testId")
	private int id;

	@IdentAnnocation("testStr")
	private String str1;

	@IdentAnnocation("testBool")
	private boolean boolean1;

	@IdentAnnocation("testDate")
	private Date date1;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getStr1()
	{
		return str1;
	}

	public void setStr1(String str1)
	{
		this.str1 = str1;
	}

	public boolean isBoolean1()
	{
		return boolean1;
	}

	public void setBoolean1(boolean boolean1)
	{
		this.boolean1 = boolean1;
	}

	public Date getDate1()
	{
		return date1;
	}

	public void setDate1(Date date1)
	{
		this.date1 = date1;
	}

}
