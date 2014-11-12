package presenter.util;

import java.io.File;
import java.io.IOException;

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
		} catch (IOException e) {

			logger.error(e);

		}

		return fileList;
	}

}