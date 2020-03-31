package hrds.agent.job.biz.core.service;

import hrds.agent.job.biz.bean.SourceDataConfBean;
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
			return new DbCollectTableHandleParse();
		} else if (IsFlag.Fou.getCode().equals(sourceDataConfBean.getDb_agent())) {
			//否
			return new JdbcCollectTableHandleParse();
		} else {
			throw new AppSystemException("是否为db平面采集标识错误");
		}
	}
}
