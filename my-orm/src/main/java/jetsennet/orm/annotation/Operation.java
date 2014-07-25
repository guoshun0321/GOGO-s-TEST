package jetsennet.orm.annotation;

import java.lang.annotation.Retention;

/**
 * @author ？
 */
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Operation
{
    /**
     * @return 操作名称
     */
    public String name() default "";
}
