package jetsennet.jbmp.dataaccess.base.annotation;

import java.lang.annotation.Retention;

/**
 * @author ？
 */
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD })
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Operation
{
    /**
     * @return
     */
    public String name();

    /**
     * @return
     */
    public String tablename();
}
