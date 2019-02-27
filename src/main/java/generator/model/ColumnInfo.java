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
		this.columnDescription = null == columnDescription ? "" : columnDescription;
		if (jdbcType.contains("(")){
			this.jdbcType = jdbcType.substring(0,jdbcType.indexOf("("));
		} else {
			this.jdbcType = jdbcType;
		}
		this.entityAttr = FileUtil.getInstance().stringFormat(columnName, false);

		setJavaType(jdbcType);
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
			whereStr = whereStr.replace("@#$", "= #{@@@}");
		} else {
			whereStr = whereStr.replace("@#$", "like '%'||#{@@@}||'%'");
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
		if ("Date".equals(this.javaType))
			aaa = aaa.replace(" == null ? null : ###.trim()", "");
		aaa = aaa.replace("###", this.entityAttr)
				.replace("@@@", FileUtil.getInstance().stringFormat(columnName, true))
				.replace("%%%", this.javaType);
		return aaa;
	}

	public String getWhereXml() {
//		return this.whereXml;==============================================需要修改==============================================================================
		return getXmlWhere();
	}

	public String getDlgAttr(){
		String ddd = "<label class='toolLabel'/>@@@：\n\t\t<input type='text' name='###' autocomplete='off' lay-verify='required'>\n\t</label>\n\t";
		//报错： 数据库没有关于字段的描述
		return ddd.replace("@@@", this.columnDescription).replace("###",this.entityAttr);
	}

	public String getLayuiField(){
		String aa = "{field:'###', title: '@@@',align:'center'},\n\t\t";
		return aa.replace("@@@", this.columnDescription).replace("###",this.entityAttr);
	}

	public void setWhereXml(String whereXml) {
		this.whereXml = whereXml;
	}

	private void setJavaType(String jdbcType) {
		if (jdbcType.contains("TIME") || jdbcType.contains("DATE")){
			this.javaType = "Date";
		} else {
			this.javaType = "String";
		}
	}
}
