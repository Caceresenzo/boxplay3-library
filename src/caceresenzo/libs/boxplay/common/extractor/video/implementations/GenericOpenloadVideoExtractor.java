package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.aa.AADecoder;
import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.string.StringUtils;

/**
 * Implementation of a ContentExtractor for Openload
 * 
 * @author Enzo CACERES
 */
public class GenericOpenloadVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED = "We can't find the file you are looking for. It maybe got deleted by the owner or was removed due a copyright violation.";
	
	@Override
	public String extractDirectVideoUrl(String url, VideoContentExtractorProgressCallback progressCallback) {
		if (progressCallback != null) {
			progressCallback.onDownloadingUrl(url);
		}
		
		Matcher baseUrlMarcher = getStaticHelper().regex("(http:\\/\\/|ftp:\\/\\/|https:\\/\\/)(.*?)(?:\\/)", url);
		
		String baseUrlFormat;
		
		if (baseUrlMarcher.find()) {
			// https://<domain>/stream/<extracted>?mime=true
			baseUrlFormat = baseUrlMarcher.group(1) + baseUrlMarcher.group(2) + "/stream/%s??mime=true";
		} else {
			Exception exception = new Exception("Base url not found.");
			
			failed(true).notifyException(exception);
			getLogger().appendln("-- Failed: " + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().appendln("Parameters: ").appendln(" - BASE: " + baseUrlFormat).appendln();
		
		String html;
		try {
			getLogger().appendln("Downloading target page: " + url);
			html = Webb.create().get(url).chromeUserAgent().ensureSuccess().asString().getBody();
			
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
			String extractedKey = new OpenloadVideoIdComputeSandbox().execute(html);
			
			getLogger().appendln("Computed key: " + extractedKey);
			
			if (!StringUtils.validate(extractedKey)) {
				return null;
			}
			
			if (progressCallback != null) {
				progressCallback.onFormattingResult();
			}
			
			return String.format(baseUrlFormat, extractedKey);
		}
	}
	
	/**
	 * Used to check if the target file to stream is available or not.
	 * 
	 * @param html
	 *            Source of the page.
	 * @return Weather or not the {@link #FILE_DELETED} message is not on the page.
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
		/* Custom from provider: full-stream/, belike1.stream/ */
		return baseUrl.matches(".*?(oload\\..+|openload\\.co|oloadcdn\\.net|full-stream\\.co\\/|belike1\\.stream\\/).*?");
	}
	
	/**
	 * This class is supposed to be able to compute the id of an openload video that will be client sensitive (decoded for an ip) in java. This will much faster than the old method.<br>
	 * Kept in mind that this is really sensitive to changes, and can break at any time.<br>
	 * <br>
	 * Please put it in a try catch block, this function will throw plenty of error if anything goes not as espected.
	 * 
	 * @version 2
	 * @author Enzo CACERES
	 */
	@SuppressWarnings("all")
	public static class OpenloadVideoIdComputeSandbox implements Sandbox<String, String> {
		
		/* Variables */
		private ProviderHelper helper;
		
		/* Constructor */
		public OpenloadVideoIdComputeSandbox() {
			this.helper = ProviderHelper.getStaticHelper();
		}
		
		/**
		 * This function is an implementation of the obsfrucated javascript code available on a openload video page.
		 * 
		 * @param html
		 *            Downloaded page's HTML of the video you want to compute the key from.
		 * @throws IllegalStateException
		 *             If the <code>html</code> parameter is not valid (<code>null</code> or empty).
		 * @throws IllegalStateException
		 *             If there are more than two aa-encoded javascript in the page.
		 * @throws IllegalStateException
		 *             If the source key failed to be found.
		 * @throws IllegalStateException
		 *             If at least one of the two value changer didn't find its new values.
		 */
		@Override
		public String execute(String html) {
			if (!StringUtils.validate(html)) {
				throw new IllegalStateException("HTML is not a valid String.");
			}
			
			List<String> aaEncodedScripts = AADecoder.extractAllAAEncodedStrings(html);
			
			if (aaEncodedScripts.size() != 2) {
				throw new IllegalStateException("Invalid aa-encoded script count.");
			}
			
			String sourceKey;
			try {
				String decodedScript0 = AADecoder.decode(aaEncodedScripts.get(0));
				String htmlDivId = helper.extract("\\+'(.*?)';", decodedScript0);
				
				sourceKey = helper.extract(String.format("<p id=\".*?\" style=\"\">(.*?)<\\/p>", htmlDivId), html);
			} catch (Exception exception) {
				throw new IllegalStateException("Failed to extract source key.", exception);
			}
			
			Matcher valueChanger1 = helper.regex("_0x30725e=.*?parseInt\\('(\\d+?)'.*?\\)-(\\d+).*?-(\\d+?).*?\\((\\d+?)-", html);
			Matcher valueChanger2 = helper.regex("_1x4bfb36=parseInt\\('(\\d+?)',8\\)-(\\d+?);", html);
			
			if (!(valueChanger1.find() && valueChanger2.find())) {
				throw new IllegalStateException("One of the value changer hasn't found its values.");
			}
			
			// 11
			String _0x531f91 = sourceKey;
			
			// 12
			// var _0x5d72cd = _0x531f91.charAt(0x0);
			
			// 13
			String _0x5d72cd = _0x531f91;
			
			// 0
			String _0x1bf6e5 = "";
			
			// 14
			int _0x41e0ff = (0x9 * 0x8);
			
			// 3
			int _0xccbe62 = _0x5d72cd.length();
			
			// 2
			Object _0x439a49 = _0x5d72cd.substring(0x0, _0x41e0ff);
			
			// 9
			List<Long> _0x3d7b02 = new ArrayList<>();
			
			// 16
			Object _0x31f4aa = new KKeHolder((String) _0x439a49);
			
			// 1
			for (int i = 0x0; i < ((String) _0x439a49).length(); i += 0x8) {
				_0x41e0ff = i * 0x8;
				
				String _0x40b427 = ((String) _0x439a49).substring(i, i + 0x8);
				long _0x577716 = Long.valueOf(_0x40b427, 0x10);
				
				{
					if (false) {
						_0x577716 = 0x0;
					}
					((KKeHolder) _0x31f4aa).ke.add(_0x577716);
				}
			}
			
			// 4
			_0x3d7b02 = ((KKeHolder) _0x31f4aa).ke;
			{
				if (false) {
					_0x3d7b02 = null;
				}
			}
			
			// 8
			_0x41e0ff = 0x9 * 0x8;
			
			// 5
			_0x5d72cd = _0x5d72cd.substring(_0x41e0ff);
			
			// 6
			_0x439a49 = 0x0;
			
			// 15
			int _0x145894 = 0x0;
			
			// 10
			while ((int) _0x439a49 < _0x5d72cd.length()) {
				
				// 10 - 5
				int _0x5eb93a = 0x40;
				
				// 10 - 8
				int _0x37c346 = 0x7f;
				
				// 10 - 0
				long _0x896767 = 0x0;
				
				// 10 - 12
				int _0x1a873b = 0x0;
				
				// 10 - 13
				long _0x3d9c8e = 0x0;
				
				// 10 - 9
				_0x31f4aa = new MmXxHolder(0x80, 0x3f);
				
				// 10 - 10
				do {
					// 10 - 10 - 4
					if (((int) _0x439a49 + 0x1) >= _0x5d72cd.length()) {
						_0x5eb93a = 0x8f;
					}
					
					// 10 - 10 - 3
					String _0x1fa71e = _0x5d72cd.substring((int) _0x439a49, ((int) _0x439a49 + 0x2));
					
					// 10 - 10 - 0
					_0x439a49 = ((int) _0x439a49) + 1;
					
					// 10 - 10 - 5
					_0x439a49 = ((int) _0x439a49) + 1;
					
					// 10 - 10 - 6
					_0x3d9c8e = Long.valueOf(_0x1fa71e, 0x10);
					
					// 10 - 10 - 2
					{
						if (false) {
							_0x3d9c8e += 10;
							((MmXxHolder) _0x31f4aa).xx = 0x11;
						}
						
						if (_0x1a873b < (0x6 * 0x5)) {
							long _0x332549 = _0x3d9c8e & ((MmXxHolder) _0x31f4aa).xx;
							_0x896767 += _0x332549 << _0x1a873b;
						} else {
							long _0x332549 = _0x3d9c8e & ((MmXxHolder) _0x31f4aa).xx;
							_0x896767 += _0x332549 * Math.pow(0x2, _0x1a873b);
						}
					}
					
					// 10 - 10 - 1
					_0x1a873b += 0x6;
					
				} while (_0x3d9c8e >= _0x5eb93a);
				
				// 10 - 4
				long _0x59ce16 = 0x28a28dec;
				
				// 10 - 11
				long _1x4bfb36 = Long.valueOf(valueChanger2.group(1), 8) - Integer.valueOf(valueChanger2.group(2));
				
				// 10 - 6
				long _0x30725e = _0x896767 ^ _0x3d7b02.get((_0x145894 % 0x9));
				
				// 10 - 3
				_0x30725e = (_0x30725e ^ ((Long.valueOf(valueChanger1.group(1), 8) - Integer.valueOf(valueChanger1.group(2)) + 0x4 - Integer.valueOf(valueChanger1.group(3))) / (Integer.valueOf(valueChanger1.group(4)) - 0x8))) ^ _1x4bfb36;
				
				// 10 - 1
				int _0x2de433 = (_0x5eb93a * 0x2) + _0x37c346;
				
				// 10 - 7
				for (int i = 0x0; (i < 0x4); i++) {
					// 10 - 7 - 2
					long _0x1a9381 = _0x30725e & _0x2de433;
					
					// 10 - 7 - 0
					int _0x1a0e90 = (_0x41e0ff / 0x9) * i;
					
					// 10 - 7 - 5
					_0x1a9381 = _0x1a9381 >> _0x1a0e90;
					
					// 10 - 7 - 4
					String _0x3fa834 = String.valueOf((char) (_0x1a9381 - 1));
					
					// 10 - 7 - 3
					if (_0x3fa834.charAt(0) != '$') {
						_0x1bf6e5 += _0x3fa834;
					}
					
					// 10 - 7 - 1
					_0x2de433 = _0x2de433 << (_0x41e0ff / 0x9);
				}
				
				// 10 - 2
				_0x145894 += 0x1;
				
			}
			
			// 7
			// $("#lqEH1").text(_0x1bf6e5);
			
			return _0x1bf6e5;
		}
		
		/**
		 * Simple data holder.
		 * 
		 * @author Enzo CACERES
		 */
		private static class KKeHolder {
			
			/* Variables */
			public String k;
			public List<Long> ke;
			
			/* Constructor */
			public KKeHolder(String k) {
				this.k = k;
				this.ke = new ArrayList<>();
			}
			
		}
		
		/**
		 * Simple data holder.
		 * 
		 * @author Enzo CACERES
		 */
		private static class MmXxHolder {
			
			/* Variables */
			public int mm, xx;
			
			/* Constructor */
			public MmXxHolder(int mm, int xx) {
				this.mm = mm;
				this.xx = xx;
			}
			
		}
		
	}
	
}