package hrds.codes;
/**Created by automatic  */
/**代码类型名：储存方式  */
public enum StorageType {
	/**增量<ZengLiang>  */
	ZengLiang("1","增量","25"),
	/**追加<ZhuiJia>  */
	ZhuiJia("2","追加","25"),
	/**替换<TiHuan>  */
	TiHuan("3","替换","25");

	private final String code;
	private final String value;
	private final String catCode;

	StorageType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
