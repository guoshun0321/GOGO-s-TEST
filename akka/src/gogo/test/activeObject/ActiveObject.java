package gogo.test.activeObject;

import java.util.concurrent.LinkedBlockingQueue;

public class ActiveObject
{

    private Scheduler scheduler;

    public ActiveObject()
    {
        // TODO Auto-generated constructor stub
    }

    public void init()
    {
        ActivationList list = new ActivationList();
        scheduler = new Scheduler(list);
        scheduler.init();
    }

    public void stop()
    {

    }

    public Future get(String msg)
    {
        Future future = new Future();
        Get get1 = new Get(msg, future);
        scheduler.insert(get1);
        return future;
    }

    private class Proxy
    {

    }

    private class Scheduler
    {

        private final ActivationList list;

        private Thread t;

        public Scheduler(ActivationList list)
        {
            this.list = list;
        }

        public void init()
        {
            t = new Thread()
            {
                @Override
                public void run()
                {
                    while (true)
                    {
                        Get get = list.get();
                    }
                }
            };
            t.start();
        }

        public void insert(Get get)
        {
            list.put(get);
        }

        public void stop()
        {
            t.stop();
        }

    }

    private class ActivationList
    {
        private LinkedBlockingQueue<Get> queue;

        public ActivationList()
        {
            queue = new LinkedBlockingQueue<Get>();
        }

        public void put(Get get)
        {
            try
            {
                queue.put(get);
            }
            catch (Exception ex)
            {

            }
        }

        public Get get()
        {
            try
            {
                return queue.take();
            }
            catch (Exception ex)
            {

            }
            return null;
        }
    }

    private class Get
    {

        private String msg;

        private Future future;

        public Get(String msg, Future future)
        {
            this.msg = msg;
            this.future = future;
        }

    }

    private class Servant
    {

        public String get()
        {
            return "msg";
        }

    }

}
