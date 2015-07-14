package jetsennet.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jetsennet.orm.executor.keygen.KeyGenEnum;

/**
 * @author lianghongjie
 */
@Target(value = { ElementType.METHOD, ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Id
{

    public String keyGen() default "";

    public KeyGenEnum keyEnum() default KeyGenEnum.DB;

}
