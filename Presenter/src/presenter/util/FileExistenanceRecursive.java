package presenter.util;

import java.io.File;
import java.util.Map;

import javaxt.io.Directory;

import org.apache.log4j.Logger;

/**
 * 
 * @author sunil.patel
 * 
 */
public class FileExistenanceRecursive {
	final static Logger logger = Logger
			.getLogger(FileExistenanceRecursive.class);

	// boolean fileFound = false;
	// String filePath = "";
	private FileExistenanceRecursive() {
	}

	/**
	 * Populating File Path
	 * 
	 * @param dirPath
	 * @param level
	 * @param fileCheck
	 * @return
	 */
	public static String checkingDirectoryForFile(String dirPath,
			String fileCheck) {
		logger.info("fileCheck===========> " + fileCheck);
		boolean recursiveSearch = true;
		Object fileFilter = new String(fileCheck);
		boolean wait = false;
		javaxt.io.Directory directory = new Directory(dirPath);
		// Initiate search
		java.util.List results = directory.getChildren(recursiveSearch,
				fileFilter, wait);
		while (true) {
			Object item;
			synchronized (results) {

				// Wait for files/directories to be added to the list
				while (results.isEmpty()) {
					try {
						results.wait();
					} catch (InterruptedException e) {
						break;
					}
				}

				// Grab the next available file/directory from the list
				item = results.remove(0);
				results.notifyAll();
			}

			// Do something with the file/directory
			if (item != null) {

				if (item instanceof javaxt.io.File) {
					javaxt.io.File file = (javaxt.io.File) item;
					String fileNm = file.toString();
					logger.info("file=== "
							+ file.toString()
							+ "  >>>   "
							+ (fileNm.toString().substring(fileNm
									.lastIndexOf(File.separator) + 1)));
					if (fileCheck.equals(fileNm.toString().substring(
							fileNm.lastIndexOf(File.separator) + 1))) {
						logger.info("File Found===========> " + file.toString());
						return file.toString();
					}
				} else {
					javaxt.io.Directory dir = (javaxt.io.Directory) item;
				}
			} else { // Item is null. This is our queue that the search is done!
				break;
			}
		}
		return "";
	}

	/**
	 * Populating path for multiple file
	 * 
	 * @param dirPath
	 * @param level
	 * @param fileCheck
	 */
	public static void checkingDirectoryForMultipleFile(String dirPath,
			int level, Map<String, String> fileCheck) {
		// logger.info("checkingDirectoryForMultipleFile =====================Dir Path "
		// + dirPath);
		File dir = new File(dirPath);
		File[] firstLevelFiles = dir.listFiles();
		if (firstLevelFiles != null && firstLevelFiles.length > 0) {
			for (File aFile : firstLevelFiles) {
				// for (int i = 0; i < level; i++) {
				// System.out.print("\t");
				// }
				if (aFile.isDirectory()) {
					// logger.info("[" + aFile.getName() + "]");
					if ((!aFile.getName().equals("bin"))
							&& (!aFile.getName().equals("classes"))
							&& (!aFile.getName().equals("lib"))) {
						checkingDirectoryForMultipleFile(
								aFile.getAbsolutePath(), level + 1, fileCheck);
					}
				} else {
					// fileExist = fileCheck.equals(aFile.getName());
					// logger.info(aFile.getName() + " condition "
					// + fileCheck.containsKey(aFile.getName()));
					// for (Entry<String, String> entry : fileCheck.entrySet())
					// {

					// if (entry.getKey().equals(aFile.getName())
					// && (entry.getValue().isEmpty() || entry
					// .getValue() == null)) {

					// fileFound = true;
					// filePath = aFile.getAbsolutePath();
					// logger.info(aFile.getName() +
					// " fileExist "
					// + aFile.getAbsolutePath());
					// filePath = aFile.getPath();

					if (fileCheck.containsKey(aFile.getName())) {

						fileCheck.put(aFile.getName(), aFile.getAbsolutePath());
						// fileCheck.
						// fileCheck.put("AbstractLesson.java","123");
						// fileCheck.containsKey("AbstractLesson.java")
						logger.info("File Found ====> " + aFile.getName()
								+ "   " + aFile.getAbsolutePath());
						// return filePath;

					}
				}
				// }

			}
			// }
		}

		// return fileCheck;
	}

}
