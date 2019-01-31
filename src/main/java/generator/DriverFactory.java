package generator;

public abstract class DriverFactory {

	//数据库工厂 实现多数据库查询字段与备注
	private String driver;
	private String tableName;
	public abstract String getSql(String tableName);

	public static DriverFactory newInstanc(){

		return new DriverFactory() {
			@Override
			public String getSql(String tableName) {
				return null;
			}
		};
	}

	static DriverFactory getOracleSql(String tableName){
		return new DriverFactory(){

			@Override
			public String getSql(String tableName) {
				return null;
			}
		};
	}
	static DriverFactory getSqlserverSql(String tableName){
		return new DriverFactory(){

			@Override
			public String getSql(String tableName) {
				return null;
			}
		};
	}
	static DriverFactory getMysqlSql(String tableName){
		return new DriverFactory(){

			@Override
			public String getSql(String tableName) {
				return null;
			}
		};
	}
	/**
	 * oracle
	  SELECT
	        b.COLUMN_NAME,a.COMMENTS
	    FROM
	        USER_TAB_COLUMNS b,USER_COL_COMMENTS a
	    WHERE
	        b.TABLE_NAME = 'AUTH_MENU_TB' AND b.TABLE_NAME = a.TABLE_NAME AND b.COLUMN_NAME = a.COLUMN_NAME

	 */

	/**
	 * sqlserver
	 * SELECT B.name AS column_name,C.value AS column_description
	 * FROM sys.tables A INNER JOIN sys.columns B ON B.object_id = A.object_id
	 * LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id
	 * WHERE A.name ='AUTH_MENU_TB'
	 */

	/**
	 * mysql
	 * select COLUMN_NAME COLUMN_NAME ,COLUMN_COMMENT COLUMN_DESCRIPTION from information_schema.columns where  table_name = 't_yjzm_yjjl_pgb';
	 */
}
