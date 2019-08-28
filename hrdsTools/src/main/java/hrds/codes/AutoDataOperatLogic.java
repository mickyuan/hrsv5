package hrds.codes;
/**Created by automatic  */
/**代码类型名：可视化数据运算逻辑  */
public enum AutoDataOperatLogic {
	/**且<Qie>  */
	Qie("01","且","161"),
	/**或<Huo>  */
	Huo("02","或","161");

	private final String code;
	private final String value;
	private final String catCode;

	AutoDataOperatLogic(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
