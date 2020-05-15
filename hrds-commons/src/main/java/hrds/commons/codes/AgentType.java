package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：Agent类别  */
public enum AgentType {
	/**数据库Agent<ShuJuKu>  */
	ShuJuKu("1","数据库Agent","35","Agent类别"),
	/**文件系统Agent<WenJianXiTong>  */
	WenJianXiTong("2","文件系统Agent","35","Agent类别"),
	/**FtpAgent<FTP>  */
	FTP("3","FtpAgent","35","Agent类别"),
	/**数据文件Agent<DBWenJian>  */
	DBWenJian("4","数据文件Agent","35","Agent类别"),
	/**对象Agent<DuiXiang>  */
	DuiXiang("5","对象Agent","35","Agent类别");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	AgentType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "AgentType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (AgentType typeCode : AgentType.values()) {
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
	public static AgentType ofEnumByCode(String code) {
		for (AgentType typeCode : AgentType.values()) {
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
		return AgentType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return AgentType.values()[0].getCatCode();
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
