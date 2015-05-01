package Client;

import java.net.*;
//import java.util.HashMap;
import java.util.Iterator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import net.sf.json.JSONObject;
import Server.JsonTrans;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;

	private Socket socket;

	private JTextField jtfIP = new JTextField(4);
	private JTextField jtfPort = new JTextField(4);
	public JTextField jtfName = new JTextField(4);
	//public类型是为了在构造函数外边设置输入焦点，控件没有绘制出时无法获取焦点

	private String serverIP;
	private int serverPort;
	private String clientName;

	public Client() {
		socket = null;
		setTitle("ChatBox");
		setLayout(new BorderLayout());

		JPanel jpInput = new JPanel(new GridLayout(4, 2));

		JLabel jlbIP = new JLabel("ServerIP:");
		JLabel jlbPort = new JLabel("Port:");
		JLabel jlbName = new JLabel("Name:");

		jtfIP.setText("localhost");
		jtfPort.setText("8888");
		jtfName.setText("吉宏伟");

		jpInput.add(jlbIP);
		jpInput.add(jtfIP);

		jpInput.add(jlbPort);
		jpInput.add(jtfPort);

		jpInput.add(jlbName);
		jpInput.add(jtfName);

		add(jpInput, BorderLayout.NORTH);

		JButton jbtOK = new JButton("Connect");
		add(jbtOK, BorderLayout.SOUTH);

		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//jtfName.grabFocus();
				
				serverIP = jtfIP.getText();
				clientName = jtfName.getText();

				// “空输入”判断
				if (jtfIP.getText().equals(null)
						|| jtfPort.getText().length() < 1
						|| jtfName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "输入信息不能为空");
				} else {
					// 判断不为空之后再转化
					serverPort = Integer.parseInt(jtfPort.getText());

					try {
						// 向服务器发出连接请求
						socket = new Socket(serverIP, serverPort);

						// 向服务器注册新用户
						Director director = new Director(
								new PrintWriterBuilder(socket));
						PrintWriter writer = (PrintWriter) director.construct();

						director = new Director(new BufferedReaderBuilder(
								socket));
						BufferedReader reader = (BufferedReader) director
								.construct();

						// 发送 username
						writer.println(clientName);
						// 接受一个字符串
						String result = reader.readLine();

						if (!result.equals("成功")) {
							JOptionPane.showMessageDialog(null, "用户名已注册！");
						} else {
							dispose();
							try {
								// 新建主页
								HomePage homePageFrame = new HomePage(socket,
										clientName);
								homePageFrame.setTitle("主页");

							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}

					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}// end of else
			}
		});// end of jbtOK
	}

	public class HomePage extends JFrame {
		private static final long serialVersionUID = 1L;

		JTree tree;
		DefaultTreeModel model;
		DefaultMutableTreeNode rootNode;

		PrintWriter writer;
		BufferedReader reader;

		public HomePage(final Socket socket, String userName) {

			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setSize(300, 540);
			setVisible(true);

			setLayout(new BorderLayout());

			JLabel jlbUserName = new JLabel("用户名：" + userName);

			// 向服务器要“好友列表”，然后根据返回的类建立树
			Director director = new Director(new PrintWriterBuilder(socket));
			try {
				// 向服务器发送“1”
				writer = (PrintWriter) director.construct();
				writer.println("1");
				writer.flush();

				director = new Director(new BufferedReaderBuilder(socket));
				reader = (BufferedReader) director.construct();
				String jsonString = reader.readLine();

				JSONObject json = (JSONObject) JsonTrans.parseJson(jsonString,
						"userMap");

				// 利用map建树
				rootNode = new DefaultMutableTreeNode("好友");

				DefaultMutableTreeNode leafTreeNode;

				Iterator<?> it = json.keys();
				while (it.hasNext()) {
					String name = (String) it.next();

					leafTreeNode = new DefaultMutableTreeNode(name);
					rootNode.add(leafTreeNode);
				}

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

				// 树叶节点的点击事件
				tree.addTreeSelectionListener(new TreeSelectionListener() {

					public void valueChanged(TreeSelectionEvent e) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent();
						if (node == null)
							return;

						Object object = node.getUserObject();
						if (node.isLeaf()) {
							String user = (String) object;
							
							// 通知服务器进入私聊状态
							writer.println("2");
							writer.flush();

							//等待服务器响应“已进入私聊状态”（两次握手）
							try {
								System.out.println(reader.readLine());
								
								//这里应该加一些超时处理机制？？？
								
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							
							//System.out.println("即将进入聊天室");//为了等待服务器进入私聊状态！							
							writer.println(user);//这个有可能发太快了
							writer.flush();
							
							System.out.println("用户与" + user + "进入聊天室");
							
							try {
								ChatBox chatbox = new ChatBox(socket, user);
								chatbox.setTitle("");

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				// 刷新按钮事件
				jbtUpdate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						rootNode.removeAllChildren();

						System.out.println("刷新");

						//向服务器请求好友列表
						writer.println("1"); 
						writer.flush();
						
						String jsonString = null;
						try {
							jsonString = reader.readLine();
						} catch (IOException e1) {
							System.out.println("jsonString接收错误！");
							e1.printStackTrace();
						}

						JSONObject json = (JSONObject) JsonTrans.parseJson(
								jsonString, "userMap");

						DefaultMutableTreeNode leafTreeNode;

						Iterator<?> it = json.keys();
						while (it.hasNext()) {
							String name = (String) it.next();

							leafTreeNode = new DefaultMutableTreeNode(name);
							rootNode.add(leafTreeNode);
						}

						model.reload();
					}
				});

				addWindowListener(new WindowAdapter() // 关闭窗口
				{
					public void windowClosing(WindowEvent e) {
						writer.println("4");
						writer.flush();
						System.out.println("关闭主页");
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	

	public static void main(String[] args) throws IOException {

		Client frame = new Client();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 150);
		frame.setVisible(true);

		frame.jtfName.requestFocus();
		
		frame.addWindowListener(new WindowAdapter() // 关闭窗口
		{
			public void windowClosing(WindowEvent e) {
				System.out.println("关闭登录页");
			}
		});

	}
}