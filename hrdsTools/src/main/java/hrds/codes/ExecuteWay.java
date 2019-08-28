package hrds.codes;
/**Created by automatic  */
/**代码类型名：启动方式  */
public enum ExecuteWay {
	/**按时启动<AnShiQiDong>  */
	AnShiQiDong("1","按时启动","8"),
	/**命令触发<MingLingChuFa>  */
	MingLingChuFa("2","命令触发","8"),
	/**信号文件触发<QianZhiTiaoJian>  */
	QianZhiTiaoJian("3","信号文件触发","8");

	private final String code;
	private final String value;
	private final String catCode;

	ExecuteWay(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
