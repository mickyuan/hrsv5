package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：加工层算法类型  */
public enum MachiningAlgType {
	/**F1<F1_DEISERT>  */
	F1_DEISERT("F1","F1","95","加工层算法类型"),
	/**F2<F2_UPSERT>  */
	F2_UPSERT("F2","F2","95","加工层算法类型"),
	/**F3<F3_HISTORYCHAIN>  */
	F3_HISTORYCHAIN("F3","F3","95","加工层算法类型"),
	/**F5<F5_FULLDATAHISTORYCHAIN>  */
	F5_FULLDATAHISTORYCHAIN("F5","F5","95","加工层算法类型"),
	/**I<I_APPEND>  */
	I_APPEND("I","I","95","加工层算法类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	MachiningAlgType(String code,String value,String catCode,String catValue){
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
		for (MachiningAlgType typeCode : MachiningAlgType.values()) {
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
	public static MachiningAlgType getCodeObj(String code) {
		for (MachiningAlgType typeCode : MachiningAlgType.values()) {
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
		return MachiningAlgType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return MachiningAlgType.values()[0].getCatCode();
	}

	/**
	* 禁止使用类的tostring()方法
	* @return
	*/
	public String toString() {
		throw new AppSystemException("There's no need for you to !");
	}
}
