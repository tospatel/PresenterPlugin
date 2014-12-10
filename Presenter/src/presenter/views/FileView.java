package presenter.views;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
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

		showMainWindow(parent);
		splashWindow(Display.getDefault(), parent);

	}

	public void showMainWindow(Composite parent) {
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

	public void splashWindow(final Display display, final Composite parent) {

		// final Display display = new Display();

		final int[] count = new int[] { 2 };
		// final Image image = new Image(display, 200, 300);
		final Image image = new Image(display, this
				.getClass()
				.getClassLoader()
				.getResourceAsStream(
						"presenter/views/logo/whitehat_security_logo.jpg"));
		image.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		final Shell splash = new Shell(SWT.None);
		splash.setMinimumSize(150, 150);
		// Label titleLabel = new Label(splash, SWT.NONE);

		// titleLabel
		// .setText("This Custom Plugin is created by WhiteHat Security");
		// titleLabel.setSize(150, 20);
		// titleLabel.setLayoutData(new GridData(SWT.FILL, SWT.None, true,
		// false));
		FontData[] fD = label.getFont().getFontData();
		fD[0].setHeight(18);
		// titleLabel.setFont(new Font(display, fD[0]));
		// splash.setBackgroundMode(SWT.INHERIT_FORCE);
		// titleLabel.setSize(70, 150);
		// titleLabel.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		// titleLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		// String title = "";
		// if (OSValidatorUtil.isUnix()) {
		// title +=
		// "\n On Ubuntu once need to execute following command using Terminal\n  sudo apt-get install libwebkitgtk-1.0-0";
		// Label commandLabel = new Label(splash, SWT.NONE);
		//
		// commandLabel.setText(title);
		// commandLabel.setSize(70, 20);
		// commandLabel.setFont(new Font(display, fD[0]));
		// commandLabel.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
		//
		// commandLabel.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		// }

		// Device device = Display.getCurrent();

		final ProgressBar bar = new ProgressBar(splash, SWT.NONE);
		bar.setMaximum(count[0]);
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		label.setSize(200, 200);

		// label.setText("Custom Plugin by WhiteHat");
		FormLayout layout = new FormLayout();
		splash.setLayout(layout);
		FormData labelData = new FormData();
		labelData.right = new FormAttachment(100, 0);
		labelData.bottom = new FormAttachment(100, 0);
		label.setLayoutData(labelData);
		FormData progressData = new FormData();
		progressData.left = new FormAttachment(0, 5);
		progressData.right = new FormAttachment(100, -5);
		progressData.bottom = new FormAttachment(100, -5);
		bar.setLayoutData(progressData);
		splash.pack();
		Rectangle splashRect = splash.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);
		splash.open();
		display.asyncExec(new Runnable() {
			public void run() {
				System.out.println("executed");
				// Shell[] shells = new Shell[count[0]];
				for (int i = 0; i < count[0]; i++) {
					// shells[i] = new Shell(display);
					// shells[i].setSize(300, 300);
					// shells[i].addListener(SWT.Close, new Listener() {
					// public void handleEvent(Event e) {
					// --count[0];
					// }
					// });
					bar.setSelection(i + 1);
					try {
						Thread.sleep(1000);
					} catch (Throwable e) {
					}
				}
				splash.close();
				image.dispose();
				// for (int i = 0; i < count[0]; i++) {
				// shells[i].open();
				// }

			}
		});
		// while (count[0] != 0) {
		// if (!display.readAndDispatch())
		// display.sleep();
		// }
		// display.dispose();
	}
}