/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.datacollect.collectorif.transmsg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.MObjectDal;
import jetsennet.jbmp.dataaccess.base.ClassWrapper;
import jetsennet.jbmp.entity.MObjectEntity;
import jetsennet.jbmp.entity.ObjAttribEntity;

/**
 * 服务器端与客服端沟通的数据结构
 * @author 郭祥
 */
public class TransMsg implements Serializable
{

    /**
     * OBJ_ID
     */
    private int objId;
    /**
     * 对象
     */
    private MObjectEntity mo;
    /**
     * 信息编号
     */
    private int msgId;
    /**
     * 采集的类型。实例化、采集等等。
     */
    private int collType;
    /**
     * 采集执行的结果
     */
    private int collState;
    /**
     * 信息。采集失败时，为失败信息。更新时，为更新信息。
     */
    private String msg;
    /**
     * 采集花费的时间
     */
    private long duration;
    /**
     * 标量数据集合
     */
    private ScalarMsg scalar;
    /**
     * 表格数据集合
     */
    private ColumnsMsg columns;
    /**
     * 扩展信息，该对象必须可序列化。主要用于传递其他信息，比如：Trap数据。
     */
    private Object recInfo;
    /**
     * 采集
     */
    public static final int COLL_TYPE_COLL = 0;
    /**
     * 实例化
     */
    public static final int COLL_TYPE_INS = 1;
    /**
     * 返回接收到的信息，collType为该类型时，recInfo字段有效
     */
    public static final int COLL_TYPE_REC = 2;
    /**
     * 获取子对象信息
     */
    public static final int COLL_TYPE_INS_SUB = 3;
    /**
     * 采集成功
     */
    public static final int COLL_STATE_SUC = 0;
    /**
     * 采集失败
     */
    public static final int COLL_STATE_FAILD = 1;
    /**
     * 空采集器
     */
    public static final int COLL_STATE_EMPTY = 2;
    /**
     * 更新
     */
    public static final int COLL_STATE_UPDATE = 3;
    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -1L;
    /**
     * 编号产生器
     */
    private static final AtomicInteger idGen = new AtomicInteger(0);
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TransMsg.class);

    /**
     * 构造函数
     */
    public TransMsg()
    {
        this.msgId = idGen.getAndIncrement();
    }

    /**
     * 添加标量数据
     * @param oa 参数
     */
    public void addScalar(ObjAttribEntity oa)
    {
        if (oa == null)
        {
            logger.info("传输模块：TransMsg<addScalar>传入对象为空!");
            return;
        }
        if (scalar == null)
        {
            scalar = new ScalarMsg();
        }
        scalar.addInput(oa);
    }

    /**
     * 添加表格数据
     * @param oa 参数
     */
    public void addTable(ObjAttribEntity oa)
    {
        if (oa == null)
        {
            logger.info("传输模块：TransMsg<addTable>传入对象为空！");
            return;
        }
        if (columns == null)
        {
            columns = new ColumnsMsg();
        }
        ColumnMsg cm = new ColumnMsg(oa);
        columns.addColumn(cm);
    }

    /**
     * 获取全部对象属性
     * 
     * @return 包含的对象属性，如果无对象属性则返回空List
     */
    public List<ObjAttribEntity> getObjAttribs()
    {
        List<ObjAttribEntity> retval = new ArrayList<ObjAttribEntity>();
        if (scalar != null)
        {
            retval.addAll(scalar.getObjAttribs());
        }
        if (columns != null)
        {
            retval.addAll(columns.getObjAttribs());
        }
        return retval;
    }

    /**
     * 使用采集结果填充TransMsg
     * 
     * @param valMap
     */
    public void fillWithResult(Map<ObjAttribEntity, Object> valMap)
    {
        if (scalar != null)
        {
            scalar.fillWithResult(valMap);
        }
        if (columns != null)
        {
            columns.fillWithResult(valMap);
        }
    }

    /**
     * @return the scalar
     */
    public ScalarMsg getScalar()
    {
        return scalar;
    }

    /**
     * @param scalar the scalar to set
     */
    public void setScalar(ScalarMsg scalar)
    {
        this.scalar = scalar;
    }

    /**
     * @return the columns
     */
    public ColumnsMsg getColumns()
    {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(ColumnsMsg columns)
    {
        this.columns = columns;
    }

    /**
     * @return the objId
     */
    public int getObjId()
    {
        return objId;
    }

    /**
     * @param objId the objId to set
     */
    public void setObjId(int objId)
    {

        this.objId = objId;
        if (mo == null)
        {
            try
            {
                MObjectDal modal = ClassWrapper.wrapTrans(MObjectDal.class);
                MObjectEntity temp = modal.get(objId);
                this.mo = temp;
            }
            catch (Exception ex)
            {
                logger.error("", ex);
                this.mo = null;
            }
        }
    }

    public MObjectEntity getMo()
    {
        return mo;
    }

    /**
     * @param mo 参数
     */
    public void setMo(MObjectEntity mo)
    {
        this.objId = mo.getObjId();
        this.mo = mo;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public int getMsgId()
    {
        return msgId;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }

    public int getCollState()
    {
        return collState;
    }

    public void setCollState(int collState)
    {
        this.collState = collState;
    }

    public int getCollType()
    {
        return collType;
    }

    public void setCollType(int collType)
    {
        this.collType = collType;
    }

    public Object getRecInfo()
    {
        return recInfo;
    }

    public void setRecInfo(Object recInfo)
    {
        this.recInfo = recInfo;
    }

}
