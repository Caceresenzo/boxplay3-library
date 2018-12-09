package cloudflare;

import java.net.HttpCookie;
import java.util.List;

import caceresenzo.libs.logger.Logger;

public class BypassTester {
	
	public static void main(String[] args) {
		Cloudflare cloudflare = new Cloudflare("https://www.japscan.to/");
		
		cloudflare.setUser_agent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.80 Safari/537.36");
		
		cloudflare.getCookies(new Cloudflare.cfCallback() {
			@Override
			public void onSuccess(List<HttpCookie> cookieList) {
				Logger.info("success: " + cookieList);
			}
			
			@Override
			public void onFail() {
				Logger.info("onFail");
			}
		});
	}
	
}
