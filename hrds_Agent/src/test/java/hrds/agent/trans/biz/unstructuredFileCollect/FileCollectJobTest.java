package hrds.agent.trans.biz.unstructuredFileCollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.ActionResult;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.BusinessException;
import hrds.testbase.WebBaseTestCase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * FileCollectJobTest
 * date: 2019/11/26 14:45
 * author: zxz
 */
public class FileCollectJobTest extends WebBaseTestCase {
	private static final String FOLDER1 = "D:\\xinwen\\";
	private static final String FOLDER2 = "D:\\文档\\海云文档\\";
	private static final String FOLDER3 = "D:\\文档\\海云文档2\\";

	/**
	 * 测试执行文件采集
	 * 1.测试单文件夹采集
	 * 2.测试同时选择多个文件夹采集
	 * 3.测试增量
	 */
	@Test
	public void executeJobTest() {
		String bodyString;
		ActionResult ar;
		//1.测试单文件夹采集
		JSONArray array = new JSONArray();
		JSONObject object = new JSONObject();
		object.put("file_source_id", "33332222");
		object.put("file_source_path", FOLDER1);
		object.put("is_pdf", IsFlag.Shi.getCode());
		object.put("is_office", IsFlag.Shi.getCode());
		object.put("is_text", IsFlag.Shi.getCode());
		object.put("is_video", IsFlag.Shi.getCode());
		object.put("is_audio", IsFlag.Shi.getCode());
		object.put("is_image", IsFlag.Shi.getCode());
		object.put("is_compress", IsFlag.Shi.getCode());
		object.put("custom_suffix", "");
		object.put("is_other", IsFlag.Fou.getCode());
		object.put("file_remark", "");
		object.put("fcs_id", "10000001");
		object.put("agent_id", "10000002");
		array.add(object);
		//1.模拟数据测试
		bodyString = new HttpClient()
				.addData("fcs_id", "10000001")
				.addData("agent_id", "10000002")
				.addData("fcs_name", "文件采集名称")
				.addData("host_name", "127.0.0.1")
				.addData("system_type", "linux")
				.addData("is_sendok", "1")
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("agent_name", "测试名称")
				.addData("source_id", "10000003")
				.addData("datasource_name", "测试数据源名称")
				.addData("dep_id", "10000004")
				.addData("file_source_array", array.toJSONString())
				.post(getActionUrl("execute")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));

		//2.测试同时选择多个文件夹采集
		array.clear();
		JSONObject object1 = new JSONObject();
		object.put("file_source_id", "33332222");
		object.put("file_source_path", FOLDER2);
		object.put("is_pdf", IsFlag.Shi.getCode());
		object.put("is_office", IsFlag.Shi.getCode());
		object.put("is_text", IsFlag.Shi.getCode());
		object.put("is_video", IsFlag.Shi.getCode());
		object.put("is_audio", IsFlag.Shi.getCode());
		object.put("is_image", IsFlag.Shi.getCode());
		object.put("is_compress", IsFlag.Shi.getCode());
		object.put("custom_suffix", "");
		object.put("is_other", IsFlag.Fou.getCode());
		object.put("file_remark", "");
		object.put("fcs_id", "10000001");
		object.put("agent_id", "10000002");
		array.add(object1);
		JSONObject object2 = new JSONObject();
		object2.put("file_source_id", "66663333");
		object2.put("file_source_path", FOLDER3);
		object2.put("is_pdf", IsFlag.Shi.getCode());
		object2.put("is_office", IsFlag.Shi.getCode());
		object2.put("is_text", IsFlag.Shi.getCode());
		object2.put("is_video", IsFlag.Shi.getCode());
		object2.put("is_audio", IsFlag.Shi.getCode());
		object2.put("is_image", IsFlag.Shi.getCode());
		object2.put("is_compress", IsFlag.Shi.getCode());
		object2.put("custom_suffix", "");
		object2.put("is_other", IsFlag.Fou.getCode());
		object2.put("file_remark", "");
		object2.put("fcs_id", "10000001");
		object2.put("agent_id", "10000002");
		array.add(object2);
		//1.模拟数据测试
		bodyString = new HttpClient()
				.addData("fcs_id", "10000001")
				.addData("agent_id", "10000002")
				.addData("fcs_name", "文件采集名称")
				.addData("host_name", "127.0.0.1")
				.addData("system_type", "linux")
				.addData("is_sendok", "1")
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("agent_name", "测试名称")
				.addData("source_id", "10000003")
				.addData("datasource_name", "测试数据源名称")
				.addData("dep_id", "10000004")
				.addData("file_source_array", array.toJSONString())
				.post(getActionUrl("execute")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));

		//3.测试增量文件采集
		array.clear();
		JSONObject object3 = new JSONObject();
		object.put("file_source_id", "33332222");
		object.put("file_source_path", FOLDER1);
		object.put("is_pdf", IsFlag.Shi.getCode());
		object.put("is_office", IsFlag.Shi.getCode());
		object.put("is_text", IsFlag.Shi.getCode());
		object.put("is_video", IsFlag.Shi.getCode());
		object.put("is_audio", IsFlag.Shi.getCode());
		object.put("is_image", IsFlag.Shi.getCode());
		object.put("is_compress", IsFlag.Shi.getCode());
		object.put("custom_suffix", "");
		object.put("is_other", IsFlag.Fou.getCode());
		object.put("file_remark", "");
		object.put("fcs_id", "10000001");
		object.put("agent_id", "10000002");
		array.add(object3);
		JSONObject object4 = new JSONObject();
		object2.put("file_source_id", "66663333");
		object2.put("file_source_path", FOLDER2);
		object2.put("is_pdf", IsFlag.Shi.getCode());
		object2.put("is_office", IsFlag.Shi.getCode());
		object2.put("is_text", IsFlag.Shi.getCode());
		object2.put("is_video", IsFlag.Shi.getCode());
		object2.put("is_audio", IsFlag.Shi.getCode());
		object2.put("is_image", IsFlag.Shi.getCode());
		object2.put("is_compress", IsFlag.Shi.getCode());
		object2.put("custom_suffix", "");
		object2.put("is_other", IsFlag.Fou.getCode());
		object2.put("file_remark", "");
		object2.put("fcs_id", "10000001");
		object2.put("agent_id", "10000002");
		array.add(object4);
		//1.模拟数据测试
		bodyString = new HttpClient()
				.addData("fcs_id", "10000001")
				.addData("agent_id", "10000002")
				.addData("fcs_name", "文件采集名称")
				.addData("host_name", "127.0.0.1")
				.addData("system_type", "linux")
				.addData("is_sendok", "1")
				.addData("is_solr", IsFlag.Shi.getCode())
				.addData("agent_name", "测试名称")
				.addData("source_id", "10000003")
				.addData("datasource_name", "测试数据源名称")
				.addData("dep_id", "10000004")
				.addData("file_source_array", array.toJSONString())
				.post(getActionUrl("execute")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
	}

}
