/************************************************************************
日 期: 2012-3-13
作 者: 郭祥
版 本: v1.3
描 述: 数据变化时的观察者接口
历 史:
 ************************************************************************/
package jetsennet.jbmp.dataaccess.base;

/**
 * 数据变化时的观察者接口
 * @author 郭祥
 * @param <T> 参数
 */
public interface IDataChangeObserver<T>
{

    /**
     * @param obj 参数
     * @param opNum 参数
     */
    public void change(T obj, int opNum);
}
