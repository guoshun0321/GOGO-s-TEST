package jetsennet.jbmp.protocols.linklayer.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由器关系
 * 
 * @author 郭祥
 */
public class RouterRelation
{

	private List<RouterRelationEntity> rels;

	public RouterRelation()
	{
		rels = new ArrayList<RouterRelationEntity>();
	}

	/**
	 * 添加路由关系
	 * @param interf
	 * @param ip1
	 */
	public void addRel(SnmpNetInterface interf, String ip1)
	{
		String ip2 = interf.getIp().getIp();
		boolean isMatch = false;
		for (RouterRelationEntity rel : rels)
		{
			if (rel.firstIp.equals(ip1) && rel.secIp.equals(ip2))
			{
				rel.setSeconde(interf);
				rel.setSecInterf(interf.getInterf());
			}
		}
		if (!isMatch)
		{
			RouterRelationEntity entity = new RouterRelationEntity();
			entity.setFirst(interf);
			entity.setFirstInterf(interf.getInterf());
			entity.setFirstIp(interf.getIp().getIp());
			entity.setSecIp(ip2);
			rels.add(entity);
		}
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String head = String.format("%1$-20s %2$-20s %3$-20s %4$-20s\n", "ip1", "port1", "ip2", "port2");
		sb.append(head);
		for (RouterRelationEntity rel : rels)
		{
			sb.append(rel.toString());
		}
		return sb.toString();
	}

	public static class RouterRelationEntity
	{
		/**
		 * 第一个接口
		 */
		private SnmpNetInterface first;
		/**
		 * 第一个接口的IP
		 */
		private String firstIp;
		/**
		 * 第一个接口的编号
		 */
		private int firstInterf;
		/**
		 * 第二个接口
		 */
		private SnmpNetInterface seconde;
		/**
		 * 第二个接口的IP
		 */
		private String secIp;
		/**
		 * 第二个接口的编号
		 */
		private int secInterf;

		public String toString()
		{
			return String.format("%1$-20s %2$-20s %3$-20s %4$-20s\n", this.firstIp, this.firstInterf, this.secIp, this.secInterf);
		}

		public SnmpNetInterface getFirst()
		{
			return first;
		}

		public void setFirst(SnmpNetInterface first)
		{
			this.first = first;
		}

		public SnmpNetInterface getSeconde()
		{
			return seconde;
		}

		public void setSeconde(SnmpNetInterface seconde)
		{
			this.seconde = seconde;
		}

		public String getFirstIp()
		{
			return firstIp;
		}

		public void setFirstIp(String firstIp)
		{
			this.firstIp = firstIp;
		}

		public int getFirstInterf()
		{
			return firstInterf;
		}

		public void setFirstInterf(int firstInterf)
		{
			this.firstInterf = firstInterf;
		}

		public String getSecIp()
		{
			return secIp;
		}

		public void setSecIp(String secIp)
		{
			this.secIp = secIp;
		}

		public int getSecInterf()
		{
			return secInterf;
		}

		public void setSecInterf(int secInterf)
		{
			this.secInterf = secInterf;
		}

	}

}
