package hr.csvcompv2.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;

import hr.csvcompv2.batcher.Batcher;

public class Parser {

	static final Logger logger = Logger.getLogger(Parser.class);

	Batcher batcher;
	List<String> list;
	int colCount;
	int recordCount;

	public int parse(File file, int colCount) {
		this.colCount = colCount;
		
		batcher = new Batcher();
		list = new ArrayList<>();
		
		recordCount=0;

		try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath())).skip(1)) {

			
			if (colCount > 1) {
				stream.forEachOrdered(line -> sendToBatcher(line));
			}
			
			else {
				list = stream
						.filter(line -> !Strings.isNullOrEmpty(line))
						.map(line -> line.trim().split("@")[0].toLowerCase().replace(",", ""))
						.collect(Collectors.toList()); 
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return recordCount;
		
	}

	private void sendToBatcher(String line) {
		String record = line.trim();
		if (!"".equals(record)){
			recordCount++;
			batcher.addRecord(record, colCount);
		}
	}
	
	public Batcher getBatch() {
		return batcher;
	}

	public List<String> getList() {
		logger.debug("List size : " + list.size());
		return list;
	}

}
