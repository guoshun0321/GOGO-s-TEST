/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Room.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.business;

import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jnmp.dataaccess.Room2floorDal;
import jetsennet.jnmp.dataaccess.RoomDal;
import jetsennet.jnmp.dataaccess.Topo2rfDal;
import jetsennet.jnmp.entity.Room2floorEntity;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlLogicType;
import jetsennet.sqlclient.SqlParamType;
import jetsennet.sqlclient.SqlRelationType;

/**
 *  Bussiness
 */
public class Room
{
    /**
     * 新建
     * @param objXml
     */
    @Business
    public String addRoom(String objXml, String floorId) throws Exception
    {
        RoomDal dalRoom = new RoomDal();
        String roomId = "" + dalRoom.insertXml(objXml);
        Room2floorEntity r2f = new Room2floorEntity();
        r2f.setFloorId(Integer.parseInt(floorId));
        r2f.setRoomId(Integer.parseInt(roomId));
        Room2floorDal r2fDal = new Room2floorDal();
        r2fDal.insert(r2f);
        return roomId;
    }
    
    /**
     * 编辑
     * @param objXml
     * @throws Exception
     */
    @Business
    public void updateRoom(String objXml) throws Exception
    {
        RoomDal dalRoom = new RoomDal();
        dalRoom.updateXml(objXml);
    }
	  	
    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteRoom(int keyId) throws Exception
    {
    	Room2floorDal r2fDal = new Room2floorDal();
    	r2fDal.delete(new SqlCondition( "ROOM_ID" , "" + keyId, SqlLogicType.And , SqlRelationType.Equal, SqlParamType.Numeric , true));
    	
    	Topo2rfDal t2rf = new Topo2rfDal();
    	t2rf.delete(new SqlCondition( "RF_ID" , "" + keyId, SqlLogicType.And , SqlRelationType.Equal, SqlParamType.Numeric , true));
    	
        RoomDal dalRoom = new RoomDal();
        dalRoom.delete(keyId);
    }

}
