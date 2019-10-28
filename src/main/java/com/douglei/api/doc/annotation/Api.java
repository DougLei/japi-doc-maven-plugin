package com.douglei.api.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMethod;

import com.douglei.api.doc.types.ParamStructType;

/**
 * api操作注解
 * @author DougLei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Api {
	
	/**
	 * api的url地址
	 * @return
	 */
	String value() default "";
	
	/**
	 * 操作名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 操作详细描述
	 * @return
	 */
	String description() default "";
	
	/**
	 * 请求方式
	 * @return
	 */
	RequestMethod requestMethod() default RequestMethod.POST;
	
	/**
	 * 请求header参数
	 * @return
	 */
	ApiParam header() default @ApiParam(struct=ParamStructType.COMMON);
	
	/**
	 * 请求url参数
	 * @return
	 */
	ApiParam url() default @ApiParam(struct=ParamStructType.NULL);
	
	/**
	 * 请求体参数
	 * @return
	 */
	ApiParam request() default @ApiParam(struct=ParamStructType.NULL);
	
	/**
	 * 响应体参数
	 * @return
	 */
	ApiParam response() default @ApiParam(struct=ParamStructType.COMMON);
}
