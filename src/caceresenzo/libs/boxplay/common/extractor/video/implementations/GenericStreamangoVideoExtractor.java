package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

/**
 * Implementation of a {@link ContentExtractor} for the <a href="https://streamango.com/">Streamango</a> plateform.
 * 
 * @author Enzo CACERES
 */
public class GenericStreamangoVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED = "We are unable to find the video you're looking for. There could be several reasons for this, for example it got removed by the owner.";
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		String html;
		try {
			getLogger().appendln("Downloading target page: " + url);
			html = Webb.create().get(url).header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT).ensureSuccess().asString().getBody();
			
			if (!StringUtils.validate(html)) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + html.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		if (!checkStreamingAvailability(html)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: " + FILE_DELETED);
			
			return null;
		} else {
			try {
				String directUrl = new StreamangoUrlDecoderSandbox().execute(html);
				
				getLogger().appendln("Computed url: " + directUrl);
				
				if (!StringUtils.validate(directUrl)) {
					return null;
				}
				
				if (progressCallback != null) {
					progressCallback.onFormattingResult();
				}
				
				return directUrl;
			} catch (Exception exception) {
				return null;
			}
		}
	}
	
	/**
	 * Used to check if the target file to stream is available or not.
	 * 
	 * @param html
	 *            Source of the page.
	 * @return File availability.
	 */
	public boolean checkStreamingAvailability(String html) {
		if (html == null) {
			return false;
		}
		
		return !html.contains(FILE_DELETED);
	}
	
	@Override
	public boolean isClientSensitive() {
		return true;
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		return baseUrl.matches(".*?(streamango\\.com|fruithosts\\.net|streamcherry\\.com).*?");
	}
	
	@SuppressWarnings("all")
	public static class StreamangoUrlDecoderSandbox implements Sandbox<String, String> {
		
		/* Variables */
		private ProviderHelper helper;
		
		/* Constructor */
		public StreamangoUrlDecoderSandbox() {
			this.helper = ProviderHelper.getStaticHelper();
		}
		
		@Override
		public String execute(String source) {
			Matcher decoderParametersMatcher = helper.regex("d\\('(.*?)',(\\d*?)\\)", source);
			
			if (!decoderParametersMatcher.find()) {
				throw new IllegalStateException("Failed to get decoder parameters.");
			}
			
			String _0x5ecd00 = decoderParametersMatcher.group(1);
			int _0x184b8d = Integer.valueOf(decoderParametersMatcher.group(2));
			
			// 4
			String k = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
			
			// 6
			String _0x59b81a = "";
			
			// 5
			int _0x2e4782;
			int _0x2c0540;
			int _0x5a46ef;
			
			// 0
			int _0x4a2f3a;
			int _0x29d5bf;
			int _0x3b6833;
			int _0x426d70;
			
			// 7
			int _0x1598e0 = 0x0;
			
			// 3
			// k = k.split("").reverse().join("");
			k = new StringBuilder(k).reverse().toString();
			
			// 2
			_0x5ecd00 = _0x5ecd00.replaceAll("[^A-Za-z0-9\\+\\/\\=]", "");
			
			// 1
			while (_0x1598e0 < _0x5ecd00.length()) {
				// 6
				_0x4a2f3a = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
				
				// 2
				_0x29d5bf = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
				
				// 9
				_0x3b6833 = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
				
				// 8
				_0x426d70 = k.indexOf(_0x5ecd00.charAt(_0x1598e0++));
				
				// 5
				_0x2e4782 = (_0x4a2f3a << 0x2) | (_0x29d5bf >> 0x4);
				
				// 4
				_0x2c0540 = ((_0x29d5bf & 0xf) << 0x4) | (_0x3b6833 >> 0x2);
				
				// 7
				_0x5a46ef = ((_0x3b6833 & 0x3) << 0x6) | _0x426d70;
				
				// 10
				_0x2e4782 = _0x2e4782 ^ _0x184b8d;
				
				// 0
				// _0x59b81a = _0x59b81a + String.fromCharCode(_0x2e4782);
				_0x59b81a = _0x59b81a + ((char) _0x2e4782);
				
				// 3
				if (_0x3b6833 != 0x40) {
					// _0x59b81a = _0x59b81a + String.fromCharCode(_0x2c0540);
					_0x59b81a = _0x59b81a + ((char) _0x2c0540);
				}
				
				// 1
				if (_0x426d70 != 0x40) {
					// _0x59b81a = _0x59b81a + String.fromCharCode(_0x5a46ef);
					_0x59b81a = _0x59b81a + ((char) _0x5a46ef);
				}
			}
			
			// 8
			// return _0x59b81a;
			String url = _0x59b81a;
			if (!url.startsWith("http:")) {
				url = "http:" + url;
			}
			
			return url;
		}
		
	}
	
}