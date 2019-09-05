package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：海云内部消费目的地  */
public enum HyrenConsumeDes {
	/**hbase<HBASE>  */
	HBASE("1","hbase","128","海云内部消费目的地"),
	/**mpp<MPP>  */
	MPP("2","mpp","128","海云内部消费目的地"),
	/**hbaseOnSolr<HBASEONSOLR>  */
	HBASEONSOLR("3","hbaseOnSolr","128","海云内部消费目的地"),
	/**hdfs<HDFS>  */
	HDFS("4","hdfs","128","海云内部消费目的地"),
	/**时序数据库<Druid>  */
	Druid("5","时序数据库","128","海云内部消费目的地"),
	/**sparkd<SPARKD>  */
	SPARKD("6","sparkd","128","海云内部消费目的地");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	HyrenConsumeDes(String code,String value,String catCode,String catValue){
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
		for (HyrenConsumeDes typeCode : HyrenConsumeDes.values()) {
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
	public static HyrenConsumeDes getCodeObj(String code) {
		for (HyrenConsumeDes typeCode : HyrenConsumeDes.values()) {
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
		return HyrenConsumeDes.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return HyrenConsumeDes.values()[0].getCatCode();
	}
}
