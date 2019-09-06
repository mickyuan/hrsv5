package hrds.agent.job.biz.bean;

import java.io.Serializable;

/**
 * ClassName: TableCleanResult <br/>
 * Function: 全表数据清洗实体 <br/>
 * Reason: 海云向agent发送，agent解析生成实体
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class TableCleanResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private String clean_order;
	private TableTrimResult tableTrimResult;
	private String columnMergeResult;
	private String is_table_repeat_result;
	private String is_table_fille_result;

	public String getClean_order() {
		return clean_order;
	}

	public void setClean_order(String clean_order) {
		this.clean_order = clean_order;
	}

	public TableTrimResult getTableTrimResult() {
		return tableTrimResult;
	}

	public void setTableTrimResult(TableTrimResult tableTrimResult) {
		this.tableTrimResult = tableTrimResult;
	}

	public String getColumnMergeResult() {
		return columnMergeResult;
	}

	public void setColumnMergeResult(String columnMergeResult) {
		this.columnMergeResult = columnMergeResult;
	}

	public String getIs_table_repeat_result() {
		return is_table_repeat_result;
	}

	public void setIs_table_repeat_result(String is_table_repeat_result) {
		this.is_table_repeat_result = is_table_repeat_result;
	}

	public String getIs_table_fille_result() {
		return is_table_fille_result;
	}

	public void setIs_table_fille_result(String is_table_fille_result) {
		this.is_table_fille_result = is_table_fille_result;
	}

	@Override
	public String toString() {
		return "TableCleanResult [clean_order=" + clean_order + ", tableTrimResult=" + tableTrimResult
				+ ", columnMergeResult=" + columnMergeResult + ", is_table_repeat_result=" + is_table_repeat_result
				+ ", is_table_fille_result=" + is_table_fille_result + "]";
	}
}
