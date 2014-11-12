package presenter.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;

import presenter.views.FileView;

public class PropertyFileUtil {

	private static Properties prop = new Properties();
	private InputStream input = null;
	final static Logger logger = Logger.getLogger(FileView.class);

	public void loadPropertyFile() {

		try {

			String filename = "msg.properties";
			input = PropertyFileUtil.class.getClassLoader()
					.getResourceAsStream(filename);
			if (input == null) {
				logger.info("Sorry, unable to find " + filename);
				return;
			}

			// load a properties file from class path, inside static method
			prop.load(input);

		} catch (IOException ex) {
			logger.error(ex);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}

	}

	public static void updatePropertyFile(HashMap<String, String> map) {
		OutputStream output = null;

		try {

			output = new FileOutputStream("msg.properties");
			// set the properties value
			for (Entry<String, String> entry : map.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}

			prop.store(output, null);

		} catch (IOException io) {
			logger.error(io);
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}

		}
	}

	public static Properties getProp() {
		return prop;
	}

	public void setProp(Properties prop) {
		this.prop = prop;
	}

}
