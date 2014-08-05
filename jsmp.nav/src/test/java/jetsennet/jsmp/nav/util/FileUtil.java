package jetsennet.jsmp.nav.util;

import java.io.FileInputStream;
import java.io.InputStream;

public class FileUtil
{

	public static final String COP_COLUMN = readFile("/cop_column.xml");
	public static final String COP_COLUMN_DOWN = readFile("/cop_column_down.xml");
	public static final String COP_CHANNEL = readFile("/cop_channel.xml");
	public static final String COP_CHANNEL_DOWN = readFile("/cop_channel_down.xml");
	public static final String COP_PROGRAM = readFile("/cop_program.xml");
	public static final String COP_PROGRAM_DOWN = readFile("/cop_program_down.xml");
	public static final String COP_PLAYBILL = readFile("/cop_playbill.xml");
	public static final String COP_PLAYBILL_DOWN = readFile("/cop_playbill_down.xml");

	public static String readFile(String name)
	{
		StringBuilder sb = new StringBuilder();
		InputStream in = null;
		try
		{
			String f = FileUtil.class.getResource(name).getFile();
			in = new FileInputStream(f);
			int i = -1;
			byte[] b = new byte[1024];
			while ((i = in.read(b)) > 0)
			{
				sb.append(new String(b, 0, i));
			}
		}
		catch (Exception ex)
		{
			throw new UncheckedNavException(ex);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}

}
