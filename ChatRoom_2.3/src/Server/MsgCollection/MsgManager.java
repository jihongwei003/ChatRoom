package Server.MsgCollection;

public class MsgManager {

	private MsgCollection msgQueue;
	
	public MsgManager(){
		msgQueue = MsgQueue.getInstance();
	}
	
	public  void addMsg(String msg) {
		msgQueue.addMsg(msg); 
	}

	public  String getMsg() {
		return msgQueue.getMsg();
	}
}
