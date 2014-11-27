package presenter.util;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;

import presenter.shared.FileTableColumnDtl;
import presenter.util.customproject.CustomProjectNewWizard;

/**
 * This class demonstrates how to create your own dialog classes. It allows
 * users to input a String
 */
public class CustomFileBrowseDialog extends Dialog {
	private String message;
	private String input;
	private String filename;
	private IWorkbenchPage wbPage;
	final static Logger logger = Logger.getLogger(CustomFileBrowseDialog.class);

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public CustomFileBrowseDialog(Shell parent, String title, String msg,
			IWorkbenchPage wbPage) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, title, msg,
				wbPage);
	}

	/**
	 * InputDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public CustomFileBrowseDialog(Shell parent, int style, String title,
			String msg, IWorkbenchPage wbPage) {
		// Let users override the default styles
		super(parent, style);
		setText(title);
		setMessage(msg);
		this.wbPage = wbPage;
	}

	/**
	 * Gets the message
	 * 
	 * @return String
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message
	 * 
	 * @param message
	 *            the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the input
	 * 
	 * @return String
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Sets the input
	 * 
	 * @param input
	 *            the new input
	 */
	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public String open(Map<String, String> fileNamePath, TreeItem selectTreeItem) {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell, fileNamePath, selectTreeItem);
		// shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// Return the entered value, or null
		return input;
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell,
			final Map<String, String> fileNamePath,
			final TreeItem selectTreeItem) {
		MessageBoxView.showMsgAtStatusBar(message);
		shell.setLayout(new GridLayout(2, true));
		shell.setSize(400, 100);
		shell.setLocation(400, 350);
		// Show the message
		final Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		label.setSize(350, 25);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label.setForeground(Display.getDefault().getSystemColor(
				SWT.COLOR_DARK_BLUE));
		label.setAlignment(SWT.CENTER);

		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(12);
		label.setFont(new Font(Display.getDefault(), fD[0]));
		// Display the input box
		final Button text = new Button(shell, SWT.NONE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		text.setLayoutData(data);
		text.setText("Browse File");

		text.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {

				String file = FileOperation.openFileDialog();

				if (file != null) {

					String fileName = file.substring(file
							.lastIndexOf(File.separator) + 1);
					String rowSelectedFileName = selectTreeItem.getText(
							FileTableColumnDtl.fileIndex).substring(
							selectTreeItem
									.getText(FileTableColumnDtl.fileIndex)
									.lastIndexOf(File.separator) + 1);
					if (rowSelectedFileName.equals(fileName)) {

						// uncomment
						// FileOperation.openFile(file, wbPage);

						logger.info("File Name ======= " + file);

						CustomProjectNewWizard customProject = new CustomProjectNewWizard(
								file, fileNamePath, selectTreeItem);
						// customProject.addPage();
						customProject.performFinish();
						// dir.setText();
						shell.dispose();
					} else {
						label.setText(PropertyFileUtil.getProp().getProperty(
								"fileNameSame"));
						// MessageBoxView.show(PropertyFileUtil.getProp()
						// .getProperty("fileNameSame"));
					}
				}

			}
		});

	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
