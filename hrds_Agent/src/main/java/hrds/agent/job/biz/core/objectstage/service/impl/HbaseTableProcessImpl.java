package hrds.agent.job.biz.core.objectstage.service.impl;

import fd.ng.db.jdbc.DatabaseWrapper;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.core.objectstage.service.ObjectProcessAbstract;
import hrds.agent.job.biz.utils.DataTypeTransform;
import hrds.commons.codes.StoreLayerAdded;
import hrds.commons.collection.ConnectionTool;
import hrds.commons.exception.AppSystemException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MppTableProcessImpl
 * date: 2020/4/26 17:26
 * author: zxz
 */
public class HbaseTableProcessImpl extends ObjectProcessAbstract {

	public HbaseTableProcessImpl(TableBean tableBean, ObjectTableBean objectTableBean,
								 DataStoreConfBean dataStoreConfBean) {
		super(tableBean, objectTableBean);
	}


	@Override
	public void parserFileToTable(String readFile) {

	}

	@Override
	public void close() {

	}
}
