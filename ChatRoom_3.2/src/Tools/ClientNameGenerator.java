package Tools;

import java.text.SimpleDateFormat;
import java.util.Random;

public class ClientNameGenerator {
	
	public ClientNameGenerator() {	
	}

	public static String gen() {
		Random r = new Random();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		String randomName = dateFormat
				.format(new java.util.Date())
				+ r.nextInt(10)
				+ r.nextInt(10)
				+ r.nextInt(10);	
		return randomName;
	}
}
