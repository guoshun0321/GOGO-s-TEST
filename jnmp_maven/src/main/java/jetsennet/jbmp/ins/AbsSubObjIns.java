/************************************************************************
日 期: 2012-1-31
作 者: 郭祥
版 本: v1.3
描 述: 子对象实例化抽象类
历 史:
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.List;
import java.util.Map;

import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.helper.AttrsInsResult;

/**
 * @author 郭祥
 */
public abstract class AbsSubObjIns
{

    protected boolean useBuffer;

    /**
     * 构造函数
     */
    public AbsSubObjIns()
    {
        this.useBuffer = true;
    }

    /**
     * 获取可实例化的子对象
     * @param objId 对象id
     * @param classId 参数
     * @param isLocal 是否本地扫描
     * @return 结果
     * @throws Exception 异常
     */
    public abstract SubObjInsInfo getSubInfo(int objId, int classId, int collId, boolean isLocal) throws Exception;

    /**
     * 实例化选择的子对象
     * @param info 信息
     * @throws Exception 异常
     */
    public abstract void ins(SubObjInsInfo info, int collId) throws Exception;

    /**
     * 获取子对象实例化结果
     * @param mo
     * @param attrs
     * @param collId
     * @param infos
     * @param isLocal
     * @return
     */
    public abstract AttrsInsResult getInsResult(MObjectEntity mo, List<AttributeEntity> attrs, int collId, Map<String, String> infos, boolean isLocal)
            throws Exception;

    public boolean isUseBuffer()
    {
        return useBuffer;
    }

    public void setUseBuffer(boolean useBuffer)
    {
        this.useBuffer = useBuffer;
    }

}
