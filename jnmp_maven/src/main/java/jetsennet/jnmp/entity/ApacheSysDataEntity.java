package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NMP_APACHESYSDATA")
public class ApacheSysDataEntity
{

    /**
     * 空闲线程
     */
    @Column(name = "OBJ_ID")
    protected int objId;
    @Column(name = "COLL_TIME")
    protected long collTime;
    @Column(name = "TOTAL_ACCESSES")
    protected int totalAccesses;
    @Column(name = "TOTAL_KBYTES")
    protected int totalKbytes;
    @Column(name = "UPTIME")
    protected int upTime;
    @Column(name = "REQ_PERSEC")
    protected float reqPersec;
    @Column(name = "BYTES_PERSEC")
    protected float bytesPersec;
    @Column(name = "BYTES_PERREQ")
    protected float bytesPerreq;
    @Column(name = "BUSY_WORKERS")
    protected int busyWorkers;
    @Column(name = "IDLE_WORKERS")
    protected int idleWorkers;

    /**
     * 构造函数
     */
    public ApacheSysDataEntity()
    {
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
    }

    /**
     * @return the collTime
     */
    public long getCollTime()
    {
        return collTime;
    }

    /**
     * @param collTime the collTime to set
     */
    public void setCollTime(long collTime)
    {
        this.collTime = collTime;
    }

    /**
     * @return the totalAccesses
     */
    public int getTotalAccesses()
    {
        return totalAccesses;
    }

    /**
     * @param totalAccesses the totalAccesses to set
     */
    public void setTotalAccesses(int totalAccesses)
    {
        this.totalAccesses = totalAccesses;
    }

    /**
     * @return the totalKbytes
     */
    public int getTotalKbytes()
    {
        return totalKbytes;
    }

    /**
     * @param totalKbytes the totalKbytes to set
     */
    public void setTotalKbytes(int totalKbytes)
    {
        this.totalKbytes = totalKbytes;
    }

    /**
     * @return the upTime
     */
    public int getUpTime()
    {
        return upTime;
    }

    /**
     * @param upTime the upTime to set
     */
    public void setUpTime(int upTime)
    {
        this.upTime = upTime;
    }

    /**
     * @return the reqPersec
     */
    public float getReqPersec()
    {
        return reqPersec;
    }

    /**
     * @param reqPersec the reqPersec to set
     */
    public void setReqPersec(float reqPersec)
    {
        this.reqPersec = reqPersec;
    }

    /**
     * @return the bytesPersec
     */
    public float getBytesPersec()
    {
        return bytesPersec;
    }

    /**
     * @param bytesPersec the bytesPersec to set
     */
    public void setBytesPersec(float bytesPersec)
    {
        this.bytesPersec = bytesPersec;
    }

    /**
     * @return the bytesPerreq
     */
    public float getBytesPerreq()
    {
        return bytesPerreq;
    }

    /**
     * @param bytesPerreq the bytesPerreq to set
     */
    public void setBytesPerreq(float bytesPerreq)
    {
        this.bytesPerreq = bytesPerreq;
    }

    /**
     * @return the busyWorkers
     */
    public int getBusyWorkers()
    {
        return busyWorkers;
    }

    /**
     * @param busyWorkers the busyWorkers to set
     */
    public void setBusyWorkers(int busyWorkers)
    {
        this.busyWorkers = busyWorkers;
    }

    /**
     * @return the idleWorkers
     */
    public int getIdleWorkers()
    {
        return idleWorkers;
    }

    /**
     * @param idleWorkers the idleWorkers to set
     */
    public void setIdleWorkers(int idleWorkers)
    {
        this.idleWorkers = idleWorkers;
    }
}
