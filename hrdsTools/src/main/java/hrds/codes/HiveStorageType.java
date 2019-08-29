package hrds.codes;
/**Created by automatic  */
/**代码类型名：hive文件存储类型  */
public enum HiveStorageType {
	/**TEXTFILE<TEXTFILE>  */
	TEXTFILE("1","TEXTFILE","131","hive文件存储类型"),
	/**SEQUENCEFILE<SEQUENCEFILE>  */
	SEQUENCEFILE("2","SEQUENCEFILE","131","hive文件存储类型"),
	/**PARQUET<PARQUET>  */
	PARQUET("3","PARQUET","131","hive文件存储类型"),
	/**CSV<CSV>  */
	CSV("4","CSV","131","hive文件存储类型"),
	/**ORC<ORC>  */
	ORC("5","ORC","131","hive文件存储类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	HiveStorageType(String code,String value,String catCode,String catValue){
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
		for (HiveStorageType typeCode : HiveStorageType.values()) {
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
	public static HiveStorageType getCodeObj(String code) {
		for (HiveStorageType typeCode : HiveStorageType.values()) {
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
		return HiveStorageType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return HiveStorageType.values()[0].getCatCode();
	}
}
