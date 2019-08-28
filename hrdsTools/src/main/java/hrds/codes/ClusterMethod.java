package hrds.codes;
/**Created by automatic  */
/**代码类型名：聚类方法  */
public enum ClusterMethod {
	/**迭代与分类<DieDaiYuFenLei>  */
	DieDaiYuFenLei("1","迭代与分类","61"),
	/**分类<FenLei>  */
	FenLei("2","分类","61");

	private final String code;
	private final String value;
	private final String catCode;

	ClusterMethod(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
