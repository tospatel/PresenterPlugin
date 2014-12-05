package presenter.views;

import java.io.File;
import java.util.ArrayList;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Sash;
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
import presenter.util.OSValidatorUtil;
import presenter.util.PropertyFileUtil;
import presenter.util.SortTreeListener;
import presenter.util.Splitter;
import presenter.util.StringUtility;

public class TreeTable {

	private Label filenameLbl;
	private Button openBtn;
	private Tree tree;
	// private IWorkbenchPage wbPage;
	// private String dirPath;
	// private TreeItem parentItem;
	private String description;
	private String solution;
	private String traceId;
	// private Label createFolderLbl;
	private Text createFolderTxt;
	private Map<String, Map<String, String>> vulnDetailsMap;
	private Map<String, String> vulnMap;
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
			FileTableColumnDtl.compliance, FileTableColumnDtl.formattedCode,
			FileTableColumnDtl.stepId };
	private TreeItem selectTreeItem;
	private boolean itemSelection = false;
	private int totalVulnSize = 0, itemFillInTable = 0;
	private Composite parent;
	private Map<String, String> fileNamePath = null;
	private Map<String, String> previousFileNamePath = null;
	private Boolean fileNamePathUpdated = false;
	private ArrayList<Object> tabItemList = new ArrayList<Object>();
	final static Logger logger = Logger.getLogger(TreeTable.class);
	private TabFolder tabFolder = null;

	public TreeTable() {

	}

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
		// this.wbPage = wbPage;
		// this.dirPath = workSpacePath;
		// this.workspace = workspace;
		vulnDetailsMap = null;
		vulnDetailsMap = new HashMap<String, Map<String, String>>();
		vulnMap = null;
		this.addChildControls(parent);
	}

	/**
	 * This method add button & table
	 * 
	 * @param composite
	 */
	private void addChildControls(Composite composite) {
		logger.info(" Calling addChildControls ");
		List<Object> splitterObjectList = new ArrayList<Object>();

		Sash sash = null;
		Composite childComp;

		if (OSValidatorUtil.isUnix()) {
			composite.setLayout(new GridLayout(2, false));
			childComp = new Composite(composite, SWT.NONE);
			childComp
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			childComp.setLayout(new GridLayout(2, true));
		} else {
			sash = Splitter.getSash(composite, SWT.VERTICAL);
			childComp = composite;
		}

		createCompositePanel(childComp, sash, splitterObjectList);

		if (OSValidatorUtil.isUnix()) {
			setSnippetForUnix(childComp, sash, splitterObjectList);
		} else {
			setSnippet(childComp, sash, splitterObjectList);
		}

		// Splitter.verticalSplitter(composite, splitterObjectList);
		setTableColumn();
		createListner();
		// fillTable(fileNm);
		// openPreviousJsonFile();
	}

	/**
	 * Set table layout
	 * 
	 * @param parent
	 */
	public void setTableLayout(Composite parent) {

		int style = SWT.MULTI | SWT.FULL_SELECTION;
		tree = new Tree(parent, style);

		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
	}

	public void setSnippetForUnix(Composite parent, Sash sash,
			List<Object> splitterObjectList) {

		Group rightGroup = new Group(parent, SWT.FILL);
		// Group group = new Group(parent, SWT.NONE);
		tabFolder = new TabFolder(rightGroup, SWT.FILL);

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

			Text sText = new Text(tabFolder, SWT.None);
			sText.setText("");
			tabItem.setControl(sText);
			tabItemList.add(sText);

		}

		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		// Create the second text box and attach its left edge
		// to the sash
		// Text two = new Text(composite, SWT.BORDER);

		Label version = new Label(rightGroup, SWT.BOTTOM | SWT.RIGHT
				| SWT.BORDER);
		version.setText(getVersionAndDate());
		version.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false, 1,
				1));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		rightGroup.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		// gridData.heightHint = 66;
		// gridData.widthHint = 500;
		rightGroup.setLayoutData(gridData);

	}

	public void setSnippet(Composite parent, Sash sash,
			List<Object> splitterObjectList) {

		Composite rightComposite = new Composite(parent, SWT.BORDER
				| SWT.CENTER | SWT.SHADOW_NONE);

		// Group group = new Group(parent, SWT.NONE);
		tabFolder = new TabFolder(rightComposite, SWT.FILL);
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

		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1));
		// Create the second text box and attach its left edge
		// to the sash
		// Text two = new Text(composite, SWT.BORDER);

		Label version = new Label(rightComposite, SWT.BOTTOM | SWT.RIGHT
				| SWT.BORDER);
		version.setText(getVersionAndDate());
		version.setLayoutData(new GridData(SWT.FILL, SWT.None, true, false, 1,
				1));

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		rightComposite.setLayout(gridLayout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		// gridData.heightHint = 66;
		// gridData.widthHint = 500;
		rightComposite.setLayoutData(gridData);

		rightComposite.setLayoutData(Splitter.getRightFormData(sash));
		// splitterObjectList.add(rightComposite);

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
				FileTableColumnDtl.formattedCodeWidth,
				FileTableColumnDtl.stepWidth };

		for (int colIndex = 0; colIndex < colName.length; colIndex++) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);

			column.setText(colName[colIndex]);
			column.setWidth(columnsWidth[colIndex]);
			if (columnsWidth[colIndex] != 0 && colIndex != 2 && colIndex != 3) {
				column.addSelectionListener(new SortTreeListener());
			}
		}
		// tree.setRedraw(true);
		// tree.setHeaderVisible(false);
	}

	/**
	 * Fill data to Table
	 * 
	 * @param fileList
	 */
	public void setFileDetailList(final Issue fileList) {
		tree.removeAll();
		if (fileNamePath != null) {
			previousFileNamePath = fileNamePath;
		}
		fileNamePath = new HashMap<String, String>();
		if (fileList != null) {

			try {

				Thread fileDetailThread = new Thread() {

					public void run() {

						int index = 0;
						itemFillInTable = 0;
						totalVulnSize = fileList.getCollection().size();

						for (Object collection : fileList.getCollection()) {
							final Map collectionMap = (LinkedHashMap) collection;
							if (collectionMap != null) {

								index++;
								Display.getDefault().asyncExec(new Runnable() {
									public void run() {

										createTreeItem(collectionMap);

									}
								});

								if (index % 4 == 0) {
									try {

										Thread.sleep(2000);
									} catch (InterruptedException e) {
										logger.error(e);
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

	private void setPreviousMapPath() {
		if (previousFileNamePath != null) {
			for (Map.Entry<String, String> entry : previousFileNamePath
					.entrySet()) {
				if (!entry.getValue().isEmpty()) {

					// if (fileNamePath.containsKey(entry.getKey())) {
					fileNamePath.put(entry.getKey(), entry.getValue());
					// System.out.println("Key : " + entry.getKey()
					// + " Value : " + entry.getValue());
					// } else {
					//
					// }
				}
			}
			previousFileNamePath = null;
		}
	}

	/**
	 * This method create TreeItem for table
	 * 
	 * @param fileList
	 * @param startIndex
	 * @param endIndex
	 * @param totalFileSize
	 */
	private void createTreeItem(Map collectionMap) {

		TreeItem fileDtlRow = new TreeItem(tree, SWT.NONE);
		Map<String, String> descSolMap = new HashMap<String, String>();

		Device device = Display.getCurrent();
		fileDtlRow.setBackground(device.getSystemColor(SWT.COLOR_BLACK));
		fileDtlRow.setForeground(device.getSystemColor(SWT.COLOR_GRAY));
		String vulnId = StringUtility.checkIfNullThenEmpty(collectionMap
				.get("id"));

		descSolMap.put(FileTableColumnDtl.description, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("description")));
		descSolMap.put(FileTableColumnDtl.solution, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("solution")));
		vulnDetailsMap.put(vulnId, descSolMap);
		fileDtlRow.setText(FileTableColumnDtl.descriptionIndex, vulnId);
		fileDtlRow.setText(FileTableColumnDtl.idIndex, vulnId);
		fileDtlRow.setText(FileTableColumnDtl.appIdIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("application_id")));
		fileDtlRow.setText(FileTableColumnDtl.classIndex, StringUtility
				.checkIfNullThenEmpty(collectionMap.get("class_readable")));
		fileDtlRow.setText(FileTableColumnDtl.riskIndex,
				StringUtility.checkIfNullThenEmpty(collectionMap.get("risk")));
		fileDtlRow
				.setText(FileTableColumnDtl.impactIndex, StringUtility
						.checkIfNullThenEmpty(collectionMap.get("impact")));
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
			String trace = StringUtility.checkIfNullThenEmpty(traceMap
					.get("id"));
			traceDtlRow.setText(FileTableColumnDtl.traceIdIndex,
					StringUtility.checkIfNullThenEmpty(trace));

			traceDtlRow.setText(FileTableColumnDtl.descriptionIndex, vulnId);

			LinkedHashMap stepMap = (LinkedHashMap) traceMap.get("steps");
			List stepList = (List) stepMap.get("collection");
			traceDtlRow.setBackground(device
					.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
			for (Object stepColllection : stepList) {
				LinkedHashMap step = (LinkedHashMap) stepColllection;
				TreeItem stepDtlRow = new TreeItem(traceDtlRow, SWT.NONE);
				Map<String, String> stepDetailsMap = new HashMap<String, String>();

				stepDtlRow.setText(FileTableColumnDtl.nameIndex,
						StringUtility.checkIfNullThenEmpty(step.get("kind")));
				stepDtlRow.setText(FileTableColumnDtl.fileIndex, StringUtility
						.checkIfNullThenEmpty(step.get("filename")));

				stepDtlRow.setText(FileTableColumnDtl.descriptionIndex, vulnId);
				stepDtlRow.setText(FileTableColumnDtl.temptraceIdIndex, trace);
				String file = StringUtility.checkIfNullThenEmpty(step
						.get("filename"));
				String stepId = StringUtility.checkIfNullThenEmpty(step
						.get("id"));

				fileNamePath.put(
						file.substring(file.lastIndexOf((OSValidatorUtil
								.isWindows() ? AppConstant.windowsSlash
								: File.separator)) + 1), "");
				stepDtlRow.setText(FileTableColumnDtl.locationIndex,
						StringUtility.checkIfNullThenEmpty(step
								.get("relative_line_number")));

				stepDetailsMap.put(FileTableColumnDtl.code, Base64Util
						.decode(StringUtility.checkIfNullThenEmpty(step
								.get("formatted_code"))));

				stepDetailsMap.put(FileTableColumnDtl.formattedCode, Base64Util
						.decode(StringUtility.checkIfNullThenEmpty(step
								.get("code"))));

				vulnDetailsMap.put(trace + "/" + stepId, stepDetailsMap);
				stepDtlRow.setText(FileTableColumnDtl.stepIndex,
						StringUtility.checkIfNullThenEmpty(step.get("id")));

				stepDtlRow.setText(FileTableColumnDtl.startLineIndex,
						StringUtility.checkIfNullThenEmpty(step
								.get("start_line_number")));

			}
			// System.out.println(stepObject);
		}
		itemFillInTable++;

		// \ }
	}

	private void createCompositePanel(final Composite parent, Sash sash,
			List<Object> splitterObjectList) {

		Composite composite = new Composite(parent, SWT.None);

		// final SashForm topSash = new SashForm(composite, SWT.HORIZONTAL);
		// topSash.setLayout(new FillLayout());
		// topSash.setLayoutData(new GridData(GridData.FILL_BOTH));
		// final SashForm bottom = new SashForm(composite, SWT.HORIZONTAL);
		// topSash.setWeights(new int[] { 1, 2 });
		// final SashForm sashForm = new SashForm(composite, SWT.VERTICAL);
		// Sash topSash = Splitter.getSash(composite, SWT.HORIZONTAL);
		Composite upComposite = new Composite(composite, SWT.BORDER);
		Device device = Display.getCurrent();
		// upComposite.setBackground(device.getSystemColor(SWT.COLOR_GRAY));
		upComposite.setBackground(new Color(device, new RGB(240, 240, 240)));
		Composite downComposite = new Composite(composite, SWT.None);
		// Group group = new Group(parent, SWT.None);
		openBtn = new Button(upComposite, SWT.NONE);
		openBtn.setText("Open WIS File");
		// openBtn.setLocation(0, 2);
		GridData btnGridData = new GridData(SWT.None, SWT.None, false, false,
				1, 1);
		btnGridData.verticalIndent = -4;
		openBtn.setLayoutData(btnGridData);

		filenameLbl = new Label(upComposite, SWT.LEFT | SWT.NONE);
		// filenameLbl
		// .setText("           `                                                                            ");
		// filenameLbl.setLocation(110, 10);
		GridData lblGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1,
				1);
		// lblGridData.exclude = false;
		// lblGridData.heightHint = 30;
		// lblGridData.verticalIndent = 2;
		filenameLbl.setLayoutData(lblGridData);
		// filenameLbl.pack();
		Canvas canvas = new Canvas(upComposite, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Image image = new Image(e.display,
						this.getClass()
								.getClassLoader()
								.getResourceAsStream(
										"presenter/views/whslogoalpha.png"));
				e.gc.drawImage(image, -5, 0);
			}
		});

		GridData grid = new GridData(SWT.FILL, SWT.None, false, false, 1, 1);
		grid.widthHint = 180;
		grid.heightHint = 25;
		canvas.setLayoutData(grid);

		Button button = new Button(upComposite, SWT.PUSH);
		button.setText("External Src Folder");

		GridData btnExtGridData = new GridData(SWT.None, SWT.None, false,
				false, 1, 1);
		btnExtGridData.verticalIndent = -2;
		button.setLayoutData(btnExtGridData);

		createFolderTxt = new Text(upComposite, SWT.LEFT | SWT.BORDER);
		// createFolderTxt.setLocation(135, 10);

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
		// gridLayout.marginHeight = 0;
		// gridLayout.marginWidth = 2;
		upComposite.setLayout(gridLayout);
		// GridData gridData = new GridData(SWT.FILL, SWT.None, true, false, 1,
		// 1);
		// upComposite.setLayoutData(gridData);

		setTableLayout(downComposite);

		GridLayout downGridLayout = new GridLayout();
		downGridLayout.numColumns = 1;
		// downGridLayout.marginHeight = 5;
		// downGridLayout.marginWidth = 0;
		downComposite.setLayout(downGridLayout);
		// GridData downGridData = new GridData(SWT.FILL, SWT.FILL, true, true,
		// 1,
		// 1);
		GridData upCompositeGrid = new GridData(SWT.FILL, SWT.None, true, false);
		upCompositeGrid.heightHint = 57;
		upComposite.setLayoutData(upCompositeGrid);
		// upComposite.setSize(400, 80);
		// GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.grabExcessVerticalSpace = true;
		// composite.setLayoutData(gridData);
		downComposite
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// downComposite.setLayoutData(downGridData);

		GridLayout grid1Layout = new GridLayout();
		grid1Layout.numColumns = 1;
		// grid1Layout.verticalSpacing=true;
		composite.setLayout(grid1Layout);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gridData.grabExcessVerticalSpace = true;
		composite.setLayoutData(gridData);
		if (!OSValidatorUtil.isUnix()) {
			composite.setLayoutData(Splitter.getLeftFormData(sash));
		}
		// Add the Restore Weights functionality

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

					logger.info("====== TotalVulnSize " + totalVulnSize
							+ "===ItemFillInTable===> " + itemFillInTable);
					if (fileNamePathUpdated == false
							&& itemFillInTable == totalVulnSize) {
						setPreviousMapPath();
						checkPathForMultipleFile();
					}

					if (!selectTreeItem.getText(FileTableColumnDtl.fileIndex)
							.isEmpty()) {
						fileNamePath.put(AppConstant.folderTextFieldName,
								createFolderTxt.getText());
						// vulnDetailsMap

						// fileNamePath.put(AppConstant.codeDetails,
						FileOperation.checkFileExist(selectTreeItem,
								fileNamePathUpdated, fileNamePath, vulnMap);
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
					System.out.println("" + selectTreeItem);
					if (selectTreeItem != null) {

						String idIndex = selectTreeItem
								.getText(FileTableColumnDtl.descriptionIndex);

						if (idIndex != null && !idIndex.isEmpty()) {
							Map<String, String> map = vulnDetailsMap
									.get(idIndex);
							description = map
									.get(FileTableColumnDtl.description);
							solution = map.get(FileTableColumnDtl.solution);
						}

						String trace = selectTreeItem
								.getText(FileTableColumnDtl.temptraceIdIndex);
						trace = ((trace != null && !trace.isEmpty()) ? trace
								: "");
						if (!trace.isEmpty()) {
							idIndex = trace
									+ "/"
									+ StringUtility
											.checkIfNullThenEmpty(selectTreeItem
													.getText(FileTableColumnDtl.stepIndex));
							vulnMap = vulnDetailsMap.get(idIndex);
						} else {
							vulnMap = null;
						}
						updateTabSection();
					}

					// System.out.println("Expand={" + e.item + "}");

				}
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

					fillTable(dialog.getFilterPath() + File.separator
							+ dialog.getFileName());

				}

			}
		});

		// tree.addListener(SWT.Expand, new Listener() {
		// public void handleEvent(Event e) {
		// // parentItem = (TreeItem) e.item;
		// // previousFileNamePath = fileNamePath;
		// // more elegant way
		// if (previousFileNamePath != null) {
		// for (Map.Entry<String, String> entry : previousFileNamePath
		// .entrySet()) {
		// if ((!entry.getValue().isEmpty())
		// && fileNamePath.containsKey(entry.getKey())) {
		// fileNamePath.put(entry.getKey(), entry.getValue());
		// System.out.println("Key : " + entry.getKey()
		// + " Value : " + entry.getValue());
		// }
		//
		// }
		// }
		// }
		// });

	}

	public void updateTabSection() {
		if (OSValidatorUtil.isUnix()) {
			((Text) tabItemList.get(0)).setText(vulnMap.get(
					FileTableColumnDtl.code).toString());
			((Text) tabItemList.get(1)).setText(description);
			((Text) tabItemList.get(2)).setText(solution);
		} else {

			Browser browse = (Browser) tabItemList.get(0);

			if (vulnMap != null && vulnMap.get(FileTableColumnDtl.code) != null) {
				browse.setText(vulnMap.get(FileTableColumnDtl.code).toString());
			} else {
				browse.setText("");
			}
			Browser browseDesc = (Browser) tabItemList.get(1);

			if (description != null) {
				String ss = new String();
				ss = "<script>" + "var aa=\"" + description + "\";"
				// + "alert(aa);"
						+ "document.write(aa);" + "</script>";

				browseDesc.setText(ss);
			}
			// Prabhu: the description and solution has tags that are in
			// unicode,
			// and either, we need the JSON pasrse to decode them
			// or I used a shortcut to use Javascript to decode and display them
			// on
			// the page

			Browser browseSolution = (Browser) tabItemList.get(2);
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
	}

	// public void openPreviousJsonFile() {
	// logger.info(" invoke openPreviousJsonFile() ");
	// String jsonFile = PropertyFileUtil.getProp().getProperty("jsonFile");
	//
	// if ((!jsonFile.isEmpty()) && jsonFile != null) {
	// logger.info("Loading previous json file " + jsonFile);
	//
	// filenameLbl.setText(jsonFile);
	// createTable(parent);
	//
	// }
	// }
	public void fillTable(String fileName) {
		if (fileName != null && !fileName.isEmpty()) {
			filenameLbl.setText(fileName);
			filenameLbl.setToolTipText(filenameLbl.getText());
			// PropertyFileUtil.getProp().setProperty("jsonFile",
			// filenameLbl.getText());
			HashMap<String, String> msgMap = new HashMap<String, String>();
			msgMap.put("jsonFile", filenameLbl.getText());
			PropertyFileUtil.updatePropertyFile(msgMap);
			createTable(parent);

		}
	}

	public String getVersionAndDate() {
		return PropertyFileUtil.getProp().getProperty("version");
	}

	public Label getFilenameLbl() {
		return filenameLbl;
	}

	public void setFilenameLbl(Label filenameLbl) {
		this.filenameLbl = filenameLbl;
	}

}
