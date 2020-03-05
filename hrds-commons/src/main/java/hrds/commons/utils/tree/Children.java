package hrds.commons.utils.tree;

import fd.ng.core.annotation.DocClass;

import java.util.ArrayList;
import java.util.List;

@DocClass(desc = "孩子列表类", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:04")
public class Children {

    private List<Node> nodeList = new ArrayList<>();

    int getSize() {
        return nodeList.size();
    }

    void addChild(Node node) {
        nodeList.add(node);
    }

    // 拼接孩子节点的JSON字符串
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        nodeList.forEach(node -> {
            sb.append(node.toString());
            sb.append(",");
        });
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }

    // 孩子节点排序
    void sortChildren() {
        // 对本层节点进行排序
        // 可根据不同的排序属性，传入不同的比较器，这里传入ID比较器
        nodeList.sort(new NodeIDComparator());
        // 对每个节点的下一层节点进行排序
        nodeList.forEach(Node::sortChildren);
    }
}
