package hrds.codes;
/**Created by automatic  */
/**代码类型名：聚类模型类型  */
public enum ClusterModelType {
	/**K-means<KMeans>  */
	KMeans("01","K-means","62");

	private final String code;
	private final String value;
	private final String catCode;

	ClusterModelType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
