package hrds.b.biz.agent.dbagentconf;

import com.alibaba.fastjson.JSONObject;
import fd.ng.core.utils.JsonUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.netserver.conf.HttpServerConf;
import fd.ng.netserver.conf.HttpServerConfBean;
import fd.ng.web.action.ActionResult;
import fd.ng.web.annotation.Action;
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
@Action(UriExt = "DBConfStepAction")
public class DBConfStepAction extends BaseAction{

	/**
	 * 数据库直连采集，根据databaseId进行查询并在页面上回显数据源配置信息
	 *
	 * 1、在数据库设置表(database_set)中，根据databaseId查看当前是否设置完成并发送成功,如果已经发送了，则不允许编辑
	 * 2、判断是否查询到数据，如果查询不到，抛异常给前端
	 * 3、如果任务已经设置完成并发送成功，则不允许编辑
	 * 4、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息并返回
	 *
	 * @Param: databaseId long
	 *         含义：database_set表主键
	 *         取值范围：不为空
	 * @return: fd.ng.db.resultset.Result
	 *          含义：数据源信息查询结果集
	 *          取值范围：不会为null
	 *
	 * */
	public Result getDBConfInfo(long databaseId) {
		//1、在数据库设置表中，根据databaseId查看当前是否设置完成并发送成功,如果已经发送了，则不允许编辑
		Optional<Database_set> firResult = Dbo.queryOneObject(Database_set.class,
				"select * from database_set where database_id = ? ", databaseId);
		//2、判断是否查询到数据，如果查询不到，抛异常给前端
		Database_set dbSet = firResult.orElseThrow(() -> new BusinessException("未能找到该任务"));
		//3、如果任务已经设置完成并发送成功，则不允许编辑
		if(IsFlag.Shi == IsFlag.ofEnumByCode(dbSet.getIs_sendok())){
			throw new BusinessException("该任务已经设置完成并发送成功，不允许编辑");
		}
		//4、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息并返回
		return Dbo.queryResult("select * from database_set t1 " +
				"left join collect_job_classify t2 on " +
				"t1.classify_id = t2.classify_id  where database_id = ?", databaseId);
	}

	/**
	 * 根据数据库类型和端口获得数据库连接url等信息
	 *
	 * 1、在数据库设置表(database_set)中，根据databaseId查看当前是否设置完成并发送成功,如果已经发送了，则不允许编辑
	 * 2、判断是否查询到数据，如果查询不到，抛异常给前端
	 * 3、如果任务已经设置完成并发送成功，则不允许编辑
	 * 4、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息并返回
	 *
	 * @Param: dbType String
	 *         含义：数据库类型
	 *         取值范围：DatabaseType代码项code值
	 * @Param: port String
	 *         含义：数据库连接端口号
	 *         取值范围：不为空
	 * @return: String
	 *          含义：数据库连接url
	 *          取值范围：不会为null
	 *
	 * */
	public String getJDBCDriver(String dbType, String port) {
		//ConnUtil.getConn_url(dbType, port);
		return null;
	}

	/**
	 * 保存数据库采集Agent数据库配置信息
	 *
	 * 1、获取实体中的database_id
	 * 2、如果存在，则更新信息
	 * 3、如果不存在，则新增信息
	 *
	 * @Param: databaseSet Database_set
	 *         含义：待保存的Database_set实体对象
	 *         取值范围：不为空
	 * @return: 无
	 *
	 * */
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
	 * 测试连接
	 *
	 * 1、根据agent_id获得agent_ip,agent_port
	 * 2、在配置文件中获取webContext和actionPattern
	 * 3、调用工具类方法给agent发消息，并获取agent响应
	 * 4、将响应封装成ActionResult的对象
	 *
	 * @Param: databaseSet Database_set
	 *         含义：存有agent_id, driver, url, username, password, dbtype等信息的Database_set实体类对象
	 *         取值范围：不为空
	 * @return: ActionResult对象
	 *          含义：封装了Agent响应信息
	 *          取值范围：不为空
	 *
	 * */
	public ActionResult testConnection(@RequestBean Database_set databaseSet) {
		//1、根据agent_id获得agent_ip,agent_port
		Result result = Dbo.queryResult("select agent_ip, agent_port from agent_info " +
						"where agent_id = ?",
				databaseSet.getAgent_id());
		if(result.isEmpty()){
			throw new BusinessException("未能找到Agent信息");
		}
		if(result.getRowCount() != 1){
			throw new BusinessException("找到的Agent信息不唯一");
		}
		//2、在配置文件中获取webContext和actionPattern
		HttpServerConfBean test = HttpServerConf.getHttpServer("testConnection");
		String webContext = test.getWebContext();
		String actionPattern = test.getActionPattern();
		String agentIp = result.getString(0, "agent_ip");
		String agentPort = result.getString(0, "agent_port");
		String url = "http://" + agentIp + ":" + agentPort + webContext;
		//3、调用工具类方法给agent发消息，并获取agent响应
		HttpClient.ResponseValue resVal = new HttpClient()
				.addData("driver", databaseSet.getDatabase_drive())
				.addData("url", databaseSet.getJdbc_url())
				.addData("username", databaseSet.getUser_name())
				.addData("password", databaseSet.getDatabase_pad())
				.addData("dbtype", databaseSet.getDatabase_type())
				.post(url + actionPattern);
		//4、将响应封装成ActionResult的对象
		return JsonUtil.toObject(resVal.getBodyString(), ActionResult.class);
	}
}
