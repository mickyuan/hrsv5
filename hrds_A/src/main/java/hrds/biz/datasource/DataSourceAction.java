package hrds.biz.datasource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.netclient.http.HttpClient;
import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.Action;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.entity.AgentInfo;
import hrds.entity.SourceRelationDep;
import hrds.entity.DataSource;
import hrds.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Random;

/**
 * DataSource class
 *
 * @author mine
 * @date 2019-08-22 16:29:19
 */
public class DataSourceAction extends AbstractWebappBaseAction {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

    }

    /**
     * 新增/编辑数据源
     */
    public String saveDataSource(@RequestBean DataSource dataSource, String dep_id) {
        // 新增数据源
        String status = "0";
        if (StringUtil.isBlank(dataSource.getSource_id().toString())) {
            // 新增
            dataSource.setSource_id(new BigDecimal(1000000000));
            // 新增前查询数据源编号是否已存在
            Result result = SqlOperator.queryResult(new DatabaseWrapper(), "select datasource_number from " + DataSource.TableName + "  where datasource_number=?", dataSource.getDatasource_number());
            if (!result.isEmpty()) {
                // 数据源编号重复
                status = "1";
            } else {
                if (dataSource.add(Dbo.db()) != 1) {
                    // 新增保存失败
                    status = "2";
                }
            }
        } else {
            // 编辑
            if (dataSource.update(Dbo.db()) != 1) {
                // 编辑保存失败
                status = "2";
            }
        }
        // 如果前面已经失败，就没必要再执行后面的
        if ("0".equals(status)) {
            // 新增数据源与部门关系表信息
            status = saveSourceRelationDep(dataSource.getSource_id().toString(), dep_id);
        }
        return status;

    }

    /**
     * 保存数据源与部门关系表信息
     *
     * @param source_id 数据源编号
     * @param dep_id    部门编号
     * @return
     */
    public String saveSourceRelationDep(String source_id, String dep_id) {
        // 更新数据源与部门关系表信息
        // 先删除关系，再建立新关系
        String status = deleteSourceRelationDep(source_id.toString());
        if ("0".equals(status)) {
            // 删除数据源与部门关系表信息未出现问题
            SourceRelationDep srd = new SourceRelationDep();
            srd.setSource_id(new BigDecimal(source_id));
            String[] depIds = dep_id.split(",");
            for (String depId : depIds) {
                srd.setDep_id(new BigDecimal(depId));
                if (srd.add(Dbo.db()) != 1) {
                    status = "1";
                    break;
                }
            }
        }
        return status;
    }

    /**
     * 编辑前根据数据源编号查询数据源及数据源与部门关系信息
     *
     * @param source_id 数据源编号
     * @return
     */
    public JsonObject searchDataSource(String source_id) {
        JsonObject jsonObject = new JsonObject();
        String status = "0";
        if (StringUtil.isBlank(source_id)) {
            // 数据源编号不能为空
            status = "1";
            jsonObject.addProperty("status", status);
            return jsonObject;
        }
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            Result result = SqlOperator.queryResult(db, "select ds.*,srd.dep_id from data_source ds join source_relation_dep srd on ds.source_id=srd.source_id where ds.source_id = ?", source_id);
            if (result.isEmpty()) {
                // 该数据源下数据为空
                status = "2";
                jsonObject.addProperty("status", status);
            } else {
                status = "0";
                jsonObject.addProperty("status", status);
                jsonObject.addProperty("result", result.toString());
            }
        }
        return jsonObject;
    }

    /**
     * 删除数据源与部门关系表信息
     *
     * @param source_id 数据源编号
     */
    public String deleteSourceRelationDep(String source_id) {
        // 删除数据源与部门关系表信息
        String status = "0";
        try (DatabaseWrapper db = new DatabaseWrapper()) {
            int num = db.execute("delete from " + SourceRelationDep.TableName + " where source_id=?", new BigDecimal(source_id));
            if (num != 1) {
                // 数据源与部门关系表数据删除失败
                status = "3";
            }
        }
        return status;
    }

    /**
     * 删除数据源信息
     *
     * @param source_id 数据源编号
     */
    public String deleteDataSource(String source_id) {

        // 先查询该datasource下是否还有agent,有不能删除，没有，可以删除
        Result result = Dbo.queryResult("SELECT * FROM agent_info WHERE source_id=? ", new BigDecimal(source_id));
        String status = "0";
        if (!result.isEmpty()) {
            // 此数据源下还有agent，不能删除
            status = "1";
            return status;
        }

        // 删除data_source表信息
        int num = Dbo.execute("delete from " + DataSource.TableName + " where source_id=?", new BigDecimal(source_id));
        if (num != 1) {
            // 删除数据异常
            status = "2";
        }

        // 删除source_relation_dep信息
        if ("0".equals(status)) {
            status = deleteSourceRelationDep(source_id);
        }
        return status;
    }

    /**
     * 保存agent信息
     *
     * @param agentInfo agent实体对象
     * @return
     */
    public String saveAgent(@RequestBean AgentInfo agentInfo) {
        String status = "0";
        boolean flag = monitorPort(agentInfo.getAgent_ip(), Integer.parseInt(agentInfo.getAgent_port()));
        if (flag) {
            // 端口不可使用
            status = "3";
        } else {
            if (StringUtil.isBlank(agentInfo.getAgent_id().toString())) {
                // 新增
                agentInfo.setSource_id(new BigDecimal(1000000000));
                if (agentInfo.add(Dbo.db()) != 1) {
                    status = "1";
                }
            } else {
                // 编辑
                if (agentInfo.update(Dbo.db()) != 1) {
                    status = "2";
                }
            }
        }
        return status;
    }

    /**
     * 监控agent端口是否被占用
     *
     * @param ip
     * @param port
     * @return
     */
    public boolean monitorPort(String ip, int port) {

        HttpClient httpClient = new HttpClient();

        StringBuffer url = new StringBuffer();
        url = url.append("http://").append(ip).append(":").append(port);
        HttpClient.ResponseValue post = httpClient.post(url.toString());

        if (post.getCode() != 200) {
            // 未连通，端口可用
            return false;
        } else {
            // 连通，端口被使用中
            return true;
        }
    }

}
