package Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import net.sf.json.JSONObject;
import Client.Pages.HomePage;
import Client.Pages.LoginPage;
import Client.ResCollection.ResponseCollectionManager;
import Client.ResponseHandlers.ResDisplayFriendsHandler;
import Client.ResponseHandlers.ResLoginHandler;
import Client.ResponseHandlers.ResRegistHandler;
import Client.ResponseHandlers.ResponseHandler;
import Tools.JsonTrans;

public class ResponseManager {
	private Socket socket;

	// 客户端消息队列
	private ResponseCollectionManager resCollection;

	// 存放策略类
	private Map<?, ?> responseMap;

	public void setResponseMap(Map<?, ?> map) {
		this.responseMap = map;
	}

	public ResponseManager(Socket socket) {
		this.resCollection = null;
		this.socket = socket;
	}

	// 需要依赖注入
	public void setResponseCollectionManager(
			ResponseCollectionManager resCollection) {
		this.resCollection = resCollection;
	}

	public void runManager() {
		// 创建主页窗口，设置为不可见
		HomePage homePage = null;
		try {
			homePage = HomePage.getInstance(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		homePage.setVisible(false);
		homePage.setResponseHandler((ResDisplayFriendsHandler) responseMap
				.get("1"));
		// homePage.paintWaitAction();

		// 创建登录页
		LoginPage loginPage = LoginPage.getInstance(socket);
		loginPage.paintWaitAction();
		loginPage.setHomePage(homePage);
		loginPage.jtfName.requestFocus(); // 输入焦点
		loginPage.setResponseHandler((ResLoginHandler) responseMap.get("6"));
		loginPage.setRegistHandler((ResRegistHandler) responseMap.get("5")); // 帮注册页面注入handler

		ResDistributeThread thread = new ResDistributeThread();
		thread.setDaemon(true); // 设置为守护进程
		thread.start();

	}

	public class ResDistributeThread extends Thread {
		public void run() {
			while (true) {
				String msg = resCollection.getMsg(); // 阻塞式的取
				System.out.println("【取出来自Server的回应消息为：" + msg + "】");

				try {
					// 解析服务器包头
					JSONObject json = (JSONObject) JsonTrans.parseJson(msg,
							"res");
					String key = json.getString("msgNum");// 解析出的消息编号

					// 策略模式
					ResponseHandler handler = (ResponseHandler) responseMap
							.get(key);
					handler.setResponseMsg(msg);
					handler.handleResponse();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
