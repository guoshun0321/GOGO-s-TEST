/**********************************************************************
 * 日 期： 2014-5-6
 * 作 者:  梁洪杰
 * 版 本： v2.1
 * 描 述:  IDao.java
 * 历 史： 2014-5-6 创建
 *********************************************************************/
package jetsennet.frame.dataaccess;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import jetsennet.sqlclient.ISqlParser;
import jetsennet.sqlclient.SqlCondition;
import jetsennet.sqlclient.SqlField;

import org.uorm.dao.common.ICommonDaoXmlExt;
import org.uorm.dao.common.SqlParameter;

/**
 * 数据库操作类，目前兼容uorm接口，集成监控defaultdal接口
 * TODO 移植新orm接口
 */
public interface IDao extends ICommonDaoXmlExt
{
    /**
     * @return sql解析器
     */
    ISqlParser getSqlParser();

    /**
     * 查单个对象
     * @param <X> 
     * @param c 类型
     * @param conds 条件
     * @return 单个对象结果
     * @throws Exception 异常 
     */
    <X> X get(Class<X> c, SqlCondition... conds) throws Exception;

    /**
     * 查单个对象
     * @param <X>
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return 单个对象结果
     * @throws Exception 异常
     */
    <X> X get(Class<X> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查单个map
     * @param <X>
     * @param c 类型
     * @param conds 条件
     * @return 单个map结果
     * @throws Exception 异常
     */
    Map<String, Object> getMap(Class<?> c, SqlCondition... conds) throws Exception;

    /**
     * 查单个map
     * @param <X>
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return 单个map结果
     * @throws Exception 异常
     */
    Map<String, Object> getMap(Class<?> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查单个map
     * @param <X>
     * @param c 类型
     * @param fields 字段列表
     * @param order 排序
     * @param conds 条件
     * @return 单个map结果
     * @throws Exception 异常
     */
    Map<String, Object> getMap(Class<?> c, String fields, String order, SqlCondition... conds) throws Exception;

    /**
     * 查单个string map
     * @param <X>
     * @param c 类型
     * @param conds 条件
     * @return 单个string map结果
     * @throws Exception 异常
     */
    Map<String, String> getStrMap(Class<?> c, SqlCondition... conds) throws Exception;

    /**
     * 查单个string map
     * @param <X>
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return 单个string map结果
     * @throws Exception 异常
     */
    Map<String, String> getStrMap(Class<?> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查单个string map
     * @param <X>
     * @param c 类型
     * @param fields 字段列表
     * @param order 排序
     * @param conds 条件
     * @return 单个string map结果
     * @throws Exception 异常
     */
    Map<String, String> getStrMap(Class<?> c, String fields, String order, SqlCondition... conds) throws Exception;

    /**
     * 查单个string map
     * @param sql sql语句
     * @param params 参数，顺序很重要，和@param sql中参数?的顺序对应;name:参数名，为ORM对应class变量名或表字段名
     * @return 单个string map结果
     * @throws Exception
     */
    Map<String, String> getStrMap(String sql, SqlParameter... params) throws SQLException;

    /**
     * 查单个字段
     * @param retCla 返回class类型
     * @param c 类型
     * @param field 字段
     * @param conds 条件
     * @return 字段值
     * @throws Exception
     */
    <X> X getFirst(Class<X> retCla, Class<?> c, String field, SqlCondition... conds) throws Exception;

    /**
     * 查单个字段
     * @param retCla 返回class类型
     * @param c 类型
     * @param field 字段
     * @param order 排序
     * @param conds 条件
     * @return 字段值
     * @throws Exception
     */
    <X> X getFirst(Class<X> retCla, Class<?> c, String field, String order, SqlCondition... conds) throws Exception;

    /**
     * 查对象列表
     * @param c 类型
     * @param conds 条件
     * @return 对象列表
     * @throws Exception
     */
    <X> List<X> getLst(Class<X> c, SqlCondition... conds) throws Exception;

    /**
     * 查对象列表
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return 对象列表
     * @throws Exception
     */
    <X> List<X> getLst(Class<X> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查map列表
     * @param c 类型
     * @param conds 条件
     * @return map列表
     * @throws Exception
     */
    <X> List<Map<String, Object>> getMapLst(Class<X> c, SqlCondition... conds) throws Exception;

    /**
     * 查map列表
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return map列表
     * @throws Exception
     */
    <X> List<Map<String, Object>> getMapLst(Class<X> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查指定字段的map列表
     * @param c 类型
     * @param fields 字段列表
     * @param order 排序
     * @param conds 条件
     * @return map列表
     * @throws Exception
     */
    <X> List<Map<String, Object>> getMapLst(Class<X> c, String fields, String order, SqlCondition... conds) throws Exception;

    /**
     * 查string map列表
     * @param c 类型
     * @param conds 条件
     * @return string map列表
     * @throws Exception
     */
    <X> List<Map<String, String>> getStrMapLst(Class<X> c, SqlCondition... conds) throws Exception;

    /**
     * 查string map列表
     * @param c 类型
     * @param order 排序
     * @param conds 条件
     * @return string map列表
     * @throws Exception
     */
    <X> List<Map<String, String>> getStrMapLst(Class<X> c, String order, SqlCondition... conds) throws Exception;

    /**
     * 查string map列表
     * @param c 类型
     * @param fields 字段列表
     * @param order 排序
     * @param conds 条件
     * @return string map列表
     * @throws Exception
     */
    <X> List<Map<String, String>> getStrMapLst(Class<X> c, String fields, String order, SqlCondition... conds) throws Exception;

    /**
     * 查string map列表
     * @param sql sql语句
     * @param params 参数列表
     * @return string map列表
     * @throws SQLException
     */
    <X> List<Map<String, String>> getStrMapLst(String sql, SqlParameter... params) throws SQLException;

    /**
     * 查某字段列表
     * @param retCla 返回类型
     * @param c 类型
     * @param field 字段
     * @param conds 条件
     * @return 某字段列表
     * @throws Exception
     */
    <X> List<X> getFirstLst(Class<X> retCla, Class<?> c, String field, SqlCondition... conds) throws Exception;

    /**
     * 查某字段列表
     * @param retCla 返回类型
     * @param c 类型
     * @param field 字段
     * @param order 排序
     * @param conds 条件
     * @return 某字段列表
     * @throws Exception
     */
    <X> List<X> getFirstLst(Class<X> retCla, Class<?> c, String field, String order, SqlCondition... conds) throws Exception;

    /**
     * 检验是否存在记录
     * @param c 类型
     * @param conds 条件
     * @return true：存在；false：不存在
     * @throws Exception
     */
    boolean isExist(Class<?> c, SqlCondition... conds) throws Exception;

    /**
     * 递归获取所有子id
     * @param retCla 返回类型
     * @param c 类型
     * @param parentVals 父id
     * @param parentFld 父字段
     * @param childFld 子字段
     * @return 子id列表
     * @throws Exception
     */
    <X> List<X> getAllChildIdsByParent(Class<X> retCla, Class<?> c, String parentVals, String parentFld, String childFld) throws Exception;

    /**
     * 检验是否构成循环父子关系
     * @param c 类型
     * @param parentVal 父id
     * @param childVals 子id列表
     * @param parentFld 父字段
     * @param childFld 子字段
     * @return true：循环；false：无循环
     * @throws Exception
     */
    boolean isCircle(Class<?> c, int parentVal, String childVals, String parentFld, String childFld) throws Exception;

    /**
     * 更新
     * @param c 类型
     * @param field 更新字段
     * @param conds 条件
     * @result 影响结果条数
     * @throws Exception
     */
    int update(Class<?> c, SqlField field, SqlCondition... conds) throws Exception;

    /**
     * 更新
     * @param c 类型
     * @param fields 更新字段列表
     * @param conds 条件
     * @result 影响结果条数
     * @throws Exception
     */
    int update(Class<?> c, List<SqlField> fields, SqlCondition... conds) throws Exception;

    /**
     * 删除
     * @param c 类型
     * @param conds 条件
     * @result 影响结果条数
     * @throws Exception
     */
    int delete(Class<?> c, SqlCondition... conds) throws Exception;
}
