package hrds.k.biz.algorithms.helper;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.lucene.util.OpenBitSet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UCCTreeElement {

    protected UCCTreeElement[] children;
    protected boolean isUCC;
    protected int numAttributes;

    public UCCTreeElement(int numAttributes, boolean isUCC) {
        this.isUCC = isUCC;
        this.numAttributes = numAttributes;
    }

    public int getNumAttributes() {
        return this.numAttributes;
    }

    // children

    public UCCTreeElement[] getChildren() {
        return this.children;
    }

    public void setChildren(UCCTreeElement[] children) {
        this.children = children;
    }

    public boolean hasChildren() {
        if (this.children == null)
            return false;
        for (int i = 0; i < this.children.length; i++)
            if (this.children[i] != null)
                return true;
        return false;
    }

    // isUCC

    public boolean isUCC() {
        return this.isUCC;
    }

    public void setUCC(boolean isUCC) {
        this.isUCC = isUCC;
    }

    public boolean isObsolete() {
        return (!this.hasChildren()) && (!this.isUCC);
    }

    protected void trimRecursive(int currentDepth, int newDepth) {
        if (currentDepth == newDepth) {
            this.children = null;
            return;
        }

        if (this.children == null)
            return;

        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i] == null)
                continue;

            UCCTreeElement child = this.children[i];
            child.trimRecursive(currentDepth + 1, newDepth);
            if (child.isObsolete())
                this.children[i] = null;
        }
    }

    protected void removeRecursive(OpenBitSet ucc, int currentUCCAttr) {
        if (currentUCCAttr < 0) {
            this.isUCC = false;
            return;
        }

        if ((this.children != null) && (this.children[currentUCCAttr] != null)) {
            this.children[currentUCCAttr].removeRecursive(ucc, ucc.nextSetBit(currentUCCAttr + 1));

            if (this.children[currentUCCAttr].isObsolete())
                this.children[currentUCCAttr] = null;
        }
    }

    protected void getUCCAndGeneralizations(OpenBitSet ucc, int currentUCCAttr, OpenBitSet currentUCC, List<OpenBitSet> foundUCCs) {
        if (this.isUCC)
            foundUCCs.add(currentUCC.clone());

        if (this.children == null)
            return;

        while (currentUCCAttr >= 0) {
            int nextLhsAttr = ucc.nextSetBit(currentUCCAttr + 1);

            if (this.children[currentUCCAttr] != null) {
                currentUCC.set(currentUCCAttr);
                this.children[currentUCCAttr].getUCCAndGeneralizations(ucc, nextLhsAttr, currentUCC, foundUCCs);
                currentUCC.clear(currentUCCAttr);
            }

            currentUCCAttr = nextLhsAttr;
        }
    }

    protected boolean containsUCCOrGeneralization(OpenBitSet ucc, int currentUCCAttr) {
        if (this.isUCC())
            return true;

        if (currentUCCAttr < 0)
            return false;

        int nextUCCAttr = ucc.nextSetBit(currentUCCAttr + 1);

        if ((this.children != null) && (this.children[currentUCCAttr] != null))
            if (this.children[currentUCCAttr].containsUCCOrGeneralization(ucc, nextUCCAttr))
                return true;

        return this.containsUCCOrGeneralization(ucc, nextUCCAttr);
    }

    protected boolean containsUCCOrSpecialization(OpenBitSet ucc, int currentUCCAttr) {
        if (this.isUCC() && (currentUCCAttr < 0))
            return true;

        if (this.children == null)
            return false;

        if (currentUCCAttr < 0) {
            for (int childAttr = 0; childAttr < this.children.length; childAttr++)
                if ((this.children[childAttr] != null) && (this.children[childAttr].containsUCCOrSpecialization(ucc, currentUCCAttr)))
                    return true;
        } else {
            int nextUCCAttr = ucc.nextSetBit(currentUCCAttr + 1);
            if (this.children[currentUCCAttr] != null)
                if (this.children[currentUCCAttr].containsUCCOrGeneralization(ucc, nextUCCAttr))
                    return true;
        }

        return false;
    }

    public void getLevel(int level, int currentLevel, OpenBitSet currentUCC, List<UCCTreeElementUCCPair> result) {
        if (level == currentLevel) {
            result.add(new UCCTreeElementUCCPair(this, currentUCC.clone()));
            return;
        }
        currentLevel++;
        if (this.children == null)
            return;

        for (int child = 0; child < this.numAttributes; child++) {
            if (this.children[child] == null)
                continue;

            currentUCC.set(child);
            this.children[child].getLevel(level, currentLevel, currentUCC, result);
            currentUCC.clear(child);
        }
    }

    public int writeUniqueColumnCombinations(/*UniqueColumnCombinationResultReceiver resultReceiver, */OutputStreamWriter receiver, OpenBitSet ucc, ObjectArrayList<ColumnIdentifier> columnIdentifiers, boolean writeTableNamePrefix) throws IOException {
        int numUCCs = 0;
        if (this.isUCC) {
            List<String> lhsIdentifier = new ArrayList<>();
            int j = 0;
            for (int i = ucc.nextSetBit(0); i >= 0; i = ucc.nextSetBit(i + 1)) {
                int columnId = i; //plis.get(i).getAttribute(); // Here we translate the column i back to the real column i before the sorting
                if (writeTableNamePrefix)
                    lhsIdentifier.add(columnIdentifiers.get(columnId).toString());
                else
                    lhsIdentifier.add(columnIdentifiers.get(columnId).getColumnIdentifier());
            }
            Collections.sort(lhsIdentifier);
            String lhsString = "[" + CollectionUtils.concat(lhsIdentifier, ",") + "]";
            // System.out.println(lhsString);
            // resultReceiver.receiveResult(uccResult);
            receiver.write(lhsString + "\n");
            numUCCs++;
            return numUCCs;
        }

        if (this.getChildren() == null)
            return numUCCs;

        for (int childAttr = 0; childAttr < this.numAttributes; childAttr++) {
            UCCTreeElement element = this.getChildren()[childAttr];
            if (element != null) {
                ucc.set(childAttr);
                numUCCs += element.writeUniqueColumnCombinations(/*resultReceiver, */receiver, ucc, columnIdentifiers, writeTableNamePrefix);
                ucc.clear(childAttr);
            }
        }
        return numUCCs;
    }

}
