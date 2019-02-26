package generator.model;

import generator.utils.FileUtil;

public class ColumnInfo {

	private String columnName;//字段名
	private String entityAttr;//实体类属性名
	private String columnDescription;//表字段描述
	private String jdbcType;//表字段类型
	private String javaType;//实体类属性类型
	private int columnType; //1:主键  0：非主键
	private String whereXml;// mapperxml中条件查询的字符串: <if test='id != null'> and id = '%id%'</if>

	public ColumnInfo() { }

	public ColumnInfo(String columnName, String columnDescription, String jdbcType) {
		this.columnName = columnName;
		this.columnDescription = columnDescription;
		if (jdbcType.contains("(")){
			this.jdbcType = jdbcType.substring(0,jdbcType.indexOf("("));
		} else {
			this.jdbcType = jdbcType;
		}
		this.entityAttr = FileUtil.getInstance().stringFormat(columnName, false);

		switch (this.jdbcType){
			case "a": this.javaType = "";break;
			case "z": this.javaType = "";break;
			case "b": this.javaType = "";break;
			case "v": this.javaType = "";break;
			case "d": this.javaType = "";break;
			default: this.javaType = "String";break;
		}
	}

	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}

	//xml中的resultdata标签 <result column="USER_ID" jdbcType="VARCHAR" property="userId" />
	public String getResultData(){
		StringBuilder sb = new StringBuilder();
		if (1 == columnType){
			sb.append("<id column=\"");
		} else {
			sb.append("\t\t<result column=\"");
		}
		sb.append(this.columnName).append("\" jdbcType=\"").append(this.jdbcType)
				.append("\" property=\"").append(this.entityAttr).append("\" />\n");
		return sb.toString();
	}

	//xml中读取数据的字符串 #{userId,jdbcType=VARCHAR}
	public String getValue() {
		return new StringBuilder("#{").append(this.entityAttr).append(",jdbcType=")
				.append(this.jdbcType).append("}").toString();
	}

	//实体类中获得属性 private String userId;
	public String getEntity(){
		return new StringBuilder("\n\tprivate ").append(this.javaType)
				.append(" ").append(this.entityAttr).append(";//")
				.append(this.columnDescription).append("\n").toString();
	}

	//xml中获得更新方法 字段的sql USER_ID = #{userId,jdbcType=VARCHAR}
	public String getUpdate() {
		if (1 == this.columnType){
			return new StringBuilder(this.columnName).append(" = ").append(getValue()).toString();
		}
		return new StringBuilder("\t\t").append(this.columnName).append(" = ").append(getValue()).toString();
	}

	//oracle获得mybatis xml中 where条件查询
	private String getXmlWhere(){
		String whereStr = "\n\t\t<if test=\"@@@ != '' and @@@ != null\">\n\t\t\tand ### @#$\n\t\t</if>";
		if (1 == this.columnType){
			whereStr.replace("@#$", "= #{@@@}");
		} else {
			whereStr.replace("@#$", "like '%'||#{@@@}||'%'");
		}
		return whereStr.replace("@@@",this.entityAttr).replace("###",this.columnName);
	}

	public String getEntityAttr() {
		return entityAttr;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getColumnType() {
		return columnType;
	}

	//返回getset方法字符串
	public String getGetSetMethod() {
		String aaa = "\n\tpublic void set@@@(%%% ###){ this.### = ### == null ? null : ###.trim(); }\n\n\tpublic %%% get@@@(){ return this.###; }\n";
		aaa = aaa.replace("###", this.entityAttr);
		aaa = aaa.replace("@@@", FileUtil.getInstance().stringFormat(columnName, true));
		return aaa.replace("%%%", this.javaType);
	}

	public String getWhereXml() {
//		return this.whereXml;
		return getXmlWhere();
	}

	public void setWhereXml(String whereXml) {
		this.whereXml = whereXml;
	}
}
