package hrds.agent.job.biz.core.dfstage.incrementfileprocess.impl;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.dfstage.incrementfileprocess.TableProcessAbstract;

/**
 * MppTableProcessImpl
 * date: 2020/4/26 17:26
 * author: zxz
 */
public class MppTableProcessImpl extends TableProcessAbstract {

	public MppTableProcessImpl(TableBean tableBean, CollectTableBean collectTableBean, String readFile) {
		super(tableBean, collectTableBean, readFile);
	}

}
