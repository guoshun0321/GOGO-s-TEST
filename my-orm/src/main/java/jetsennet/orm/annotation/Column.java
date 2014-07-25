package jetsennet.orm.annotation;

/**
 * @author lianghongjie
 */
@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Column
{

    public String value() default "";

    public String desc() default "";

    public boolean isText() default false;
}
