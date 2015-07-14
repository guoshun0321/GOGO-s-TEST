/**
 * 
 */
package jetsennet.orm.annotation;

import java.lang.annotation.Retention;

/**
 * @author lianghongjie
 */
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Table
{
    /**
     * @return
     */
    public String value() default "";
}
