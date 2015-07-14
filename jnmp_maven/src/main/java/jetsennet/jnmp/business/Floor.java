/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Floor.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.business;

import jetsennet.jbmp.dataaccess.base.annotation.Business;
import jetsennet.jnmp.dataaccess.FloorDal;

/**
 *  Bussiness
 */
public class Floor
{
    /**
     * 新建
     * @param objXml
     */
    @Business
    public String addFloor(String objXml) throws Exception
    {
        FloorDal dalFloor = new FloorDal();
        return "" + dalFloor.insertXml(objXml);
    }
    
    /**
     * 编辑
     * @param objXml
     * @throws Exception
     */
    @Business
    public void updateFloor(String objXml) throws Exception
    {
        FloorDal dalFloor = new FloorDal();
        dalFloor.updateXml(objXml);
    }
	  	
    /**
     * 删除
     * @param key
     * @throws Exception
     */
    @Business
    public void deleteFloor(int keyId) throws Exception
    {
        FloorDal dalFloor = new FloorDal();
        dalFloor.delete("DELETE FROM NMP_TOPO2RF WHERE RF_ID IN (SELECT A.ROOM_ID FROM NMP_ROOM A INNER JOIN NMP_ROOM2FLOOR B ON A.ROOM_ID = B.ROOM_ID WHERE B.FLOOR_ID = " + keyId + ")");
        dalFloor.delete("DELETE FROM NMP_ROOM WHERE ROOM_ID IN (SELECT ROOM_ID FROM NMP_ROOM2FLOOR WHERE FLOOR_ID = " + keyId + ")");
        dalFloor.delete("DELETE FROM NMP_ROOM2FLOOR WHERE FLOOR_ID = " + keyId);
        dalFloor.delete(keyId);
    }

}
