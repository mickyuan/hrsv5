package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "自定义节点类", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:02")
public class Node {

    /**
     * 节点编号
     */
    private String id;
    /**
     * 节点内容
     */
    private String label;
    /**
     * 父节点编号
     */
    private String parent_id;
    /**
     * 节点描述
     */
    private String description;
    /**
     * 所属数据层
     */
    private String data_layer;
    /**
     * 数据所属类型
     */
    private String data_own_type;
    /**
     * 数据源id
     */
    private String data_source_id;
    /**
     * agent_id
     */
    private String agent_id;
    /**
     * 采集分类id
     */
    private String classify_id;
    /**
     * 表源属性id
     */
    private String file_id;
    /**
     * 原始表名
     */
    private String table_name;
    /**
     * 原始文件名或中文名称
     */
    private String original_name;
    /**
     * 系统内对应表名
     */
    private String hyren_name;
    /**
     * 孩子节点列表
     */
    private List<Node> children = new ArrayList<>();

    // 先序遍历，拼接JSON字符串
    public String toString() {
        String str = "{"
                + "id : '" + id + "'"
                + ", label : '" + label + "'"
                + ", parent_id : '" + parent_id + "'"
                + ", description : '" + description + "'"
                + ", data_layer : '" + data_layer + "'"
                + ", data_own_type : '" + data_own_type + "'"
                + ", data_source_id : '" + data_source_id + "'"
                + ", agent_id : '" + agent_id + "'"
                + ", classify_id : '" + classify_id + "'"
                + ", file_id : '" + file_id + "'"
                + ", table_name : '" + table_name + "'"
                + ", original_name : '" + original_name + "'"
                + ", hyren_name : '" + hyren_name + "'";
        if (children != null && children.size() != 0) {
            str += ", children : " + children.toString();
        } else {
            str += ", leaf : false";
        }
        return str + "}";
    }

    // 兄弟节点横向排序
    void sortChildren() {
        if (children != null && children.size() != 0) {
            // 对本层节点进行排序
            // 可根据不同的排序属性，传入不同的比较器，这里传入ID比较器
            children.sort(new NodeIDComparator());
            // 对每个节点的下一层节点进行排序
            children.forEach(Node::sortChildren);
        }
    }

    // 添加孩子节点
    void addChild(Node node) {
        this.children.add(node);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getData_layer() {
        return data_layer;
    }

    public void setData_layer(String data_layer) {
        this.data_layer = data_layer;
    }

    public String getData_own_type() {
        return data_own_type;
    }

    public void setData_own_type(String data_own_type) {
        this.data_own_type = data_own_type;
    }

    public String getData_source_id() {
        return data_source_id;
    }

    public void setData_source_id(String data_source_id) {
        this.data_source_id = data_source_id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getClassify_id() {
        return classify_id;
    }

    public void setClassify_id(String classify_id) {
        this.classify_id = classify_id;
    }

    public String getFile_id() {
        return file_id;
    }

    public void setFile_id(String file_id) {
        this.file_id = file_id;
    }

    public String getTable_name() {
        return table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
    }

    public String getHyren_name() {
        return hyren_name;
    }

    public void setHyren_name(String hyren_name) {
        this.hyren_name = hyren_name;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }
}
