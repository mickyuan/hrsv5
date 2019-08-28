package hrds.codes;
/**Created by automatic  */
/**代码类型名：需提取数据项类别  */
public enum NeedType {
	/**单项数据<DanXiangShuJu>  */
	DanXiangShuJu("0","单项数据","24"),
	/**列表项数据<LieBiaoXiangShuJu>  */
	LieBiaoXiangShuJu("1","列表项数据","24");

	private final String code;
	private final String value;
	private final String catCode;

	NeedType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
