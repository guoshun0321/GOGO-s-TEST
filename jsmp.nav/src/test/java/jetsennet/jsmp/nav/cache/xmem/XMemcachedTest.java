package jetsennet.jsmp.nav.cache.xmem;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import junit.framework.TestCase;
import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.transcoders.StringTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

public class XMemcachedTest
{

    public void testBase() throws IOException
    {
//        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000 192.168.8.175:12001"));
//        // AddrUtil.getAddresses("server1:11211 server2:11211")
//        // 宕机报警  
//        builder.setFailureMode(true);
//        // 使用二进制文件  
//        builder.setCommandFactory(new BinaryCommandFactory());
//        /** 
//         * 设置连接池大小，即客户端个数 
//         * In a high concurrent enviroment,you may want to pool memcached clients. 
//         * But a xmemcached client has to start a reactor thread and some thread pools, 
//         * if you create too many clients,the cost is very large.  
//         * Xmemcached supports connection pool instreadof client pool. 
//         * you can create more connections to one or more memcached servers, 
//         * and these connections share the same reactor and thread pools, 
//         * it will reduce the cost of system. 
//         *  默认的pool size是1。设置这一数值不一定能提高性能，请依据你的项目的测试结果为准。初步的测试表明只有在大并发下才有提升。 
//         *  设置连接池的一个不良后果就是，同一个memcached的连接之间的数据更新并非同步的 
//         *  因此你的应用需要自己保证数据更新的原子性（采用CAS或者数据之间毫无关联）。 
//         */
//        builder.setConnectionPoolSize(10);
//        MemcachedClient client = builder.build();
//
//        try
//        {
//            client.flushAll();
//
//            /**
//             * 第一个是存储的key名称，
//             * 第二个是expire时间（单位秒），超过这个时间,memcached将这个数据替换出去，0表示永久存储（默认是一个月)
//             * 第三个参数就是实际存储的数据
//             */
//            client.set("hello", 0, "Hello,xmemcached");
//            String value = client.get("hello");
//            System.out.println("hello=" + value);
//            client.delete("hello");
//            value = client.get("hello");
//            System.out.println("hello=" + value);
//
//            // value=client.get(“hello”,3000);
//
//            /**
//             * Memcached是通过cas协议实现原子更新，所谓原子更新就是compare and set，
//             * 原理类似乐观锁，每次请求存储某个数据同时要附带一个cas值， memcached比对这个cas值与当前存储数据的cas值是否相等，
//             * 如果相等就让新的数据覆盖老的数据，如果不相等就认为更新失败， 这在并发环境下特别有用
//             */
//            client.set("a", 0, 123);
//            GetsResponse<Integer> result = client.gets("a");
//            long cas = result.getCas();
//            // 尝试将a的值更新为2
//            if (!client.cas("a", 0, 2, cas))
//            {
//                System.err.println("cas error");
//            }
//        }
//        catch (MemcachedException e)
//        {
//            System.err.println("MemcachedClient operation fail");
//            e.printStackTrace();
//        }
//        catch (TimeoutException e)
//        {
//            System.err.println("MemcachedClient operation timeout");
//            e.printStackTrace();
//        }
//        catch (InterruptedException e)
//        {
//            // ignore
//        }
//        try
//        {
//            // close memcached client
//            client.shutdown();
//        }
//        catch (IOException e)
//        {
//            System.err.println("Shutdown MemcachedClient fail");
//            e.printStackTrace();
//        }

    }

    //    public void testBase1() throws TimeoutException, InterruptedException, MemcachedException, IOException
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        MemcachedClient client = builder.build();
    //        client.flushAll();
    //        if (!client.set("hello", 0, "world"))
    //        {
    //            System.err.println("set error");
    //        }
    //        if (client.add("hello", 0, "dennis"))
    //        {
    //            System.err.println("Add error,key is existed");
    //        }
    //        if (!client.replace("hello", 0, "dennis"))
    //        {
    //            System.err.println("replace error");
    //        }
    //        client.append("hello", " good");
    //        client.prepend("hello", "hello ");
    //        String name = client.get("hello", new StringTranscoder());
    //        System.out.println(name);
    //
    //        /**
    //         * 而删除数据则是通过deleteWithNoReply方法，这个方法删除数据并且告诉memcached
    //         * 不用返回应答，因此这个方法不会等待应答直接返回，特别适合于批量处理
    //         */
    //        client.deleteWithNoReply("hello");
    //    }
    //
    //    public void testIncrDecr() throws IOException, TimeoutException, InterruptedException, MemcachedException
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        MemcachedClient client = builder.build();
    //        /**
    //         * 第一个参数指定递增的key名称， 第二个参数指定递增的幅度大小， 第三个参数指定当key不存在的情况下的初始值。
    //         * 两个参数的重载方法省略了第三个参数，默认指定为0。
    //         */
    //        assert (1 == client.incr("a", 5, 1));
    //        assert (6 == client.incr("a", 5));
    //        assert (10 == client.incr("a", 4));
    //        assert (9 == client.decr("a", 1));
    //        assert (7 == client.decr("a", 2));
    //    }
    //
    //    public void testCounter() throws Exception
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        MemcachedClient client = builder.build();
    //        Counter counter = client.getCounter("counter", 0);
    //        counter.incrementAndGet();
    //        counter.decrementAndGet();
    //        counter.addAndGet(-10);
    //    }
    //
    //    public void auth() throws Exception
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        builder.addAuthInfo(AddrUtil.getOneAddress("192.168.8.175:12000"), AuthInfo.typical("cacheuser", "123456"));
    //        // Must use binary protocol
    //        builder.setCommandFactory(new BinaryCommandFactory());
    //        MemcachedClient client = builder.build();
    //    }
    //
    //    public void nioPool() throws Exception
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        builder.setConnectionPoolSize(5);
    //    }
    //
    //    /**
    //     *这里应该安装kestrel消息服务器，才能使用如下API生效
    //     * @throws IOException
    //     * @throws MemcachedException 
    //     * @throws InterruptedException 
    //     * @throws TimeoutException 
    //     */
    //
    //    public void testGet() throws IOException, TimeoutException, InterruptedException, MemcachedException
    //    {
    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("192.168.8.175:12000"));
    //        MemcachedClient client = builder.build();
    //        String value = client.get("1");
    //        System.out.println("hello=" + value);
    //    }
    //
    //    //    public void testGet2() throws IOException, TimeoutException, InterruptedException, MemcachedException{
    //    //        MemcachedClientBuilder builder = new XMemcachedClientBuilder(
    //    //                AddrUtil.getAddresses("192.168.8.175:12000"));
    //    //        MemcachedClient client = builder.build();
    //    //        String value = client.get("srp_"+MD5Util.MD5("3rdsearch_周杰伦"));
    //    //        System.out.println(value);
    //    //    }
    //
    //    public static void main(String[] args) throws Exception
    //    {
    //        XMemcachedTest test = new XMemcachedTest();
    //        test.testBase();
    //    }
}
