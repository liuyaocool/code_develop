package generator;

import java.io.*;
import java.sql.*;
import java.util.*;

import generator.model.ColumnInfo;
import org.ho.yaml.Yaml;

public class SqlRunner {

	private String driver;
	private String url;
	private String userid;
	private String password;

	public SqlRunner(String sourcePath, String driver, String url, String userId, String password){
		sourcePath = System.getProperty("user.dir") + "/src/main/resources/" + sourcePath;
		if (sourcePath.endsWith(".yml")){
			Map<String, Map<String,Map<String,String>>> ymlMap = null;
			try {
				ymlMap = Yaml.loadType(new File(sourcePath) , HashMap.class);
				Map<String, String> jdbcMap = ymlMap.get("spring").get("datasource");
				this.driver = jdbcMap.get("driver-class-name");
				this.url = jdbcMap.get("url");
				this.userid = jdbcMap.get("username");
				this.password = jdbcMap.get("password");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
//			System.out.println("yml:"+this.driver+this.userid+this.password+this.url);
		} else {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(sourcePath);
				Properties p = new Properties();
				p.load(fis);
				this.driver = p.getProperty(driver);
				this.url = p.getProperty(url);
				this.userid = p.getProperty(userId);
				this.password = p.getProperty(password);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != fis) fis.close();
				} catch (IOException e) {}
			}

		}

//		System.out.println(this.driver+this.url+this.userid+this.password);
	}
	public SqlRunner(String driver, String url, String userId, String password) {
			this.driver = driver;
			this.url = url;
			this.userid = userId;
			this.password = password;
	}

	public List<ColumnInfo> executeScript(String tableName) {
		Connection connection = null;
		PreparedStatement statement = null;
		List<ColumnInfo> datas = new ArrayList<>();//保存字段名
		try {
			connection = DriverManager.getConnection(this.url, this.userid, this.password);
//			connection.setAutoCommit(false);
			String sql = getSql(tableName);
			statement = connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()){
				ColumnInfo tableInfo = new ColumnInfo(
						rs.getString("name"),
						rs.getString("description"),
						rs.getString("datatype"));
				String pri = rs.getString("pri");
				tableInfo.setColumnType(0);
				if (null != pri && !"".equals(pri)){
					tableInfo.setColumnType(1);
				}
				datas.add(tableInfo);
//				System.out.println(tableInfo.getJdbcType() + tableInfo.getColumnName() + tableInfo.getColumnDescription() + tableInfo.getColumnType());
			}
//			connection.commit();
		} catch (SQLException var18) {
			System.out.println("SQLException on connection:" + var18);
		} finally {
			this.closeStatement(statement);
			this.closeConnection(connection);
		}
		return datas;
	}

	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException var3) {
				System.out.println("SQLException on close connection" + var3);
			}
		}

	}

	private void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException var3) {
				System.out.println("SQLException on close statement" + var3);
			}
		}

	}

	private String getSql(String tableName){
		if (this.driver.contains("oracle")){
			return "SELECT B.COLUMN_NAME NAME,A.COMMENTS DESCRIPTION,CU.COLUMN_NAME PRI, replace(B.DATA_TYPE,'VARCHAR2','VARCHAR') DATATYPE \n" +
					"FROM USER_TAB_COLUMNS B \n" +
					"LEFT JOIN USER_COL_COMMENTS A ON B.TABLE_NAME = A.TABLE_NAME AND B.COLUMN_NAME = A.COLUMN_NAME \n" +
					"LEFT JOIN (SELECT CU.TABLE_NAME,CU.COLUMN_NAME FROM USER_CONS_COLUMNS CU,USER_CONSTRAINTS AU \n" +
					"WHERE CU.CONSTRAINT_NAME = AU.CONSTRAINT_NAME AND AU.CONSTRAINT_TYPE = 'P') CU " +
					"ON CU.TABLE_NAME = B.TABLE_NAME AND B.COLUMN_NAME = CU.COLUMN_NAME \n" +
					"WHERE B.TABLE_NAME = '" + tableName + "' ORDER BY B.COLUMN_ID ASC";
		} else if (this.driver.contains("mysql")){
			return "SELECT COLUMN_NAME NAME, COLUMN_COMMENT DESCRIPTION, COLUMN_KEY PRI, DATA_TYPE DATATYPE \n" +
					" FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + tableName + " ORDER BY ORDINAL_POSITION ASC";
		} else if (this.driver.contains("sqlserver")){ //缺少datatype
			return "SELECT B.NAME, C.VALUE AS DESCRIPTION, E.COLUMN_NAME PRI  FROM\n" +
					"SYS.TABLES A INNER JOIN SYS.COLUMNS B ON B.OBJECT_ID = A.OBJECT_ID LEFT JOIN SYS.EXTENDED_PROPERTIES C ON C.MAJOR_ID = B.OBJECT_ID \n" +
					"AND C.MINOR_ID = B.COLUMN_ID LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS D ON A.NAME = D.TABLE_NAME \n" +
					"AND D.CONSTRAINT_TYPE = 'PRIMARY KEY' LEFT JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE E ON D.CONSTRAINT_NAME = E.CONSTRAINT_NAME \n" +
					"AND B.NAME = E.COLUMN_NAME WHERE A.NAME = '" + tableName + "' ORDER BY B.COLUMN_ID ASC";
		} else {
			return null;
		}
	}

}
