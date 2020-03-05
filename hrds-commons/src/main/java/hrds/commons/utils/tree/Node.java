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
     * 孩子节点列表
     */
    private Children children = new Children();

    // 先序遍历，拼接JSON字符串
    public String toString() {
        String str = "{"
                + "id : '" + id + "'"
                + ", label : '" + label + "'";
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
