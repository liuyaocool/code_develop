package generator;

import generator.utils.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;

@Mojo(
		name = "testt",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class webMojo extends AbstractMojo{

	private ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal<>();

	//在调用项目pom的plugin中的configuration标签下定义
	@Parameter(property = "tableName",required = true)
	private String tableName;
	@Parameter(property = "webFolder")
	private String webFolder;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		String sourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
		String fileName = FileUtil.getInstance().stringFormat(this.tableName, false);

		try {

			throw new IOException();
		} catch (IOException e){
//			e.printStackTrace();
			System.out.println(e.toString());
		}


	}
}
