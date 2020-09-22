package hrds.agent.job.biz.core.jdbcdirectstage;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Return;
import fd.ng.db.conf.Dbtype;
import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.*;
import hrds.agent.job.biz.constant.RunStatusConstant;
import hrds.agent.job.biz.constant.StageConstant;
import hrds.agent.job.biz.core.AbstractJobStage;
import hrds.agent.job.biz.core.databaseadditinfo.DatabaseAdditInfoOperateInterface;
import hrds.agent.job.biz.core.databaseadditinfo.impl.OracleAdditInfoOperateImpl;
import hrds.agent.job.biz.core.databaseadditinfo.impl.PostgresqlAdditInfoOperateImpl;
import hrds.agent.job.biz.core.increasement.Increasement;
import hrds.agent.job.biz.core.increasement.JDBCIncreasement;
import hrds.agent.job.biz.core.increasement.impl.IncreasementByMpp;
import hrds.agent.job.biz.core.increasement.impl.IncreasementBySpark;
import hrds.agent.job.biz.utils.JobStatusInfoUtil;
import hrds.commons.codes.*;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.StorageTypeKey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DocClass(desc = "数据库直连采集计算增量阶段", author = "zxz")
public class JdbcDirectCalIncrementStageImpl extends AbstractJobStage {
	//打印日志
	private static final Logger LOGGER = LogManager.getLogger();
	//数据采集表对应的存储的所有信息
	private final CollectTableBean collectTableBean;

	public JdbcDirectCalIncrementStageImpl(CollectTableBean collectTableBean) {
		this.collectTableBean = collectTableBean;
	}

	@Method(desc = "数据库直连采集，计算增量阶段实现，处理完成后，无论成功还是失败，" +
			"将相关状态信息封装到StageStatusInfo对象中返回", logicStep = "")
	@Return(desc = "StageStatusInfo是保存每个阶段状态信息的实体类", range = "不会为null,StageStatusInfo实体类对象")
	@Override
	public StageParamInfo handleStage(StageParamInfo stageParamInfo) {
		long startTime = System.currentTimeMillis();
		LOGGER.info("------------------表" + collectTableBean.getHbase_name()
				+ "数据库直连采集增量计算阶段开始------------------");
		//1、创建卸数阶段状态信息，更新作业ID,阶段名，阶段开始时间
		StageStatusInfo statusInfo = new StageStatusInfo();
		JobStatusInfoUtil.startStageStatusInfo(statusInfo, collectTableBean.getTable_id(),
				StageConstant.CALINCREMENT.getCode());
		try {

			List<DataStoreConfBean> dataStoreConfBeanList = collectTableBean.getDataStoreConfBean();
			TableBean tableBean = stageParamInfo.getTableBean();
			for (DataStoreConfBean dataStoreConf : dataStoreConfBeanList) {
				//根据存储类型上传到目的地
				if (Store_type.DATABASE.getCode().equals(dataStoreConf.getStore_type())
						|| Store_type.HIVE.getCode().equals(dataStoreConf.getStore_type())) {
					JDBCIncreasement increase = null;
					try {
						if (Store_type.HIVE.getCode().equals(dataStoreConf.getStore_type())) {
							//设置hive的默认类型
							dataStoreConf.getData_store_connect_attr().put(StorageTypeKey.database_type,
									DatabaseType.Hive.getCode());
						}
						DatabaseWrapper db = ConnectionTool.getDBWrapper(dataStoreConf.getData_store_connect_attr());
						increase = getJdbcIncreasement(tableBean, collectTableBean.getHbase_name(),
								collectTableBean.getEtlDate(), db, dataStoreConf.getDsl_name());
						execIncreasement(increase);
						//配置附加属性
						configureAdditInfo(collectTableBean.getHbase_name(), dataStoreConf.getAdditInfoFieldMap(),
								dataStoreConf.getData_store_connect_attr().get(StorageTypeKey.database_type), db);
					} catch (Exception e) {
						if (increase != null) {
							//报错删除当次跑批数据
							increase.restore(collectTableBean.getStorage_type());
						}
						throw new AppSystemException("计算增量失败");
					} finally {
						if (increase != null)
							increase.close();
					}
				} else if (Store_type.HBASE.getCode().equals(dataStoreConf.getStore_type())) {
					LOGGER.warn("数据进Hbase计算增量不做任何操作....");
				} else if (Store_type.SOLR.getCode().equals(dataStoreConf.getStore_type())) {
					LOGGER.warn("数据进Solr计算增量不做任何操作....");
				} else if (Store_type.ElasticSearch.getCode().equals(dataStoreConf.getStore_type())) {
					LOGGER.warn("数据进ElasticSearch待实现");
				} else if (Store_type.MONGODB.getCode().equals(dataStoreConf.getStore_type())) {
					LOGGER.warn("数据进MONGODB待实现");
				} else {
					//TODO 上面的待补充。
					throw new AppSystemException("表" + collectTableBean.getHbase_name()
							+ "不支持的存储类型");
				}
			}
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.SUCCEED.getCode(), "执行成功");
			LOGGER.info("------------------表" + collectTableBean.getHbase_name()
					+ "数据库直连采集增量阶段成功------------------执行时间为："
					+ (System.currentTimeMillis() - startTime) / 1000 + "，秒");
		} catch (Exception e) {
			JobStatusInfoUtil.endStageStatusInfo(statusInfo, RunStatusConstant.FAILED.getCode(), e.getMessage());
			LOGGER.error("表" + collectTableBean.getHbase_name()
					+ "数据库直连采集增量阶段失败：", e);
		}
		//结束给stageParamInfo塞值
		JobStatusInfoUtil.endStageParamInfo(stageParamInfo, statusInfo, collectTableBean
				, AgentType.ShuJuKu.getCode());
		return stageParamInfo;
	}

	/**
	 * 给表配置附加信息
	 *
	 * @param hbase_name        表名
	 * @param additInfoFieldMap 附加信息的字段
	 * @param database_type     数据库类型
	 * @param db                数据库的连接
	 */
	private void configureAdditInfo(String hbase_name, Map<String, Map<String, Integer>> additInfoFieldMap,
									String database_type, DatabaseWrapper db) {
		if (additInfoFieldMap != null && !additInfoFieldMap.isEmpty()) {
			DatabaseAdditInfoOperateInterface additInfoOperateInterface;
			if (DatabaseType.Postgresql.getCode().equals(database_type)) {
				//查询
				additInfoOperateInterface = new PostgresqlAdditInfoOperateImpl();
			} else if (DatabaseType.Oracle10g.getCode().equals(database_type)
					|| DatabaseType.Oracle9i.getCode().equals(database_type)) {
				additInfoOperateInterface = new OracleAdditInfoOperateImpl();
			} else {
				LOGGER.warn("暂时还没有实现" + DatabaseType.ofValueByCode(database_type) +
						"数据库配置主键和索引的功能");
				return;
			}
			for (String dsla_storelayer : additInfoFieldMap.keySet()) {
				List<String> columnList = new ArrayList<>(additInfoFieldMap.get(dsla_storelayer).keySet());
				if (StoreLayerAdded.ZhuJian.getCode().equals(dsla_storelayer)) {
					additInfoOperateInterface.addPkConstraint(hbase_name, columnList, db);
				} else if (StoreLayerAdded.SuoYinLie.getCode().equals(dsla_storelayer)) {
					additInfoOperateInterface.addNormalIndex(hbase_name, columnList, db);
				} else {
					throw new AppSystemException("数据库" + DatabaseType.ofValueByCode(database_type) +
							"不支持" + StoreLayerAdded.ofValueByCode(dsla_storelayer) + "操作");
				}
			}
		}
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

	/**
	 * 执行增量逻辑
	 *
	 * @param increasement 执行增量的接口
	 */
	private void execIncreasement(Increasement increasement) throws Exception {
		if (StorageType.ZengLiang.getCode().equals(collectTableBean.getStorage_type())) {
			LOGGER.info("----------------------------增量--------------------------------");
			//计算增量
			increasement.calculateIncrement();
			//合并增量表
			increasement.mergeIncrement();
		} else if (StorageType.ZhuiJia.getCode().equals(collectTableBean.getStorage_type())) {
			LOGGER.info("----------------------------追加--------------------------------");
			//追加
			increasement.append();
		} else if (StorageType.TiHuan.getCode().equals(collectTableBean.getStorage_type())) {
			LOGGER.info("----------------------------替换--------------------------------");
			//替换
			increasement.replace();
		} else {
			throw new AppSystemException("表" + collectTableBean.getHbase_name()
					+ "请选择正确的存储方式！");
		}
	}

	@Override
	public int getStageCode() {
		return StageConstant.CALINCREMENT.getCode();
	}
}
