package generator.mybatis1;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.File;
import java.util.StringTokenizer;

public class MavenShellCallback extends DefaultShellCallback {

	private MyBatisGeneratorMojo mybatisGeneratorMojo;

	public MavenShellCallback(MyBatisGeneratorMojo mybatisGeneratorMojo, boolean overwrite) {
		super(overwrite);
		this.mybatisGeneratorMojo = mybatisGeneratorMojo;
	}

	public File getDirectory(String targetProject, String targetPackage) throws ShellException {
		if (!"MAVEN".equals(targetProject)) {
			return super.getDirectory(targetProject, targetPackage);
		} else {
			File project = this.mybatisGeneratorMojo.getOutputDirectory();
			if (!project.exists()) {
				project.mkdirs();
			}

			if (!project.isDirectory()) {
				throw new ShellException(Messages.getString("Warning.9", project.getAbsolutePath()));
			} else {
				StringBuilder sb = new StringBuilder();
				StringTokenizer st = new StringTokenizer(targetPackage, ".");

				while(st.hasMoreTokens()) {
					sb.append(st.nextToken());
					sb.append(File.separatorChar);
				}

				File directory = new File(project, sb.toString());
				if (!directory.isDirectory()) {
					boolean rc = directory.mkdirs();
					if (!rc) {
						throw new ShellException(Messages.getString("Warning.10", directory.getAbsolutePath()));
					}
				}

				return directory;
			}
		}
	}
}
