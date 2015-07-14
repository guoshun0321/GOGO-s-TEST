package jetsennet.jbmp.alarm.rule;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 * 按时间排序的队列
 * 元素按时间排序，当队尾数据的时间-队头数据的时间>timeSpan时，抛弃队头数据
 * 线程不安全
 * 
 * @author 郭祥
 */
public class TimeQueue<T>
{

    /**
     * 队列长度
     */
    private int size = 0;
    /**
     * 时间检查
     */
    private long timeSpan;
    /**
     * 队头哨兵
     */
    private TimeQueueEntry<T> BEGIN;
    /**
     * 队尾哨兵
     */
    private TimeQueueEntry<T> END;
    /**
     * 遍历时的，当前位置
     */
    private TimeQueueEntry<T> pos;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(TimeQueue.class);

    public TimeQueue(long timeSpan)
    {
        if (timeSpan <= 0)
        {
            throw new IllegalArgumentException("timeSpan小于等于0");
        }
        this.timeSpan = timeSpan;
        BEGIN = new TimeQueueEntry<T>(null, Long.MIN_VALUE);
        END = new TimeQueueEntry<T>(null, Long.MAX_VALUE);
        BEGIN.next = END;
        END.pre = BEGIN;
    }

    public void add(T obj, long time) throws Exception
    {
        if (time == Long.MAX_VALUE || time == Long.MIN_VALUE)
        {
            throw new Exception("传入时间有误：" + new Date(time));
        }

        // 插入数据
        TimeQueueEntry<T> insert = new TimeQueueEntry<T>(obj, time);
        TimeQueueEntry<T> last = END.pre;
        while (last != null)
        {
            if (time >= last.time)
            {
                TimeQueueEntry<T> lastNext = last.next;
                last.next = insert;
                insert.pre = last;
                insert.next = lastNext;
                lastNext.pre = insert;
                break;
            }
            last = last.pre;
        }
        size++;

        // 判断时间
        while (true)
        {
            TimeQueueEntry<T> begin = BEGIN.next;
            TimeQueueEntry<T> end = END.pre;
            long span = end.time - begin.time;
            if (span > this.timeSpan)
            {
                TimeQueueEntry<T> second = begin.next;
                second.pre = BEGIN;
                BEGIN.next = second;
                size--;
            }
            else
            {
                break;
            }
        }
    }

    public void clear()
    {
        BEGIN.next = END;
        END.pre = BEGIN;
        this.size = 0;
    }

    public T getLast()
    {
        T retval = null;
        TimeQueueEntry<T> last = END.pre;
        if (last != BEGIN)
        {
            retval = last.obj;
        }
        return retval;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        TimeQueueEntry<T> last = END.pre;
        sb.append("队列尾，队列大小：").append(size).append("\n");
        while (last != BEGIN)
        {
            sb.append(last.toString()).append("\n");
            last = last.pre;
        }
        sb.append("队列头");
        return sb.toString();
    }

    /**
     * 设置
     */
    public void resetPos()
    {
        this.pos = BEGIN;
    }

    /**
     * @return 结果
     */
    public T next()
    {
        T retval = null;
        if (this.size != 0)
        {
            pos = pos.next;
            if (pos != END)
            {
                retval = pos.obj;
            }
        }
        return retval;
    }

    /**
     * 双向链表
     */
    public static class TimeQueueEntry<T>
    {
        public T obj;

        public long time;

        public TimeQueueEntry<T> next;

        public TimeQueueEntry<T> pre;

        public TimeQueueEntry(T obj, long time)
        {
            this.obj = obj;
            this.time = time;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("时间：").append(time);
            sb.append("；数据：").append(obj);
            return sb.toString();
        }
    }

    public static void main(String[] args) throws Exception
    {
        TimeQueue<Integer> queue = new TimeQueue<Integer>(2);
        queue.add(1, 1);
        queue.add(2, 2);
        queue.add(4, 4);
        queue.add(3, 3);
        queue.add(5, 4);
        System.out.println(queue);
        queue.clear();
        queue.add(5, 4);
        queue.add(3, 3);
        queue.add(4, 4);
        queue.add(2, 2);
        queue.add(1, 1);
        System.out.println(queue);
    }

}
