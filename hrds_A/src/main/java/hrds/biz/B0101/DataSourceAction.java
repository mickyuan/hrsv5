package hrds.biz.B0101;

import fd.ng.web.action.AbstractWebappBaseAction;
import fd.ng.web.annotation.RequestBean;
import fd.ng.web.util.Dbo;
import hrds.entity.SourceRelationDep;
import hrds.entity.DataSource;
import hrds.exception.BusinessException;

/**
 * DataSource class
 *
 * @author mine
 * @date 2019-08-22 16:29:19
 */
public class DataSourceAction extends AbstractWebappBaseAction {

    public  void  addDataSource(@RequestBean SourceRelationDep dataSource, @RequestBean DataSource sourceRelationDep){
        // 新增数据源
        if (dataSource.add(Dbo.db("local"))!=1) {
            throw new BusinessException("添加数据失败！data="+dataSource);
        }

        // 新增数据源与部门关系
        if (sourceRelationDep.add(Dbo.db("local"))!=1) {
            throw new BusinessException("添加数据失败！data="+sourceRelationDep);
        }
    }

}
