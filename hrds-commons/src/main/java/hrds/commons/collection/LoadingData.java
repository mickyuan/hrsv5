package hrds.commons.collection;

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
	public LoadingData(LoadingDataBean ldbean){
		this.ldbbean = ldbean;
	}
	/**
	 * @return
	 */
	public String intoDataLayer(Map<String,Object> map){
		Map<String, String> layer = LoadingDataBean.getLayer();//存储层

		boolean isbatch = LoadingDataBean.isIsbatch();//是否批量存储
		if(isbatch){

		}
		return "";
	}

	public abstract String intoDataImp(List<Map<String,Object>> data);
}
