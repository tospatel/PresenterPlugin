package presenter.util;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * 
 * @author sunil.patel
 * 
 */
public class FileExistenanceRecursive {
	final static Logger logger = Logger
			.getLogger(FileExistenanceRecursive.class);
	boolean fileFound = false;
	String filePath = "";

	/**
	 * Populating File Path
	 * 
	 * @param dirPath
	 * @param level
	 * @param fileCheck
	 * @return
	 */

	public String checkingDirectoryForFile(String dirPath, int level,
			String fileCheck) {
		logger.info("=====================Dir Path " + dirPath);
		File dir = new File(dirPath);
		File[] firstLevelFiles = dir.listFiles();
		if (firstLevelFiles != null && firstLevelFiles.length > 0) {
			for (File aFile : firstLevelFiles) {
				for (int i = 0; i < level; i++) {
					System.out.print("\t");
				}
				if (aFile.isDirectory()) {
					// logger.info("[" + aFile.getName() + "]");
					if ((!aFile.getName().equals("bin"))
							&& (!aFile.getName().equals("classes"))
							&& (!aFile.getName().equals("lib"))) {
						String fileExist = checkingDirectoryForFile(
								aFile.getAbsolutePath(), level + 1, fileCheck);

						if (fileExist != null && (!fileExist.isEmpty())) {
							return fileExist;
						}
					}
				} else {
					// fileExist = fileCheck.equals(aFile.getName());
					logger.info(aFile.getName());
					if (fileCheck.equals(aFile.getName()) && fileFound == false) {
						fileFound = true;
						// filePath = aFile.getAbsolutePath();
						// logger.info(aFile.getName() + " fileExist "
						// + aFile.getAbsolutePath());
						filePath = aFile.getPath();

						logger.info(aFile.getName() + " fileExist "
								+ aFile.getAbsolutePath());
						return filePath;
					}
				}
			}
		}

		return filePath;
	}

	/**
	 * Populating path for multiple file
	 * 
	 * @param dirPath
	 * @param level
	 * @param fileCheck
	 */
	public Map<String, String> checkingDirectoryForMultipleFile(String dirPath,
			int level, Map<String, String> fileCheck) {
		logger.info("checkingDirectoryForMultipleFile =====================Dir Path "
				+ dirPath);
		File dir = new File(dirPath);
		File[] firstLevelFiles = dir.listFiles();
		if (firstLevelFiles != null && firstLevelFiles.length > 0) {
			for (File aFile : firstLevelFiles) {
				for (int i = 0; i < level; i++) {
					System.out.print("\t");
				}
				if (aFile.isDirectory()) {
					// logger.info("[" + aFile.getName() + "]");
					if ((!aFile.getName().equals("bin"))
							&& (!aFile.getName().equals("classes"))
							&& (!aFile.getName().equals("lib"))) {
						fileCheck = checkingDirectoryForMultipleFile(
								aFile.getAbsolutePath(), level + 1, fileCheck);
					}
				} else {
					// fileExist = fileCheck.equals(aFile.getName());
					// logger.info(aFile.getName());
					for (Entry<String, String> entry : fileCheck.entrySet()) {

						if (entry.getKey().equals(aFile.getName())
								&& (entry.getValue().isEmpty() || entry
										.getValue() == null)) {

							// fileFound = true;
							// filePath = aFile.getAbsolutePath();
							// logger.info(aFile.getName() +
							// " fileExist "
							// + aFile.getAbsolutePath());
							// filePath = aFile.getPath();
							fileCheck.put(aFile.getName() + "",
									aFile.getAbsolutePath());
							// fileCheck.
							// fileCheck.put("AbstractLesson.java","123");
							// fileCheck.containsKey("AbstractLesson.java")
							logger.info("key " + entry.getKey() + " value "
									+ aFile.getName() + " fileExist "
									+ aFile.getAbsolutePath());
							// return filePath;

						}
					}

				}
			}
		}

		return fileCheck;
	}

	public static String checkFileInDirectories(String dirPath, int level,
			String fileName) {
		logger.info("====Checking Dir Path " + dirPath + " for filename "
				+ fileName);
		File dir = new File(dirPath);
		File[] firstLevelFiles = dir.listFiles();
		String path = "";
		if (firstLevelFiles != null && firstLevelFiles.length > 0) {
			for (File aFile : firstLevelFiles) {
				for (int i = 0; i < level; i++) {
					System.out.print("\t");
				}
				if (aFile.isDirectory()) {
					// logger.info("[" + aFile.getName() + "]");
					if ((!aFile.getName().equals("bin"))
							&& (!aFile.getName().equals("classes"))
							&& (!aFile.getName().equals("lib"))) {
						String fileExist = checkFileInDirectories(
								aFile.getAbsolutePath(), level + 1, fileName);
						if (fileExist != null && (!fileExist.isEmpty())) {
							return fileExist;
						}
					}
				} else {
					// fileExist = fileCheck.equals(aFile.getName());
					// logger.info(""+aFile.getName());
					if (fileName.equals(aFile.getName())) {

						// filePath = aFile.getAbsolutePath();
						// logger.info(aFile.getName() + " fileExist "
						// + aFile.getAbsolutePath());
						path = aFile.getPath();

						logger.info(aFile.getName() + " fileExist "
								+ aFile.getAbsolutePath());
						// break;
						return path;
					}
				}
			}
		}

		return path;
	}

	public boolean isFileFound() {
		return fileFound;
	}

	public void setFileFound(boolean fileFound) {
		this.fileFound = fileFound;
	}

	public String isFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
