package com.douglei.api.doc.metadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.douglei.api.doc.annotation.ApiCatalog;
import com.douglei.tools.utils.StringUtil;

/**
 * {@link ApiCatalog} 注解的元数据
 * @author DougLei
 */
public class ApiCatalogMetadata {
	private ApiCatalogMetadata() {}
	
	private Class<?> apiCatalogClass;
	private String value;// 目录的url
	private String name;// 目录名
	private byte priority;
	
	private List<ApiMetadata> apis;// api方法的配置元数据
	private short index;// api方法的下标
	
	public static final ApiCatalogMetadata newInstance(Class<?> apiCatalogClass) {
		ApiCatalog apiCatalog = apiCatalogClass.getAnnotation(ApiCatalog.class);
		if(apiCatalog != null) {
			ApiCatalogMetadata acm = new ApiCatalogMetadata();
			acm.apiCatalogClass = apiCatalogClass;
			acm.value = Utils.getUrl(apiCatalog.value(), apiCatalogClass.getAnnotation(RequestMapping.class));
			acm.name = StringUtil.isEmpty(apiCatalog.name())?apiCatalogClass.getSimpleName():apiCatalog.name();
			acm.priority = apiCatalog.priority();
			return acm;
		}
		return null;
	}
	
	// 初始化 {@code ApiCatalogMetadata#apis} 集合
	private void initialApiMetadatas() {
		if(apis == null) {
			Class<?> apiCatalogClass_ = apiCatalogClass;
			Method[] apiMethods;
			ApiMetadata apiMetadata;
			do {
				apiMethods = apiCatalogClass_.getDeclaredMethods();
				for (Method apiMethod : apiMethods) {
					apiMetadata = ApiMetadata.newInstance(apiMethod);
					if(apiMetadata != null) {
						if(apis == null) {
							apis = new ArrayList<ApiMetadata>(apiMethods.length);
						}
						apis.add(apiMetadata);
					}
				}
				apiCatalogClass_ = apiCatalogClass_.getSuperclass();
			}while(apiCatalogClass_ != Object.class);
			
			if(apis == null) {
				apis = Collections.emptyList();
			}
		}
	}
	
	/**
	 * 是否存在 {@link ApiMetadata}
	 * @return
	 */
	public boolean existsApi() {
		initialApiMetadatas();
		return apis.size() > 0;
	}
	
	/**
	 * 下包含具体的api数量
	 * @return
	 */
	public short apiCount() {
		return (short) apis.size();
	}
	
	/**
	 * 是否还存在 {@link ApiMetadata}
	 * @return
	 */
	public boolean hasNextApi() {
		return index < apis.size();
	}

	/**
	 * 获取下一个 {@link ApiMetadata}
	 * @return
	 */
	public ApiMetadata nextApi() {
		return apis.get(index++).insertUrlHead(value);
	}
	
	public String getName() {
		return name;
	}
	public String getClassName() {
		return apiCatalogClass.getName();
	}
	public byte getPriority() {
		return priority;
	}
	
	/**
	 * 销毁
	 */
	public void destroy() {
		if(apis.size() > 0) {
			apis.clear();
		}
		apis = null;
	}
}
