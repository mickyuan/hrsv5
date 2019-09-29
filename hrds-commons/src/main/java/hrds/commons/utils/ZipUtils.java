package hrds.commons.utils;

import fd.ng.core.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.*;


public class ZipUtils {

	private static final Log logger = LogFactory.getLog(ZipUtils.class);

	/**
	 * 使用gzip进行压缩
	 */
	public static String gzip(String primStr) {

		if (StringUtil.isEmpty(primStr)) {
			return primStr;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (GZIPOutputStream gzip = new GZIPOutputStream(out);) {
			gzip.write(primStr.getBytes());
		} catch (IOException e) {
			logger.debug(e.getMessage());
			return null;
		}
		return Base64.getEncoder().encodeToString(out.toByteArray());
	}

	/**
	 * 使用gzip进行解压缩
	 *
	 * @param compressedStr
	 * @return
	 */
	public static String gunzip(String compressedStr) {

		if (StringUtil.isEmpty(compressedStr)) {
			return compressedStr;
		}
		byte[] compressed = Base64.getDecoder().decode(compressedStr);
		String decompressed = null;
		try (ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		     GZIPInputStream ginzip = new GZIPInputStream(in);
		     ByteArrayOutputStream out = new ByteArrayOutputStream()
		) {

			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = ginzip.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();
		} catch (IOException e) {
			logger.debug(e.getMessage());
			return null;
		}
		return decompressed;
	}

	/**
	 * 使用zip进行压缩
	 *
	 * @param str 压缩前的文本
	 * @return 返回压缩后的文本
	 */
	public static final String zip(String str) {

		if (StringUtil.isEmpty(str)) {
			return str;
		}
		byte[] compressed;
		String compressedStr = null;
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();ZipOutputStream zout = new ZipOutputStream(out);){
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes());
			zout.closeEntry();
			compressed = out.toByteArray();
			compressedStr = Base64.getEncoder().encodeToString(compressed);
		} catch (IOException e) {
			logger.debug(e.getMessage());
			return null;
		}
		return compressedStr;
	}

	/**
	 * 使用zip进行解压缩
	 *
	 * @return 解压后的字符串
	 */
	public static final String unzip(String compressedStr) {

		if (StringUtil.isEmpty(compressedStr)) {
			return compressedStr;
		}
		String decompressed = null;
		byte[] compressed = Base64.getDecoder().decode(compressedStr);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
		     ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		     ZipInputStream zin = new ZipInputStream(in);){
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			decompressed = out.toString();

		} catch (IOException e) {
			logger.debug(e.getMessage());
			return null;
		}
		return decompressed;
	}
}