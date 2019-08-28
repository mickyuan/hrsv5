package hrds.codes;
/**Created by automatic  */
/**代码类型名：流数据管理druid服务类型  */
public enum SdmDruidServerType {
	/**索引服务<IndexingServer>  */
	IndexingServer("1","索引服务","136"),
	/**实时服务<realtimeServer>  */
	realtimeServer("2","实时服务","136");

	private final String code;
	private final String value;
	private final String catCode;

	SdmDruidServerType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
