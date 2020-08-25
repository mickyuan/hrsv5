package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：可视化数据操作符  */
public enum AutoDataOperator {
	/**介于<JieYu>  */
	JieYu("01","介于","115","可视化数据操作符"),
	/**不介于<BuJieYu>  */
	BuJieYu("02","不介于","115","可视化数据操作符"),
	/**等于<DengYu>  */
	DengYu("03","等于","115","可视化数据操作符"),
	/**不等于<BuDengYu>  */
	BuDengYu("04","不等于","115","可视化数据操作符"),
	/**大于<DaYu>  */
	DaYu("05","大于","115","可视化数据操作符"),
	/**小于<XiaoYu>  */
	XiaoYu("06","小于","115","可视化数据操作符"),
	/**大于等于<DaYuDengYu>  */
	DaYuDengYu("07","大于等于","115","可视化数据操作符"),
	/**小于等于<XiaoYuDengYu>  */
	XiaoYuDengYu("08","小于等于","115","可视化数据操作符"),
	/**最大的N个<ZuiDaDeNGe>  */
	ZuiDaDeNGe("09","最大的N个","115","可视化数据操作符"),
	/**最小的N个<ZuiXiaoDeNGe>  */
	ZuiXiaoDeNGe("10","最小的N个","115","可视化数据操作符"),
	/**为空<WeiKong>  */
	WeiKong("11","为空","115","可视化数据操作符"),
	/**非空<FeiKong>  */
	FeiKong("12","非空","115","可视化数据操作符");

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
	public static final String CodeName = "AutoDataOperator";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (AutoDataOperator typeCode : AutoDataOperator.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode.value;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**根据指定的代码值转换成对象
	* @param code   本代码的代码值
	* @return
	*/
	public static AutoDataOperator ofEnumByCode(String code) {
		for (AutoDataOperator typeCode : AutoDataOperator.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new AppSystemException("根据"+code+"没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String ofCatValue(){
		return AutoDataOperator.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return AutoDataOperator.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	@Override
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
