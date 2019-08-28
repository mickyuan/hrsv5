package hrds.codes;
/**Created by automatic  */
/**代码类型名：可视化数据汇总类型  */
public enum AutoDataSumType {
	/**求和<QiuHe>  */
	QiuHe("01","求和","160"),
	/**求平均<QiuPingJun>  */
	QiuPingJun("02","求平均","160"),
	/**求最大值<QiuZuiDaZhi>  */
	QiuZuiDaZhi("03","求最大值","160"),
	/**求最小值<QiuZuiXiaoZhi>  */
	QiuZuiXiaoZhi("04","求最小值","160"),
	/**总行数<ZongHangShu>  */
	ZongHangShu("05","总行数","160"),
	/**原始数据<YuanShiShuJu>  */
	YuanShiShuJu("06","原始数据","160"),
	/**查看全部<ChaKanQuanBu>  */
	ChaKanQuanBu("07","查看全部","160");

	private final String code;
	private final String value;
	private final String catCode;

	AutoDataSumType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
