package presenter.views;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import presenter.util.DailyRollingLogFiles;
import presenter.util.PropertyFileUtil;
import presenter.util.customproject.CustomProjectSupport;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class FileView extends ViewPart implements IViewData {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "presenter.views.FileView";
	final static Logger logger = Logger.getLogger(FileView.class);
	private String fileName;
	private Composite parent;
	private Label label;
	private TreeTable treeTable;
	// public FileView() {
	// }
	/**
	 * Main method from where plugin start
	 */

	public void createPartControl(Composite parent) {

		this.parent = parent;
		label = new Label(parent, SWT.NONE); // label is a field
		// label.setVisible(false);
		DailyRollingLogFiles.createLogFile();
		logger.info("Invoke main method - createPartControl() for plugin ");
		CustomProjectSupport.deleteTempProject();
		new PropertyFileUtil().loadPropertyFile();

		// get object which represents the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String workSpacePath = workspace.getRoot().getLocation().toFile()
				.getPath().toString();
		treeTable = new TreeTable(parent, PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage(), workSpacePath,
				workspace);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		// viewer.getControl().setFocus();

	}

	@Override
	public void setMessage(final String message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				// treeTable = new TreeTable();
				// treeTable.getFilenameLbl().setText(message);
				treeTable.fillTable(message);
				// label.setText(message);
				// label.pack();
				// treeTable.pack(true);

			}
		});
	}

}