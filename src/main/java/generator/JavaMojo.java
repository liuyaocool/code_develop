package generator;

import generator.model.TableTask;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
		name = "java",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class JavaMojo extends AbstractMojo{

	private ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal<>();
	private int taskCount = 0;

	//在调用项目pom的plugin中的configuration标签下定义
	@Parameter(
			property = "sourcePath",
			defaultValue = "application.yml",
			required = true
	)
	private String sourcePath;
	@Parameter(property = "jdbcDriver")
	private String jdbcDriver;
	@Parameter(property = "jdbcUrl")
	private String jdbcUrl;
	@Parameter(property = "jdbcUsername")
	private String jdbcUsername;
	@Parameter(property = "jdbcPwd")
	private String jdbcPwd;
	@Parameter(property = "tableName",required = true)
	private String tableName;
	@Parameter(property = "encoding",defaultValue = "utf-8")
	private String encoding;
	@Parameter(property = "proPkg",required = true)
	private String proPkg;
	@Parameter(property = "modelPkg",required = true)
	private String modelPkg;
	@Parameter(property = "controllerPkg", defaultValue = "controller")
	private String controllerPkg;
	@Parameter(property = "servicePkg", defaultValue = "service")
	private String servicePkg;
	@Parameter(property = "mapperPkg", defaultValue = "mapper")
	private String mapperPkg;
	@Parameter(property = "pojoPkg", defaultValue = "pojo")
	private String pojoPkg;
	@Parameter(property = "webFolder", defaultValue = "jsp")
	private String webFolder;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		String[] tables = this.tableName.split(",");
		SqlRunner sr = new SqlRunner(this.sourcePath, this.jdbcDriver,this.jdbcUrl,this.jdbcUsername,this.jdbcPwd);
		TableTask tableTask = new TableTask(this.proPkg,this.modelPkg,this.controllerPkg,
				this.servicePkg, this.mapperPkg,this.pojoPkg, this.encoding, this.webFolder, sr);
		for (int i = 0; i < tables.length; i++) {
			//若使用多异步操作需要确认所有线程执行完毕才可返回
			new Thread(tableTask.myClone(tables[i])).start();
			JavaHelpMojo.taskCount++;
//			tableTask.create(tables[i]);
		}
		while (JavaHelpMojo.taskCount > 0){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
