package foo;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This program shifts the timestamps in a subtitle file of the format `srt` by an arbitrary number of milliseconds
 * 
 * @author Grant Weekley
 */
public class SubtitleShifter {
	
	public static final boolean DEBUG = false;
	
	/*
	 * args[0] The path to the srt file being modified
	 * args[1] The amount of milliseconds by which to shift the subtitles
	 * args[2] The optional output file name
	 */
	public static void main(String[] args) {
		
		// extract the subtitles from the file and save them as a string
		String subtitles = null;
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(args[0]));
			subtitles = new String (fileBytes, StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			System.out.println("Failed to find " + args[0]);
			System.exit(1);
		}
		
		// walk through the string to finds substrings matching `##:##:##,###`, where # is a number
		Pattern timePattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}");
		Matcher timeMatch = timePattern.matcher(subtitles);
		while (timeMatch.find()) {
			
			String oldTimestamp = timeMatch.group();
			int index = timeMatch.start();
			Timestamp newTimestamp = new Timestamp(oldTimestamp);
			// calculate the new timestamp and replace the value in the subtitles string
			newTimestamp.shift(Integer.valueOf(args[1]));
			subtitles = subtitles.substring(0, index) + newTimestamp + subtitles.substring(index + newTimestamp.toString().length());
			
			if (DEBUG)
				System.out.println("Changed " + oldTimestamp + " to " + newTimestamp.toString());
		}
		
		// write new subtitles to a new file, the optional arg or `new_$oldfileName.srt`
		String fileName;
		if (args.length >= 3) {
			fileName = args[2];
		} else {
			fileName = "new_" + args[0];			
		}
		
		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.ISO_8859_1));
			try {
				fileWriter.write(subtitles);
			} catch (IOException e) {
				System.out.println("Failed to write to file: " + fileName);
			} finally {
				fileWriter.close();
			}
		} catch (IOException e) {
			System.out.println("Failed to open file: " + fileName);
		}
	}
}
