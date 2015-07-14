/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Id;
import jetsennet.jbmp.dataaccess.base.annotation.Table;


/**
 *
 * @author Li
 */
@Table(name="BMP_KNOWLEDGETYPE")
public class KnowledgeTypeEntity
{
    @Id
    @Column(name="TYPE_ID")
    private int typeId;
    @Column(name="TYPE_NAME")
    private String typeName;
    @Column(name="TYPE_DESC")
    private String typeDesc;
    @Column(name="PARENT_ID")
    private int parentId;

    public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public KnowledgeTypeEntity()
    {
    	
    }

    /**
     * @return the typeId
     */
    public int getTypeId()
    {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(int typeId)
    {
        this.typeId = typeId;
    }

    /**
     * @return the typeName
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
    
    /**
     * @return the typeDesc
     */
    public String getTypeDesc()
    {
        return typeDesc;
    }

    /**
     * @param typeDesc the typeDesc to set
     */
    public void setTypeDesc(String typeDesc)
    {
        this.typeDesc = typeDesc;
    }
}
