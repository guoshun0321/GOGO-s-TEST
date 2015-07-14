/**
 * 
 */
package jetsennet.jbmp.entity;

import java.util.Date;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author lianghongjie
 */
@Table(name = "BMP_TOPOMAP")
public class TopoMapEntity
{
    @Id
    @Column(name = "MAP_ID")
    private int mapId;
    @Column(name = "MAP_NAME")
    private String mapName;
    @Column(name = "MAP_INFO")
    private String mapInfo;
    @Column(name = "GROUP_ID")
    private int groupId;
    @Column(name = "MAP_STATE")
    private int mapState;
    @Column(name = "CREATE_USERID")
    private int createUserId;
    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "CREATE_TIME")
    private Date createTime;
    @Column(name = "FIELD_1")
    private String field1;

    public int getMapId()
    {
        return mapId;
    }

    public void setMapId(int mapId)
    {
        this.mapId = mapId;
    }

    public String getMapName()
    {
        return mapName;
    }

    public void setMapName(String mapName)
    {
        this.mapName = mapName;
    }

    public String getMapInfo()
    {
        return mapInfo;
    }

    public void setMapInfo(String mapInfo)
    {
        this.mapInfo = mapInfo;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public int getMapState()
    {
        return mapState;
    }

    public void setMapState(int mapState)
    {
        this.mapState = mapState;
    }

    public int getCreateUserId()
    {
        return createUserId;
    }

    public void setCreateUserId(int createUserId)
    {
        this.createUserId = createUserId;
    }

    public String getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(String createUser)
    {
        this.createUser = createUser;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
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
