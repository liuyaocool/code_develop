package generator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import generator.utils.FileUtil;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

public class SqlRunner {

	private String driver;
	private String url;
	private String userid;
	private String password;
	private String tableName;
	private String sourceFilePath;
	private Log log;

	public SqlRunner(String sourceFile, String driver, String url, String userId, String password, String tableName) {
			this.sourceFilePath = sourceFile;
			this.driver = driver;
			this.url = url;
			this.userid = userId;
			this.password = password;
			this.tableName = tableName;
	}

	public List<String> executeScript() throws MojoExecutionException {
		Connection connection = null;
		List<String> colums = new ArrayList<>();//保存字段名
		try {
			connection = DriverManager.getConnection(this.url, this.userid, this.password);
//			connection.setAutoCommit(false);
			String sql = getSql(this.tableName);
			PreparedStatement statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery(sql);
			ResultSetMetaData data = rs.getMetaData();
			//保存查到的字段明
			for (int i = 1; i < data.getColumnCount(); i++) {
				colums.add(FileUtil.getInstance().stringFormat(data.getColumnName(i), false));
				System.out.println(colums.get(i-1));
			}
			this.closeStatement(statement);
			connection.commit();
		} catch (SQLException var18) {
			throw new MojoExecutionException("SqlException: " + var18.getMessage(), var18);
		} finally {
			this.closeConnection(connection);
		}
		return colums;
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

	public void setLog(Log log) {
		this.log = log;
	}

	private String getSql(String tableName){
		switch (this.driver){
			case "oracle":
				return "SELECT b.COLUMN_NAME name,a.COMMENTS description FROM USER_TAB_COLUMNS b,USER_COL_COMMENTS a \n" +
					"WHERE b.TABLE_NAME = '" + tableName + "' AND b.TABLE_NAME = a.TABLE_NAME AND b.COLUMN_NAME = a.COLUMN_NAME";
			default: return null;
		}
	}

}
