package hrds.agent.job.biz.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import org.apache.parquet.example.data.Group;

import java.math.BigDecimal;
import java.util.List;


public class ColUtil {

	public void addData2Group(Group group, String columnType, String columname, String data) {
		//TODO 待讨论的，这里需要修改为非手动配置文件修改的，需要加字段映射关系表
//		columnType = DataTypeTransformHive.tansform(columnType.toUpperCase());
		data = data.trim();
		if (columnType.contains("BOOLEAN")) {
			boolean dataResult = Boolean.valueOf(data);
			group.add(columname, dataResult);
		} else if (columnType.contains("INT")) {
			int dataResult = StringUtils.isEmpty(data) ? 0 : Integer.valueOf(data);
			group.add(columname, dataResult);
		} else if (columnType.contains("FLOAT")) {
			float dataResult = StringUtils.isEmpty(data) ? 0 : Float.valueOf(data);
			group.add(columname, dataResult);
		} else if (columnType.contains("DOUBLE") || columnType.contains("DECIMAL")) {
			double dataResult = StringUtils.isEmpty(data) ? 0 : Double.valueOf(data);
			group.add(columname, dataResult);
		}
		//		else if(columnType.indexOf("DECIMAL")){
		//			data  = StringUtil.isEmpty(data) ? "0" : data;
		//			group.add(columname, data);
		//		}
		else {
			//char与varchar都为string
			data = StringUtils.isEmpty(data) ? "" : data;
			group.add(columname, data);
		}
	}

	public void addData2Inspector(List<Object> lineData, String columnType, String data) {
//		columnType = DataTypeTransformHive.tansform(columnType.toUpperCase());
		data = data.trim();
		if (columnType.contains("BOOLEAN")) {
			boolean dataResult = Boolean.valueOf(data);
			lineData.add(dataResult);
		} else if (columnType.contains("INT")) {
			int dataResult = StringUtils.isEmpty(data) ? 0 : Integer.valueOf(data);
			lineData.add(dataResult);
		} else if (columnType.contains("FLOAT")) {
			float dataResult = StringUtils.isEmpty(data) ? 0 : Float.valueOf(data);
			lineData.add(dataResult);
		} else if (columnType.contains("DOUBLE")) {
			double dataResult = StringUtils.isEmpty(data) ? 0 : Double.valueOf(data);
			lineData.add(dataResult);
		} else if (columnType.contains("DECIMAL")) {
			BigDecimal dataResult = StringUtils.isEmpty(data) ? new BigDecimal("0") : new BigDecimal(data);
			HiveDecimal create = HiveDecimal.create(dataResult);
			lineData.add(create);
		} else {
			//char与varchar都为string
			data = StringUtils.isEmpty(data) ? "" : data;
			lineData.add(data);
		}
	}

}
