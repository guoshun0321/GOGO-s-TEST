/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.log;

import java.util.HashMap;

/**
 * @author Guo
 */
public final class SwingReg
{

    private HashMap<String, Object> areas;

    private SwingReg()
    {
        areas = new HashMap<String, Object>();
    }

    public static SwingReg reg = new SwingReg();

    public static SwingReg getInstance()
    {
        return reg;
    }

    /**
     * @param name 名称
     * @return 结果
     */
    public Object getObject(String name)
    {
        return areas.get(name);
    }

    /**
     * @param name 名称
     * @param comp 参数
     */
    public void reg(String name, Object comp)
    {
        areas.put(name, comp);
    }
}
