package generator.mybatis;

import org.mybatis.generator.logging.Log;

public class MavenLogImpl implements Log {
	private final org.apache.maven.plugin.logging.Log mavenLog;

	MavenLogImpl(org.apache.maven.plugin.logging.Log log) {
		this.mavenLog = log;
	}

	public boolean isDebugEnabled() {
		return this.mavenLog.isDebugEnabled();
	}

	public void error(String s, Throwable e) {
		this.mavenLog.error(s, e);
	}

	public void error(String s) {
		this.mavenLog.error(s);
	}

	public void debug(String s) {
		this.mavenLog.debug(s);
	}

	public void warn(String s) {
		this.mavenLog.warn(s);
	}
}
