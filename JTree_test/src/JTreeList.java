import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class JTreeList {

	public JTreeList() {

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("软件部");// 自定义根节点

		ArrayList<String> list = new ArrayList<String>();
		list.add("小吉");
		list.add("小宏");
		list.add("小伟");

		DefaultMutableTreeNode leafTreeNode;
		for (String x : list) {
			leafTreeNode = new DefaultMutableTreeNode(x);
			rootNode.add(leafTreeNode);
		}

		final JTree tree = new JTree(rootNode);// 面板中的树

		JFrame f = new JFrame("JTreeDemo");
		f.add(tree);
		f.setSize(300, 300);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// 添加选择事件
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				if (node == null)
					return;

				Object object = node.getUserObject();
				if (node.isLeaf()) {
					String user = (String) object;
					System.out.println("你选择了：" + user.toString());
				}

			}
		});
	}
}
