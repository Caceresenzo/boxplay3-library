package caceresenzo.libs.boxplay.api.test;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.BoxPlayApi;
import caceresenzo.libs.boxplay.api.request.implementations.video.movies.MoviesListApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.series.SeriesListApiRequest;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.test.SimpleTestUnits;

public class ApiTestUnits extends SimpleTestUnits {
	
	public static final BoxPlayApi boxPlayApi = new BoxPlayApi("hello_fab_fab");
	
	public static class DumpApiTest {
		
		public static void main(String[] args) {
			/* Movies */
			MoviesListApiRequest moviesListApiRequest = new MoviesListApiRequest();
			ApiResponse moviesListResponse = boxPlayApi.call(moviesListApiRequest);
			
			for (SimpleVideoStoreElement video : moviesListApiRequest.processResponse(moviesListResponse)) {
				$("+ ---------------------------");
				$("| ID: " + video.getId());
				$("| TITLE: " + video.getTitle());
				$("| IMAGE URL: " + video.getImageUrl());
			}
			
			$(moviesListResponse.getRawResponse());
			
			/* Series */
			SeriesListApiRequest seriesListApiRequest = new SeriesListApiRequest();
			ApiResponse seriesListResponse = boxPlayApi.call(seriesListApiRequest);
			
			for (SimpleVideoStoreElement video : seriesListApiRequest.processResponse(seriesListResponse)) {
				$("+ ---------------------------");
				$("| ID: " + video.getId());
				$("| TITLE: " + video.getTitle());
				$("| IMAGE URL: " + video.getImageUrl());
			}
			
			$(seriesListResponse.getRawResponse());
		}
		
	}
	
}