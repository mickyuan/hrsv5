package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netclient.http.SubmitMediaType;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.key.PrimayKeyGener;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据库直连采集各个步骤服务类
 * @author: WangZhengcheng
 * @create: 2019-09-04 11:22
 **/
public class DBConfStepAction {

	/**
	 * @Description: 数据库直连采集，数据源配置信息页面后台服务类，用于根据参数进行查询并在页面上回显数据
	 * @Param: [agentId]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤;
	 *      1、在数据库设置表(database_set)中，根据agentId查看当前agent是否设置完成并发送成功
	 *      2、如果得到结果，说明先前agent设置完成并发送成功，需要回显数据给客户编辑
	 *      3、在结果中得到database_id,根据database_id查询出相应的信息
	 *          3-1、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息
	 *          3-2、在collect_frequency(卸数作业参数表)表中，查询出id,文件存储路径，作业编号信息
	 *          3-3、二者汇总成一个Result对象返回
	 *      4、根据数据库类型获取驱动，用于前台展示
	 *      5、返回
	 */
	public void getDBConfInfo(String agentId) {
		//1、在数据库设置表中，根据agentId查看当前agent是否设置完成并发送成功
		Result firResult = Dbo.queryResult("select * from database_set where agent_id = ?", agentId);
		if (firResult != null) {
			//2、如果得到结果，说明先前agent设置完成并发送成功，需要回显数据给客户编辑
			if (firResult.getRowCount() == 1) {
				if (IsFlag.Shi == IsFlag.getCodeObj(firResult.getString(0, "is_sendok"))) {
					//3、在结果中得到database_id,根据database_id查询出相应的信息
					String databaseId = firResult.getString(0, "database_id");
					//3-1、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息
					Result secResult = Dbo.queryResult("select * from database_set t1 left join collect_job_classify t2 on t1.classify_id = t2.classify_id  where database_id = ?", databaseId);
					//3-2、在collect_frequency(卸数作业参数表)表中，查询出id,文件存储路径，作业编号信息
					Result thiResult = Dbo.queryResult("select file_path,cf_jobnum,cf_id from collect_frequency where collect_set_id = ?", databaseId);
					//3-3、二者汇总成一个Result对象返回
					secResult.setObject(0, "file_path", thiResult.getString(0, "file_path"));
					secResult.setObject(0, "cf_jobnum", thiResult.getString(0, "cf_jobnum"));
					secResult.setObject(0, "cf_id", thiResult.getString(0, "cf_id"));
					//4、根据数据库类型获取驱动，用于前台展示
					//5、返回
				} else {
					throw new BusinessException("Agent设置未完成或未发送");
				}
			} else {
				throw new BusinessException("Agent不唯一");
			}
		} else {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
		}
	}

	/**
	 * @Description: 根据数据库类型和端口获得数据库连接url等信息
	 * @Param: [dbType, port]
	 * @return: java.lang.String
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 */
	public String getJDBCDriver(String dbType, String port) {
		//ConnUtil.getConn_url(dbType, port);
		return null;
	}

	/**
	 * @Description: 保存数据库采集Agent数据库配置信息
	 * @Param: [databaseSet]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤：
	 *      1、获取实体中的database_id，判断在database_set表中是否存在
	 *      2、如果存在，则更新信息
	 *      3、如果不存在，则新增信息
	 */
	public void saveDbConf(@RequestBean Database_set databaseSet) {
		//1、获取实体中的database_id，判断在database_set表中是否存在
		Result result = Dbo.queryResult("select * from database_set where database_id = ?", databaseSet.getDatabase_id());
		if (!result.isEmpty()) {
			//2、如果存在，则更新信息
			if (databaseSet.update(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + databaseSet);
		} else {
			//3、如果不存在，则新增信息
			JSONObject cleanObj = new JSONObject(true);
			cleanObj.put("complement", Integer.valueOf(CleanType.ZiFuBuQi.getCode()));
			cleanObj.put("replacement", Integer.valueOf(CleanType.ZiFuTiHuan.getCode()));
			cleanObj.put("formatting", Integer.valueOf(CleanType.ShiJianZhuanHuan.getCode()));
			cleanObj.put("conversion", Integer.valueOf(CleanType.MaZhiZhuanHuan.getCode()));
			cleanObj.put("consolidation", Integer.valueOf(CleanType.ZiFuHeBing.getCode()));
			cleanObj.put("split", Integer.valueOf(CleanType.ZiFuChaiFen.getCode()));
			cleanObj.put("trim", Integer.valueOf(CleanType.ZiFuTrim.getCode()));
			String id = PrimayKeyGener.getNextId();
			databaseSet.setDatabase_number(id);
			databaseSet.setDatabase_id(id);
			databaseSet.setDb_agent(IsFlag.Fou.toString());
			databaseSet.setIs_sendok(IsFlag.Fou.toString());
			databaseSet.setCp_or(cleanObj.toJSONString());
			if (databaseSet.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + databaseSet);
		}
	}

	/**
	 * @Description: 测试连接
	 * @Param: [databaseSet]
	 * @return: boolean
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤：
	 *      1、根据agent_id获得agent_ip,agent_port
	 *      2、调用工具类方法给agent发消息，并获取agent响应
	 *      3、将响应封装成ActionResult的对象
	 */
	public ActionResult testConnection(@RequestBean Database_set databaseSet) {
		//1、根据agent_id获得agent_ip,agent_port
		Result result = Dbo.queryResult("select * from agent_info where agent_id = ?", databaseSet.getAgent_id());
		if (!result.isEmpty()) {
			if (result.getRowCount() == 1) {
				String agentIp = result.getString(0, "agent_ip");
				String agentPort = result.getString(0, "agent_port");
				String url = "http://" + agentIp + ":" + agentPort + "/agent/receive/";
				String action = "hrds/agent/trans/biz/testConn";
				Map<String, Object> map = new HashMap<>();
				Class clazz = databaseSet.getClass();
				Field[] fields = clazz.getDeclaredFields();
				try {
					for (Field field : fields) {
						field.setAccessible(true);
						map.put(field.getName(), field.get(databaseSet));
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				//2、调用工具类方法给agent发消息，并获取agent响应
				HttpClient.ResponseValue resVal = new HttpClient(SubmitMediaType.JSON)
						.addJson(JsonUtil.toJson(map))
						.post(url + action);
				//3、将响应封装成ActionResult的对象
				return JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
			} else {
				throw new BusinessException("Agent不唯一");
			}
		} else {
			throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
		}
	}
}
