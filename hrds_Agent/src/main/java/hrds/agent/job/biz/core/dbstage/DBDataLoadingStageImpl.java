package hrds.agent.job.biz.core.dbstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.StageStatusInfo;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.Store_type;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DocClass(desc = "数据库直连采集数据加载阶段", author = "WangZhengcheng")
public class DBDataLoadingStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DBUploadStageImpl.class);
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;
	//数据库采集表对应的meta信息
	private TableBean tableBean;

	public DBDataLoadingStageImpl(TableBean tableBean, CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
		this.tableBean = tableBean;
	}

	@Method(desc = "数据库直连采集数据加载阶段处理逻辑，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null，StageStatusInfo实体类对象")
	@Override
	public StageStatusInfo handleStage() {
		LOGGER.info("------------------数据库直连采集数据加载阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.CALINCREMENT.getCode());
		try {
			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			for (DataStoreConfBean dataStoreConfBean : dataStoreConfBeanList) {
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConfBean.getStore_type())) {
					//数据库类型在upload时已经装载数据了，直接跳过
					continue;
				} else if (Store_type.HBASE.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.SOLR.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else if (Store_type.MONGODB.getCode().equals(dataStoreConfBean.getStore_type())) {

				} else {
					//TODO 上面的待补充。
					throw new AppSystemException("不支持的存储类型");
				}
				LOGGER.info("数据成功进入库" + dataStoreConfBean.getDsl_name() + "下的表"
						+ collectTableBean.getHbase_name());
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------数据库直连采集数据加载阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("数据库直连采集数据加载阶段失败：", e.getMessage());
		}
		return statusInfo;
	}
}
