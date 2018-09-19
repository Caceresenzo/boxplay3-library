package caceresenzo.libs.boxplay.api.test;

import java.util.List;

import caceresenzo.libs.boxplay.api.ApiResponse;
import caceresenzo.libs.boxplay.api.BoxPlayApi;
import caceresenzo.libs.boxplay.api.request.RequestSettings;
import caceresenzo.libs.boxplay.api.request.implementations.tags.TagsApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.movies.MovieApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.movies.MoviesListApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.series.SeriesApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.series.SeriesListApiRequest;
import caceresenzo.libs.boxplay.store.video.TagsCorresponder;
import caceresenzo.libs.boxplay.store.video.implementations.MovieVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.SeriesVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.series.SeriesSeasonVideoStoreElement;
import caceresenzo.libs.test.SimpleTestUnits;

public class ApiTestUnits extends SimpleTestUnits {
	
	public static final BoxPlayApi boxPlayApi = new BoxPlayApi("hello_fab_fab");
	
	public static class DumpApiTest {
		
		public static final boolean FETCH_MOVIES = true;
		public static final boolean FETCH_SERIES = false;
		
		public static void main(String[] args) {
			$("STARTING");
			
			TagsApiRequest tagsApiRequest = new TagsApiRequest();
			TagsCorresponder tagsCorresponder = tagsApiRequest.processResponse(boxPlayApi.call(tagsApiRequest));
			
			int index = 0;
			for (String correspondance : tagsCorresponder.getTagsCorrespondances()) {
				$("| Correspondance: " + correspondance + " >> " + index++);
			}
			
			/* Movies */
			if (FETCH_MOVIES) {
				MoviesListApiRequest moviesListApiRequest = new MoviesListApiRequest();
				ApiResponse moviesListResponse = boxPlayApi.call(moviesListApiRequest);
				
				List<SimpleVideoStoreElement> movies = moviesListApiRequest.processResponse(moviesListResponse);
				$("| --- MOVIES: " + movies.size());
				
				for (SimpleVideoStoreElement video : movies) {
					$("+ --------------------------- MOVIES");
					$("| ID: " + video.getId());
					$("| TITLE: " + video.getTitle());
					$("| TAGS: " + video.getTagsBitset().getMask());
					$("| IMAGE URL: " + video.getImageUrl());
					
					for (String tag : tagsCorresponder.findCorrespondances(video)) {
						$("| TAG >> " + tag);
					}
					
					MovieApiRequest movieApiRequest = new MovieApiRequest(video.getId());
					ApiResponse movieResponse = boxPlayApi.call(movieApiRequest);
					MovieVideoStoreElement movie = movieApiRequest.processResponse(movieResponse);
					
					$("| --- INSTANCE: " + (movie != null ? "VALID" : "NULL"));
					
					if (movie != null) {
						$("| GROUP: " + movie.getParentGroup());
						if (movie.getParentGroup() != null) {
							SimpleVideoStoreElement parentGroup = movie.getParentGroup();
							
							$("+ ------------ + ");
							$("| PARENT GROUP | ID: " + parentGroup.getId());
							$("| PARENT GROUP | TITLE: " + parentGroup.getTitle());
							$("| PARENT GROUP | IMAGE URL: " + parentGroup.getImageUrl());
							$("| PARENT GROUP | TAGS: " + parentGroup.getTagsBitset().getMask());
							$("+ ------------ + ");
						}
						
						$("| RELEASE DATE: " + movie.getReleaseDate());
						$("| RUNNING TIME: " + movie.getRunningTime());
						$("| FILE SIZE: " + movie.getFileSize());
						$("| STATUS: " + movie.getStatus());
						$("| URL: " + movie.getUrl());
					}
				}
				
				$(moviesListResponse.getRawResponse());
			}
			
			/* Series */
			if (FETCH_SERIES) {
				SeriesListApiRequest seriesListApiRequest = new SeriesListApiRequest(new RequestSettings.Builder().include(1).search("ni").build());
				ApiResponse seriesListResponse = boxPlayApi.call(seriesListApiRequest);
				
				List<SimpleVideoStoreElement> seriesList = seriesListApiRequest.processResponse(seriesListResponse);
				$("| --- SERIES: " + seriesList.size());
				
				for (SimpleVideoStoreElement video : seriesList) {
					$("+ --------------------------- SERIES");
					$("| ID: " + video.getId());
					$("| TITLE: " + video.getTitle());
					$("| IMAGE URL: " + video.getImageUrl());
					
					for (String tag : tagsCorresponder.findCorrespondances(video)) {
						$("| TAG >> " + tag);
					}
					
					SeriesApiRequest seriesApiRequest = new SeriesApiRequest(video.getId());
					ApiResponse serieResponse = boxPlayApi.call(seriesApiRequest);
					SeriesVideoStoreElement series = seriesApiRequest.processResponse(serieResponse);
					
					$("| --- INSTANCE: " + (series != null ? "VALID" : "NULL"));
					
					if (series != null) {
						$("| HAS SEASONS?: " + series.hasSeasons());
						
						if (series.hasSeasons()) {
							for (SeriesSeasonVideoStoreElement season : series.getSeasons()) {
								$("+ ------ +");
								$("| SEASON | ID: " + season.getId());
								$("| SEASON | TITLE: " + season.getTitle());
								$("| SEASON | IMAGE URL: " + season.getImageUrl());
							}
							$("+ ------ +");
						}
					}
				}
				
				$(seriesListResponse.getRawResponse());
			}
		}
		
	}
	
}