package Tools.Bulider;

import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

//从inputStream到bufferedReader的建造者
public class BufferedReaderBuilder extends Builder {

	private Socket socket;

	// private InputStream input;

	public BufferedReaderBuilder() {
		super();
		this.socket = null;
	}

	public BufferedReaderBuilder(Socket socket) {
		super();
		this.socket = socket;
	}

	@Override
	public Object getResult() throws IOException {
		InputStreamReader isreader = new InputStreamReader(
				socket.getInputStream());
		BufferedReader reader = new BufferedReader(isreader);

		return reader;
	}

}
