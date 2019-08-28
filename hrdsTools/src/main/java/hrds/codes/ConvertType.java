package hrds.codes;
/**Created by automatic  */
/**代码类型名：转换类型  */
public enum ConvertType {
	/**不需要转换<BuXuYaoZhuanHuan>  */
	BuXuYaoZhuanHuan("0","不需要转换","94"),
	/**转换为日期<ZhuanHuanWeiRiQi>  */
	ZhuanHuanWeiRiQi("1","转换为日期","94"),
	/**转换为时间<ZhuanHuanWeiShiJian>  */
	ZhuanHuanWeiShiJian("2","转换为时间","94"),
	/**参数转换<CanShuZhuanHuan>  */
	CanShuZhuanHuan("3","参数转换","94"),
	/**格式转换<GeShiZhuanHuan>  */
	GeShiZhuanHuan("4","格式转换","94");

	private final String code;
	private final String value;
	private final String catCode;

	ConvertType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
