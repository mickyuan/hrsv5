package hrds.codes;
/**Created by automatic  */
/**代码类型名：归一化范围  */
public enum NormalizedRange {
	/**0至1范围<LingZhiYiFanWei>  */
	LingZhiYiFanWei("1","0至1范围","55"),
	/**-1至1范围<FuYiZhiYiFanWei>  */
	FuYiZhiYiFanWei("2","-1至1范围","55");

	private final String code;
	private final String value;
	private final String catCode;

	NormalizedRange(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
