package jetsennet.orm.executor.keygen;

import jetsennet.orm.session.Session;

public interface IKeyGeneration
{

    /**
     * 获取一个单独的主键
     * 
     * @param session
     * @return
     */
    public long genKey(Session session);

    /**
     * 批量获取主键
     * 
     * @param session
     * @param num 主键数量
     * @return
     */
    public long[] genKeys(Session session, int num);
}
