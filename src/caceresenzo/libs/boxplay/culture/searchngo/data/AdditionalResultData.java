package caceresenzo.libs.boxplay.culture.searchngo.data;

import java.util.Iterator;
import java.util.List;

public class AdditionalResultData {
	
	public static final String DATA_SEPARATOR = ", ";
	
	private ResultDataType type;
	private Object data;
	
	public AdditionalResultData(Object data) {
		this(ResultDataType.NULL, data);
	}
	
	public AdditionalResultData(ResultDataType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public ResultDataType getType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}
	
	public String convert() {
		String converted = "";
		
		if (data instanceof List) {
			Iterator<?> iterator = ((List<?>) data).iterator();
			
			while (iterator.hasNext()) {
				converted += iterator.next() + (iterator.hasNext() ? DATA_SEPARATOR : "");
			}
		} else {
			converted = (String) data;
		}
		
		return converted;
	}
	
}