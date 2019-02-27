package generator.test;

import generator.model.ColumnInfo;

public class TestColumnInfo {

	public static void main(String[] args) {
		ColumnInfo c = new ColumnInfo("USER_ID","用户id","varchar(1)");
		System.out.println(c.getDlgAttr());
		System.out.println("a".length());
	}
}
