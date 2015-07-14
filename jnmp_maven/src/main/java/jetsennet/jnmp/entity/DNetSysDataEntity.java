package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author ？
 */
@Table(name = "NMP_DNETSYSDATA")
public class DNetSysDataEntity
{

    @Column(name = "OBJ_ID")
    private int obj_id;
    @Column(name = "COLL_TIME")
    private long coll_time;
    @Column(name = "CONN_CURRENT")
    private Integer conn_current;
    @Column(name = "CONN_MAX")
    private Integer conn_max;
    @Column(name = "LOGON")
    private Integer logon;
    @Column(name = "USER_ANONYCURR")
    private Integer user_anonycurr;
    @Column(name = "USER_NANONYCURR")
    private Integer user_nanonycurr;
    @Column(name = "USER_ANONYMAX")
    private Integer user_anonymax;
    @Column(name = "USER_NANONYMAX")
    private Integer user_nanonymax;
    @Column(name = "USER_ANONYTOTAL")
    private Integer user_anonytotal;
    @Column(name = "USER_NANONYTOTAL")
    private Integer user_nanonytotal;
    @Column(name = "SEND_BYTES")
    private Long send_bytes;
    @Column(name = "SEND_PERSEC")
    private Integer send_persec;
    @Column(name = "REC_BYTES")
    private Long rec_bytes;
    @Column(name = "REC_PERSEC")
    private Integer rec_persec;
    @Column(name = "ERROR_NOTFOUND")
    private Integer error_notfound;
    @Column(name = "ERROR_LOCKED")
    private Integer error_locked;

    /**
     * @return the obj_id
     */
    public int getObj_id()
    {
        return obj_id;
    }

    /**
     * @param obj_id the obj_id to set
     */
    public void setObj_id(int obj_id)
    {
        this.obj_id = obj_id;
    }

    /**
     * @return the coll_time
     */
    public long getColl_time()
    {
        return coll_time;
    }

    /**
     * @param coll_time the coll_time to set
     */
    public void setColl_time(long coll_time)
    {
        this.coll_time = coll_time;
    }

    /**
     * @return the conn_current
     */
    public Integer getConn_current()
    {
        return conn_current;
    }

    /**
     * @param conn_current the conn_current to set
     */
    public void setConn_current(Integer conn_current)
    {
        this.conn_current = conn_current;
    }

    /**
     * @return the conn_max
     */
    public Integer getConn_max()
    {
        return conn_max;
    }

    /**
     * @param conn_max the conn_max to set
     */
    public void setConn_max(Integer conn_max)
    {
        this.conn_max = conn_max;
    }

    /**
     * @return the logon
     */
    public Integer getLogon()
    {
        return logon;
    }

    /**
     * @param logon the logon to set
     */
    public void setLogon(Integer logon)
    {
        this.logon = logon;
    }

    /**
     * @return the user_anonycurr
     */
    public Integer getUser_anonycurr()
    {
        return user_anonycurr;
    }

    /**
     * @param user_anonycurr the user_anonycurr to set
     */
    public void setUser_anonycurr(Integer user_anonycurr)
    {
        this.user_anonycurr = user_anonycurr;
    }

    /**
     * @return the user_nanonycurr
     */
    public Integer getUser_nanonycurr()
    {
        return user_nanonycurr;
    }

    /**
     * @param user_nanonycurr the user_nanonycurr to set
     */
    public void setUser_nanonycurr(Integer user_nanonycurr)
    {
        this.user_nanonycurr = user_nanonycurr;
    }

    /**
     * @return the user_anonymax
     */
    public Integer getUser_anonymax()
    {
        return user_anonymax;
    }

    /**
     * @param user_anonymax the user_anonymax to set
     */
    public void setUser_anonymax(Integer user_anonymax)
    {
        this.user_anonymax = user_anonymax;
    }

    /**
     * @return the user_nanonymax
     */
    public Integer getUser_nanonymax()
    {
        return user_nanonymax;
    }

    /**
     * @param user_nanonymax the user_nanonymax to set
     */
    public void setUser_nanonymax(Integer user_nanonymax)
    {
        this.user_nanonymax = user_nanonymax;
    }

    /**
     * @return the user_anonytotal
     */
    public Integer getUser_anonytotal()
    {
        return user_anonytotal;
    }

    /**
     * @param user_anonytotal the user_anonytotal to set
     */
    public void setUser_anonytotal(Integer user_anonytotal)
    {
        this.user_anonytotal = user_anonytotal;
    }

    /**
     * @return the user_nanonytotal
     */
    public Integer getUser_nanonytotal()
    {
        return user_nanonytotal;
    }

    /**
     * @param user_nanonytotal the user_nanonytotal to set
     */
    public void setUser_nanonytotal(Integer user_nanonytotal)
    {
        this.user_nanonytotal = user_nanonytotal;
    }

    /**
     * @return the send_bytes
     */
    public Long getSend_bytes()
    {
        return send_bytes;
    }

    /**
     * @param send_bytes the send_bytes to set
     */
    public void setSend_bytes(Long send_bytes)
    {
        this.send_bytes = send_bytes;
    }

    /**
     * @return the send_persec
     */
    public Integer getSend_persec()
    {
        return send_persec;
    }

    /**
     * @param send_persec the send_persec to set
     */
    public void setSend_persec(Integer send_persec)
    {
        this.send_persec = send_persec;
    }

    /**
     * @return the rec_bytes
     */
    public Long getRec_bytes()
    {
        return rec_bytes;
    }

    /**
     * @param rec_bytes the rec_bytes to set
     */
    public void setRec_bytes(Long rec_bytes)
    {
        this.rec_bytes = rec_bytes;
    }

    /**
     * @return the rec_persec
     */
    public Integer getRec_persec()
    {
        return rec_persec;
    }

    /**
     * @param rec_persec the rec_persec to set
     */
    public void setRec_persec(Integer rec_persec)
    {
        this.rec_persec = rec_persec;
    }

    /**
     * @return the error_notfound
     */
    public Integer getError_notfound()
    {
        return error_notfound;
    }

    /**
     * @param error_notfound the error_notfound to set
     */
    public void setError_notfound(Integer error_notfound)
    {
        this.error_notfound = error_notfound;
    }

    /**
     * @return the error_locked
     */
    public Integer getError_locked()
    {
        return error_locked;
    }

    /**
     * @param error_locked the error_locked to set
     */
    public void setError_locked(Integer error_locked)
    {
        this.error_locked = error_locked;
    }
}
