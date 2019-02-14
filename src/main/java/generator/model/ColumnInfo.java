package generator.model;

import generator.utils.FileUtil;

public class ColumnInfo {

	private String columnName;//字段名
	private String entityAttr;//实体类属性名
	private String columnDescription;//表字段描述
	private String jdbcType;//表字段类型
	private String javaType;//实体类属性类型
	private int columnType; //1:主键  0：非主键
	private String getterMethod;//get方法
	private String setterMethod;//set方法字符串

	public ColumnInfo() { }

	public ColumnInfo(String columnName, String columnDescription, String jdbcType) {
		this.columnName = columnName;
		this.columnDescription = columnDescription;
		this.jdbcType = jdbcType;
		this.entityAttr = FileUtil.getInstance().stringFormat(columnName, false);

		switch (jdbcType){
			case "a": this.javaType = "";break;
			case "TIMESTAMP": this.javaType = "Date";break;
			case "b": this.javaType = "";break;
			case "v": this.javaType = "";break;
			case "d": this.javaType = "";break;
			default: this.javaType = "String";break;
		}

		String bigEntityAttr = FileUtil.getInstance().stringFormat(columnName, true);
		String aaa = "\n\tpublic void set@@@(%%% ###){ this.### = ### == null ? null : ###.trim(); }\n";
		aaa = aaa.replace("###", this.entityAttr);
		aaa = aaa.replace("@@@", bigEntityAttr);
		this.setterMethod = aaa.replace("%%%", this.javaType);
		aaa = "\n\tpublic %%% get@@@(){ return this.###; }\n";
		aaa = aaa.replace("@@@", bigEntityAttr);
		aaa = aaa.replace("###", this.entityAttr);
		this.getterMethod = aaa.replace("%%%", this.javaType);

//		System.out.print(this.getterMethod + this.setterMethod);
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	public String getEntityAttr() {
		return entityAttr;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public int getColumnType() {
		return columnType;
	}

	public String getJdbcType() {
		return jdbcType;
	}

	public String getGetterMethod() {
		return getterMethod;
	}

	public String getSetterMethod() {
		return setterMethod;
	}

	public String getJavaType() {
		return javaType;
	}
}
