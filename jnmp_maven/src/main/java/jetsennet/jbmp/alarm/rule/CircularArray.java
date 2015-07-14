/************************************************************************
日 期：2011-12-12
作 者: 郭祥
版 本：v1.3
描 述: 大小固定的循环数组
历 史：
 ************************************************************************/
package jetsennet.jbmp.alarm.rule;

import jetsennet.jbmp.util.ArrayUtils;

/**
 * 大小固定的循环数组
 * @author 郭祥
 * @param <T>
 */
public class CircularArray<T>
{

    /**
     * 头指针，指向第一个元素
     */
    private int head;
    /**
     * 尾指针，指向最后一个元素的下一个位置
     */
    private int tail;
    /**
     * 可用容量
     */
    private int size;
    /**
     * 当前容量
     */
    private int length;
    /**
     * 遍历的指针
     */
    private int epos;
    /**
     * 数据存储
     */
    private T[] datas;

    /**
     * 构造方法
     * @param clz 参数
     * @param size 参数
     */
    public CircularArray(Class<? extends T> clz, int size)
    {
        if (size < 1)
        {
            throw new IllegalArgumentException();
        }
        this.size = size;
        head = 0;
        tail = 0;
        datas = ArrayUtils.newArrayByClass(clz, size + 1);
    }

    /**
     * 设置
     */
    public void resetPos()
    {
        this.epos = head;
    }

    /**
     * @return 结果
     */
    public T next()
    {
        if (length == 0)
        {
            return null;
        }
        if (epos != tail)
        {
            T retval = datas[epos];
            epos = this.next(epos);
            return retval;
        }
        return null;
    }

    /**
     * @return 结果
     */
    public boolean isFull()
    {
        if (length == size)
        {
            return true;
        }
        return false;
    }

    /**
     * 清除
     */
    public void clear()
    {
        for (int i = 0; i < datas.length; i++)
        {
            datas[i] = null;
        }
        head = 0;
        tail = 0;
        length = 0;
    }

    /**
     * 添加一个对象
     * @param t 泛型参数
     */
    public void add(T t)
    {
        datas[tail] = t;
        tail = next(tail);
        if (tail == head)
        {
            head = this.next(head);
        }
        if (length < size)
        {
            length++;
        }
    }

    /**
     * 获取最后一次添加的对象
     * @return 结果
     */
    public T getLast()
    {
        if (length <= 0)
        {
            return null;
        }
        else
        {
            return datas[pre(tail)];
        }
    }

    /**
     * @param pos 参数
     * @return 结果
     */
    public int pre(int pos)
    {
        pos--;
        return pos < 0 ? size : pos;
    }

    /**
     * @param pos 参数
     * @return 结果
     */
    public int next(int pos)
    {
        pos++;
        return pos > size ? 0 : pos;
    }

    public int getHead()
    {
        return head;
    }

    public void setHead(int head)
    {
        this.head = head;
    }

    public int getTail()
    {
        return tail;
    }

    public void setTail(int tail)
    {
        this.tail = tail;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public T[] getDatas()
    {
        return datas;
    }

    public void setDatas(T[] datas)
    {
        this.datas = datas;
    }
}
