package presenter.views;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;

import presenter.shared.AppConstant;
import presenter.shared.FileTableColumnDtl;
import presenter.shared.Issue;
import presenter.util.Base64Util;
import presenter.util.FileExistenanceRecursive;
import presenter.util.FileOperation;
import presenter.util.JsonUtil;
import presenter.util.PropertyFileUtil;
import presenter.util.StringUtility;

public class TreeTable {

	private Label filenameLbl;
	private Button openBtn;
	private Tree tree;
	private IWorkbenchPage wbPage;
	private String dirPath;
	private TreeItem parentItem;
	private String description;
	private String solution;
	private Label createFolderLbl;
	private Text createFolderTxt;
	/* ID to store Layout-Data with TreeColumns */
	//  private static final String LAYOUT_DATA = "org.rssowl.ui.internal.CTreeLayoutData"; //$NON-NLS-1$
	private String[] colName = { FileTableColumnDtl.id,
			FileTableColumnDtl.traceId, FileTableColumnDtl.name,
			FileTableColumnDtl.file, FileTableColumnDtl.appId,
			FileTableColumnDtl.status, FileTableColumnDtl.classname,
			FileTableColumnDtl.risk, FileTableColumnDtl.impact,
			FileTableColumnDtl.location, FileTableColumnDtl.code,
			FileTableColumnDtl.startLine, FileTableColumnDtl.endLine,
			FileTableColumnDtl.lineNo, FileTableColumnDtl.colStart,
			FileTableColumnDtl.colEnd, FileTableColumnDtl.hash,
			FileTableColumnDtl.description, FileTableColumnDtl.solution,
			FileTableColumnDtl.compliance, FileTableColumnDtl.formattedCode };
	private TreeItem selectTreeItem;
	private boolean itemSelection = false;
	private Composite parent;
	Map<String, String> fileNamePath = null;
	private Boolean fileNamePathUpdated = false;
	private ArrayList<Browser> tabItemList = new ArrayList<Browser>();
	final static Logger logger = Logger.getLogger(TreeTable.class);
	private TabFolder tabFolder = null;

	/**
	 * Constructor allow to show table
	 * 
	 * @param parent
	 * @param wbPage
	 * @param workSpacePath
	 * @param workspace
	 */
	public TreeTable(Composite parent, IWorkbenchPage wbPage,
			String workSpacePath, IWorkspace workspace) {
		this.parent = parent;
		this.wbPage = wbPage;
		this.dirPath = workSpacePath;
		// this.workspace = workspace;
		this.addChildControls(parent);
	}

	/**
	 * This method add button & table
	 * 
	 * @param composite
	 */
	private void addChildControls(Composite composite) {
		logger.info(" Calling addChildControls ");
		// Create a composite to hold the children

		createGroupFields(composite);
		Label separator = new Label(composite, SWT.VERTICAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(SWT.None, SWT.FILL, false, true,
				1, 3));
		setSnippet(parent);

		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);

		setTableLayout(composite);

		Label version = new Label(composite, SWT.RIGHT);
		version.setText(getVersionAndDate());
		version.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false, 1,
				1));

		setTableColumn();
		createListner();
		openPreviousJsonFile();
	}

	/**
	 * Set table layout
	 * 
	 * @param parent
	 */
	public void setTableLayout(Composite parent) {

		int style = SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER;
		tree = new Tree(parent, style);
		GridData gridData = new GridData(GridData.FILL_BOTH); // new
		gridData.grabExcessVerticalSpace = true;

		tree.setLayoutData(new GridData(SWT.None, SWT.FILL, false, true, 1, 2));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

	}

	public void setSnippet(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.BORDER);
		for (int loopIndex = 0; loopIndex < 3; loopIndex++) {
			TabItem tabItem = new TabItem(tabFolder, SWT.NULL);
			String header = "";
			switch (loopIndex) {
			case 0:
				header = "Snippet";
				break;
			// case 1:
			// header = "Live";
			// break;
			// case 2:
			// header = "Diff";
			// break;
			case 1:
				header = "Description";
				break;
			case 2:
				header = "Solution";
				break;
			}

			tabItem.setText(header);

			Browser browser = new Browser(tabFolder, SWT.NONE);
			browser.setText("");
			tabItem.setControl(browser);
			tabItemList.add(browser);

		}

		GridData gridData = new GridData(GridData.FILL_BOTH); // new
		gridData.grabExcessVerticalSpace = true;

		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				2));
	}

	/**
	 * Set table columne name and width
	 */
	public void setTableColumn() {

		int[] columnsWidth = { FileTableColumnDtl.idWidth,
				FileTableColumnDtl.traceIdWidth, FileTableColumnDtl.nameWidth,
				FileTableColumnDtl.fileWidth, FileTableColumnDtl.appIdWidth,
				FileTableColumnDtl.statusWidth, FileTableColumnDtl.classWidth,
				FileTableColumnDtl.riskWidth, FileTableColumnDtl.locationWidth,
				FileTableColumnDtl.impactWidth, FileTableColumnDtl.codeWidth,
				FileTableColumnDtl.startLineWidth,
				FileTableColumnDtl.endLineWidth,
				FileTableColumnDtl.lineNoWidth,
				FileTableColumnDtl.colStartWidth,
				FileTableColumnDtl.colEndWidth, FileTableColumnDtl.hashWidth,
				FileTableColumnDtl.descriptionWidth,
				FileTableColumnDtl.solutionWidth,
				FileTableColumnDtl.complianceWidth,
				FileTableColumnDtl.formattedCodeWidth };

		for (int colIndex = 0; colIndex < colName.length; colIndex++) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);
			column.setText(colName[colIndex]);
			column.setWidth(columnsWidth[colIndex]);

		}

	}

	/**
	 * Fill data to Table
	 * 
	 * @param fileList
	 */
	public void setFileDetailList(final Issue fileList) {
		tree.removeAll();
		fileNamePath = new LinkedHashMap<String, String>();
		if (fileList != null) {

			try {

				Thread fileDetailThread = new Thread() {

					public void run() {

						int index = 0;

						for (Object collection : fileList.getCollection()) {
							final Map collectionMap = Collections
									.synchronizedMap((LinkedHashMap) collection);
							synchronized (collectionMap) {
								if (collectionMap != null) {

									index++;
									Display.getDefault().asyncExec(
											new Runnable() {
												public void run() {

													createTreeItem(collectionMap);

												}
											});

									if (index % 3 == 0) {
										try {

											Thread.sleep(2000);
										} catch (InterruptedException e) {
											logger.error(e);
										}
									}

								}
							}
						}

					}

				};
				fileDetailThread.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void checkPathForMultipleFile() {
		Thread fileDetailsMapThread = new Thread() {

			public void run() {

				// FileExistenanceRecursive fileChecking = new
				// FileExistenanceRecursive();
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				// Get all projects in the workspace
				IProject[] projects = root.getProjects();
				// Loop over all projects
				logger.info("Map value " + fileNamePath);
				for (IProject project : projects) {
					FileExistenanceRecursive.checkingDirectoryForMultipleFile(
							project.getLocation().toString(), 0, fileNamePath);
				}
				logger.info("Map value " + fileNamePath);
				fileNamePathUpdated = true;
			}
		};
		fileDetailsMapThread.start();
		// fileDetailsMapThread.join();
	}

	/**
	 * This method create TreeItem for table
	 * 
	 * @param fileList
	 * @param startIndex
	 * @param endIndex
	 * @param totalFileSize
	 */
	private synchronized void createTreeItem(Map collectionMap) {

		// for (int index = startIndex; index < totalFileSize && index <
		// endIndex; index++) {
		// logger.info("-----------start createTreeItem");
		TreeItem fileDtlRow = new TreeItem(tree, SWT.NONE);

		// for (Object collection : fileList.getCollection()) {
		// LinkedHashMap collectionMap = (LinkedHashMap) collection;
		Device device = Display.getCurrent();
		fileDtlRow.setBackground(device
				.getSystemColor(SWT.COLOR_TITLE_FOREGROUND));
		fileDtlRow.setForeground(device.getSystemColor(SWT.COLOR_WHITE));

		fileDtlRow.setText(FileTableColumnDtl.idIndex,
				StringUtility.checkIfNullThenEmpty(collectionMap.get("id")));
		fileDtlRow.setText(FileTableColumnDtl.appIdIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("application_id")));
		fileDtlRow.setText(FileTableColumnDtl.classIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("class_readable")));
		fileDtlRow.setText(FileTableColumnDtl.riskIndex,
				StringUtility.checkIfNullThenEmpty(collectionMap.get("risk")));
		fileDtlRow
				.setText(FileTableColumnDtl.impactIndex, StringUtility
						.checkIfNullThenEmpty(collectionMap.get("impact")));
		fileDtlRow.setText(FileTableColumnDtl.descriptionIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("description")));
		fileDtlRow.setText(FileTableColumnDtl.solutionIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("solution")));
		fileDtlRow.setText(FileTableColumnDtl.complianceIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("compliance")));
		fileDtlRow
				.setText(FileTableColumnDtl.statusIndex, StringUtility
						.checkIfNullThenEmpty(collectionMap.get("status")));

		LinkedHashMap traces = (LinkedHashMap) collectionMap.get("traces");
		List objCollTrace = (List) traces.get("collection");
		for (Object stepObject : objCollTrace) {
			LinkedHashMap traceMap = (LinkedHashMap) stepObject;

			TreeItem traceDtlRow = new TreeItem(fileDtlRow, SWT.NONE);
			traceDtlRow.setText(FileTableColumnDtl.traceIdIndex,
					StringUtility.checkIfNullThenEmpty(traceMap.get("id")));
			LinkedHashMap stepMap = (LinkedHashMap) traceMap.get("steps");
			List stepList = (List) stepMap.get("collection");
			traceDtlRow.setBackground(device
					.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
			for (Object stepColllection : stepList) {
				LinkedHashMap step = (LinkedHashMap) stepColllection;
				TreeItem stepDtlRow = new TreeItem(traceDtlRow, SWT.NONE);
				stepDtlRow.setText(FileTableColumnDtl.nameIndex,
						StringUtility.checkIfNullThenEmpty(step.get("kind")));
				stepDtlRow.setText(FileTableColumnDtl.fileIndex, StringUtility
						.checkIfNullThenEmpty(step.get("filename")));

				// String rowSelectedFileName = selectTreeItem.getText(
				// FileTableColumnDtl.fileIndex).substring(
				// selectTreeItem.getText(FileTableColumnDtl.fileIndex)
				// .lastIndexOf(File.separator) + 1);
				String file = StringUtility.checkIfNullThenEmpty(step
						.get("filename"));

				fileNamePath.put(
						file.substring(file.lastIndexOf(File.separator) + 1),
						"");
				// logger.info("++++++++++++++++fileNamePath " + fileNamePath);
				stepDtlRow.setText(FileTableColumnDtl.locationIndex,
						StringUtility.checkIfNullThenEmpty(step
								.get("relative_line_number")));
				stepDtlRow.setText(FileTableColumnDtl.codeIndex, Base64Util
						.decode(StringUtility.checkIfNullThenEmpty(step
								.get("formatted_code"))));

				stepDtlRow.setText(FileTableColumnDtl.formattedCodeIndex,
						Base64Util.decode(StringUtility
								.checkIfNullThenEmpty(step.get("code"))));
				stepDtlRow.setText(FileTableColumnDtl.startLineIndex,
						StringUtility.checkIfNullThenEmpty(step
								.get("start_line_number")));

			}
			// System.out.println(stepObject);
		}

		// \ }
	}

	private void createGroupFields(final Composite parent) {

		Group group = new Group(parent, SWT.None);

		openBtn = new Button(group, SWT.NONE);
		openBtn.setText("Open WIS File");
		openBtn.setLocation(0, 2);

		filenameLbl = new Label(group, SWT.LEFT | SWT.NONE);
		filenameLbl
				.setText("                                                                                       ");
		filenameLbl.setLocation(110, 2);

		Canvas canvas = new Canvas(group, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Image image = new Image(e.display,
						this.getClass()
								.getClassLoader()
								.getResourceAsStream(
										"presenter/views/whslogoalpha.png"));
				e.gc.drawImage(image, -5, 0);
				// e.gc.drawImage(image, 0, 0, 100, 100, 200, 10, 200, 50);
			}
		});
		GridData grid = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		grid.widthHint = 180;
		canvas.setLayoutData(grid);

		Button button = new Button(group, SWT.PUSH);
		button.setText("External Src Folder");

		createFolderTxt = new Text(group, SWT.LEFT | SWT.BORDER);
		createFolderTxt.setLocation(135, 10);

		Display display = Display.getDefault();
		final Shell shell = new Shell(display);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(shell);

				// Set the initial filter path according
				// to anything they've selected or typed in
				dlg.setFilterPath(createFolderTxt.getText());

				// Change the title bar text
				dlg.setText("DirectoryDialog");

				// Customizable message displayed in the dialog
				dlg.setMessage("Select a directory");

				// Calling open() will open and run the dialog.
				// It will return the selected directory, or
				// null if user cancels
				String dir = dlg.open();
				if (dir != null) {
					// Set the text box to the new selection
					createFolderTxt.setText(dir);
					createFolderTxt.setToolTipText(dir);
				}
			}
		});

		createFolderTxt.setLayoutData(new GridData(SWT.FILL, SWT.None, true,
				false, 2, 1));
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		group.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1);
		gridData.heightHint = 66;

		group.setLayoutData(gridData);

	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {

		// new Thread() {
		//
		// public void run() {
		// Display.getDefault().asyncExec(new Runnable() {
		// public void run() {
		try {
			fileNamePathUpdated = false;
			final Issue fileList = JsonUtil.convertJsonToObject(new File(
					filenameLbl.getText()));
			if (fileList != null) {
				// logger.info("size===== " + fileList.size());

				setFileDetailList(fileList);
				// System.out.println("ending======");
			}
		} catch (Exception e) {
			logger.error(e);
		}

		// }
		// });
		// }
		// }.start();

	}

	/**
	 * This contain all listner
	 */
	public void createListner() {

		tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {

				if (itemSelection) {

					if (!selectTreeItem.getText(FileTableColumnDtl.fileIndex)
							.isEmpty()) {
						fileNamePath.put(AppConstant.folderTextFieldName,
								createFolderTxt.getText());
						FileOperation.checkFileExist(selectTreeItem,
								fileNamePathUpdated, fileNamePath);
					}
				}

				itemSelection = false;

			}
		});

		tree.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectTreeItem = (TreeItem) e.item;
				itemSelection = true;
				if (tabItemList.size() > 0) {
					updateTabSection();
				}
			}
		});

		tree.addListener(SWT.Expand, new Listener() {
			public void handleEvent(Event e) {

				parentItem = (TreeItem) e.item;
				String desc = parentItem
						.getText(FileTableColumnDtl.descriptionIndex);
				String sol = parentItem
						.getText(FileTableColumnDtl.solutionIndex);
				if (desc != null && !desc.isEmpty()) {
					description = desc;
				}
				if (sol != null && !sol.isEmpty()) {
					solution = sol;
				}
				if (fileNamePathUpdated == false) {
					checkPathForMultipleFile();
				}
				// System.out.println("Expand={" + e.item + "}");
			}
		});

		// Button allow to open File Dialog for JSON file
		openBtn.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				logger.info("before thread");
				// openExternalFile();
				Shell shell = new Shell();
				shell.setLocation(300, 250);
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				// dialog.setFilterNames(new String[] { "All Files (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.*" }); // Windows

				String firstFile = dialog.open();

				if (firstFile != null) {
					logger.info(" path " + dialog.getFilterPath() + " path2 "
							+ dialog.getFileName());
					filenameLbl.setText(dialog.getFilterPath() + File.separator
							+ dialog.getFileName());
					filenameLbl.setToolTipText(filenameLbl.getText());
					// PropertyFileUtil.getProp().setProperty("jsonFile",
					// filenameLbl.getText());
					HashMap<String, String> msgMap = new HashMap<String, String>();
					msgMap.put("jsonFile", filenameLbl.getText());
					PropertyFileUtil.updatePropertyFile(msgMap);
					createTable(parent);

				}

			}
		});

	}

	public void updateTabSection() {
		Browser browse = tabItemList.get(0);

		/*
		 * 
		 * browse.setText("<pre>" +
		 * selectTreeItem.getText(FileTableColumnDtl.codeIndex).replace( "\n",
		 * "<br/>") + "<pre/>");
		 */
		if (parentItem.getText(FileTableColumnDtl.codeIndex) != null) {
			browse.setText(selectTreeItem.getText(FileTableColumnDtl.codeIndex));
			// Prabhu:
			// I modified the codeIndex field to have the formattedCode info,
			// you
			// will have to modify/add the field as a separate item

			String ss0 = new String();
			ss0 = "<script>" + "var aa=\""
					+ selectTreeItem.getText(FileTableColumnDtl.codeIndex)
					+ "\";"
					// + "alert(aa);"
					+ "document.write(aa);" + "</script>";
		}
		Browser browseDesc = tabItemList.get(1);

		// browseDesc.setText("<pre>"
		// + selectTreeItem.getText(FileTableColumnDtl.descriptionIndex)
		// .replace("\n", "<br/>") + "<pre/>");

		if (description != null) {
			String ss = new String();
			ss = "<script>" + "var aa=\"" + description + "\";"
			// + "alert(aa);"
					+ "document.write(aa);" + "</script>";

			// browseDesc.setText(selectTreeItem.getText(FileTableColumnDtl.descriptionIndex));

			browseDesc.setText(ss);
		}
		// Prabhu: the description and solution has tags that are in unicode,
		// and either, we need the JSON pasrse to decode them
		// or I used a shortcut to use Javascript to decode and display them on
		// the page

		Browser browseSolution = tabItemList.get(2);
		// browseSolution.setText("<pre>"
		// + selectTreeItem.getText(FileTableColumnDtl.solutionIndex)
		// .replace("\n", "<br/>") + "<pre/>");

		if (solution != null) {
			browseSolution.setText(solution);
			// logger.info(JSONUtils.convertToJavaIdentifier(parentItem
			// .getText(FileTableColumnDtl.solutionIndex)));
			String ss1 = new String();
			ss1 = "<script>" + "var aa=\"" + solution + "\";"
			// + "alert(aa);"
					+ "document.write(aa);" + "</script>";
			browseSolution.setText(ss1);
		}

	}

	public void openPreviousJsonFile() {
		logger.info(" invoke openPreviousJsonFile() ");
		String jsonFile = PropertyFileUtil.getProp().getProperty("jsonFile");

		if ((!jsonFile.isEmpty()) && jsonFile != null) {
			logger.info("Loading previous json file " + jsonFile);

			filenameLbl.setText(jsonFile);
			createTable(parent);

		}
	}

	public String getVersionAndDate() {
		return PropertyFileUtil.getProp().getProperty("version");
	}
}
