package presenter.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;

public class MessageBoxView {
	private MessageBoxView() {

	}

	/**
	 * Message Box
	 * 
	 * @param msg
	 */
	public static void show(String msg) {
		showMsgAtStatusBar(msg);
		Shell shell = new Shell();
		// shell.setSize(400, 300);
		int style = SWT.ERROR_IO;

		// SWT.ICON_QUESTION | SWT.YES | SWT.NO

		msgBox(new MessageBox(shell, style), msg);

	}

	/**
	 * 
	 * Message Box
	 * 
	 * @param msg
	 * @param styleFlag
	 */
	public static void showMsgAtStatusBar(String msg) {
		IViewSite site = (IViewSite) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite();
		site.getActionBars().getStatusLineManager().setMessage(msg);
	}

	/**
	 * Message Box
	 * 
	 * @param messageBox
	 * @param msg
	 */
	private static void msgBox(MessageBox messageBox, String msg) {

		messageBox.setText("Message");
		messageBox.setMessage(msg);
		// messageBox.set
		int rc = messageBox.open();

		switch (rc) {
		case SWT.OK:
			System.out.println("SWT.OK");
			break;
		case SWT.CANCEL:
			System.out.println("SWT.CANCEL");
			break;
		case SWT.YES:
			System.out.println("SWT.YES");
			break;
		case SWT.NO:
			System.out.println("SWT.NO");
			break;
		case SWT.RETRY:
			System.out.println("SWT.RETRY");
			break;
		case SWT.ABORT:
			System.out.println("SWT.ABORT");
			break;
		case SWT.IGNORE:
			System.out.println("SWT.IGNORE");
			break;
		}
	}
}
