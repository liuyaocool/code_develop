package generator.mybatis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

public class SqlScriptRunner {
	private String driver;
	private String url;
	private String userid;
	private String password;
	private String sourceFile;
	private Log log;

	public SqlScriptRunner(String sourceFile, String driver, String url, String userId, String password) throws MojoExecutionException {
		System.out.println("sql: "+sourceFile + "/" + driver + "/" + url + "/" + userId + "/" + password);

		if (!StringUtility.stringHasValue(sourceFile)) {
			throw new MojoExecutionException("SQL script file is required");
		} else if (!StringUtility.stringHasValue(driver)) {
			throw new MojoExecutionException("JDBC Driver is required");
		} else if (!StringUtility.stringHasValue(url)) {
			throw new MojoExecutionException("JDBC URL is required");
		} else {
			this.sourceFile = sourceFile;
			this.driver = driver;
			this.url = url;
			this.userid = userId;
			this.password = password;
		}
	}

	public void executeScript() throws MojoExecutionException {
		Connection connection = null;

		try {
			Class<?> driverClass = ObjectFactory.externalClassForName(this.driver);
			Driver theDriver = (Driver)driverClass.newInstance();
			Properties properties = new Properties();
			if (this.userid != null) {
				properties.setProperty("user", this.userid);
			}

			if (this.password != null) {
				properties.setProperty("password", this.password);
			}

			connection = theDriver.connect(this.url, properties);
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			BufferedReader br = this.getScriptReader();

			String sql;
			while((sql = this.readStatement(br)) != null) {
				statement.execute(sql);
			}

			this.closeStatement(statement);
			connection.commit();
			br.close();
		} catch (ClassNotFoundException var16) {
			throw new MojoExecutionException("Class not found: " + var16.getMessage());
		} catch (FileNotFoundException var17) {
			throw new MojoExecutionException("File note found: " + this.sourceFile);
		} catch (SQLException var18) {
			throw new MojoExecutionException("SqlException: " + var18.getMessage(), var18);
		} catch (IOException var19) {
			throw new MojoExecutionException("IOException: " + var19.getMessage(), var19);
		} catch (InstantiationException var20) {
			throw new MojoExecutionException("InstantiationException: " + var20.getMessage());
		} catch (IllegalAccessException var21) {
			throw new MojoExecutionException("IllegalAccessException: " + var21.getMessage());
		} finally {
			this.closeConnection(connection);
		}
	}

	public String getDriver() {
		return this.driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException var3) {
				this.log.debug("SQLException on close connection", var3);
			}
		}

	}

	private void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException var3) {
				this.log.debug("SQLException on close statement", var3);
			}
		}

	}

	private String readStatement(BufferedReader br) throws IOException {
		StringBuilder sb = new StringBuilder();

		String line;
		while((line = br.readLine()) != null) {
			if (!line.startsWith("--") && StringUtility.stringHasValue(line)) {
				if (line.endsWith(";")) {
					sb.append(' ');
					sb.append(line.substring(0, line.length() - 1));
					break;
				}

				sb.append(' ');
				sb.append(line);
			}
		}

		String s = sb.toString().trim();
		if (s.length() > 0) {
			this.log.debug(Messages.getString("Progress.13", s));
		}

		return s.length() > 0 ? s : null;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	private BufferedReader getScriptReader() throws MojoExecutionException, IOException {
		BufferedReader answer;
		if (this.sourceFile.startsWith("classpath:")) {
			String resource = this.sourceFile.substring("classpath:".length());
			URL url = ObjectFactory.getResource(resource);
			InputStream is = url.openStream();
			if (is == null) {
				throw new MojoExecutionException("SQL script file does not exist: " + resource);
			}

			answer = new BufferedReader(new InputStreamReader(is));
		} else {
			File file = new File(this.sourceFile);
			if (!file.exists()) {
				throw new MojoExecutionException("SQL script file does not exist");
			}

			answer = new BufferedReader(new FileReader(file));
		}

		return answer;
	}
}
