package generator.test;

import generator.mybatis1.MyBatisGeneratorMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;

public class TestMybatis {

	public static void main(String[] args) throws MojoExecutionException {
		MyBatisGeneratorMojo mojo = new MyBatisGeneratorMojo();
		mojo.setConfigurationFile(new File("E:\\project\\costManageSystem\\develop\\src\\standard_framework\\generateor\\src\\main\\resources\\generatorConfig.xml"));
		mojo.execute();
	}
}
