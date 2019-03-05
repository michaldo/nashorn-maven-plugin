package io.github.michaldo.nashorn_maven_plugin;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "eval", requiresProject = false, threadSafe = true)
public class EvalMojo extends AbstractMojo {

	@Parameter(required=true, property="script")
	private String script;
	
	@Parameter(defaultValue="false")
	private boolean skip;
	
	@Component
	private MavenProject project;
	
	@Component
	private MojoExecution mojoExecution;
	
	@Component
	private  MavenSession mavenSession;

	public void execute() throws MojoExecutionException {
		
		if (skip) {
			return;
		}

		ScriptEngineManager scriptEngineManager = 
				new ScriptEngineManager(ClassLoader.getSystemClassLoader());
		ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("nashorn");
		scriptEngine.put("$project", project);
		scriptEngine.put("$mojo", mojoExecution);
		scriptEngine.put("$session", mavenSession);
		scriptEngine.put("$plugin", mojoExecution.getMojoDescriptor().getPluginDescriptor());
		scriptEngine.put("$settings", mavenSession.getSettings());
		scriptEngine.put("$localRepository", mavenSession.getLocalRepository());
		scriptEngine.put("$reactorProjects", mavenSession.getProjects());
		scriptEngine.put("$repositorySystemSession", mavenSession.getRepositorySession());
		scriptEngine.put("$executedProject", project.getExecutionProject());
		scriptEngine.put("$basedir", project.getBasedir());
		
		String jsCode =  "(function(){" + script + "})()"; //function gives "if (..) return"
		try {
			scriptEngine.eval(jsCode);
		} catch (final ScriptException se) {
			throw new MojoExecutionException(jsCode, se);
		}
	}
}
