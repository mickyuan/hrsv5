package hrds.agent.trans.biz.ftpcollect;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
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
public class FtpCollectJobTest extends WebBaseTestCase {

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
		//1.模拟数据测试
		bodyString = new HttpClient()
				.addData("ftp_id", "10000001")
				.addData("ftp_number", "10000002")
				.addData("ftp_name", "ftp采集名称")
				.addData("start_date", DateUtil.getSysDate())
				.addData("end_date", DateUtil.getSysTime())
				.addData("ftp_ip", "172.168.0.101")
				.addData("ftp_port", "22")
				.addData("ftp_username", "hyshf")
				.addData("ftp_password", StringUtil.string2Unicode("q1w2e3"))
				.addData("ftp_dir", "/home/hyshf/zxz")
				.addData("local_path", "D:\\xinwen")
				.addData("file_suffix", "docx")
				.addData("run_way", "1")
				.addData("remark", "")
				.addData("agent_id", "10000002")
				.addData("ftp_rule_path", "3")
				.addData("child_file_path", "")
				.addData("child_time", "3")
				.addData("is_sendok", "1")
				.addData("is_unzip", "0")
				.addData("reduce_type", "")
				.addData("ftp_model", "1")
				.addData("is_read_realtime", "1")
				.addData("realtime_interval", "15")
				.post(getActionUrl("execute")).getBodyString();
		ar = JsonUtil.toObjectSafety(bodyString, ActionResult.class).orElseThrow(()
				-> new BusinessException("连接失败！"));
		assertThat(ar.isSuccess(), is(true));
	}

}
