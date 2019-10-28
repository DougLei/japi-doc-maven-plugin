package com.douglei.api.doc;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author DougLei
 */
public class ApiDocBuilderContext {
	private static final ThreadLocal<CommonParamConfiguration> commonParamConfiguration = new ThreadLocal<CommonParamConfiguration>();
	private static final Map<String, SimpleDateFormat> dateFormatMap = new HashMap<String, SimpleDateFormat>(4);
	
	private static CommonParamConfiguration getCommonParamConfiguration() {
		CommonParamConfiguration conf = commonParamConfiguration.get();
		if(conf == null) {
			conf = new CommonParamConfiguration();
			commonParamConfiguration.set(conf);
		}
		return conf;
	}
	static void setClassLoader(ClassLoader classloader) {
		getCommonParamConfiguration().classloader = classloader;
	}
	static void setExistsCommonHeaderParam() {
		getCommonParamConfiguration().existsCommonHeaderParam = true;
	}
	static void setExistsCommonUrlParam() {
		getCommonParamConfiguration().existsCommonUrlParam = true;
	}
	static void setExistsCommonRequestParam() {
		getCommonParamConfiguration().existsCommonRequestParam = true;
	}
	static void setExistsCommonResponseParam() {
		getCommonParamConfiguration().existsCommonResponseParam = true;
	}
	
	public static ClassLoader getClassLoader() {
		ClassLoader classloader = getCommonParamConfiguration().classloader;
		if(classloader == null) {
			setClassLoader(ApiDocBuilderContext.class.getClassLoader());
		}
		return classloader;
	}
	public static boolean existsCommonHeaderParam() {
		return getCommonParamConfiguration().existsCommonHeaderParam;
	}
	public static boolean existsCommonUrlParam() {
		return getCommonParamConfiguration().existsCommonUrlParam;
	}
	public static boolean existsCommonRequestParam() {
		return getCommonParamConfiguration().existsCommonRequestParam;
	}
	public static boolean existsCommonResponseParam() {
		return getCommonParamConfiguration().existsCommonResponseParam;
	}
	
	public static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat sdf = dateFormatMap.get(pattern);
		if(sdf == null) {
			sdf = new SimpleDateFormat(pattern);
			dateFormatMap.put(pattern, sdf);
		}
		return sdf;
	}
}

// 通用参数配置
class CommonParamConfiguration {
	ClassLoader classloader;
	boolean existsCommonHeaderParam;
	boolean existsCommonUrlParam;
	boolean existsCommonRequestParam;
	boolean existsCommonResponseParam;
}
