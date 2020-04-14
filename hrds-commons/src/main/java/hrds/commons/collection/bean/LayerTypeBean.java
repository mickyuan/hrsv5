package hrds.commons.collection.bean;

import java.util.List;

/**
 * @program: hrsv5
 * @description: 最终的使用
 * @author: xchao
 * @create: 2020-04-14 09:29
 */
public class LayerTypeBean {
	public enum ConnPyte{
		oneJdbc,moreJdbc,oneOther,moreOther
	}
	private ConnPyte connType;
	private LayerBean layerBean;
	private List<LayerBean> layerBeanList;

	public ConnPyte getConnType() {
		return connType;
	}

	public void setConnType(ConnPyte connType) {
		this.connType = connType;
	}

	public List<LayerBean> getLayerBeanList() {
		return layerBeanList;
	}

	public void setLayerBeanList(List<LayerBean> layerBeanList) {
		this.layerBeanList = layerBeanList;
	}
	public LayerBean getLayerBean() {
		return layerBean;
	}

	public void setLayerBean(LayerBean layerBean) {
		this.layerBean = layerBean;
	}
}
