package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：存储层附件属性  */
public enum StoreLayerAdded {
	/**主键<ZhuJian>  */
	ZhuJian("01","主键","62","存储层附件属性"),
	/**rowkey<RowKey>  */
	RowKey("02","rowkey","62","存储层附件属性"),
	/**索引列<SuoYinLie>  */
	SuoYinLie("03","索引列","62","存储层附件属性"),
	/**预聚合列<YuJuHe>  */
	YuJuHe("04","预聚合列","62","存储层附件属性"),
	/**排序列<PaiXuLie>  */
	PaiXuLie("05","排序列","62","存储层附件属性"),
	/**分区列<FenQuLie>  */
	FenQuLie("06","分区列","62","存储层附件属性");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	StoreLayerAdded(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "StoreLayerAdded";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (StoreLayerAdded typeCode : StoreLayerAdded.values()) {
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
	public static StoreLayerAdded ofEnumByCode(String code) {
		for (StoreLayerAdded typeCode : StoreLayerAdded.values()) {
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
		return StoreLayerAdded.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return StoreLayerAdded.values()[0].getCatCode();
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
