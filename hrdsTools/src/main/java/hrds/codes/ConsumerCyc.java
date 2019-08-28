package hrds.codes;
/**Created by automatic  */
/**代码类型名：消费周期  */
public enum ConsumerCyc {
	/**无限期<WuXianQi>  */
	WuXianQi("1","无限期","127"),
	/**按时间结束<AnShiJianJieShu>  */
	AnShiJianJieShu("2","按时间结束","127"),
	/**按数据量结束<AnShuJuLiangJieShu>  */
	AnShuJuLiangJieShu("3","按数据量结束","127");

	private final String code;
	private final String value;
	private final String catCode;

	ConsumerCyc(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
