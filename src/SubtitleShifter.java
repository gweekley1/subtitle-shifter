import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubtitleShifter {

	private static SimpleDateFormat srtTimeFormat = new SimpleDateFormat("HH:mm:ss,SSS");
	
	public static void main(String[] args) {
		
		// open file
		String subtitles = null;
		try {
			byte[] fileBytes = Files.readAllBytes(Paths.get(args[0]));
			subtitles = new String (fileBytes, StandardCharsets.ISO_8859_1);
		} catch (IOException e) {
			System.out.println("Failed to find " + args[0]);
		}
		
		// walk through
		Pattern timePattern = Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}");
		Matcher timeMatch = timePattern.matcher(subtitles);
		
		while (timeMatch.find()) {
			String timestamp = timeMatch.group();
			try {
				Date time = srtTimeFormat.parse(timestamp);
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				c.add(Calendar.MILLISECOND, Integer.valueOf(args[1]));
				subtitles = subtitles.replace(timestamp, srtTimeFormat.format(c.getTime()));
				System.out.println("Changed " + timestamp + " to " + srtTimeFormat.format(c.getTime()));
				
			} catch (ParseException e) {
				System.out.println("Failed to parse " + timestamp);
			}
		}
		try {
			Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("new_" + args[0]), StandardCharsets.ISO_8859_1));
			fileWriter.write(subtitles);
			fileWriter.close();
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		}
	}

}
