package hrds.commons.hadoop.loginAuth.impl;

import hrds.commons.exception.BusinessException;
import org.apache.hadoop.conf.Configuration;

import java.io.File;

/**
 * CDH平台登录认证实现
 */
public class CDH5LoginAuthImpl extends LoginAuthImpl {

	/**
	 * 登录认证实现,如果认证配置文件目录不存在,则取服务部署目录下的conf
	 */
	public CDH5LoginAuthImpl() {
		this(System.getProperty("user.dir") + File.separator + "conf" + File.separator);
		logger.info("platform: CDH5, go to the default configuration file!");
	}

	/**
	 * 登录认证实现,根据指定的配置文件目录,设置认证文件信息
	 *
	 * @param conf_dir 认证配置文件所在目录
	 */
	public CDH5LoginAuthImpl(String conf_dir) {
		PATH_TO_KEYTAB = conf_dir + "user.keytab";
		PATH_TO_KRB5_CONF = conf_dir + "krb5.conf";
		PATH_TO_JAAS = conf_dir + "jaas.conf";
		PATH_TO_CORE_SITE_XML = conf_dir + "core-site.xml";
		PATH_TO_HDFS_SITE_XML = conf_dir + "hdfs-site.xml";
		PATH_TO_HBASE_SITE_XML = conf_dir + "hbase-site.xml";
		PATH_TO_MAPRED_SITE_XML = conf_dir + "mapred-site.xml";
		PATH_TO_YARN_SITE_XML = conf_dir + "yarn-site.xml";
	}


	/**
	 * 登录认证
	 *
	 * @param principle_name 认证实体
	 * @param keytabPath keytab文件路径
	 * @param krb5ConfPath   krb5文件路径
	 * @param conf           Configuration对象
	 */
	@Override
	public synchronized Configuration login(String principle_name, String keytabPath, String krb5ConfPath, Configuration conf) {
		// 1.check input parameters
		if ((principle_name == null) || (principle_name.length() <= 0)) {
			logger.error("input principle_name is invalid.");
			throw new BusinessException("input principle_name is invalid.");
		}
		if ((keytabPath == null) || (keytabPath.length() <= 0)) {
			logger.error("input keytabPath is invalid.");
			throw new BusinessException("input keytabPath is invalid.");
		}
		if ((krb5ConfPath == null) || (krb5ConfPath.length() <= 0)) {
			logger.error("input krb5ConfPath is invalid.");
			throw new BusinessException("input krb5ConfPath is invalid.");
		}
		if ((conf == null)) {
			logger.error("input conf is invalid.");
			throw new BusinessException("input conf is invalid.");
		}
		// 2.check file exsits
		File userKeytabFile = new File(keytabPath);
		if (!userKeytabFile.exists()) {
			logger.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
			throw new BusinessException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!userKeytabFile.isFile()) {
			logger.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
			throw new BusinessException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
		}
		// 3.set krb5config
		File krb5ConfFile = new File(krb5ConfPath);
		if (!krb5ConfFile.exists()) {
			logger.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
			throw new BusinessException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
		}
		if (!krb5ConfFile.isFile()) {
			logger.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
			throw new BusinessException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
		}
		setKrb5Config(krb5ConfFile.getAbsolutePath());
		// 4.set configuration
		setConfiguration(conf);
		// 5 set zookeeperServerPrincipal
		setZookeeperServerPrincipal("zookeeper/hadoop");
		// 6.login and check for hadoop
		loginHDFS(principle_name, userKeytabFile.getAbsolutePath());
		logger.info("Login success!!!!!!!!!!!!!!");
		//返回conf
		return conf;
	}
}
