package hrds.commons.collection;

import fd.ng.db.jdbc.DatabaseWrapper;
import fd.ng.db.jdbc.SqlOperator;
import hrds.commons.codes.Store_type;
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
	public Long intoDataLayer(String sql, DatabaseWrapper db,LayerBean intolayerBean) {
		LayerTypeBean allTableIsLayer = ProcessingData.getAllTableIsLayer(sql, db);
		LayerTypeBean.ConnTyte connType = allTableIsLayer.getConnType();
		if (LayerTypeBean.ConnTyte.oneJdbc == connType) {
			LayerBean layerBean = allTableIsLayer.getLayerBean();
			Map<String, String> layerAttr = layerBean.getLayerAttr();
			try (DatabaseWrapper dbDataConn = ConnectionTool.getDBWrapper(layerAttr)) {
				SqlOperator.execute(dbDataConn, "insert into " + ldbbean.getTableName() + sql);
				dbDataConn.commit();
				return layerBean.getDsl_id();
			}
		} else if (LayerTypeBean.ConnTyte.oneOther == connType) {
			LayerBean layerBean = allTableIsLayer.getLayerBean();
			String store_type = intolayerBean.getStore_type();//入库是谁
			new ProcessingData() {
				@Override
				public void dealLine(Map<String, Object> map){
					if(Store_type.DATABASE == Store_type.ofEnumByCode(store_type) ){

					}
				}
			}.getDataLayer(sql, db);


			return layerBean.getDsl_id();
		}
		return null;
	}

	public abstract String intoDataImp(List<Map<String, Object>> data);
}
