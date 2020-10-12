package hrds.g.biz.CallInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.exception.BusinessException;

import java.util.Iterator;
import java.util.Set;

public class CallInterface {
	public static void main(String args[]) throws  Exception{
		System.out.println("清输入参数，第一个参数为访问的url路径（例如：http://172.168.0.23:8091/G/action/hrds/g/biz/serviceuser/impl/getToken）," +
				"第二个参数url所需要的参数，以json的方式进行传输（例如：{\"user_id\":\"2001\",\"user_password\":\"1\"}）");
		String httpUrl = args[0];
		String json = args[1];
		String s = doPost(httpUrl, json);
		System.out.println(s);
	}

	private static String doPost(String httpUrl, String json) throws Exception {
		HttpClient client = new HttpClient();
		JSONObject jsonObject = JSON.parseObject(json);
		Set<String> strings = jsonObject.keySet();
		for(String string : strings){
			client.addData(string,jsonObject.getString(string));
		}
		HttpClient.ResponseValue resVal = client.post(httpUrl);
		//4、根据响应状态码判断响应是否成功
		ActionResult ar = JsonUtil.toObjectSafety(resVal.getBodyString(), ActionResult.class)
				.orElseThrow(() -> new BusinessException("连接" + httpUrl + "服务异常"));
		if (!ar.isSuccess()) {
			throw new BusinessException("调用接口失败,接口地址：" + httpUrl);
		}
		//注释：调用reload接口时，返回值为string，而调用其他接口返回值为json，所以公共方法同一返回string
		return ar.getData().toString();
	}
}
