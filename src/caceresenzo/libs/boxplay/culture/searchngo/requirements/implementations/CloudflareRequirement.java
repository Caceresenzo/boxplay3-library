package caceresenzo.libs.boxplay.culture.searchngo.requirements.implementations;

import java.io.Serializable;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.requirements.BaseSystemRequirement;
import caceresenzo.libs.cryptography.MD5;
import caceresenzo.libs.http.client.webb.WebbConstante;
import caceresenzo.libs.reversing.cloudflare.CloudflareBypass;

public class CloudflareRequirement extends BaseSystemRequirement {
	
	/* Constants */
	/**
	 * Indication how long these cookies can be used before they are considered as unusable.<br>
	 * This time is exprimed in second, and it is usually 24 hour.<br>
	 * For this code, this value has been set to only one hour.
	 */
	public static final long CLOUDFLARE_COOKIE_EXPIRATION_MILLISECONDS = 60 * 60 * 1 * 1000;
	
	/* Static */
	public static final Map<String, CookieJar> COOKIE_CACHE = new HashMap<>();
	
	/* Variables */
	private CookieJar cookieJar;
	private CloudflareBypass.Callback callback;
	private String nextUrl;
	
	/* Constructor */
	public CloudflareRequirement() {
		this.cookieJar = new CookieJar(null, 0);
		
		this.callback = new CloudflareBypass.Callback() {
			@Override
			public void onSuccess(List<HttpCookie> cookieList) {
				List<SimpleHttpCookie> cookies = new ArrayList<>();
				
				for (HttpCookie httpCookie : cookieList) {
					cookies.add(new SimpleHttpCookie(httpCookie));
				}
				
				cookieJar = new CookieJar(cookies);
			}
			
			@Override
			public void onFail() {
				;
			}
			
			@Override
			public void onException(Exception exception) {
				exception.printStackTrace();
			}
		};
	}
	
	/**
	 * Prepare the cloudflare requirement with a url.
	 * 
	 * @param url
	 *            Target url to fetch cookies
	 * @return Itself
	 */
	public CloudflareRequirement prepare(String url) {
		this.nextUrl = url;
		
		String md5Url = MD5.silentMd5(url);
		
		if (COOKIE_CACHE.containsKey(md5Url)) {
			CookieJar cachedJar = COOKIE_CACHE.get(md5Url);
			
			if (isTimeExceed(cachedJar)) {
				COOKIE_CACHE.remove(md5Url);
			} else {
				cookieJar = cachedJar;
			}
		}
		
		return this;
	}
	
	/**
	 * Force the cookie extraction.
	 */
	public void execute() {
		new CloudflareBypass(nextUrl, callback).extract();
		
		COOKIE_CACHE.put(MD5.silentMd5(nextUrl), cookieJar);
		nextUrl = null;
	}
	
	/**
	 * Do the cookie extraction only if the cached cookies are bad or expired.
	 */
	public void executeOnlyIfNotUsable() {
		if (!isUsable()) {
			execute();
		}
	}
	
	/**
	 * Check if the cookies are valid or not.
	 * 
	 * @return Cookies' validity
	 */
	public boolean hasSuccess() {
		return cookieJar.areCookiesValid();
	}
	
	/**
	 * Check if actual cookies are usable for another requests.
	 * 
	 * @return Usability
	 */
	public boolean isUsable() {
		return cookieJar.areCookiesValid() && !isTimeExceed(cookieJar);
	}
	
	/**
	 * Check if the time has exceed for the {@link CookieJar} that you put in parameters.
	 * 
	 * @param cookieJar
	 *            Target {@link CookieJar} to check
	 * @return If they should be changed
	 */
	public boolean isTimeExceed(CookieJar cookieJar) {
		return cookieJar.getLastRunningMilliseconds() - new Date().getTime() > CLOUDFLARE_COOKIE_EXPIRATION_MILLISECONDS;
	}
	
	/**
	 * Get the same {@link Map} as put in parameters but wuth the "Cookie" field filled by cloudflare cookie, all other will be overrided.
	 * 
	 * @param headers
	 *            Actual headers
	 * @return Same headers map but with the "Cookie" field filled
	 */
	public Map<String, String> getCookiesAsHeaderMap(Map<String, String> headers) {
		if (headers == null) {
			headers = new HashMap<>();
		}
		
		if (cookieJar.areCookiesValid()) {
			headers.put(WebbConstante.HDR_USER_AGENT, WebbConstante.DEFAULT_USER_AGENT);
			headers.put("cookie", getCookiesAsString());
		}
		
		return headers;
	}
	
	/**
	 * @return A string that you can use directly in a HTTP request.
	 */
	public String getCookiesAsString() {
		if (cookieJar.areCookiesValid()) {
			return CloudflareBypass.listToString(cookieJar.getCookies());
		}
		
		return null;
	}
	
	/**
	 * Cookie holding class.
	 * 
	 * @author Enzo CACERES
	 */
	private static class CookieJar implements Serializable {
		
		/* Variables */
		private final List<SimpleHttpCookie> cookies;
		private final long lastRunningMilliseconds;
		
		/* Constructor */
		public CookieJar(List<SimpleHttpCookie> cookies) {
			this(cookies, new Date().getTime());
		}
		
		/* Constructor */
		public CookieJar(List<SimpleHttpCookie> cookies, long lastRunningMilliseconds) {
			this.cookies = cookies;
			this.lastRunningMilliseconds = lastRunningMilliseconds;
		}
		
		/**
		 * @return Stored cookies
		 */
		public List<SimpleHttpCookie> getCookies() {
			return cookies;
		}
		
		/**
		 * @return If stored cookies are not null
		 */
		public boolean areCookiesValid() {
			return cookies != null;
		}
		
		/**
		 * @return TIme in millisecond updated when this object was created.
		 */
		public long getLastRunningMilliseconds() {
			return lastRunningMilliseconds;
		}
		
	}
	
	public static class SimpleHttpCookie implements Serializable {
		
		/* Variables */
		private String name;
		private String content;
		
		/* Constructor */
		public SimpleHttpCookie(HttpCookie httpCookie) {
			this(httpCookie.getName(), httpCookie.getValue());
		}
		
		/* Constructor */
		public SimpleHttpCookie(String name, String content) {
			this.name = name;
			this.content = content;
		}
		
		/**
		 * @return Cookie's name
		 */
		public String getName() {
			return name;
		}
		
		/**
		 * @return Cookie's content
		 */
		public String getContent() {
			return content;
		}
		
		/* To String */
		@Override
		public String toString() {
			return name + "=" + content;
		}
		
	}
	
}