package generator.mybatis1;

import net.sf.json.JSONObject;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.mybatis.generator.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("all")
@Mojo(
		name = "mybatiss",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class MyBatisGeneratorMojo extends AbstractMojo {
	@Parameter(
			property = "project",
			required = true,
			readonly = true
	)
	private MavenProject project;
	@Parameter(
			property = "mybatis.generator.outputDirectory",
			defaultValue = "${project.build.directory}/generated-sources/mybatis-generator",
			required = true
	)
	private File outputDirectory;
	@Parameter(
			property = "mybatis.generator.configurationFile",
			defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml",
			required = true
	)
	private File configurationFile;

	public MyBatisGeneratorMojo() { }

	public void execute() throws MojoExecutionException {
//		System.out.println("configurationFile " + this.configurationFile);
//		System.out.println("outputDirectory " + this.outputDirectory);
//		System.out.println("project " + this.project);

//		LogFactory.setLogFactory(new MavenLogFactory(this));
		List<String> resourceDirectories = new ArrayList();

		for (Resource re: this.project.getResources()) {
			resourceDirectories.add(re.getDirectory());
			System.out.println(re.getDirectory());
		}

		ClassLoader cl = ClassloaderUtility.getCustomClassloader(resourceDirectories);
		ObjectFactory.addExternalClassLoader(cl);
		if (this.configurationFile == null || !this.configurationFile.exists()) {
			throw new MojoExecutionException(Messages.getString("RuntimeError.config file not exists."));
		} else {
			List<String> warnings = new ArrayList();
			Set<String> fullyqualifiedTables = new HashSet();

			Set<String> contextsToRun = new HashSet();
			String error;
			Iterator var22;
			try {
				ConfigurationParser cp = new ConfigurationParser(this.project.getProperties(), warnings);
				Configuration config = cp.parseConfiguration(this.configurationFile);
				ShellCallback callback = new MavenShellCallback(this, false);
				MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
				myBatisGenerator.generate(new MavenProgressCallback(this.getLog(), false), contextsToRun, fullyqualifiedTables);
			} catch (XMLParserException var11) {
				var22 = var11.getErrors().iterator();

				while(var22.hasNext()) {
					error = (String)var22.next();
					this.getLog().error(error);
				}

				throw new MojoExecutionException(var11.getMessage());
			} catch (SQLException var12) {
				throw new MojoExecutionException(var12.getMessage());
			} catch (IOException var13) {
				throw new MojoExecutionException(var13.getMessage());
			} catch (InvalidConfigurationException var14) {
				var22 = var14.getErrors().iterator();

				while(var22.hasNext()) {
					error = (String)var22.next();
					this.getLog().error(error);
				}

				throw new MojoExecutionException(var14.getMessage());
			} catch (InterruptedException var15) {
				;
			}

			Iterator var21 = warnings.iterator();
			//警告打印
			while(var21.hasNext()) {
				error = (String)var21.next();
				this.getLog().warn(error);
			}

			if (this.project != null && this.outputDirectory != null && this.outputDirectory.exists()) {
				this.project.addCompileSourceRoot(this.outputDirectory.getAbsolutePath());
				Resource resource = new Resource();
				resource.setDirectory(this.outputDirectory.getAbsolutePath());
				resource.addInclude("**/*.xml");
				this.project.addResource(resource);
			}

		}
	}


	public File getOutputDirectory() {
		return this.outputDirectory;
	}

	public File getConfigurationFile() {
		return configurationFile;
	}

	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}
}
