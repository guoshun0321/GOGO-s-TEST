package jetsennet.jbmp.alarm.handle;

import java.util.ArrayList;
import java.util.List;

import jetsennet.jbmp.dataaccess.buffer.DynamicMObject;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.trap.util.TrapConfigs;
import jetsennet.jbmp.trap.util.TrapMatcher;
import jetsennet.jbmp.util.TwoTuple;

public class TrapFilter
{

    /**
     * 根据IP和Trap内容选择对象和对象属性
     * @param ip
     * @param trap
     * @return
     */
    public static TwoTuple<MObjectEntity, ObjAttribEntity> ensureObj(String ip, TrapParseEntity trap)
    {
        // 取出匹配规则
        String trapOid = trap.getTrapOid();
        List<TrapMatcher> matchers = TrapConfigs.getInstance().getMatchers(trapOid);

        // 取出IP符合的对象
        List<MObjectEntity> mos = DynamicMObject.getInstance().get(ip);
        if (mos == null || mos.isEmpty())
        {
            return new TwoTuple<MObjectEntity, ObjAttribEntity>(null, null);
        }

        MObjectEntity moRst = null;
        ObjAttribEntity oaRst = null;
        boolean isFind = false;
        for (MObjectEntity mo : mos)
        {
            ArrayList<ObjAttribEntity> oas = mo.getAttrs();
            if (oas != null)
            {
                for (ObjAttribEntity oa : oas)
                {
                    // 比较TrapOid
                    if (oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_TRAP && trapOid.equals(oa.getAttribValue()))
                    {
                        // 存在匹配规则时
                        if (!matchers.isEmpty())
                        {
                            for (TrapMatcher matcher : matchers)
                            {
                                if (matcher.match(mo, trap))
                                {
                                    moRst = mo;
                                    oaRst = oa;
                                    isFind = true;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            // 不存在匹配规则时，取第一个满足条件的对象和对象属性
                            moRst = mo;
                            oaRst = oa;
                            isFind = true;
                            break;
                        }
                    }
                    // 找到匹配规则时，返回
                    if (isFind)
                    {
                        break;
                    }
                }
            }
            if (isFind)
            {
                break;
            }
        }
        // 匹配对象但不存在匹配属性时，返回第一个匹配对象
        if (moRst == null && oaRst == null && mos.size() > 0)
        {
            moRst = mos.get(0);
        }
        return new TwoTuple<MObjectEntity, ObjAttribEntity>(moRst, oaRst);
    }

}
