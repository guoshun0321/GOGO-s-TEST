package jetsennet.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标识方法自动处理数据库事务
 * 
 * @author 郭祥
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Transactional
{
}
