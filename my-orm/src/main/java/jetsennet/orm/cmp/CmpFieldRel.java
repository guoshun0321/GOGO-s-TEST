package jetsennet.orm.cmp;

public class CmpFieldRel
{

    public final String pTable;

    public final String pField;

    public final String sTable;

    public final String sField;

    public CmpFieldRel(String pTable, String pField, String sTable, String sField)
    {
        this.pTable = pTable;
        this.pField = pField;
        this.sTable = sTable;
        this.sField = sField;
    }

}
