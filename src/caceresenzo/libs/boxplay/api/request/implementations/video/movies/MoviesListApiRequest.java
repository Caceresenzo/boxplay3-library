package caceresenzo.libs.boxplay.api.request.implementations.video.movies;

import caceresenzo.libs.boxplay.api.request.RequestSettings;
import caceresenzo.libs.boxplay.api.request.implementations.video.VideoListApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.bytes.bitset.LongBitSet;

public class MoviesListApiRequest extends VideoListApiRequest<SimpleVideoStoreElement> {
	
	public MoviesListApiRequest() {
		this(null);
	}
	
	public MoviesListApiRequest(RequestSettings requestSettings) {
		super("movies", requestSettings);
	}
	
	@Override
	public SimpleVideoStoreElement createItem(long id, String title, String imageUrl, long tagsMask) {
		return new SimpleVideoStoreElement(id, title, imageUrl, new LongBitSet(tagsMask));
	}
	
}