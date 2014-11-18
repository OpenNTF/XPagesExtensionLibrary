package extlib.tree;

import com.ibm.xsp.extlib.tree.impl.BasicLeafTreeNode;
import com.ibm.xsp.extlib.tree.impl.BasicNodeList;

public class SimpleTreeNode extends BasicNodeList {

	private static final long serialVersionUID = 1L;

	public SimpleTreeNode() {
		addLeaf("Node 1", "node 1");
		addLeaf("Node 2", "node 2");
		addLeaf("Node 3", "node 3");
	}
	
	private void addLeaf(String label, String submitValue) {
		BasicLeafTreeNode node = new BasicLeafTreeNode();
		node.setLabel(label);
		node.setSubmitValue(submitValue);
		addChild(node);
	}
}
