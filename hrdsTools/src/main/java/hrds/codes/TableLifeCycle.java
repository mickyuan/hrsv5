package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据表生命周期  */
public enum TableLifeCycle {
	/**永久<YongJiu>  */
	YongJiu("1","永久","38"),
	/**临时<LinShi>  */
	LinShi("2","临时","38");

	private final String code;
	private final String value;
	private final String catCode;

	TableLifeCycle(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
