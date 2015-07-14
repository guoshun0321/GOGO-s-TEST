/**********************************************************************
 * 日 期: 2013-06-30
 * 作 者: AutoCodeCRUD
 * 版 本: v1.0
 * 描 述: FloorEntity.java
 * 历 史: 2013-06-30 Create
 *********************************************************************/
package jetsennet.jnmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * 
 */
@Table(name="NMP_FLOOR")
public class FloorEntity
{
    /**
     * FLOOR_ID
     */
    @Id
    @Column(name="FLOOR_ID") 
    private int floorId;
    
    /**
     * FLOOR_NAME
     */
    @Column(name="FLOOR_NAME")   
    private String floorName;
    
    /**
     * FLOOR_ALIAS
     */
    @Column(name="FLOOR_ALIAS")   
    private String floorAlias;
    
    /**
     * FLOOR_ATTACH
     */
    @Column(name="FLOOR_ATTACH")   
    private int floorAttach;
    
    /**
     * FLOOR_NUM
     */
    @Column(name="FLOOR_NUM")   
    private int floorNum;
    
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

	public int getFloorId() {
		return floorId;
	}

	public void setFloorId(int floorId) {
		this.floorId = floorId;
	}

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public String getFloorAlias() {
		return floorAlias;
	}

	public void setFloorAlias(String floorAlias) {
		this.floorAlias = floorAlias;
	}

	public int getFloorAttach() {
		return floorAttach;
	}

	public void setFloorAttach(int floorAttach) {
		this.floorAttach = floorAttach;
	}

	public int getFloorNum() {
		return floorNum;
	}

	public void setFloorNum(int floorNum) {
		this.floorNum = floorNum;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}
}


                                        