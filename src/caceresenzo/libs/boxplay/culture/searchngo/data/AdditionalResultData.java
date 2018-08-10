package caceresenzo.libs.boxplay.culture.searchngo.data;

import java.util.Iterator;
import java.util.List;

import caceresenzo.libs.boxplay.culture.searchngo.data.models.CategoryResultData;

/**
 * Holder class to hold data for a common result
 * 
 * @author Enzo CACERES
 */
public class AdditionalResultData {
	
	public static final String DATA_SEPARATOR = ", ";
	
	private ResultDataType type;
	private Object data;
	
	/**
	 * Constructor, create a new instance with the object only, {@link ResultDataType} will be interpreted as {@link ResultDataType#NULL}
	 * 
	 * @param data
	 *            Your data
	 */
	public AdditionalResultData(Object data) {
		this(ResultDataType.NULL, data);
	}
	
	/**
	 * Constructor, create a new instance with a {@link ResultDataType} and an object
	 * 
	 * @param type
	 *            Your {@link ResultDataType}
	 * @param data
	 *            Your data
	 */
	public AdditionalResultData(ResultDataType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	/**
	 * Get the {@link ResultDataType} of this result
	 * 
	 * @return The type
	 */
	public ResultDataType getType() {
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
			converted = (String) data;
		}
		
		return converted;
	}
	
	/**
	 * Static function to escape most se (during developpement) html custom char
	 * 
	 * @param string
	 *            Source string
	 * @return Escaped string
	 */
	public static String escapeHtmlChar(String string) {
		return string //
				.replace("&#039;", "'") //
				.replace("&eacute;", "é") //
		;
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
	
}