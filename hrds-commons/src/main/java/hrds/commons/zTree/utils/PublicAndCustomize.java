package hrds.commons.zTree.utils;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import hrds.commons.utils.PathUtil;
import hrds.commons.zTree.bean.TreeDataInfo;

import java.util.List;
import java.util.Map;

@DocClass(desc = "获取公共层和自定义层的数据信息", author = "BY-HLL", createdate = "2019/12/23 0023 下午 04:51")
public class PublicAndCustomize {

	@Method(desc = "获取公共层和自定义层的数据数据信息",
			logicStep = "1.Public/Customize数据库类型" +
					"2.获取本集群数据库下的表空间" +
					"3.获取WEB表信息")
	@Param(name = "t", desc = "TreeDataInfo", range = "TreeDataInfo的对象实例")
	public static void getPublicAndCustomize(Map<String, Object> map, TreeDataInfo treeDataInfo) {
		// 1.Public/Customize数据库类型
		if (PathUtil.UDL.equals(treeDataInfo.getAgent_layer())) {
			List<Map<String, Object>> databases_type = PublicLayer.getDatabasesType();
			map.put("databases_type", databases_type);
		}
		// 2.获取本集群数据库下的表空间
		if (PathUtil.UDL.equals(treeDataInfo.getParent_id())) {
			if (PublicLayer.HIVE.equals(treeDataInfo.getDatabase_type())
					|| PublicLayer.HBASE.equals(treeDataInfo.getDatabase_type())
					|| PublicLayer.MPP.equals(treeDataInfo.getDatabase_type())
					|| PublicLayer.CARBONDATA.equals(treeDataInfo.getDatabase_type())) {
				List<Map<String, Object>> dataBases = PublicLayer.dataBasesInfo(treeDataInfo.getDatabase_type());
				map.put("dataBases", dataBases);
			}
		}
		// 3.获取WEB表信息
		if (PathUtil.UDL.equals(treeDataInfo.getSpaceTable())) {
			List<Map<String, Object>> tableInfo = PublicLayer.getTableNameInfo(treeDataInfo.getDatabase_type(),
					treeDataInfo.getParent_id());
			map.put("tableInfo", tableInfo);
		}
	}

}
