package imdb;

import java.util.regex.Pattern;

public class ImdbRecordParser {
	private static Pattern splitter = Pattern.compile("\\|");
	private String title = null;
	private int year = 0;
	private boolean isValid = false;

	public void parse(String recordLine) {
		try {
			String[] splits = splitter.split(recordLine);
			
			if (validate(splits)) {
				this.title = splits[2].trim();
				// Some year entries have slashes like '1937/I'
				this.year = Integer.parseInt(splits[3].trim().split("/")[0]);
			}
		} catch (NumberFormatException e) {
			this.isValid = false;
		}
	}
	
	public boolean isValidRecord(){
		return this.isValid;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getYear() {
		return this.year;
	}
	
	private boolean validate(String[] splits) {
		this.isValid = (splits.length == 7 
				&& splits[2].length() > 1 		// Title shouldn't be empty
				&& splits[3].length() > 1 		// Year shouldn't be empty
				&& !splits[3].contains("?")); 	// Some invalid entries have ???? as year
		return this.isValid;
	}
}
