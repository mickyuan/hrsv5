package hrds.h.biz.scriptsql;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.Validator;
import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import fd.ng.db.resultset.Result;
import fd.ng.web.util.Dbo;
import hrds.commons.base.BaseAction;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.ProcessType;
import hrds.commons.codes.StorageType;
import hrds.commons.collection.ProcessingData;
import hrds.commons.entity.*;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.Constant;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.realloader.Utils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

@DocClass(desc = "加工SQL脚本生成类", author = "Mr.Lee", createdate = "2020-11-17 16:22")
public class SqlScriptAction extends BaseAction {

	@Method(desc = "根据模型生成脚本文件", logicStep = "" +
			"1: 查询所有字段" +
			"2: 获取执行SQL信息")
	@Param(name = "datatable_id", range = "任务DI", desc = "不可为空")
	public void generatingScript(String datatable_id) {
		List<Dm_datatable> dm_datatables = Dbo.queryList(Dm_datatable.class, "select * from "
				+ Dm_datatable.TableName + " where remark = ?", datatable_id);
		//获取本次作业的运行配置
		MarketConf conf = getconf(datatable_id);
		String tableName = conf.getTableName();
		String createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf, true);
		TDScriptGeneration tdScriptGeneration = new TDScriptGeneration();
		List<String> allsqls = new ArrayList<>();
		List<String> strings = tdScriptGeneration.sqlGeneration(conf, createTableColumnTypes);
		for (Dm_datatable every_dm_datatable : dm_datatables) {
			Long datatable_id1 = every_dm_datatable.getDatatable_id();
//			conf = new MarketConf(String.valueOf(datatable_id1));
			conf = getconf(String.valueOf(datatable_id1));
			createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf, true);
			strings = tdScriptGeneration.sqlGeneration(conf, createTableColumnTypes);
			allsqls.addAll(strings);
		}
		allsqls.addAll(strings);
		tdScriptGeneration.scriptGeneration(allsqls, tableName);
	}

	private MarketConf getconf(String datatable_id) {
		MarketConf conf = new MarketConf(datatable_id);
		String datatableId = conf.getDatatableId();
		Dm_datatable dm_datatable = new Dm_datatable();
		dm_datatable.setDatatable_id(datatableId);
		Optional<Dm_relevant_info> dm_relevant_info = Dbo.queryOneObject(Dm_relevant_info.class, "select * from " + Dm_relevant_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_relevant_info.isPresent()) {
			Dm_relevant_info dm_relevant_info1 = dm_relevant_info.get();
			String pre_work = dm_relevant_info1.getPre_work();
			String post_work = dm_relevant_info1.getPost_work();
			conf.setPreSql(pre_work);
			conf.setFinalSql(post_work);
		}
		Optional<Dm_datatable> dm_datatable1 = Dbo.queryOneObject(Dm_datatable.class, "select * from " + Dm_datatable.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_datatable1.isPresent()) {
			Dm_datatable dm_datatable2 = dm_datatable1.get();
			String datatable_en_name = dm_datatable2.getDatatable_en_name();
			conf.setTableName(datatable_en_name);
			conf.setDmDatatable(dm_datatable2);
		}
		Optional<Dm_operation_info> dm_operation_info = Dbo.queryOneObject(Dm_operation_info.class, "select * from " + Dm_operation_info.TableName + " where datatable_id = ?", dm_datatable.getDatatable_id());
		if (dm_operation_info.isPresent()) {
			Dm_operation_info dm_operation_info1 = dm_operation_info.get();
			String execute_sql = dm_operation_info1.getExecute_sql();
			conf.setBeforeReplaceSql(execute_sql);
		}
		Map<String, List<String>> addAttrColMap = new HashMap<>();
		String sql = "select dfi.field_en_name,dsla.dsla_storelayer from " + Datatable_field_info.TableName
				+ " dfi join " + Dcol_relation_store.TableName + " dcs on dfi.datatable_field_id = " +
				"dcs.col_id join " + Data_store_layer_added.TableName + " dsla on dcs.dslad_id = " +
				"dsla.dslad_id where dfi.datatable_id = ? AND dfi.end_date in (?,?)";
		List<Map<String, Object>> maps = Dbo.queryList(sql, dm_datatable.getDatatable_id(), Constant.MAXDATE, Constant.INITDATE);
		//遍历
		for (int i = 0; i < maps.size(); i++) {
			Map<String, Object> stringObjectMap = maps.get(i);
			String dsla_storelayer = stringObjectMap.get("dsla_storelayer").toString();
			List<String> list = addAttrColMap.get(dsla_storelayer);
			if (list == null) {
				list = new ArrayList<>();
			}
			//和上面保持一致，字段转为小写
			list.add(stringObjectMap.get("field_en_name").toString().toLowerCase());
			addAttrColMap.put(dsla_storelayer, list);
		}
		conf.setAddAttrColMap(addAttrColMap);

		List<Datatable_field_info> datatableFields = Dbo.queryList(Datatable_field_info.class,
				"select * from " + Datatable_field_info.TableName
						+ " where datatable_id = ? and (end_date = ? or end_date = ?)"
				, dm_datatable.getDatatable_id(), Constant.INITDATE, Constant.MAXDATE);
		conf.setDatatableFields(datatableFields);
		return conf;


	}
}
