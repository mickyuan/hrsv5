package hrds.agent.job.biz.core.service;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.bean.TableBean;

@DocClass(desc = "获取采集表结构信息", author = "zxz", createdate = "2020/3/26 17:16")
public interface CollectTableHandle {
	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	TableBean generateTableInfo(SourceDataConfBean sourceDataConfBean,
	                            CollectTableBean collectTableBean);
}
