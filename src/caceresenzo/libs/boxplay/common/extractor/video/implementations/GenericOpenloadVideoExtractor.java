package caceresenzo.libs.boxplay.common.extractor.video.implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.common.extractor.video.VideoContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.string.StringUtils;

/**
 * Implementation of a ContentExtractor for Openload
 * 
 * @author Enzo CACERES
 */
public class GenericOpenloadVideoExtractor extends VideoContentExtractor {
	
	/* Constants */
	public static final String FILE_DELETED = "We can't find the file you are looking for. It maybe got deleted by the owner or was removed due a copyright violation.";
	
	/**
	 * This regex is used to extract the "japanease smiley" encoded java script at the end of the page that is used to decypher a key to watch the video
	 */
	public static final String REGEX_DOM_DATA_EXTRACTOR = "\\<div\\sclass=\\\"\\\"\\sstyle=\\\"display:none;\\\"\\>[ \\t\\n]*\\<p\\sstyle=\\\"\\\"\\sid=\\\"(.*?)\\\"\\>(.*?)\\<\\/p\\>[ \\t\\n]*\\<p\\sstyle=\\\"\\\"\\sclass=\\\"\\\"\\sid=\\\"DtsBlkVFQx\\\"\\>(.*?)\\<\\/p\\>[ \\t\\n]*\\<\\/div\\>";
	
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
		
		String openloadHtml;
		try {
			getLogger().appendln("Downloading target page: " + url);
			openloadHtml = Webb.create().get(url).header(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT).ensureSuccess().asString().getBody();
			
			if (!StringUtils.validate(openloadHtml)) {
				throw new NullPointerException("Download string is null.");
			}
			
			getLogger().appendln("-- Finished > size=" + openloadHtml.length());
		} catch (Exception exception) {
			failed(true).notifyException(exception);
			getLogger().appendln("-- Finished > failed=" + exception.getLocalizedMessage());
			return null;
		}
		
		getLogger().separator();
		
		if (!checkStreamingAvailability(openloadHtml)) {
			if (progressCallback != null) {
				progressCallback.onFileNotAvailable();
			}
			
			getLogger().appendln("Error: " + FILE_DELETED);
			
			return null;
		} else {
			String extractedKey = new OpenloadUrlDecoderSandbox().execute(openloadHtml);
			
			if (!StringUtils.validate(extractedKey)) {
				return null;
			}
			
			return String.format(baseUrlFormat, extractedKey);
		}
	}
	
	/**
	 * Used to check if the target file to stream is available or not
	 * 
	 * @param html
	 *            Source of the page
	 * @return Yes or not
	 */
	public boolean checkStreamingAvailability(String html) {
		if (html == null) {
			return false;
		}
		
		return !html.contains(FILE_DELETED);
	}
	
	@Override
	public boolean matchUrl(String baseUrl) {
		/* Custom from provider: full-stream/, belike1.stream/ */
		return baseUrl.matches(".*?(oload\\.stream|oload\\.download|oload\\.fun|openload\\.co|oloadcdn\\.net|full-stream\\.co\\/|belike1\\.stream\\/).*?");
	}
	
	/**
	 * This class is supposed to be able to calculate the link for a Openload Video in java. This will much faster than the old method.<br>
	 * Kept in mind that this is really sensitive to changes, and can break at any time.<br>
	 * 
	 * Please put it in a try catch block, this function will throw plenty of error if anything goes not as espected.
	 * 
	 * @author Enzo CACERES
	 */
	@SuppressWarnings("all")
	public static class OpenloadUrlDecoderSandbox implements Sandbox<String, String> {
		
		/* Variables */
		private ProviderHelper helper;
		
		/* Constructor */
		public OpenloadUrlDecoderSandbox() {
			this.helper = ProviderHelper.getStaticHelper();
		}
		
		/**
		 * This function is an implementation of the obsfrucated javascript available on a openload video page.
		 * 
		 * @param source
		 *            Html or js container present on the page that will be used to extract precious information
		 */
		@Override
		public String execute(String source) {
			String extractedSourceKey = extractSourceKey(source);
			long swifterKey1 = getKeySwifterValue(1, source);
			long swifterKey2 = getKeySwifterValue(2, source);
			
			String finalKey = "";
			
			String editableKey = extractedSourceKey;
			
			int chunckSize = 72;
			Object chunck = editableKey.substring(0x0, chunckSize);
			
			List<Long> _0x3d7b02 = new ArrayList<>();
			
			Object _0x31f4aa = new KKeyPartHolder((String) chunck);
			
			for (int i = 0; i < ((String) chunck).length(); i += 8) {
				chunckSize = i * 8;
				String data = ((String) chunck).substring(i, i + 8);
				long value = Long.parseLong(data, 16);
				
				KKeyPartHolder.class.cast(_0x31f4aa).keyPart.add(value);
			}
			
			_0x3d7b02 = KKeyPartHolder.class.cast(_0x31f4aa).keyPart;
			
			// 8
			chunckSize = 72;
			
			// 5
			editableKey = editableKey.substring(chunckSize);
			
			// 6
			chunck = 0;
			
			// 15
			long _0x145894 = 0;
			
			// 10
			while ((int) chunck < editableKey.length()) {
				// 5
				long _0x5eb93a = 64;
				
				// 8
				long _0x37c346 = 127;
				
				// 0
				long _0x896767 = 0;
				
				// 12
				long _0x1a873b = 0;
				
				// 13
				long _0x3d9c8e = 0;
				
				// 9
				_0x31f4aa = new MmXxHolder(128, 63);
				
				// 10
				do {
					// 4
					if ((int) chunck + 1 >= editableKey.length()) {
						_0x5eb93a = 143;
					}
					
					// 3
					String _0x1fa71e = editableKey.substring((int) chunck, (int) chunck + 2);
					
					// 0
					chunck = ((int) chunck) + 1;
					
					// 5
					chunck = ((int) chunck) + 1;
					
					// 6
					_0x3d9c8e = Integer.parseInt(_0x1fa71e, 16);
					
					// 2
					if (_0x1a873b < 30) {
						long _0x332549 = _0x3d9c8e & MmXxHolder.class.cast(_0x31f4aa).xx;
						_0x896767 += _0x332549 << _0x1a873b;
					} else {
						long _0x332549 = _0x3d9c8e & MmXxHolder.class.cast(_0x31f4aa).xx;
						_0x896767 += _0x332549 * Math.pow(2, _0x1a873b);
					}
					
					// 1
					_0x1a873b += 6;
				} while (_0x3d9c8e >= _0x5eb93a);
				
				// 4
				long _0x59ce16 = 0x28a28dec;
				
				// 11
				long _1x4bfb36 = swifterKey2;
				
				// 6
				long _0x30725e = _0x896767 ^ _0x3d7b02.get((int) (_0x145894 % 9));
				
				// 3
				_0x30725e = (_0x30725e ^ swifterKey1) ^ _1x4bfb36;
				
				// 1
				long _0x2de433 = (_0x5eb93a * 2) + _0x37c346;
				
				// 7
				for (int i = 0; i < 4; i++) {
					// 2
					long _0x1a9381 = _0x30725e & _0x2de433;
					
					// 0
					long _0x1a0e90 = (chunckSize / 9) * i;
					
					// 5
					_0x1a9381 = _0x1a9381 >> _0x1a0e90;
					
					// 4
					char _0x3fa834 = (char) (_0x1a9381 - 1);
					
					// 3
					if (_0x3fa834 != '$') {
						finalKey += _0x3fa834;
					}
					
					// 1
					_0x2de433 = _0x2de433 << (chunckSize / 9);
				}
				
				// 2
				_0x145894 += 1;
			}
			
			return finalKey;
		}
		
		/**
		 * Extract encoded data that is needed to compute the final url.
		 * 
		 * @param source
		 *            Html/js container source of the page to extract these values from
		 * @return Extracted source key to work with
		 * @throws IllegalArgumentException
		 *             If the source parameter is null
		 * @throws NullPointerException
		 *             If source key not found, or if the code really do a {@link NullPointerException}
		 */
		public String extractSourceKey(String source) {
			if (!StringUtils.validate(source)) {
				throw new IllegalArgumentException("Source can't be null.");
			}
			
			Matcher matcher = helper.regex("\\<div\\sclass\\=\\\"\\\"\\sstyle\\=\\\"display\\:none\\;\\\"\\>[\\s]*\\<p\\sstyle\\=\\\"\\\"\\sid\\=\\\".*?\\\"\\>(.*?)\\<\\/p\\>.*?\\<\\/div\\>", source);
			
			if (!matcher.find()) {
				throw new NullPointerException("Source key not found.");
			}
			
			return matcher.group(1).trim();
		}
		
		/**
		 * "key swifter" are little values that are changing depending of the video file.<br>
		 * For an extraction to work, you will need to tweak these values in your code.<br>
		 * <br>
		 * Actually, there are 2 values that you need to change:
		 * <ul>
		 * <li>First occure at (relative) line 170 in the openload source page, here a regex to get this value: <a href="https://regex101.com/r/JZqOnj/1">regex101</a></li>
		 * <li>The second occure at (relative) line 265, regex: <a href="https://regex101.com/r/UzbSWO/1">regex101</a></li>
		 * </ul>
		 * If one of the keys are not found, you must considere that the extraction is not possible.<br>
		 * In that case, try to redownload the page, or tweak the regex.
		 * 
		 * @param id
		 *            Id of the "swifter key" that you want, you can only put <code>1</code> or <code>2</code>
		 * @param source
		 *            Html/js container source of the page to extract these values from
		 * @return Calculated values
		 * @throws IllegalArgumentException
		 *             If the source parameter is null
		 * @throws IllegalStateException
		 *             If no swifter key is available with the id you enter
		 * @throws NullPointerException
		 *             If swifter keys are not founds, or if the code really do a {@link NullPointerException}
		 */
		public long getKeySwifterValue(int id, String source) {
			if (!StringUtils.validate(source)) {
				throw new IllegalArgumentException("Source can't be null.");
			}
			
			switch (id) {
				case 1: {
					Matcher matcher = helper.regex("\\(parseInt\\(\\'([\\d]*)\\'\\,8\\)\\-([\\d]*)\\+0x4\\-([\\d]*)\\)\\/\\(([\\d]*)-0x8\\)\\)", source);
					
					if (!matcher.find()) {
						throw new NullPointerException("Key Swifter 1 not found.");
					}
					
					long a = Long.parseLong(matcher.group(1), 8);
					long b = Integer.parseInt(matcher.group(2));
					long c = Integer.parseInt(matcher.group(3));
					long d = Integer.parseInt(matcher.group(4));
					
					return ((a - b + 0x4 - c) / (d - 0x8));
				}
				
				case 2: {
					Matcher matcher = helper.regex("\\=parseInt\\('([\\d]*)'\\,8\\)-(.*?);", source);
					
					if (!matcher.find()) {
						throw new NullPointerException("Key Swifter 2 not found.");
					}
					
					long a = Long.parseLong(matcher.group(1), 8);
					long b = Integer.parseInt(matcher.group(2));
					
					return (a - b);
				}
				
				default: {
					throw new IllegalStateException("No key swifter with id: " + id);
				}
			}
		}
		
		private class KKeyPartHolder {
			
			public String k;
			public List<Long> keyPart;
			
			public KKeyPartHolder(String chunk) {
				k = chunk;
				keyPart = new ArrayList<>();
			}
			
		}
		
		private class MmXxHolder {
			
			public int mm, xx;
			
			public MmXxHolder(int mm, int xx) {
				this.mm = mm;
				this.xx = xx;
			}
			
		}
	}
	
}