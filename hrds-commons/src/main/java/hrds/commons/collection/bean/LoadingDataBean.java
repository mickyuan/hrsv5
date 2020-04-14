package hrds.commons.collection.bean;

import java.util.Map;

/**
 * @program: hrsv5
 * @description: 数据加载需要的数据实体
 * @author: xchao
 * @create: 2020-04-13 11:31
 */
public class LoadingDataBean {
	private static Map<String, String> layer;
	private static boolean isbatch = true;
	private static int batchNum = 50000;

	public static Map<String, String> getLayer() {
		return layer;
	}

	public static void setLayer(Map<String, String> layer) {
		LoadingDataBean.layer = layer;
	}

	public static boolean isIsbatch() {
		return isbatch;
	}

	public static void setIsbatch(boolean isbatch) {
		LoadingDataBean.isbatch = isbatch;
	}

	public static int getBatchNum() {
		return batchNum;
	}

	public static void setBatchNum(int batchNum) {
		LoadingDataBean.batchNum = batchNum;
	}
}
