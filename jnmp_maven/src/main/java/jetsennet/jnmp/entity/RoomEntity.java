/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: RoomEntity.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 
 */
@Table(name="NMP_ROOM")
public class RoomEntity
{
    /**
     * ROOM_ID
     */
	@Id
    @Column(name="ROOM_ID")   
    private int roomId;
    
    /**
     * FIELD_1
     */
    @Column(name="FIELD_1")   
    private String field1;
    
    /**
     * FIELD_2
     */
    @Column(name="FIELD_2")   
    private String field2;
    
    /**
     * ROOM_NAME
     */
    @Column(name="ROOM_NAME")   
    private String roomName;
    
    /**
     * ROOM_ALIAS
     */
    @Column(name="ROOM_ALIAS")   
    private String roomAlias;
    

    public int getRoomId()
    {
        return roomId;
    }
				 
    public void setRoomId(int roomId)
    {
        this.roomId = roomId;
    }
    public String getField1()
    {
        return field1;
    }
				 
    public void setField1(String field1)
    {
        this.field1 = field1;
    }
    public String getField2()
    {
        return field2;
    }
				 
    public void setField2(String field2)
    {
        this.field2 = field2;
    }
    public String getRoomName()
    {
        return roomName;
    }
				 
    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }
    public String getRoomAlias()
    {
        return roomAlias;
    }
				 
    public void setRoomAlias(String roomAlias)
    {
        this.roomAlias = roomAlias;
    }
}


                                        