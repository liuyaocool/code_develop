package generator.mybatis;


import org.apache.maven.plugin.Mojo;
import org.mybatis.generator.logging.AbstractLogFactory;
import org.mybatis.generator.logging.Log;

public class MavenLogFactory implements AbstractLogFactory{
	private final MavenLogImpl logImplementation;

	public MavenLogFactory(Mojo mojo) {
		this.logImplementation = new MavenLogImpl(mojo.getLog());
	}

	public Log getLog(Class<?> targetClass) {
		return this.logImplementation;
	}
}
