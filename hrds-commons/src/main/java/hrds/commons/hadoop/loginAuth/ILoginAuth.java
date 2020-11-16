package hrds.commons.hadoop.loginAuth;

import org.apache.hadoop.conf.Configuration;

/**
 * 登录认证接口
 */
public interface ILoginAuth {


	/**
	 * 登录方法,使用指定的认证实体名
	 *
	 * @param principle_name 认证实体名
	 * @return Configuration
	 */
	Configuration login(String principle_name);

	/**
	 * 登录方法,使用指定的认证实体名和Configuration对象
	 *
	 * @param principle_name 认证实体名
	 * @param conf           Configuration
	 * @return Configuration
	 */
	Configuration login(String principle_name, Configuration conf);

	/**
	 * 登录认证,使用指定的认证实体名,kerberos,hrb5,Configuration对象
	 *
	 * @param principle_name 认证实体
	 * @param keytabPath     keytab文件路径
	 * @param krb5ConfPath   krb5文件路径
	 * @param conf           Configuration对象
	 */
	Configuration login(String principle_name, String keytabPath, String krb5ConfPath, Configuration conf);
}
