package hrds.codes;
/**Created by automatic  */
/**代码类型名：可视化数据操作符  */
public enum AutoDataOperator {
	/**介于<JieYu>  */
	JieYu("01","介于","159"),
	/**不介于<BuJieYu>  */
	BuJieYu("02","不介于","159"),
	/**等于<DengYu>  */
	DengYu("03","等于","159"),
	/**不等于<BuDengYu>  */
	BuDengYu("04","不等于","159"),
	/**大于<DaYu>  */
	DaYu("05","大于","159"),
	/**小于<XiaoYu>  */
	XiaoYu("06","小于","159"),
	/**大于等于<DaYuDengYu>  */
	DaYuDengYu("07","大于等于","159"),
	/**小于等于<XiaoYuDengYu>  */
	XiaoYuDengYu("08","小于等于","159"),
	/**最大的N个<ZuiDaDeNGe>  */
	ZuiDaDeNGe("09","最大的N个","159"),
	/**最小的N个<ZuiXiaoDeNGe>  */
	ZuiXiaoDeNGe("10","最小的N个","159"),
	/**为空<WeiKong>  */
	WeiKong("11","为空","159"),
	/**非空<FeiKong>  */
	FeiKong("12","非空","159");

	private final String code;
	private final String value;
	private final String catCode;

	AutoDataOperator(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
