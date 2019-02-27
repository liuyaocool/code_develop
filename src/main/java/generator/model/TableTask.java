package generator.model;

import generator.JavaHelpMojo;
import generator.SqlRunner;
import generator.utils.FileUtil;

import java.io.IOException;
import java.util.List;

public class TableTask implements Runnable{

	private String filePrefix;	//文件前缀名(实体类名)
	private ColumnInfo primaryInfo;// 主键信息
	private String proPkg;// 项目名
	private String modelPkg;// 模块名
	private String controllerPkg;//
	private String servicePkg;//
	private String mapperPkg;//
	private String pojoPkg;//
	private String javaPath;// java路径
	private String resourcesPath;// 资源路径
	private String webFolder;// jsp页面路径
	private String encoding;// 编码
	private String tableName;// 编码
	private SqlRunner sqlRunner;// 数据库驱动

	public TableTask(String proPkg, String modelPkg, String controllerPkg, String servicePkg, String mapperPkg, String pojoPkg, String encoding, String webFolder, SqlRunner sqlRunner) {
		this.proPkg = proPkg;
		this.modelPkg = modelPkg;
		this.controllerPkg = controllerPkg;
		this.servicePkg = servicePkg;
		this.mapperPkg = mapperPkg;
		this.pojoPkg = pojoPkg;
		this.encoding = encoding;
		this.sqlRunner = sqlRunner;
		this.javaPath = System.getProperty("user.dir") + "/src/main/java/";
		this.resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
		this.webFolder = webFolder;
//		System.out.println(this.proPkg + "/" + this.modelPkg +
//		this.controllerPkg  + "/" +
//				this.servicePkg  + "/" +
//				this.mapperPkg  + "/" +
//				this.pojoPkg  + "/" +
//				this.encoding  + "/" +
//				this.sqlRunner  + "/" +
//				this.javaPath  + "/" +
//				this.resourcesPath);
	}

	public TableTask myClone(String tableName){
		TableTask tableTask = new TableTask(this.proPkg,this.modelPkg,this.controllerPkg,
				this.servicePkg, this.mapperPkg,this.pojoPkg, this.encoding, this.webFolder, this.sqlRunner);
		tableTask.setTableName(tableName);
		return tableTask;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		this.filePrefix = FileUtil.getInstance().stringFormat(tableName, true);
	}

	public void create(String tableName) {
		this.filePrefix = FileUtil.getInstance().stringFormat(tableName, true);
		List<ColumnInfo> columns = this.sqlRunner.executeScript(tableName);
		//包名(aa.bb.**.cc)，将路径中**替换成controller、service 等即可 全局可用
		String pkgPath = this.proPkg + "." + this.modelPkg;
		try {
			//创建实体类并返回导包信息
			String entityImport = createEneity(this.filePrefix,columns, pkgPath.replace("**", this.pojoPkg));
			//创建mapper接口并返回导包信息
			String imapperImport = createInter("IMapper.txt", entityImport,
					pkgPath.replace("**", this.mapperPkg),"I"+this.filePrefix+"Mapper");
			//创建mapper.xml
			String mapperXmlPath = createMapperXml(tableName,columns,entityImport,imapperImport,
					this.modelPkg.replace("**",""),this.filePrefix+"Mapper");
			//创建mapper接口并返回导包信息
			String iserviceImport = createInter("IService.txt",entityImport,
					pkgPath.replace("**", this.servicePkg),"I"+this.filePrefix+"Service");
			//创建service实现类并返回导包信息
			String serviceImport = createServiceImpl(entityImport, iserviceImport, imapperImport,
					pkgPath.replace("**", this.servicePkg) + ".impl",this.filePrefix+"Service");
			//创建controller
			String controlMapping = createController(tableName,entityImport,iserviceImport,
					pkgPath.replace("**", this.controllerPkg), this.filePrefix+"Controller");
			createJsp(controlMapping,columns);

			System.out.println(tableName + "创建完成");
		} catch (IOException e){
//			e.printStackTrace();
			System.out.println(e.toString());
		} finally {
			JavaHelpMojo.taskCount--;
		}
	}


	/**
	 * 创建实体类
	 * @param filePrefix 文件名
	 * @param columns 表格字段详情
	 * @param pkgPath 包名
	 * @return
	 */
	private String createEneity(String filePrefix, List<ColumnInfo> columns, String pkgPath) throws IOException {

		StringBuffer sb = new StringBuffer("package ").append(pkgPath).append(";\n\nimport java.util.*;\nimport java.text.*;\n\n");
		sb.append("public class ").append(filePrefix).append("{\n");
		StringBuffer sbGetSet = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getColumnType() == 1) this.primaryInfo = columns.get(i);
			sb.append(columns.get(i).getEntity());
			sbGetSet.append(columns.get(i).getGetSetMethod());
		}
		sb.append("\n\tpublic ").append(filePrefix).append(" (){}\n");
		sb.append(sbGetSet).append("\n}");

		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				filePrefix + ".java", sb.toString(), this.encoding, false);
		return pkgPath + "." + filePrefix;
	}

	/**
	 * 创建接口
	 * @param entityImport 实体类导包路径
	 * @return
	 */
	private String createInter(String mfileName, String entityImport,String pkgPath,String fileName) throws IOException {

		String content = FileUtil.getInstance().getInsideFile("/model/"+mfileName);
		content = content.replace("#filePrefix#", this.filePrefix);
		content = content.replace("#entityImport#", entityImport);
		content = content.replace("#interPkg#", pkgPath);
		content = content.replace("#fileName#", fileName);

		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				fileName + ".java", content, this.encoding, false);
		return pkgPath + "." + fileName;
	}

	private String createServiceImpl(String entityImport, String iServiceImport, String iMapperImport, String pkgPath,String fileName) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/serviceImpl.txt");
		content = content.replace("#servicePkg#", pkgPath);
		content = content.replace("#entityImport#", entityImport);
		content = content.replace("#iServiceImport#", iServiceImport);
		content = content.replace("#iMapperImport#", iMapperImport);
		content = content.replace("#fileName#", fileName);
		content = content.replace("#iServiceName#", getNameFromImport(iServiceImport));
		content = content.replace("#iMapperName#", getNameFromImport(iMapperImport));
		content = content.replace("#filePrefix#", this.filePrefix);
		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				fileName + ".java", content, this.encoding, false);
		return pkgPath + "." + filePrefix;
	}

	private String createMapperXml(String tableName, List<ColumnInfo> columns, String entityImport, String iMapperImport, String pkgPath,String fileName) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/MapperXml.txt");
		content = content.replace("#iMapperImport#", iMapperImport);
		content = content.replace("#entityImport#", entityImport);
		content = content.replace("#tableName#", tableName);
		content = content.replace("#prmaryLimit#", this.primaryInfo.getUpdate());
		content = content.replace("#prmaryColumn#", this.primaryInfo.getColumnName());
		content = content.replace("#entityPrimary#", this.primaryInfo.getEntityAttr());
		StringBuffer dataSb = new StringBuffer();
		StringBuffer columnsSb = new StringBuffer();
		StringBuffer insertValuesSb = new StringBuffer();
		StringBuffer updateSb = new StringBuffer();
		StringBuffer whereSb = new StringBuffer();
		ColumnInfo c;
		for (int i = 0; i < columns.size(); i++) {
			c = columns.get(i);
			dataSb.append(c.getResultData());
			whereSb.append(c.getWhereXml());
			if (0 == i){//第一个不加逗号
				columnsSb.append(c.getColumnName());
				insertValuesSb.append(c.getValue());
				updateSb.append(c.getUpdate());
			} else {
				insertValuesSb.append(",").append(c.getValue());
				columnsSb.append(",").append(c.getColumnName());
				updateSb.append(",\n").append(c.getUpdate());
			}
		}
		content = content.replace("#columns#", columnsSb.toString());
		content = content.replace("#resultData#", dataSb.toString());
		content = content.replace("#insertValues#", insertValuesSb.toString());
		content = content.replace("#updateValues#", updateSb.toString());
		content = content.replace("#whereVague#", whereSb);
//		System.out.println(content);
		FileUtil.getInstance().createFile(
				this.resourcesPath + "mapper/" + pkgPath.replace(".", "/"),
				fileName + ".xml", content, this.encoding, false);
		return pkgPath + "." + filePrefix;
	}

	private String createController(String tableName, String entityImport, String iServiceImport, String pkgPath, String fileName) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/Controller.txt");
		content = content.replace("#controllerPkg#", pkgPath);
		content = content.replace("#IServiceImport#", iServiceImport);
		content = content.replace("#entityImport#", entityImport);
		String controllerMapping = FileUtil.getInstance().stringFormat(tableName, false);
		content = content.replace("#controllerMapping#", controllerMapping);
		content = content.replace("#IService#", getNameFromImport(iServiceImport));
		content = content.replace("#filePrefix#", this.filePrefix);
		content = content.replace("#controllerName#", fileName);
		content = content.replace("#PrimaryId#",
				FileUtil.getInstance().stringFormat(this.primaryInfo.getColumnName(), true));
		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				fileName + ".java", content, this.encoding, false);
		return controllerMapping;
	}

	/**
	 *
	 * @param controllerMap 控制层请求路径
	 * @param columns
	 */
	private void createJsp(String controllerMap, List<ColumnInfo> columns) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/jsp.txt");
		content = content.replace("#controllerUrl#", controllerMap);
		StringBuilder searchSb = new StringBuilder();
		StringBuilder inputSb = new StringBuilder("<input name='").append(this.primaryInfo.getEntityAttr()).append("' hidden>\n\t");
		StringBuilder fieldSb = new StringBuilder();
		ColumnInfo c;
		for (int i = 0; i < columns.size(); i++) {
			c = columns.get(i);
			if (1 != c.getColumnType()){
				searchSb.append(c.getDlgAttr());
				fieldSb.append(c.getLayuiField());
			}
		}
		inputSb.append(searchSb);
		content = content.replace("#searchAttr#", searchSb.toString().replace(" lay-verify='required'",""));
		content = content.replace("#inputAttr#", inputSb.toString());
		content = content.replace("#layuifield#", fieldSb.toString());

		FileUtil.getInstance().createFile(
				(this.resourcesPath + this.webFolder + "/" + this.modelPkg).replace(".**", "").replace(".", "/"),
				controllerMap + ".jsp", content, this.encoding, false);
	}

	/**
	 * 创建通用js
	 */
	public void createCommonJs() throws IOException {

		String content = FileUtil.getInstance().getInsideFile("/model/commonjs.txt");
		FileUtil.getInstance().createFile(
				this.resourcesPath + this.webFolder.replace(".", "/"),
				"common.js", content, this.encoding, false);
	}

	/**
	 * 从导包路径中获得文件名（无后缀）
	 * @param fileImport
	 * @return
	 */
	private String getNameFromImport(String fileImport){
		return fileImport.substring(fileImport.lastIndexOf(".") + 1);
	}

	@Override
	public void run() {
		create(this.tableName);
	}
}
