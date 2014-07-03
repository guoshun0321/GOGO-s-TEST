/************************************************************************
日 期：2011-11-28
作 者: 郭祥
版 本：v1.3
描 述: 属性实例化抽象类
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.Map;

import org.apache.log4j.Logger;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.exception.InstanceException;

/**
 * 属性实例化抽象类。传入对象以及属性，传出实例化结果。
 * @author 郭祥
 */
public abstract class AbsAttrsIns
{

    protected boolean isAuto;
    /**
     * 实例化结果
     */
    protected AttrsInsResult result;
    /**
     * 是否使用缓存
     */
    protected boolean useBuffer;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(AbsAttrsIns.class);

    /**
     * 构造函数
     */
    public AbsAttrsIns()
    {
        useBuffer = true;
        result = new AttrsInsResult();
    }

    /**
     * 实例化
     * @param mo 对象
     * @param collId 采集器ID
     * @param isLocal 是否本地实例化
     * @param infos 实例化附加信息
     * @return 结果
     * @throws InstanceException 异常
     */
    public abstract AttrsInsResult ins(MObjectEntity mo, int collId, Map<String, String> infos, boolean isLocal) throws InstanceException;

    /**
     * 添加属性。传入的属性作为实例化的输入。
     * @param attr 参数
     */
    public void addAttr(AttributeEntity attr)
    {
        result.addInput(attr);
    }

    /**
     * 获取实例化结果
     * @return 实例化结果
     */
    public AttrsInsResult getResult()
    {
        return result;
    }

    public boolean isUseBuffer()
    {
        return useBuffer;
    }

    public void setUseBuffer(boolean useBuffer)
    {
        this.useBuffer = useBuffer;
    }

    public boolean isAuto()
    {
        return isAuto;
    }

    public void setAuto(boolean isAuto)
    {
        this.isAuto = isAuto;
    }
}
