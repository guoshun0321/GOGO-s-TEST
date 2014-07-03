/************************************************************************
日 期：2011-11-29
作 者: 郭祥
版 本：v1.3
描 述: 抽象类，处理实例化结果。
历 史：
 ************************************************************************/
package jetsennet.jbmp.ins.helper;

import java.util.ArrayList;

import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;

/**
 * 处理实例化结果，抽象类。
 * @author 郭祥
 */
public abstract class AbsInsRstHandle
{

    protected ArrayList<ObjAttribEntity> oas;

    /**
     * 构造函数
     */
    public AbsInsRstHandle()
    {
        oas = new ArrayList<ObjAttribEntity>();
    }

    /**
     * 添加对象属性
     * @param oa 参数
     */
    public void addObjAttr(ObjAttribEntity oa)
    {
        this.oas.add(oa);
    }

    /**
     * 处理
     * @throws InstanceException 异常
     */
    public abstract void handle() throws InstanceException;

}
