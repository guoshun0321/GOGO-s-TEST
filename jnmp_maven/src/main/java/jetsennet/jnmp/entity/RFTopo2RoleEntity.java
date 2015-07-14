/**********************************************************************
 * 日 期: 2013-07-01
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: Topo2rfEntity.java
 * 历 史: 2013-07-01 Create
 *********************************************************************/
package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 
 */
@Table(name="NMP_RFTOPO2ROLE")
public class RFTopo2RoleEntity
{
    /**
     * MAP_ID
     */
    @Column(name="MAP_ID")   
    private int mapId;
    
    /**
     * ROLE_ID
     */
    @Column(name="ROLE_ID")   
    private int roleId;
    
	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
}


                                        