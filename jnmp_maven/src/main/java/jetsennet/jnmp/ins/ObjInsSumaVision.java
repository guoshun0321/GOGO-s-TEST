/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jnmp.ins;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.AttributeDal;
import jetsennet.jbmp.dataaccess.ObjAttribDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.AttributeEntity;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.exception.InstanceException;
import jetsennet.jbmp.ins.InsConfig;
import jetsennet.jbmp.ins.InsResult;

/**
 * 数码视讯设备实例化
 * @author Gum
 */
public class ObjInsSumaVision
{

    /**
     * 结果
     */
    private InsResult result;
    private AttributeDal adal;
    private ObjAttribDal oadal;
    private static final Logger logger = Logger.getLogger(ObjInsSumaVision.class);

    /**
     * 构造方法
     */
    public ObjInsSumaVision()
    {
        adal = ClassWrapper.wrapTrans(AttributeDal.class);
        oadal = ClassWrapper.wrapTrans(ObjAttribDal.class);
        result = new InsResult();
    }

    /**
     * 实例化
     * @param mo 对象
     * @param attrs 属性
     * @param info 信息
     * @throws InstanceException 异常
     */
    public void ins(MObjectEntity mo, ArrayList<AttributeEntity> attrs, InsConfig info) throws InstanceException
    {
        try
        {
            int classId = mo.getClassId();
            int objId = mo.getObjId();
            ArrayList<ObjAttribEntity> oas = new ArrayList<ObjAttribEntity>();
            ArrayList<AttributeEntity> attrs1 = adal.getByType(classId);
            for (AttributeEntity attr : attrs1)
            {
                ObjAttribEntity oa = attr.genObjAttrib();
                oa.setObjId(objId);
                oa.setAttribValue(attr.getAttribValue());
                oa.setObjattrName(mo.getObjName() + " - " + attr.getAttribName());
                oas.add(oa);
            }
            result.setResults(oas);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * 实例化
     * @param mo 对象
     */
    public void ins(MObjectEntity mo)
    {
        try
        {
            int classId = mo.getClassId();
            int objId = mo.getObjId();
            ArrayList<ObjAttribEntity> oas = new ArrayList<ObjAttribEntity>();
            ArrayList<AttributeEntity> attrs = adal.getByType(classId);
            for (AttributeEntity attr : attrs)
            {
                ObjAttribEntity oa = attr.genObjAttrib();
                oa.setObjId(objId);
                oa.setAttribValue(attr.getAttribValue());
                oa.setObjattrName(mo.getObjName() + " - " + attr.getAttribName());
                oas.add(oa);
            }
            oadal.insert(oas);
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
    }

    /**
     * @return 结果
     * @throws InstanceException 异常
     */
    public InsResult getInsResule() throws InstanceException
    {
        return this.result;
    }
}
