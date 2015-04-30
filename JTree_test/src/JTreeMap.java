import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class JTreeMap {

	public JTreeMap() {

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("软件部");// 自定义根节点

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("小吉", 1);
		map.put("小宏", 1);
		map.put("小伟", 1);

		DefaultMutableTreeNode leafTreeNode;
		
		Iterator<String> iter = map.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			//Object val = map.get(key);
			
			leafTreeNode = new DefaultMutableTreeNode(key);
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
