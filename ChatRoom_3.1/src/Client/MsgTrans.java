package Client;

public class MsgTrans {

	private String publisher;
	private String msgNum;
	private String receiver;
	private String words;

	public MsgTrans(){
		this.publisher = null;
		this.msgNum = null;
		this.receiver = null;
		this.words = null;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setMsgNum(String msgNum) {
		this.msgNum = msgNum;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public void setWords(String words) {
		this.words = words;
	}

	public String getResult() {
		String jsonStr = "{\"publisher\": \"" + publisher +  "\", "
				+ "\"msgNum\": \"" + msgNum + "\", "
				+ "\"receiver\": \"" + receiver + "\", "
				+ "\"words\": \"" + words + "\" " + "}";

		return jsonStr;
	}
}
