package hrds.codes;
/**Created by automatic  */
/**代码类型名：ETL主服务器同步  */
public enum Main_Server_Sync {
	/**锁定<LOCK>  */
	LOCK("L","锁定","111"),
	/**不同步<NO>  */
	NO("N","不同步","111"),
	/**同步<YES>  */
	YES("Y","同步","111"),
	/**备份中<BACKUP>  */
	BACKUP("B","备份中","111");

	private final String code;
	private final String value;
	private final String catCode;

	Main_Server_Sync(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
