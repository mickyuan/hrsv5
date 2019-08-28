package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理分区方式  */
public enum SdmPatitionWay {
	/**随机分布<SuiJiFenBu>  */
	SuiJiFenBu("1","随机分布","99"),
	/**key<Key>  */
	Key("2","key","99"),
	/**分区<FenQu>  */
	FenQu("3","分区","99");

	private final String code;
	private final String value;
	private final String catCode;

	SdmPatitionWay(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
