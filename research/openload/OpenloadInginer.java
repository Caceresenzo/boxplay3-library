package openload;

import java.io.IOException;
import java.util.List;

import caceresenzo.libs.inginer.languages.functions.SimpleFunction;
import caceresenzo.libs.inginer.languages.javascript.JavaScriptLanguage;
import caceresenzo.libs.string.StringUtils;

public class OpenloadInginer {
	
	public static void main(String[] args) throws IOException {
		JavaScriptLanguage language = new JavaScriptLanguage();
		
		String lines = StringUtils.fromFile("./research/openload/working.js");
		lines = language.reverseCallerFunction(lines, "var _0x9495 = (.*?);", "_0x5949");
		
		List<SimpleFunction> functions = language.parseSimpleFunction(lines, "var _0x45ae41 = \\{([\\w\\W]*?)\\};");
		lines = language.reverseDeepFunctionCalls(lines, functions, "_0x45ae41");
		System.out.println(lines);
	}
	
}