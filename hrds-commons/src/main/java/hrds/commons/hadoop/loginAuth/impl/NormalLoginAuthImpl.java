package hrds.commons.hadoop.loginAuth.impl;

import org.apache.hadoop.conf.Configuration;

import java.io.File;

/**
 * 华为 FI C80平台登录认证实现
 */
public class NormalLoginAuthImpl extends LoginAuthImpl {

	/**
	 * 登录认证实现,如果认证配置文件目录不存在,则取服务部署目录下的conf
	 */
	public NormalLoginAuthImpl() {
		this(System.getProperty("user.dir") + File.separator + "conf" + File.separator);
		logger.info("platform: normal, go to the default configuration file!");
	}

	/**
	 * 登录认证实现,根据指定的配置文件目录,设置认证文件信息
	 *
	 * @param conf_dir 认证配置文件所在目录
	 */
	public NormalLoginAuthImpl(String conf_dir) {
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
	 * normal 登录认证
	 *
	 * @param principle_name 认证实体
	 * @param userKeytabPath keytab文件路径
	 * @param krb5ConfPath   krb5文件路径
	 * @param conf           Configuration对象
	 * @return Configuration
	 */
	@Override
	public synchronized Configuration login(String principle_name, String userKeytabPath, String krb5ConfPath, Configuration conf) {
		return conf;
	}
}
