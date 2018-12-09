package cloudflare;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

public class Cloudflare {
	
	private String mUrl;
	private String mUser_agent;
	private cfCallback mCallback;
	private int mRetry_count;
	private URL ConnUrl;
	private List<HttpCookie> mCookieList;
	private CookieManager mCookieManager;
	private HttpURLConnection mCheckConn;
	private HttpURLConnection mGetMainConn;
	private HttpURLConnection mGetRedirectionConn;
	
	private static final int MAX_COUNT = 3;
	private static final int CONN_TIMEOUT = 60000;
	private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;";
	
	private boolean canVisit = false;
	
	public Cloudflare(String url) {
		mUrl = url;
	}
	
	public Cloudflare(String url, String user_agent) {
		mUrl = url;
		mUser_agent = user_agent;
	}
	
	public String getUser_agent() {
		return mUser_agent;
	}
	
	public void setUser_agent(String user_agent) {
		mUser_agent = user_agent;
	}
	
	public void getCookies(final cfCallback callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				urlThread(callback);
			}
		}).start();
	}
	
	private void urlThread(cfCallback callback) {
		mCookieManager = new CookieManager();
		mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL); // ????cookies
		CookieHandler.setDefault(mCookieManager);
		HttpURLConnection.setFollowRedirects(false);
		
		while (!canVisit) {
			if (mRetry_count > MAX_COUNT) {
				break;
			}
			try {
				
				int responseCode = checkUrl();
				if (responseCode == 200) {
					canVisit = true;
					break;
				} else {
					getVisiteCookie();
				}
			} catch (IOException | InterruptedException e) {
				if (mCookieList != null) {
					mCookieList.clear();
				}
				e.printStackTrace();
			} finally {
				closeAllConn();
			}
			mRetry_count++;
		}
		if (callback != null) {
			if (canVisit) {
				callback.onSuccess(mCookieList);
			} else {
				e("Get Cookie Failed");
				callback.onFail();
			}
		}
	}
	
	private void getVisiteCookie() throws IOException, InterruptedException {
		ConnUrl = new URL(mUrl);
		mGetMainConn = (HttpURLConnection) ConnUrl.openConnection();
		mGetMainConn.setRequestMethod("GET");
		mGetMainConn.setConnectTimeout(CONN_TIMEOUT);
		mGetMainConn.setReadTimeout(CONN_TIMEOUT);
		if (StringUtils.validate(mUser_agent)) {
			mGetMainConn.setRequestProperty("user-agent", mUser_agent);
		}
		mGetMainConn.setRequestProperty("accept", ACCEPT);
		mGetMainConn.setRequestProperty("referer", mUrl);
		if (mCookieList != null && mCookieList.size() > 0) {
			mGetMainConn.setRequestProperty("cookie", listToString(mCookieList));
		}
		mGetMainConn.setUseCaches(false);
		mGetMainConn.connect();
		switch (mGetMainConn.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				e("MainUrl", "visit website success");
				return;
			case HttpURLConnection.HTTP_FORBIDDEN:
				e("MainUrl", "IP block or cookie err");
				return;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				InputStream mInputStream = mCheckConn.getErrorStream();
				BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
				StringBuilder sb = new StringBuilder();
				String str;
				while ((str = mBufferedReader.readLine()) != null) {
					sb.append(str);
				}
				mInputStream.close();
				mBufferedReader.close();
				mCookieList = mCookieManager.getCookieStore().getCookies();
				str = sb.toString();
				getCheckAnswer(str);
				break;
			default:
				
				break;
		}
	}
	
	/**
	 * ????????cookies
	 * 
	 * @param str
	 */
	private void getCheckAnswer(String str) throws InterruptedException, IOException {
		String jschl_vc = regex(str, "name=\"jschl_vc\" value=\"(.+?)\"").get(0); // ????
		String pass = regex(str, "name=\"pass\" value=\"(.+?)\"").get(0); //
		double jschl_answer = get_answer(str);
		e(String.valueOf(jschl_answer));
		Thread.sleep(3000);
		String req = String.valueOf("https://" + ConnUrl.getHost()) + "/cdn-cgi/l/chk_jschl?" + "jschl_vc=" + jschl_vc + "&pass=" + pass + "&jschl_answer=" + jschl_answer;
		e("RedirectUrl", req);
		getRedirectResponse(req);
	}
	
	private void getRedirectResponse(String req) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		mGetRedirectionConn = (HttpURLConnection) new URL(req).openConnection();
		mGetRedirectionConn.setRequestMethod("GET");
		mGetRedirectionConn.setConnectTimeout(CONN_TIMEOUT);
		mGetRedirectionConn.setReadTimeout(CONN_TIMEOUT);
		if (StringUtils.validate(mUser_agent)) {
			mGetRedirectionConn.setRequestProperty("user-agent", mUser_agent);
		}
		mGetRedirectionConn.setRequestProperty("accept", ACCEPT);
		mGetRedirectionConn.setRequestProperty("referer", req);
		if (mCookieList != null && mCookieList.size() > 0) {
			mGetRedirectionConn.setRequestProperty("cookie", listToString(mCookieList));
		}
		mGetRedirectionConn.setUseCaches(false);
		mGetRedirectionConn.connect();
		switch (mGetRedirectionConn.getResponseCode()) {
			case HttpURLConnection.HTTP_OK:
				mCookieList = mCookieManager.getCookieStore().getCookies();
				break;
			case HttpURLConnection.HTTP_MOVED_TEMP:
				mCookieList = mCookieManager.getCookieStore().getCookies();
				break;
			default:
				throw new IOException("getOtherResponse Code: " + mGetRedirectionConn.getResponseCode());
		}
	}
	
	private int checkUrl() throws IOException {
		URL ConnUrl = new URL(mUrl);
		mCheckConn = (HttpURLConnection) ConnUrl.openConnection();
		mCheckConn.setRequestMethod("GET");
		mCheckConn.setConnectTimeout(CONN_TIMEOUT);
		mCheckConn.setReadTimeout(CONN_TIMEOUT);
		if (StringUtils.validate(mUser_agent)) {
			mCheckConn.setRequestProperty("user-agent", mUser_agent);
		}
		mCheckConn.setRequestProperty("accept", ACCEPT);
		mCheckConn.setRequestProperty("referer", mUrl);
		if (mCookieList != null && mCookieList.size() > 0) {
			mCheckConn.setRequestProperty("cookie", listToString(mCookieList));
		}
		mCheckConn.setUseCaches(false);
		mCheckConn.connect();
		return mCheckConn.getResponseCode();
	}
	
	private void closeAllConn() {
		if (mCheckConn != null) {
			mCheckConn.disconnect();
		}
		if (mGetMainConn != null) {
			mGetMainConn.disconnect();
		}
		if (mGetRedirectionConn != null) {
			mGetRedirectionConn.disconnect();
		}
	}
	
	public interface cfCallback {
		void onSuccess(List<HttpCookie> cookieList);
		
		void onFail();
	}
	
	private double get_answer(String str) { // ??
		double a = 0;
		
		try {
			List<String> s = regex(str, "var s,t,o,p,b,r,e,a,k,i,n,g,f, " + "(.+?)=\\{\"(.+?)\"");
			String varA = s.get(0);
			String varB = s.get(1);
			StringBuilder sb = new StringBuilder();
			sb.append("var a=");
			sb.append(regex(str, varA + "=\\{\"" + varB + "\":(.+?)\\}").get(0));
			sb.append(";");
			List<String> b = regex(str, varA + "\\." + varB + "(.+?)\\;");
			for (int i = 0; i < b.size() - 1; i++) {
				sb.append("a");
				sb.append(b.get(i));
				sb.append(";");
			}
			
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			engine.eval("print('Hello, World')");
			
			e("add", sb.toString());
			// V8 v8 = V8.createV8Runtime();
			Logger.info("executing double script with: " + sb.toString());
			a = (double) engine.eval(sb.toString());
			// a = v8.executeDoubleScript(sb.toString());
			List<String> fixNum = regex(str, "toFixed\\((.+?)\\)");
			if (fixNum != null) {
				a = Double.parseDouble(String.valueOf(engine.eval("String(" + String.valueOf(a) + ".toFixed(" + fixNum.get(0) + "));")));
			}
			a += new URL(mUrl).getHost().length();
			// v8.release();
			
		} catch (IndexOutOfBoundsException e) {
			e("answerErr", "get answer error");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return a;
	}
	
	/**
	 * ??
	 * 
	 * @param text
	 *            ??
	 * @param pattern
	 *            ???
	 * @return List<String>
	 */
	private List<String> regex(String text, String pattern) {
		try {
			Pattern pt = Pattern.compile(pattern);
			Matcher mt = pt.matcher(text);
			List<String> group = new ArrayList<>();
			
			while (mt.find()) {
				if (mt.groupCount() >= 1) {
					if (mt.groupCount() > 1) {
						group.add(mt.group(1));
						group.add(mt.group(2));
					} else
						group.add(mt.group(1));
				}
			}
			return group;
		} catch (NullPointerException e) {
			Logger.info("MATCH %s", "null");
		}
		return null;
	}
	
	/**
	 * ??list? ; ????????
	 * 
	 * @param list
	 * @return
	 */
	public static String listToString(List list) {
		char separator = ";".charAt(0);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i)).append(separator);
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}
	
	/**
	 * ???jsoup???Hashmap
	 * 
	 * @param list
	 *            HttpCookie??
	 * @return Hashmap
	 */
	public static Map<String, String> List2Map(List<HttpCookie> list) {
		Map<String, String> map = new HashMap<>();
		try {
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					String[] listStr = list.get(i).toString().split("=");
					map.put(listStr[0], listStr[1]);
				}
				Logger.info("List2Map %s", map.toString());
			} else {
				return map;
			}
			
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	private void e(String tag, String content) {
		Logger.error(tag + " %s", content);
	}
	
	private void e(String content) {
		Logger.error("cloudflare %s", content);
	}
	
}