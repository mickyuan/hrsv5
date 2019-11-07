package hrds.b.biz.agent.bean;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "数据库连接URL拼接参数类", author = "WangZhengcheng")
public class DBConnectionProp {

	//JDBCURL串前缀，如jdbc:mysql://
	private String urlPrefix;
	//JDBCURL串拼接数据库连接IP地址的占位符，一般用:，前端直接在冒号后面动态追加用户输入的IP地址
	private String ipPlaceholder;
	//JDBCURL串拼接数据库连接端口号的占位符，一般用/，前端直接在冒号后面动态追加用户输入的端口号
	private String portPlaceholder;
	//JDBCURL串后缀，在连接TeraData，Informatic，MYSQL数据库时，有后缀
	//如：?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull，其他类型的数据库设置这个值为空字符串
	private String urlSuffix;

	public String getUrlPrefix() {
		return urlPrefix;
	}

	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	public String getIpPlaceholder() {
		return ipPlaceholder;
	}

	public void setIpPlaceholder(String ipPlaceholder) {
		this.ipPlaceholder = ipPlaceholder;
	}

	public String getPortPlaceholder() {
		return portPlaceholder;
	}

	public void setPortPlaceholder(String portPlaceholder) {
		this.portPlaceholder = portPlaceholder;
	}

	public String getUrlSuffix() {
		return urlSuffix;
	}

	public void setUrlSuffix(String urlSuffix) {
		this.urlSuffix = urlSuffix;
	}

	@Override
	public String toString() {
		return "URLTemplate{" +
				"urlPrefix='" + urlPrefix + '\'' +
				", ipPlaceholder='" + ipPlaceholder + '\'' +
				", portPlaceholder='" + portPlaceholder + '\'' +
				", urlSuffix='" + urlSuffix + '\'' +
				'}';
	}
}
