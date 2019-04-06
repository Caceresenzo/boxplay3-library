package openload;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.aa.AADecoder;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderHelper;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.network.Downloader;
import caceresenzo.libs.string.StringUtils;

/**
 * This class is supposed to be able to calculate the link for a Openload Video in java. This will much faster than the old method.<br>
 * Kept in mind that this is really sensitive to changes, and can break at any time.<br>
 * <br>
 * Please put it in a try catch block, this function will throw plenty of error if anything goes not as espected.
 * 
 * @version 3
 * @author Enzo CACERES
 */
public class OpenloadUrlDecoderSandbox implements Sandbox<String, String> {
	
	public static void main(String[] args) throws IOException {
		// String key = "b25694ce859400d8e3b5c96686d2cb4eb4a64af40bf27ff6c4ed3bfe743c60780b5ad0be735e7e721b4846634c246f505a4a5b01427d7f4475017243675946017c67545e7a03725b514835694d757942027f637976680246524e6422735e52516701";
		Logger.info(new OpenloadUrlDecoderSandbox("").execute(Downloader.webget("https://oload.space/embed/v39H1kFTAk0/f5c0fdbb63f523d65ac9b90225c257b9")));
	}
	
	private ProviderHelper helper;
	private final String pageUrl;
	
	public OpenloadUrlDecoderSandbox(String pageUrl) {
		this.helper = ProviderHelper.getStaticHelper();
		
		this.pageUrl = pageUrl;
	}
	
	@SuppressWarnings("unused")
	@Override
	public String execute(String html) {
		Logger.info(html);
		
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
			Logger.info(decodedScript0);
			String htmlDivId = helper.extract("\\='(.*?)';", decodedScript0);
			
			sourceKey = helper.extract(String.format("<p style=\"\" id=\".*?\">(.*?)<\\/p>", htmlDivId), html);
		} catch (Exception exception) {
			throw new IllegalStateException("Failed to extract source key.", exception);
		}
		
		Matcher valueChanger1 = helper.regex("_0x30725e=.*?parseInt\\('(\\d+?)'.*?\\)-(\\d+).*?-(\\d+?).*?\\((\\d+?)-", html);
		Matcher valueChanger2 = helper.regex("_1x4bfb36=parseInt\\('(\\d+?)',8\\)-(\\d+?);", html);
		
		if (!(valueChanger1.find() && valueChanger2.find())) {
			throw new IllegalStateException("One of the value changer hasn't found its values.");
		}
		
		Logger.info("valueChanger1: " + valueChanger1.group(1));
		Logger.info("valueChanger1: " + valueChanger1.group(2));
		Logger.info("valueChanger1: " + valueChanger1.group(3));
		Logger.info("valueChanger1: " + valueChanger1.group(4));
		Logger.info("valueChanger2: " + valueChanger2.group(1));
		Logger.info("valueChanger2: " + valueChanger2.group(2));
		Logger.info("sourceKey: " + sourceKey);
		
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
		
		return String.format("%s/stream/%s?mime=true", pageUrl, _0x1bf6e5);
	}
	
	private class KKeHolder {
		
		public String k;
		public List<Long> ke;
		
		public KKeHolder(String k) {
			this.k = k;
			this.ke = new ArrayList<>();
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