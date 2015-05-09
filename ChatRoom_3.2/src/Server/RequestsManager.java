package Server;

import java.util.Map;

import net.sf.json.JSONObject;
//import Server.MsgCollection.MsgManager;
import Server.MsgCollection.MsgManagerInter;
import Server.RequestHandlers.RequestHandler;
import Tools.JsonTrans;

public class RequestsManager extends Thread {

	private Map<?, ?> requestMap;
	private MsgManagerInter msgManager;

	public RequestsManager() {
		requestMap = null;
		msgManager = null;
	}

	public void setRequestMap(Map<?, ?> map) {
		this.requestMap = map;
	}

	public void setMsgManager(MsgManagerInter msgManager) {
		this.msgManager = msgManager;
	}

	public void run() {
		while (true) {
			String msg = msgManager.getMsg(); // 阻塞式的取
			System.out.println("【取出来自Client的请求消息为" + msg + "】");

			JSONObject json = (JSONObject) JsonTrans.parseJson(msg, "msg");
			String key = json.getString("msgNum");// 解析出的消息编号

			// 策略模式
			RequestHandler handler = (RequestHandler) requestMap.get(key);
			handler.setRequestMsg(msg);
			handler.handleRequest();
		}
	}
}
