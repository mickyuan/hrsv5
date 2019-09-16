package hrds.b.biz.agentinfo;

import fd.ng.netclient.http.HttpClient;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.exception.BusinessException;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;

import java.util.List;
import java.util.Map;

/**
 * agent增删改类
 *
 * @author mine
 * @date 2019-09-04 17:30:27
 */
public class AgentInfoAction extends BaseAction {
	/**
	 * 保存agent信息
	 * <p>
	 * 1.判断端口是否被占用，被占用抛异常，否则正常保存
	 * 2.判断agent编号是否为空，为空则新增，不为空则编辑
	 * 3.保存或更新agent信息
	 *
	 * @param agentInfo agent_info表对象
	 *                  含义：agent_info表实体对象
	 *                  取值范围：与数据库agent_info表字段定义规则一致
	 */
	public void saveAgent(@RequestBean Agent_info agentInfo) {
		// 1.判断端口是否被占用
		boolean flag = isPortOccupied(agentInfo.getAgent_ip(),
				Integer.parseInt(agentInfo.getAgent_port()));
		if (flag) {
			// 端口被占用不可使用
			throw new BusinessException("端口被占用，agent_port=" + agentInfo.getAgent_port() +
					",agent_ip =" + agentInfo.getAgent_ip() +
					",agent_name=" + agentInfo.getAgent_name());
		}
		// 2.判断agent编号是否为空
		if (agentInfo.getAgent_id() == null) {
			// 2.为空，新增
			agentInfo.setSource_id(PrimayKeyGener.getNextId());
			agentInfo.setUser_id(ActionUtil.getUser().getUserId());
			// 3.保存agent信息
			if (agentInfo.add(Dbo.db()) != 1) {
				throw new BusinessException("新增agent_info表信息失败," +
						"agent_port=" + agentInfo.getAgent_port() +
						",agent_ip =" + agentInfo.getAgent_ip() +
						",agent_name=" + agentInfo.getAgent_name());
			}
		} else {
			// 2.不为空，编辑
			// 3.更新agent信息
			if (agentInfo.update(Dbo.db()) != 1) {
				throw new BusinessException("编辑保存agent_info表信息失败," +
						"agent_port=" + agentInfo.getAgent_port() +
						",agent_ip =" + agentInfo.getAgent_ip() +
						",agent_name=" + agentInfo.getAgent_name());
			}
		}
	}

	/**
	 * 监控agent端口是否被占用（后期移动到hrds-commons下）
	 * <p>
	 * 1.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
	 *
	 * @param agent_ip   String
	 *                   含义： agent地址
	 *                   取值范围：不为空，服务器地址
	 * @param agent_port int
	 *                   含义：agent端口
	 *                   取值范围：1024-65535
	 * @return 返回端口是否被占用信号
	 */
	private boolean isPortOccupied(String agent_ip, int agent_port) {

		// 1.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
		HttpClient httpClient = new HttpClient();
		String url = "http://".concat(agent_ip).concat(":").concat(agent_port + "");
		HttpClient.ResponseValue post = httpClient.post(url);

		if (post.getCode() != 200) {
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
	 * 1.根据agent_id与agent_type查询该agent信息
	 *
	 * @param agent_id   long
	 *                   含义：agent_info表主键
	 *                   取值范围：不为空以及不为空格，长度不超过10
	 * @param agent_type String
	 *                   含义：agent类型
	 *                   取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 * @return 返回根据agent_id与agent_type查询该agent信息结果
	 */
	public List<Map<String, Object>> searchAgent(long agent_id, String agent_type) {
		// 1.根据agent_id与agent_type查询该agent信息
		return Dbo.queryList(" SELECT * FROM agent_info WHERE agent_id = ? AND agent_type = ?",
				agent_id, agent_type);
	}

	/**
	 * 删除agent
	 * <p>
	 * 1.删除前查询此agent是否已部署，已部署不能删除
	 * 2.判断此数据源与agent下是否有任务，有任务不能删除
	 * 3.删除agent
	 *
	 * @param agent_id   long
	 *                   含义：agent_info表主键
	 *                   取值范围：不为空以及不为空格，长度不超过10
	 * @param agent_type String
	 *                   含义：agent类型
	 *                   取值范围：1:数据库Agent,2:文件系统Agent,3:FtpAgent,4:数据文件Agent,5:对象Agent
	 */
	public void deleteAgent(Long agent_id, String agent_type) {

		// 1.删除前查询此agent是否已部署
		if (Dbo.queryNumber("select * from agent_down_info where agent_id=?",
				agent_id).orElse(-1) > 0) {
			// 此agent已部署不能删除
			throw new BusinessException("此agent已部署不能删除");
		}
		// 2.判断此数据源与agent下是否有任务
		if (Dbo.queryNumber(" SELECT task_name FROM agent_info t1 join database_set t2 on " +
						"t1.agent_id=t2.agent_id WHERE  t1.agent_id=? and  t1.agent_type=?",
				agent_id, agent_type).orElse(-1) > 0) {
			// 此数据源与agent下有任务，不能删除
			throw new BusinessException("此数据源与agent下有任务，不能删除");
		}

		// 3.删除agent
		int num = Dbo.execute("delete  from agent_info where agent_id=?", agent_id);
		if (num != 1) {
			// 3.判断库里是否没有这条数据
			if (num == 0) {
				throw new BusinessException("删除agent_info表信息失败，数据库里没有此条数据，" +
						"agent_id=" + agent_id + ",agent_type=" + agent_type);
			}
			throw new BusinessException("删除agent_info表信息失败，agent_id=" + agent_id
					+ ",agent_type=" + agent_type);
		}
	}

}

