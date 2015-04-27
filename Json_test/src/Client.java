import java.util.ArrayList;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Client {

	public static void main(String[] args) {
		Client c = new Client();
		// c.parseJson1(c.buildJson1());

		ArrayList<String> list = new ArrayList<String>();
		list.add("first");
		list.add("second");
		JSONArray jsonArray2 = JSONArray.fromObject(list);

		JSONArray jsonArray = (JSONArray) c.parseJson(
				c.buildJson("list", jsonArray2), "list");

//		JSONArray jsonArray3 = (JSONArray) c.parseJson(
//				c.buildJson("list", jsonArray2), "list");
		
		Iterator<?> it = jsonArray.iterator();
		while (it.hasNext()) {
			String a = (String) it.next();
			System.out.println(a);
		}
	}

	public String buildJson(String key, Object obj) {
		// JSON格式数据解析对象
		JSONObject jo = new JSONObject();
		jo.put(key, obj);
		return jo.toString();
	}

	public Object parseJson(String jsonString, String key) {
		JSONObject jsonObj = JSONObject.fromObject(jsonString);

		return jsonObj.get(key);
	}

	// public String buildJson1() {
	// // JSON格式数据解析对象
	// JSONObject jo = new JSONObject();
	//
	// ArrayList<String> list = new ArrayList<String>();
	// list.add("first");
	// list.add("second");
	// JSONArray jsonArray2 = JSONArray.fromObject(list);
	//
	// jo.put("list", jsonArray2);
	//
	// return jo.toString();
	// }
	//
	// public void parseJson1(String jsonString) {
	// JSONObject jsonObj = JSONObject.fromObject(jsonString);
	// JSONArray ja = jsonObj.getJSONArray("list");
	//
	// Iterator<?> it = ja.iterator();
	// while(it.hasNext()){
	// String a = (String) it.next();
	// System.out.println(a);
	// }
	//
	// System.out.println(ja.toString());
	// }
}
