package Client.ResponseHandlers;

import java.net.Socket;

import Client.Pages.RegistPage;

public class ResRegistHandler extends ResponseHandler{

	public ResRegistHandler(Socket socket) {
		super(socket);
	}

	private RegistPage registPage;
	
	private String registFlagTemp;
	
	public void updateRegstFlag(){
		registPage.setRegistFlag(registFlagTemp);
	}
	
	@Override
	public void handleResponse() {
		registPage = RegistPage.getInstance(super.socket);
		
		registFlagTemp = super.responseMsg;
		
		//双向依赖更新registPage的注册标志
		registPage.regist();
	}

}
