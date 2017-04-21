package hr.csvcompv2.api;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

public class Loader {

	final static Logger logger = Logger.getLogger(Loader.class);
	private Path path;

	Loader(Path path) {
		this.path = path;
		load();
	}

	public static File custDataCmod = null, IMRecCmod = null, IMRepCmod = null, userCmod = null, custDataAlf = null,
			IMRecAlf = null, IMRepAlf = null, userAlf = null;

	public static File getCustDataCmod() {
		return custDataCmod;
	}

	public static File getIMRecCmod() {
		return IMRecCmod;
	}

	public static File getIMRepCmod() {
		return IMRepCmod;
	}

	public static File getUserCmod() {
		return userCmod;
	}

	public static File getCustDataAlf() {
		return custDataAlf;
	}

	public static File getIMRecAlf() {
		return IMRecAlf;
	}

	public static File getIMRepAlf() {
		return IMRepAlf;
	}

	public static File getUserAlf() {
		return userAlf;
	}

	private void load() {

		/**
		 * this method will load all the required files from the given location
		 * to the above declared File variables
		 */
		Path cmodPath = Paths.get(path + "\\cmodOut");
		Path alfPath = Paths.get(path + "\\alfOut");

		logger.debug("cmodPath : " + cmodPath);
		logger.debug("alfPath : " + alfPath);

		try {
			File[] cmodFiles = cmodPath.toFile().listFiles();
			for (File eachFile : cmodFiles) {
				if (eachFile.getName().contains("CustomerDataReport_DRCi"))
					custDataCmod = eachFile;
				else if (eachFile.getName().contains("IMReconciliationDataReport_DRCi"))
					IMRecCmod = eachFile;
				else if (eachFile.getName().contains("IMReportData_DRCi"))
					IMRepCmod = eachFile;
				else if (eachFile.getName().contains("UserReport_DRCi"))
					userCmod = eachFile;
			}

			File[] alfFiles = alfPath.toFile().listFiles();
			for (File eachFile : alfFiles) {
				if (eachFile.getName().contains("CustomerDataReport_Alfresco"))
					custDataAlf = eachFile;
				else if (eachFile.getName().contains("IMReconciliationDataReport_Alfresco"))
					IMRecAlf = eachFile;
				else if (eachFile.getName().contains("IMReportData_Alfresco"))
					IMRepAlf = eachFile;
				else if (eachFile.getName().contains("UserReport_Alfresco"))
					userAlf = eachFile;
			}
		} catch (Exception e) {
			logger.fatal("No Files Found", e);
		}
	}

}
