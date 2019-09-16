package hrds.commons.apiannotation;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Params {
	Param[] value();
}
