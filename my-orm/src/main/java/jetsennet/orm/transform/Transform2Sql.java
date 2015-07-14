package jetsennet.orm.transform;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jetsennet.orm.configuration.Configuration;
import jetsennet.orm.sql.ConditionEntry;
import jetsennet.orm.sql.ConditionEntryDuple;
import jetsennet.orm.sql.ConditionEntryMulti;
import jetsennet.orm.sql.DeleteEntity;
import jetsennet.orm.sql.FilterNode;
import jetsennet.orm.sql.ISql;
import jetsennet.orm.sql.InsertEntity;
import jetsennet.orm.sql.RelationshipEnum;
import jetsennet.orm.sql.SelectEntity;
import jetsennet.orm.sql.SqlTypeEnum;
import jetsennet.orm.sql.UpdateEntity;
import jetsennet.orm.tableinfo.FieldInfo;
import jetsennet.orm.tableinfo.TableInfo;
import jetsennet.orm.util.SqlUtil;
import jetsennet.orm.util.UncheckedOrmException;
import jetsennet.util.SafeDateFormater;
import jetsennet.util.TwoTuple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SQL语句转换类。
 * 包括以下功能：
 * 1、ISql转SQL语句
 * 2、Map转SQL语句
 * 3、参数处理，特殊字符处理
 * 
 * @author 郭祥
 *
 */
public abstract class Transform2Sql extends AbsTransform2Sql
{

    /**
     * 配置
     */
    protected Configuration config;
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(Transform2Sql.class);

    public Transform2Sql(Configuration config)
    {
        this.config = config;
    }

    //TODO 这里以后可能会需要加入参数自动判断的机制
    @Override
    public final String trans(ISql sql)
    {
        String retval = null;
        switch (sql.getSqlType())
        {
        case SELECT:
            retval = transSelect((SelectEntity) sql, false);
            break;
        case INSERT:
            retval = transInsert((InsertEntity) sql);
            break;
        case UPDATE:
            retval = transUpdate((UpdateEntity) sql);
            break;
        case DELETE:
            retval = transDelete((DeleteEntity) sql);
            break;
        default:
            throw new IllegalArgumentException("sql类型未知：" + sql.getSqlType());
        }
        return retval;
    }

    public PageSqlEntity pageSelect(SelectEntity sql, int page, int pageSize)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 调整Map里面参数的顺序，使得符合对象中参数的排列顺序
     * 
     * @param tableInfo
     * @param list
     * @return String, SQL语句；Object[][]，参数列表
     */
    @Override
    public List<Map<String, Object>> prepareInsertMap(TableInfo tableInfo, List<Map<String, Object>> list)
    {
        List<Map<String, Object>> retval = new ArrayList<Map<String, Object>>(list.size());

        List<FieldInfo> fields = tableInfo.getFieldInfos();
        for (Map<String, Object> map : list)
        {
            Map<String, Object> tempMap = new LinkedHashMap<String, Object>(fields.size());
            for (FieldInfo field : fields)
            {
                String fieldName = field.getName();
                tempMap.put(fieldName, map.get(fieldName));
            }
            retval.add(tempMap);
        }
        return retval;
    }

    /**
     * @see ITransform2Sql
     */
    @Override
    public List<Map<String, Object>> prepareInsertObj(TableInfo tableInfo, List<Object> list)
    {
        List<Map<String, Object>> retval = new ArrayList<Map<String, Object>>(list.size());

        List<FieldInfo> fields = tableInfo.getFieldInfos();
        for (Object obj : list)
        {
            Map<String, Object> tempMap = new LinkedHashMap<String, Object>(fields.size());
            for (FieldInfo field : fields)
            {
                tempMap.put(field.getName(), field.get(obj));
            }
            retval.add(tempMap);
        }
        return retval;
    }

    /**
     * 将json转换成sql语句
     * 
     * @param tableInfo
     * @param json
     * @param type
     * @return
     */
    public final List<String> transJson(TableInfo tableInfo, String json, SqlTypeEnum type)
    {
        List<Map<String, Object>> list = SimpleJsonParse.parse(json);
        List<String> retval = new ArrayList<String>(list.size());
        for (Map<String, Object> map : list)
        {
            retval.add(this.trans(tableInfo.map2Sql(map, type)));
        }
        return retval;
    }

    /**
     * 将xml转换成sql语句
     * 
     * @param tableInfo
     * @param xml
     * @param type
     * @return
     */
    public final List<String> transXml(TableInfo tableInfo, String xml, SqlTypeEnum type)
    {
        List<Map<String, Object>> list = SimpleXmlParse.parse(xml);
        List<String> retval = new ArrayList<String>(list.size());
        for (Map<String, Object> map : list)
        {
            retval.add(this.trans(tableInfo.map2Sql(map, type)));
        }
        return retval;
    }

    /**
     * insert语句转换
     * 
     * @param insert
     * @return
     */
    protected final String transInsert(InsertEntity insert)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(insert.table);
        String[] keys = insert.getKeys();
        Object[] objs = insert.getValues();

        if (keys != null && keys.length > 0)
        {
            int keysLength = keys.length;
            sb.append("(");
            for (int i = 0; i < keysLength; i++)
            {
                if (objs[i] != null)
                {
                    sb.append(keys[i]).append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        sb.append(" VALUES(");
        for (Object obj : objs)
        {
            if (obj != null)
            {
                sb.append(param(obj)).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    /**
     * delete语句转换
     * 
     * @param delete
     * @return
     */
    protected final String transDelete(DeleteEntity delete)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ").append(delete.table);
        if (delete.getFilter() != null)
        {
            sb.append(" WHERE ");
            condition(delete.getFilter(), sb);
        }
        return sb.toString();
    }

    /**
     * update语句转换
     * @param update
     * @return
     */
    protected final String transUpdate(UpdateEntity update)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(update.table).append(" SET ");
        for (int i = 0; i < update.getLength(); i++)
        {
            String key = update.getKeys()[i];
            Object value = update.getValues()[i];
            if (key != null && value != null)
            {
                sb.append(key).append("=").append(param(value)).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        if (update.getFilter() != null)
        {
            sb.append(" WHERE ");
            condition(update.getFilter(), sb);
        }
        return sb.toString();
    }

    /**
     * select语句转换
     * 
     * @param select
     * @return
     */
    protected final String transSelect(SelectEntity select, boolean isCount)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (select.isDistinct())
        {
            sb.append("DISTINCT ");
        }
        // 结果集声明
        if (!isCount)
        {
            if (select.getColumn() == null || select.getColumn().isEmpty())
            {
                sb.append("*");
            }
            else
            {
                sb.append(select.getColumn());
            }
        }
        else
        {
            sb.append("COUNT(*)");
        }
        sb.append(" ");
        // 表名声明
        if (select.getTable() != null)
        {
            sb.append("FROM ").append(select.getTable());
        }
        else if (select.getSubTable() != null)
        {
            sb.append("FROM (").append(transSelect(select.getSubTable(), false)).append(") ").append(select.getSubTableAlias());
        }
        else
        {
            throw new IllegalArgumentException("select语句中找不到表名");
        }
        // 条件约束
        if (select.getFilter() != null)
        {
            sb.append(" WHERE ");
            condition(select.getFilter(), sb);
        }
        // GROUP 相关约束
        if (select.getGroup() != null && !select.getGroup().isEmpty())
        {
            sb.append(" GROUP BY ").append(select.getGroup());
        }
        if (select.getHaving() != null && !select.getHaving().isEmpty())
        {
            sb.append(" HAVING ").append(select.getHaving());
        }
        // 连接其他表
        if (select.getUnion() != null)
        {
            sb.append(" UNION ").append(transSelect(select.getUnion(), false));
        }
        if (select.getUnionAll() != null)
        {
            sb.append(" UNION ALL ").append(transSelect(select.getUnionAll(), false));
        }
        // 排序
        if (!isCount && select.getOrder() != null && !select.getOrder().isEmpty())
        {
            sb.append(" ORDER BY ").append(select.getOrder());
        }
        return sb.toString();
    }

    protected final StringBuilder condition(FilterNode node, StringBuilder sb)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        if (node.type == FilterNode.TYPE_REL_AND || node.type == FilterNode.TYPE_REL_OR)
        {
            Stack<FilterNode> stack = new Stack<FilterNode>();
            stack.push(node);
            FilterNode[] children = node.getChildren();
            int length = children.length;
            sb.append("(");
            for (int i = 0; i < length; i++)
            {
                FilterNode child = children[i];
                if (child.type == FilterNode.TYPE_REL_AND || child.type == FilterNode.TYPE_REL_OR)
                {
                    condition(child, sb);
                }
                else
                {
                    ConditionEntry cond = child.getCond();
                    parseCondition(cond, sb);
                }
                if (i != (length - 1))
                {
                    if (node.type == FilterNode.TYPE_REL_AND)
                    {
                        sb.append(" AND ");
                    }
                    else
                    {
                        sb.append(" OR ");
                    }
                }
            }
            sb.append(")");
        }
        else
        {
            parseCondition(node.getCond(), sb);
        }
        return sb;
    }

    protected final String parseCondition(ConditionEntry cond, StringBuilder sb)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        RelationshipEnum relType = cond.rel;
        switch (relType)
        {
        case Equal:
            sb.append(cond.key).append(" = ").append(param(cond.value));
            break;
        case NotEqual:
            sb.append(cond.key).append(" <> ").append(param(cond.value));
            break;
        case IsNull:
            sb.append(cond.key).append(" IS NULL");
            break;
        case IsNotNull:
            sb.append(cond.key).append(" IS NOT NULL");
            break;
        case Than:
            sb.append(cond.key).append(" > ").append(param(cond.value));
            break;
        case Less:
            sb.append(cond.key).append(" < ").append(param(cond.value));
            break;
        case ThanEqual:
            sb.append(cond.key).append(" >= ").append(param(cond.value));
            break;
        case LessEqual:
            sb.append(cond.key).append(" <= ").append(param(cond.value));
            break;
        case Like:
        case NotLike:
        case ILike:
            handleLikeCond(cond, sb);
            break;
        case In:
        case NotIn:
            handleInCond(cond, sb);
            break;
        case Between:
            handleBetweenCond(cond, sb);
            break;
        case Exists:
            sb.append("EXISTS (").append(cond.value).append(")");
            break;
        case NotExists:
            sb.append("NOT EXISTS (").append(cond.value).append(")");
            break;
        default:
            throw new IllegalArgumentException("不支持条件：" + relType);
        }
        return sb.toString();
    }

    /**
     * 处理函数
     */
    protected String getSqlFunction(SqlFunctionEnum sqlEnum)
    {
        switch (sqlEnum)
        {
        case ToUpper:
            return "UPPER";
        case ToLower:
            return "LOWER";
        default:
            throw new UncheckedOrmException("暂时不支持函数：" + sqlEnum.name());
        }
    }

    /**
     * 参数处理
     * 数字类型，调用toString()
     * 时间类型，调用formatDateTimeString格式化日期字符串
     * String类型，非like，前后加单引号
     * String类型，like，返回原始值
     * 
     * @param obj
     * @return
     */
    protected String param(Object obj)
    {
        if (obj == null)
        {
            throw new NullPointerException();
        }
        String retval = null;
        if (obj instanceof Short || obj instanceof Integer || obj instanceof Long || obj instanceof Double)
        {
            retval = obj.toString();
        }
        else if (obj instanceof String)
        {
            retval = "'" + paramString((String) obj) + "'";
        }
        else if (obj instanceof Date)
        {
            retval = formatDateTimeString(SafeDateFormater.format((Date) obj));
        }
        else
        {
            throw new UncheckedOrmException("不支持类型：" + obj.getClass());
        }
        return retval;
    }

    protected String paramString(String str)
    {
        throw new UnsupportedOperationException();
    }

    protected TwoTuple<String, Boolean> paramStringLike(String str)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * 格式化时间字符串。
     * 目前格式通用于SqlServer和MySql
     * 
     * @param s
     * @return
     */
    protected String formatDateTimeString(String s)
    {
        return "'" + s + "'";
    }

    /**
     * 处理Like类型的条件，对需要转义的字符进行转义
     * 
     * @param relType
     * @param key
     * @param obj
     * @return
     */
    protected String handleLikeCond(ConditionEntry cond, StringBuilder sb)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        String key = cond.key;
        Object obj = cond.value;
        TwoTuple<String, Boolean> specialTemp = this.paramStringLike(obj.toString());
        boolean isSpec = specialTemp.second;
        String value = specialTemp.first;
        switch (cond.rel)
        {
        case Like:
            sb.append(key).append(" LIKE '%").append(value).append("%'");
            break;
        case NotLike:
            sb.append(key).append(" NOT LIKE '%").append(value).append("%'");
            break;
        case ILike:
            sb.append(this.getSqlFunction(SqlFunctionEnum.ToUpper))
                .append("(")
                .append(key)
                .append(")")
                .append(" LIKE ")
                .append(this.getSqlFunction(SqlFunctionEnum.ToUpper))
                .append("('%")
                .append(value)
                .append("%')");
            break;
        default:
            throw new UncheckedOrmException();
        }
        if (isSpec)
        {
            sb.append(" ESCAPE '/'");
        }
        return sb.toString();
    }

    /**
     * 处理Like类型的条件，对需要转义的字符进行转义
     * 
     * @param relType
     * @param key
     * @param obj
     * @return
     */
    protected String handleInCond(ConditionEntry cond, StringBuilder sb)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        if (cond instanceof ConditionEntryMulti)
        {
            ConditionEntryMulti condM = (ConditionEntryMulti) cond;
            Object[] values = condM.values;
            if (values == null || values.length <= 0)
            {
                throw new IllegalArgumentException();
            }
            sb.append(condM.key);
            if (condM.rel == RelationshipEnum.In)
            {
                sb.append(" IN (");
            }
            else
            {
                sb.append(" NOT IN (");
            }
            for (Object value : values)
            {
                sb.append(param(value)).append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.append(")");
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return sb.toString();
    }

    /**
     * 处理Like类型的条件，对需要转义的字符进行转义
     * 
     * @param relType
     * @param key
     * @param obj
     * @return
     */
    protected String handleBetweenCond(ConditionEntry cond, StringBuilder sb)
    {
        if (sb == null)
        {
            sb = new StringBuilder();
        }
        if (cond instanceof ConditionEntryDuple)
        {
            ConditionEntryDuple condD = (ConditionEntryDuple) cond;
            sb.append(cond.key).append(" BETWEEN (").append(param(condD.first)).append(" AND ").append(param(condD.second)).append(")");
        }
        else
        {
            throw new IllegalArgumentException();
        }
        return sb.toString();
    }

    protected String getDefaultSequenceName(String tablename)
    {
        return "SEQ_" + tablename;
    }

    @Override
    public boolean supportsOffset()
    {
        return true;
    }

    @Override
    public String getSequenceNextValString(String sequenceName)
    {
        throw new UnsupportedOperationException(getClass().getName() + " does not support Sequence");
    }

    @Override
    public String getCountSql(String query)
    {
        String rsel = TransformUtil.removeSelect(query);
        rsel = SqlUtil.shallowRemoveOrderBy(rsel, 0);
        if (rsel == null)
        {
            return "SELECT COUNT(0) FROM (" + query + ") AS _TCOUNT";
        }
        else
        {
            return "SELECT COUNT(0) " + rsel;
        }
    }

}
