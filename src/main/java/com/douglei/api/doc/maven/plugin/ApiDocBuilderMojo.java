package com.douglei.api.doc.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.douglei.api.doc.js.variable.entity.apis.common.CommonParamEntity;

/**
 * 
 * @author DougLei
 */
@Mojo(name = "apiDocBuilder", defaultPhase = LifecyclePhase.COMPILE)
public class ApiDocBuilderMojo extends AbstractMojo {
	
	protected String name;
	protected String path;

	private String[] authors;
	private String version;
	private String[] devEnvironmentUrls;
	private String[] testEnvironmentUrls;
	private String[] prodEnvironmentUrls;
	
	private CommonParamEntity commonHeader;
	private CommonParamEntity commonUrl;
	private CommonParamEntity commonRequest;
	private CommonParamEntity commonResponse;
	
	private String[] scanPackages;
	
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub

	}
}
