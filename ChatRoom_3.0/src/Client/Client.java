package Client;

import java.net.*;
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
import javax.swing.JTextField;

import Tools.Bulider.BufferedReaderBuilder;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;

	private Socket socket;

	private JTextField jtfIP = new JTextField(4);
	private JTextField jtfPort = new JTextField(4);
	public JTextField jtfName = new JTextField(4);
	// public类型是为了在构造函数外边设置输入焦点，控件没有绘制出时无法获取焦点

	private String serverIP; //匿名内部类用
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
								System.out.println("用户注册成功！");
								
								ClientAdmin admin = new ClientAdmin(socket, clientName);
								admin.runAdmin();

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
		
		addWindowListener(new WindowAdapter() // 关闭窗口
		{
			public void windowClosing(WindowEvent e) {
				System.out.println("关闭登录页");
			}
		});
	}

	public static void main(String[] args) throws IOException {

		Client frame = new Client();
		frame.setLocation(500, 250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 150);
		frame.setVisible(true);

		//只能在构造函数外设置输入焦点
		frame.jtfName.requestFocus();

	}
}