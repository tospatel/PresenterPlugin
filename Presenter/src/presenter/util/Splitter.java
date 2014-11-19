package presenter.util;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;

public class Splitter {
	private Splitter() {
	}

	public static void horizontalSplitter() {

	}

	public static void verticalSplitter(Composite composite,
			List<Object> splitterObjectList) {
	}

	public static Sash getSash(Composite composite, int style) {
		composite.setLayout(new FormLayout());
		final Sash sash = new Sash(composite, style);
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0); // Attach to top
		data.bottom = new FormAttachment(100, 0); // Attach to bottom
		data.left = new FormAttachment(59, 0); // Attach halfway across
		sash.setLayoutData(data);
		Device device = Display.getCurrent();
		sash.setBackground(device.getSystemColor(SWT.COLOR_DARK_GRAY));
		// final int SASH_LIMIT = 20;
		sash.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				((FormData) sash.getLayoutData()).left = new FormAttachment(0,
						event.x);

				sash.getParent().layout();
			}
		});
		return sash;
	}

	public static FormData getLeftFormData(Sash sash) {
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(sash, 0);

		return data;
	}

	public static FormData getRightFormData(Sash sash) {
		FormData data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(100, 0);
		data.left = new FormAttachment(sash, 0);
		data.right = new FormAttachment(100, 0);

		return data;
	}
}
