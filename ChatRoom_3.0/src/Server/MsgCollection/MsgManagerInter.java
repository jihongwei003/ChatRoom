package Server.MsgCollection;

//为了使用日志代理类，必须将实现类转化为抽象类
public interface MsgManagerInter {
	public void addMsg(String msg);

	public String getMsg();
}
