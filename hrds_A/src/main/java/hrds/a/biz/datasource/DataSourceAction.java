package hrds.a.biz.datasource;

import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Agent_info;
import hrds.commons.entity.Data_source;
import hrds.commons.entity.Source_relation_dep;
import hrds.commons.exception.BusinessException;
import hrds.commons.exception.ExceptionEnum;
import hrds.commons.utils.ActionUtil;
import hrds.commons.utils.key.PrimayKeyGener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DataSource class
 *
 * @author mine
 * @date 2019-08-22 16:29:19
 */
public class DataSourceAction extends BaseAction {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 新增/编辑数据源
     *
     * 1.判断数据源编号是否为空，为空则为新增，不为空则为编辑
     * 2.新增前查询数据源编号是否已存在，存在则抛异常，不存在就新增
     * 3.保存数据源与部门关系信息
     *
     * @param dataSource 数据源编号
     */
    public void saveDataSource(@RequestBean Data_source dataSource, String dep_id) {

        // 新增数据源
        if (StringUtil.isBlank(dataSource.getSource_id().toString())) {
            // 新增
            dataSource.setSource_id(PrimayKeyGener.getNextId());
            dataSource.setUser_id(ActionUtil.getUser().getUserId());
            // 新增前查询数据源编号是否已存在
            Result result = Dbo.queryResult("select datasource_number from " + Data_source.TableName +
                    "  where datasource_number=?", dataSource.getDatasource_number());
            if (!result.isEmpty()) {
                // 数据源编号重复（稍后在异常枚举类中增加新异常后替换）
                throw new BusinessException(ExceptionEnum.OTHER_ERROR);
            } else {
                if (dataSource.add(Dbo.db()) != 1) {
                    // 新增保存失败
                    throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
                }
            }
        } else {
            // 编辑
            if (dataSource.update(Dbo.db()) != 1) {
                // 编辑保存失败
                throw new BusinessException(ExceptionEnum.DATA_UPDATE_ERROR);
            }
        }
        // 保存数据源与部门关系信息
        saveSourceRelationDep(dataSource.getSource_id(), dep_id);
    }

    /**
     * 保存数据源与部门关系表信息
     *
     * 1.先删除此数据源下的数据源与部门关系信息
     * 2.重新建立数据源与部门关系信息
     *
     * @param source_id 数据源编号
     * @param dep_id    部门编号
     * @return
     */
    public void saveSourceRelationDep(Long source_id, String dep_id) {
        // 更新数据源与部门关系表信息
        // 先删除数据源与部门关系信息
        int num = Dbo.execute("delete from " + Source_relation_dep.TableName +
                " where source_id=?", source_id);
        if (num != 1) {
            throw new BusinessException(ExceptionEnum.DATA_DELETE_ERROR);
        }
        // 重新建立数据源与部门关系信息
        Source_relation_dep srd = new Source_relation_dep();
        srd.setSource_id(source_id);
        String[] depIds = dep_id.split(",");
        for (String depId : depIds) {
            srd.setDep_id(Long.parseLong(dep_id));
            if (srd.add(Dbo.db()) != 1) {
                throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
            }
        }
    }

    /**
     * 编辑前根据数据源编号查询数据源及数据源与部门关系信息
     *
     * 1.判断数据源编号是否为空，不为空则抛异常
     * 2.查询该数据源下的数据源及数据源与部门关系信息
     * 3.判断该数据源下是否有数据，没有抛异常，有则返回查询结果
     *
     * @param source_id 数据源编号
     * @return
     */
    public Result searchDataSource(Long source_id) {
        if (StringUtil.isBlank(source_id.toString())) {
            // 数据源编号不能为空
            throw new BusinessException(ExceptionEnum.OTHER_ERROR.getCode(),
                    ExceptionEnum.OTHER_ERROR.getMessage());
        }

        // 查询该数据源下的数据源及数据源与部门关系信息
        Result result = Dbo.queryResult("select ds.*,srd.dep_id from data_source ds " +
                "join source_relation_dep srd on ds.source_id=srd.source_id where ds.source_id = ?", source_id);
        if (result.isEmpty()) {
            // 该数据源下数据为空
            throw new BusinessException(ExceptionEnum.DATA_NOT_EXIST);
        } else {
            // 不为空，返回查询结果
            return result;
        }

    }

    /**
     * 删除数据源与部门关系表信息
     *
     * 1.删除数据源与部门关系表信息
     * 2.失败就抛异常，否则就正常删除
     *
     * @param source_id 数据源编号
     */
    public void deleteSourceRelationDep(Long source_id) {
        // 删除数据源与部门关系表信息
        Dbo.queryNumber("delete from " + Source_relation_dep.TableName + " where source_id=?", source_id)
                .orElseThrow(() -> new BusinessException(ExceptionEnum.DATA_DELETE_ERROR));
    }

    /**
     * 删除数据源信息
     *
     * 1.先查询该datasource下是否还有agent,有不能删除，没有，可以删除
     * 2.删除data_source表信息，删除失败就抛异常，否则正常删除
     *
     * @param source_id 数据源编号
     */
    public void deleteDataSource(Long source_id) {

        // 先查询该datasource下是否还有agent,有不能删除，没有，可以删除
        Result result = Dbo.queryResult("SELECT * FROM agent_info WHERE source_id=? ", source_id);
        if (!result.isEmpty()) {
            // 此数据源下还有agent，不能删除(稍后定义新异常替换)
            throw new BusinessException(ExceptionEnum.OTHER_ERROR);
        }

        // 删除data_source表信息
        Dbo.queryNumber("delete from " + Data_source.TableName + " where source_id=?", source_id)
                .orElseThrow(() -> new BusinessException(ExceptionEnum.DATA_DELETE_ERROR));
        // 删除source_relation_dep信息
        deleteSourceRelationDep(source_id);

    }

    /**
     * 保存agent信息
     *
     * 1.判断端口是否被占用，被占用抛异常，否则正常保存
     * 2.判断agent编号是否为空，为空则新增，不为空则编辑
     *
     * @param agentInfo agent实体对象
     * @return
     */
    public void saveAgent(@RequestBean Agent_info agentInfo) {
        boolean flag = monitorPort(agentInfo.getAgent_ip(), Integer.parseInt(agentInfo.getAgent_port()));
        if (flag) {
            // 端口不可使用（稍后在异常枚举类中增加新异常）
            throw new BusinessException(ExceptionEnum.OTHER_ERROR);
        } else {
            if (StringUtil.isBlank(agentInfo.getAgent_id().toString())) {
                // 新增
                agentInfo.setSource_id(PrimayKeyGener.getNextId());
                agentInfo.setUser_id(ActionUtil.getUser().getUserId());
                if (agentInfo.add(Dbo.db()) != 1) {
                    throw new BusinessException(ExceptionEnum.DATA_ADD_ERROR);
                }
            } else {
                // 编辑
                if (agentInfo.update(Dbo.db()) != 1) {
                    throw new BusinessException(ExceptionEnum.DATA_UPDATE_ERROR);
                }
            }
        }
    }

    /**
     * 监控agent端口是否被占用
     *
     * 1.通过http方式去测试端口连通情况，测通则被占用，不通则可以使用
     *
     * @param ip
     * @param port
     * @return
     */
    public boolean monitorPort(String ip, int port) {

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

}
