package hrds.commons.utils.jsch;

import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import hrds.commons.exception.BusinessException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: Agent部署需要的配置文件信息</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-06 10:36</p>
 * <p>version: JDK 1.8</p>
 */
public class WriteConf {

	/**
	 * <p>字段描述: 当前目录</p>
	 */
	private final static String parentFilePath = System.getProperty("user.dir") + File.separator + "agentConf";
	/**
	 * <p>字段描述: 系统换行符</p>
	 */
	private final static String lineSeparator = System.lineSeparator();
	/**
	 * <p>字段描述: 缩进(4个空格)</p>
	 */
	private final static String indentation = "    ";

	/**
	 * <p>方法描述: 写部署Agent需要的HttpServer配置文件</p>
	 * <p> 1: 获取本项目下的配置文件信息</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-06</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public static void writeHttpServeConf(String confName) {

		final HttpServerConfBean httpServer = HttpServerConf.getHttpServer();
		writeData(parseConfInfoToStr(httpServer, true), confName, false);

	}

	/**
	 * <p>方法描述: DB配置文件写入</p>
	 * <p>1 : 重新设置配置文件的Name</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-06</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public static void writeHyRenConf(String confName) {

		final HttpServerConfBean confBean = HttpServerConf.confBean;
		//1 : 重新设置配置文件的Name
		writeData(parseConfInfoToStr(confBean, false), confName, true);
	}

	/**
	 * <p>方法描述: 写配置文件的统一方法</p>
	 * <p>1 : 如果不是追加的方式,则做文件的判断删除,防止有上次文件的存在</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-06</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	private static void writeData(String confData, String fileName, Boolean isAppend) {

		FileWriter writer = null;
		try {
			File file = new File(parentFilePath + File.separator + fileName);
			//如果上级目录不存在先创建目录,在创建文件
			String parent = file.getParent();
			File parentDir = new File(parent);
			if( !parentDir.exists() ) {
				boolean mkdir = new File(parent).mkdir();
				//目录创建成功,则创建文件
				if( mkdir ) {
					file.createNewFile();
				}
			}
			else {
				//如果文件存在,并且不是追加的方式,则先删除文件,在建立新的文件
				if( file.exists() && !isAppend ) {
					boolean delete = file.delete();
					if( delete ) {
						file.createNewFile();
					}
				}
			}
			writer = new FileWriter(file, isAppend);

			writer.write(confData.toString());
			writer.write(lineSeparator);
			writer.flush();
		}
		catch(IOException e) {
			if( null != writer ) {
				try {
					writer.close();
				}
				catch(IOException ex) {
					throw new BusinessException("文件流关闭异常");
				}
			}
			throw new BusinessException("文件写入失败");
		}
	}

	/**
	 * <p>方法描述: 将读取的配置信息组合成规定的格式(yam)</p>
	 * <p>1 : 如果是hyren的配置信息,需要将name定义为 : hyrenagent,并不在写第一行信息</p>
	 * <p>2 : 将组合的数据返回</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-06</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	private static String parseConfInfoToStr(HttpServerConfBean httpServer, Boolean isWriteOneLine) {

		String oneLine = "";
		//1 : 如果是hyren的配置信息,需要将name定义为 : hyrenagent,并不在写第一行信息
		if( isWriteOneLine ) {
			oneLine = "httpserver:" + lineSeparator;
		}
		else {
			httpServer.setName("hyrenagent");
		}
		//将组合的数据返回
		return String.format(oneLine +
										"  -" +
										lineSeparator + indentation
										+ "name : %s" +
										lineSeparator + indentation
										+ "host : %s" +
										lineSeparator + indentation
										+ "port : %s" +
										lineSeparator + indentation
										+ "httpsPort : %s" +
										lineSeparator + indentation
										+ "webContext : %s" +
										lineSeparator + indentation
										+ "actionPattern : %s" +
										lineSeparator + indentation
										+ "session :" + lineSeparator + indentation
										+ "  maxage : %s" + lineSeparator + indentation
										+ "  httponly : %s", httpServer.getName(), httpServer.getHost(), httpServer.getHttpPort(), httpServer.getHttpsPort(),
						httpServer.getWebContext(), httpServer.getActionPattern(), httpServer.getSession_MaxAge(), httpServer.isSession_HttpOnly()
		);
	}
}
