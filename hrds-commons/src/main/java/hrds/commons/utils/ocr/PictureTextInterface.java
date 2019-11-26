package hrds.commons.utils.ocr;

public interface PictureTextInterface {

	public String extractText(String filePath);

	public String byteToStr(byte[] bytes);
}
