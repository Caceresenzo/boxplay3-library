package caceresenzo.libs.boxplay.api.request.implementations.video.movies;

import caceresenzo.libs.boxplay.api.request.implementations.video.VideoListApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;

public class MoviesListApiRequest extends VideoListApiRequest<SimpleVideoStoreElement> {
	
	public MoviesListApiRequest() {
		super("movies");
	}
	
	@Override
	public SimpleVideoStoreElement createItem(long id, String title, String imageUrl) {
		return new SimpleVideoStoreElement(id, title, imageUrl);
	}
	
}