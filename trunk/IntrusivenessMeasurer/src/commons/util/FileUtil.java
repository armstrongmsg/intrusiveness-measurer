package commons.util;

import static commons.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

// FIXME test this class !!!!
public class FileUtil {
	
	public static void checkFileExist(String fileName) throws FileNotFoundException {
		checkNotNull(fileName, "fileName must not be null.");
		if (!new File(fileName).exists()) {
			throw new FileNotFoundException(fileName + " was not found.");
		}
	}
	
	public static void checkFileIsReadable(String fileName) throws IOException {
		checkNotNull(fileName, "fileName must not be null.");
		if (!new File(fileName).canRead()) {
			throw new IOException(fileName + " could not be read.");
		}
	}
	
	public static void checkFileIsWritable(String fileName) throws IOException {
		checkNotNull(fileName, "fileName must not be null.");
		if (!new File(fileName).canWrite()) {
			throw new IOException(fileName + " could not be modified.");
		}
	}
	
	public static void checkFileIsExecutable(String fileName) throws IOException {
		checkNotNull(fileName, "fileName must not be null.");
		if (!new File(fileName).canExecute()) {
			throw new IOException(fileName + " could not be executed.");
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
	
	public static String toPath(String first, String ... nodes) {
		String separator = File.separator;
		StringBuilder builder = new StringBuilder();
		builder.append(first);
		for (String node : nodes) {
			builder.append(separator);
			builder.append(node);
		}
		return builder.toString();
	}
}
