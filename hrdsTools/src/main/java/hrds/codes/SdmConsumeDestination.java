package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理消费端目的地  */
public enum SdmConsumeDestination {
	/**数据库<ShuJuKu>  */
	ShuJuKu("1","数据库","104","流数据管理消费端目的地"),
	/**hbase<Hbase>  */
	Hbase("2","hbase","104","流数据管理消费端目的地"),
	/**rest服务<RestFuWu>  */
	RestFuWu("3","rest服务","104","流数据管理消费端目的地"),
	/**文件<LiuWenJian>  */
	LiuWenJian("4","文件","104","流数据管理消费端目的地"),
	/**二进制文件<ErJinZhiWenJian>  */
	ErJinZhiWenJian("5","二进制文件","104","流数据管理消费端目的地"),
	/**Kafka<Kafka>  */
	Kafka("6","Kafka","104","流数据管理消费端目的地"),
	/**自定义业务类<ZiDingYeWuLei>  */
	ZiDingYeWuLei("7","自定义业务类","104","流数据管理消费端目的地");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	SdmConsumeDestination(String code,String value,String catCode,String catValue){
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
		for (SdmConsumeDestination typeCode : SdmConsumeDestination.values()) {
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
	public static SdmConsumeDestination getCodeObj(String code) {
		for (SdmConsumeDestination typeCode : SdmConsumeDestination.values()) {
			if (typeCode.getCode().equals(code)) {
				return typeCode;
			}
		}
		throw new RuntimeException("根据code没有找到对应的代码项");
	}

	/**
	* 获取代码项的中文类名名称
	* @return
	*/
	public static String getObjCatValue(){
		return SdmConsumeDestination.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String getObjCatCode(){
		return SdmConsumeDestination.values()[0].getCatCode();
	}
}
