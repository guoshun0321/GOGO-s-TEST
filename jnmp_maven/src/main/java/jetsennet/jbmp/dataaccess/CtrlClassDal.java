/**
 * 日 期： 2012-12-12
 * 作 者:  邝星
 * 版 本： v1.3
 * 描 述:  CtrlClassDal.java
 * 历 史： 2012-12-12 创建
 */
package jetsennet.jbmp.dataaccess;

import jetsennet.jbmp.entity.CtrlClassEntity;

/**
 * kx 添加
 */
public class CtrlClassDal extends DefaultDal<CtrlClassEntity>
{
    /**
     * 构造函数
     */
    public CtrlClassDal()
    {
        super(CtrlClassEntity.class);
    }
}
