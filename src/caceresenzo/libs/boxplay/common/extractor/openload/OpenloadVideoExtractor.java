package caceresenzo.libs.boxplay.common.extractor.openload;

import caceresenzo.libs.boxplay.common.extractor.IExtractor;

public abstract class OpenloadVideoExtractor implements IExtractor {
	
	public abstract String downloadTargetPage(String url);
	
	public abstract void notifyFail(Exception exception);
	
	public abstract void injectJsCode(String code);
	
	public abstract String getJsResult();
	
}