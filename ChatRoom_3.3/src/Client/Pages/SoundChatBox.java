package Client.Pages;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import Client.MsgTrans;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class SoundChatBox extends JFrame {
	private static final long serialVersionUID = 1L;

	private Socket socket;
	private String userName;
	private String friendName;

	private PrintWriter writer;

	// 单件模式
	private static SoundChatBox s_soundChatBox;

	public static SoundChatBox getInstance(Socket socket, String userName,
			String friendName) {
		if (null == s_soundChatBox) {
			s_soundChatBox = new SoundChatBox(socket, userName, friendName);
		} else {
			System.out.println("语音聊天窗口已经存在，返回已存在实例");
		}
		return s_soundChatBox;
	}

	private SoundChatBox(Socket socket, String userName, String friendName) {
		this.socket = socket;
		this.userName = userName;
		this.friendName = friendName;

		// 指导者
		Director director = new Director(new PrintWriterBuilder(this.socket));
		// 使用指导者生成一个writer
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e) {
			e.printStackTrace();
		}

		paintWaitAction();
	}

	public JButton jbtOK; // 为了外部设置是否可按

	private void paintWaitAction() {
		setLocation(600, 250);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(200, 100);
		// setVisible(true); //这个写在最后，要不然控件一开始显示不出来！

		setTitle("语音聊天");
		setLayout(new GridLayout(3, 1));

		JLabel jbl = new JLabel("好友：" + friendName);

		jbtOK = new JButton("开始");
		JButton jbtCancel = new JButton("停止");

		add(jbl);
		add(jbtOK);
		add(jbtCancel);

		// 开始
		jbtOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 按钮变为disable
				jbtOK.setEnabled(false);

				// 向Server发送语音聊天请求
				MsgTrans msgTrans = new MsgTrans();
				msgTrans.setPublisher(userName);
				msgTrans.setMsgNum("10");
				msgTrans.setReceiver(friendName);
				String sendMsg = msgTrans.getResult();
				// 添加Client包头
				String jsonOut = JsonTrans.buildJson("msg", sendMsg);

				writer.println(jsonOut);
				writer.flush();
			}
		});

		// 取消
		jbtCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 按钮变为able
				jbtOK.setEnabled(true);

				// 向Server发送中止语音聊天请求
				MsgTrans msgTrans = new MsgTrans();
				msgTrans.setPublisher(userName);
				msgTrans.setMsgNum("11");
				msgTrans.setReceiver(friendName);
				msgTrans.setWords("cancel"); // 让服务器知道是取消还是关闭
				String sendMsg = msgTrans.getResult();
				// 添加Client包头
				String jsonOut = JsonTrans.buildJson("msg", sendMsg);

				writer.println(jsonOut);
				writer.flush();
			}
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("关闭语音聊天窗口");

				if (s_soundChatBox != null) {
					// 向Server发送中止语音聊天请求
					MsgTrans msgTrans = new MsgTrans();
					msgTrans.setPublisher(userName);
					msgTrans.setMsgNum("11");
					msgTrans.setReceiver(friendName);
					msgTrans.setWords("close"); // 让服务器知道是取消还是关闭
					String sendMsg = msgTrans.getResult();
					// 添加Client包头
					String jsonOut = JsonTrans.buildJson("msg", sendMsg);

					writer.println(jsonOut);
					writer.flush();
				}
				s_soundChatBox = null;
			}
		});

		setVisible(true);
	}

	public void closeWindows() {
		dispose();
		s_soundChatBox = null;
	}

}
