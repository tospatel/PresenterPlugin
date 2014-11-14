package presenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import presenter.shared.Issue;

public class JsonUtil {
	final static Logger logger = Logger.getLogger(JsonUtil.class);

	private JsonUtil() {
	}

	public static Issue convertJsonToObject(File jsonFile) {
		logger.info("Invoke convertJsonToObject ");
		ObjectMapper mapper = new ObjectMapper();
		Issue fileList = null;
		try {

			// read from file, convert it to user class
			fileList = mapper.readValue(
			// new File("c:\\fileList.json"),
					jsonFile, new TypeReference<Issue>() {
					});

		} catch (JsonGenerationException e) {
			logger.error(e);

		} catch (JsonMappingException e) {

			logger.error(e);
			// file does not conform to standards, check to see if the file is
			// for a single vuln
			return convertJsonToObject2(jsonFile);

		} catch (IOException e) {

			logger.error(e);

		}

		return fileList;
	}

	public static Issue convertJsonToObject2(File jsonFile) {

		// parse single vuln content
		logger.info("Invoke convertJsonToObject2 ");
		ObjectMapper mapper = new ObjectMapper();
		Issue fileList = null;
		try {

			String ss = readFile(jsonFile);
			String prefix = "{\"page\": {\"limit\": 3,\"total\": 3,\"offset\": 0},\"href\": \"/api/source_vuln/\",\"collection\": [";
			String suffix = "],\"type\": \"source_vuln\"}";
			ss = prefix + ss + suffix;
			fileList = mapper.readValue(ss, new TypeReference<Issue>() {
			});

		} catch (Exception e) {

			logger.error(e);

		}

		return fileList;
	}

	public static String readFile(File file) throws Exception {

		FileInputStream stream = new FileInputStream(file.getPath());
		StringBuffer buffer = new StringBuffer((int) file.length());
		try {
			Reader in = new BufferedReader(new InputStreamReader(stream));
			char[] readBuffer = new char[2048];

			int n;
			while ((n = in.read(readBuffer)) > 0) {
				buffer.append(readBuffer, 0, n);
			}
			stream.close();
		} catch (IOException e) {
			// Err_file_io
			String message = "Err_file_io";
			// displayError(message);
			return null;
		}
		return buffer.toString();
	}

}