package hr.csvcompv2.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import hr.csvcompv2.batcher.Batcher;
import hr.csvcompv2.exception.NoRecordsFoundException;

public class Parser {

	final static Logger logger = Logger.getLogger(Parser.class);

	Batcher batcher;
	List<String> list;

	public int parse(File file, int colCount) {
		batcher = new Batcher();
		list = new ArrayList<>();
		String line, record;
		int recordCount=0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));

			if (colCount > 1) {
				line = null;

				reader.readLine();
				while ((line = reader.readLine()) != null) {
					record = line.trim();
					if (!record.equals("")){
						batcher.addRecord(record, colCount);
						recordCount++;
					}
				}
				
			} else {
				/**
				 * for user reports the batcher is not used and the list is used
				 * to store all the email addresses
				 */
				line = null;

				reader.readLine();
				while ((line = reader.readLine()) != null) {
					if (!line.equals("")){
						recordCount++;
						record = line.trim().split("@")[0].toLowerCase().replace(",", "");
						list.add(record);
					}
				}
			}
			reader.close();

			if (recordCount == 0) {
				throw new NoRecordsFoundException();
				
			}
			
		} catch (IOException e) {
			logger.fatal("File Not Found", e);
		}
		return recordCount;
	}

	public Batcher getBatch() {
		return batcher;
	}

	public List<String> getList() {
		logger.debug("List size : " + list.size());
		return list;
	}

}
