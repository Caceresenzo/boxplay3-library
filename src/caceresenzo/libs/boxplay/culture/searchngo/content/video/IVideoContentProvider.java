package caceresenzo.libs.boxplay.culture.searchngo.content.video;

import caceresenzo.libs.boxplay.culture.searchngo.content.IContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;

public interface IVideoContentProvider extends IContentProvider {
	
	String extractVideoUrl(VideoItemResultData videoItemResult);
	
}