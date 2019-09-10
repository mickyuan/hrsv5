package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：预测模型类型  */
public enum PredictModelType {
	/**线性模型<XianXingMoXing>  */
	XianXingMoXing("01","线性模型","79","预测模型类型"),
	/**支持向量机-svr<ZhiChiXiangLiangJiSVR>  */
	ZhiChiXiangLiangJiSVR("02","支持向量机-svr","79","预测模型类型"),
	/**时间序列<ShiJianXuLie>  */
	ShiJianXuLie("03","时间序列","79","预测模型类型"),
	/**随机森林回归<SuiJiSenLinHuiGui>  */
	SuiJiSenLinHuiGui("04","随机森林回归","79","预测模型类型"),
	/**堆叠回归<DuiDieHuiGui>  */
	DuiDieHuiGui("05","堆叠回归","79","预测模型类型"),
	/**梯度增强回归树<TiDuZengQiangHuiGuiShu>  */
	TiDuZengQiangHuiGuiShu("06","梯度增强回归树","79","预测模型类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	PredictModelType(String code,String value,String catCode,String catValue){
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
		for (PredictModelType typeCode : PredictModelType.values()) {
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
	public static PredictModelType getCodeObj(String code) {
		for (PredictModelType typeCode : PredictModelType.values()) {
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
	public static String getObjCatValue(){
		return PredictModelType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return PredictModelType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
