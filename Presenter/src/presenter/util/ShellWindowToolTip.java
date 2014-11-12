package presenter.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ShellWindowToolTip {

	private ShellWindowToolTip(Shell parent, String msg) {
	}

	/**
	 * Shell window showing tooltip
	 * 
	 * @param msg
	 */
	public static void show(String msg) {

		final Display display = Display.getDefault();
		Shell shell = new Shell(display);

		Label labelWrap = new Label(shell, SWT.WRAP | SWT.BORDER);
		labelWrap.setText(msg);
		labelWrap.setBounds(10, 10, 500, 340);
		shell.setSize(500, 350);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

}
