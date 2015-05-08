package Client.ResponseHandlers;

import java.net.Socket;

public abstract class ResponseHandler {

	protected String responseMsg;
	protected Socket socket;
	
	public ResponseHandler(Socket socket){
		this.socket = socket;
	}
	
	public void setResponseMsg(String msg) {
		this.responseMsg = msg;
	}

	// ¥¶¿Ìªÿ∏¥
	public abstract void handleResponse();
}
