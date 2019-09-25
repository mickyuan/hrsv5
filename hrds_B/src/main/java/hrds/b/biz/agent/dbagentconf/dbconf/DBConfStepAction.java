package hrds.b.biz.agent.dbagentconf.dbconf;

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
import hrds.b.biz.agent.tools.ConnUtil;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.CleanType;
import hrds.commons.codes.DatabaseType;
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
	public Result getDBConfInfo(long databaseId, long userId) {
		//1、在数据库设置表(database_set)中，根据databaseId判断是否查询到数据，如果查询不到，抛异常给前端
		Database_set dbSet = Dbo.queryOneObject(Database_set.class,
				"SELECT das.* " +
						" FROM "+ Data_source.TableName +" ds " +
						" JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
						" JOIN "+ Database_set.TableName +" das ON ai.agent_id = das.agent_id " +
						" WHERE das.database_id = ? AND ds.create_user_id = ? "
				, databaseId, userId)
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
	 * 1、在collect_job_classify表中查询传入的classifyId是否存在
	 * 2、如果存在，在数据库中查询database_set表中是否有使用到当前classifyId的数据
	 * 3、如果有，返回false，表示该分类被使用，不能编辑
	 * 4、如果没有，返回true，表示该分类没有被使用，可以编辑
	 *
	 * @Param: classifyId long
	 *         含义：采集任务分类表ID
	 *         取值范围：不可为空
	 * @return: boolean
	 *          含义：该分类是否可以被编辑
	 *          取值范围：返回false，表示该分类被使用，不能编辑；返回true，表示该分类没有被使用，可以编辑
	 *
	 * */
	public boolean checkClassifyId(long classifyId, long userId){
		//1、在collect_job_classify表中查询传入的classifyId是否存在
		long count = Dbo.queryNumber(" SELECT count(1) FROM " + Collect_job_classify.TableName + " " +
				"WHERE classify_id = ? AND user_id = ? ", classifyId, userId).orElseThrow(() -> new BusinessException("查询结果必须有且只有一条"));
		if(count != 1){
			throw new BusinessException("采集作业分类信息不存在");
		}
		//2、在数据库中查询database_set表中是否有使用到当前classifyId的数据
		long val = Dbo.queryNumber("SELECT count(1) FROM " + Data_source.TableName + " ds" +
				" JOIN " + Agent_info.TableName + " ai ON ds.source_id = ai.source_id " +
				" JOIN " + Database_set.TableName + " das ON ai.agent_id = das.agent_id " +
				" WHERE das.classify_id = ? AND ds.create_user_id = ? ", classifyId, userId)
				.orElseThrow(() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制

		//3、如果有，返回false，表示该分类被使用，不能编辑
		//4、如果没有，返回true，表示该分类没有被使用，可以编辑
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
	public List<Collect_job_classify> getClassifyInfo(long sourceId, long userId){
		//1、在数据库中查询相应的信息并返回
		return Dbo.queryList(Collect_job_classify.class, "SELECT cjc.* FROM "+ Data_source.TableName +" ds " +
				" JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id" +
				" JOIN "+ Collect_job_classify.TableName +" cjc ON ai.agent_id = cjc.agent_id" +
				" WHERE ds.source_id = ? AND cjc.user_id = ? order by cjc.classify_num "
				, sourceId, userId);
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
	}

	/**
	 * 保存采集任务分类信息
	 *
	 * 1、对传入的数据进行判断，对不能为空的字段进行校验，如果不合法，提供明确的提示信息
	 * 2、在数据库或中对新增数据进行校验
	 * 3、分类编号重复抛异常给前台
	 * 4、分类编号不重复可以新增
	 * 5、给新增数据设置ID
	 * 6、完成新增
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
	public void saveClassifyInfo(@RequestBean Collect_job_classify classify, long sourceId, long userId){
		//1、对传入的数据进行判断，对不能为空的字段进行校验，如果不合法，提供明确的提示信息
		verifyClassifyEntity(classify, true);
		//2、在数据库或中对新增数据进行校验
		long val = Dbo.queryNumber("SELECT count(1) FROM "+ Collect_job_classify.TableName +" cjc " +
						" LEFT JOIN "+ Agent_info.TableName +" ai ON cjc.agent_id=ai.agent_id" +
						" LEFT JOIN "+ Data_source.TableName +" ds ON ds.source_id=ai.source_id" +
						" WHERE cjc.classify_num=? AND ds.source_id=? AND ds.create_user_id = ? ",
				classify.getClassify_num(), sourceId, userId).orElseThrow(
				() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
		//3、分类编号重复抛异常给前台
		if(val != 0){
			throw new BusinessException("分类编号重复，请重新输入");
		}
		//4、分类编号不重复可以新增
		//5、给新增数据设置ID
		classify.setClassify_id(PrimayKeyGener.getNextId());
		//6、完成新增
		if (classify.add(Dbo.db()) != 1)
			throw new BusinessException("保存分类信息失败！data=" + classify);
	}

	/**
	 * 更新采集任务分类信息
	 *
	 * 1、对传入的数据进行判断，对不能为空的字段进行校验，如果不合法，提供明确的提示信息
	 * 2、在数据库或中对待更新数据进行校验，判断待更新的数据是否存在
	 * 3、不存在抛异常给前台
	 * 4、存在则校验更新后的分类编号是否重复
	 * 5、完成更新操作
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
	public void updateClassifyInfo(@RequestBean Collect_job_classify classify, long sourceId, long userId){
		//1、对传入的数据进行判断，对不能为空的字段进行校验，如果不合法，提供明确的提示信息
		verifyClassifyEntity(classify, false);
		//2、在数据库或中对待更新数据进行校验，判断待更新的数据是否存在
		long val = Dbo.queryNumber("SELECT count(1) FROM "+ Collect_job_classify.TableName +" cjc " +
						" LEFT JOIN "+ Agent_info.TableName +" ai ON cjc.agent_id=ai.agent_id" +
						" LEFT JOIN "+ Data_source.TableName +" ds ON ds.source_id=ai.source_id" +
						" WHERE cjc.classify_id=? AND ds.source_id=? AND ds.create_user_id = ? ",
				classify.getClassify_id(), sourceId, userId).orElseThrow(
				() -> new BusinessException("查询得到的数据必须有且只有一条"));
		//数据可访问权限处理方式
		//以上SQL中，通过当前用户ID进行关联查询，达到了数据权限的限制
		//3、不存在抛异常给前台
		if(val != 1){
			throw new BusinessException("待更新的数据不存在");
		}
		//4、存在则校验更新后的分类编号是否重复
		long count = Dbo.queryNumber("SELECT count(1) FROM "+ Collect_job_classify.TableName +" cjc " +
						" LEFT JOIN "+ Agent_info.TableName +" ai ON cjc.agent_id=ai.agent_id" +
						" LEFT JOIN "+ Data_source.TableName +" ds ON ds.source_id=ai.source_id" +
						" WHERE cjc.classify_num=? AND ds.source_id=? AND ds.create_user_id = ? ",
				classify.getClassify_num(), sourceId, userId).orElseThrow(
				() -> new BusinessException("查询得到的数据必须有且只有一条"));
		if(count != 0){
			throw new BusinessException("分类编号重复，请重新输入");
		}
		//5、存在则完成更新
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
	public void deleteClassifyInfo(long classifyId, long userId){
		//1、在数据库或中对待更新数据进行校验，判断待删除的分类数据是否被使用
		boolean flag = checkClassifyId(classifyId, userId);
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
	 * 1、调用方法对传入数据的合法性进行校验
	 * 2、获取实体中的database_id
	 * 3、如果存在，则更新信息
	 * 4、如果不存在，则新增信息
	 *
	 * @Param: databaseSet Database_set
	 *         含义：待保存的Database_set实体对象
	 *         取值范围：不为空
	 * @return: 无
	 *
	 * */
	public long saveDbConf(@RequestBean Database_set databaseSet, long userId) {
		//1、调用方法对传入数据的合法性进行校验
		verifyDatabaseSetEntity(databaseSet);
		//2、获取实体中的database_id
		if(databaseSet.getDatabase_id() != null){
			//3、如果存在，则更新信息
			long val = Dbo.queryNumber("SELECT count(1)" +
					" FROM "+ Data_source.TableName +" ds " +
					" JOIN "+ Agent_info.TableName +" ai ON ds.source_id = ai.source_id " +
					" JOIN "+ Database_set.TableName +" das ON ai.agent_id = das.agent_id " +
					" WHERE das.database_id = ? AND ds.create_user_id = ?",
					databaseSet.getDatabase_id(), userId).orElseThrow(
					() -> new BusinessException("查询得到的数据必须有且只有一条"));
			if(val != 1){
				throw new BusinessException("待更新的数据不存在");
			}
			if (databaseSet.update(Dbo.db()) != 1){
				throw new BusinessException("更新数据失败！data=" + databaseSet);
			}
		}
		else {
			//4、如果不存在，则新增信息
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
		//返回id的目的是为了在点击下一步跳转页面的时候能通过database_id拿到上一个页面的信息
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
	 *         取值范围：Database_set实体类对象，不为空
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

	/**
	 * 新增/更新操作校验Collect_job_classify中数据的合法性，对数据库中不能为空的字段，校验合法性，若不合法，提供明确的提示信息
	 *
	 * 1、对于新增操作，校验classify_id不能为空
	 * 2、校验classify_num不能为空
	 * 3、校验classify_name不能为空
	 * 4、校验user_id不能为空
	 * 5、校验Agent_id不能为空
	 *
	 * @Param: entity Collect_job_classify
	 *         含义：存有classify_num、classify_name、user_id、Agent_id的Collect_job_classify实体类对象
	 *         取值范围：Collect_job_classify实体类对象，不为空
	 *
	 * @Param: isAdd boolean
	 *         含义：新增/更新的标识位
	 *         取值范围：true为新增，false为更新
	 *
	 * @return: 无
	 *
	 * */
	private void verifyClassifyEntity(Collect_job_classify entity, boolean isAdd){
		//1、对于新增操作，校验classify_id不能为空
		if(!isAdd){
			if(entity.getClassify_id() == null){
				throw new BusinessException("分类id不能为空");
			}
		}
		//2、校验classify_num不能为空
		if(StringUtil.isBlank(entity.getClassify_num())){
			throw new BusinessException("分类编号不能为空");
		}
		//3、校验classify_name不能为空
		if(StringUtil.isBlank(entity.getClassify_name())){
			throw new BusinessException("分类名称不能为空");
		}
		//4、校验user_id不能为空
		if(entity.getUser_id() == null){
			throw new BusinessException("用户ID不能为空");
		}
		//5、校验Agent_id不能为空
		if(entity.getAgent_id() == null){
			throw new BusinessException("AgentID不能为空");
		}
		//数据可访问权限处理方式
		//该方法不与数据库交互，无需校验用户访问权限
	}

	/**
	 * 保存数据库配置页面时，校验Database_set中数据的合法性，对数据库中不能为空的字段，校验合法性，若不合法，提供明确的提示信息
	 *
	 * 1、校验database_type不能为空，并且取值范围必须在DatabaseType代码项中
	 * 2、校验classify_id不能为空
	 *
	 * @Param: databaseSet Database_set
	 *         含义：存有待保存信息的Database_set实体类对象
	 *         取值范围：Database_set实体类对象，不为空
	 *
	 *
	 * @return: 无
	 *
	 * */
	private void verifyDatabaseSetEntity(Database_set databaseSet){
		//1、校验database_type不能为空，并且取值范围必须在DatabaseType代码项中
		if(StringUtil.isBlank(databaseSet.getDatabase_type())){
			throw new BusinessException("保存数据库配置信息时数据库类型不能为空");
		}
		if(DatabaseType.ofEnumByCode(databaseSet.getDatabase_type()) == null){
			throw new BusinessException("系统不支持的数据库类型，请重新选择");
		}
		//2、校验classify_id不能为空
		if(databaseSet.getClassify_id() == null){
			throw new BusinessException("保存数据库配置信息时分类信息不能为空");
		}
	}

}
