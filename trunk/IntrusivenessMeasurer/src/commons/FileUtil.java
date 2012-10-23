package commons;

import static commons.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	public static void checkFileExist(String fileName) throws FileNotFoundException {
		checkNotNull(fileName, "fileName must not be null.");
		if (!new File(fileName).exists()) {
			throw new FileNotFoundException(fileName + " was not found.");
		}
	}
	
	public static String getNextLineOfData(RandomAccessFile file) throws IOException {
		checkNotNull(file, "file must not be null.");
		String line = file.readLine();

		if (line == null || line.equals("")) {
			throw new IOException("Could not find necessary data.");
		}
		return line;
	}
	
	public static void jumpLines(RandomAccessFile file, int numberOfLines) throws IOException {
		for (int i = 0; i < numberOfLines; i++) {
			if (file.readLine() == null) {
				throw new IOException("Could not jump lines from the file.");
			}
		}
	}
	
	public static void readUntilFindBlankLine(RandomAccessFile file) throws IOException {
		String line = file.readLine();
		while (line != null && !line.equals("")) {
			line = file.readLine();
		}
	}
}
