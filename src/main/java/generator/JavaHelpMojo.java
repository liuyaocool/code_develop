package generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(
		name = "javaHelp",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class JavaHelpMojo extends AbstractMojo{

	//保存当前线程数
	public static int taskCount = 0;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		System.out.println("使用帮助：");
		System.out.println("pom.xml中添加plugin：");
		System.out.println("<plugin>");
		System.out.println("\t<groupId>zl</groupId>");
		System.out.println("\t<artifactId>code_develop</artifactId>");
		System.out.println("\t<version>1.0-SNAPSHOT</version>");
		System.out.println("\t<configuration>");
		System.out.println("\t\t<sourcePath>数据库配置文件路径</sourcePath>（必填）");
		System.out.println("\t\t<jdbcDriver>配置文件中驱动字段名</jdbcDriver>(yml文件不填.properties文件必填)");
		System.out.println("\t\t<jdbcUrl>jdbc.url</jdbcUrl>（同上）");
		System.out.println("\t\t<jdbcUsername>jdbc.username</jdbcUsername>（同上）");
		System.out.println("\t\t<jdbcPwd>jdbc.password</jdbcPwd>（同上）");
		System.out.println("\t\t<proPkg>org.test.generator</proPkg>（必填）");
		System.out.println("\t\t<-- **为controller，service等包名通配符 -->");
		System.out.println("\t\t<modelPkg>model1.model1-1.**</modelPkg>（必填）");
		System.out.println("\t\t<-- 配置如下，可生成org/test/generator/model1/model1-1/mycontroller/xxController.java -->");
		System.out.println("\t\t<controllerPkg>mycontroller</controllerPkg>（选填）");
		System.out.println("\t\t<-- service mapper pojo同上 -->");
		System.out.println("\t\t<servicePkg>myservice</servicePkg>（选填）");
		System.out.println("\t\t<mapperPkg>entry.mymapper</mapperPkg>（选填）");
		System.out.println("\t\t<pojoPkg>entry.mypojoo</pojoPkg>（选填）");
		System.out.println("\t\t<tableName>表名1,表名2，。。。</tableName>（必填）");
		System.out.println("\t</configuration>");
		System.out.println("</plugin>");

	}

}
