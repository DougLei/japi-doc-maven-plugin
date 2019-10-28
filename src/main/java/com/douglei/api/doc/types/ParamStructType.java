package com.douglei.api.doc.types;

/**
 * 参数的结构类型
 * @author DougLei
 */
public enum ParamStructType {
	
	/**
	 * 标识没有配置, 例如不需要请求头, 不需要请求体
	 */
	NULL,
	
	/**
	 * 标识使用通用配置, 例如通用请求头, 通用请求体
	 */
	COMMON,
	
	
	/**
	 * 标识请求的数据为对象, 例如{name:"douglei"}
	 */
	OBJECT,
	
	/**
	 * 标识请求的数据为数组, 例如[{name:"douglei"}]
	 */
	ARRAY,
	
	/**
	 * 标识请求的数据为对象或数组
	 */
	OBJECT_OR_ARRAY;
}
