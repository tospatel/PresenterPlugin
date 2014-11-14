package presenter.util;

import java.io.File;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import presenter.shared.AppConstant;
import presenter.util.customproject.CustomProjectSupport;

public class DailyRollingLogFiles {
	public static void createLogFile() {
		Logger logger = null;
		try {

			// creates pattern layout
			PatternLayout layout = new PatternLayout();
			String conversionPattern = "[%p] %d %c %M - %m%n";
			layout.setConversionPattern(conversionPattern);

			IProject project = CustomProjectSupport.createProject(
					ResourcesPlugin.getWorkspace().getRoot().getLocationURI(),
					AppConstant.logFolder);
			DeleteDirAndFilesUtil.deleteFilesInFolder(project.getLocation()
					+ File.separator + AppConstant.logFolder);
			// creates daily rolling file appender
			DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
			// rollingAppender.setFile(project.getFullPath() + "app.log");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			String workSpacePath = workspace.getRoot().getLocation().toFile()
					.getPath().toString();

			rollingAppender.setFile(workSpacePath + project.getFullPath()
					+ File.separator + AppConstant.logFolder + File.separator
					+ AppConstant.logFileName);
			rollingAppender.setDatePattern("'.'yyyy-MM-dd");
			rollingAppender.setLayout(layout);
			rollingAppender.activateOptions();

			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			// configures the root logger
			Logger rootLogger = Logger.getRootLogger();
			rootLogger.setLevel(Level.DEBUG);
			rootLogger.addAppender(rollingAppender);

			// creates a custom logger and log messages
			logger = Logger.getLogger(DailyRollingLogFiles.class);

		} catch (Exception e) {
			// logger.error(e);
		}
	}
}
