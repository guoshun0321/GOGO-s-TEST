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
@Table(name="NMP_TOPO2RF")
public class Topo2rfEntity
{
    /**
     * ID
     */
    @Id
    @Column(name="ID") 
    private int id;
    
    /**
     * MAP_ID
     */
    @Column(name="MAP_ID")   
    private int mapId;
    
    /**
     * RF_ID
     */
    @Column(name="RF_ID")   
    private int rfId;
    
    /**
     * RF_TYPE
     */
    @Column(name="RF_TYPE")   
    private int rfType;
    
    /**
     * TOPO_TYPE
     */
    @Column(name="TOPO_TYPE")   
    private int topoType;
    
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
    

    public int getId()
    {
        return id;
    }
				 
    public void setId(int id)
    {
        this.id = id;
    }
    public int getRfId()
    {
        return rfId;
    }
				 
    public void setRfId(int rfId)
    {
        this.rfId = rfId;
    }
    public int getRfType()
    {
        return rfType;
    }
				 
    public void setRfType(int rfType)
    {
        this.rfType = rfType;
    }
    public int getTopoType()
    {
        return topoType;
    }
				 
    public void setTopoType(int topoType)
    {
        this.topoType = topoType;
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

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}
}


                                        