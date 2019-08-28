package hrds.codes;
/**Created by automatic  */
/**代码类型名：停止抓取条件  */
public enum StopConditions {
	/**按网页数量停止抓取<AnShuLiang>  */
	AnShuLiang("1","按网页数量停止抓取","20"),
	/**按下载量停止抓取<AnXiaZaiLiang>  */
	AnXiaZaiLiang("2","按下载量停止抓取","20"),
	/**按时间停止抓取<AnShiJian>  */
	AnShiJian("3","按时间停止抓取","20");

	private final String code;
	private final String value;
	private final String catCode;

	StopConditions(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
