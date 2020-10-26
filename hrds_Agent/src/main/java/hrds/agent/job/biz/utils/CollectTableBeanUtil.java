package hrds.agent.job.biz.utils;

import fd.ng.core.utils.StringUtil;
import hrds.commons.codes.DataExtractType;
import hrds.commons.entity.Data_extraction_def;
import hrds.commons.exception.AppSystemException;
import hrds.commons.utils.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class CollectTableBeanUtil {
	public static List<Data_extraction_def> getTransSeparatorExtractionList(List<Data_extraction_def> data_extraction_def_list) {
		List<Data_extraction_def> data_extraction_defs = new ArrayList<>();
		for (Data_extraction_def data_extraction_def : data_extraction_def_list) {
			//复制data_extraction_def的值
			Data_extraction_def def = new Data_extraction_def();
			BeanUtils.copyProperties(data_extraction_def, def);
			if (!StringUtil.isEmpty(data_extraction_def.getRow_separator())) {
				String row_sp = StringUtil.unicode2String(data_extraction_def.getRow_separator());
				//对于转义的换行符做处理
				switch (row_sp) {
					case "\\r\\n":
						def.setRow_separator("\r\n");
						break;
					case "\\r":
						def.setRow_separator("\r");
						break;
					case "\\n":
						def.setRow_separator("\n");
						break;
				}
			}
			if (!StringUtil.isEmpty(data_extraction_def.getDatabase_separatorr())) {
				def.setDatabase_separatorr(StringUtil.
						unicode2String(data_extraction_def.getDatabase_separatorr()));
			}
			data_extraction_defs.add(def);
		}
		return data_extraction_defs;
	}

	public static Data_extraction_def getSourceData_extraction_def(List<Data_extraction_def> data_extraction_def_list) {
		List<Data_extraction_def> def_list = getTransSeparatorExtractionList(data_extraction_def_list);
		//遍历，获取需要读取的db文件的文件格式
		for (Data_extraction_def data_extraction_def : def_list) {
			if (DataExtractType.YuanShuJuGeShi.getCode().equals(data_extraction_def.getData_extract_type())) {
				//此对象需要读取的db文件的文件格式
				return data_extraction_def;
			}
		}
		throw new AppSystemException("找不到原文件格式定义的对象");
	}

//	public static Data_extraction_def getTargetData_extraction_def(List<Data_extraction_def> data_extraction_def_list) {
//		List<Data_extraction_def> def_list = getTransSeparatorExtractionList(data_extraction_def_list);
//		//遍历，获取转存文件格式
//		for (Data_extraction_def data_extraction_def : def_list) {
//			if (DataExtractType.ShuJuJiaZaiGeShi.getCode().equals(data_extraction_def.getData_extract_type())) {
//				//此对象为转存之后的文件格式对象。
//				return data_extraction_def;
//			}
//		}
//		throw new AppSystemException("找不到目标文件格式定义的对象");
//	}
}
