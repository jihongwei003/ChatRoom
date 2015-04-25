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
	//public������Ϊ���ڹ��캯������������뽹�㣬�ؼ�û�л��Ƴ�ʱ�޷���ȡ����

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
		jtfName.setText("����ΰ");

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

				// �������롱�ж�
				if (jtfIP.getText().equals(null)
						|| jtfPort.getText().length() < 1
						|| jtfName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "������Ϣ����Ϊ��");
				} else {
					// �жϲ�Ϊ��֮����ת��
					serverPort = Integer.parseInt(jtfPort.getText());

					try {
						// �������������������
						socket = new Socket(serverIP, serverPort);

						// �������ע�����û�
						Director director = new Director(
								new PrintWriterBuilder(socket));
						PrintWriter writer = (PrintWriter) director.construct();

						director = new Director(new BufferedReaderBuilder(
								socket));
						BufferedReader reader = (BufferedReader) director
								.construct();

						// ���� username
						writer.println(clientName);
						// ����һ���ַ���
						String result = reader.readLine();

						if (!result.equals("�ɹ�")) {
							JOptionPane.showMessageDialog(null, "�û�����ע�ᣡ");
						} else {
							dispose();
							try {
								// �½���ҳ
								HomePage homePageFrame = new HomePage(socket,
										clientName);
								homePageFrame.setTitle("��ҳ");

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

			JLabel jlbUserName = new JLabel("�û�����" + userName);

			// �������Ҫ�������б�����Ȼ����ݷ��ص��ཨ����
			Director director = new Director(new PrintWriterBuilder(socket));
			try {
				// ����������͡�1��
				writer = (PrintWriter) director.construct();
				writer.println("1");
				writer.flush();

				director = new Director(new BufferedReaderBuilder(socket));
				reader = (BufferedReader) director.construct();
				String jsonString = reader.readLine();

				JSONObject json = (JSONObject) JsonTrans.parseJson(jsonString,
						"userMap");

				// ����map����
				rootNode = new DefaultMutableTreeNode("����");

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

				JButton jbtUpdate = new JButton("ˢ��");
				JButton jbtGroupChat = new JButton("Ⱥ��");

				JPanel jpSouth = new JPanel();
				jpSouth.add(jbtUpdate);
				jpSouth.add(jbtGroupChat);

				add(jlbUserName, BorderLayout.NORTH);
				add(jsp, BorderLayout.CENTER);
				add(jpSouth, BorderLayout.SOUTH);

				// ��Ҷ�ڵ�ĵ���¼�
				tree.addTreeSelectionListener(new TreeSelectionListener() {

					public void valueChanged(TreeSelectionEvent e) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent();
						if (node == null)
							return;

						Object object = node.getUserObject();
						if (node.isLeaf()) {
							String user = (String) object;
							
							// ֪ͨ����������˽��״̬
							writer.println("2");
							writer.flush();

							//�ȴ���������Ӧ���ѽ���˽��״̬�����������֣�
							try {
								System.out.println(reader.readLine());
								
								//����Ӧ�ü�һЩ��ʱ�������ƣ�����
								
							} catch (IOException e2) {
								e2.printStackTrace();
							}
							
							//System.out.println("��������������");//Ϊ�˵ȴ�����������˽��״̬��							
							writer.println(user);//����п��ܷ�̫����
							writer.flush();
							
							System.out.println("�û���" + user + "����������");
							
							try {
								ChatBox chatbox = new ChatBox(socket, user);
								chatbox.setTitle("");

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				});

				// ˢ�°�ť�¼�
				jbtUpdate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						rootNode.removeAllChildren();

						System.out.println("ˢ��");

						//���������������б�
						writer.println("1"); 
						writer.flush();
						
						String jsonString = null;
						try {
							jsonString = reader.readLine();
						} catch (IOException e1) {
							System.out.println("jsonString���մ���");
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

				addWindowListener(new WindowAdapter() // �رմ���
				{
					public void windowClosing(WindowEvent e) {
						writer.println("4");
						writer.flush();
						System.out.println("�ر���ҳ");
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
		
		frame.addWindowListener(new WindowAdapter() // �رմ���
		{
			public void windowClosing(WindowEvent e) {
				System.out.println("�رյ�¼ҳ");
			}
		});

	}
}