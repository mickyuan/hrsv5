package hrds.commons.utils.key;

import fd.ng.core.conf.AppinfoConf;
import fd.ng.core.conf.ConfFileLoader;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.core.utils.key.SnowflakeImpl;
import fd.ng.core.yaml.YamlFactory;
import fd.ng.core.yaml.YamlMap;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.spark.sql.sources.In;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p> 标 题: 海云数服 </p>
 * <p> 描 述: 产生各表的主键 </p>
 * <p> 版 权: Copyright (c) 2011 </p>
 * <p> 公 司: 博彦科技 </p>
 * <p> 创建时间: 2011-7-20 下午04:41:33 </p>
 *
 * @author Administrator
 * @version 1.0 UuidGener
 */
public class PrimayKeyGener {
	private static final Logger logger = LogManager.getLogger();

	private static SnowflakeImpl idgen = null;

	static {
		String projectId = AppinfoConf.ProjectId;
		if (StringUtil.isEmpty(projectId)) throw new AppSystemException("ProjectId Can't be empty");
		try (DatabaseWrapper db = new DatabaseWrapper()) {
			Result rs = SqlOperator.queryResult(db, "SELECT * FROM keytable_snowflake WHERE project_id = ?",
					projectId);
			if (rs == null || rs.getRowCount() != 1) {
				throw new AppSystemException("DB data select exception: keytable_snowflake select fail!");
			}
			Integer datacenterId = rs.getInteger(0, "datacenter_id");
			Integer machineId = rs.getInteger(0, "machine_id");
			idgen = new SnowflakeImpl(datacenterId, machineId);
		} catch (Exception e) {
			throw new AppSystemException("DB data select exception: keytable_snowflake select fail!");
		}
	}

	public static Long getNextId() {
		return idgen.nextId();
	}

	/**
	 * 生成6位随机数
	 *
	 * @return
	 */
	public static String getRandomStr() {

		Random r = new Random();
		long i = r.nextInt(100000);
		long number = i + 900000L;
		return Long.toString(number);
	}

	public static String getRandomTime() {

		return getRandomStr() + DateUtil.getSysTime();
	}

	/**
	 * 获取操作员ID
	 *
	 * @return
	 */
	public static String getOperId() {

		KeyGenerator keygen = KeyGenerator.getInstance();
		long val = keygen.getNextKey("tellers");
		long number = 5000L + val;
		StringBuffer str = new StringBuffer();
		str.append(number);
		return str.toString();
	}

	/**
	 * 获取工作组id和角色id
	 *
	 * @return
	 */
	public static String getRole() {

		KeyGenerator keygen = KeyGenerator.getInstance();
		long roleid = keygen.getNextKey("roleid"); //从KeyTable生成组编码
		long number = 100L + roleid;
		StringBuffer str = new StringBuffer();
		str.append(number);
		return str.toString();
	}


	private static String getMACAddress() throws Exception {
		InetAddress ia = InetAddress.getLocalHost();
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		return sb.toString().toUpperCase();
	}
}
