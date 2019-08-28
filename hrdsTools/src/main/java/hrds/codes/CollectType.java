package hrds.codes;
/**Created by automatic  */
/**代码类型名：采集类型  */
public enum CollectType {
	/**数据库采集<ShuJuKuCaiJi>  */
	ShuJuKuCaiJi("1","数据库采集","13"),
	/**文件采集<WenJianCaiJi>  */
	WenJianCaiJi("2","文件采集","13"),
	/**数据文件采集<DBWenJianCaiJi>  */
	DBWenJianCaiJi("3","数据文件采集","13"),
	/**对象文件采集<DuiXiangWenJianCaiJi>  */
	DuiXiangWenJianCaiJi("4","对象文件采集","13"),
	/**Ftp采集<FtpCaiJi>  */
	FtpCaiJi("5","Ftp采集","13");

	private final String code;
	private final String value;
	private final String catCode;

	CollectType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
