package com.douglei.api.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * api目录注解
 * @author DougLei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApiCatalog {
	
	/**
	 * 目录的url
	 * @return
	 */
	String value() default "";
	
	/**
	 * 目录名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 输出时的优先级, 越大越优先
	 */
	byte priority() default 0;
}
