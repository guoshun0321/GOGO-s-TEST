package jetsennet.jsmp.nav.syn.cache;

public interface IDataSynCache<T>
{

    public void insert(T obj);

    public void update(T obj);

    public void delete(T obj);

}
