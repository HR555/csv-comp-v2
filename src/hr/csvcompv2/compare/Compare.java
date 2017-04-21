package hr.csvcompv2.compare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import hr.csvcompv2.batcher.Batcher;
import hr.csvcompv2.exception.NoRecordsFoundException;
import hr.csvcompv2.parser.Parser;

public class Compare {

	Parser parser;
	final static Logger logger = Logger.getLogger(Compare.class);
	private List<String> missing, cmodDuplicates, alfDuplicates;
	final String comma = ",";
	int alfrescoRecordCount, cmodRecordCount;
	public Compare() {

		parser = new Parser();
	}

	public void customerData(File custDataCmod, File custDataAlf) throws NoRecordsFoundException {
		missing = new ArrayList<>();
		cmodDuplicates = new ArrayList<>();
		alfDuplicates = new ArrayList<>();

		alfrescoRecordCount = parser.parse(custDataAlf, 2);
		// compare

		if(alfrescoRecordCount>0){
			compareBatchwithFile(parser.getBatch(), custDataCmod, 2);
		}else{
			logger.error("No data on Alfresco side");
			throw new NoRecordsFoundException("No Records Found in the alfresco side file!");
		}

		logger.debug("batches count : " + parser.getBatch().getBatches().size());
		
		alfDuplicates = parser.getBatch().getDuplicates();
		
		cmodRecordCount = parser.parse(custDataCmod, 2);
		cmodDuplicates = parser.getBatch().getDuplicates();
		
	}

	private void compareBatchwithFile(Batcher batch, File file, int colCount) {
		BufferedReader reader;
		String[] lineArray;
		String batchID,docID;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				line = line.trim();

				lineArray = line.split(comma);

				batchID = lineArray[0].trim();
				
				if(colCount<3){
					docID = lineArray[1].trim();
				}else{
					docID = lineArray[1].trim()+ comma +lineArray[2].trim();
				}
				boolean foundBatch = false;

				Set<String> docs = new HashSet<>();
				for (String each : batch.getDocs(batchID)){
					docs.add(each);
				}

				if (docs != null) {
					foundBatch = true;
					boolean foundDoc = false;
					for (String Doc : docs) {
						if (docID.equals(Doc)) {
							foundDoc = true;
						}

					}
					if (!foundDoc && !missing.contains(line)) {
						missing.add(line);
					}

				}
				if (!foundBatch && !missing.contains(line)) {
					missing.add(line);
				}

			}

		} catch (IOException e) {
			logger.fatal(e);
		}

	}

	public void reconData(File imRecCmod, File imRecAlf) {
		customerData(imRecCmod, imRecAlf);
	}

	public void reportData(File imRepCmod, File imRepAlf) {
		missing = new ArrayList<>();
		cmodDuplicates = new ArrayList<>();
		alfDuplicates = new ArrayList<>();

		alfrescoRecordCount = parser.parse(imRepAlf, 4);
		
		if(alfrescoRecordCount>0){
			compareBatchwithFile(parser.getBatch(), imRepCmod, 3);
		}else{
			logger.error("No data on Alfresco side");
			throw new NoRecordsFoundException("No Records Found in the alfresco side file!");
		}

		alfDuplicates = parser.getBatch().getDuplicates();
		
		logger.debug("batches count : " + parser.getBatch().getBatches().size());
		
		cmodRecordCount = parser.parse(imRepCmod, 3);
		
		cmodDuplicates = parser.getBatch().getDuplicates();
		

	}
	
	public void userData(File userCmod, File userAlf) {

		parser.parse(userCmod, 1);
		List<String> cmodUserList = parser.getList();

		parser.parse(userAlf, 1);
		List<String> alfUserList = parser.getList();

		CompareList(cmodUserList, alfUserList);

	}

	private void CompareList(List<String> list1, List<String> list2) {
		missing = new ArrayList<>();
		cmodDuplicates = new ArrayList<>();
		alfDuplicates = new ArrayList<>();

		for (String eachUser : list1) {
			if (!list2.contains(eachUser)) {
				if(!missing.contains(eachUser)){
					missing.add(eachUser);
				}
			}
		}

		Set<String> cmodUniques = new HashSet<String>();
		Set<String> temp = new HashSet<>();
		for (String user : list1) {
			if (!cmodUniques.add(user)) {
				if(temp.add(user)){
					cmodDuplicates.add(user);
				}
			}
		}
		
		temp.clear();

		Set<String> alfUniques = new HashSet<String>();

		for (String user : list2) {
			if (!alfUniques.add(user)) {
				if(temp.add(user)){
					alfDuplicates.add(user);
				}
			}
		}
		temp.clear();
		temp=null;
	}

	public List<String> getMissing() {
		logger.info("Missing Count : \t\t" + missing.size());
		return missing;
	}

	public List<String> getAlfDuplicates() {
		logger.info("Alfresco Duplicates Count : \t" + alfDuplicates.size());
		return alfDuplicates;
	}

	public List<String> getCmodDuplicates() {
		logger.info("CMOD Duplicates Count : \t" + cmodDuplicates.size());
		return cmodDuplicates;
	}
	
	public int getRecordCountDiferrence(){
		return cmodRecordCount - alfrescoRecordCount;
	}
	
}
