package caceresenzo.libs.boxplay.culture.searchngo.data;

import java.util.Iterator;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.content.ContentViewerType;

/**
 * Holder class to hold data for a common result
 * 
 * @author Enzo CACERES
 */
public class AdditionalResultData {
	
	public static final String DATA_SEPARATOR = ", ";
	
	private final AdditionalDataType type;
	private final Object data;
	
	/**
	 * Constructor, create a new instance with the object only, {@link AdditionalDataType} will be interpreted as {@link AdditionalDataType#NULL}
	 * 
	 * @param data
	 *            Your data
	 */
	public AdditionalResultData(Object data) {
		this(AdditionalDataType.NULL, data);
	}
	
	/**
	 * Constructor, create a new instance with a {@link AdditionalDataType} and an object
	 * 
	 * @param type
	 *            Your {@link AdditionalDataType}
	 * @param data
	 *            Your data, and if this is an instance of string, auto escaping will be applied
	 */
	public AdditionalResultData(AdditionalDataType type, Object data) {
		this.type = type;
		this.data = data instanceof String ? escapeHtmlChar(String.valueOf(data)) : data;
	}
	
	/**
	 * Get the {@link AdditionalDataType} of this result
	 * 
	 * @return The type
	 */
	public AdditionalDataType getType() {
		return type;
	}
	
	/**
	 * Get the object
	 * 
	 * @return The data
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * Automaticly convert this data result to a readable string
	 * 
	 * @return A readable string
	 */
	public String convert() {
		String converted = "";
		
		if (data instanceof List) {
			Iterator<?> iterator = ((List<?>) data).iterator();
			
			while (iterator.hasNext()) {
				Object next = iterator.next();
				String toString;
				
				if (next instanceof DisplayableString) {
					toString = ((DisplayableString) next).convertToDisplayableString();
				} else {
					toString = String.valueOf(next);
				}
				
				converted += toString + (iterator.hasNext() ? DATA_SEPARATOR : "");
			}
		} else if (data instanceof DisplayableString) {
			converted = ((DisplayableString) data).convertToDisplayableString();
		} else {
			converted = String.valueOf(data);
		}
		
		return converted;
	}
	
	/**
	 * Static function to escape most known (during developpement) html custom/encoded char
	 * 
	 * @param string
	 *            Source string
	 * @return Escaped string
	 */
	public static String escapeHtmlChar(String string) {
		return escapeDoubleSpace(string //
				.replace("&#039;", "'") //
				.replace("&#39;", "'") //
				.replace("&eacute;", "é") //
				.replace("&quot;", "\"") //
				.replace("&amp;amp;", "&") //
				.replace("&amp;", "&") //
				.replace("&nbsp;", " ") //
				.replace("&NBSP;", " ") //
				.replaceAll("<br[\\s]*(\\/)>", "") //
				.trim() //
		);
	}
	
	/**
	 * Static function to escape every double space till no remain
	 * 
	 * @param string
	 *            Source string
	 * @return Escaped string
	 */
	public static String escapeDoubleSpace(String string) {
		while (string.contains(" " + " ")) {
			string = string.replace("  ", " ");
		}
		
		return string;
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "AdditionalResultData[type=" + type + ", data=" + data + "]";
	}
	
	public static interface DisplayableString {
		
		String convertToDisplayableString();
		
	}
	
	public static interface ViewableContent {
		
		ContentViewerType getContentViewerType();
		
	}
	
}