package hrds.agent.job.biz.core.metaparse.impl;

import fd.ng.core.annotation.DocClass;
import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import hrds.agent.job.biz.bean.ObjectCollectParamBean;
import hrds.agent.job.biz.bean.ObjectTableBean;
import hrds.agent.job.biz.bean.TableBean;
import hrds.agent.job.biz.utils.TypeTransLength;
import hrds.commons.codes.IsFlag;
import hrds.commons.codes.UpdateType;
import hrds.commons.entity.Object_collect_struct;
import hrds.commons.utils.Constant;

import java.util.List;

@DocClass(desc = "DB文件采集根据页面所选的表和字段对jdbc所返回的meta信息进行解析", author = "zxz", createdate = "2019/12/4 11:17")
public class ObjectCollectTableHandleParse {

	@Method(desc = "根据数据源信息和采集表信息得到卸数元信息", logicStep = "" +
			"1、根据数据源信息和采集表信息抽取SQL" +
			"2、根据数据源信息和抽取SQL，执行SQL，获取")
	@Param(name = "sourceDataConfBean", desc = "数据库采集,DB文件采集数据源配置信息", range = "不为空")
	@Param(name = "collectTableBean", desc = "数据库采集表配置信息", range = "不为空")
	@Return(desc = "卸数阶段元信息", range = "不为空")
	public TableBean generateTableInfo(ObjectCollectParamBean objectCollectParamBean,
									   ObjectTableBean objectTableBean) {
		TableBean tableBean = new TableBean();
		//获取页面选择的需要采集的文件格式
//		tableBean.setFile_format(objectTableBean.getCollect_data_type());
//		tableBean.setIs_header(sourceData_extraction_def.getIs_header());
//		tableBean.setRow_separator(sourceData_extraction_def.getRow_separator());
//		tableBean.setColumn_separator(sourceData_extraction_def.getDatabase_separatorr());
//		tableBean.setRoot_path(sourceData_extraction_def.getPlane_url());
//		tableBean.setFile_code(sourceData_extraction_def.getDatabase_code());
//		tableBean.setIs_archived(sourceData_extraction_def.getIs_archived());
		StringBuilder allColumns = new StringBuilder();//要采集的列名
		StringBuilder allType = new StringBuilder();//要采集的列类型
		StringBuilder columnMetaInfo = new StringBuilder();//生成的元信息列名
		StringBuilder colTypeMetaInfo = new StringBuilder();//生成的元信息列类型
		StringBuilder colLengthInfo = new StringBuilder();//生成的元信息列长度
		//这里的主键其实是页面选择的拉链字段，是为了做跟新删除或者拉链更新删除使用的
		StringBuilder primaryKeyInfo = new StringBuilder();//是否为主键
//		HashMap<String, Boolean> isCollectMap = new HashMap<>();//db文件采集，字段是否采集的映射,对新增列不做映射，默认采集
		//3.获取数据字典下所有的表信息,找到当前线程对应需要采集表的数据字典，获取表结构(注数据字典中的是有序的)
//		List<String> cols = ColumnMeta.getColumnListByDictionary(collectTableBean.getTable_name(),
//				sourceDataConfBean.getPlane_url());
		List<Object_collect_struct> object_collect_structList = objectTableBean.getObject_collect_structList();
		//遍历db文件数据字典里面列信息的集合
		// XXX 注：DB文件采集，页面只允许查看列，不允许选择列，默认是全选，所有数据字典里面定义的列都采集。
		//  页面选择了转存的情况下，不读取DB数据文件的HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列
		//  HYREN_S_DATE、HYREN_E_DATE、HYREN_MD5_VAL三列，根据页面选择的是否拉链存储的存储方式自行添加
		for (Object_collect_struct object_collect_struct : object_collect_structList) {
			if (IsFlag.Shi.getCode().equals(object_collect_struct.getIs_operate())) {
				//是操作字段
				tableBean.setOperate_column(object_collect_struct.getColumn_name());
			} else {
				String colName = object_collect_struct.getColumn_name();
				String colType = object_collect_struct.getColumn_type();
				allColumns.append(colName).append(Constant.METAINFOSPLIT);
				allType.append(colType).append(Constant.METAINFOSPLIT);
				//不转存，取所有字段
				columnMetaInfo.append(colName).append(Constant.METAINFOSPLIT);
				colTypeMetaInfo.append(colType).append(Constant.METAINFOSPLIT);
				colLengthInfo.append(TypeTransLength.getLength(colType)).append(Constant.METAINFOSPLIT);
				primaryKeyInfo.append(object_collect_struct.getIs_zipper_field()).append(Constant.METAINFOSPLIT);
			}
		}
		if (colLengthInfo.length() > 0) {
			colLengthInfo.delete(colLengthInfo.length() - 1, colLengthInfo.length());
			allType.delete(allType.length() - 1, allType.length());
			allColumns.delete(allColumns.length() - 1, allColumns.length());
			colTypeMetaInfo.delete(colTypeMetaInfo.length() - 1, colTypeMetaInfo.length());
			columnMetaInfo.delete(columnMetaInfo.length() - 1, columnMetaInfo.length());
			primaryKeyInfo.delete(primaryKeyInfo.length() - 1, primaryKeyInfo.length());
		}
		//拉链跟新，增加结束日期和开始日期两个字段
		if (UpdateType.IncrementUpdate.getCode().equals(objectTableBean.getUpdatetype())) {
			columnMetaInfo.append(Constant.METAINFOSPLIT).append(Constant.SDATENAME).append(Constant.METAINFOSPLIT).append(Constant.EDATENAME);
			colTypeMetaInfo.append(Constant.METAINFOSPLIT).append("char(8)").append(Constant.METAINFOSPLIT).append("char(32)");
			colLengthInfo.append(Constant.METAINFOSPLIT).append("8").append(Constant.METAINFOSPLIT).append("32");
		}
		// 页面定义的清洗格式进行卸数
		tableBean.setAllColumns(allColumns.toString());
		tableBean.setAllType(allType.toString());
		tableBean.setColLengthInfo(colLengthInfo.toString());
		tableBean.setColTypeMetaInfo(colTypeMetaInfo.toString());
		tableBean.setColumnMetaInfo(columnMetaInfo.toString());
		tableBean.setPrimaryKeyInfo(primaryKeyInfo.toString());
		//返回表信息
		return tableBean;
	}

}
