package caceresenzo.libs.boxplay.common.extractor;

/**
 * Simple string logger used by extractor to log activity and result
 * 
 * 
 * @author Enzo CACERES
 */
public class ExtractionLogger {
	
	public static final String NEW_LINE_CHAR = "\n";
	public static final String SEPARATOR = "*** *** *** *** ***";
	
	private final String name;
	private String content;
	
	/**
	 * Constructor, create a new instance
	 * 
	 * @param extractorName
	 *            Specify the extractor name
	 */
	public ExtractionLogger(String extractorName) {
		this.name = extractorName;
		this.content = "";
		
		appendln("EXTRACTOR: " + name).separator().appendln("Extraction log: ").separator();
	}
	
	/**
	 * Print a separator ({@value #SEPARATOR})
	 * 
	 * @return Itself
	 */
	public ExtractionLogger separator() {
		return appendln().appendln(SEPARATOR).appendln();
	}
	
	/**
	 * Append an empty line
	 * 
	 * @return Itself
	 */
	public ExtractionLogger appendln() {
		return appendln("");
	}
	
	/**
	 * Append a message to the logger, and add a new line char ({@value #NEW_LINE_CHAR})
	 * 
	 * @param message
	 *            Message content
	 * @return Itself
	 */
	public ExtractionLogger appendln(Object message) {
		return append(String.valueOf(message).concat(NEW_LINE_CHAR));
	}
	
	/**
	 * Append a message to the logger
	 * 
	 * @param message
	 *            Message content
	 * @return Itself
	 */
	public ExtractionLogger append(Object message) {
		content += String.valueOf(message);
		
		return this;
	}
	
	/**
	 * Get the content of the logger
	 * 
	 * @return Content
	 */
	public String getContent() {
		return content;
	}
	
}