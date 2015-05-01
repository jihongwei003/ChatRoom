package Tools.Bulider;

import java.io.IOException;
//import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

//从outputStream到printWriter的建造者
public class PrintWriterBuilder extends Builder {

	private Socket socket;

	// private OutputStream output;

	public PrintWriterBuilder() {
		super();
		this.socket = null;
	}

	public PrintWriterBuilder(Socket socket) {
		super();
		this.socket = socket;
	}

	public Object getResult() throws IOException {
		PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

		return pw;
	}
}
