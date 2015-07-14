/**********************************************************************
 * 日 期: 2012-08-23
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: AlarmlogEntity.java
 * 历 史: 2012-08-23 Create
 *********************************************************************/
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 
 */
@Table(name = "BMP_ALARMLOG")
public class AlarmlogEntity
{
    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private int id;

    /**
     * 报警编号
     */
    @Column(name = "ALARMEVT_ID")
    private int alarmevtId;

    /**
     * USERID
     */
    @Column(name = "USERID")
    private int userid;

    /**
     * OPERATE_USER
     */
    @Column(name = "OPERATE_USER")
    private String operateUser;

    /**
     * OPERATE_TIME
     */
    @Column(name = "OPERATE_TIME")
    private Date operateTime;

    /**
     * 操作动作
     */
    @Column(name = "OPERATE")
    private String operate;

    /**
     * 操作类型
     */
    @Column(name = "OPERATETYPE")
    private int operatetype;

    /**
     * FIELD2
     */
    @Column(name = "FIELD2")
    private String field2;

    /**
     * FIELD3
     */
    @Column(name = "FIELD3")
    private String field3;

    /**
     * FIELD1
     */
    @Column(name = "FIELD1")
    private String field1;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getAlarmevtId()
    {
        return alarmevtId;
    }

    public void setAlarmevtId(int alarmevtId)
    {
        this.alarmevtId = alarmevtId;
    }

    public int getUserid()
    {
        return userid;
    }

    public void setUserid(int userid)
    {
        this.userid = userid;
    }

    public String getOperateUser()
    {
        return operateUser;
    }

    public void setOperateUser(String operateUser)
    {
        this.operateUser = operateUser;
    }

    public Date getOperateTime()
    {
        return operateTime;
    }

    public void setOperateTime(Date operateTime)
    {
        this.operateTime = operateTime;
    }

    public String getOperate()
    {
        return operate;
    }

    public void setOperate(String operate)
    {
        this.operate = operate;
    }

    public int getOperatetype()
    {
        return operatetype;
    }

    public void setOperatetype(int operatetype)
    {
        this.operatetype = operatetype;
    }

    public String getField2()
    {
        return field2;
    }

    public void setField2(String field2)
    {
        this.field2 = field2;
    }

    public String getField3()
    {
        return field3;
    }

    public void setField3(String field3)
    {
        this.field3 = field3;
    }

    public String getField1()
    {
        return field1;
    }

    public void setField1(String field1)
    {
        this.field1 = field1;
    }
}
