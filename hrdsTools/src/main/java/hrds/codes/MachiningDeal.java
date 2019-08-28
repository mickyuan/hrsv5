package hrds.codes;
/**Created by automatic  */
/**代码类型名：加工层字段处理方式  */
public enum MachiningDeal {
	/**映射<YINGSHE>  */
	YINGSHE("1","映射","96"),
	/**规则mapping<GUIZEMAPPING>  */
	GUIZEMAPPING("2","规则mapping","96"),
	/**去空<QUKONG>  */
	QUKONG("3","去空","96"),
	/**映射并添加<YINGSHEBINGTIANJIA>  */
	YINGSHEBINGTIANJIA("4","映射并添加","96");

	private final String code;
	private final String value;
	private final String catCode;

	MachiningDeal(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
