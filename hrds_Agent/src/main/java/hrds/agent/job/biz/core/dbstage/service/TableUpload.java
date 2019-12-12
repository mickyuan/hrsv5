package hrds.agent.job.biz.core.dbstage.service;

import hrds.agent.job.biz.bean.CollectTableBean;
import hrds.agent.job.biz.bean.DataStoreConfBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.commons.codes.StorageType;

/**
 * TableUpload
 * date: 2019/12/11 16:56
 * author: zxz
 */
public class TableUpload {
	//上传文件，后续数据库如果想用外部表的形式，需要重新写文件，判断进数方式，来觉得是否加默认字段
	public static void uploadData(DataStoreConfBean dataStoreConfBean, String[] localFiles, TableBean tableBean,
	                              CollectTableBean collectTableBean) {
	}
}
