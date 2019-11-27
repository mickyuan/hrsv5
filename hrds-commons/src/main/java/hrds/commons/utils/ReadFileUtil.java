package hrds.commons.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.commons.utils.ocr.PictureTextExtract;
import info.monitorenter.cpdetector.io.*;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;

@DocClass(desc = "提取文本文件的文本内容的工具类", author = "zxz", createdate = "2019/11/4 16:04")
public class ReadFileUtil {

	//打印日志
	private static final Log logger = LogFactory.getLog(ReadFileUtil.class);
	//获取文件编码的对象
	private static final CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();

	static {
		// 可用于检查HTML、XML等文件或字符流的编码
		detector.add(new ParsingDetector(false));
		// 可满足大多数检测需求
		detector.add(JChardetFacade.getInstance());
		// ASCIIDetector用于ASCII编码测定
		detector.add(ASCIIDetector.getInstance());
		// UnicodeDetector用于Unicode家族编码的测定
		detector.add(UnicodeDetector.getInstance());
	}

	@Method(desc = "根据文件获取文件的编码", logicStep = "调用api获取文件编码")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件编码的字符串", range = "不会为空")
	public static String detectCode(File file) {
		try {
			return detector.detectCodepage(file.toURI().toURL()).name();
		} catch (Exception e) {
			logger.error(e);
			return Charset.defaultCharset().name();
		}
	}

	@Method(desc = "读取xls和xlsx文件的文本内容", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String readExcel(File file) {
		StringBuilder sb = new StringBuilder(120);
		Sheet sheet;
		try (InputStream ins = new FileInputStream(file); Workbook wb = WorkbookFactory.create(ins)) {
			for (int i = 0; i < wb.getNumberOfSheets(); i++) {
				// 得到Excel工作表对象
				sheet = wb.getSheetAt(i);
				// 总行数
				int trNumber = sheet.getLastRowNum() + 1;
				// 得到Excel工作表指定行的单元格
				if (trNumber == 0)
					System.exit(0);
				for (int j = 0; j < trNumber; j++) {
					// 得到Excel工作表的行
					Row row1 = sheet.getRow(j);
					// 该行为空则忽略
					if (row1 == null)
						continue;
					int tdNumber = row1.getLastCellNum();
					// 对每行的每列进行处理
					for (int k = 0; k < tdNumber; k++) {
						// 得到该行的每列的单元格
						Cell cell1 = row1.getCell(k);
						/*
						 * 为了处理：Excel异常Cannot get a text value from a numeric cell
						 * 将所有列中的内容都设置成String类型格式
						 */
						if (cell1 != null) {
							// TODO Use CellType.STRING replace Cell.CELL_TYPE_STRING after Hadoop 3.0
							cell1.setCellType(Cell.CELL_TYPE_STRING);
						}
						// 打印所有值
						sb.append(cell1);
					}
				}
			}
			return sb.toString().replace("null", "");
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			return "";
		}
	}

	@Method(desc = "提取html文件的文本", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String readHtml(File file) {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), detectCode(file)))) {
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			return sb.toString();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	@Method(desc = "提取pdf文本", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String getPdf(File file) {

		boolean sort = true;
		int startPage = 1;
		int endPage = 10;
		try (PDDocument document = PDDocument.load(file)) {
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setSortByPosition(sort);
			stripper.setStartPage(startPage);
			stripper.setEndPage(endPage);
			return stripper.getText(document);
		} catch (Exception e) {
			return "";
		}
	}

	@Method(desc = "读取文本内容", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String readText(File file) {
		StringBuilder sub = new StringBuilder();
		String code = detectCode(file);
		if ("UTF-16LE".equals(code) || "UTF-18".equals(code)) {
			code = "GBK";
		}
		try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), code))) {
			String str;
			while ((str = bufReader.readLine()) != null) {
				sub.append(str);
			}
			return convertSymbol(sub.toString());
		} catch (Exception e) {
			return "";
		}
	}

	@Method(desc = "处理特殊字符", logicStep = "")
	@Param(name = "docText", desc = "文本字符串", range = "不可为空")
	@Return(desc = "处理特殊字符完特殊字符的字符串", range = "不会为空")
	public static String convertSymbol(String docText) {
		StringBuilder sub = new StringBuilder();
		char[] ch = docText.toCharArray();
		for (char buf : ch) {
			if (9 == buf || 10 == buf || 13 == buf || 32 <= buf && !Character.isISOControl(buf))
				sub.append(buf);
		}
		return sub.toString();
	}


	@Method(desc = "读取word文件内容", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String readWord(File file) {
		String text = "";
		// 得到名字小写
		String fileName = file.getName().toLowerCase();
		try {
			FileInputStream in = new FileInputStream(file);
			//doc为后缀的
			if (fileName.endsWith(".doc")) {
				WordExtractor extractor = new WordExtractor(in);
				text = extractor.getText();
			}
			//docx为后缀的
			if (fileName.endsWith(".docx")) {
				XWPFWordExtractor docx = new XWPFWordExtractor(new XWPFDocument(in));
				text = docx.getText();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;
	}

	@Method(desc = "读取pdf或者图片中文件内容", logicStep = "")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String readPdfOrPicture(File file) {
		try {
			ITesseract instance = new Tesseract();// 获取工具对象
			logger.info(" tessdata 文件夹存放目录： " + System.getProperty("user.dir"));
			instance.setDatapath(System.getProperty("user.dir"));// tessdata目录放在jar包同级目录
			instance.setLanguage(PropertyParaValue.getString("language", "chi_sim"));// 设置识别语言，默认简体中文（chi_sim）
			return instance.doOCR(file);// 识别文本返回
		} catch (Exception e) {
			return "";
		}
	}

	@Method(desc = "根据文件格式，将文件的文本读成一个字符串", logicStep = "判断文件格式，调用相应方法")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String file2String(File file) {
		//文件大小为0时直接返回空字符串
		if (file.length() == 0) {
			return "";
		}
		try {
			// 获取文件后缀
			String fileExtension = FilenameUtils.getExtension(file.getName());
			switch (fileExtension) {
				case "pdf":
					return getPdf(file);
				case "docx":
					return readWord(file);
				case "doc":
					return readWord(file);
				case "xlsx":
				case "xls":
					return readExcel(file);
				case "html":
					return readHtml(file);
				case "txt":
				case "csv":
				case "log":
					return readText(file);
				default:
					return "";
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return "";
	}

	@Method(desc = "根据文件的绝对路径将文件读成一个字符串",
			logicStep = "1.判断是否是图片文件" +
					"2.调用识别图片内容的api" +
					"3.调用识别其他类型文件内容的方法")
	@Param(name = "file", desc = "文件对象", range = "不可为空")
	@Return(desc = "文件的内容的字符串", range = "可能为空字符串")
	public static String file2String(String filePath) {
		File file = FileUtils.getFile(filePath);
		Image image;
		String readData = null;
		try {
			//1.判断是否是图片文件
			image = ImageIO.read(file);
			if (image != null) {
				//2.调用识别图片内容的api
				readData = new PictureTextExtract().extractText(filePath);
				return readData;
			}
			//3.调用识别其他类型文件内容的方法
			readData = file2String(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readData;
	}

	public static void main(String[] args) {

		System.out.println(file2String("D:\\文档\\海云文档\\专业领域智能问答系统设计与实现.pdf"));
	}
}
