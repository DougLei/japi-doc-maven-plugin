package com.douglei.api.doc.maven.plugin;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.douglei.api.doc.ApiDocBuilder;
import com.douglei.api.doc.ApiFolderBuilder;
import com.douglei.api.doc.ApiZipBuilder;
import com.douglei.tools.utils.ExceptionUtil;

/**
 * 
 * @author DougLei
 */
@Mojo(name = "apiDocBuilder", defaultPhase = LifecyclePhase.COMPILE)
public class ApiDocBuilderMojo extends AbstractMojo {
	private static final Logger logger = LoggerFactory.getLogger(ApiDocBuilderMojo.class);
	
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
	 * 版本, 默认为1.0
	 */
	@Parameter
	private String version;
	
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
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		ApiDocBuilder builder = getBuilder();
		if(name != null) {
			builder.setName(name);
		}
		if(path != null) {
			builder.setPath(path);
		}
		
		if(arrayNotEmpty(authors)) {
			builder.setAuthors(authors);
		}
		if(version != null) {
			builder.setVersion(version);
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
		
		if(header != null && header.unEmpty()) {
			builder.setCommonHeader(header.getStructureType(), header.getClz());
		}
		if(url != null && url.unEmpty()) {
			builder.setCommonUrl(url.getStructureType(), url.getClz());
		}
		if(request != null && request.unEmpty()) {
			builder.setCommonRequest(request.getStructureType(), request.getClz());
		}
		if(response != null && response.unEmpty()) {
			builder.setCommonResponse(response.getStructureType(), response.getClz());
		}
		
		if(arrayNotEmpty(scanPackages)) {
			builder.setScanPackages(scanPackages);
		}
		
		try {
			builder.build();
			logger.info("api文档创建完成");
		} catch (IOException e) {
			logger.error("api文档创建时出现异常: {}", ExceptionUtil.getExceptionDetailMessage(e));
		}
	}
	
	// 获取builder实例
	private ApiDocBuilder getBuilder() {
		if("folder".equalsIgnoreCase(apiDocType)) {
			return new ApiFolderBuilder();
		}else {
			return new ApiZipBuilder();
		}
	}
	
	// 判断array是否不为空
	private boolean arrayNotEmpty(String[] array) {
		return array != null && array.length > 0;
	}
}
