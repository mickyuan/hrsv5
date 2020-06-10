package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：存储层关系-数据来源  */
public enum StoreLayerDataSource {
	/**db采集<DB>  */
	DB("1","db采集","93","存储层关系-数据来源"),
	/**数据库采集<DBA>  */
	DBA("2","数据库采集","93","存储层关系-数据来源"),
	/**对象采集<OBJ>  */
	OBJ("3","对象采集","93","存储层关系-数据来源"),
	/**数据集市<DM>  */
	DM("4","数据集市","93","存储层关系-数据来源"),
	/**数据管控<DQ>  */
	DQ("5","数据管控","93","存储层关系-数据来源");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	StoreLayerDataSource(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "StoreLayerDataSource";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (StoreLayerDataSource typeCode : StoreLayerDataSource.values()) {
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
	public static StoreLayerDataSource ofEnumByCode(String code) {
		for (StoreLayerDataSource typeCode : StoreLayerDataSource.values()) {
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
		return StoreLayerDataSource.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return StoreLayerDataSource.values()[0].getCatCode();
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
