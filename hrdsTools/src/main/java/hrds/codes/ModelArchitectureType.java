package hrds.codes;
/**Created by automatic  */
/**代码类型名：模型体系结构类型  */
public enum ModelArchitectureType {
	/**自动体系结构选择<ZiDongTiXiJieGouXuanZe>  */
	ZiDongTiXiJieGouXuanZe("1","自动体系结构选择","64"),
	/**自定义体系结构<ZiDingYiTiXiJieGou>  */
	ZiDingYiTiXiJieGou("2","自定义体系结构","64");

	private final String code;
	private final String value;
	private final String catCode;

	ModelArchitectureType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
