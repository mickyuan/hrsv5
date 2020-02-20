package hrds.k.biz.dbmtree.util;

import fd.ng.core.annotation.DocClass;

import java.util.Comparator;

@DocClass(desc = "节点比较器", author = "BY-HLL", createdate = "2020/2/20 0020 下午 07:06")
public class NodeIDComparator implements Comparator {

    // 按照节点编号比较
    public int compare(Object o1, Object o2) {
        int x = Integer.parseInt(((Node) o1).id);
        int y = Integer.parseInt(((Node) o2).id);
        return (x < y ? -1 : x == y ? 0 : 1);
    }
}
