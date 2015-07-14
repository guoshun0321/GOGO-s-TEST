package jetsennet.orm.executor.keygen;

import jetsennet.orm.session.SessionBase;

public class KeyGenerationError implements IKeyGeneration<Long>
{

    @Override
    public Long genKey(SessionBase session, String tableName)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long[] genKeys(SessionBase session, String tableName, int num)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        throw new UnsupportedOperationException();
    }

}
