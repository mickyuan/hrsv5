/*
package hrds.commons.utils.jsch;

import fd.ng.core.utils.StringUtil;
import fd.ng.netserver.conf.HttpServerConf;
import hrds.commons.codes.Dispatch_Type;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_down_info;
import hrds.commons.utils.PropertyParaValue;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class AgentDownload {

	private static final Logger logger = LogManager.getLogger(AgentDownload.class.getName());

	public static void main(String[] args) throws IOException {

		*/
/*//*
/scpAgent(null, null, "123", "1", "123", "213", "sdf", "1");
		//System.out.println(path);
		String userDir = System.getProperty("user.dir");
		System.out.println(userDir);*//*

//		scpAgent(null, null, "123", "1", "123", "123", "123", "123", "1", "123", "q23", "qwe", "", "");
	}

	*/
/**
	 * 使用scp的方式直接部署启动agent
	 *
	 * @param request        {@link HttpServletRequest}
	 * @param response       {@link HttpServletResponse}
	 * @param agent_name     {@link String} agent名称
	 * @param agent_type     {@link String} agent类型
	 * @param agent_ip       {@link String} agentIp
	 * @param agent_port     {@link String} agent端口
	 * @param log4jLogDir    {@link String} log4j路径
	 * @param targetdir      {@link String} 部署agent的目录
	 * @param isstart        {@link String} 是否启动agent
	 * @param agentPatch     {@link String} agent路径
	 * @param oldTargetDir   {@link String} 老的安装地址
	 * @param oldLog4jLogDir {@link String} 老的日志地址
	 * @return {@link String} 0代表成功，1代表失败
	 *//*

	public static Map<String, String> scpAgent(HttpServletRequest request, HttpServletResponse response, String agent_name, String agent_type,
					String agent_ip, String agent_port, String log4jLogDir, String targetdir, String isstart, String agentPatch, String userName,
					String password, String hostPort, String oldTargetDir, String oldLog4jLogDir) {
//		String agentPatch = PropertyParaValue.getString("agentpath", "");
		File ff = FileUtils.getFile(agentPatch);
		String pathAbsolute = ff.getParent();
		String agent_zip = ff.getName();
		BufferedWriter bw = null;
		BufferedReader br = null;
		String separator = File.separator;
		String code = "1";
		Map<String, String> map = new HashMap<String, String>();
		try {
			//agent的目录(中文的拼音_端口)
			String dirAgent = ChineseUtil.getPingYin(agent_name) + "_" + agent_port;
			String hashCode = (agent_name + agent_type + agent_port).hashCode() + "";
			String _msgConf = hashCode + "_msgConf.xml";
			String pathFile = pathAbsolute + separator;
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile + _msgConf, false), "utf-8"));
			StringBuffer sb = new StringBuffer();
//			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator());
//			sb.append("<project><hyren>").append(System.lineSeparator());
//			sb.append("<hyren_host>").append(HttpServerConf.getHttpServer()).append("</hyren_host>").append(System.lineSeparator());
//			sb.append("<hyren_port>").append(HyrenXML.getHyrenPort()).append("</hyren_port>").append(System.lineSeparator());
//			sb.append("<hyren_osname>").append(HyrenXML.gethryOsName()).append("</hyren_osname>").append(System.lineSeparator());
//			sb.append("<hyren_storedir>").append(HyrenXML.getStoreDir()).append("</hyren_storedir>").append(System.lineSeparator())；
//			sb.append("</hyren>").append("<agent>").append(System.lineSeparator());
//			sb.append("<agent_name>").append(agent_name).append("</agent_name>").append(System.lineSeparator());
//			sb.append("<agent_type>").append(agent_type).append("</agent_type>").append(System.lineSeparator());
//			sb.append("<agent_ip>").append(agent_ip).append("</agent_ip>").append(System.lineSeparator());
//			sb.append("<agent_port>").append(agent_port).append("</agent_port>").append(System.lineSeparator());
//			sb.append("<agent_ishive>")
//							.append("HD".equals(PropertyParaValue.getString("ver_type", "HD")) ? IsFlag.Shi.toString() : IsFlag.Fou.toString())
//							.append("</agent_ishive>")
//							.append(System.lineSeparator());
//			sb.append("</agent></project>");
			bw.write(sb.toString());
			bw.flush();
			IOUtils.closeQuietly(bw);
			*/
/**
			 * 修改dbinfo.properties
			 *//*

			String _dbinfo = hashCode + "_dbinfo.properties";
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile + _dbinfo, false), "utf-8"));
			String path = AgentDownload.class.getResource("/dbinfo.properties").getPath();
			StringBuilder result = new StringBuilder();
			br = new BufferedReader(new FileReader(path));//构造一个BufferedReader类来读取文件
			String s = null;
			while( (s = br.readLine()) != null ) {//使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();

			*/
/**
			 * 修改log4j.properties
			 *//*

			String _log4j = hashCode + "_log4j.properties";
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile + _log4j, false), "utf-8"));
			StringBuffer log4j = new StringBuffer();
			log4j.append("log4j.rootLogger=INFO,DailyFile,CONSOLE,E").append(System.lineSeparator()).append(System.lineSeparator());

			log4j.append("log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender").append(System.lineSeparator());
			log4j.append("log4j.appender.CONSOLE.Threshold=DEBUG,INFO").append(System.lineSeparator());
			log4j.append("log4j.appender.CONSOLE.Target=System.out").append(System.lineSeparator());
			log4j.append("log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout").append(System.lineSeparator());
			log4j.append("log4j.appender.CONSOLE.layout.ConversionPattern=[%-5p] %m%n").append(System.lineSeparator()).append(System.lineSeparator());

			log4j.append("log4j.appender.DailyFile=org.apache.log4j.DailyRollingFileAppender").append(System.lineSeparator());
			log4j.append("log4j.appender.DailyFile.Threshold=INFO").append(System.lineSeparator());
			if( !StringUtil.isEmpty(log4jLogDir) ) {
				log4j.append("log4j.appender.DailyFile.File=").append(log4jLogDir).append(System.lineSeparator());
			}
			else {
				log4jLogDir = targetdir + separator + dirAgent + separator + "log" + separator + "running.log";
				log4j.append("log4j.appender.DailyFile.File=").append(log4jLogDir).append(System.lineSeparator());
				;
			}
			log4j.append("log4j.appender.DailyFile.Append=true").append(System.lineSeparator());
			log4j.append("log4j.appender.DailyFile.Encoding=utf-8").append(System.lineSeparator());
			log4j.append("log4j.appender.DailyFile.DatePattern='.'yyyy-MM-dd").append(System.lineSeparator());
			log4j.append("log4j.appender.DailyFile.layout=org.apache.log4j.PatternLayout").append(System.lineSeparator());
			log4j.append("log4j.appender.DailyFile.layout.ConversionPattern=%d - %c -%-4r [%t] %-5p %c %x - %m%n").append(System.lineSeparator());
			//写错误文件
			log4j.append("log4j.appender.E = org.apache.log4j.DailyRollingFileAppender").append(System.lineSeparator());
			log4j.append("log4j.appender.E.Threshold = ERROR").append(System.lineSeparator());
			if( !StringUtil.isEmpty(log4jLogDir) ) {
				String log4jLogE = new File(log4jLogDir).getParent() + separator + "error.log";
				log4j.append("log4j.appender.E.File=").append(log4jLogE).append(System.lineSeparator());
			}
			else {
				String log4jLogE = targetdir + separator + dirAgent + separator + "log" + separator + "error.log";
				log4j.append("log4j.appender.E.File=").append(log4jLogE).append(System.lineSeparator());
				;
			}
			log4j.append("log4j.appender.E.Append = true").append(System.lineSeparator());
			log4j.append("log4j.appender.E.Encoding=utf-8").append(System.lineSeparator());
			log4j.append("log4j.appender.E.DatePattern='.'yyyy-MM-dd").append(System.lineSeparator());
			log4j.append("log4j.appender.E.layout=org.apache.log4j.PatternLayout").append(System.lineSeparator());
			log4j.append("log4j.appender.E.layout.ConversionPattern=%d - %c -%-4r [%t] %-5p %c %x - %m%n").append(System.lineSeparator());
			bw.write(log4j.toString());
			bw.flush();
			IOUtils.closeQuietly(bw);

			String userDir = System.getProperty("user.dir");
			File userDirFile = FileUtils.getFile(userDir);
			//找到scpagent脚本，tomcat/webapps/A/scpagent
			//String scpagent = userDirFile.getParent() + separator + "webapps" + separator + PageHtml.proCon + separator + "scpagent";
			//找到conf脚本，tomcat/bin/conf
			String hadoopConf = userDirFile + separator + "conf" + separator;
			;

			Map<String, String> sftpDetails = new HashMap<>();
			sftpDetails.put(SCPFileSender.HOST, agent_ip);
			sftpDetails.put(SCPFileSender.USERNAME, userName);
			sftpDetails.put(SCPFileSender.PASSWORD, password);
			sftpDetails.put(SCPFileSender.PORT, hostPort);

			sftpDetails.put(SCPFileSender.SOURCEPATH, pathFile);//本地文件路径
			sftpDetails.put(SCPFileSender.AGENT_GZ, agent_zip);//本地文件名称
			sftpDetails.put(SCPFileSender._MSGCONF, _msgConf);//msgconf文件
			sftpDetails.put(SCPFileSender._DBINFO, _dbinfo);//dbinfo文件
			sftpDetails.put(SCPFileSender._LOG4J, _log4j);//log4j文件

			sftpDetails.put(SCPFileSender.HADOOPCONF, hadoopConf);//集群conf配置文件
			sftpDetails.put(SCPFileSender.TARGETDIR, targetdir);//目标路径
			sftpDetails.put(SCPFileSender.ISSTART, isstart);//是否启动
			String libList = userDirFile.getParent() + separator + "shared" + separator;
			sftpDetails.put(SCPFileSender.LIBLIST, libList);//lib

			sftpDetails.put(SCPFileSender.OLDTARGETDIR, oldTargetDir);
			sftpDetails.put(SCPFileSender.OLDLOG4JLOGDIR, oldLog4jLogDir);

			code = SCPFileSender.scpToFrom(sftpDetails, dirAgent);

			FileUtils.getFile(pathFile + _msgConf).delete();
			FileUtils.getFile(pathFile + _dbinfo).delete();
			FileUtils.getFile(pathFile + _log4j).delete();
			if( "0".equals(isstart) ) {
				logger.info("部署成功，agent到" + agent_ip + "机器的" + targetdir + "目录下，anget已经启动");
			}
			else {
				logger.info("部署成功，agent到" + agent_ip + "机器的" + targetdir + "目录下，anget没有启动，请到相应目录下进行启动");
			}
			map.put("state", code);//状态
			map.put("log4jLogDir", log4jLogDir);//日志文件，如/xxx/xxx/xxx/db_wenjiancaiji_3388/logruning.log
			map.put("targetdir", targetdir + File.separator + dirAgent);//部署目录 如/xxx/xxx/xxx/db_wenjiancaiji_3388
			return map;
		}
		catch(Exception e) {
			map.put("state", code);//状态
			return map;
		}
		finally {
			IOUtils.closeQuietly(br);
		}

	}

	*/
/**
	 * @param request    HttpServletRequest
	 * @param response   HttpServletResponse
	 * @param agent_name {@link String} agent名称
	 * @param agent_type {@link String} agent类型（要中文）
	 * @param agent_ip   {@link String} agentIP
	 * @param agent_port {@link String} agent端口
	 * @throws Exception
	 *//*

	public static void readZipFile(HttpServletRequest request, HttpServletResponse response, String agent_name, String agent_type, String agent_ip,
					String agent_port) throws Exception {

		String agentPatch = PropertyParaValue.getString("agentpath", "");
		readZipFile(request, response, agent_name, agent_type, agent_ip, agent_port, agentPatch);
	}

	*/
/**
	 * @param request    HttpServletRequest
	 * @param response   HttpServletResponse
	 * @param agent_name {@link String} agent名称
	 * @param agent_type {@link String} agent类型（要中文）
	 * @param agent_ip   {@link String} agentIP
	 * @param agent_port {@link String} agent端口
	 * @param agentPatch {@link String} agent指定的下载文件的目录
	 * @throws Exception
	 *//*

	private static void readZipFile(HttpServletRequest request, HttpServletResponse response, String agent_name, String agent_type, String agent_ip,
					String agent_port, String agentPatch) throws Exception {

		InputStream is = null;
		OutputStream out = null;
		FileInputStream in = null;
		BufferedReader br = null;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(System.lineSeparator());
			sb.append("<project><hyren>").append(System.lineSeparator());
//			sb.append("<hyren_host>").append(HyrenXML.getHyrenIp()).append("</hyren_host>").append(System.lineSeparator());
//			sb.append("<hyren_port>").append(HyrenXML.getHyrenPort()).append("</hyren_port>").append(System.lineSeparator());
//			sb.append("<hyren_osname>").append(HyrenXML.gethryOsName()).append("</hyren_osname>").append(System.lineSeparator());
//			sb.append("<hyren_storedir>").append(HyrenXML.getStoreDir()).append("</hyren_storedir>").append(System.lineSeparator());
			sb.append("</hyren>").append("<agent>").append(System.lineSeparator());
			sb.append("<agent_name>").append(agent_name).append("</agent_name>").append(System.lineSeparator());
			sb.append("<agent_type>").append(agent_type).append("</agent_type>").append(System.lineSeparator());
			sb.append("<agent_ip>").append(agent_ip).append("</agent_ip>").append(System.lineSeparator());
			sb.append("<agent_port>").append(agent_port).append("</agent_port>").append(System.lineSeparator());
			sb.append("</agent></project>");

			is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

			ZipFile zipFile = new ZipFile(agentPatch);
			String msgconf = "msgConf.xml";

			try {
				zipFile.removeFile(msgconf);
			}
			catch(Exception e) {
			}

			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setFileNameInZip(msgconf);
			parameters.setSourceExternalStream(true);
			zipFile.addStream(is, parameters);

			*/
/**
			 * 修改dbinfo.properties
			 *//*

			String dbinfo = "config/dbinfo.properties";
			String path = AgentDownload.class.getResource("/dbinfo.properties").getPath();
			StringBuilder result = new StringBuilder();
			br = new BufferedReader(new FileReader(path));//构造一个BufferedReader类来读取文件
			String s = null;
			while( (s = br.readLine()) != null ) {//使用readLine方法，一次读一行
				result.append(System.lineSeparator() + s);
			}
			br.close();
			result.append(System.lineSeparator());
			result.append("#-----------Derby----------------------").append(System.lineSeparator());
			;
			result.append("dbtype_derby=2").append(System.lineSeparator());
			;

			result.append("connect_switch_derby=0").append(System.lineSeparator());
			result.append("conn_url_derby=jdbc:derby:JobDB;create=true").append(System.lineSeparator());
			;
			result.append("driver_class_derby=org.apache.derby.jdbc.EmbeddedDriver").append(System.lineSeparator());
			;
			result.append("dbuser_derby=").append(System.lineSeparator());
			;
			result.append("dbpasswd_derby=").append(System.lineSeparator());
			;

			result.append("pjdbcjndi_derby=hrds").append(System.lineSeparator());
			;

			result.append("initialPoolSize_derby=10").append(System.lineSeparator());
			;
			result.append("minPoolSize_derby=10").append(System.lineSeparator());
			;
			result.append("maxPoolSize_derby=20").append(System.lineSeparator());
			;
			result.append("maxIdleTime_derby=18000").append(System.lineSeparator());
			;
			result.append("idleConnectionTestPeriod_derby=600").append(System.lineSeparator());
			;

			is = new ByteArrayInputStream(result.toString().getBytes("UTF-8"));
			try {
				zipFile.removeFile(dbinfo);
			}
			catch(Exception e) {
			}

			parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setFileNameInZip(dbinfo);
			parameters.setSourceExternalStream(true);
			zipFile.addStream(is, parameters);

			*/
/**
			 *
			 *//*

			File file = zipFile.getFile();
			in = new FileInputStream(file);
			// 清空response
			response.reset();
			//设置响应头，控制浏览器下载该文件
			agent_name = agent_name + ".zip";
			if( request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0 ) {
				response.setHeader("content-disposition", "attachment;filename=" + new String(agent_name.getBytes("UTF-8"), "ISO8859-1"));
			}
			else {
				response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(agent_name, "UTF-8"));
			}

			response.setContentType("APPLICATION/OCTET-STREAM");
			//创建输出流
			out = response.getOutputStream();
			byte buffer[] = new byte[1024];
			int len = 0;
			//循环将输入流中的内容读取到缓冲区当中
			while( (len = in.read(buffer)) > 0 ) {
				//输出缓冲区的内容到浏览器，实现文件下载
				out.write(buffer, 0, len);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(br);
		}
	}
}
*/
