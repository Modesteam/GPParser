package annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HasOne {

	Class<?> entity();

	String reference();
	
	boolean belongs();

}
