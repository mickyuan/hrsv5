package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：缺失值处理方式  */
public enum MissValueApproach {
	/**均值<LinJinDianJunZhi>  */
	LinJinDianJunZhi("1","均值","66","缺失值处理方式"),
	/**中位数<LinJinDianZhongWeiShu>  */
	LinJinDianZhongWeiShu("2","中位数","66","缺失值处理方式"),
	/**线性插值<XianXingChaZhi>  */
	XianXingChaZhi("3","线性插值","66","缺失值处理方式"),
	/**点处的线性趋势<DianShuDeXianXingQuShi>  */
	DianShuDeXianXingQuShi("4","点处的线性趋势","66","缺失值处理方式"),
	/**序列均值<XuLieJunZhi>  */
	XuLieJunZhi("5","序列均值","66","缺失值处理方式"),
	/**删除<ShanChu>  */
	ShanChu("6","删除","66","缺失值处理方式");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	MissValueApproach(String code,String value,String catCode,String catValue){
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
		for (MissValueApproach typeCode : MissValueApproach.values()) {
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
	public static MissValueApproach getCodeObj(String code) {
		for (MissValueApproach typeCode : MissValueApproach.values()) {
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
		return MissValueApproach.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return MissValueApproach.values()[0].getCatCode();
	}
}
