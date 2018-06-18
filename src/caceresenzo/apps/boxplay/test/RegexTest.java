package caceresenzo.apps.boxplay.test;

import java.io.IOException;

import caceresenzo.apps.boxplay.factory.AdultFactory;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

public class RegexTest {
	
	private static AdultFactory adultFactory = new AdultFactory();
	
	public static void main(String[] args) throws IOException {
		String html = StringUtils.fromStream(RegexTest.class.getResourceAsStream("openload.html"));
		
		// Logger.info("outpiut: " + adultFactory.extractOpenloadJSKeyGeneratorFromHtml(html));
		// Logger.info("outpiut2 : " + adultFactory.formatHtmlDomForJsKeyGenerator(html));
		Logger.info("HTML: " + AdultFactory.formatOpenloadJsCodeExecutor(adultFactory.formatHtmlDomForJsKeyGenerator(html), adultFactory.extractOpenloadJSKeyGeneratorFromHtml(html)));
	}
	
}
