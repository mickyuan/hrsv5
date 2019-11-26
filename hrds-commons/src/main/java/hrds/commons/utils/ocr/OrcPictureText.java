package hrds.commons.utils.ocr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

/**
 * 

 * @ClassName: Picture

 * @Description: 调用RPC的ORC图片识别

 * @author: win

 * @date: 2018年10月23日 下午2:42:53
 */
public class OrcPictureText implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url = null;

	public OrcPictureText(String url) {
		this.url = url;
	}

	public byte[] readPicture() {

		File f = new File(url);
		BufferedInputStream bis = null;
		byte[] b = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(f));
			b = new byte[bis.available()];

			bis.read(b);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if( bis != null ) {
					bis.close();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return b;
	}
	
	public byte[] readPicture(File file) {

		BufferedInputStream bis = null;
		byte[] b = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
			b = new byte[bis.available()];

			bis.read(b);
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if( bis != null ) {
					bis.close();
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		return b;
	}
}
