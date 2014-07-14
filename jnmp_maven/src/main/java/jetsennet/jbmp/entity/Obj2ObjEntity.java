/************************************************************************
日 期：2012-3-31
作 者: 余灵
版 本：v1.3
描 述: 
历 史：
 ************************************************************************/
package jetsennet.jbmp.entity;

import jetsennet.jbmp.dataaccess.base.annotation.Column;
import jetsennet.jbmp.dataaccess.base.annotation.Table;

/**
 * @author yl
 */
@Table(name = "BMP_OBJ2OBJ")
public class Obj2ObjEntity
{
    @Column(name = "OBJ_ID")
    private int objId;
    @Column(name = "NEXT_ID")
    private int nextId;
    @Column(name = "FIELD_1")
    private int field_1;
    @Column(name = "FIELD_2")
    private int field_2;

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
     * @return the nextId
     */
    public int getNextId()
    {
        return nextId;
    }

    /**
     * @param nextId the nextId to set
     */
    public void setNextId(int nextId)
    {
        this.nextId = nextId;
    }

    /**
     * @return the field_1
     */
    public int getField_1()
    {
        return field_1;
    }

    /**
     * @param field_1 the field_1 to set
     */
    public void setField_1(int field_1)
    {
        this.field_1 = field_1;
    }

    /**
     * @return the field_2
     */
    public int getField_2()
    {
        return field_2;
    }

    /**
     * @param field_2 the field_2 to set
     */
    public void setField_2(int field_2)
    {
        this.field_2 = field_2;
    }

}
