package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class HomePage extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private DefaultTreeModel model;
	private DefaultMutableTreeNode rootNode;

	private PrintWriter writer; // 有匿名内部类需要使用

	private Map<String, JFrame> chatBoxesMap;// 防止和同一个人重复创建chatBox

	private Socket socket;
	private String userName;
	private String friendListStr; // 好友列表，原Json串

	private ResponseManager manager; // 需要依赖注入

	public void setManager(ResponseManager manager) {
		this.manager = manager;
	}

	public void setFriendListStr(String s) {
		this.friendListStr = s;
	}

	// 解析消息，根据publisher传入ChatBox
	private Queue<String> msgQ;

	// 为了让manager传入消息
	public Queue<String> getMsgQueue() {
		return msgQ;
	}

	// 群聊消息
	private Queue<String> msgGroupQ;

	public Queue<String> getMsgGroupQueue() {
		return msgGroupQ;
	}

	public HomePage(Socket socket, String userName) {
		this.friendListStr = null;
		this.socket = socket;
		this.userName = userName;
		this.manager = null;

		setLocation(500, 170);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 540);
		setVisible(true);

		setLayout(new BorderLayout());

		this.chatBoxesMap = new HashMap<String, JFrame>();
		this.msgQ = new LinkedBlockingQueue<String>();
		this.msgGroupQ = new LinkedBlockingQueue<String>();

		Director director = new Director(new PrintWriterBuilder(socket));
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//paintWaitAction();

		ResConsumeThread thread = new ResConsumeThread();
		thread.setDaemon(true); // 设置为守护进程
		thread.start();

		ResConsumeGroupThread thread1 = new ResConsumeGroupThread();
		thread1.setDaemon(true); // 设置为守护进程
		thread1.start();
	}

	// 向不同的聊天窗口中发送聊天消息
	public class ResConsumeThread extends Thread {
		public void run() {
			String jsonStr = null;
			while (true) {
				try {
					// 从queue中取出string
					jsonStr = ((LinkedBlockingQueue<String>) msgQ).take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 解析publisher部分
				JSONObject json = (JSONObject) JsonTrans.parseJson(jsonStr,
						"res");
				String friName = json.getString("publisher");
				String content = json.getString("content");

				// 在map中找publisher
				ChatBox chatbox = (ChatBox) chatBoxesMap.get(friName);
				if (chatbox != null) {
					// 将消息添加到chatBox的消息队列中
					chatbox.getMsgQueue().offer(content);
					chatbox.setChatBoxesMap(chatBoxesMap);
				} else {
					try {
						chatbox = new ChatBox(socket, userName, friName);
						chatbox.setChatBoxesMap(chatBoxesMap);

						// 将新建的chatBox放入map
						chatBoxesMap.put(friName, chatbox);

					} catch (IOException e) {
						e.printStackTrace();
					}
					chatbox.getMsgQueue().offer(content);
				}
			}// end of while
		}
	}

	// 向群聊窗口中发送聊天消息
	public class ResConsumeGroupThread extends Thread {
		public void run() {
			String jsonStr = null;
			while (true) {
				try {
					// 从queue中取出string
					jsonStr = ((LinkedBlockingQueue<String>) msgGroupQ).take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("从homepage的群聊queue中取出消息");

				// 单件模式下的groupChatBox
				GroupChatBox groupBox = GroupChatBox.getInstance(socket,
						userName);

				// 直接传入原始串
				groupBox.getMsgQueue().offer(jsonStr);

			}// end of while
		}
	}

	// caller
	public void updateFriendList() {
		System.out.println("刷新好友列表");

		rootNode.removeAllChildren();

		// 通知ResponseManager更新自己的好友列表
		manager.updateFriendListStr();

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
			String name = (String) it.next();

			leafTreeNode = new DefaultMutableTreeNode(name);
			rootNode.add(leafTreeNode);
		}
		model.reload();
	}

	public void paintWaitAction() {
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
						String friend = (String) object;

						// 自己和自己不能聊天
						if (!friend.equals(userName)) {

							System.out.println("用户与" + friend + "进入聊天室");

							// 先查重
							ChatBox chatbox = (ChatBox) chatBoxesMap
									.get(friend);
							if (chatbox == null) {
								try {
									chatbox = new ChatBox(socket, userName,
											friend);

									// 将这个chatBox放入map保存，防止重复创建
									chatBoxesMap.put(friend, chatbox);

									// 注入map
									chatbox.setChatBoxesMap(chatBoxesMap);

								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}// end of if (chatbox == null)
							chatbox.setTitle("私聊窗口");
						}// end of if (!friend.equals(userName))
					}// end of if (node.isLeaf())
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