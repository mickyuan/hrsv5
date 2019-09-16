package hrds.commons.apiannotation;

import fd.ng.core.utils.StringUtil;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface DocMethod {
	/**
	 * 类描述
	 * @return
	 */
	String description();
	/**
	 * 是否为rest api
	 * @return
	 */
	boolean isApi() default false;

	/**
	 * API默认使用的请求方式
	 * @return
	 */
	String httpMethod() default "POST";

	/**
	 * 版本，预留，先放到这里
	 * @return
	 */
	String version() default "v5";
}
