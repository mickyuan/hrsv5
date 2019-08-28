package hrds.codes;
/**Created by automatic  */
/**代码类型名：缺失值处理方式  */
public enum MissValueApproach {
	/**均值<LinJinDianJunZhi>  */
	LinJinDianJunZhi("1","均值","66"),
	/**中位数<LinJinDianZhongWeiShu>  */
	LinJinDianZhongWeiShu("2","中位数","66"),
	/**线性插值<XianXingChaZhi>  */
	XianXingChaZhi("3","线性插值","66"),
	/**点处的线性趋势<DianShuDeXianXingQuShi>  */
	DianShuDeXianXingQuShi("4","点处的线性趋势","66"),
	/**序列均值<XuLieJunZhi>  */
	XuLieJunZhi("5","序列均值","66"),
	/**删除<ShanChu>  */
	ShanChu("6","删除","66");

	private final String code;
	private final String value;
	private final String catCode;

	MissValueApproach(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
