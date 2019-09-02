package hrds.control.beans;

/**
 * @description: 列拆分实体类
 * @author: WangZhengcheng
 * @create: 2019-08-28 14:05
 **/
public class ColumnSplitBean {

    //TODO 老项目中还有一些字段不知道用途，所以仅体现以下5个字段
    private String colName;
    private String colOffset;
    private String colType;
    private Long seq;
    private String splitSep;
    private String splitType;

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColOffset() {
        return colOffset;
    }

    public void setColOffset(String colOffset) {
        this.colOffset = colOffset;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getSplitSep() {
        return splitSep;
    }

    public void setSplitSep(String splitSep) {
        this.splitSep = splitSep;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    @Override
    public String toString() {
        return "ColumnSplitBean{" +
                "colOffset='" + colOffset + '\'' +
                ", colType='" + colType + '\'' +
                ", seq=" + seq +
                ", splitSep='" + splitSep + '\'' +
                ", splitType='" + splitType + '\'' +
                '}';
    }
}
