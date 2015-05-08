package Client.Pages;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sf.json.JSONObject;
import Client.MsgTrans;
import Client.ResponseHandlers.ResDisplayFriendsHandler;
import Client.ResponseHandlers.ResponseHandler;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class HomePage extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode rootNode;

	private Socket socket;
	private PrintWriter writer; // 有匿名内部类需要使用

	// 当前用户
	private String userName;

	public void setUser(String user) {
		this.userName = user;
	}

	// 依赖于刷新好友列表策略类
	private ResponseHandler handler;

	public void setResponseHandler(ResDisplayFriendsHandler handler) {
		this.handler = handler;
	}

	// 好友列表，原Json串
	private String friendListStr;

	public void setFriendListStr(String s) {
		this.friendListStr = s;
	}

	// 单件模式
	private static HomePage s_homePage;

	public static HomePage getInstance(Socket socket) throws IOException {
		if (s_homePage == null) {
			s_homePage = new HomePage(socket);
		} else {
			System.out.println("主页已经存在，返回已存在的实例");
		}
		return s_homePage;
	}

	private HomePage(Socket socket) {
		this.socket = socket;

		this.userName = null;
		this.handler = null;
		this.friendListStr = null;

		Director director = new Director(new PrintWriterBuilder(socket));
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// paintWaitAction(); //外部指定
	}

	// caller
	public void updateFriendList() {
		System.out.println("刷新好友列表");

		rootNode.removeAllChildren();

		// 通知handler更新自己的好友列表
		((ResDisplayFriendsHandler) handler).updateFriendListStr();

		// 解析服务器包头
		JSONObject json1 = (JSONObject) JsonTrans.parseJson(friendListStr,
				"res");
		String content = json1.getString("content");// 取出content部分

		// 解析content部分
		JSONObject userMap = (JSONObject) JsonTrans.parseJson(content,
				"userMap");

		DefaultMutableTreeNode leafTreeNode;

		Iterator<?> it = userMap.keys();
		while (it.hasNext()) {
			String name = (String) it.next();// 真实名

			leafTreeNode = new DefaultMutableTreeNode(name);
			rootNode.add(leafTreeNode);
		}
		model.reload();
	}

	public void paintWaitAction() {
		setLocation(500, 170);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 540);
		// setVisible(true);
		setLayout(new BorderLayout());

		try {
			JLabel jlbUserName = new JLabel("用户名：" + userName);

			// 利用map建树
			rootNode = new DefaultMutableTreeNode("好友");

			// 向服务器请求好友列表
			MsgTrans msgTrans = new MsgTrans();
			msgTrans.setPublisher(userName);
			msgTrans.setMsgNum("1");
			String sendMsg = msgTrans.getResult();

			// 添加客户端包头
			String a = JsonTrans.buildJson("msg", sendMsg);

			writer.println(a);
			writer.flush();

			tree = new JTree(rootNode);
			model = (DefaultTreeModel) tree.getModel();

			JScrollPane jsp = new JScrollPane(tree);

			JButton jbtUpdate = new JButton("刷新");
			JButton jbtGroupChat = new JButton("群聊");

			JPanel jpSouth = new JPanel();
			jpSouth.add(jbtUpdate);
			jpSouth.add(jbtGroupChat);

			add(jlbUserName, BorderLayout.NORTH);
			add(jsp, BorderLayout.CENTER);
			add(jpSouth, BorderLayout.SOUTH);

			setVisible(true);

			// 刷新按钮事件
			jbtUpdate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 向服务器请求好友列表
					MsgTrans msgTrans = new MsgTrans();
					msgTrans.setPublisher(userName);
					msgTrans.setMsgNum("1");

					String sendMsg = msgTrans.getResult();

					// 添加客户端包头
					String a = JsonTrans.buildJson("msg", sendMsg);

					writer.println(a);
					writer.flush();
				}
			});

			// 群聊按钮事件
			jbtGroupChat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// 单件模式下的groupChatBox
					GroupChatBox groupBox = GroupChatBox.getInstance(socket,
							userName);
					groupBox.setTitle("群聊窗口");
				}
			});

			// 树叶节点的点击事件
			tree.addTreeSelectionListener(new TreeSelectionListener() {

				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
							.getLastSelectedPathComponent();
					if (node == null)
						return;

					Object object = node.getUserObject();
					if (node.isLeaf()) {
						String friend = (String) object; // 真实名

						// 自己和自己不能聊天
						if (!friend.equals(userName)) {
							ChatBox chatbox = ChatBox.getInstance(socket,
									userName, friend);
							chatbox.setTitle("私聊窗口");
						}
					}
				}
			});

			addWindowListener(new WindowAdapter() // 关闭窗口
			{
				public void windowClosing(WindowEvent e) {
					// 组装消息
					MsgTrans msgTrans = new MsgTrans();
					msgTrans.setPublisher(userName);
					msgTrans.setMsgNum("4");

					String sendMsg = msgTrans.getResult();

					// 添加客户端包头
					String a = JsonTrans.buildJson("msg", sendMsg);

					writer.println(a);
					writer.flush();

					// 关闭线程ResConsumeThread->已经将子线程设置为守护进程
					// thread.stop();
					// thread1.stop();

					System.out.println("关闭主页");

					System.exit(0); // 强行关闭所有线程
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}