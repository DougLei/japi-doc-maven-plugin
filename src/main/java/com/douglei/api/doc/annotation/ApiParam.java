package com.douglei.api.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.douglei.api.doc.types.ParamStructType;

/**
 * api参数
 * @author DougLei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiParam {
	
	/**
	 * 参数的结构类型
	 * @return
	 */
	ParamStructType struct() default ParamStructType.OBJECT;
	
	/**
	 * 具体的参数数组
	 * @return
	 */
	ApiParam_[] params() default {};
}
