package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据申请状态  */
public enum FlowApplyStatus {
	/**未申请<WeiShenQing>  */
	WeiShenQing("1","未申请","146"),
	/**申请中<ShenQingZhong>  */
	ShenQingZhong("2","申请中","146"),
	/**申请通过<ShenQingTongGuo>  */
	ShenQingTongGuo("3","申请通过","146"),
	/**申请不通过<ShenQingBuTongGuo>  */
	ShenQingBuTongGuo("4","申请不通过","146");

	private final String code;
	private final String value;
	private final String catCode;

	FlowApplyStatus(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
