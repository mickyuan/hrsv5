package hrds.agent.job.biz.core.metaparse;

import hrds.agent.job.biz.bean.SourceDataConfBean;
import hrds.agent.job.biz.core.metaparse.impl.DFCollectTableHandleParse;
import hrds.agent.job.biz.core.metaparse.impl.JdbcCollectTableHandleParse;
import hrds.agent.job.biz.core.metaparse.impl.JdbcDirectCollectTableHandleParse;
import hrds.commons.codes.CollectType;
import hrds.commons.codes.IsFlag;
import hrds.commons.exception.AppSystemException;

/**
 * CollectTableHandleFactory
 * date: 2020/3/26 17:32
 * author: zxz
 */
public class CollectTableHandleFactory {

	public static CollectTableHandle getCollectTableHandleInstance(SourceDataConfBean sourceDataConfBean) {

		//判断是否为db平面采集
		if (IsFlag.Shi.getCode().equals(sourceDataConfBean.getDb_agent())) {
			//是
			return new DFCollectTableHandleParse();
		} else if (IsFlag.Fou.getCode().equals(sourceDataConfBean.getDb_agent())) {
			//否
			//判断是数据库直连采集还是数据库抽取
			if (CollectType.ShuJuKuChouShu.getCode().equals(sourceDataConfBean.getCollect_type())) {
				return new JdbcCollectTableHandleParse();
			} else if (CollectType.ShuJuKuCaiJi.getCode().equals(sourceDataConfBean.getCollect_type())) {
				return new JdbcDirectCollectTableHandleParse();
			} else {
				throw new AppSystemException("数据库采集方式不正确");
			}
		} else {
			throw new AppSystemException("是否为db平面采集标识错误");
		}
	}
}
