package jetsennet.jsmp.nav.syn;

public class DataSynContentEntity
{

    private int opFlag;

    private Object obj;
    /**
     * 修改
     */
    public static final int OP_FLAG_MOD = 0;
    /**
     * 删除
     */
    public static final int OP_FLAG_DEL = 1;
    /**
     * 全量发布
     */
    public static final int OP_FLAG_ALL = 2;

    public int getOpFlag()
    {
        return opFlag;
    }

    public void setOpFlag(int opFlag)
    {
        this.opFlag = opFlag;
    }

    public Object getObj()
    {
        return obj;
    }

    public void setObj(Object obj)
    {
        this.obj = obj;
    }

}
