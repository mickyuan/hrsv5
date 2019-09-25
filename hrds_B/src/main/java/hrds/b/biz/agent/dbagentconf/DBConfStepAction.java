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
import hrds.b.biz.agent.tools.ConnUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.IsFlag;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Collect_job_classify;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Database_set;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Map;

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
	 * 1、在数据库设置表(database_set)中，根据databaseId判断是否查询到数据，如果查询不到，抛异常给前端
	 * 2、如果任务已经设置完成并发送成功，则不允许编辑
	 * 3、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息并返回
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
		//1、在数据库设置表(database_set)中，根据databaseId判断是否查询到数据，如果查询不到，抛异常给前端
		Database_set dbSet = Dbo.queryOneObject(Database_set.class,
				"SELECT das.* " +
						" FROM "+ Data_source.TableName +" ds " +
						" JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
						" JOIN "+ Database_set.TableName +" das ON ai.agent_id = das.agent_id " +
						" WHERE das.database_id = ? AND ds.create_user_id = ? "
				, databaseId, getUserId())
				.orElseThrow(() -> new BusinessException("未能找到该任务"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制

		//2、如果任务已经设置完成并发送成功，则不允许编辑
		if(IsFlag.Shi == IsFlag.ofEnumByCode(dbSet.getIs_sendok())){
			throw new BusinessException("该任务已经设置完成并发送成功，不允许编辑");
		}
		//3、在数据库设置表表中，关联采集作业分类表(collect_job_classify)，查询出当前database_id的所有信息并返回
		return Dbo.queryResult("select * from database_set t1 " +
				"left join collect_job_classify t2 on " +
				"t1.classify_id = t2.classify_id  where database_id = ?", databaseId);
	}

	/**
	 * 根据数据库类型和端口获得数据库连接url等信息
	 *
	 * 1、调用工具类方法直接获取数据并返回
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
	public Map<String, String> getJDBCDriver(String dbType) {
		return ConnUtil.getConnURL(dbType);
		//数据可访问权限处理方式
		//不与数据库交互，无需限制访问权限
	}

	/**
	 * 根据classifyId判断当前分类是否被使用，如果被使用，则不能编辑，否则，可以编辑
	 *
	 * 1、在数据库中查询database_set表中是否有使用到当前classifyId的数据
	 * 2、如果有，返回false，表示该分类被使用，不能编辑
	 * 3、如果没有，返回true，表示该分类没有被使用，可以编辑
	 *
	 * @Param: classifyId long
	 *         含义：采集任务分类表ID
	 *         取值范围：不可为空
	 * @return: boolean
	 *          含义：该分类是否可以被编辑
	 *          取值范围：返回false，表示该分类被使用，不能编辑；返回true，表示该分类没有被使用，可以编辑
	 *
	 * */
	public boolean checkClassifyId(long classifyId){
		//1、在数据库中查询database_set表中是否有使用到当前classifyId的数据
		long val = Dbo.queryNumber("SELECT count(1) FROM " + Data_source.TableName + " ds" +
				" JOIN " + Agent_info.TableName + " ai ON ds.source_id = ai.source_id " +
				" JOIN " + Database_set.TableName + " das ON ai.agent_id = das.agent_id " +
				" WHERE das.classify_id = ? AND ds.create_user_id = ? ", classifyId, getUserId())
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制

		//2、如果有，返回false，表示该分类被使用，不能编辑
		//3、如果没有，返回true，表示该分类没有被使用，可以编辑
		return val == 0;
	}

	/**
	 * 根据sourceId获取分类信息
	 *
	 * 1、在数据库中查询相应的信息并返回
	 *
	 * @Param: sourceId long
	 *         含义：数据源表ID
	 *         取值范围：不可为空
	 * @return: List<Collect_job_classify>
	 *          含义：所有在该数据源下的分类信息的List集合
	 *          取值范围：不会为空
	 *
	 * */
	public List<Collect_job_classify> getClassifyInfo(long sourceId){
		//1、在数据库中查询相应的信息并返回
		return Dbo.queryList(Collect_job_classify.class, "SELECT cjc.* FROM data_source ds " +
				" JOIN agent_info ai ON ds.source_id = ai.source_id" +
				" JOIN collect_job_classify cjc ON ai.agent_id = cjc.agent_id" +
				" WHERE ds.source_id = ? AND cjc.user_id = ? order by cjc.classify_num "
				, sourceId, getUserId());
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	/**
	 * 保存采集任务分类信息
	 *
	 * 1、在数据库或中对新增数据进行校验
	 * 2、分类编号重复抛异常给前台
	 * 3、分类编号不重复可以新增
	 * 4、给新增数据设置ID
	 * 5、完成新增
	 *
	 * @Param: sourceId long
	 *         含义：数据源表ID
	 *         取值范围：不可为空
	 * @Param: classify Collect_job_classify
	 *         含义：Collect_job_classify类对象，保存着待保存的信息
	 *         取值范围：Collect_job_classify类对象
	 * @return: 无
	 *
	 * */
	public void saveClassifyInfo(@RequestBean Collect_job_classify classify, long sourceId){
		//1、在数据库或中对新增数据进行校验
		long val = Dbo.queryNumber("SELECT count(1) FROM collect_job_classify cjc " +
						" LEFT JOIN agent_info ai ON cjc.agent_id=ai.agent_id" +
						" LEFT JOIN data_source ds ON ds.source_id=ai.source_id" +
						" WHERE cjc.classify_num=? AND ds.source_id=? AND ds.user_id = ? ",
				classify.getClassify_num(), sourceId, getUserId()).orElseThrow(
				() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
		//2、分类编号重复抛异常给前台
		if(val != 0){
			throw new BusinessException("分类编号重复，请重新输入");
		}
		//3、分类编号不重复可以新增
		//4、给新增数据设置ID
		classify.setClassify_id(PrimayKeyGener.getNextId());
		//5、完成新增
		if (classify.add(Dbo.db()) != 1)
			throw new BusinessException("保存分类信息失败！data=" + classify);
	}

	/**
	 * 更新采集任务分类信息
	 *
	 * 1、在数据库或中对待更新数据进行校验，判断待更新的数据是否存在
	 * 2、不存在抛异常给前台
	 * 3、存在则完成更新
	 *
	 * @Param: sourceId long
	 *         含义：数据源表ID
	 *         取值范围：不可为空
	 * @Param: classify Collect_job_classify
	 *         含义：Collect_job_classify类对象，保存着待保存的信息
	 *         取值范围：Collect_job_classify类对象
	 * @return: 无
	 *
	 * */
	public void updateClassifyInfo(@RequestBean Collect_job_classify classify, long sourceId){
		//1、在数据库或中对待更新数据进行校验，判断待更新的数据是否存在
		long val = Dbo.queryNumber("SELECT count(1) FROM collect_job_classify cjc " +
						" LEFT JOIN agent_info ai ON cjc.agent_id=ai.agent_id" +
						" LEFT JOIN data_source ds ON ds.source_id=ai.source_id" +
						" WHERE cjc.classify_id=? AND ds.source_id=? AND ds.user_id = ? ",
				classify.getClassify_id(), sourceId, getUserId()).orElseThrow(
				() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
		//2、不存在抛异常给前台
		if(val == 0){
			throw new BusinessException("待更新的数据不存在");
		}
		//3、存在则完成更新
		if (classify.update(Dbo.db()) != 1)
			throw new BusinessException("保存分类信息失败！data=" + classify);
	}

	/**
	 * 删除采集任务分类信息
	 *
	 * 1、在数据库或中对待更新数据进行校验，判断待删除的分类数据是否被使用
	 * 2、若正在被使用，则不能删除
	 * 3、若没有被使用，可以删除
	 *
	 * @Param: classifyId long
	 *         含义：采集任务分类表ID
	 *         取值范围：不可为空
	 * @return: 无
	 *
	 * */
	public void deleteClassifyInfo(long classifyId){
		//1、在数据库或中对待更新数据进行校验，判断待删除的分类数据是否被使用
		boolean flag = checkClassifyId(classifyId);
		//2、若正在被使用，则不能删除
		if(!flag){
			throw new BusinessException("待删除的采集任务分类已被使用，不能删除");
		}
		//3、若没有被使用，可以删除
		int nums = Dbo.execute("delete from " + Collect_job_classify.TableName + " where classify_id = ?"
				, classifyId);
		if(nums != 1) {
			if (nums == 0)
				throw new BusinessException("删除失败");
			else
				throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
		}
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
	public long saveDbConf(@RequestBean Database_set databaseSet) {
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
		//返回id的目的是为了在点击下一步跳转页面的时候拿到上一个页面的信息
		return databaseSet.getDatabase_id();
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
