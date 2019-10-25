package com.douglei.api.doc.maven.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.artifact.resolve.ArtifactResolver;
import org.apache.maven.shared.artifact.resolve.ArtifactResolverException;

import com.douglei.api.doc.ApiDocBuilder;
import com.douglei.api.doc.ApiFolderBuilder;
import com.douglei.api.doc.ApiZipBuilder;

/**
 * 
 * @author DougLei
 */
@Mojo(name = "apiDocBuilder", defaultPhase = LifecyclePhase.COMPILE)
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
			ClassLoader classloader = getCurrentProjectClassLoader();
			
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
			
			builder.setClassLoader(classloader).build();
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
	private boolean arrayNotEmpty(String[] array) {
		return array != null && array.length > 0;
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	/**
	 * 当前使用插件的项目对象
	 */
	@Parameter(defaultValue="${project}", readonly=true, required=true)
	private MavenProject currentProject;
	
	@Component
	private ProjectBuilder projectBuilder;
	
	@Parameter( defaultValue = "${session}", readonly = true, required = true )
	protected MavenSession session;
	
	@Parameter( defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true )
	private List<ArtifactRepository> remoteRepositories;
	
	@Component
	private ArtifactResolver artifactResolver;
	
	private void addParentArtifacts(MavenProject project, Set<Artifact> artifacts) throws ArtifactResolverException {
		while(project.hasParent()) {
			project = project.getParent();
			if(artifacts.contains(project.getArtifact())) {
				break;
			}
			ProjectBuildingRequest buildingRequest = newResolveArtifactProjectBuildingRequest();
			Artifact resolvedArtifact = artifactResolver.resolveArtifact(buildingRequest, project.getArtifact()).getArtifact();
			artifacts.add(resolvedArtifact);
		}
	}
	
	private ProjectBuildingRequest newResolveArtifactProjectBuildingRequest() {
		ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
		buildingRequest.setRemoteRepositories(remoteRepositories);
		return buildingRequest;
	}
	
	// 获取当前项目的类加载器
	private ClassLoader getCurrentProjectClassLoader() throws MalformedURLException, DependencyResolutionRequiredException, ProjectBuildingException, ArtifactResolverException {
		
        Set<Artifact> artifacts = currentProject.getArtifacts();
        for (Artifact artifact : new ArrayList<Artifact>(artifacts)){
            addParentArtifacts(projectBuilder.build(artifact, session.getProjectBuildingRequest()).getProject(), artifacts);
        }
        addParentArtifacts(currentProject, artifacts);
        System.out.println(artifacts.size());
        
        
        
		List<String> currentProjectCompileClasspathElements = currentProject.getCompileClasspathElements();
		URL[] urls = new URL[currentProjectCompileClasspathElements .size()];
		for (byte i=0;i<currentProjectCompileClasspathElements.size();i++) {
			urls[i] = new File(currentProjectCompileClasspathElements.get(i)).toURI().toURL();
		}
		return new URLClassLoader(urls);
	}
	
	
}
