package caceresenzo.libs.boxplay.api.test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import caceresenzo.libs.boxplay.api.BoxPlayApi;
import caceresenzo.libs.boxplay.api.request.RequestSettings;
import caceresenzo.libs.boxplay.api.request.implementations.tags.TagsApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.user.identification.UserLoginApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.movies.MovieApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.movies.MoviesListApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.series.SeriesApiRequest;
import caceresenzo.libs.boxplay.api.request.implementations.video.series.SeriesListApiRequest;
import caceresenzo.libs.boxplay.api.response.ApiResponse;
import caceresenzo.libs.boxplay.api.response.ApiResponseStatus;
import caceresenzo.libs.boxplay.store.video.TagsCorresponder;
import caceresenzo.libs.boxplay.store.video.implementations.MovieVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.SeriesVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.SimpleVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.series.SeriesSeasonVideoStoreElement;
import caceresenzo.libs.boxplay.users.User;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.test.SimpleTestUnits;

public class ApiTestUnits extends SimpleTestUnits {
	
	public static final BoxPlayApi boxPlayApi = new BoxPlayApi("hello_fab_fab");
	
	private static TagsCorresponder tagsCorresponder;
	
	public static void initializeCorresponder() {
		tagsCorresponder = new TagsApiRequest().call(boxPlayApi).selfProcess();
		
		if (tagsCorresponder == null) {
			throw new IllegalStateException("Corresponder is null");
		}
	}
	
	public static class DumpApiTest {
		
		public static final boolean FETCH_MOVIES = false;
		public static final boolean FETCH_SERIES = true;
		
		public static void main(String[] args) {
			$("STARTING");
			
			initializeCorresponder();
			
			int index = 0;
			for (String correspondance : tagsCorresponder.getTagsCorrespondances()) {
				$("| Correspondance: " + correspondance + " >> " + index++);
			}
			
			/* Movies */
			if (FETCH_MOVIES) {
				List<SimpleVideoStoreElement> movies = new MoviesListApiRequest().call(boxPlayApi).selfProcess();
				$("| --- MOVIES: " + movies.size());
				
				for (SimpleVideoStoreElement video : movies) {
					$("+ --------------------------- MOVIES");
					$("| ID: " + video.getId());
					$("| TITLE: " + video.getTitle());
					$("| TAGS: " + video.getTagsBitset().getValue());
					$("| IMAGE URL: " + video.getImageUrl());
					
					for (String tag : tagsCorresponder.findCorrespondances(video)) {
						$("| TAG >> " + tag);
					}
					
					MovieVideoStoreElement movie = new MovieApiRequest(video.getId()).call(boxPlayApi).selfProcess();
					
					$("| --- INSTANCE: " + (movie != null ? "VALID" : "NULL"));
					
					if (movie != null) {
						$("| GROUP: " + movie.getParentGroup());
						if (movie.getParentGroup() != null) {
							SimpleVideoStoreElement parentGroup = movie.getParentGroup();
							
							$("+ ------------ + ");
							$("| PARENT GROUP | ID: " + parentGroup.getId());
							$("| PARENT GROUP | TITLE: " + parentGroup.getTitle());
							$("| PARENT GROUP | IMAGE URL: " + parentGroup.getImageUrl());
							$("| PARENT GROUP | TAGS: " + parentGroup.getTagsBitset().getValue());
							$("+ ------------ + ");
						}
						
						$("| RELEASE DATE: " + movie.getReleaseDate());
						$("| RUNNING TIME: " + movie.getRunningTime());
						$("| FILE SIZE: " + movie.getFileSize());
						$("| STATUS: " + movie.getStatus());
						$("| URL: " + movie.getUrl());
					}
				}
			}
			
			/* Series */
			if (FETCH_SERIES) {
				List<SimpleVideoStoreElement> seriesList = new SeriesListApiRequest(new RequestSettings.Builder().include(1).search("kono").build()).call(boxPlayApi).selfProcess();
				$("| --- SERIES: " + seriesList.size());
				
				for (SimpleVideoStoreElement video : seriesList) {
					$("+ --------------------------- SERIES");
					$("| ID: " + video.getId());
					$("| TITLE: " + video.getTitle());
					$("| IMAGE URL: " + video.getImageUrl());
					$("| TAG: " + video.getTagsBitset().getValue());
					
					for (String tag : tagsCorresponder.findCorrespondances(video)) {
						$("| TAG >> " + tag);
					}
					
					SeriesVideoStoreElement series = new SeriesApiRequest(video.getId()).call(boxPlayApi).selfProcess();
					
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
			}
		}
		
	}
	
	public static class IdentificationApiTest {
		
		public static void main(String[] args) {
			$("STARTING");
			
			ApiResponse<User> userResponse = new UserLoginApiRequest("thewhooshe", "placeholder").call(boxPlayApi);
			$(userResponse.getStatus());
			$(userResponse.getRawResponse());
			$(userResponse.selfProcess());
		}
		
	}
	
	public static class TagsCompilerTest {
		
		public static void main(String[] args) {
			$("STARTING");
			
			initializeCorresponder();
			
			String inputTags = "Adventure\r\n" + "Comedy\r\n" + "Fantasy\r\n" + "Supernatural\r\n" + "Other world\r\n" + "fights\r\n" + "Gods\r\n" + "demons\r\n" + "Parody";
			
			List<String> formattedTags = new ArrayList<>();
			for (String tag : inputTags.toUpperCase().split("[\\n\\r\\,\\-]")) {
				tag = tag.trim();
				
				if (!StringUtils.validate(tag)) {
					continue;
				}
				
				formattedTags.add(tag);
				
				if (tagsCorresponder.findIndexByName(tag) == TagsCorresponder.NO_TAG) {
					$("Not found tag: " + tag);
				}
				
				$(tagsCorresponder.findIndexByName(tag) + " -->> " + tag);
			}
			
			List<Integer> indexes = tagsCorresponder.getIndexesByName(formattedTags);
			$(indexes);
			
			BigIntegerBitSet bitSet = new BigIntegerBitSet(BigInteger.ONE);
			for (int index : indexes) {
				bitSet.set(index, true);
			}
			
			$("Value: " + bitSet.getValue().toString(16));
		}
		
	}
	
	public static class CsvTagsValueConverter {
		
		public static void main(String[] args) throws IOException {
			$("STARTING");
			
			String csv = StringUtils.fromFile("bp_group.csv").replace("\"", "");
			
			String sql = "UPDATE `bp_group` SET `tags` = 0x%s WHERE `bp_group`.`id` = %s;";
			
			for (String line : csv.split("\n")) {
				String[] items = line.split(",");
				
				// $(items[4]);
				BigInteger bigInteger = new BigInteger(items[4], 10);
				
				$(String.format(sql, bigInteger.toString(16), items[0]));
			}
		}
		
	}
	
	public static class AndroidI18nExporter {
		
		public static void main(String[] args) {
			$("STARTING");
			
			for (ApiResponseStatus status : ApiResponseStatus.values()) {
				System.out.println(String.format("<string name=\"boxplay_identification_response_error_%s\">%s</string>", status.toString().toLowerCase(), status.toString()));
			}
			
			for (ApiResponseStatus status : ApiResponseStatus.values()) {
				System.out.println(String.format("enumCacheTranslation.put(%s.%s, boxPlayApplication.getString(R.string.boxplay_identification_response_error_%s));", status.getClass().getSimpleName(), status.toString(), status.toString().toLowerCase()));
			}
		}
		
	}
	
}