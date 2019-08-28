package hrds.codes;
/**Created by automatic  */
/**代码类型名：清洗参数属性类型  */
public enum CcParameterType {
	/**特殊字段转义<TeSuZiDuanZhuanYi>  */
	TeSuZiDuanZhuanYi("1","特殊字段转义","23"),
	/**特殊字段去除<TeSuZiDuanQuChu>  */
	TeSuZiDuanQuChu("2","特殊字段去除","23");

	private final String code;
	private final String value;
	private final String catCode;

	CcParameterType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
