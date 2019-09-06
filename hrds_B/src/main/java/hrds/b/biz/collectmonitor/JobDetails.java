package hrds.b.biz.collectmonitor;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.ExecuteState;
import hrds.commons.entity.Collect_case;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>标    题: 海云数服 V5.0</p>
 * <p>描    述: 处理采集作业的详细信息</p>
 * <p>版    权: Copyright (c) 2019</p>
 * <p>公    司: 博彦科技(上海)有限公司</p>
 * <p>@author : Mr.Lee</p>
 * <p>创建时间 : 2019-09-05 11:43</p>
 * <p>version: JDK 1.8</p>
 */
public class JobDetails {

	/**
	 * <p>方法描述: 处理采集作业的信息</p>
	 * <p>1 : 找出有失败作业的信息,并将其优先显示在最前面</p>
	 * <p>2 : 返回处理后的数据</p>
	 * <p>@author: Mr.Lee </p>
	 * <p>创建时间: 2019-09-05</p>
	 * <p>参   数:  </p>
	 * <p>return:  </p>
	 */
	public static void getDetails(List<Collect_case> collectJobList) {

		//定义一个完成的状态
		String state = ExecuteState.YunXingWanCheng.getCode();

		//成功数
		int success = 0;
		//运行数
		int go = 0;
		//失败数
		int fail = 0;

		Map<String, JSONObject> detailsMap = new LinkedHashMap<>();
		String jobSuccess = "";

		//1 : 找出有失败作业的信息,并将其优先显示在最前面
		collectJobList.forEach(collect_case -> {
			//采集原表名称
			String table_name = collect_case.getTable_name();
			//作业类型
			String job_type = collect_case.getJob_type();
			//作业状态
			String execute_state = collect_case.getExecute_state();

			//TODO 缺少日期转换函数
			//采集开始时间
			//采集结束时间

			//如果当期表已经存在了
			if( detailsMap.containsKey(table_name) ) {
				detailsMap.get(table_name).put(job_type, execute_state);
			}
			else {
				JSONObject jsonObject = new JSONObject(true);
				jsonObject.put(job_type, execute_state);
			}
		});
	}
}
