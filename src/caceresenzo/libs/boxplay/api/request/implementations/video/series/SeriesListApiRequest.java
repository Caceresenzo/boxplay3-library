package caceresenzo.libs.boxplay.api.request.implementations.video.series;

import caceresenzo.libs.boxplay.api.request.RequestSettings;
import caceresenzo.libs.boxplay.api.request.implementations.video.VideoListApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.bytes.bitset.LongBitSet;

public class SeriesListApiRequest extends VideoListApiRequest<SimpleVideoStoreElement> {
	
	public SeriesListApiRequest() {
		this(null);
	}
	
	public SeriesListApiRequest(RequestSettings requestSettings) {
		super("series", requestSettings);
	}
	
	@Override
	public SimpleVideoStoreElement createItem(long id, String title, String imageUrl, long tagsMask) {
		return new SimpleVideoStoreElement(id, title, imageUrl, new LongBitSet(tagsMask));
	}
	
}