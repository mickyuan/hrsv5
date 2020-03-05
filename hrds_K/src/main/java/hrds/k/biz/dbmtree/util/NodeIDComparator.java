package hrds.k.biz.dbmtree.util;

import fd.ng.core.annotation.DocClass;

import java.util.Comparator;

@DocClass(desc = "节点比较器", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:06")
public class NodeIDComparator implements Comparator<Node> {

    // 按照节点编号比较
    public int compare(Node n1, Node n2) {
        int x = Integer.parseInt(n1.id);
        int y = Integer.parseInt(n2.id);
        return (Integer.compare(x, y));
    }
}
