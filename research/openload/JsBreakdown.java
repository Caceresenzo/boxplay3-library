package openload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

public class JsBreakdown {
	
	public static String lines;
	
	public static void main(String[] args) throws IOException {
		lines = StringUtils.fromFile("./research/openload/sample2.js");
		
		breakJs();
		callQuicker();
		
		System.out.println(lines);
	}
	
	public static void breakJs() {
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
			
			// Logger.info("escaping: %s to %s", value, character);
			
			lines = lines.replace(syntax, String.valueOf(character));
		}
	}
	
	public static void callQuicker() {
		Matcher matcher = Pattern.compile("_0x5949\\([\\\"\\']{1}0x(.*?)[\\\"\\']{1}\\)").matcher(lines);
		
		List<String> already = new ArrayList<>();
		while (matcher.find()) {
			String syntax = matcher.group(0);
			String value = matcher.group(1);
			
			int index = Integer.valueOf(value, 16);
			String call = calls.get(index);
			
			if (already.contains(syntax)) {
				continue;
			}
			already.add(syntax);
			
			Logger.info("escaping: %s to %s", value, call);
			
			lines = lines.replace(syntax, String.format("\"%s\"", call));
		}
	}
	
	public static List<String> calls = new ArrayList<>();
	
	static {
		calls.add("11|12|13|0|14|3|2|9|16|1|4|8|5|6|15|10|7");
		calls.add("split");
		calls.add("xXM");
		calls.add("length");
		calls.add("jLa");
		calls.add("substring");
		calls.add("aZP");
		calls.add("dOD");
		calls.add("Poy");
		calls.add("write");
		calls.add("push");
		calls.add("dkF");
		calls.add("text");
		calls.add("tjf");
		calls.add("HIZ");
		calls.add("WyX");
		calls.add("faz");
		calls.add("Gcy");
		calls.add("ucs");
		calls.add("2|0|5|4|3|1");
		calls.add("dhh");
		calls.add("pQu");
		calls.add("eMz");
		calls.add("ZBD");
		calls.add("Lnk");
		calls.add("fromCharCode");
		calls.add("DYl");
		calls.add("4|3|0|5|6|2|1");
		calls.add("WOa");
		calls.add("gzw");
		calls.add("cBV");
		calls.add("Zft");
		calls.add("pow");
		calls.add("tcV");
		calls.add("EAk");
		calls.add("QHb");
		calls.add("charCodeAt");
	}
}