package hrds.codes;
/**Created by automatic  */
/**代码类型名：资源管理器文件类型  */
public enum FileType {
	/**全部文件<All>  */
	All("1001","全部文件","22"),
	/**图片<TuPian>  */
	TuPian("1002","图片","22"),
	/**文档<WenDang>  */
	WenDang("1003","文档","22"),
	/**PDF文件<PDFFile>  */
	PDFFile("1013","PDF文件","22"),
	/**office文件<OfficeFile>  */
	OfficeFile("1023","office文件","22"),
	/**文本文件<WenBenFile>  */
	WenBenFile("1033","文本文件","22"),
	/**压缩文件<YaSuoFile>  */
	YaSuoFile("1043","压缩文件","22"),
	/**日志文件<RiZhiFile>  */
	RiZhiFile("1053","日志文件","22"),
	/**表数据文件<biaoShuJuFile>  */
	biaoShuJuFile("1063","表数据文件","22"),
	/**视频<ShiPin>  */
	ShiPin("1004","视频","22"),
	/**音频<YinPin>  */
	YinPin("1005","音频","22"),
	/**其它<Other>  */
	Other("1006","其它","22");

	private final String code;
	private final String value;
	private final String catCode;

	FileType(String code,String value,String catCode){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
}
