package hrds.agent.job.biz.core.dfstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.increasement.IncreasementByMpp;
import hrds.agent.job.biz.core.increasement.IncreasementBySpark;
import hrds.agent.job.biz.core.increasement.JDBCIncreasement;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.StorageType;
import hrds.commons.codes.Store_type;
import hrds.commons.codes.UnloadType;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@DocClass(desc = "数据文件采集，计算增量阶段实现", author = "WangZhengcheng")
public class DFCalIncrementStageImpl extends AbstractJobStage {
	private final static Logger LOGGER = LoggerFactory.getLogger(DFCalIncrementStageImpl.class);
	//数据采集表对应的存储的所有信息
	private CollectTableBean collectTableBean;

	public DFCalIncrementStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据文件采集，计算增量阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		LOGGER.info("------------------DB文件采集增量计算阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.CALINCREMENT.getCode());
		try {
			if (UnloadType.ZengLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				LOGGER.info("增量卸数计算增量阶段不用做任何操作");
			} else if (UnloadType.QuanLiangXieShu.getCode().equals(collectTableBean.getUnload_type())) {
				List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
				TableBean tableBean = stageParamInfo.getTableBean();
				for (DataStoreConfBean dataStoreConf : dataStoreConfBeanList) {
					//根据存储类型上传到目的地
					if (Store_type.DATABASE.getCode().equals(dataStoreConf.getStore_type())) {
						try (DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConf.getData_store_connect_attr());
						     JDBCIncreasement increase = getJdbcIncreasement(tableBean, collectTableBean.getHbase_name(),
								     collectTableBean.getEtlDate(), db, dataStoreConf.getDsl_name())) {
							if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
								//计算增量
								increase.calculateIncrement();
								//合并增量表
								increase.mergeIncrement();
							} else if (StorageType.ZhuiJia.getCode().equals(collectTableBean.getStorage_type())) {
								//追加
								increase.append();
							} else if (StorageType.TiHuan.getCode().equals(collectTableBean.getStorage_type())) {
								//替换
								increase.replace();
							} else {
								throw new AppSystemException("请选择正确的存储方式！");
							}
						}
					} else if (Store_type.HBASE.getCode().equals(dataStoreConf.getStore_type())) {

					} else if (Store_type.SOLR.getCode().equals(dataStoreConf.getStore_type())) {

					} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConf.getStore_type())) {

					} else if (Store_type.MONGODB.getCode().equals(dataStoreConf.getStore_type())) {

					} else {
						//TODO 上面的待补充。
						throw new AppSystemException("不支持的存储类型");
					}
				}
			} else {
				throw new AppSystemException("DB文件采集指定的数据抽取卸数方式类型不正确");
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------DB文件采集增量阶段成功------------------");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("DB文件采集增量阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, CollectType.DBWenJianCaiJi.getCode());
		return stageParamInfo;
	}

	/**
	 * 数据库类型的做增量目前分为两种，一种是传统数据库，另一种是hive库（hive库不支持update）
	 * 根据数据库类型获取执行数据增量、追加、替换的程序
	 *
	 * @param tableBean  表结构
	 * @param hbase_name 表名
	 * @param etlDate    跑批日期
	 * @param db         数据库连接
	 * @param dsl_name   数据目的地名称
	 * @return 增量算法接口
	 */
	private JDBCIncreasement getJdbcIncreasement(TableBean tableBean, String hbase_name, String etlDate,
	                                             DatabaseWrapper db, String dsl_name) {
		JDBCIncreasement increasement;
		//数据库类型的做增量目前分为两种，一种是传统数据库，另一种是hive库（hive库不支持update）
		if (Dbtype.HIVE.equals(db.getDbtype())) {
			increasement = new IncreasementBySpark(tableBean, hbase_name, etlDate, db, dsl_name);
		} else {
			increasement = new IncreasementByMpp(tableBean, hbase_name, etlDate, db, dsl_name);
		}
		return increasement;
	}

	@Override
	public int getStageCode() {
		return StageConstant.CALINCREMENT.getCode();
	}
}
