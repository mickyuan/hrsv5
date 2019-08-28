package hrds.codes;
/**Created by automatic  */
/**代码类型名：压缩范围  */
public enum ReduceScope {
	/**全库压缩<QuanKuYaSuo>  */
	QuanKuYaSuo("1","全库压缩","9"),
	/**按表压缩<AnBiaoYaSuo>  */
	AnBiaoYaSuo("2","按表压缩","9");

	private final String code;
	private final String value;
	private final String catCode;

	ReduceScope(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
