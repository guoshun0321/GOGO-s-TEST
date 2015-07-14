package jetsennet.orm.cmp;

import java.util.concurrent.ConcurrentHashMap;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.session.SqlSessionFactory;
import jetsennet.orm.session.SqlSessionFactoryBuilder;
import jetsennet.orm.transaction.auto.ClassWrapper;

public class CmpMgr
{

    private static final ConcurrentHashMap<Configuration, Cmp> cmpMap = new ConcurrentHashMap<Configuration, Cmp>();

    public static final Cmp ensureCmp(Configuration config)
    {
        Cmp retval = cmpMap.get(config);
        if (retval == null)
        {
            SqlSessionFactory factory = SqlSessionFactoryBuilder.builder(config);
            Cmp cmp = ClassWrapper.wrap(Cmp.class, factory, config);
            Cmp temp = cmpMap.putIfAbsent(config, cmp);
            if (temp == null)
            {
                retval = cmp;
            }
            else
            {
                retval = temp;
            }
        }
        return retval;
    }

}
