package hrds.codes;
/**Created by automatic  */
/**代码类型名：ftp目录规则  */
public enum FtpRule {
	/**流水号<LiuShuiHao>  */
	LiuShuiHao("1","流水号","125"),
	/**固定目录<GuDingMuLu>  */
	GuDingMuLu("2","固定目录","125"),
	/**按时间<AnShiJian>  */
	AnShiJian("3","按时间","125");

	private final String code;
	private final String value;
	private final String catCode;

	FtpRule(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
