package hrds.commons.collection;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.collection.bean.LayerBean;
import hrds.commons.collection.bean.LayerTypeBean;
import hrds.commons.collection.bean.LoadingDataBean;

import java.util.List;
import java.util.Map;

/**
 * @program: hrsv5
 * @description: 装载数据
 * @author: xchao
 * @create: 2020-04-13 11:00
 */
public abstract class LoadingData {

	private LoadingDataBean ldbbean = new LoadingDataBean();

	/**
	 * @param ldbean 存储层信息
	 */
	public LoadingData(LoadingDataBean ldbean) {
		this.ldbbean = ldbean;
	}

	/**
	 * @return
	 */
	public String intoDataLayer(String sql, DatabaseWrapper db) {
		LayerTypeBean allTableIsLayer = ProcessingData.getAllTableIsLayer(sql, db);
		LayerTypeBean.ConnTyte connType = allTableIsLayer.getConnType();
		if (LayerTypeBean.ConnTyte.oneJdbc == connType) {
			if (ldbbean.isIsDirTran()) {
				LayerBean layerBean = allTableIsLayer.getLayerBean();
				Map<String, String> layerAttr = layerBean.getLayerAttr();
				try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(layerAttr)) {
					SqlOperator.execute(dbDataConn, "insert into " + ldbbean.getTableName() + sql);
					dbDataConn.commit();
				}
			} else {

			}
		}else if(LayerTypeBean.ConnTyte.oneOther == connType){
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map) throws Exception {

				}
			}.getDataLayer(sql,db);
		}
		return null;
	}

	public abstract String intoDataImp(List<Map<String, Object>> data);
}
