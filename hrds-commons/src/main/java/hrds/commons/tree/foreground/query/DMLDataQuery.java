package hrds.commons.tree.foreground.query;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.web.util.Dbo;
import hrds.commons.codes.UserType;
import hrds.commons.entity.Dm_datatable;
import hrds.commons.entity.Dm_info;
import hrds.commons.entity.Sys_user;
import hrds.commons.utils.User;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

@DocClass(desc = "集市层(DML)层数据信息查询类", author = "BY-HLL", createdate = "2020/1/7 0007 上午 11:17")
public class DMLDataQuery {

    private static final SqlOperator.Assembler asmSql = SqlOperator.Assembler.newInstance();
//
//	@Method(desc = "获取集市信息",
//			logicStep = "1.获取集市信息")
//	@Return(desc = "集市信息列表", range = "无限制")
//	public static List<Map<String, Object>> getDMLDataInfos() {
//		//1.获取集市信息
//		return getDMLDataInfos(null);
//	}

    @Method(desc = "获取集市信息",
            logicStep = "1.如果集市名称不为空,模糊查询获取集市信息")
    @Param(name = "marketName", desc = "集市名称", range = "marketName")
    @Return(desc = "集市信息列表", range = "无限制")
    public static List<Map<String, Object>> getDMLDataInfos(String marketName, User user) {
        asmSql.clean();
        asmSql.addSql("SELECT distinct t1.* from " + Dm_info.TableName + " t1 left join "+Sys_user.TableName+" t2 on t1.create_id = t2.user_id ");
        
        //1.获取数据源下分类信息,如果是系统管理员,则不过滤部门
        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql(" where t2.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        //1.如果集市名称不为空,模糊查询获取集市信息
        if (!StringUtil.isBlank(marketName)) {
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql("  lower(mart_name) like ?").addParam('%' + marketName.toLowerCase() + '%');
        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

    public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, User user, String table_name) {
        asmSql.clean();
        asmSql.addSql("SELECT distinct t1.* from " + Dm_datatable.TableName + " t1 left join " + Dm_info.TableName +
                " t2 on t1.data_mart_id = t2.data_mart_id left join " + Sys_user.TableName + " t3 on t2.create_id = t3.user_id");

        if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
            asmSql.addSql(" where t3.dep_id = ?");
            asmSql.addParam(user.getDepId());
        }
        if (!StringUtils.isEmpty(data_mart_id)) {
            Dm_datatable dm_datatable = new Dm_datatable();
            dm_datatable.setData_mart_id(data_mart_id);
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" t1.data_mart_id = ?");
            asmSql.addParam(dm_datatable.getData_mart_id());
        } else if (!StringUtil.isEmpty(table_name)) {
            if (!UserType.XiTongGuanLiYuan.getCode().equals(user.getUserType())) {
                asmSql.addSql(" and ");
            } else {
                asmSql.addSql(" where ");
            }
            asmSql.addSql(" lower(t1.datatable_en_name) like ?").addParam('%' + table_name.toLowerCase() + '%');

        }
        return Dbo.queryList(asmSql.sql(), asmSql.params());
    }

//	@Method(desc = "根据集市id获取集市下数据表信息",
//			logicStep = "1.根据集市id获取集市下数据表信息")
//	@Param(name = "data_mart_id", desc = "集市id", range = "集市id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Return(desc = "集市信息", range = "无限制")
//	public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, String pageFrom) {
//		//1.根据集市id获取集市下数据表信息
//		return getDMLTableInfos(data_mart_id, pageFrom, null);
//	}
//
//	@Method(desc = "根据集市id获取集市下数据表信息",
//			logicStep = "1.根据集市id获取集市下数据表信息")
//	@Param(name = "data_mart_id", desc = "集市id", range = "集市id,唯一")
//	@Param(name = "pageFrom", desc = "页面来源", range = "TreePageSource.webType")
//	@Param(name = "dataTableName", desc = "集市表名", range = "0:是,1:否")
//	@Return(desc = "集市下数据表信息", range = "无限制")
//	public static List<Map<String, Object>> getDMLTableInfos(String data_mart_id, String pageFrom,
//	                                                         String dataTableName) {
//		//1.根据集市id获取集市下数据表信息
//		Datatable_info datatableInfo = new Datatable_info();
//		asmSql.clean();
//		asmSql.addSql("SELECT * from datatable_info where is_current_cluster = ?").addParam(IsFlag.Shi.getCode());
//		if (StringUtil.isNotBlank(data_mart_id)) {
//			datatableInfo.setData_mart_id(data_mart_id);
//			asmSql.addSql(" AND data_mart_id = ?").addParam(datatableInfo.getData_mart_id());
//		}
//		datatableInfo.setDatatable_due_date(DateUtil.getSysDate());
//		asmSql.addSql(" AND datatable_due_date >=").addParam(datatableInfo.getDatatable_due_date());
//		DataSourceType dataSourceType = DataSourceType.ofEnumByCode(pageFrom);
//		if (DataSourceType.DML == dataSourceType && !TreePageSource.WEBSQL.equalsIgnoreCase(pageFrom)
//				&& !TreePageSource.REPORT.equalsIgnoreCase(pageFrom)) {
//			//TODO JobExecuteState
//			asmSql.addSql(" and ( ? in (hy_success,elk_success,kv_success,solr_success,solrbase_success," +
//					"carbondata_success))").addParam("104");
//			asmSql.addSql(" and datatype = ?").addParam(IsFlag.Shi.getCode());
//		}
//		if (TreePageSource.WEBSQL.equalsIgnoreCase(pageFrom) || TreePageSource.REPORT.equalsIgnoreCase(pageFrom)
//				|| TreePageSource.INTERFACE.equalsIgnoreCase(pageFrom)) {
//			//TODO JobExecuteState
//			asmSql.addSql(" and ( ? in (hy_success,elk_success,kv_success,solr_success,solrbase_success," +
//					"carbondata_success))").addParam("104");
//		}
//		if (StringUtil.isNotBlank(dataTableName)) {
//			datatableInfo.setDatatable_cn_name('%' + dataTableName + '%');
//			asmSql.addSql(" AND lower(datatable_cn_name) like lower(?)").addParam(datatableInfo.getDatatable_cn_name());
//			datatableInfo.setDatatable_en_name(dataTableName);
//			asmSql.addSql(" OR lower(datatable_en_name) like lower(?)").addParam(datatableInfo.getDatatable_en_name());
//		}
//		return Dbo.queryList(asmSql.sql(), asmSql.params());
//	}
}
