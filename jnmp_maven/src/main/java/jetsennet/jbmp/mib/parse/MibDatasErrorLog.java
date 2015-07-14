/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib.parse;

/**
 * 记录MibDatas生成过程中的错误
 * @author Guo
 */
public class MibDatasErrorLog
{

    private boolean error;
    private StringBuilder sb;

    /**
     * 构造函数
     */
    public MibDatasErrorLog()
    {
        error = false;
        sb = new StringBuilder();
    }

    /**
     * @param s 参数
     */
    public void addError(String s)
    {
        error = true;
        if (sb == null)
        {
            sb = new StringBuilder();
            sb.append(s);
        }
        else
        {
            sb.append("\n");
            sb.append(s);
        }
    }

    /**
     * @return the error
     */
    public boolean isError()
    {
        return error;
    }
}
