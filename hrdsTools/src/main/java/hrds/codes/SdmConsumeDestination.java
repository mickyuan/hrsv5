package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理消费端目的地  */
public enum SdmConsumeDestination {
	/**数据库<ShuJuKu>  */
	ShuJuKu("1","数据库","104"),
	/**hbase<Hbase>  */
	Hbase("2","hbase","104"),
	/**rest服务<RestFuWu>  */
	RestFuWu("3","rest服务","104"),
	/**文件<LiuWenJian>  */
	LiuWenJian("4","文件","104"),
	/**二进制文件<ErJinZhiWenJian>  */
	ErJinZhiWenJian("5","二进制文件","104"),
	/**Kafka<Kafka>  */
	Kafka("6","Kafka","104"),
	/**自定义业务类<ZiDingYeWuLei>  */
	ZiDingYeWuLei("7","自定义业务类","104");

	private final String code;
	private final String value;
	private final String catCode;

	SdmConsumeDestination(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
