/************************************************************************
日 期：2012-3-7
作 者: 郭祥
版 本: v1.3
描 述: 自动发现结果
历 史:
 ************************************************************************/
package jetsennet.jbmp.entity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;
import jetsennet.util.StringUtil;

/**
 * 自动发现结果
 * @author 郭祥
 */
@Table(name = "BMP_AUTODISOBJ")
public class AutoDisObjEntity
{

    /**
     * ID
     */
    @Id
    @Column(name = "OBJ_ID")
    private int objId;
    /**
     * 名称
     */
    @Column(name = "OBJ_NAME")
    private String objName;
    /**
     * IP地址
     */
    @Column(name = "IP")
    private String ip;
    /**
     * 数字形式的IP
     */
    @Column(name = "IP_NUM")
    private long ipNum;
    /**
     * CLASS_ID
     */
    @Column(name = "CLASS_ID")
    private int classId;
    /**
     * COLL_ID
     */
    @Column(name = "COLL_ID")
    private int collId;
    /**
     * TASK_ID
     */
    @Column(name = "TASK_ID")
    private int taskId;
    /**
     * 状态
     */
    @Column(name = "OBJ_STATUS")
    private int objStatus;
    /**
     * 创建状态
     */
    @Column(name = "REC_STATUS")
    private int recStatus;
    /**
     * 用户名
     */
    @Column(name = "USER_NAME")
    private String userName;
    /**
     * 密码
     */
    @Column(name = "PASSWORD")
    private String password;
    /**
     * 版本
     */
    @Column(name = "VERSION")
    private String version;
    /**
     * 端口
     */
    @Column(name = "PORT")
    private String port;
    /**
     * 描述
     */
    @Column(name = "OBJ_DESC")
    private String objDesc;
    /**
     * FIELD_1
     */
    @Column(name = "FIELD_1")
    private String field1;
    /**
     * 操作状态，用于自动发现
     */
    private int opStatus;
    /**
     * 附加信息
     */
    private Map<String, String> infos;
    // 对象状态
    /**
     * 可用
     */
    public static final int STATUS_USABLE = 0;
    /**
     * 上一次更新时新增
     */
    public static final int STATUS_NEW = 1;
    /**
     * 上一次更新时删除
     */
    public static final int STATUS_DELETE = 2;
    /**
     * 上一次更新时修改
     */
    public static final int STATUS_UPDATE = 3;
    /**
     * 不可用
     */
    public static final int STATUS_UNUSABLE = 4;
    // 创建状态
    /**
     * 未创建
     */
    public static final int RECORD_STATUS_UNCREATED = 0;
    /**
     * 已创建
     */
    public static final int RECORD_STATUS_CREATED = 1;
    /**
     * 操作状态，新增
     */
    public static final int OP_STATUS_NEW = 0;
    /**
     * 操作状态，更新
     */
    public static final int OP_STATUS_UPD1 = 1;
    /**
     * 操作状态，更新
     */
    public static final int OP_STATUS_UPD2 = 2;
    /**
     * 操作状态，更新
     */
    public static final int OP_STATUS_UPD3 = 3;
    /**
     * 操作状态，更新
     */
    public static final int OP_STATUS_UPD4 = 4;
    /**
     * 操作状态，删除
     */
    public static final int OP_STATUS_DEL = 5;

    /**
     * 添加附加信息
     * @param key 键
     * @param value 值
     */
    public void addInfo(String key, String value)
    {
        if (infos == null)
        {
            infos = new HashMap<String, String>();
        }
        infos.put(key, value);
    }

    /**
     * 获取附加信息
     * @param key 键
     * @return 结果
     */
    public String getInfo(String key)
    {
        if (infos != null)
        {
            return infos.get(key);
        }
        return null;
    }

    /**
     * 从xml中获取信息
     * @param key
     * @return
     */
    public String getInfoFromXml(String key)
    {
        String retval = null;
        if (key != null && !StringUtil.isNullOrEmpty(this.objDesc))
        {
            String beginStr = "<" + key + ">";
            String endStr = "</" + key + ">";
            int begin = objDesc.indexOf(beginStr) + beginStr.length();
            int end = objDesc.indexOf(endStr);
            if (begin > 0 && end > 0 && end > begin)
            {
                retval = objDesc.substring(begin, end);
            }
        }
        return retval;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(ip);
        sb.append("<");
        sb.append(objName);
        sb.append(">，类型");
        sb.append("<");
        sb.append(classId);
        sb.append(">，操作类型");
        sb.append("<");
        sb.append(opStatus);
        sb.append(">");
        return sb.toString();
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
     * @return the objName
     */
    public String getObjName()
    {
        return objName;
    }

    /**
     * @param objName the objName to set
     */
    public void setObjName(String objName)
    {
        this.objName = objName;
    }

    /**
     * @return the ip
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * @return the ipNum
     */
    public long getIpNum()
    {
        return ipNum;
    }

    /**
     * @param ipNum the ipNum to set
     */
    public void setIpNum(long ipNum)
    {
        this.ipNum = ipNum;
    }

    /**
     * @return the classId
     */
    public int getClassId()
    {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(int classId)
    {
        this.classId = classId;
    }

    /**
     * @return the collId
     */
    public int getCollId()
    {
        return collId;
    }

    /**
     * @param collId the collId to set
     */
    public void setCollId(int collId)
    {
        this.collId = collId;
    }

    /**
     * @return the objStatus
     */
    public int getObjStatus()
    {
        return objStatus;
    }

    /**
     * @param objStatus the objStatus to set
     */
    public void setObjStatus(int objStatus)
    {
        this.objStatus = objStatus;
    }

    /**
     * @return the objDesc
     */
    public String getObjDesc()
    {
        return objDesc;
    }

    /**
     * @param objDesc the objDesc to set
     */
    public void setObjDesc(String objDesc)
    {
        this.objDesc = objDesc;
    }

    /**
     * @return the field1
     */
    public String getField1()
    {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1)
    {
        this.field1 = field1;
    }

    /**
     * @return the userName
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return the port
     */
    public String getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * @return the recStatus
     */
    public int getRecStatus()
    {
        return recStatus;
    }

    /**
     * @param recStatus the recStatus to set
     */
    public void setRecStatus(int recStatus)
    {
        this.recStatus = recStatus;
    }

    /**
     * @return the taskId
     */
    public int getTaskId()
    {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public int getOpStatus()
    {
        return opStatus;
    }

    public void setOpStatus(int opStatus)
    {
        this.opStatus = opStatus;
    }
}
