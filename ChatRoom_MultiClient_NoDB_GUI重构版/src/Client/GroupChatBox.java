package Client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.json.JSONObject;
import Tools.JsonTrans;

public class GroupChatBox extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTextField jtfMsg = new JTextField();// 设为属性为了匿名内部类
	private JTextArea jtaMsg = new JTextArea();

	private GroupChatWriter sender;

	private Socket socket;
	private String userName;

	private Queue<String> msgQ; // 存储解析过的content

	public Queue<String> getMsgQueue() {
		return msgQ;
	}

	private static GroupChatBox s_box;

	public static GroupChatBox getInstance(Socket socket, String userName) {
		if (s_box == null) {
			try {
				s_box = new GroupChatBox(socket, userName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("群聊窗口已经存在，返回已存在的实例");
		}
		return s_box;
	}

	private GroupChatBox(Socket socket, String userName) throws IOException {
		this.socket = socket;
		this.userName = userName;
		// this.friendName = friendName;

		this.setLocation(500, 220);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(600, 400);
		this.setVisible(true);

		setTitle("GroupChatBox");
		setLayout(new BorderLayout());

		msgQ = new LinkedBlockingQueue<String>();
		sender = new GroupChatWriter(this.socket, this.userName);

		paintWaitAction();

		// 关闭当前窗口时，这条线程还存活吗？？？
		UpdateTextAreaThread thread = new UpdateTextAreaThread();
		thread.start();
	}

	private void paintWaitAction() {
		JLabel jlbUserName = new JLabel("当前用户:" + userName);

		JPanel jpnTop = new JPanel();
		jpnTop.add(jlbUserName);

		jtaMsg.setEnabled(false);
		jtaMsg.setLineWrap(true);// 激活自动换行功能
		jtaMsg.setWrapStyleWord(true);// 激活断行不断字功能
		// jtaMsg.selectAll();

		JScrollPane jsp = new JScrollPane(jtaMsg);

		JPanel jpList = new JPanel(new GridLayout(2, 1));
		jpList.add(jsp);
		jpList.add(jtfMsg);

		JButton jbtSend = new JButton("Send");

		add(jpnTop, BorderLayout.NORTH);
		add(jpList, BorderLayout.CENTER);
		add(jbtSend, BorderLayout.SOUTH);

		jbtSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String sendMsg = jtfMsg.getText();
				try {
					sender.send(sendMsg);

					// 将自己发送的消息加到textArea
					String a = userName + " 说：" + sendMsg;

					String temp = jtaMsg.getText();
					String output = "";
					if (temp.equals(""))
						output = a;
					else
						output = temp + "\n" + a;
					jtaMsg.setText(output);
					jtaMsg.setCaretPosition(jtaMsg.getText().length());

					jtfMsg.grabFocus();// 获得焦点
					jtfMsg.setText("");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					// 关闭线程UpdateTextAreaThread？？？？？？？？？？
					// thread.stop();

					// 关闭之后可以再次新建
					s_box = null;

					System.out.println("退出群聊");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public class UpdateTextAreaThread extends Thread {
		public void run() {
			try {
				while (true) {
					// 取出json串
					String jsonStr = ((LinkedBlockingQueue<String>) msgQ)
							.take();

					// 解析服务器包头
					JSONObject json = (JSONObject) JsonTrans.parseJson(jsonStr,
							"res");
					// 解析内容
					String friName = json.getString("publisher");
					String content = json.getString("content");

					String a = friName + " 说：" + content;

					String temp = jtaMsg.getText();

					String output = "";
					if (temp.equals(""))
						output = a;
					else
						output = temp + "\n" + a;

					jtaMsg.setText(output);
					jtaMsg.setCaretPosition(jtaMsg.getText().length());
				}
			} catch (Exception e) {
				System.out.print(e);
			}
		}
	}

}
