package presenter.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.json.JSONObject;
import presenter.shared.Issue;

import com.google.gson.Gson;

import flexjson.JSONDeserializer;

public class JsonUtility {
	public static Object deserialize(String code) {
		return new JSONDeserializer<Object>().deserialize(code.toString());
	}

	public static void main(String[] args) {

		Gson gson = new Gson();
		JsonUtility jsonObj = new JsonUtility();
		JSONObject json;
		try {
			// System.out.println(StringUtils.isEmpty(null));
			// System.out
			// .println(deserialize(<body><div id="main" class="box">));
			// BufferedReader br = new BufferedReader(new FileReader(
			// "/Users/sunilpatel/Documents/whitehat/json/test.json"));
			// String text = br.readLine();
			// // convert the json string back to object
			// // FileList obj = gson.fromJson(br, FileList.class);
			// jsonObj = (Json) JSONObject.toBean(JSONObject.fromObject(br),
			// Json.class);
			final Issue fileList = JsonUtil
					.convertJsonToObject(new File(
							"/Users/sunilpatel/Documents/whitehat/json/jsonsample.json"));
			System.out.println(fileList);

			for (Object col : fileList.getCollection()) {
				System.out.println(col);
				LinkedHashMap collectionMap = (LinkedHashMap) col;
				LinkedHashMap traces = (LinkedHashMap) collectionMap
						.get("traces");
				List objCollTrace = (List) traces.get("collection");
				for (Object stepObject : objCollTrace) {
					LinkedHashMap traceMap = (LinkedHashMap) stepObject;
					LinkedHashMap stepMap = (LinkedHashMap) traceMap
							.get("steps");
					List stepList = (List) stepMap.get("collection");
					for (Object stepColllection : stepList) {
						LinkedHashMap step = (LinkedHashMap) stepColllection;
					}
					System.out.println(stepObject);
				}
				// }
				System.out.println(traces);
			} // System.out
				// .println("Read JSON from file, convert JSON string back to object");
				// BufferedReader reader = new BufferedReader(new FileReader(
				// "/Users/sunilpatel/Documents/whitehat/json/test.json"));
				// jsonObj = new JSONDeserializer<Json>().deserialize(reader);

			// JSONSerializer serializer = new JSONSerializer();
			// String str = serializer.serialize(jsonObj);

			// System.out.println(new JSONDeserializer<JsonUtility>()
			// .deserialize(reader));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
