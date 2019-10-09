package hrds.b.biz.agentinfo;

import fd.ng.core.utils.DateUtil;
import fd.ng.core.utils.StringUtil;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.AgentStatus;
import hrds.commons.codes.AgentType;
import hrds.commons.entity.Agent_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.DboExecute;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;

/**
 * agent增删改类
 *
 * @author dhw
 * @date 2019-09-04 17:30:27
 */
public class AgentInfoAction extends BaseAction {
	/**
	 * 保存agent信息
	 * <p>
	 * 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
	 * 2.字段合法性验证
	 * 3.判断端口是否被占用，被占用抛异常，否则正常保存
	 * 4.初始化AgentInfo的一些非页面传值
	 * 5.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口
	 * 6.保存agent信息
	 *
	 * @param agentInfo agent_info表对象
	 *                  含义：agent_info表实体对象
	 *                  取值范围：与数据库agent_info表字段定义规则一致
	 */
	public void saveAgent(@RequestBean Agent_info agentInfo) {
		// 1.数据可访问权限处理方式，新增时会设置创建用户ID，会获取当前用户ID，所以不需要权限验证
		// 2.字段合法性验证
		fieldLegalityValidation(agentInfo.getAgent_name(), agentInfo.getAgent_type(),
				agentInfo.getAgent_ip(), agentInfo.getAgent_port(), agentInfo.getSource_id()
				, agentInfo.getUser_id());
		// 3.判断端口是否被占用
		boolean flag = isPortOccupied(agentInfo.getAgent_ip(),
				Integer.parseInt(agentInfo.getAgent_port()));
		if (flag) {
			// 端口被占用，不可使用
			throw new BusinessException("端口被占用，agent_port=" + agentInfo.getAgent_port() + "," +
					"agent_ip =" + agentInfo.getAgent_ip());
		}
		// 4.初始化AgentInfo的一些非页面传值
		agentInfo.setAgent_id(PrimayKeyGener.getNextId());
		agentInfo.setAgent_status(AgentStatus.WeiLianJie.getCode());
		agentInfo.setCreate_time(DateUtil.getSysTime());
		agentInfo.setCreate_date(DateUtil.getSysDate());
		// 5.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口
		check(agentInfo.getSource_id(), agentInfo.getAgent_type(), agentInfo.getAgent_ip(),
				agentInfo.getAgent_port());
		// 6.保存agent信息
		agentInfo.add(Dbo.db());
	}

	/**
	 * 检查数据源是否还存在以及数据源下相同的IP地址中是否包含相同的端口
	 * <p>
	 * 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以不需要权限验证
	 * 2.验证数据源是否还存在,查到至少一条数据，查不到为0
	 * 3.判断数据源下相同的IP地址中是否包含相同的端口,查到至少一条数据，查不到为0
	 *
	 * @param sourceId  long
	 *                  含义：data_source表主键ID
	 *                  取值范围：10位数字，新增时自动生成
	 * @param agentType String
	 *                  含义：agent类型
	 *                  取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * @param agentIp   String
	 *                  含义：agent所在服务器ip
	 *                  取值范围：合法IP地址
	 * @param agentPort String
	 *                  含义：agent连接端口
	 *                  取值范围：1024-65535
	 */
	private void check(long sourceId, String agentType, String agentIp, String agentPort) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以不需要权限验证
		// 2.验证数据源是否还存在,查到至少一条数据，查不到为0
		if (Dbo.queryNumber("select count(1) from data_source where source_id = ?",
				sourceId).orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("该agent对应的数据源已不存在不可新增，source_id=" + sourceId);
		}
		// 3.判断数据源下相同的IP地址中是否包含相同的端口,查到至少一条数据，查不到为0
		if (Dbo.queryNumber("SELECT count(1) FROM agent_info WHERE source_id=? AND agent_type=?"
				+ " AND agent_ip=? AND agent_port=?", sourceId, agentType, agentIp, agentPort)
				.orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
			throw new BusinessException("该agent对应的数据源下相同的IP地址中包含相同的端口，" +
					"source_id=" + sourceId);
		}
	}

	/**
	 * 字段合法性验证
	 * <p>
	 * 1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证
	 * 2.验证agent_type是否为空或空格
	 * 3. 验证agent_name是否为空或空格
	 * 4.判断agent_ip是否是一个合法的ip
	 * 5.判断agent_port是否是一个有效的端口
	 * 6.验证user_id是否为空或空格
	 * 7.验证source_id是否为空或空格
	 *
	 * @param agentName String
	 *                  含义：agent名称
	 *                  取值范围：不为空以及空格
	 * @param agentType String
	 *                  含义：agent类型
	 *                  取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * @param agentIp   String
	 *                  含义：agent所在服务器ip
	 *                  取值范围：合法IP地址
	 * @param agentPort String
	 *                  含义：agent连接端口
	 *                  取值范围：1024-65535
	 * @param sourceId  Long
	 *                  含义：agent_info表外键ID，data_source表主键ID,定义为Long目的是判null
	 *                  取值范围：十位数字，新增数据源时自动生成
	 * @param userId    Long
	 *                  含义：数据采集用户ID,定义为Long目的是判null
	 *                  取值范围：四位数字，新增用户时自动生成
	 */
	private void fieldLegalityValidation(String agentName, String agentType, String agentIp, String agentPort,
	                                     Long sourceId, Long userId) {
		// 1.数据可访问权限处理方式，这是个私有方法，不会单独被调用，所以不需要权限验证
		// 2.验证agent_type是否为空或空格且是否合法，不合法该方法会直接抛异常
		AgentType.ofEnumByCode(agentType);
		if (StringUtil.isBlank(agentType)) {
			throw new BusinessException("agent_type不能为空且不能为空格，agent_type=" + agentType);
		}
		// 3.验证agent_name是否为空或空格
		if (StringUtil.isBlank(agentName)) {
			throw new BusinessException("agent_name不为空且不为空格，agent_name=" + agentName);
		}
		// 4.判断agent_ip是否是一个合法的ip
		String[] split = agentIp.split("\\.");
		for (int i = 0; i < split.length; i++) {
			int temp = Integer.parseInt(split[i]);
			if (temp < 0 || temp > 255) {
				throw new BusinessException("agent_ip不是一个为空或空格的ip地址," +
						"agent_ip=" + agentIp);
			}
		}
		// 5.判断agent_port是否是一个有效的端口
		// 端口范围最小值
		int min = 1024;
		// 端口范围最大值
		int max = 65535;
		if (Integer.parseInt(agentPort) < min || Integer.parseInt(agentPort) > max) {
			throw new BusinessException("agent_port端口不是有效的端口，不在取值范围内，" +
					"agent_port=" + agentPort);
		}
		// 6.验证user_id是否为空或空格
		if (userId == null) {
			throw new BusinessException("user_id不为空且不为空格");
		}
		// 7.验证source_id是否为空或空格
		if (sourceId == null) {
			throw new BusinessException("source_id不为空且不为空格");
		}
	}

	/**
	 * 更新agent信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过关联agent_id与user_id检查
	 * 2.验证agent_id是否合法
	 * 3.字段合法性验证
	 * 4.创建agent_info实体对象，同时封装值
	 * 5.创建agent_info实体对象，同时封装值
	 * 6.更新agent信息
	 *
	 * @param agentId   Long
	 *                  含义：agent_info主键ID,定义为Long为了判断是否为空
	 *                  取值范围：十位数字，新增时自动生成
	 * @param agentName String
	 *                  含义：agent名称
	 *                  取值范围：不为空以及空格
	 * @param agentType String
	 *                  含义：agent类型
	 *                  取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * @param agentIp   String
	 *                  含义：agent所在服务器ip
	 *                  取值范围：合法IP地址
	 * @param agentPort String
	 *                  含义：agent连接端口
	 *                  取值范围：1024-65535
	 * @param sourceId  long
	 *                  含义：agent_info表外键ID，data_source表主键ID
	 *                  取值范围：十位数字，新增数据源时自动生成
	 * @param userId    long
	 *                  含义：数据采集用户ID
	 *                  取值范围：四位数字，新增用户时自动生成
	 */
	public void updateAgent(Long agentId, String agentName, String agentType, String agentIp
			, String agentPort, long sourceId, long userId) {
		// 1.数据可访问权限处理方式，通过关联agent_id与user_id检查
		if (Dbo.queryNumber("select count(1) from agent_info where agent_id=? and user_id=?",
				agentId, userId).orElseThrow(() -> new BusinessException("sql查询错误")) > 0) {
			throw new BusinessException("数据权限校验失败，数据不可访问！");
		}
		// 2.验证agent_id是否合法
		if (agentId == null) {
			throw new BusinessException("agent_id为一个10位数字，不能为空，新增时自动生成");
		}
		// 3.字段合法性验证
		fieldLegalityValidation(agentName, agentType, agentIp, agentPort, sourceId, userId);
		// 4.创建agent_info实体对象，同时封装值
		Agent_info agentInfo = new Agent_info();
		agentInfo.setAgent_id(agentId);
		agentInfo.setUser_id(userId);
		agentInfo.setSource_id(sourceId);
		agentInfo.setAgent_ip(agentIp);
		agentInfo.setAgent_port(agentPort);
		agentInfo.setAgent_type(agentType);
		agentInfo.setAgent_name(agentName);
		// 5.检查数据源是否还存在以及判断数据源下相同的IP地址中是否包含相同的端口
		check(sourceId, agentType, agentIp, agentPort);
		// 6.更新agent信息
		if (agentInfo.update(Dbo.db()) != 1) {
			throw new BusinessException("更新表信息失败," + "agent_port=" + agentInfo.getAgent_port()
					+ ",agent_ip =" + agentInfo.getAgent_ip() + ",agent_name=" + agentInfo.getAgent_name());
		}
	}

	/**
	 * 监控agent端口是否被占用（后期移动到hrds-commons下）
	 * <p>
	 * 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
	 * 2.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
	 *
	 * @param agentIp   String
	 *                  含义： agent地址
	 *                  取值范围：不为空，服务器地址
	 * @param agentPort int
	 *                  含义：agent端口
	 *                  取值范围：1024-65535
	 * @return boolean
	 * 含义：返回端口是否被占用信号
	 * 取值范围：false,true
	 */
	private boolean isPortOccupied(String agentIp, int agentPort) {
		// 1.数据可访问权限处理方式，这是一个私有方法，不会单独被调用，所以这里不需要做权限验证
		// 2.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
		String url = "http://".concat(agentIp).concat(":").concat(agentPort + "");
		HttpClient.ResponseValue post = new HttpClient().post(url);
		// 状态值
		int code = 200;
		if (post.getCode() != code) {
			// 未连通，端口可用
			return false;
		} else {
			// 连通，端口被使用中
			return true;
		}
	}

	/**
	 * 查询Agent信息
	 * <p>
	 * 1.数据可访问权限处理方式，通过agent_id,agent_type，user_id关联检查
	 * 2.根据agent_id与agent_type查询该agent信息
	 *
	 * @param agentId   long
	 *                  含义：agent_info表主键
	 *                  取值范围：不为空且不为空格，长度不超过10
	 * @param agentType String
	 *                  含义：agent类型
	 *                  取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * @return java.util.List
	 * 含义：返回根据agent_id与agent_type查询该agent_info信息集合
	 * 取值范围：无限制
	 */
	public List<Agent_info> searchAgent(long agentId, String agentType) {
		// 1.数据可访问权限处理方式，通过agent_id,agent_type，user_id关联检查
		// 2.根据agent_id与agent_type查询该agent信息
		return Dbo.queryList(Agent_info.class, " SELECT * FROM agent_info WHERE agent_id = ? " +
				" AND agent_type = ? and user_id=?", agentId, agentType, getUserId());
	}

	/**
	 * 删除agent
	 * <p>
	 * 1.数据可访问权限处理方式，以下SQL关联user_id检查
	 * 2.删除前查询此agent是否已部署，已部署不能删除
	 * 3.判断此数据源与agent下是否有任务，有任务不能删除
	 * 4.删除agent
	 *
	 * @param agentId   long
	 *                  含义：agent_info表主键
	 *                  取值范围：不为空且不为空格，长度不超过10
	 * @param agentType String
	 *                  含义：agent类型
	 *                  取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 */
	public void deleteAgent(Long agentId, String agentType) {
		// 1.数据可访问权限处理方式，通过agent_id,agent_type,user_id关联检查
		if (Dbo.queryNumber("select count(*) from agent_info where agent_id=? and user_id=? " +
				"and agent_type=?", agentId, getUserId(), agentType)
				.orElseThrow(() -> new BusinessException("sql查询错误！")) == 0) {
			throw new BusinessException("数据可访问权限校验失败，数据不可访问");
		}
		// 2.删除前查询此agent是否已部署
		if (Dbo.queryNumber("select count(1) from agent_down_info where agent_id=?", agentId)
				.orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
			// 此agent已部署不能删除
			throw new BusinessException("此agent已部署不能删除");
		}
		// 3.判断此数据源与agent下是否有任务
		if (Dbo.queryNumber(" SELECT count(1) FROM agent_info t1 join database_set t2 on " +
						" t1.agent_id=t2.agent_id WHERE  t1.agent_id=? and  t1.agent_type=?",
				agentId, agentType).orElseThrow(() -> new BusinessException("sql查询错误！")) > 0) {
			// 此数据源与agent下有任务，不能删除
			throw new BusinessException("此数据源对应的agent下有任务，不能删除");
		}
		// 4.删除agent
		DboExecute.deletesOrThrow("删除表信息失败，agent_id=" + agentId + ",agent_type=" + agentType,
				"delete from agent_info where agent_id=?", agentId);
	}

}

