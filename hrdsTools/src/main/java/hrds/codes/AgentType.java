package hrds.codes;
/**Created by automatic  */
/**代码类型名：Agent类别  */
public enum AgentType {
	/**数据库Agent<ShuJuKu>  */
	ShuJuKu("1","数据库Agent","5"),
	/**文件系统Agent<WenJianXiTong>  */
	WenJianXiTong("2","文件系统Agent","5"),
	/**FtpAgent<FTP>  */
	FTP("3","FtpAgent","5"),
	/**数据文件Agent<DBWenJian>  */
	DBWenJian("4","数据文件Agent","5"),
	/**对象Agent<DuiXiang>  */
	DuiXiang("5","对象Agent","5");

	private final String code;
	private final String value;
	private final String catCode;

	AgentType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
