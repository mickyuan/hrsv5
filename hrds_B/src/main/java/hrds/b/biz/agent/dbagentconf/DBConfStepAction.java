package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.Optional;

/**
 * @description: 应用管理端数据库采集配置步骤一、数据库连接信息配置
 * @author: WangZhengcheng
 * @create: 2019-09-04 11:22
 **/
public class DBConfStepAction extends BaseAction{

	/**
	 * @Description: 数据库直连采集，根据agentId进行查询并在页面上回显数据源配置信息
	 * @Param: [agentId : AgentID, 取值范围 : 不限]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤;
	 * 1、在数据库设置表(database_set)中，根据databaseId查看当前是否设置完成并发送成功,如果已经发送了，则不允许编辑
	 * 2、判断是否查询到数据，如果查询不到，抛异常给前端
	 * 3、如果任务已经设置完成并发送成功，则不允许编辑
	 *      3-1、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息
	 *      3-2、在collect_frequency(卸数作业参数表)表中，查询出id,文件存储路径，作业编号信息
	 *      3-3、二者汇总成一个Result对象返回
	 * 4、返回
	 */
	public Result getDBConfInfo(long databaseId) {
		//1、在数据库设置表中，根据databaseId查看当前是否设置完成并发送成功,如果已经发送了，则不允许编辑
		Optional<Database_set> firResult = Dbo.queryOneObject(Database_set.class,
				"select * from database_set where database_id = ? ", databaseId);
		//2、判断是否查询到数据，如果查询不到，抛异常给前端
		Database_set dbSet = firResult.orElseThrow(() -> new BusinessException("未能找到该任务"));
		//3、如果任务已经设置完成并发送成功，则不允许编辑
		if(IsFlag.Shi == IsFlag.getCodeObj(dbSet.getIs_sendok())){
			throw new BusinessException("该任务已经设置完成并发送成功，不允许编辑");
		}
		//3-1、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息
		Result secResult = Dbo.queryResult("select * from database_set t1 left join collect_job_classify t2 on " +
				"t1.classify_id = t2.classify_id  where database_id = ?", databaseId);
		//3-2、在collect_frequency(卸数作业参数表)表中，查询出id,文件存储路径，作业编号信息
		Result thiResult = Dbo.queryResult("select file_path,cf_jobnum,cf_id from collect_frequency " +
				"where collect_set_id = ?", databaseId);
		//3-3、二者汇总成一个Result对象返回
		secResult.setObject(0, "file_path", thiResult.getString(0, "file_path"));
		secResult.setObject(0, "cf_jobnum", thiResult.getString(0, "cf_jobnum"));
		secResult.setObject(0, "cf_id", thiResult.getString(0, "cf_id"));
		return secResult;
	}

	/**
	 * @Description: 根据数据库类型和端口获得数据库连接url等信息
	 * @Param: [dbType : 数据库类型, 取值范围 : DatabaseType枚举类]
	 * @Param: [port : , 端口号, 取值范围 : 不限]
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
	 * @Param: [databaseSet : 数据库设置对象, 取值范围 : 不限]
	 * @return: void
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤：
	 * 1、获取实体中的database_id
	 * 2、如果存在，则更新信息
	 * 3、如果不存在，则新增信息
	 */
	public void saveDbConf(@RequestBean Database_set databaseSet) {
		//1、获取实体中的database_id
		if(StringUtil.isNotBlank(String.valueOf(databaseSet.getDatabase_id()))){
			//2、如果存在，则更新信息
			if (databaseSet.update(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + databaseSet);
		} else {
			//3、如果不存在，则新增信息
			//任务级别的清洗规则，在这里新增时定义一个默认顺序，后面的页面可能改动这个顺序,后面在取这个清洗顺序的时候，用枚举==的方式
			JSONObject cleanObj = new JSONObject(true);
			cleanObj.put(CleanType.ZiFuBuQi.getCode(), 1);
			cleanObj.put(CleanType.ZiFuTiHuan.getCode(), 2);
			cleanObj.put(CleanType.ShiJianZhuanHuan.getCode(), 3);
			cleanObj.put(CleanType.MaZhiZhuanHuan.getCode(), 4);
			cleanObj.put(CleanType.ZiFuHeBing.getCode(), 5);
			cleanObj.put(CleanType.ZiFuChaiFen.getCode(), 6);
			cleanObj.put(CleanType.ZiFuTrim.getCode(), 7);
			String id = PrimayKeyGener.getNextId();
			databaseSet.setDatabase_number(id);
			databaseSet.setDatabase_id(id);
			databaseSet.setDb_agent(IsFlag.Fou.getCode());
			databaseSet.setIs_sendok(IsFlag.Fou.getCode());
			databaseSet.setCp_or(cleanObj.toJSONString());
			if (databaseSet.add(Dbo.db()) != 1)
				throw new BusinessException("新增数据失败！data=" + databaseSet);
		}
	}

	/**
	 * @Description: 测试连接
	 * @Param: [databaseSet : 数据库设置对象, 取值范围 : 不限]
	 * @return: boolean
	 * @Author: WangZhengcheng
	 * @Date: 2019/9/4
	 * 步骤：
	 * 1、根据agent_id获得agent_ip,agent_port
	 * 2、在配置文件中获取webContext和actionPattern
	 * 2、调用工具类方法给agent发消息，并获取agent响应
	 * 3、将响应封装成ActionResult的对象
	 */
	public ActionResult testConnection(@RequestBean Database_set databaseSet) {
		//1、根据agent_id获得agent_ip,agent_port
		Result result = Dbo.queryResult("select agent_ip, agent_port from agent_info where agent_id = ?",
				databaseSet.getAgent_id());
		if(result.isEmpty()){
			throw new BusinessException("未能找到Agent信息");
		}
		if(result.getRowCount() != 1){
			throw new BusinessException("找到的Agent信息不唯一");
		}
		HttpServerConfBean test = HttpServerConf.getHttpServer("testConnection");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String agentIp = result.getString(0, "agent_ip");
		String agentPort = result.getString(0, "agent_port");
		String url = "http://" + agentIp + ":" + agentPort + webContext;
		//2、调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("driver", databaseSet.getDatabase_drive())
				.addData("url", databaseSet.getJdbc_url())
				.addData("username", databaseSet.getUser_name())
				.addData("password", databaseSet.getDatabase_pad())
				.addData("dbtype", databaseSet.getDatabase_type())
				.post(url + actionPattern);
		//3、将响应封装成ActionResult的对象
		return JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
	}
}
