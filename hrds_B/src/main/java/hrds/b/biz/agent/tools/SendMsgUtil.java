package hrds.b.biz.agent.tools;

import fd.ng.core.utils.StringUtil;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.AgentActionUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 海云应用管理端向Agent端发送消息的工具类
 * @Author: wangz
 * @CreateTime: 2019-09-26-11:36
 * @BelongsProject: hrsv5
 * @BelongsPackage: hrds.b.biz.agent.tools
 **/
public class SendMsgUtil {

	private static final Log logger = LogFactory.getLog(SendMsgUtil.class);

	/**
	 * 海云应用管理端根据agentIp和agentPort向Agent端发送jsonData
	 *
	 * 1、对参数合法性进行校验
	 * 2、根据agentIp和agentPort构建http请求的url
	 * 3、将请求的url和配置信息封装成HttpPost对象
	 * 4、对请求的参数调用工具类做压缩加密操作，得到处理后的数据并封装
	 * 5、httpClient发送请求并接收响应
	 * 6、根据响应状态码判断响应是否成功
	 * 7、若响应成功，调用方法解析响应报文，并返回响应数据
	 * 8、若响应不成功，记录日志，并返回操作失败
	 *
	 * @Param: agentId Long
	 *         含义：agentId，agent_info表主键，agent_down_info表外键
	 *         取值范围：不为空
	 * @Param: userId Long
	 *         含义：当前登录用户Id，sys_user表主键，agent_down_info表外键
	 *         取值范围：不为空
	 * @Param: jsonData String
	 *         含义：海云应用管理端到Agent端的请求参数
	 *         取值范围 : json格式的字符串
	 * @Param: methodName String
	 *         含义：Agent端的提供服务的方法的方法名
	 *         取值范围 : AgentActionUtil类中的静态常量
	 *
	 * @return: String
	 *          含义 : Agent端通过本地http交互得到的响应数据的msg部分，内容是按照表名模糊查询或者查询所有表得到的表信息
	 *          取值范围 : json格式的字符串
	 * */
	public static String sendMsg(Long agentId, Long userId, String jsonData, String methodName){
		//1、对参数合法性进行校验
		if(agentId == null){
			throw new BusinessException("向Agent发送信息时agentId不能为空");
		}
		if(userId == null){
			throw new BusinessException("向Agent发送信息时userId不能为空");
		}
		if(StringUtil.isBlank(methodName)){
			throw new BusinessException("向Agent发送信息时methodName不能为空");
		}

		String url = AgentActionUtil.getUrl(agentId, userId, methodName);
		logger.debug("准备建立连接，请求的URL为" + url);

		CloseableHttpClient httpClient = null;
		HttpPost httpPost;
		CloseableHttpResponse response = null;

		String var15;
		try {
			//2、根据agentIp和agentPort构建http请求的url
			httpClient = HttpClients.createDefault();
			//3、将请求的url和配置信息封装成HttpPost对象
			httpPost = new HttpPost(url);
			RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(300000).
					setConnectTimeout(300000).setSocketTimeout(300000).build();
			httpPost.setConfig(config);
			//4、对请求的参数调用工具类做压缩加密操作，得到处理后的数据并封装
			String sendStr = PackUtil.packMsg(jsonData);
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("msgjson", sendStr));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
			httpPost.setEntity(entity);
			//5、httpClient发送请求并接收响应
			response = httpClient.execute(httpPost);
			//6、根据响应状态码判断响应是否成功
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entityJson = response.getEntity();
				String jsonStr = EntityUtils.toString(entityJson, "UTF-8");
				String msg = PackUtil.unpackMsg(jsonStr).get("msg");
				logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>返回消息为：" + msg);
				return msg;
			}

			logger.error(">>>>>>>>>>>>>>>>>>>>>>>>错误信息为：" + response.getStatusLine().getStatusCode());
			//TODO 后面可以进行国际化操作
			var15 = "操作失败";
		} catch (Exception var32) {
			//TODO 后面可以进行国际化处理
			throw new AppSystemException("SENDERR : 连接失败", var32);
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException var31) {
					logger.error("CloseableHttpResponse资源关闭失败", var31);
				}
			}
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException var30) {
					logger.error("CloseableHttpClient资源关闭失败", var30);
				}
			}
		}
		return var15;
	}
}
