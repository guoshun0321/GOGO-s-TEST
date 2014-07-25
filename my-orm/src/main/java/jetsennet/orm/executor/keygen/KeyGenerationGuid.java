package jetsennet.orm.executor.keygen;

import java.util.UUID;

import jetsennet.orm.session.Session;
import jetsennet.orm.session.SessionBase;

/**
 * 目前采用和UUID一样的方法，后期做修改
 * 
 * @author 郭祥
 */
public class KeyGenerationGuid implements IKeyGeneration<String>
{

    @Override
    public String genKey(SessionBase session, String tableName)
    {
        return UUID.randomUUID().toString().toUpperCase();
    }

    @Override
    public String[] genKeys(SessionBase session, String tableName, int num)
    {
        return genKeys(session, tableName, null, num);
    }

    @Override
    public String[] genKeys(SessionBase session, String tableName, String fieldName, int num)
    {
        String[] retval = new String[num];
        for (int i = 0; i < num; i++)
        {
            retval[i] = UUID.randomUUID().toString().toUpperCase();
        }
        return retval;
    }

}
