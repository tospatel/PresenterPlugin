package presenter.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import name.fraser.neil.plaintext.diff_match_patch;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import presenter.shared.AppConstant;
import presenter.shared.FileTableColumnDtl;
import presenter.util.customproject.CustomProjectNewWizard;

public class FileOperation {
	final static Logger logger = Logger.getLogger(FileOperation.class);

	diff_match_patch diffutil = new diff_match_patch();

	private FileOperation() {
	}

	/**
	 * Check for file existance in project
	 * 
	 * @param wbPage
	 * @param dirPath
	 * @param selectTreeItem
	 * @param fileNamePathUpdated
	 * @param fileNamePath
	 */
	public static void checkFileExist(final IWorkbenchPage wbPage,
			final TreeItem selectTreeItem, boolean fileNamePathUpdated,
			final Map<String, String> fileNamePath) {

		String fileName = selectTreeItem.getText(FileTableColumnDtl.fileIndex);
		// final int line = Integer.parseInt(((selectTreeItem
		// .getText(FileTableColumnDtl.locationIndex).isEmpty()) ? "0"
		// : selectTreeItem.getText(FileTableColumnDtl.locationIndex)));
		// String lineContent = selectTreeItem
		// .getText(FileTableColumnDtl.codeIndex);
		String fullPath = null;
		String fileExist = "";
		if (!fileName.isEmpty()) {

			// FileExistenanceRecursive fileChecking = new
			// FileExistenanceRecursive();
			// fileChecking.setFileFound(false);
			// fileChecking.setFilePath("");
			try {
				// Get the root of the workspace
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				// Get all projects in the workspace
				IProject[] projects = root.getProjects();

				// if (!fileNamePathUpdated) {
				for (IProject project : projects) {

					fileExist = FileExistenanceRecursive
							.checkingDirectoryForFile(project.getLocation()
									.toString(), fileName.substring(fileName
									.lastIndexOf(File.separator) + 1));
					if (fileExist != null
							&& (!fileExist.isEmpty())
							&& fileExist.startsWith(project.getLocation()
									.toString())) {
						fullPath = fileExist;
						fileExist = fileExist.substring(project.getLocation()
								.toString().lastIndexOf(File.separatorChar),
								fileExist.length());

						break;
					}
				}
				// } else {
				// for (IProject project : projects) {
				// fileExist =
				// fileNamePath.get(fileName.substring(fileName.lastIndexOf(File.separator)+1));
				// if (fileExist != null
				// && (!fileExist.isEmpty())
				// && fileExist.startsWith(project.getLocation()
				// .toString())) {
				// fileExist = fileExist.substring(
				// project.getLocation().toString()
				// .lastIndexOf(File.separatorChar),
				// fileExist.length());
				// break;
				// }
				// }
				//
				// }

				final String file = fileExist;
				final String path = fullPath;
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						findDeleteMarkers();

						loadFileByMarkingLineOrMethod(file, selectTreeItem,
								fileNamePath, path);
					}
				});

			} catch (Exception e) {
				logger.error(e);
			}

		} else {

			MessageBoxView.show(PropertyFileUtil.getProp().getProperty(
					"fileNameEmpty"));
		}
	}

	/**
	 * To load file by marking line or method
	 * 
	 * @param dirPath
	 * @param file
	 * @param selectTreeItem
	 * @param fileNamePath
	 * @param wbPage
	 */
	public static void loadFileByMarkingLineOrMethod(String file,
			TreeItem selectTreeItem, Map<String, String> fileNamePath,
			String filePath) {

		List<Object> lineContent = getCodeForMatching(selectTreeItem, 0);
		if (Integer.parseInt(lineContent.get(1).toString()) == 0) {
			List<Object> lineCont = getMethodNameMatching(selectTreeItem, 0);

			FileOperation.loadFileByMarkingMethod(file,
					Integer.parseInt(lineCont.get(1).toString()),
					lineCont.get(0).toString(), selectTreeItem, null,
					fileNamePath, filePath);
		} else {
			FileOperation.loadFile(file,
					Integer.parseInt(lineContent.get(1).toString()),
					lineContent.get(0).toString(), selectTreeItem, null,
					fileNamePath, filePath);
		}
	}

	/**
	 * load File By Marking Method
	 * 
	 * @param wbPage
	 * @param dirPath
	 * @param fileExist
	 * @param line
	 * @param lineContent
	 * @param selectTreeItem
	 * @param ipath
	 * @return
	 */
	public static IEditorPart loadFileByMarkingMethod(String fileExist,
			int line, String lineContent, TreeItem selectTreeItem, IPath ipath,
			Map<String, String> fileNamePath, String fullPath) {

		logger.info("Invoke loadFileByMarkingMethod " + fileExist);
		IWorkbenchPage wbPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (fileExist != null && !fileExist.isEmpty()) {

			// fileExist = fileExist.substring(dirPath.length());
			IPath path = new Path(fileExist);
			IFile fileToBeOpened = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			IMarker marker = null, marker2 = null;
			// IFile fileToBeOpened = path.toFile();
			try {
				List<String> contentList = null;
				logger.info(" raw loaction " + fileToBeOpened.getRawLocation());
				if (fileToBeOpened.getRawLocation() == null) {
					try {
						contentList = FileUtils.readLines(fileToBeOpened
								.getFullPath().toFile());

					} catch (Exception e) {

						fileToBeOpened = ResourcesPlugin.getWorkspace()
								.getRoot().getFile(new Path(fullPath));
						contentList = FileUtils.readLines(fileToBeOpened
								.getFullPath().toFile());
					}

				} else {
					contentList = FileUtils.readLines(fileToBeOpened
							.getRawLocation().makeAbsolute().toFile());
					logger.info("Full Path "
							+ fileToBeOpened.getRawLocation().makeAbsolute()
									.toFile());
				}

				// openExternalfile(fileToBeOpened
				// .getRawLocation().makeAbsolute().toFile());
				// List<String> contentList = FileUtils.readLines(fileToBeOpened
				// .getFullPath().toFile());

				List<Integer> lineNoList = FileOperation
						.fileMethodNameContentChecking(fileToBeOpened, line,
								lineContent, null, contentList, selectTreeItem);

				HashMap<String, Object> map = new HashMap<String, Object>();

				// IMarker marker = null, marker2 = null;
				int lineSize = lineNoList.size();
				try {

					if (lineSize == 0) {

						marker = fileToBeOpened.createMarker(IMarker.PROBLEM);
						map.put(IMarker.LINE_NUMBER, 1);
						map.put(IMarker.CHAR_START, 0);
						map.put(IMarker.CHAR_END, 0);
						map.put(IMarker.MESSAGE, PropertyFileUtil.getProp()
								.getProperty("lineMatchFail"));
						map.put(IMarker.TRANSIENT, true);
					} else {
						marker = fileToBeOpened.createMarker(IMarker.PROBLEM);
						map.put(IMarker.LINE_NUMBER, lineNoList.get(0));
						map.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
						map.put(IMarker.MESSAGE, "SAST Vulnerability  Warning");
						map.put(IMarker.TRANSIENT, true);
						// if (!selectTreeItem.getText(
						// FileTableColumnDtl.colStartIndex).equals("0")) {
						// map.put(IMarker.CHAR_START,
						// Integer.parseInt(selectTreeItem
						// .getText(FileTableColumnDtl.colStartIndex)));
						// map.put(IMarker.CHAR_END,
						// Integer.parseInt(selectTreeItem
						// .getText(FileTableColumnDtl.colEndIndex)));
						// }

					}

					marker.setAttributes(map);
				} catch (Exception e) {

				}
				// IDE.
				if (marker == null)
					IDE.openEditor(wbPage, fileToBeOpened);
				else
					IDE.openEditor(wbPage, marker);

				// IDE.gotoMarker(editor, marker2);
				// marker.delete();
				if (lineSize == 0) {
					// findDeleteMarkers();
					MessageBoxView.show(PropertyFileUtil.getProp().getProperty(
							"lineMatchFail"));
				} else {

					// Prabhu
					// MessageBoxView.show(PropertyFileUtil.getProp().getProperty("lineMethodMatchFail"));

				}

			} catch (IOException e) {
				logger.error(e);
			} catch (PartInitException e1) {
				logger.error(e1);
			} catch (CoreException e) {
				logger.error(e);
			}

		} else {
			checkExternalFile(selectTreeItem, fileNamePath, wbPage);

		}

		return null;
	}

	public static void checkExternalFile(TreeItem selectTreeItem,
			Map<String, String> fileNamePath, IWorkbenchPage wbPage) {
		String rowSelectedFileName = selectTreeItem.getText(
				FileTableColumnDtl.fileIndex).substring(
				selectTreeItem.getText(FileTableColumnDtl.fileIndex)
						.lastIndexOf(File.separator) + 1);
		// String filePath = null;
		// Get the root of the workspace
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// IWorkspaceRoot root = workspace.getRoot();
		// // Get all projects in the workspace
		// IProject[] projects = root.getProjects();
		// for (IProject project : projects) {
		//
		// filePath = new FileExistenanceRecursive().checkingDirectoryForFile(
		// project.getLocation().toString(), 0, rowSelectedFileName);
		//
		// }
		String filePath = null;
		if (!fileNamePath.get(rowSelectedFileName).isEmpty()) {
			filePath = fileNamePath.get(rowSelectedFileName);
		} else {
			filePath = FileExistenanceRecursive.checkingDirectoryForFile(
					fileNamePath.get(AppConstant.folderTextFieldName),
					rowSelectedFileName);
		}
		if (filePath != null && (!filePath.isEmpty())) {
			CustomProjectNewWizard customProject = new CustomProjectNewWizard(
					filePath, fileNamePath, selectTreeItem);
			// customProject.addPage();
			customProject.performFinish();
		} else {
			Shell shell = new Shell();
			shell.setLocation(400, 350);
			CustomFileBrowseDialog dialog = new CustomFileBrowseDialog(shell,
					PropertyFileUtil.getProp().getProperty("msg"),
					PropertyFileUtil.getProp().getProperty("fileNotExist"),
					wbPage);
			dialog.open(fileNamePath, selectTreeItem);
		}
	}

	/**
	 * delete old markers
	 */
	public static void findDeleteMarkers() {
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			IMarker[] markers = root.findMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE);

			for (int i = 0; i < markers.length; i++) {
				String message = (String) markers[i]
						.getAttribute(IMarker.MESSAGE);

				if (message != null
						&& message.startsWith("SAST Vulnerability  Warning")) {
					markers[i].delete();
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}

	}

	/**
	 * Fetch Method Name
	 * 
	 * @param selectTreeItem
	 * @param line
	 * @return
	 */
	public static List<Object> getMethodNameMatching(
			final TreeItem selectTreeItem, int line) {
		// String codeForMatching = null;
		List<Object> lineContent = new ArrayList<Object>();
		String[] lineDetails = selectTreeItem
				.getText(FileTableColumnDtl.formattedCodeIndex)
				.replaceAll("\\r", "").split("\\n");

		lineContent.add(lineDetails[line]);
		lineContent.add(line);

		return lineContent;
	}

	/**
	 * Checking method in file
	 * 
	 * @param fileContent
	 * @param line
	 * @param lineContent
	 * @param multipLineContent
	 * @param contentList
	 * @param selectTreeItem
	 * @return
	 */
	public static List<Integer> fileMethodNameContentChecking(
			IFile fileContent, int line, String lineContent,
			Map<Boolean, Set<String>> multipLineContent,
			List<String> contentList, TreeItem selectTreeItem) {

		// int contentMatchLineNo = 0;
		List<Integer> contentMatchLineNoList = null;
		try {

			if (line >= 0) {

				contentMatchLineNoList = new ArrayList<Integer>();
				int row = 0;

				// contentList
				for (String contentCheck : contentList) {
					// logger.info(trimLine(contentCheck)
					// + " test  " +trimLine(lineContent)+ " cond  "
					// + trimLine(contentCheck).equals(
					// trimLine(lineContent)));
					if (trimLine(contentCheck)
							.startsWith(trimLine(lineContent))) { // Change done
						// by sunil
						// searching
						// content
						contentMatchLineNoList.add(row + 1);
						break;
					}
					row++;
				}
				logger.info("Find Content " + trimLine(lineContent));
				// }
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return contentMatchLineNoList;
	}

	/**
	 * Code comparison
	 * 
	 * @param selectTreeItem
	 * @param line
	 * @return
	 */
	public static List<Object> getCodeForMatching(
			final TreeItem selectTreeItem, int line) {
		// String codeForMatching = null;
		List<Object> lineContent = new ArrayList<Object>();
		String[] lineDetails = selectTreeItem
				.getText(FileTableColumnDtl.formattedCodeIndex)
				.replaceAll("\\r", "").split("\\n");
		int startLine = getIntForStringData(selectTreeItem
				.getText(FileTableColumnDtl.startLineIndex));
		// int endLine = getIntForStringData(selectTreeItem
		// .getText(FileTableColumnDtl.endLineIndex));

		int lineNo = getIntForStringData(selectTreeItem
				.getText(FileTableColumnDtl.locationIndex));

		int lineToFetch = startLine + lineNo;

		if (lineToFetch > 0) {
			lineContent.add(lineDetails[lineNo]);
			lineContent.add(lineToFetch);
		} else {
			lineContent.add("");
			lineContent.add(0);
		}

		return lineContent;
	}

	/**
	 * get int for string
	 * 
	 * @param data
	 * @return
	 */
	public static int getIntForStringData(String data) {
		if (data != null && !data.isEmpty()) {
			return Integer.parseInt(data);
		}

		return 0;
	}

	/**
	 * Load file
	 * 
	 * @param wbPage
	 * @param dirPath
	 * @param fileExist
	 * @param line
	 * @param lineContent
	 * @param selectTreeItem
	 * @param ipath
	 * @return
	 */
	public static IEditorPart loadFile(String fileExist, int line,
			String lineContent, TreeItem selectTreeItem, IPath ipath,
			Map<String, String> fileNamePath, String fullPath) {

		logger.info("Invoke loadFile " + fileExist);
		IWorkbenchPage wbPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		if (fileExist != null && !fileExist.isEmpty()) {
			IMarker marker = null, marker2 = null;
			// fileExist = fileExist.substring(dirPath.length());
			IPath path = new Path(fileExist);
			IFile fileToBeOpened = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			// IFile fileToBeOpened = path.toFile();
			try {
				List<String> contentList = null;
				logger.info(" raw loaction " + fileToBeOpened.getRawLocation());
				if (fileToBeOpened.getRawLocation() == null) {
					try {
						contentList = FileUtils.readLines(fileToBeOpened
								.getFullPath().toFile());

					} catch (Exception e) {

						fileToBeOpened = ResourcesPlugin.getWorkspace()
								.getRoot().getFile(new Path(fullPath));
						contentList = FileUtils.readLines(fileToBeOpened
								.getFullPath().toFile());
					}

				} else {
					contentList = FileUtils.readLines(fileToBeOpened
							.getRawLocation().makeAbsolute().toFile());
					logger.info("full path "
							+ fileToBeOpened.getRawLocation().makeAbsolute()
									.toFile());
				}
				// openExternalfile(fileToBeOpened
				// .getRawLocation().makeAbsolute().toFile());
				// List<String> contentList = FileUtils.readLines(fileToBeOpened
				// .getFullPath().toFile());

				List<Integer> lineNoList = FileOperation.fileContentChecking(
						fileToBeOpened, line, lineContent, null, contentList,
						selectTreeItem);

				HashMap<String, Object> map = new HashMap<String, Object>();

				if (lineNoList.size() == 0) {
					List<Object> lineCont = getMethodNameMatching(
							selectTreeItem, 0);

					FileOperation.loadFileByMarkingMethod(fileExist,
							Integer.parseInt(lineCont.get(1).toString()),
							lineCont.get(0).toString(), selectTreeItem, null,
							fileNamePath, fullPath);
				} else {
					marker = fileToBeOpened.createMarker(IMarker.PROBLEM);
					map.put(IMarker.LINE_NUMBER, lineNoList.get(0));
					map.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					map.put(IMarker.MESSAGE, "SAST Vulnerability  Warning");
					map.put(IMarker.TRANSIENT, true);
					// if (!selectTreeItem.getText(
					// FileTableColumnDtl.colStartIndex).equals("0")) {
					// map.put(IMarker.CHAR_START,
					// Integer.parseInt(selectTreeItem
					// .getText(FileTableColumnDtl.colStartIndex)));
					// map.put(IMarker.CHAR_END,
					// Integer.parseInt(selectTreeItem
					// .getText(FileTableColumnDtl.colEndIndex)));
					//
					// }
					marker.setAttributes(map);
					IEditorPart editor = IDE.openEditor(wbPage, marker);
					// marker2 = fileToBeOpened.createMarker(IMarker.PROBLEM);
					// marker2.setAttribute(IMarker.LINE_NUMBER, 5);
					// marker2.setAttribute(IMarker.MESSAGE, "Another Warning");
					// marker2.setAttribute(IMarker.TRANSIENT, true);
					// marker2.setAttribute(IMarker.SEVERITY,
					// IMarker.SEVERITY_WARNING);

				}

			} catch (IOException e) {
				logger.error(e);
			} catch (PartInitException e1) {
				logger.error(e1);
			} catch (CoreException e) {
				logger.error(e);
			}

		} else {
			// MessageBoxView.show("File Not Exist");
			checkExternalFile(selectTreeItem, fileNamePath, wbPage);
			// CustomFileBrowseDialog dialog = new CustomFileBrowseDialog(shell,
			// PropertyFileUtil.getProp().getProperty("msg"),
			// PropertyFileUtil.getProp().getProperty("fileNotExist"),
			// wbPage);
			// dialog.open(fileNamePath, selectTreeItem);
		}

		return null;
	}

	public static String trimLine(String line) {
		// Prabhu: trim the /t etc
		// line = line.replaceAll("\\t", "");
		return line.trim();
	}

	/**
	 * Open File
	 * 
	 * @param filePath
	 * @param wbPage
	 */
	public static void openFile(String filePath, IWorkbenchPage wbPage) {
		IPath path = new Path(filePath);
		IFile fileToBeOpened = ResourcesPlugin.getWorkspace().getRoot()
				.getFile(path);

		HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put(IMarker.LINE_NUMBER, line);
		// map.put(IMarker.LOCATION, line);
		map.put(IMarker.MESSAGE, "Syntax error");

		// IMarker marker = null;

		try {
			logger.info("File Path === " + filePath);
			// openExternalfile(new File(filePath));
			// marker = fileToBeOpened.createMarker(IMarker.TEXT);
			// marker.setAttributes(map);s
			// IDE.getDefaultEditor(fileToBeOpened);
			IWorkbench wb = PlatformUI.getWorkbench();
			IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			IWorkbenchPage page = win.getActivePage();
			IDE.openEditor(page, fileToBeOpened);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	/**
	 * file Content Checking
	 * 
	 * @param fileContent
	 * @param line
	 * @param lineContent
	 * @param multipLineContent
	 * @param contentList
	 * @param selectTreeItem
	 * @return
	 */
	public static List<Integer> fileContentChecking(IFile fileContent,
			int line, String lineContent,
			Map<Boolean, Set<String>> multipLineContent,
			List<String> contentList, TreeItem selectTreeItem) {

		// int contentMatchLineNo = 0;
		List<Integer> contentMatchLineNoList = null;
		try {
			int fileSize = contentList.size();

			if (line >= 0) {

				if (line > 0 && fileSize >= line) {
					line = line - 1;
				} else {
					line = 0;
				}

				String contentByLine;
				contentMatchLineNoList = new ArrayList<Integer>();
				contentByLine = (String) contentList.get(line);

				// if (trimLine(contentByLine).equals(trimLine(lineContent))) {
				if (trimLine(contentByLine).startsWith(trimLine(lineContent))) {
					contentMatchLineNoList.add(line + 1);
				} else {

					int totalLineCheck = getIntForStringData(PropertyFileUtil
							.getProp().getProperty(
									"totalLineCheckingBeforeOrAfter"));

					int startLineChecking = line - totalLineCheck;
					int endLine = line + totalLineCheck;

					if (startLineChecking > 0 && endLine < fileSize) {

						for (int row = startLineChecking; row < endLine; row++) {
							if (trimLine(contentList.get(row)).startsWith(
									trimLine(lineContent))) {
								contentMatchLineNoList.add(row + 1);
								break;
							}
						}
					}
				}
				// Comment by sunil
				// else {
				// // Prabhu: try fuzzy search
				// String pattern = trimLine(lineContent);
				// // String text = fileContent.
				// String livecontent = readFile(fileContent.getLocation()
				// .toString());
				// diff_match_patch diff = new diff_match_patch();
				// int approx = diff.match_main(livecontent, pattern,
				// line * 22);
				// String aa = livecontent.substring(approx, approx
				// + (lineContent.length()));
				// System.out.println("Approx loc=" + approx + ":" + aa);
				// }
				// else {
				//
				// new Thread() {
				// public void run() {
				// Display.getDefault().asyncExec(new Runnable() {
				// public void run() {
				// MessageBoxView.show("Content Match Failed");
				// }
				// });
				// }
				//
				// }.start();
				//
				// }

				// }
			}
		} catch (Exception e) {
			logger.error(e);
		}

		return contentMatchLineNoList;
	}

	public static String removeSpecialCharacter(String lineContentCheck) {
		lineContentCheck = lineContentCheck.replaceAll("[\\n\\t]", "");
		return lineContentCheck;
	}

	public static String openFileDialog() {
		String firstFile = "";
		Shell shell = new Shell();
		shell.setLocation(300, 250);
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(new String[] { "Batch Files", "All Files (*.*)" });
		dialog.setFilterExtensions(new String[] { "*.json", "*.*" }); // Windows
		firstFile = dialog.open();
		return (firstFile != null ? dialog.getFilterPath() + File.separator
				+ dialog.getFileName() : "");
	}

	// Below methods are not used
	/***
	 * check code existance
	 * 
	 * @param contentList
	 * @param index
	 * @param lineContent
	 * @return
	 */
	public static int checkCodeExistance(List<String> contentList, int index,
			Set<String> lineContent) {

		String lineContentCheck = removeSpecialCharacter(contentList.get(index));

		if (lineContent.contains(lineContentCheck)) {
			++index;
			return index;
		}

		return 0;
	}

	/**
	 * Load file
	 * 
	 * @param wbPage
	 * @param dirPath
	 * @param fileExist
	 * @param line
	 * @param lineContent
	 * @param selectTreeItem
	 * @return
	 */
	public static IEditorPart loadFile(String dirPath, String fileExist,
			int line, String lineContent, TreeItem selectTreeItem) {

		logger.info("Invoke loadFile " + fileExist);

		if (!fileExist.isEmpty()) {
			IWorkbenchPage wbPage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			fileExist = fileExist.substring(dirPath.length());
			IPath path = new Path(fileExist);
			IFile fileToBeOpened = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);

			try {

				List<String> contentList = FileUtils.readLines(fileToBeOpened
						.getRawLocation().makeAbsolute().toFile());

				// Map<Boolean, Set<String>> multipLineContent =
				// checkMultiLineExist(lineContent);

				List<Integer> lineNoList = FileOperation.fileContentChecking(
						// fileToBeOpened, line, lineContent, multipLineContent,
						fileToBeOpened, line, lineContent, null, contentList,
						selectTreeItem);

				HashMap<String, Object> map = new HashMap<String, Object>();

				if (lineNoList.size() == 0)
					map.put(IMarker.LINE_NUMBER, 0);
				else
					map.put(IMarker.LINE_NUMBER, lineNoList.get(0));
				// }

				map.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				map.put(IMarker.TRANSIENT, true);
				map.put(IMarker.MESSAGE, "SAST Vulnerability  Error");

				IMarker marker = null;

				marker = fileToBeOpened.createMarker(IMarker.PROBLEM);
				marker.setAttributes(map);

				IDE.openEditor(wbPage, marker);

			} catch (IOException e) {
				logger.error(e);
			} catch (PartInitException e1) {
				logger.error(e1);
			} catch (CoreException e) {
				logger.error(e);
			}

		} else {
			// Shell shell = new Shell();
			// shell.setLocation(300, 250);

			MessageBoxView.show(PropertyFileUtil.getProp().getProperty(
					"fileNotExist"));
			// CustomFileBrowseDialog dialog = new CustomFileBrowseDialog(shell,
			// "Message", "File Not Exist", wbPage);
			// dialog.open();
		}

		return null;
	}

}
