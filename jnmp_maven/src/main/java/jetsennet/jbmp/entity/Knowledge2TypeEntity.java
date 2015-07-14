/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author Li
 */
@Table(name = "BMP_KNOWLEDGE2TYPE")
public class Knowledge2TypeEntity
{
    @Column(name = "TYPE_ID")
    private int typeId;
    @Column(name = "KNOWLEDGE_ID")
    private int knowledgeId;

    /**
     * 构造函数
     */
    public Knowledge2TypeEntity()
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
     * @return the knowledgeId
     */
    public int getKnowledgeId()
    {
        return knowledgeId;
    }

    /**
     * @param knowledgeId the knowledgeId to set
     */
    public void setKnowledgeId(int knowledgeId)
    {
        this.knowledgeId = knowledgeId;
    }
}
