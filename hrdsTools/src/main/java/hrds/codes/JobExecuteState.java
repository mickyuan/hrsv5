package hrds.codes;
/**Created by automatic  */
/**代码类型名：作业运行状态  */
public enum JobExecuteState {
	/**等待<DengDai>  */
	DengDai("100","等待","42"),
	/**运行<YunXing>  */
	YunXing("101","运行","42"),
	/**暂停<ZanTing>  */
	ZanTing("102","暂停","42"),
	/**中止<ZhongZhi>  */
	ZhongZhi("103","中止","42"),
	/**完成<WanCheng>  */
	WanCheng("104","完成","42"),
	/**失败<ShiBai>  */
	ShiBai("105","失败","42");

	private final String code;
	private final String value;
	private final String catCode;

	JobExecuteState(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
