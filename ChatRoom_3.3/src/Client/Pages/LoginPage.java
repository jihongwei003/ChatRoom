package Client.Pages;

import java.awt.BorderLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.json.JSONObject;
import Client.MsgTrans;
import Client.ResponseHandlers.ResLoginHandler;
import Client.ResponseHandlers.ResRegistHandler;
import Client.ResponseHandlers.ResponseHandler;
import Client.ResponseHandlers.SetNameHandler;
import Tools.JsonTrans;
import Tools.Bulider.Director;
import Tools.Bulider.PrintWriterBuilder;

public class LoginPage extends JFrame {
	private static final long serialVersionUID = 1L;

	private Socket socket;
	private String userName;

	private PrintWriter writer;

	// 注入登录处理策略类
	private ResponseHandler handler;

	public void setResponseHandler(ResLoginHandler handler) {
		this.handler = handler;
	}

	// 代替注册页注入处理策略类
	private ResponseHandler agentHandler;

	public void setRegistHandler(ResRegistHandler handler) {
		this.agentHandler = handler;
	}

	// 登录成功，将homePage设置为可见
	private HomePage homePage;

	public void setHomePage(HomePage home) {
		this.homePage = home;
	}

	// 登录成功标志，双向依赖于handler
	private String loginFlag;

	public void setLoginFlag(String flag) {
		this.loginFlag = flag;
	}

	public JTextField jtfName = new JTextField(4); // 只能在绘制完成后设置输入焦点

	// 单件模式
	private static LoginPage s_loginPage;

	public static LoginPage getInstance(Socket socket) {
		if (null == s_loginPage) {
			s_loginPage = new LoginPage(socket);
		} else {
			System.out.println("登录窗口已经存在，返回已存在的实例");
		}
		return s_loginPage;
	}

	private LoginPage(Socket socket) {
		this.socket = socket;
		this.loginFlag = null;

		// 向服务器发送登录请求
		Director director = new Director(new PrintWriterBuilder(this.socket));
		try {
			writer = (PrintWriter) director.construct();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		// paintWaitAction();
	}

	public void paintWaitAction() {
		setLocation(500, 220);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(300, 200);
		setTitle("登录");
		setLayout(new BorderLayout());

		// 上
		JPanel jpInput = new JPanel(new GridLayout(1, 2));

		JLabel jlbName = new JLabel("用户名:");
		jtfName.setText("吉宏伟");

		jpInput.add(jlbName);
		jpInput.add(jtfName);

		add(jpInput, BorderLayout.NORTH);

		// 下
		JPanel jpButton = new JPanel(new GridLayout(1, 2));

		JButton jbtLogin = new JButton("登录");
		JButton jbtRegist = new JButton("注册");
		jpButton.add(jbtLogin);
		jpButton.add(jbtRegist);

		add(jpButton, BorderLayout.SOUTH);

		setVisible(true);
		
		// 登录
		jbtLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// “空输入”判断
				if (jtfName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "输入信息不能为空");
				} else {
					// 获取输入框内容
					userName = jtfName.getText();

					// 将内容打包
					MsgTrans msgTrans = new MsgTrans();
					msgTrans.setPublisher(SetNameHandler.getTempName());
					msgTrans.setMsgNum("6");
					msgTrans.setWords(userName);
					String sendMsg = msgTrans.getResult();
					// 添加Client包头
					String jsonOut = JsonTrans.buildJson("msg", sendMsg);

					writer.println(jsonOut);
					writer.flush();
					
				}// end of else			
			}
		});// end of jbtOK

		// 注册
		jbtRegist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 跳转到注册页面
				RegistPage registPage = RegistPage.getInstance(socket);
				registPage.setResponseHandler((ResRegistHandler) agentHandler);
				registPage.paintWaitAction();
			}
		});// end of jbtOK

		addWindowListener(new WindowAdapter() // 关闭窗口
		{
			public void windowClosing(WindowEvent e) {
				System.out.println("关闭登录页");
			}
		});
	}

	public void login() {
		((ResLoginHandler) handler).updateLoginFlag();

		// 解析loginFlag部分
		JSONObject json = (JSONObject) JsonTrans.parseJson(loginFlag, "res");
		String flag = json.getString("content");

		if (flag.equals("true")) {
			// 登录成功
			homePage.setVisible(true);
			homePage.setUser(userName);
			homePage.paintWaitAction();

			SetNameHandler.setRealName(userName);
			
			dispose(); //关闭窗口
		} else if (flag.equals("false")) {
			// 用户已经登陆
			JOptionPane.showMessageDialog(null, "该用户已经登录");
		} else {
			// 用户未注册
			JOptionPane.showMessageDialog(null, "未注册用户");
		}
	}

}
