package jetsennet.jsmp.nav.util;

import java.util.concurrent.CountDownLatch;

public class ThreadWaitFutrue
{

	private Object obj;

	private Throwable cause;

	private CountDownLatch latch = new CountDownLatch(1);

	public Object get(boolean isThrow)
	{
		Object retval = null;
		try
		{
			latch.await();
			if (this.cause == null)
			{
				retval = obj;
			}
			else
			{
				if (isThrow)
				{
					throw new UncheckedNavException(cause);
				}
				else
				{
					retval = cause;
				}
			}
		}
		catch (Exception ex)
		{
			if (isThrow)
			{
				throw new UncheckedNavException(ex);
			}
			else
			{
				retval = ex;
			}
		}
		return retval;
	}

	public void put(Object obj)
	{
		this.obj = obj;
		latch.countDown();
	}

	public void put()
	{
		put(null);
	}

	public void exception(Throwable t)
	{
		this.cause = t;
		latch.countDown();
	}

}
