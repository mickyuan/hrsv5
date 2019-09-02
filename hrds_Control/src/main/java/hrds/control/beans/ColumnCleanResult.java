package hrds.control.beans;

import java.io.Serializable;

/**
 * ClassName: ColumnCleanResult <br/>
 * Function: 列清洗规则实体 <br/>
 * Date: 2019/8/1 15:24 <br/>
 * <p>
 * Author WangZhengcheng
 * Version 1.0
 * Since JDK 1.8
 **/
public class ColumnCleanResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String columnName;
    private String clean_order;
    private String is_column_repeat_result;
    private String columnSplitResult;
    private String is_column_time_result;
    private String columnCodeResult;
    private String trimResult;
    private String is_column_file_result;

    public ColumnCleanResult() {
        super();
    }

    public String getClean_order() {
        return clean_order;
    }

    public void setClean_order(String clean_order) {
        this.clean_order = clean_order;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getIs_column_repeat_result() {
        return is_column_repeat_result;
    }

    public void setIs_column_repeat_result(String is_column_repeat_result) {
        this.is_column_repeat_result = is_column_repeat_result;
    }

    public String getColumnSplitResult() {
        return columnSplitResult;
    }

    public void setColumnSplitResult(String columnSplitResult) {
        this.columnSplitResult = columnSplitResult;
    }

    public String getIs_column_time_result() {
        return is_column_time_result;
    }

    public void setIs_column_time_result(String is_column_time_result) {
        this.is_column_time_result = is_column_time_result;
    }

    public String getColumnCodeResult() {
        return columnCodeResult;
    }

    public void setColumnCodeResult(String columnCodeResult) {
        this.columnCodeResult = columnCodeResult;
    }

    public String getTrimResult() {
        return trimResult;
    }

    public void setTrimResult(String trimResult) {
        this.trimResult = trimResult;
    }

    public String getIs_column_file_result() {
        return is_column_file_result;
    }

    public void setIs_column_file_result(String is_column_file_result) {
        this.is_column_file_result = is_column_file_result;
    }

    @Override
    public String toString() {
        return "ColumnCleanResult [columnName=" + columnName + ", clean_order=" + clean_order
                + ", is_column_repeat_result=" + is_column_repeat_result + ", columnSplitResult=" + columnSplitResult
                + ", is_column_time_result=" + is_column_time_result + ", columnCodeResult=" + columnCodeResult
                + ", trimResult=" + trimResult + ", is_column_file_result=" + is_column_file_result + "]";
    }
}
