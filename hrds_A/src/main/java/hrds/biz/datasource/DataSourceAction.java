package hrds.biz.datasource;

import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.Action;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.annotation.RequestParam;
import fd.ng.web.util.Dbo;
import hrds.entity.SourceRelationDep;
import hrds.entity.DataSource;
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
     */
    public void add(@RequestBean DataSource dataSource,String depIds) {
        // 新增数据源
        if (dataSource.add(Dbo.db()) != 1) {
            throw new BusinessException("添加数据失败！dataSource=" + dataSource);
        }

        SourceRelationDep srd = new SourceRelationDep();
        srd.setSource_id(dataSource.getSource_id());
        String[] split = depIds.split(",");
        //新增数据源与部门关系
        for (String dep_id : split) {
            srd.setDep_id(new BigDecimal(dep_id));
            if (srd.add(Dbo.db()) != 1) {
                throw new BusinessException("添加数据失败！srd=" + srd);
            }
        }

    }

    /**
     * 更新数据源
     *
     * @param source_id       数据源编号
     * @param depIds         部门编号
     *
     */
    public void updateDataSource(String source_id, String depIds,String datasource_name,String source_remark) {
        DataSource dataSource = new DataSource();
        dataSource.setDatasource_name(datasource_name);
        dataSource.setSource_remark(source_remark);
        // 更新数据源信息
        if (dataSource.update(Dbo.db()) != 1) {
            throw new BusinessException(String.format("更新数据失败！datasource_name=%s, source_remark=%s", datasource_name, source_remark));
        }

        // 更新数据源与部门关系表信息
        // 先删除关系，再建立新关系
        deleteSourceRelationDep(source_id);
        SourceRelationDep srd = new SourceRelationDep();
        srd.setSource_id(new BigDecimal(source_id));
        String[] split = depIds.split(",");
        for (String dep_id : split) {
            srd.setDep_id(new BigDecimal(dep_id));
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
