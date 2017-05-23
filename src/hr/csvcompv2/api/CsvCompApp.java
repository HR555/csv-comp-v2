package hr.csvcompv2.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import hr.csvcompv2.compare.Compare;
import hr.csvcompv2.exception.NoRecordsFoundException;
import hr.csvcompv2.reporter.Reporter;

/**
 * 
 * @author Hasith Ranasinghe
 *
 */

public class CsvCompApp {
/**
 * Using this we can compare 2 CSV files for missing and duplicates in each file
 * @param args
 */
	
	
	static final Logger logger = Logger.getLogger(CsvCompApp.class);
	static String customerName = "custName";
	static Path path=null, output=null;
	static Reporter reporter;
	static int outputFolderIndex = 0; // used in the method createOutputFolder() to keep the folder index
	
	public static void main(String[] args) {
		logger.info("\n			  -----  CSV Verification Tool version 2.0  -----\n"
				+ "					---(Final Edition)---\n");
		
		setPath(Paths.get("."));
	
		logger.debug("Selected Path \t\t: " + getPath().toString());
		
		try {
			setCustomerName(path);
			compare();
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	private static void compare() throws IOException{
		
		logger.info("Processing Verification Reports for the Customer : " + customerName);
		
		reporter = new Reporter();
//		Loader loader = null;
		
		try{
			@SuppressWarnings("unused")
			Loader loader = new Loader(path);
		} catch (Exception e) {
			logger.fatal("No Files Found", e);
		}
		
		logger.debug("Parssing started for : " + customerName);
		
		Compare compare = new Compare();
		
		output = createOutputFolder(Paths.get(path+ "/" + customerName + "_reports"));
		
		////////////////////////////////
		if (Loader.getCustDataCmod() != null && Loader.getCustDataAlf() != null) {
			logger.info("-----------------------------\n  Customer Data\n-----------------------------");
			try {
				compare.customerData(Loader.getCustDataCmod(), Loader.getCustDataAlf());
				generateReports(compare.getMissing(), compare.getAlfDuplicates(), compare.getCmodDuplicates(), 1,
						compare);
			} catch (NoRecordsFoundException e) {
				logger.error(e);
			}

		} else {
			logger.info("Customer data files cannot be found!\n");
		}
		
		////////////////////////////////
		if (Loader.getIMRecCmod() != null && Loader.getIMRecAlf() != null ){
			logger.info("-----------------------------\n  IM Recon Data\n-----------------------------");
			try {
				compare.reconData(Loader.getIMRecCmod(), Loader.getIMRecAlf());
				generateReports(compare.getMissing(), compare.getAlfDuplicates(), compare.getCmodDuplicates(), 2,
						compare);
			} catch (NoRecordsFoundException e) {
				logger.error(e);
			}

		} else {
			logger.info("IM Reconciliation data files cannot be found!\n");
		}
		
		////////////////////////////////
		if (Loader.getIMRepCmod() != null && Loader.getIMRepAlf() != null) {
			logger.info("-----------------------------\n  IM Report Data\n-----------------------------");
			try {
				compare.reportData(Loader.getIMRepCmod(), Loader.getIMRepAlf());
				generateReports(compare.getMissing(), compare.getAlfDuplicates(), compare.getCmodDuplicates(), 3,
						compare);
			} catch (NoRecordsFoundException e) {
				logger.error(e);
			}

		} else {
			logger.info("IM Reports data files cannot be found!\n");
		}

		////////////////////////////////
		if (Loader.getUserCmod() != null && Loader.getUserAlf() != null) {
			logger.info("-----------------------------\n  User Data\n-----------------------------");
			try {
				compare.userData(Loader.getUserCmod(), Loader.getUserAlf());
				generateReports(compare.getMissing(), compare.getAlfDuplicates(), compare.getCmodDuplicates(), 4,
						compare);
			} catch (NoRecordsFoundException e) {
				logger.error(e);
			}

		} else {
			logger.info("User data files cannot be found!\n");
		}
		logger.info("\nProcessing Completed for Customer : " + customerName);

	}
	
	private static void setPath(Path pathParam) {
		
		path = pathParam;

	}
	
	private static Path getPath() {
		return path;

	}
	
	private static void setCustomerName(Path path) throws IOException{
		/**
		 * Trying to read the file name from the files in the CMODout folder
		 * example file name - CustomerDataReport_DRCi_LILLY_06-3-2017
		 * the customer name is between DRCI_ and _.
		 * below code uses Regex to get the customer name
		 */
		
		try {

			File filePath = new File(path.toString() + "/cmodOut");

			for (String eachFile : filePath.list()) {
				String fileName = eachFile;
				if (customerName.isEmpty() || "custName".equalsIgnoreCase(customerName)) {
					Pattern pattern = Pattern.compile("(?<=DRCi_).*?(?=_[0-9])");
					Matcher matcher = pattern.matcher(fileName);
					matcher.find();
					customerName = matcher.group(0);
					logger.debug("Set Customer Name \t: " + customerName);
				}
			}
			
		} catch (Exception e) {
			logger.fatal("File Not Found \t: No files could be located inside the CMODOut Folder", e);
			throw new IOException("File Not Found \t: No files could be located inside the CMODOut Folder : " + e);
		}
		
	}
	
	public static String getCustomerName() {
		return customerName;
	}
	
	public static Path createOutputFolder(Path output) throws IOException{
		Path outputNew=output;
		while(Files.exists(outputNew)){
			outputFolderIndex++;
			outputNew = Paths.get(output.toString()+"_"+outputFolderIndex);
		}
		
		logger.debug("Output Location selected : " + output);
		
		try {
			Files.createDirectory(outputNew);
		} catch (Exception e) {
			logger.fatal("Report generation failed!" , e);
			throw new IOException("Report generation failed! : " + e);
		}
		
		return outputNew;
		
	}
	
	/**
	 * 
	 * @param missing
	 * @param alfDups
	 * @param cmodDups
	 * @param type - 1 for Customer Data, 2 for IM Recon Data, 3 for IM Report Data and 4 for User Data
	 */
	public static void generateReports(List<String> missing, List<String> alfDups, List<String> cmodDups, int type, Compare compare){

		String typeOfFile = null , header = null;
		
		switch(type){
		case 1 :
			typeOfFile = "CustomerDataReport";
			header = "BATCH ID,DOC ID";
			break;
		case 2 :
			typeOfFile = "IMReconciliationDataReport";
			header = "BATCH ID,APP ID,IN CACHE";
			break;
		case 3 :
			typeOfFile = "IMReportsDataReport";
			header = "REPT ID,REPT DATE,DOC ID,INCACHE";
			break;
		case 4 :
			typeOfFile = "UserDataReport";
			header = "User ID";
			break;
		default:
			logger.error("File Type Error : " + type);
		}
		
		if (!missing.isEmpty()){
			reporter.createCSV(missing, customerName+"_"+ typeOfFile +"_Missing", output, header);
		}
		if (!alfDups.isEmpty()){
			reporter.createCSV(alfDups, customerName+"_"+ typeOfFile +"_Alfresco_Duplicates", output, header);
		}
		if (!cmodDups.isEmpty()){
			reporter.createCSV(cmodDups, customerName+"_"+ typeOfFile +"_CMOD_Duplicates", output, header);
		}
		
		if(missing.isEmpty() && alfDups.isEmpty()){
			logger.info("\n-------------------------------------\nRecord count difference " + compare.getRecordCountDiferrence());
			logger.info("-------------------------------------");
		}
	}
	
}
