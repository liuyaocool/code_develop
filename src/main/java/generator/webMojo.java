package generator;

import generator.model.ColumnInfo;
import generator.utils.FileUtil;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.IOException;
import java.util.List;

@Mojo(
		name = "test",
		defaultPhase = LifecyclePhase.GENERATE_SOURCES,
		requiresDependencyResolution = ResolutionScope.TEST
)
public class webMojo extends AbstractMojo{

	private ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal<>();

	//在调用项目pom的plugin中的configuration标签下定义
	@Parameter(
			property = "sourcePath",
			defaultValue = "application.yml",
			required = true
	)
	private String sourcePath;
	@Parameter(property = "jdbcDriver")
	private String jdbcDriver;
	@Parameter(property = "jdbcUrl")
	private String jdbcUrl;
	@Parameter(property = "jdbcUsername")
	private String jdbcUsername;
	@Parameter(property = "jdbcPwd")
	private String jdbcPwd;
	@Parameter(property = "tableName",required = true)
	private String tableName;
	@Parameter(property = "encoding",defaultValue = "utf-8")
	private String encoding;
	@Parameter(property = "proPkg",required = true)
	private String proPkg;
	@Parameter(property = "modelPkg",required = true)
	private String modelPkg;
	@Parameter(property = "controllerPkg", defaultValue = "controller")
	private String controllerPkg;
	@Parameter(property = "servicePkg", defaultValue = "service")
	private String servicePkg;
	@Parameter(property = "mapperPkg", defaultValue = "mapper")
	private String mapperPkg;
	@Parameter(property = "pojoPkg", defaultValue = "pojo")
	private String pojoPkg;

	private String javaPath; //java路径
	private String resourcesPath;// 资源路径
	private String filePrefix;	//文件前缀名(实体类名)
	private ColumnInfo primaryInfo;// 主键名

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		this.filePrefix = FileUtil.getInstance().stringFormat(this.tableName, true);
		this.javaPath = System.getProperty("user.dir") + "/src/main/java/";
		this.resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";
		//包名(aa.bb.**.cc)，将路径中**替换成controller、service 等即可 全局可用
		String pkgPath = this.proPkg + "." + this.modelPkg;
		System.out.println(pkgPath);

		SqlRunner sr = new SqlRunner(this.resourcesPath + this.sourcePath,
				this.jdbcDriver,this.jdbcUrl,this.jdbcUsername,this.jdbcPwd,this.tableName);
		List<ColumnInfo> columns = sr.executeScript();

		System.out.println("数据库连接完成。");
		try {
			//创建实体类并返回导包信息
			String entityImport = createEneity(this.filePrefix,columns, pkgPath.replace("**", this.pojoPkg));
			//创建mapper接口并返回导包信息
			String imapperImport = createInter("IMapper.txt", entityImport,
					pkgPath.replace("**", this.mapperPkg),"I"+this.filePrefix+"Mapper");
			//创建mapper.xml
			String mapperXmlPath = createMapperXml(columns,entityImport,imapperImport,
					pkgPath.replace("**","").replace(this.proPkg,""),this.filePrefix+"Mapper");
			//创建mapper接口并返回导包信息
			String iserviceImport = createInter("IService.txt",entityImport,
					pkgPath.replace("**", this.servicePkg),"I"+this.filePrefix+"Service");
			//创建service实现类并返回导包信息
			String serviceImport = createServiceImpl(entityImport, iserviceImport, imapperImport,
					pkgPath.replace("**", this.servicePkg) + ".impl",this.filePrefix+"Service");
			//创建controller
			String controlMapping = createController(entityImport,iserviceImport,
					pkgPath.replace("**", this.controllerPkg), this.filePrefix+"Controller");

		} catch (IOException e){
//			e.printStackTrace();
			System.out.println(e.toString());
		}



//		FileUtil.getInstance().copyFile(FileUtil.getInstance().getFile(),
//				projectPath + "resources\\application-dev.yml");

	}

	/**
	 * 创建实体类
	 * @param filePrefix 文件名
	 * @param columns 表格字段详情
	 * @param pkgPath 包名
	 * @return
	 */
	public String createEneity(String filePrefix, List<ColumnInfo> columns, String pkgPath) throws IOException {

		StringBuffer sb = new StringBuffer("package ").append(pkgPath).append(";\n\n");
		sb.append("public class ").append(filePrefix).append("{\n");
		StringBuffer sbGetSet = new StringBuffer();
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getColumnType() == 1) this.primaryInfo = columns.get(i);
			sb.append("\n\t").append("private ").append(columns.get(i).getJavaType())
					.append(" ").append(columns.get(i).getEntityAttr())
					.append(";//").append(columns.get(i).getColumnDescription());
			sbGetSet.append(columns.get(i).getGetterMethod()).append(columns.get(i).getSetterMethod());
		}
		sb.append("\n\n\tpublic ").append(filePrefix).append(" (){}\n");
		sb.append(sbGetSet).append("\n}");

		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				filePrefix + ".java",
				sb.toString(), this.encoding);
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
		content = content.replace("#primaryId#", this.primaryInfo.getEntityAttr());
		content = content.replace("#interPkg#", pkgPath);
		content = content.replace("#fileName#", fileName);

		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				fileName + ".java",
				content, this.encoding);
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
				fileName + ".java", content, this.encoding);
		return pkgPath + "." + filePrefix;
	}

	private String createMapperXml(List<ColumnInfo> columns, String entityImport, String iMapperImport, String pkgPath,String fileName) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/MapperXml.txt");
		content = content.replace("#iMapperImport#", iMapperImport);
		content = content.replace("#entityImport#", entityImport);
		content = content.replace("#tableName#", this.tableName);
		content = content.replace("#prmaryLimit#", this.primaryInfo.getUpdate());
		StringBuffer dataSb = new StringBuffer();
		StringBuffer columnsSb = new StringBuffer();
		StringBuffer insertValuesSb = new StringBuffer();
		StringBuffer updateSb = new StringBuffer();
		ColumnInfo c;
		for (int i = 0; i < columns.size(); i++) {
			c = columns.get(i);
			dataSb.append(c.getResultData());
			if (0 == i){
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
//		System.out.println(content);
		FileUtil.getInstance().createFile(
				this.resourcesPath + "mapper/" + pkgPath.replace(".", "/"),
				fileName + ".xml", content, this.encoding);
		return pkgPath + "." + filePrefix;
	}

	private String createController( String entityImport, String iServiceImport, String pkgPath, String fileName) throws IOException {
		String content = FileUtil.getInstance().getInsideFile("/model/Controller.txt");
		content = content.replace("#controllerPkg#", pkgPath);
		content = content.replace("#IServiceImport#", iServiceImport);
		content = content.replace("#entityImport#", entityImport);
		String controllerMapping = FileUtil.getInstance().stringFormat(this.tableName, false);
		content = content.replace("#controllerMapping#", controllerMapping);
		content = content.replace("#IService#", getNameFromImport(iServiceImport));
		content = content.replace("#filePrefix#", this.filePrefix);
		content = content.replace("#controllerName#", fileName);
		content = content.replace("#PrimaryId#",
				FileUtil.getInstance().stringFormat(this.primaryInfo.getColumnName(), true));
		FileUtil.getInstance().createFile(
				this.javaPath + pkgPath.replace(".", "/"),
				fileName + ".java", content, this.encoding);
		return controllerMapping;
	}

	/**
	 * 从导包路径中获得文件名（无后缀）
	 * @param fileImport
	 * @return
	 */
	private String getNameFromImport(String fileImport){
		return fileImport.substring(fileImport.lastIndexOf(".") + 1);
	}
}
