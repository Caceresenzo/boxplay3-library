package openload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

public class JsBreaker {
	
	public static void main(String[] args) throws IOException {
		String lines = StringUtils.fromFile("./research/openload/sample1.js");
		
		// String parsed = lines.replaceAll("\\x[\d]{1,2}", )
		Matcher matcher = Pattern.compile("\\\\x([\\w]{1,2})").matcher(lines);
		
		List<String> already = new ArrayList<>();
		while (matcher.find()) {
			String value = matcher.group(1);
			String syntax = "\\x" + value;
			
			char character = (char) (int) Integer.valueOf(value, 16);
			
			if (already.contains(syntax)) {
				continue;
			}
			already.add(syntax);
			
			Logger.info("escaping: %s to %s", value, character);
			
			lines = lines.replace(syntax, String.valueOf(character));
		}
		
		System.out.println(lines);
	}
	
}
