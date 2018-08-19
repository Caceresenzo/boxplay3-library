package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;

/**
 * Implementation of a ContentExtractor for Openload
 * 
 * @author Enzo CACERES
 */
public abstract class OpenloadVideoExtractor extends VideoContentExtractor {
	
	public static final String CODE_EXECUTOR_JS_FUNCTION_NAME = "myFunction";
	
	/**
	 * This regex is used to extract the "japanease smiley" encoded java script at the end of the page that is used to decypher a key to watch the video
	 */
	public static final String REGEX_DOM_DATA_EXTRACTOR = "\\<div\\sclass=\\\"\\\"\\sstyle=\\\"display:none;\\\"\\>[ \\t\\n]*\\<p\\sstyle=\\\"\\\"\\sid=\\\"(.*?)\\\"\\>(.*?)\\<\\/p\\>[ \\t\\n]*\\<p\\sstyle=\\\"\\\"\\sclass=\\\"\\\"\\sid=\\\"DtsBlkVFQx\\\"\\>(.*?)\\<\\/p\\>[ \\t\\n]\\<\\/div\\>";
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String openloadHtml = downloadTargetPage(url);
		
		waitUntilUnlock();
		
		if (!checkStreamingAvailability(openloadHtml)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			failed(true).notifyException(new StreamingNotAvailableException());;
			
			return null;
		}
		
		if (progressCallback != null) {
			progressCallback.onExtractingLink();
		}
		injectJsCode(createJsCodeExecutor(openloadHtml));
		
		waitUntilUnlock();
		
		String resolvedHtml = getJsResult();
		
		waitUntilUnlock();
		
		if (progressCallback != null) {
			progressCallback.onFormattingResult();
		}
		return String.format("https://openload.co/stream/%s?mime=true", getOpenloadKey(resolvedHtml));
	}
	
	/**
	 * Abstract function, used to download a target page
	 * 
	 * @param url
	 *            Target url
	 * @return Page content
	 */
	public abstract String downloadTargetPage(String url);
	
	/**
	 * Abstract function, used to check if the target file to stream is available or not
	 * 
	 * @param html
	 *            Source of the page
	 * @return Yes or not
	 */
	public abstract boolean checkStreamingAvailability(String html);
	
	/**
	 * Abstract function, used to inject into some kind of webview or html parser (with javascript support), code that will be executed
	 * 
	 * @param html
	 *            Js code executor
	 */
	public abstract void injectJsCode(String html);
	
	/**
	 * Extract from your webview/html parser, the result of the javascript
	 * 
	 * @return All html page
	 */
	public abstract String getJsResult();
	
	/**
	 * Get a extracted openload video key, usable to format into an url later
	 * 
	 * @param htmlResult
	 *            Page after the js code execution
	 * @return An Openload key
	 */
	public abstract String getOpenloadKey(String htmlResult);
	
	/**
	 * Create a complete js code executor to "decypher" openload video key
	 * 
	 * This will create a code like this:
	 * 
	 * "<html><jquery script> htmlDom <script>function myFunction() { % decypher % }; myFunction();</script></html>";
	 * 
	 * the htmlDom is some div in html that the decypher use to get master key and save the result
	 * 
	 * the decypher is some shitty js "japanease smiley" encoded code
	 * 
	 * @param openloadHtml
	 *            Target openload page html
	 * @return A complete js code executor, null if matchers are not correct and information are not valid
	 */
	public String createJsCodeExecutor(String openloadHtml) {
		String htmlDom = null, jsCode = null;
		
		/**
		 * Extract uniques keys used by js to decypther everything
		 */
		Matcher domMatcher = ProviderHelper.getStaticHelper().regex(REGEX_DOM_DATA_EXTRACTOR, openloadHtml);
		if (domMatcher.find()) {
			String keyId = domMatcher.group(1);
			String keyContent = domMatcher.group(2);
			String filepath = domMatcher.group(3);
			
			htmlDom = ("<div class=\"\" style=\"display:none;\">\r\n" + //
					"<p style=\"\" id=\"%keyid%\">%keycontent%</p>\r\n" + //
					"<p style=\"\" class=\"\" id=\"DtsBlkVFQx\">%filepath%</p>\r\n" + //
					"</div>") //
							.replace("%keyid%", keyId) //
							.replace("%keycontent%", keyContent) //
							.replace("%filepath%", filepath) //
			; //
		}
		
		/**
		 * Extract "japanease smiley" encoded js at the end of the page
		 */
		Matcher jsMatcher = ProviderHelper.getStaticHelper().regex("\\<script\\ssrc=\\\"\\/assets\\/js\\/video-js\\/.*?\\\"\\>\\<\\/script\\>[ \\t\\n]*\\<script\\stype=\\\"text\\/javascript\\\"\\>[ \\t\\n]*(.*?)[ \\t\\n]*\\<\\/script\\>[ \\t\\n]*\\<\\/body\\>[ \\t\\n]*\\<\\/html\\>", openloadHtml);
		if (jsMatcher.find()) {
			jsCode = jsMatcher.group(1);
		}
		
		if (htmlDom == null || jsCode == null) {
			getLogger().appendln("ERROR: NULL ?").append("-- htmlDom: ").appendln(htmlDom == null).append("-- jsCode: ").appendln(htmlDom == null);
			return null;
		}
		
		return "<html><script src=\"https://oload.download/assets/js/jquery.min.js\"></script>" + htmlDom + "<script>function " + CODE_EXECUTOR_JS_FUNCTION_NAME + "() {" + jsCode + "}; myFunction();</script></html>";
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(openload\\.co|oloadcdn\\.net).*?");
	}
	
}