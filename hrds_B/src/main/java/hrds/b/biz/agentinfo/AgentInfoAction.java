package hrds.b.biz.agentinfo;

import com.alibaba.fastjson.JSONObject;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.exception.AppSystemException;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.core.util.Assert;

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
     * @param agentInfo agent实体对象
     * @return
     */
    public void saveAgent(@RequestBean Agent_info agentInfo) {
        // 1.判断端口是否被占用
        boolean flag = isPortOccupied(agentInfo.getAgent_ip(), Integer.parseInt(agentInfo.getAgent_port()));
        if (flag) {
            // 端口被占用不可使用
            throw new BusinessException("端口被占用");
        }
        // 2.判断agent编号是否为空
        if (agentInfo.getAgent_id() == null) {
            // 2.为空，新增
            agentInfo.setSource_id(PrimayKeyGener.getNextId());
            agentInfo.setUser_id(ActionUtil.getUser().getUserId());
            // 3.保存agent信息
            if (agentInfo.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        } else {
            // 2.不为空，编辑
            // 3.更新agent信息
            if (agentInfo.update(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_UPDATE_ERROR);
            }
        }

    }

    /**
     * 监控agent端口是否被占用
     * <p>
     * 1.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
     *
     * @param ip
     * @param port
     * @return
     */
    public boolean isPortOccupied(String ip, int port) {

        // 1.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
        HttpClient httpClient = new HttpClient();
        String url = "http://".concat(ip).concat(":").concat(port + "");
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
     * 编辑前查看Agent详情
     *
     * 1.编辑前查询该agent信息，为空抛异常，不为空返回查询结果
     *
     * @param agent_id agent编号
     * @param agent_type agent类型
     * @return
     */
    public Result searchAgent(String agent_id, String agent_type) {
        // 1.编辑前查询该agent信息
        Result result = Dbo.queryResult(" SELECT * FROM agent_info WHERE agent_id = ? AND agent_type = ?",
                agent_id, agent_type);
        if (result.isEmpty()) {
            // 该数据源下数据为空(此为编辑情况下数据不能为空）
            throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
        }
        // 不为空，返回查询结果
        return result;
    }

    /**
     * 删除agent
     * <p>
     * 1.删除前查询此agent是否已部署，已部署不能删除
     * 2.判断此数据源与agent下是否有任务，有任务不能删除
     * 3.删除agent
     *
     * @param agent_id
     * @param agent_type
     */
    public void deleteAgent(Long agent_id, String agent_type) {

        // 1.删除前查询此agent是否已部署
        Map<String, Object> map = Dbo.queryOneObject("select * from agent_down_info where agent_id=?", agent_id);
        if ("0".equals(map.get("deploy"))) {
            // 此agent已部署不能删除
            throw new BusinessException("此agent已部署不能删除");
        }
        // 2.判断此数据源与agent下是否有任务
        Result result = Dbo.queryResult(" SELECT task_name FROM agent_info t1 join database_set t2 on t1.agent_id=t2.agent_id WHERE" +
                "  t1.agent_id=? and  t1.agent_type=?", agent_id, agent_type);
        if (!result.isEmpty()) {
            // 此数据源与agent下有任务，不能删除
            throw new BusinessException("此数据源与agent下有任务，不能删除");
        }

        // 3.删除agent
        int num = Dbo.execute("delete  from agent_info where agent_id=?", agent_id);
        if (num != 1) {
            throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
        }
    }

}

