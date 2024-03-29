package caceresenzo.libs.boxplay.factory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.libs.boxplay.models.premium.adult.AdultVideo;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.random.RandomString;

@Deprecated
public class AdultFactory extends AbstractFactory {
	
	/*
	 * Remplace: - page: %page%
	 */
	public static final String SITE_HOMEPAGE_UNFORMATTED = new String(Base64.decode("aHR0cDovL3d3dy52ZXBvcm5zLmNvbS92aWRlb3MvJXBhZ2Ul")); // http://www.site.com/
	public static final String REPLACE_HOMEPAGE_PAGE = "%page%";
	
	/*
	 * Remplace: - id: %id% - server: %server%
	 */
	public static final String SITE_VIDEO_UNFORMATTED = new String(Base64.decode("aHR0cDovL20udmVwb3Jucy5jb20vYWpheC5waHA/cGFnZT12aWRlb19wbGF5JmFjb2RlPSVhY29kZSUmaWQ9JWlkJSZzZXJ2ZXI9JXNlcnZlciU=")); // http://m.site.com/video/...
	public static final String REPLACE_VIDEO_ACODE = "%acode%";
	public static final String REPLACE_VIDEO_ID = "%id%";
	public static final String REPLACE_VIDEO_SERVER = "%server%";
	
	public static final String BASE_OPENLOAD_VIDEO_URL_UNFORMATTED = "https://openload.co/stream/%generated%?mime=true";
	public static final String REPLACE_OPENLOAD_VIDEO_URL_GENERATED = "%generated%";
	
	public AdultFactory() {
		;
	}
	
	public void parseHomepageHtml(AdultFactoryListener listener, String html, boolean loadNewsVideo) { // news -> the section
		if (html == null || html.isEmpty()) {
			listener.onHtmlNull();
			return;
		}
		
		if (loadNewsVideo) {
			Matcher newVideoMatcher = getNewVideoMatcher(html); 
			while (newVideoMatcher.find()) {
				String targetUrl = newVideoMatcher.group(1);
				String title = newVideoMatcher.group(2);
				String imageUrl = newVideoMatcher.group(3);
				
				AdultVideo video = AdultVideo //
						.instance(targetUrl, VideoOrigin.NEWS) //
						.withTitle(title) //
						.withImageUrl(imageUrl) //
				;
				
				listener.onAdultVideoCreated(video, VideoOrigin.NEWS);
			}
		}
		
		Matcher pageVideoMatcher = getPageVideoMatcher(html);
		while (pageVideoMatcher.find()) {
			String targetUrl = pageVideoMatcher.group(1);
			String title = pageVideoMatcher.group(2);
			String imageUrl = pageVideoMatcher.group(3);
			String unformattedViewCount = pageVideoMatcher.group(4);
			
			AdultVideo video = AdultVideo //
					.instance(targetUrl, VideoOrigin.PAGE) //
					.withTitle(title) //
					.withImageUrl(imageUrl) //
					.withViewCount(unformattedViewCount) //
			;
			
			listener.onAdultVideoCreated(video, VideoOrigin.PAGE);
		}
	}
	
	public String parseVideoPageData(String html) {
		Matcher matcher = getVideoInformationGetterMatcher(html);
		
		while (matcher.find()) {
			String id = matcher.group(1);
			String server = matcher.group(2);
			
			return formatAjaxVideoTargetUrl(id, server);
		}
		return null;
	}
	
	public String extractOpenloadLinkFromIframe(String html) {
		Matcher matcher = getOpenloadVideoLinkFromIframeExtractorMatcher(html);
		
		while (matcher.find()) {
			String url = matcher.group(1);
			
			return url;
		}
		return null;
	}
	
	public static interface AdultFactoryListener {
		
		void onHtmlNull();
		
		void onAdultVideoCreated(AdultVideo adultVideo, VideoOrigin origin);
		
	}
	
	public enum VideoOrigin {
		NEWS, PAGE;
	}
	
	/**
	 * https://regex101.com/r/86ChRb/5/
	 * 
	 * @Information
	 * 				
	 * 				Parse homepage html -> "new" videos
	 * 
	 * @Instruction
	 * 				
	 * 				Group 1: pageUrl
	 * 
	 *              Group 2: title
	 * 
	 *              Group 3: imageUrl
	 */
	public static Matcher getNewVideoMatcher(String html) {
		String regex = "\\<div\\sclass=\\\"item\\\"\\s.*?\\>[ \\t\\n]*\\<a\\shref=\\\"(.*?)\\\".*?\\>[ \\t\\n]*\\<div\\sstyle=\\\".*?\\\"\\>(.*?)\\<\\/div\\>[ \\t\\n]*\\<img\\ssrc=\\\"(.*?)\\\".*?\\>[ \\t\\n]*\\<\\/a\\>[ \\t\\n]*\\<\\/div\\>";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		
		return matcher;
	}
	
	/**
	 * https://regex101.com/r/86ChRb/9
	 * 
	 * @Information
	 * 				
	 * 				Parse homepage html -> "main page" videos
	 * 
	 * @Instruction
	 * 				
	 * 				Group 1: pageUrl
	 * 
	 *              Group 2: title
	 * 
	 *              Group 3: imageUrl
	 * 
	 *              Group 4: Views (unformatted)
	 */
	public static Matcher getPageVideoMatcher(String html) {
		String regex = "\\<li\\sclass=\\\"dvd-new\\\"\\s.*?\\>[ \\t\\n]*\\<a\\shref=\\\"(.*?)\".*?title=\\\"(.*?)\\\".*?background-image:[ ]*url\\((.*?)\\)\\\"\\>\\<\\/a\\>.*?\\<span\\sclass=\\\"views\\\"\\stitle=\\\"views\\\".*?\\>[ \\t\\n]*\\<span\\>[ \\t\\n]*\\<\\/span\\>[ \\t\\n]*([ \\,\\d]*)[ \\t\\n]*\\<\\/span\\>.*?\\<\\/li\\>[ \\t\\n]*";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		
		return matcher;
	}
	
	/**
	 * https://regex101.com/r/LPfa9G/1/
	 * 
	 * @Information
	 * 				
	 * 				Parse video html -> get mplay(id, server);
	 * 
	 * @Instruction
	 * 				
	 * 				Group 1: id
	 * 
	 *              Group 2: server
	 */
	public static Matcher getVideoInformationGetterMatcher(String html) {
		String regex = "mplay\\('(\\d*)','(\\d*)'\\);";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		
		return matcher;
	}
	
	/**
	 * https://regex101.com/r/i7IanO/2
	 * 
	 * @Information
	 * 				
	 * 				iframe src='(here)' ... /iframe
	 * 
	 * @Instruction
	 * 				
	 * 				Group 1: link
	 */
	public static Matcher getOpenloadVideoLinkFromIframeExtractorMatcher(String html) {
		String regex = "src=[\\'\\\"](.*?)[\\'\\\"]";
		
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(html);
		
		return matcher;
	}
	
	public static String formatHomepageUrl(int page) {
		return SITE_HOMEPAGE_UNFORMATTED.replace(REPLACE_HOMEPAGE_PAGE, String.valueOf(page));
	}
	
	public static String formatAjaxVideoTargetUrl(Object id, Object server) {
		return SITE_VIDEO_UNFORMATTED //
				.replace(REPLACE_VIDEO_ACODE, new RandomString(20).nextString()) //
				.replace(REPLACE_VIDEO_ID, String.valueOf(id)) //
				.replace(REPLACE_VIDEO_SERVER, String.valueOf(server));
	}
	
	public static String formatWebToMobileUrl(String videoUrl) {
		return videoUrl.replace("://www", "://m");
	}
	
}