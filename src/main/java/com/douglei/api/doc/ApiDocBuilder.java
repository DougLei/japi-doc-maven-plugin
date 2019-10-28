package com.douglei.api.doc;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.douglei.api.doc.js.variable.entity.about.AboutEntity;
import com.douglei.api.doc.js.variable.entity.common.CommonParamEntity;
import com.douglei.api.doc.js.variable.entity.index.Index;
import com.douglei.api.doc.metadata.ApiCatalogMetadata;
import com.douglei.api.doc.metadata.ApiMetadata;
import com.douglei.api.doc.types.ParamStructType;
import com.douglei.tools.instances.scanner.ClassScanner;

/**
 * api文档构建器
 * @author DougLei
 */
public abstract class ApiDocBuilder {
	private static final Logger logger = LoggerFactory.getLogger(ApiDocBuilder.class);
	
	protected static final String apiDocTemplateFileName = "api-doc-template.zip";
	
	protected String name = "api文档";
	protected String version = "1.0";
	protected String path = System.getProperty("user.dir") + File.separatorChar + "target" + File.separatorChar; // 生成api文档的路径, 在当前项目的target目录下

	private String[] authors = {System.getProperty("user.name")};
	
	private String[] devEnvironmentUrls = {"-"};
	private String[] testEnvironmentUrls = {"-"};
	private String[] prodEnvironmentUrls = {"-"};
	
	private CommonParamEntity commonHeader = new CommonParamEntity("commonHeader");
	private CommonParamEntity commonUrl = new CommonParamEntity("commonUrl");
	private CommonParamEntity commonRequest = new CommonParamEntity("commonRequest");
	private CommonParamEntity commonResponse = new CommonParamEntity("commonResponse");
	
	private String[] scanPackages =new File(System.getProperty("user.dir")+ File.separatorChar +"src"+File.separatorChar+"main"+File.separatorChar+"java").list();
	private ClassLoader classloader;
	
	/**
	 * 输出数据
	 * @param out
	 * @param data
	 * @throws IOException
	 */
	private void write(OutputStream out, String data) throws IOException {
		out.write(data.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * build api文档
	 * @throws Exception 
	 */
	public void build() throws Exception{
		logParams();
		build_();
	}
	
	/**
	 * 输出相关参数数据的日志
	 */
	protected void logParams() {
		logger.info("生成api文档的名称和标题={}", name);
		logger.info("api版本={}", version);
		logger.info("生成api文档的绝对路径={}", path);
		logger.info("api作者={}", Arrays.toString(authors));
		logger.info("api开发环境的url={}", Arrays.toString(devEnvironmentUrls));
		logger.info("api测试环境的url={}", Arrays.toString(testEnvironmentUrls));
		logger.info("api生产环境的url={}", Arrays.toString(prodEnvironmentUrls));
		logger.info("通用的请求头配置类={}", commonHeader);
		logger.info("通用的请求URL配置类={}", commonUrl);
		logger.info("通用的请求体配置类={}", commonRequest);
		logger.info("通用的响应体配置类={}", commonResponse);
		logger.info("要扫描的包路径={}", Arrays.toString(scanPackages));
	}
	
	/**
	 * 子类需要实现的方法, build api文档
	 * @throws Exception 
	 */
	protected abstract void build_() throws Exception;
	
	/**
	 * 写入api的数据(api.data.js)
	 * @param out
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	protected void writeApiData(OutputStream out) throws IOException, ClassNotFoundException {
		List<ApiCatalogMetadata> apiCatalogs = apiCatalogMetadatas();
		Index index = new Index(name, version, (short)apiCatalogs.size());
		
		ApiMetadata api = null;
		write(out, "var apis={");
		for (ApiCatalogMetadata apiCatalog : apiCatalogs) {
			if(apiCatalog.existsApi()) {
				index.addApiCatalog(apiCatalog);
				do{
					// 如果不是第一个api, 则输出,用来和前面一个分割
					if(api != null) write(out, ",");
					// 写入apis信息
					api = apiCatalog.nextApi();
					write(out, "\""+index.addApi(api)+"\":");
					write(out, api.toApiEntity().toJScript());
				}while(apiCatalog.hasNextApi());
			}
			apiCatalog.destroy();
		}
		write(out, "}\n");
		
		write(out, index.toJScript());
		write(out, new AboutEntity(authors, devEnvironmentUrls, testEnvironmentUrls, prodEnvironmentUrls).toJScript());// 写入关于页面的数据
		write(out, commonHeader.toJScript());// 写入通用请求头参数的数据
		write(out, commonUrl.toJScript());// 写入通用请求URL参数的数据
		write(out, commonRequest.toJScript());// 写入通用请求体参数的数据
		write(out, commonResponse.toJScript());// 写入通用响应体参数的数据
	}
	
	/**
	 * 获取api catalog的元数据数组
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private List<ApiCatalogMetadata> apiCatalogMetadatas() throws ClassNotFoundException {
		ClassScanner scanner = new ClassScanner();
		List<String> classes = scanner.setClassLoader(ApiDocBuilderContext.getClassLoader()).multiScan(scanPackages);
		if(classes.size() > 0) {
			List<ApiCatalogMetadata> apiCatalogMetadatas = new ArrayList<ApiCatalogMetadata>(classes.size());
			ApiCatalogMetadata acm;
			boolean existsHigherPriority = false;// 是否存在更高的优先级配置
			for (String clz : classes) {
				acm = ApiCatalogMetadata.newInstance(ApiDocBuilderContext.getClassLoader().loadClass(clz));
				if(acm != null) {
					if(acm.getPriority() > 0 && !existsHigherPriority) {
						existsHigherPriority = true;
					}
					apiCatalogMetadatas.add(acm);
				}
			}
			scanner.destroy();
			
			if(apiCatalogMetadatas.size() > 0) {
				if(existsHigherPriority) {// 如果存在更高的优先级配置, 则要对集合进行排序
					apiCatalogMetadatas.sort(new Comparator<ApiCatalogMetadata>() {
						@Override
						public int compare(ApiCatalogMetadata o1, ApiCatalogMetadata o2) {
							if(o1.getPriority() > o2.getPriority()) {
								return -1;
							}else if(o1.getPriority() < o2.getPriority()) {
								return 1;
							}
							return 0;
						}
					});
				}
				return apiCatalogMetadatas;
			}
		}
		return Collections.emptyList();
	}
	
	/**
	 * 生成api文档的文件名, 默认为api文档
	 * @param name
	 * @return
	 */
	public ApiDocBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * 设置api版本, 默认为1.0
	 * @param author
	 * @return
	 */
	public ApiDocBuilder setVersion(String version) {
		this.version = version;
		return this;
	}
	
	/**
	 * 生成api文档的路径, 默认在当前项目的target目录下, 该属性要传入绝对路径
	 * @param path
	 * @return
	 */
	public ApiDocBuilder setPath(String path) {
		char lastChar = path.charAt(path.length()-1); 
		if(lastChar != '\\' && lastChar != '/') {
			path += File.separatorChar;
		}
		this.path = path;
		return this;
	}
	
	/**
	 * 设置api作者, 默认为当前计算机的名称
	 * @param authors
	 * @return
	 */
	public ApiDocBuilder setAuthors(String... authors) {
		this.authors = authors;
		return this;
	}
	
	/**
	 * 设置api开发环境的url
	 * @param urls
	 * @return
	 */
	public ApiDocBuilder setDevEnvironmentUrls(String... urls) {
		this.devEnvironmentUrls = urls;
		return this;
	}
	
	/**
	 * 设置api测试环境的url
	 * @param urls
	 * @return
	 */
	public ApiDocBuilder setTestEnvironmentUrls(String... urls) {
		this.testEnvironmentUrls = urls;
		return this;
	}
	
	/**
	 * 设置api生产环境的url
	 * @param urls
	 * @return
	 */
	public ApiDocBuilder setProdEnvironmentUrls(String... urls) {
		this.prodEnvironmentUrls = urls;
		return this;
	}
	
	/**
	 * 设置通用的请求头配置类
	 * @param type
	 * @param headerClass
	 * @return
	 */
	public ApiDocBuilder setCommonHeader(ParamStructType type, Class<?> configurationClass) {
		this.commonHeader.setProperties(type, configurationClass);
		ApiDocBuilderContext.setExistsCommonHeaderParam();
		return this;
	}
	
	/**
	 * 设置通用的请求URL配置类
	 * @param type
	 * @param headerClass
	 * @return
	 */
	public ApiDocBuilder setCommonUrl(ParamStructType type, Class<?> configurationClass) {
		this.commonUrl.setProperties(type, configurationClass);
		ApiDocBuilderContext.setExistsCommonUrlParam();
		return this;
	}
	
	/**
	 * 设置通用的请求体配置类
	 * @param type
	 * @param headerClass
	 * @return
	 */
	public ApiDocBuilder setCommonRequest(ParamStructType type, Class<?> configurationClass) {
		this.commonRequest.setProperties(type, configurationClass);
		ApiDocBuilderContext.setExistsCommonRequestParam();
		return this;
	}
	
	/**
	 * 设置通用的响应体配置类
	 * @param type
	 * @param headerClass
	 * @return
	 */
	public ApiDocBuilder setCommonResponse(ParamStructType type, Class<?> configurationClass) {
		this.commonResponse.setProperties(type, configurationClass);
		ApiDocBuilderContext.setExistsCommonResponseParam();
		return this;
	}

	/**
	 * 要扫描的包路径, 默认为当前项目的根包名
	 * @param scanPackages
	 * @return
	 */
	public ApiDocBuilder setScanPackages(String... scanPackages) {
		this.scanPackages = scanPackages;
		return this;
	}
	
	/**
	 * 设置类加载器
	 * @param classloader
	 * @return
	 */
	public ApiDocBuilder setClassLoader(ClassLoader classloader) {
		this.classloader = classloader;
		ApiDocBuilderContext.setClassLoader(classloader);
		return this;
	}
}
