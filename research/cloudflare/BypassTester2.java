package cloudflare;

import java.net.HttpCookie;
import java.util.List;

import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.reversing.cloudflare.CloudflareBypass;

public class BypassTester2 {
	
	public static void main(String[] args) {
		new CloudflareBypass("https://www.japscan.to/", new CloudflareBypass.Callback() {
			@Override
			public void onSuccess(List<HttpCookie> cookieList) {
				Logger.info("success: " + cookieList);
				
				Logger.info(Webb.create().get("https://www.japscan.to/").header(Webb.HDR_USER_AGENT, Webb.DEFAULT_USER_AGENT).header("cookie", CloudflareBypass.listToString(cookieList)).asString().getBody());
			}
			
			@Override
			public void onFail() {
				Logger.info("onFail");
			}
			
			@Override
			public void onException(Exception exception) {
				
			}
		}).extract();
	}
	
}
