package Server.RequestHandlers;

//import Server.UserCollection.UserDB;
import Server.UserCollection.UserList;

public abstract class RequestHandler {

	protected UserList users;
	protected String requestMsg;
	
	public RequestHandler(UserList users){
		this.users = users;
	}
	
	public void setRequestMsg(String msg){
		this.requestMsg = msg;
	}
	
	//¥¶¿Ì«Î«Û
	public abstract void handleRequest();
}
