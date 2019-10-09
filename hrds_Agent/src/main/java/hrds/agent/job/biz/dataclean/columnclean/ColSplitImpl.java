package hrds.agent.job.biz.dataclean.columnclean;

import fd.ng.core.utils.StringUtil;
import hrds.agent.job.biz.bean.ColumnSplitBean;
import hrds.agent.job.biz.constant.FileFormatConstant;
import hrds.agent.job.biz.constant.JobConstant;
import hrds.agent.job.biz.utils.DataTypeCheck;
import hrds.agent.job.biz.utils.ParquetUtil;
import hrds.agent.job.biz.utils.StringOperator;
import org.apache.parquet.example.data.Group;

import java.util.List;

/**
 * ClassName: ColSplitImpl <br/>
 * Function: 数据库直连采集列拆分清洗实现类 <br/>
 * Reason: 继承AbstractColumnClean抽象类，只针对一个列拆分方法进行实现
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColSplitImpl extends AbstractColumnClean {

	//TODO ORC,SEQUENCE未实现

	/**
	 * 列拆分方法具体实现
	 *
	 * 1、构造存放拆分完成之后的StringBuilder
	 * 2、在拆分的同事保留原字段
	 * 3、遍历拆分规则
	 *      3-1、得到ColumnSplitBean类对象，如果原列的值为空，则追加空字符串
	 *      3-2、如果原列的值不为空，获取拆分的类型
	 *      3-3、若拆分的类型为1，则获取分割字符串的起始下标和结束下标，并进行字符串分割
	 *      3-4、
	 *
	 * @Param: rule List<ColumnSplitBean>
	 *         含义：存放字段拆分规则的List集合
	 *         取值范围：不为空
	 * @Param: columnValue String
	 *         含义：待清洗字段值
	 *         取值范围：不为空
	 * @Param: columnName String
	 *         含义：列名
	 *         取值范围：不为空
	 * @Param: group Group
	 *         含义：用于写Parquet的一行数据
	 *         取值范围：不为空
	 * @Param: colType String
	 *         含义：列类型
	 *         取值范围：不为空
	 * @Param: fileType String
	 *         含义：卸数落地数据文件的格式
	 *         取值范围：不为空，FileFormatConstant代码项的code
	 * @Param: lineData List<Object>
	 *         含义：用于写ORC
	 *         取值范围：不为空
	 *
	 * @return: String
	 *          含义：清洗后的字段值
	 *          取值范围：不会为null
	 *
	 * */
	@Override
	public String split(List<ColumnSplitBean> ruleList, String columnValue, String columnName,
	                    Group group, String colType, String fileType, List<Object> lineData) {
		if ((ruleList != null && !ruleList.isEmpty()) && group != null) {
			StringBuilder oriAndNewField = new StringBuilder(4096);
			//目前的操作是在拆分的同时保留原字段，下面代码的就是做这件事
			if (fileType.equalsIgnoreCase(FileFormatConstant.CSV.getMessage())) {
				oriAndNewField.append(columnValue).append(JobConstant.COLUMN_NAME_SEPARATOR);
			} else if (fileType.equalsIgnoreCase(FileFormatConstant.PARQUET.getMessage())) {
				ParquetUtil.addData2Group(group, columnName, colType, columnValue);
				oriAndNewField.append(columnValue);
			} else if (fileType.equalsIgnoreCase(FileFormatConstant.ORCFILE.getMessage())) {
				//未实现
			} else {
				//未实现
			}

			for(ColumnSplitBean columnSplitBean : ruleList){
				String newColName = columnSplitBean.getColName();
				if (StringUtil.isBlank(columnValue)) {
					if (fileType.equalsIgnoreCase(FileFormatConstant.CSV.getMessage())) {
						oriAndNewField.append("");
					} else if (fileType.equalsIgnoreCase(FileFormatConstant.PARQUET.getMessage())) {
						ParquetUtil.addData2Group(group, columnName, colType, "");
					} else if (fileType.equalsIgnoreCase(FileFormatConstant.ORCFILE.getMessage())) {
						//未实现
					} else {
						//未实现
					}
				} else {
					//获取拆分的类型
					String substr = "";
					if (columnSplitBean.getSplitType().equals("1")) {
						String colOffset = columnSplitBean.getColOffset();
						String[] split = colOffset.split(",");
						int start = Integer.parseInt(split[0]);
						int end = Integer.parseInt(split[1]);
						substr = columnValue.substring(start, end);
					} else {
						int num = StringUtil.isEmpty(StringOperator.getString(columnSplitBean.getSeq())) ?
								0 : Integer.parseInt(columnSplitBean.getSeq().toString());
						List<String> splitInNull = StringUtil.split(columnValue, columnSplitBean.getSplitSep());
						substr = splitInNull.get(num);
					}
					if (fileType.equalsIgnoreCase(FileFormatConstant.CSV.getMessage())) {
						if (DataTypeCheck.checkType(columnSplitBean.getColType())) {
							oriAndNewField.append(StringUtil.replace(substr, ",", ""));
						} else {
							oriAndNewField.append(substr);
						}
					} else if (fileType.equalsIgnoreCase(FileFormatConstant.PARQUET.getMessage())) {
						ParquetUtil.addData2Group(group, newColName.toUpperCase(),
								columnSplitBean.getColType(), substr);
					} else if (fileType.equalsIgnoreCase(FileFormatConstant.ORCFILE.getMessage())) {
						//未实现
					} else {
						//未实现
					}
				}
				if (fileType.equalsIgnoreCase(FileFormatConstant.CSV.getMessage()) ||
						fileType.equalsIgnoreCase(FileFormatConstant.SEQUENCEFILE.getMessage())) {
					oriAndNewField.append(JobConstant.COLUMN_NAME_SEPARATOR);
				}
			}

			if (fileType.equalsIgnoreCase(FileFormatConstant.CSV.getMessage()) ||
					fileType.equalsIgnoreCase(FileFormatConstant.SEQUENCEFILE.getMessage())) {
				//FIXME 这里的处理有问题
				oriAndNewField = oriAndNewField.deleteCharAt(oriAndNewField.length() - 1);
			}
			return oriAndNewField.toString();
		}
		return columnValue;
	}
}
