package presenter.util.customproject;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import presenter.shared.AppConstant;
import presenter.util.DeleteDirAndFilesUtil;
import presenter.util.FileOperation;

public class CustomProjectSupport {
	final static Logger logger = Logger.getLogger(CustomProjectSupport.class);
	private static String folderPath = "src";
	private static String projectName = AppConstant.tempProjectName;
	private final static IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	/**
	 * For this marvelous project we need to: - create the default Eclipse
	 * project - add the custom project nature - create the folder structure
	 *
	 * @param projectName
	 * @param location
	 * @param natureId
	 * @return
	 */
	public static IProject createProject(URI location,
			Map<String, String> fileNamePath) {
		// Assert.isNotNull(projectName);
		// Assert.isTrue(projectName.trim().length() > 0);

		IProject project = createBaseProject(location);

		// Uncomment
		try {
			addNature(project);

			String[] paths = { folderPath };
			//"parent/child1-1/child2", "parent/child1-2/child2/child3" }; //$NON-NLS-1$ //$NON-NLS-2$
			addToProjectStructure(project, paths);
		} catch (CoreException e) {
			e.printStackTrace();
			project = null;
		}

		return project;
	}

	/**
	 * Just do the basics: create a basic project.
	 *
	 * @param location
	 * @param projectName
	 */
	private static IProject createBaseProject(URI location) {
		logger.info("Calling createBaseProject");
		// it is acceptable to use the ResourcesPlugin class
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);

		if (!newProject.exists()) {
			URI projectLocation = location;
			IProjectDescription desc = newProject.getWorkspace()
					.newProjectDescription(newProject.getName());
			if (location != null
					&& ResourcesPlugin.getWorkspace().getRoot()
							.getLocationURI().equals(location)) {
				projectLocation = null;
			}

			desc.setLocationURI(projectLocation);
			try {
				newProject.create(desc, null);

				if (!newProject.isOpen()) {
					newProject.open(null);
				}
			} catch (CoreException e) {
				logger.error(e);
			}
		}

		return newProject;
	}

	public static void deleteTempProject() {
		IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);

		if (newProject.exists()) {

			try {

				DeleteDirAndFilesUtil.deleteFilesInFolder(newProject
						.getLocation() + File.separator + folderPath);

				// String[] paths = { folderPath };
				newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
				IFolder folder = newProject.getFolder(folderPath);
				folder.create(false, true, null);
			} catch (Exception e) {
				logger.error(e);
			}
		}

	}

	public static String addFileToProject(IProject project, String filePath,
			Map<String, String> fileNamePath, TreeItem selectTreeItem) {
		logger.info("Calling addFileToProject()");
		// IPath path = new Path(filePath);
		// IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		String destFilePath = null;

		try {
			// file.cr
			String fileName = filePath.substring(filePath
					.lastIndexOf(File.separator) + 1);
			destFilePath = projectName + File.separator + folderPath
					+ File.separator + fileName;

			IPath location = new Path(filePath);
			IFile file1 = project.getFile(File.separator + folderPath
					+ File.separator + location.lastSegment());
			file1.createLink(location, IResource.NONE, null);
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
			// loadFile(destFile);
			FileOperation.loadFileByMarkingLineOrMethod(destFilePath,
					selectTreeItem, fileNamePath, null);
			fileNamePath.put(fileName, destFilePath);
			// file.create(file.getContents(), true, null);
		} catch (Exception e) {
			if (e.getMessage().endsWith("already exists.")) {
				FileOperation.loadFileByMarkingLineOrMethod(destFilePath,
						selectTreeItem, fileNamePath, null);
			} else
				logger.error(e);
		}
		return destFilePath;
	}

	public static void loadFile(File file) {
		try {

			IFile iFile = convertFileToIFile(file);
			IMarker marker = iFile.createMarker(IMarker.PROBLEM);
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put(IMarker.LINE_NUMBER, 1);
			map.put(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			map.put(IMarker.MESSAGE, "SAST Vulnerability  Warning");
			map.put(IMarker.TRANSIENT, true);

			marker.setAttributes(map);
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage(), marker);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	public static IFile convertFileToIFile(File file) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath location = Path.fromOSString(file.getAbsolutePath());
		return workspace.getRoot().getFileForLocation(location);
	}

	private static void createFolder(IFolder folder) throws CoreException {
		IContainer parent = folder.getParent();
		if (parent instanceof IFolder) {
			createFolder((IFolder) parent);
		}
		if (!folder.exists()) {
			folder.create(false, true, null);
		}
	}

	/**
	 * Create a folder structure with a parent root, overlay, and a few child
	 * folders.
	 *
	 * @param newProject
	 * @param paths
	 * @throws CoreException
	 */
	private static void addToProjectStructure(IProject newProject,
			String[] paths) throws CoreException {
		for (String path : paths) {
			IFolder etcFolders = newProject.getFolder(path);
			createFolder(etcFolders);
		}
	}

	private static void addNature(IProject project) throws CoreException {
		if (!project.hasNature(ProjectNature.NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures = description.getNatureIds();
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = ProjectNature.NATURE_ID;
			description.setNatureIds(newNatures);

			IProgressMonitor monitor = null;
			// uncomment
			// project.setDescription(description, monitor);
		}
	}

}