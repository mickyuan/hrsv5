package hrds.biz.B0101;

import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.entity.SourceRelationDep;
import hrds.entity.DataSource;
import hrds.entity.SysPara;
import hrds.exception.BusinessException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;

/**
 * DataSource class
 *
 * @author mine
 * @date 2019-08-22 16:29:19
 */
public class DataSourceAction extends AbstractWebappBaseAction {
    private static final Logger logger = LogManager.getLogger();

    /**
     * 新增数据源
     *
     * @param dataSource 数据源编号
     * @param dep_id     部门编号
     */
    public void addDataSource(@RequestBean DataSource dataSource, String[] dep_id) {
        // 新增数据源
        if (dataSource.add(Dbo.db()) != 1) {
            throw new BusinessException("添加数据失败！dataSource=" + dataSource);
        }

        // 新增数据源与部门关系
        for (String depId : dep_id) {
            SourceRelationDep srd = new SourceRelationDep();
            srd.setSource_id(dataSource.getSource_id());
            srd.setDep_id(new BigDecimal(depId));
            if (srd.add(Dbo.db()) != 1) {
                throw new BusinessException("添加数据失败！srd=" + srd);
            }
        }

    }

    /**
     * 更新数据源
     *
     * @param source_id       数据源编号
     * @param dep_id          部门编号
     * @param datasource_name 数据源名称
     * @param source_remark   数据源描述
     */
    public void updateDataSource(String source_id, String[] dep_id, String datasource_name, String source_remark) {
        DataSource dataSource = new DataSource();
        dataSource.setSource_id(new BigDecimal(source_id));
        dataSource.setDatasource_name(datasource_name);
        dataSource.setSource_remark(source_remark);
        // 更新数据源信息
        if (dataSource.update(Dbo.db()) != 1) {
            throw new BusinessException(String.format("更新数据失败！datasource_name=%s, source_remark=%s", datasource_name, source_remark));
        }

        // 更新数据源与部门关系表信息
        SourceRelationDep srd = new SourceRelationDep();
        srd.setSource_id(new BigDecimal(source_id));
        for (String id : dep_id) {
            // 先删除关系，再建立新关系
            deleteSourceRelationDep(source_id);
            srd.setDep_id(new BigDecimal(id));
            if (srd.add(Dbo.db()) != 1) {
                throw new BusinessException("添加数据失败！srd=" + srd);
            }
        }
    }

    /**
     * 删除数据源与部门关系表信息
     *
     * @param source_id 数据源编号
     */
    public void deleteSourceRelationDep(String source_id) {
        // 删除data_source表信息
        int num = Dbo.execute("delete from " + SourceRelationDep.TableName + " where source_id=?", source_id);
        if (num != 1) {
            if (num == 0) {
                throw new BusinessException(String.format("没有数据被删除！source_id=%s", source_id));
            } else {
                throw new BusinessException(String.format("删除数据异常！source_id=%s", source_id));
            }
        }
    }

    /**
     * 删除数据源信息
     *
     * @param source_id 数据源编号
     */
    public void deleteDataSource(String source_id) {
        // 删除data_source表信息
        int num = Dbo.execute("delete from " + DataSource.TableName + " where source_id=?", source_id);
        if (num != 1) {
            if (num == 0) {
                throw new BusinessException(String.format("没有数据被删除！source_id=%s", source_id));
            } else {
                throw new BusinessException(String.format("删除数据异常！source_id=%s", source_id));
            }
        }

        // 删除source_relation_dep信息
        deleteSourceRelationDep(source_id);
    }

}
