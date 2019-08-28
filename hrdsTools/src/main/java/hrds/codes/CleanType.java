package hrds.codes;
/**Created by automatic  */
/**代码类型名：清洗方式  */
public enum CleanType {
	/**字符补齐<ZiFuBuQi>  */
	ZiFuBuQi("1","字符补齐","36"),
	/**字符替换<ZiFuTiHuan>  */
	ZiFuTiHuan("2","字符替换","36"),
	/**时间转换<ShiJianZhuanHuan>  */
	ShiJianZhuanHuan("3","时间转换","36"),
	/**码值转换<MaZhiZhuanHuan>  */
	MaZhiZhuanHuan("4","码值转换","36"),
	/**字符合并<ZiFuHeBing>  */
	ZiFuHeBing("5","字符合并","36"),
	/**字符拆分<ZiFuChaiFen>  */
	ZiFuChaiFen("6","字符拆分","36"),
	/**字符trim<ZiFuTrim>  */
	ZiFuTrim("7","字符trim","36");

	private final String code;
	private final String value;
	private final String catCode;

	CleanType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
