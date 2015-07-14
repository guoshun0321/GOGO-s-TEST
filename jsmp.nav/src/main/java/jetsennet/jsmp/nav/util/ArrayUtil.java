package jetsennet.jsmp.nav.util;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtil
{

	public static final <T> List<T> subList(List<T> lst, int start, int end)
	{
		if (lst == null)
		{
			return null;
		}
		int length = lst.size();
		if (start >= length)
		{
			return new ArrayList<T>(0);
		}
		else
		{
			end = end > length ? length : end;
			return lst.subList(start, end);
		}
	}

}
