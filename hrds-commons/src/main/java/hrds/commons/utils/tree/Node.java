package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "节点类", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:02")
public class Node {

    /**
     * 节点编号
     */
    String id;
    /**
     * 节点内容
     */
    String label;
    /**
     * 父节点编号
     */
    String parent_id;
    /**
     * 节点描述
     */
    String description;
    /**
     * 所属数据层
     */
    String data_layer;
    /**
     * 数据所属类型
     */
    String data_own_type;
    /**
     * 数据源id
     */
    String data_source_id;
    /**
     * agent_id
     */
    String agent_id;
    /**
     * 采集分类id
     */
    String classify_id;
    /**
     * 表源属性id
     */
    String file_id;
    /**
     * 孩子节点列表
     */
    private Children children = new Children();

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
                + ", file_id : '" + file_id + "'";
        if (children != null && children.getSize() != 0) {
            str += ", children : " + children.toString();
        } else {
            str += ", leaf : false";
        }
        return str + "}";
    }

    // 兄弟节点横向排序
    void sortChildren() {
        if (children != null && children.getSize() != 0) {
            children.sortChildren();
        }
    }

    // 添加孩子节点
    void addChild(Node node) {
        this.children.addChild(node);
    }
}
