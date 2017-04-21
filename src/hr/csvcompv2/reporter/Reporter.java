package hr.csvcompv2.reporter;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Reporter {

	final static Logger logger = Logger.getLogger(Reporter.class);

	String custName;
	Path path;
	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	final static Charset ENCODING = StandardCharsets.UTF_8;

	/**
	 * Writes the CSV file containing the list to the given path
	 * 
	 * @throws Exception
	 */
	public void createCSV(List<String> list, String fileName, Path path, String header) {

		Date dateNow = new Date();
		String date = "_" + dateFormat.format(dateNow);

		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path + "\\XXX" + date + fileName + ".csv"),
				ENCODING)) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(header + System.getProperty("line.separator"));

			for (int j = 0; j < list.size(); j++) {
				stringBuilder.append(list.get(j) + System.getProperty("line.separator"));
			}

			writer.write(stringBuilder.toString());
			writer.newLine();

		} catch (Exception e) {
			logger.fatal("File Write Error ", e);
		}

	}
}
