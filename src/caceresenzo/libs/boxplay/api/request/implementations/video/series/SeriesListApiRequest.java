package caceresenzo.libs.boxplay.api.request.implementations.video.series;

import caceresenzo.libs.boxplay.api.request.implementations.video.VideoListApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;

public class SeriesListApiRequest extends VideoListApiRequest<SimpleVideoStoreElement> {
	
	public SeriesListApiRequest() {
		super("series");
	}

	@Override
	public SimpleVideoStoreElement createItem(long id, String title, String imageUrl) {
		return new SimpleVideoStoreElement(id, title, imageUrl);
	}
	
}