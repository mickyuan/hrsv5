package hrds.commons.utils.ocr;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import hrds.commons.utils.PropertyParaValue;
import org.apache.commons.io.FileUtils;
import org.apache.xmlrpc.XmlRpcException;


public class PictureSeach extends PictureServer implements PictureTextInterface {

	private final String hexStr = "0123456789ABCDEF";

	private final String[] binaryArray = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100",
					"1101", "1110", "1111" };
	//设置配置
	private final static String PIC_SERVER_ADDRESS = PropertyParaValue.getString("pic_rpc_cpu", "");

	public PictureSeach() {
		super(PIC_SERVER_ADDRESS);
	}

	public String extractText(String filePath) {

		//测试调用三种方法
		String question = null;
		try {
			Image image = ImageIO.read(FileUtils.getFile(filePath));
			if( image == null ) {
				return question;
			}
			OrcPictureText pic = new OrcPictureText(filePath);
			//1 指定调用的方法chinese_ocr
			question = byteToStr(pic.readPicture());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return question;
	}

	public String byteToStr(byte[] bytes) {

		String question = null;
		try {
			Object[] params = new Object[] { bytes };
			question = (String)client.execute("pHash_encode", params);
		}
		catch(XmlRpcException e) {
			e.printStackTrace();
		}
		return question;
	}

	/**
	 * 
	 * @Title: getDistance
	 * @Description: 获取俩张图片的汉明距离
	 * @date: 2018年11月15日 下午3:53:24
	 * @return: int
	 */
	public int getDistance(String upStr, String picFeature) {

		/*上传的图片的二进制流字符串*/
		byte[] upArr = hexStr2BinArr(upStr);//上传图片的二进制数组
		String upPicterStr = bytes2BinStr(upArr);
		/*上传的图片的二进制流字符串*/
		byte[] sourceBytes = hexStr2BinArr(picFeature);//数据源图片的二进制数组
		String sourceStr = bytes2BinStr(sourceBytes);
		int distance;
		if( upPicterStr.length() != sourceStr.length() ) {
			distance = -1;
		}
		else {
			distance = 0;
			for(int i = 0; i < upPicterStr.length(); i++) {
				if( upPicterStr.charAt(i) != sourceStr.charAt(i) ) {
					distance++;
				}
			}
		}
		return distance;
	}

	/** 
	 *  
	 * @param hexString 
	 * @return 将十六进制转换为二进制字节数组   16-2
	 */
	public byte[] hexStr2BinArr(String hexString) {

		//hexString的长度对2取整，作为bytes的长度  
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;//字节高四位  
		byte low = 0;//字节低四位  
		for(int i = 0; i < len; i++) {
			//右移四位得到高位  
			high = (byte)((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte)hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte)(high | low);//高地位做或运算  
		}
		return bytes;
	}

	/** 
	 *  
	 * @return 将十六进制转换为二进制字符串   16-2
	 */
	public String bytes2BinStr(byte[] bArray) {

		String outStr = "";
		int pos = 0;
		for(byte b : bArray) {
			//高四位  
			pos = (b & 0xF0) >> 4;
			outStr += binaryArray[pos];
			//低四位  
			pos = b & 0x0F;
			outStr += binaryArray[pos];
		}
		return outStr;
	}

	public static void main(String[] args) {

		PictureSeach server = new PictureSeach();
		long start1 = System.currentTimeMillis();
		System.out.println(server.extractText("e:\\aa"));
		long end1 = System.currentTimeMillis();
		System.out.println("1111111==>" + (end1 - start1) / 1000);

		//		System.out.println(bytes2BinStr(hexStr2BinArr("7ff00000ffffe000")));

		int distance = server.getDistance("7ff00000ffffe000", "7ff00000ffffe000");
		System.out.println(distance);
		//		int i = Integer.parseInt("7ff00000ffffe000");
		//		String str2 = Integer.toBinaryString(i);
		//		System.out.println(str2);
	}
}
