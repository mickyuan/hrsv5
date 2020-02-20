package hrds.k.biz.dbmtree.util;

import fd.ng.core.annotation.DocClass;

@DocClass(desc = "节点类", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:02")
public class Node {

    /**
     * 节点编号
     */
    public String id;
    /**
     * 节点内容
     */
    public String label;
    /**
     * 父节点编号
     */
    public String parent_id;
    /**
     * 孩子节点列表
     */
    private Children children = new Children();

    // 先序遍历，拼接JSON字符串
    public String toString() {
        String result = "{"
                + "id : '" + id + "'"
                + ", label : '" + label + "'";
        if (children != null && children.getSize() != 0) {
            result += ", children : " + children.toString();
        } else {
            result += ", leaf : true";
        }
        return result + "}";
    }

    // 兄弟节点横向排序
    public void sortChildren() {
        if (children != null && children.getSize() != 0) {
            children.sortChildren();
        }
    }

    // 添加孩子节点
    public void addChild(Node node) {
        this.children.addChild(node);
    }
}
