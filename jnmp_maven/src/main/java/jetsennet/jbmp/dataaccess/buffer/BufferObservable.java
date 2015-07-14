package jetsennet.jbmp.dataaccess.buffer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;

import jetsennet.jbmp.dataaccess.DefaultDal;
import jetsennet.jbmp.dataaccess.base.TableInfo;

/**
 * 缓存模块
 * @author xuyuji
 * @param <T> T是想要缓存的表的pojo，例如：缓存表BMP_OBJECT就用MObjectEntity
 */
public class BufferObservable<T> extends Observable
{
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(BufferObservable.class);
    /**
     * 缓存
     */
    private Map<Integer, T> buffer;
    /**
     * DAO类
     */
    private DefaultDal<T> dal;
    /**
     * 表信息
     */
    private TableInfo tableInfo;
    /**
     * 表ID Getter方法
     */
    private Method getId;
    /**
     * 方法返回值是ID
     */
    private static final int YES = 1;
    /**
     * 方法返回值不是ID
     */
    private static final int NO = 0;
    /**
     * 插入方法
     */
    private static final int INSERT = 1;
    /**
     * 删除方法
     */
    private static final int DELETE = 2;
    /**
     * 修改方法
     */
    private static final int UPDATE = 3;
    /**
     * 错误
     */
    private static final int ERROR = 0;

    /**
     * 构造函数
     * @param cl 泛型
     */
    public BufferObservable(Class<T> cl)
    {
        this.buffer = new HashMap<Integer, T>();
        this.dal = new DefaultDal<T>(cl);
        this.tableInfo = new TableInfo(cl);
        logger.info(tableInfo.tableName + "缓存模块启动。");
        try
        {
            String idName = tableInfo.keyColumn.getName();
            this.getId = cl.getMethod("get" + idName.substring(0, 1).toUpperCase() + idName.substring(1));
            loadBuffer();
        }
        catch (Exception e)
        {
            logger.error(tableInfo.tableName + "缓存模块启动失败。", e);
        }
    }

    /**
     * 取表名
     * @return 表名
     */
    public String getTableName()
    {
        return tableInfo.tableName;
    }

    /**
     * 从表中取出数据填充入缓存
     */
    private void loadBuffer()
    {
        List<T> entitys;
        try
        {
            entitys = dal.getAll();
            for (T entity : entitys)
            {
                buffer.put((Integer) getId.invoke(entity), entity);
            }
        }
        catch (Exception e)
        {
            logger.error("", e);
        }
    }

    /**
     * 取缓存
     * @return 结果
     */
    public Map<Integer, T> getBuffer()
    {
        return buffer;
    }

    /**
     * 刷新缓存，并通报结果。
     * @param map 参数
     */
    public void refreshBuffer(Map<String, Integer> map)
    {
        // 如果方法返回值是id则只查询该条数据，如果返回值不是id则整表重新载入。
        if (NO == map.get("isid"))
        {
            logger.debug(tableInfo.tableName + " 缓存重新加载。 ");
            buffer.clear();
            loadBuffer();
            this.setChanged();
        }
        else
        {
            int id = map.get("id");
            try
            {
                T entity = dal.get(id);
                if (INSERT == map.get("type"))
                {
                    logger.info(tableInfo.tableName + " 新增 " + entity);
                    addBuffer(id, entity);
                    this.setChanged();
                }
                else if (DELETE == map.get("type"))
                {
                    logger.info(tableInfo.tableName + " 删除 " + entity);
                    delBuffer(id, entity);
                    this.setChanged();
                }
                else if (UPDATE == map.get("type"))
                {
                    logger.info(tableInfo.tableName + " 修改 " + entity);
                    updateBuffer(id, entity);
                    this.setChanged();
                }
            }
            catch (NumberFormatException e)
            {
                logger.error("", e);
            }
            catch (Exception e)
            {
                logger.error("", e);
            }
        }
        this.notifyObservers(buffer);
    }

    /**
     * 新增缓存数据
     * @param id
     * @param entity
     */
    private void addBuffer(int id, T entity)
    {
        buffer.put(id, entity);
    }

    /**
     * 删除缓存数据
     * @param id
     * @param entity
     */
    private void delBuffer(int id, T entity)
    {
        buffer.remove(id);
    }

    /**
     * 修改缓存数据
     * @param id
     * @param entity
     */
    private void updateBuffer(int id, T entity)
    {
        delBuffer(id, entity);
        addBuffer(id, entity);
    }
}
