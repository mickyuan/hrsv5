package hrds.commons.codes;
/**Created by automatic  */
/**代码类型名：可视化数据操作符  */
public enum AutoDataOperator {
	/**介于<JieYu>  */
	JieYu("01","介于","159","可视化数据操作符"),
	/**不介于<BuJieYu>  */
	BuJieYu("02","不介于","159","可视化数据操作符"),
	/**等于<DengYu>  */
	DengYu("03","等于","159","可视化数据操作符"),
	/**不等于<BuDengYu>  */
	BuDengYu("04","不等于","159","可视化数据操作符"),
	/**大于<DaYu>  */
	DaYu("05","大于","159","可视化数据操作符"),
	/**小于<XiaoYu>  */
	XiaoYu("06","小于","159","可视化数据操作符"),
	/**大于等于<DaYuDengYu>  */
	DaYuDengYu("07","大于等于","159","可视化数据操作符"),
	/**小于等于<XiaoYuDengYu>  */
	XiaoYuDengYu("08","小于等于","159","可视化数据操作符"),
	/**最大的N个<ZuiDaDeNGe>  */
	ZuiDaDeNGe("09","最大的N个","159","可视化数据操作符"),
	/**最小的N个<ZuiXiaoDeNGe>  */
	ZuiXiaoDeNGe("10","最小的N个","159","可视化数据操作符"),
	/**为空<WeiKong>  */
	WeiKong("11","为空","159","可视化数据操作符"),
	/**非空<FeiKong>  */
	FeiKong("12","非空","159","可视化数据操作符");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AutoDataOperator(String code,String value,String catCode,String catValue){
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
		for (AutoDataOperator typeCode : AutoDataOperator.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static AutoDataOperator getCodeObj(String code) {
		for (AutoDataOperator typeCode : AutoDataOperator.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return AutoDataOperator.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return AutoDataOperator.values()[0].getCatCode();
	}
}
