package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：存储层类型  */
public enum Store_type {
	/**关系型数据库<DATABASE>  */
	DATABASE("1","关系型数据库","59","存储层类型"),
	/**hive<HIVE>  */
	HIVE("2","hive","59","存储层类型"),
	/**Hbase<HBASE>  */
	HBASE("3","Hbase","59","存储层类型"),
	/**solr<SOLR>  */
	SOLR("4","solr","59","存储层类型"),
	/**ElasticSearch<ElasticSearch>  */
	ElasticSearch("5","ElasticSearch","59","存储层类型"),
	/**mongodb<MONGODB>  */
	MONGODB("6","mongodb","59","存储层类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	Store_type(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "Store_type";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (Store_type typeCode : Store_type.values()) {
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
	public static Store_type ofEnumByCode(String code) {
		for (Store_type typeCode : Store_type.values()) {
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
		return Store_type.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return Store_type.values()[0].getCatCode();
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
