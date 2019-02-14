package generator.mybatis1;

import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.internal.NullProgressCallback;

public class MavenProgressCallback extends NullProgressCallback {
		private Log log;
		private boolean verbose;

		public MavenProgressCallback(Log log, boolean verbose) {
			this.log = log;
			this.verbose = verbose;
		}

		public void startTask(String subTaskName) {
			if (this.verbose) {
				this.log.info(subTaskName);
			}

		}

}
