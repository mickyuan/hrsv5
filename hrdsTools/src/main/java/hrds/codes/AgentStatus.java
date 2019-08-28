package hrds.codes;
/**Created by automatic  */
/**代码类型名：Agent状态  */
public enum AgentStatus {
	/**已连接<YiLianJie>  */
	YiLianJie("1","已连接","4"),
	/**未连接<WeiLianJie>  */
	WeiLianJie("2","未连接","4"),
	/**正在运行<ZhengZaiYunXing>  */
	ZhengZaiYunXing("3","正在运行","4");

	private final String code;
	private final String value;
	private final String catCode;

	AgentStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
