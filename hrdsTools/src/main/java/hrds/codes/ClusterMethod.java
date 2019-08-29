package hrds.codes;
/**Created by automatic  */
/**代码类型名：聚类方法  */
public enum ClusterMethod {
	/**迭代与分类<DieDaiYuFenLei>  */
	DieDaiYuFenLei("1","迭代与分类","61","聚类方法"),
	/**分类<FenLei>  */
	FenLei("2","分类","61","聚类方法");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	ClusterMethod(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String getValue(String code) {
		for (ClusterMethod typeCode : ClusterMethod.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		return null;
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static ClusterMethod getCodeObj(String code) {
		for (ClusterMethod typeCode : ClusterMethod.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		return null;
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return ClusterMethod.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return ClusterMethod.values()[0].getCatCode();
	}
}
