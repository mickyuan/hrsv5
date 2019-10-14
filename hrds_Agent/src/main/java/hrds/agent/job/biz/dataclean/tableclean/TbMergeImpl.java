package hrds.agent.job.biz.dataclean.tableclean;

import fd.ng.core.annotation.Method;
import fd.ng.core.annotation.Param;
import fd.ng.core.annotation.Return;
import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.ColumnTool;
import hrds.agent.job.biz.utils.ParquetUtil;
import org.apache.parquet.example.data.Group;

import java.util.List;
import java.util.Map;

/**
 * ClassName: TbMergeImpl <br/>
 * Function: 数据库直连采集表清洗列合并实现类 <br/>
 * Reason: 继承AbstractTableClean抽象类，只针对一个列合并方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TbMergeImpl extends AbstractTableClean {

	@Method(desc = "表列合并实现", logicStep = "")
	@Param(name = "mergeRule", desc = "存放有列合并规则的map集合", range = "不为空，key为合并后的列名`合并后的列类型，value为原列的列名")
	@Param(name = "columnsValue", desc = "待合并的若干列的列值", range = "不为空")
	@Param(name = "columnsName", desc = "待合并的若干列的列名", range = "不为空")
	@Param(name = "group", desc = "用于写Parquet的一行数据", range = "不为空")
	@Param(name = "lineData", desc = "用于写ORC", range = "不为空")
	@Param(name = "fileType", desc = "卸数落地数据文件的格式", range = "不为空，FileFormatConstant代码项的code")
	@Return(desc = "清洗后的字段值", range = "不会为null")
	//TODO ORC,SEQUENCE未实现
	@Override
	public String merge(Map<String, String> mergeRule, String[] columnsValue, String[] columnsName,
	                    Group group, List<Object> lineData, String fileType) {
		StringBuilder afterMergeColValue = new StringBuilder(4096);
		for (String colNameAndType : mergeRule.keySet()) {
			int[] allIndex = ColumnTool.findColIndex(columnsName, mergeRule.get(colNameAndType));
			for(int index : allIndex){
				afterMergeColValue.append(columnsValue[index]);
			}
			if (group != null) {
				List<String> colNameAndTypeArr = StringUtil.split(colNameAndType, JobConstant.CLEAN_SEPARATOR);
				ParquetUtil.addData2Group(group, colNameAndTypeArr.get(0), colNameAndTypeArr.get(1),
						afterMergeColValue.toString());
				afterMergeColValue.delete(0, afterMergeColValue.length());
			}
			if (lineData != null) {
				//未实现
			}
			if (fileType.equals(FileFormatConstant.CSV.getMessage())) {
				afterMergeColValue.append(JobConstant.COLUMN_NAME_SEPARATOR);
			} else if (fileType.equals(FileFormatConstant.SEQUENCEFILE.getMessage())) {
				afterMergeColValue.append(JobConstant.COLUMN_NAME_SEPARATOR);
			}
		}
		return afterMergeColValue.toString();
	}
}
