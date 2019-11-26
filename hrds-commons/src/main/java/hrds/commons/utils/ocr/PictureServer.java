package hrds.commons.utils.ocr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;

public class PictureServer {

	private static final Log logger = LogFactory.getLog(PictureServer.class);
	//设置配置
	XmlRpcClientConfigImpl config = null;
	XmlRpcClient client = null;

	public PictureServer(String url) {
		try {
			config = new XmlRpcClientConfigImpl();
			//指定服务器URL
			config.setServerURL(new URL(url));
			client = new XmlRpcClient();
			client.setConfig(config);
		}
		catch(MalformedURLException e) {
			logger.info("OCR PRC服务连接失败......");
			e.printStackTrace();
		}
	}

}
