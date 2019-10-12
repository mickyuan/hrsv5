package hrds.commons.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.ReduceType;
import hrds.commons.exception.BusinessException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * 解压缩文件的工具类
 * date: 2019/10/12 14:29
 * author: zxz
 */
public class DeCompressionUtil {
	//打印日志
	private static final Log log = LogFactory.getLog(DeCompressionUtil.class);

	//私有化构造方法
	private DeCompressionUtil() {
	}

	/**
	 * 将文件根据对应的解压缩方式进行解压
	 * <p>
	 * 1.判断文件是否存在，不存在直接返回false
	 * 2.判断解压缩方式是否为空，为空则默认使用Gzip进行解压
	 * 3.不为空则根据对应的方式进行解压，对应不上抛异常
	 * 4.将解压后的文件重新写出来
	 *
	 * @param filePath      String
	 *                      含义：压缩文件的全路径
	 *                      取值范围：不能为空
	 * @param deCompressWay String
	 *                      含义：解压缩方式
	 *                      取值范围：可以为空
	 * @return boolean
	 * 含义：解压缩成功或失败的判断
	 * 取值范围：不会为空
	 */
	public static boolean deCompression(String filePath, String deCompressWay) {
		//1.判断文件是否存在，不存在直接返回false
		File input = new File(filePath);
		if (!input.exists()) {
			log.info("文件：" + filePath + "不存在！！！");
			return false;
		}
		String dir = FilenameUtils.getFullPathNoEndSeparator(filePath);
		InputStream in = null;
		ArchiveInputStream tin = null;
		try {
			//2.判断解压缩方式是否为空，为空则默认使用Gzip进行解压
			if (StringUtil.isBlank(deCompressWay)) {
				in = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(input)), true);
			} else if (ReduceType.TAR.getCode().equals(deCompressWay)) {
				in = new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			} else if (ReduceType.GZ.getCode().equals(deCompressWay)) {
				in = new GzipCompressorInputStream(new BufferedInputStream(new FileInputStream(input)), true);
			} else if (ReduceType.ZIP.getCode().equals(deCompressWay)) {
				in = new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(filePath)));
				//3.不为空则根据对应的方式进行解压，对应不上抛异常
			} else {
				throw new BusinessException("无法识别压缩格式：" + deCompressWay);
			}
			tin = new TarArchiveInputStream(in);
			ArchiveEntry entry = tin.getNextEntry();
			while (entry != null) {
				File archiveEntryDir = new File(dir, entry.getName());
				if (!new File(archiveEntryDir.getParent()).exists()) {
					if (!archiveEntryDir.getParentFile().mkdirs()) {
						throw new BusinessException("创建文件夹失败");
					}
				}
				if (entry.isDirectory()) {
					if (!archiveEntryDir.exists()) {
						if (!archiveEntryDir.mkdir()) {
							throw new BusinessException("创建文件夹失败");
						}
					}
					entry = tin.getNextEntry();
					continue;
				}
				//4.将解压后的文件重新写出来
				OutputStream out = new BufferedOutputStream(new FileOutputStream(archiveEntryDir));
				IOUtils.copy(tin, out);
			}
			return true;
		} catch (Exception e) {
			log.error("解压文件： " + filePath + " 失败！！！", e);
			return false;
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != tin) {
					tin.close();
				}
			} catch (IOException e) {
				log.error("关闭流失败", e);
			}
		}
	}

	public static void main(String[] args) {

		deCompression("H:\\cao\\04测试.zip", ReduceType.TAR.getCode());
	}

}
