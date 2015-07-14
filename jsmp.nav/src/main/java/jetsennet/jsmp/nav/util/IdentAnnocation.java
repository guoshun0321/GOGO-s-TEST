package jetsennet.jsmp.nav.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段标识注解，用于执行一些类映射操作
 * 
 * @author 郭祥
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IdentAnnocation
{

    public String value();

    public String def() default "";

    public String type() default "";

    public String enumValue() default "";

}
