/************************************************************************
日  期：		2013-09-04
作  者:		李小敏
版  本：      1.0
描  述:	    
历  史：      
************************************************************************/
package jetsennet.sqlclient;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldName
{
	String name();
}
