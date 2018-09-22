package caceresenzo.libs.boxplay.culture.searchngo.test;

import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;

public class FakeOpenloadVideoExtractor extends OpenloadVideoExtractor {
	
	public FakeOpenloadVideoExtractor() {
		;
	}
	
	@Override
	public String downloadTargetPage(String url) {
		return null;
	}
	
	@Override
	public boolean checkStreamingAvailability(String html) {
		return false;
	}

	@Override
	public void injectJsCode(String html, String openloadHtml) {
		;
	}
	
	@Override
	public String getJsResult() {
		return null;
	}
	
	@Override
	public String getOpenloadKey(String htmlResult) {
		return null;
	}
	
}