package com.douglei.api.doc.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import com.douglei.api.doc.ApiDocBuilder;
import com.douglei.api.doc.ApiFolderBuilder;
import com.douglei.api.doc.ApiZipBuilder;
import com.douglei.api.doc.types.entity.DataTypeMatchEntity;

/**
 * 
 * @author DougLei
 */
@Mojo(name = "apiDocBuilder", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.COMPILE)
public class ApiDocBuilderMojo extends AbstractMojo {
	
	/**
	 * 生成api文档的类型, 值包括 [zip, folder], 不区分大小写, 默认值为zip
	 * zip标识生成压缩包, folder标识生成文件夹
	 */
	@Parameter
	private String apiDocType = "zip";
	
	/**
	 * 生成api文档的名称和标题, 默认名为 api文档
	 */
	@Parameter
	private String name;
	/**
	 * api版本, 默认为1.0
	 */
	@Parameter
	private String version;
	/**
	 * 生成api文档的绝对路径, 默认在当前项目的target目录下
	 */
	@Parameter
	private String path;
	
	/**
	 * api作者, 默认为当前计算机的名称
	 */
	@Parameter
	private String[] authors;
	
	/**
	 * api开发环境的url
	 */
	@Parameter
	private String[] devEnvironmentUrls;
	/**
	 * api测试环境的url
	 */
	@Parameter
	private String[] testEnvironmentUrls;
	/**
	 * api生产环境的url
	 */
	@Parameter
	private String[] prodEnvironmentUrls;
	
	/**
	 * 通用的请求头配置类
	 */
	@Parameter
	private CommonParam header;
	/**
	 * 通用的请求URL配置类
	 */
	@Parameter
	private CommonParam url;
	/**
	 * 通用的请求体配置类
	 */
	@Parameter
	private CommonParam request;
	/**
	 * 通用的响应体配置类
	 */
	@Parameter
	private CommonParam response;
	
	/**
	 * 要扫描的包路径, 默认为当前项目的根包名
	 */
	@Parameter
	private String[] scanPackages;
	
	/**
	 * 自定义的一些数据类型匹配实体
	 */
	@Parameter
	private DataTypeMatchEntity[] dataTypeMatchEntities;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ApiDocBuilder builder = getBuilder();
		if(name != null) {
			builder.setName(name);
		}
		if(version != null) {
			builder.setVersion(version);
		}
		if(path != null) {
			builder.setPath(path);
		}
		
		if(arrayNotEmpty(authors)) {
			builder.setAuthors(authors);
		}
		
		if(arrayNotEmpty(devEnvironmentUrls)) {
			builder.setDevEnvironmentUrls(devEnvironmentUrls);
		}
		if(arrayNotEmpty(testEnvironmentUrls)) {
			builder.setTestEnvironmentUrls(testEnvironmentUrls);
		}
		if(arrayNotEmpty(prodEnvironmentUrls)) {
			builder.setProdEnvironmentUrls(prodEnvironmentUrls);
		}
		
		if(arrayNotEmpty(scanPackages)) {
			builder.setScanPackages(scanPackages);
		}
		
		try {
			ClassLoader classloader = getClassLoader();
			builder.setClassLoader(classloader);
			
			if(header != null && header.unEmpty(classloader)) {
				builder.setCommonHeader(header.getStruct(), header.getClz(classloader));
			}
			if(url != null && url.unEmpty(classloader)) {
				builder.setCommonUrl(url.getStruct(), url.getClz(classloader));
			}
			if(request != null && request.unEmpty(classloader)) {
				builder.setCommonRequest(request.getStruct(), request.getClz(classloader));
			}
			if(response != null && response.unEmpty(classloader)) {
				builder.setCommonResponse(response.getStruct(), response.getClz(classloader));
			}
			
			if(arrayNotEmpty(dataTypeMatchEntities)) {
				builder.setDataTypeMatchEntities(dataTypeMatchEntities);
			}
			
			builder.build();
			getLog().info("api文档创建完成");
		} catch (Exception e) {
			getLog().error("api文档创建时出现异常", e);
		}
	}
	
	// 获取builder实例
	private ApiDocBuilder getBuilder() {
		getLog().info("生成api文档的类型="+apiDocType);
		if("folder".equalsIgnoreCase(apiDocType)) {
			return new ApiFolderBuilder();
		}else {
			return new ApiZipBuilder();
		}
	}
	
	// 判断array是否不为空
	private boolean arrayNotEmpty(Object[] array) {
		return array != null && array.length > 0;
	}
	
	@Parameter(defaultValue="${project.compileClasspathElements}", readonly=true, required=true)
	private List<String> compileClasspathElements;
	
	// 获取类加载器
	private ClassLoader getClassLoader() throws MalformedURLException  {
		URL[] urls = new URL[compileClasspathElements.size()];
		for(int i=0;i<compileClasspathElements.size();i++) {
			urls[i] = new File(compileClasspathElements.get(i)).toURI().toURL();
		}
		return new URLClassLoader(urls, getClass().getClassLoader());
	}
}
