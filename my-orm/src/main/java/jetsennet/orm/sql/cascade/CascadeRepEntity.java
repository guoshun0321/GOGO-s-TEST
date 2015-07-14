package jetsennet.orm.sql.cascade;

public class CascadeRepEntity
{

    public final String table;

    public final String field;

    public CascadeRepEntity(String table, String field)
    {
        this.table = table;
        this.field = field;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(this.table).append("$").append(this.field).toString();
    }

}
