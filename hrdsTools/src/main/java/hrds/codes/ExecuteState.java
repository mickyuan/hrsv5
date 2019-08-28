package hrds.codes;
/**Created by automatic  */
/**代码类型名：运行状态  */
public enum ExecuteState {
	/**开始运行<KaiShiYunXing>  */
	KaiShiYunXing("01","开始运行","10"),
	/**运行完成<YunXingWanCheng>  */
	YunXingWanCheng("02","运行完成","10"),
	/**运行失败<YunXingShiBai>  */
	YunXingShiBai("99","运行失败","10"),
	/**通知成功<TongZhiChengGong>  */
	TongZhiChengGong("20","通知成功","10"),
	/**通知失败<TongZhiShiBai>  */
	TongZhiShiBai("21","通知失败","10"),
	/**暂停运行<ZanTingYunXing>  */
	ZanTingYunXing("30","暂停运行","10");

	private final String code;
	private final String value;
	private final String catCode;

	ExecuteState(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
