package hrds.commons.codes;

import hrds.commons.exception.AppSystemException;
/**Created by automatic  */
/**代码类型名：hdfs文件类型  */
public enum HdfsFileType {
	/**csv<Csv>  */
	Csv("1","csv","81","hdfs文件类型"),
	/**parquet<Parquet>  */
	Parquet("2","parquet","81","hdfs文件类型"),
	/**avro<Avro>  */
	Avro("3","avro","81","hdfs文件类型"),
	/**orcfile<OrcFile>  */
	OrcFile("4","orcfile","81","hdfs文件类型"),
	/**sequencefile<SequenceFile>  */
	SequenceFile("5","sequencefile","81","hdfs文件类型"),
	/**其他<Other>  */
	Other("6","其他","81","hdfs文件类型");

	private final String code;
	private final String value;
	private final String catCode;
	private final String catValue;

	HdfsFileType(String code,String value,String catCode,String catValue){
		this.code = code;
		this.value = value;
		this.catCode = catCode;
		this.catValue = catValue;
	}
	public String getCode(){return code;}
	public String getValue(){return value;}
	public String getCatCode(){return catCode;}
	public String getCatValue(){return catValue;}
	public static final String CodeName = "HdfsFileType";

	/**根据指定的代码值转换成中文名字
	* @param code   本代码的代码值
	* @return
	*/
	public static String ofValueByCode(String code) {
		for (HdfsFileType typeCode : HdfsFileType.values()) {
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
	public static HdfsFileType ofEnumByCode(String code) {
		for (HdfsFileType typeCode : HdfsFileType.values()) {
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
		return HdfsFileType.values()[0].getCatValue();
	}

	/**
	* 获取代码项的分类代码
	* @return
	*/
	public static String ofCatCode(){
		return HdfsFileType.values()[0].getCatCode();
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
