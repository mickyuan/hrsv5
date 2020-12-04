package hrds.k.biz.algorithms.helper;

import org.apache.lucene.util.OpenBitSet;

public class FD {
    public OpenBitSet lhs;
    public int rhs;

    public FD(OpenBitSet lhs, int rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
}