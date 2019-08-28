package hrds.codes;
/**Created by automatic  */
/**代码类型名：数据表存储方式  */
public enum TableStorage {
	/**数据表<ShuJuBiao>  */
	ShuJuBiao("0","数据表","39"),
	/**数据视图<ShuJuShiTu>  */
	ShuJuShiTu("1","数据视图","39");

	private final String code;
	private final String value;
	private final String catCode;

	TableStorage(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
