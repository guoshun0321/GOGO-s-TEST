/************************************************************************
日 期: 2012-1-16
作 者: 郭祥
版 本: v1.3
描 述: 子对象实例化结果
历 史:
 ************************************************************************/
package jetsennet.jbmp.ins;

import java.util.LinkedHashMap;
import java.util.Map;

import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.ins.helper.AttrsInsResult;

/**
 * 子对象实例化结果
 * @author 郭祥
 */
public class SubObjsInsRst
{

    private Map<MObjectEntity, AttrsInsResult> subs;

    /**
     *构造函数
     */
    public SubObjsInsRst()
    {
        subs = new LinkedHashMap<MObjectEntity, AttrsInsResult>();
    }

    /**
     * @param obj 对象
     * @param ir 参数
     */
    public void add(MObjectEntity obj, AttrsInsResult ir)
    {
        subs.put(obj, ir);
    }

    /**
     * @return the subs
     */
    public Map<MObjectEntity, AttrsInsResult> getSubs()
    {
        return subs;
    }

    /**
     * @param subs the subs to set
     */
    public void setSubs(Map<MObjectEntity, AttrsInsResult> subs)
    {
        this.subs = subs;
    }
}
