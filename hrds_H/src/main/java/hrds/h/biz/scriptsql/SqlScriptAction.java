package hrds.h.biz.scriptsql;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.utils.JsonUtil;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.commons.base.BaseAction;
import hrds.commons.entity.Datatable_field_info;
import hrds.h.biz.config.MarketConf;
import hrds.h.biz.config.MarketConfUtils;
import hrds.h.biz.market.MarketData;
import hrds.h.biz.realloader.Utils;
import java.util.List;
import java.util.Map;

@DocClass(desc = "加工SQL脚本生成类", author = "Mr.Lee", createdate = "2020-11-17 16:22")
public class SqlScriptAction extends BaseAction {

	@Method(desc = "根据模型生成脚本文件", logicStep = "" +
		"1: 查询所有字段" +
		"2: 获取执行SQL信息")
	@Param(name = "datatable_id", range = "任务DI", desc = "不可为空")
	public void generatingScript(String datatable_id) {

		//获取本次作业的运行配置
		MarketConf conf = MarketConf.getConf(datatable_id);
		String createTableColumnTypes = Utils.buildCreateTableColumnTypes(conf, true);
		TDScriptGeneration.scriptGeneration(conf, createTableColumnTypes);
	}

}
