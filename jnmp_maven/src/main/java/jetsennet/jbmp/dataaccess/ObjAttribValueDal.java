/************************************************************************
日 期：2011-12-29
作 者: 
版 本：v1.3
描 述: 操作表BMP_OBJATTRIBVALUE
历 史：
 ************************************************************************/
package jetsennet.jbmp.dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.dataaccess.base.SqlExecutorFacotry;
import jetsennet.jbmp.dataaccess.base.annotation.Transactional;
import jetsennet.jbmp.entity.AttribClassEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;
import jetsennet.jbmp.entity.ObjAttribValueEntity;
import jetsennet.sqlclient.ConnectionInfo;
import jetsennet.sqlclient.DBUtil;
import jetsennet.sqlclient.ISqlExecutor;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlHelper;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;
import jetsennet.sqlclient.SqlValue;

/**
 * 操作表BMP_OBJATTRIBVALUE
 * @author
 */
public class ObjAttribValueDal extends DefaultDal<ObjAttribValueEntity>
{

    public static Logger logger = Logger.getLogger(ObjAttribDal.class);

    /**
     * 构造函数
     */
    public ObjAttribValueDal()
    {
        super(ObjAttribValueEntity.class);
    }

    /**
     * @param objId 对象id
     * @param objAttrIds 对象属性
     * @return 结果
     * @throws Exception 异常
     */
    public List<ObjAttribValueEntity> getLst(int objId, String objAttrIds) throws Exception
    {
        return getLst(new SqlCondition("OBJ_ID", String.valueOf(objId), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric),
            new SqlCondition("OBJATTR_ID", objAttrIds, SqlLogicType.And, SqlRelationType.In, SqlParamType.String));
    }

    /**
     * @param obj_id 对象id
     * @return 结果
     * @throws Exception 异常
     */
    public int deleteByObjId(int obj_id) throws Exception
    {
        SqlCondition cond = new SqlCondition("OBJ_ID", Integer.toString(obj_id), SqlLogicType.And, SqlRelationType.Equal, SqlParamType.Numeric);
        return delete(cond);
    }

    /**
     * 批量插入实例化结果类型为配置信息，且不为空的值
     * @param oas 参数
     */
    @Transactional
    public void insertOrUpdate(List<ObjAttribEntity> oas)
    {
        if (oas == null || oas.isEmpty())
        {
            return;
        }
        Date now = new Date();
        for (ObjAttribEntity oa : oas)
        {
            if (oa.getInsResult() != null && (oa.getAttribType() == AttribClassEntity.CLASS_LEVEL_CONFIG))
            {
                ObjAttribValueEntity value = new ObjAttribValueEntity();
                value.setObjAttrId(oa.getObjAttrId());
                value.setCollTime(now.getTime());
                value.setObjId(oa.getObjId());
                value.setStrValue(oa.getInsResult().toString());
                this.insertOrUpdate(value);
            }
        }
    }

    /**
     * 更新或添加数据
     * @param value 参数
     */
    @Transactional
    public void insertOrUpdate(ObjAttribValueEntity value)
    {
        String insert = "INSERT INTO BMP_OBJATTRIBVALUE (OBJATTR_ID, OBJ_ID, COLL_TIME, STR_VALUE) VALUES(?, ?, ?, ?)";
        String update = "UPDATE BMP_OBJATTRIBVALUE SET COLL_TIME = ?, STR_VALUE = ? WHERE OBJATTR_ID = ?";
        ISqlExecutor exec = SqlExecutorFacotry.getSqlExecutor();
        ConnectionInfo info = exec.getConnectionInfo();
        Connection conn = null;
        PreparedStatement pstmt = null;
        try
        {
            conn = SqlHelper.getConnection(info);
            logger.debug(update);
            pstmt = conn.prepareStatement(update);
            pstmt.setLong(1, value.getCollTime());
            pstmt.setString(2, value.getStrValue());
            pstmt.setInt(3, value.getObjAttrId());
            int ret = pstmt.executeUpdate();

            if (ret <= 0)
            {
                logger.debug(insert);
                pstmt = conn.prepareStatement(insert);
                pstmt.setInt(1, value.getObjAttrId());
                pstmt.setInt(2, value.getObjId());
                pstmt.setLong(3, value.getCollTime());
                pstmt.setString(4, value.getStrValue());
                pstmt.executeUpdate();
            }
        }
        catch (Exception ex)
        {
            logger.error("", ex);
        }
        finally
        {
            DBUtil.closeDbConn(null, pstmt, conn);
        }
    }

    public static void main(String[] args) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 40000; i++)
        {
            sb.append("x");
        }
        ObjAttribValueEntity oa = new ObjAttribValueEntity();
        oa.setObjId(1);
        oa.setObjAttrId(2);
        oa.setCollTime(3);
        oa.setStrValue(sb.toString());

        ObjAttribValueDal oavdal = new ObjAttribValueDal();
        oavdal.insertOrUpdate(oa);
    }
}
