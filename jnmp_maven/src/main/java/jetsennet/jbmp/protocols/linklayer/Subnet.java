package jetsennet.jbmp.protocols.linklayer;

public class Subnet
{

	public final String ip;

	public final String mask;

	public Subnet(String ip, String mask)
	{
		this.ip = ip;
		this.mask = mask;
	}
}
