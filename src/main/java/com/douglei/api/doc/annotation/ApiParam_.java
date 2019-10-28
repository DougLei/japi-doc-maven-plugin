package com.douglei.api.doc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.douglei.api.doc.types.DataType;
import com.douglei.api.doc.types.ParamStructType;

/**
 * api参数
 * @author DougLei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiParam_ {
	
	/**
	 * 参数名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 数据类型
	 * @return
	 */
	DataType dataType() default DataType.STRING;
	
	/**
	 * 日期格式化
	 * @return
	 */
	String dateFormatPattern() default "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 参数长度, -1表示没有限制
	 * @return
	 */
	short length() default -1;
	
	/**
	 * 参数精度, -1表示没有限制
	 * @return
	 */
	short precision() default -1;
	
	/**
	 * 是否必须
	 */
	boolean required() default false;
	
	/**
	 * 参数的默认值
	 */
	String defaultValue() default "-";
	
	/**
	 * 参数描述
	 * @return
	 */
	String description() default "-";
	
	/**
	 * 示例值
	 * @return
	 */
	String egValue() default "";

	/**
	 * 对应的参数实体class
	 * @return
	 */
	Class<?> entity() default Object.class;

	/**
	 * 对应参数的实体class结构
	 * @return
	 */
	ParamStructType entityStruct() default ParamStructType.OBJECT;
}
