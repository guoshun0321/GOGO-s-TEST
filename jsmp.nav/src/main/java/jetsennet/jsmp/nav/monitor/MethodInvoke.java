package jetsennet.jsmp.nav.monitor;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class MethodInvoke implements MethodInvokeMBean
{

    /**
     * 统计信息map
     */
    private ConcurrentHashMap<String, MethodInvokeStat> map = new ConcurrentHashMap<String, MethodInvokeStat>();

    @Override
    public String desc()
    {
        Collection<MethodInvokeStat> coll = map.values();
        StringBuilder sb = new StringBuilder();
        for (MethodInvokeStat ms : coll)
        {
            sb.append(ms.toString());
        }
        return sb.toString();
    }

    public void add(MethodInvokeMMsg msg)
    {
        String name = msg.getMethodName();
        MethodInvokeStat retval = map.get(name);
        if (retval == null)
        {
            retval = new MethodInvokeStat(msg);
            MethodInvokeStat temp = map.putIfAbsent(name, retval);
            if (temp != null)
            {
                retval = temp;
            }
        }
        retval.add(msg);
    }

    private class MethodInvokeStat
    {
        /**
         * 方法名称
         */
        public String name;
        /**
         * 总调用次数
         */
        public long total;
        /**
         * 成功调用次数
         */
        public long sucTotal;
        /**
         * 总调用时间
         */
        public long totalTime;

        public MethodInvokeStat(MethodInvokeMMsg msg)
        {
            this.name = msg.getMethodName();
            this.total = 0;
            this.sucTotal = 0;
            this.totalTime = 0;
        }

        public void add(MethodInvokeMMsg msg)
        {
            this.total++;
            if (!msg.isException())
            {
                this.sucTotal += 1;
            }
            this.totalTime += (msg.getEndTime() - msg.getStartTime());
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("方法名称：").append(name).append(",");
            sb.append("总调用次数：").append(total).append(",");
            sb.append("成功调用次数：").append(sucTotal).append(",");
            sb.append("总调用时间：").append(totalTime).append(";");
            return sb.toString();
        }
    }

}
