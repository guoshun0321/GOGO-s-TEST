/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.mib;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.SnmpNodesDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.SnmpNodesEntity;

/**
 * @author Guo
 */
public class MibFactory
{
    /**
     * mib字典
     */
    private HashMap<String, MibDictionary> dataList;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(MibFactory.class);
    // <editor-fold defaultstate="collapsed" desc="单例">
    private static MibFactory factory = new MibFactory();

    private MibFactory()
    {
        try
        {
            init();
        }
        catch (Exception e)
        {
            logger.error("", e);
        }
    }

    public static MibFactory getInstance()
    {
        return factory;
    }

    /**
     * @throws Exception 异常
     */
    public void init() throws Exception
    {
        if (dataList != null)
        {
            dataList.clear();
            dataList = null;
        }
        dataList = new HashMap<String, MibDictionary>();
        SnmpNodesDal modao = ClassWrapper.wrapTrans(SnmpNodesDal.class);
        List<SnmpNodesEntity> allEntity = modao.getAll();
        for (SnmpNodesEntity entity : allEntity)
        {
            String devType = Integer.toString(entity.getMibId());
            MibDictionary dic = dataList.get(devType);
            if (dic == null)
            {
                dic = new MibDictionary();
                dataList.put(devType, dic);
            }
            dic.addNode(entity);
        }
    }

    /**
     * @param name 参数
     * @return 结果
     */
    public MibDictionary getDictionary(String name)
    {
        return dataList.get(name);
    }
}
