package caceresenzo.libs.boxplay.culture.searchngo.callback;

import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;

public interface ContentProviderCallback {
	
	void onContentExtractionStarting();
	
	void onContentExtractionFinished();
	
	public static interface ImageContentCallback extends ContentProviderCallback {
		
	}
	
	public static interface VideoContentProviderCallback extends ContentProviderCallback {
		
		void onVideoUrlExtracted(VideoItemResultData videoResult, String extractedUrl);
		
	}
	
}