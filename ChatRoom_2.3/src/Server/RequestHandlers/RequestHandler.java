package Server.RequestHandlers;

import Server.UserCollection.UserDB;

public abstract class RequestHandler {

	protected UserDB users;
	protected String requestMsg;
	
	public RequestHandler(UserDB users){
		this.users = users;
	}
	
	public void setRequestMsg(String msg){
		this.requestMsg = msg;
	}
	
	//¥¶¿Ì«Î«Û
	public abstract void handleRequest();
}
