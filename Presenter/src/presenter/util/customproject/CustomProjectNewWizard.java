package presenter.util.customproject;

import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

//import customplugin.projects.CustomProjectSupport;

public class CustomProjectNewWizard extends Wizard implements INewWizard,
		IExecutableExtension {

	private IConfigurationElement _configurationElement;
	private static final String PAGE_NAME = "Custom Plug-in Project Wizard";
	private static final String WIZARD_NAME = "New Custom Plug-in Project";
	private WizardNewProjectCreationPage _pageOne;
	private URI location = null;
	private String filePath;

	final static Logger logger = Logger.getLogger(CustomProjectNewWizard.class);
	private Map<String, String> fileNamePath;
	private TreeItem selectTreeItem;

	public CustomProjectNewWizard(String filePath,
			Map<String, String> fileNamePath, TreeItem selectTreeItem) {
		this.filePath = filePath;
		// TODO Auto-generated constructor stub
		// setWindowTitle(WIZARD_NAME);
		this.fileNamePath = fileNamePath;
		this.selectTreeItem = selectTreeItem;

		location = ResourcesPlugin.getWorkspace().getRoot().getLocationURI();
		// addPages();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addPages() {
		super.addPages();
		// _pageOne = new WizardNewProjectCreationPage(PAGE_NAME);
		//		_pageOne.setTitle("From Scratch Project"); //$NON-NLS-1$
		//		_pageOne.setDescription("Create something from scratch."); //$NON-NLS-1$
		// addPage(_pageOne);
	}

	@Override
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		_configurationElement = config;
	}

	@Override
	public boolean performFinish() {

		logger.info("Invoke performFinish() === location: "
				+ location.toString());
		IProject project = CustomProjectSupport.createProject(location,
				fileNamePath);
		// Add this
		BasicNewProjectResourceWizard.updatePerspective(_configurationElement);

		String destFilePath = CustomProjectSupport.addFileToProject(project,
				filePath, fileNamePath, selectTreeItem);

		// CustomProject.createProject();
		return true;
	}
}