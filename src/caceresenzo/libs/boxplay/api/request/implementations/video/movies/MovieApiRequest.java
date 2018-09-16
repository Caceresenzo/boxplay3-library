package caceresenzo.libs.boxplay.api.request.implementations.video.movies;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.request.ApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.MovieVideoStoreElement;

public class MovieApiRequest extends ApiRequest<MovieVideoStoreElement> {
	
	private int movieId;
	
	public MovieApiRequest(int movieId) {
		super("movies/%s");
		
		this.movieId = movieId;
	}
	
	@Override
	public String forge() {
		return String.format(urlFormat, movieId);
	}

	@Override
	public MovieVideoStoreElement processResponse(ApiResponse apiResponse) {
		
		return null;
	}
	
}
