package hr.csvcompv2.batcher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Batcher {

	/**
	 * Batcher will create batches and add the docIds as the values under that
	 * batchID
	 */

	final static Logger logger = Logger.getLogger(Batcher.class);

	SetMultimap<String, String> batches = HashMultimap.create();

	SetMultimap<String, String> duplicates = HashMultimap.create();

	private String batchID, docID;
	private String[] recordAsArray;
	final private String comma = ",";

	public void addRecord(String record, int colCount) {

		recordAsArray = record.split(",");

		switch (colCount) {
		case 1:
			batchID = recordAsArray[0].trim();
			docID = recordAsArray[0].trim();

			addRecord(batchID, docID);
			break;
		case 2:
			batchID = recordAsArray[0].trim();
			docID = recordAsArray[1].trim();

			addRecord(batchID, docID);
			break;
			
		case 3:
			batchID = recordAsArray[0].trim();
			
			if(recordAsArray.length<3){
				docID = recordAsArray[1].trim();
			}else{
				docID = recordAsArray[1].trim() + comma + recordAsArray[2].trim();
			}
			
			addRecord(batchID, docID);
			break;
		case 4:
			batchID = recordAsArray[0].trim();

			/**
			 * following try catch block will convert the date format used in
			 * the Alfresco side file to the date format used in the CMOD side
			 */
			try {
				String originalDateStr = recordAsArray[1];
				DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				DateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy");
				Date originalDate = originalFormat.parse(originalDateStr);
				String formattedDateStr = targetFormat.format(originalDate);
				recordAsArray[1] = formattedDateStr;
			} catch (ParseException e) {
				logger.fatal("Date Conversion Failed", e);
//				System.exit(0);
			}

			docID = recordAsArray[1].trim() + comma + recordAsArray[2].trim();

			addRecord(batchID, docID);
			break;
		}

	}

	public void addRecord(String batchID, String docID) {

		if (batches.containsKey(batchID)) {
			if (batches.get(batchID).contains(docID)) {
				duplicates.put(batchID, docID);
			}else{
				batches.put(batchID, docID);
			}
		} else {
			batches.put(batchID, docID);
		}

	}

	public SetMultimap<String, String> getBatches() {
		return batches;
	}

	public Set<String> getDocs(String BatchID) {
		return batches.get(BatchID);
	}

	public List<String> getDuplicates() {
		logger.debug("duplicates Found : " + duplicates.size());
		return convertToList(duplicates);
	}

	private List<String> convertToList(SetMultimap<String, String> setMultimap) {
		List<String> convertedList = new ArrayList<>();

		for (Entry<String, Collection<String>> e : setMultimap.asMap().entrySet()) {
			for (String value : e.getValue()) {
				convertedList.add((e.getKey() + comma + value));
			}
		}

		return convertedList;
	}
}
