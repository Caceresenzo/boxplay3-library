package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.ITEM_VIDEO;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.RELEASE_DATE;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.RESUME;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.STATUS;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.THUMBNAIL;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;

import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.providers.exceptions.implementations.ProviderFetchFailedException;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.list.ListUtils;
import caceresenzo.libs.string.StringUtils;

public class NekoSamaSearchAndGoVideoProvider extends SearchAndGoProvider implements IVideoContentProvider {
	
	/* Serialization */
	private static final long serialVersionUID = 1L;
	
	/* Constants */
	public static final byte SCORE_MAX = 5;
	
	/* Json Keys */
	public static final String JSON_KEY_ITEM_NAME = "title";
	public static final String JSON_KEY_ITEM_URL = "url";
	public static final String JSON_KEY_ITEM_IMAGE_URL = "url_image";
	public static final String JSON_KEY_ITEM_TYPE = "type";
	public static final String JSON_KEY_ITEM_SCORE = "score";
	public static final String JSON_KEY_ITEM_EPISODE_NUMBER = "nb_eps";
	
	public static final String JSON_KEY_VIDEO_ITEM_PAGE_URL = "url";
	public static final String JSON_KEY_VIDEO_ITEM_EPISODE = "episode";
	public static final String JSON_KEY_VIDEO_ITEM_THUMBNAIL_IMAGE_URL = "url_image";
	public static final String JSON_KEY_VIDEO_ITEM_DURATION = "time";
	
	/* Variables */
	private final String animeListJsonUrl;
	private final String defaultBackgroundImageUrl;
	
	/* Constructor */
	public NekoSamaSearchAndGoVideoProvider() {
		super("NEKO-SAMA", "https://www.neko-sama.fr");
		
		this.animeListJsonUrl = getSiteUrl() + "/animes-search.json?hkhotothktohknnnn";
		this.defaultBackgroundImageUrl = getSiteUrl() + "/images/default_background.png";
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		
		selectMapIndex(MAP_INDEX_NORMAL);
		registerCorrespondence(TYPE, "Type");
		registerCorrespondence(STATUS, "Status");
		registerCorrespondence(RELEASE_DATE, "Diffusion");
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability( //
				ProviderSearchCapability.SearchCapability.ANIME, //
				ProviderSearchCapability.SearchCapability.VIDEO //
		);
	}
	
	@Override
	public boolean canExtractEverythingOnce() {
		return true;
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		JsonArray array;
		try {
			String json = getHelper().downloadPageCache(animeListJsonUrl);
			
			array = JsonArray.class.cast(new JsonParser().parse(json));
		} catch (Exception exception) {
			throw new ProviderFetchFailedException(exception);
		}
		
		for (Object object : array) {
			JsonObject item = (JsonObject) object;
			
			String name = item.getString(JSON_KEY_ITEM_NAME);
			String url = getSiteUrl() + item.getString(JSON_KEY_ITEM_URL);
			String imageUrl = item.getString(JSON_KEY_ITEM_IMAGE_URL);
			String type = item.getString(JSON_KEY_ITEM_TYPE);
			
			SearchCapability itemType;
			switch (type.toLowerCase()) {
				case "movie": {
					itemType = SearchCapability.ANIMEMOVIE;
					break;
				}
				
				case "tv":
				case "ova":
				case "ona":
				case "special":
				default: {
					itemType = SearchCapability.ANIME;
					break;
				}
			}
			
			String episodeNumber = item.getString(JSON_KEY_ITEM_EPISODE_NUMBER);
			
			StringBuilder descriptionBuilder = new StringBuilder();
			switch (itemType) {
				case ANIMEMOVIE: {
					descriptionBuilder.append(episodeNumber); /* Display: "Film" */
					break;
				}
				
				case ANIME: {
					descriptionBuilder.append(String.format("%S (%s)", type, episodeNumber));
					break;
				}
				
				default: {
					break;
				}
			}
			
			List<String> categories = new ArrayList<>();
			{
				ArrayList<Object> keys = new ArrayList<>(item.keySet());
				for (int i = keys.size() - 1; i >= 0; i--) {
					Object key = keys.get(i);
					Object value = item.get(key);
					
					if (String.valueOf(value).equals("1")) {
						categories.add(String.valueOf(key));
					} else {
						break;
					}
				}
			}
			if (!categories.isEmpty()) {
				descriptionBuilder.append("\n").append(ListUtils.separate(categories));
			}
			
			String description = descriptionBuilder.toString();
			if (!StringUtils.validate(description)) {
				description = null;
			}
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				workmap.put(url, new SearchAndGoResult(this, name, url, imageUrl, itemType) //
						.score(score) //
						.describe(description) //
				);
			}
		}
		
		return workmap;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		String html;
		try {
			html = getHelper().downloadPageCache(Objects.requireNonNull(result.getUrl()));
		} catch (Exception exception) {
			throw new ProviderFetchFailedException(exception);
		}
		
		/* Common */
		for (Entry<AdditionalDataType, String> entry : getCorrespondenceMap(MAP_INDEX_NORMAL).entrySet()) {
			AdditionalDataType type = entry.getKey();
			String correspondence = entry.getValue();
			
			String extractedCommonData = getHelper().extract(String.format("<div class=\"item\">[\\s]*<small>%s<\\/small>[\\s]*(.*?)[\\s]*<\\/div>", correspondence), html);
			if (StringUtils.validate(extractedCommonData)) {
				additionals.add(new AdditionalResultData(type, extractedCommonData));
			}
		}
		
		/* Another picture */
		String extractedPicture = getHelper().extract("<div id=\"head\" style=\"background-image: url\\((.*?)\\);.*?\">", html);
		if (StringUtils.validate(extractedPicture) && !extractedPicture.equalsIgnoreCase(defaultBackgroundImageUrl)) {
			additionals.add(new AdditionalResultData(THUMBNAIL, extractedPicture));
		}
		
		/* Resume */
		String extractedResume = getHelper().extract("<div class=\"synopsis\">[\\s]*<p>[\\s]*(.*?)[\\s]*<\\/p>[\\s]*<\\/div>", html);
		if (StringUtils.validate(extractedResume)) {
			additionals.add(new AdditionalResultData(RESUME, extractedResume));
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		JsonArray array;
		try {
			String html = getHelper().downloadPageCache(result.getUrl());
			String extractedJson = getHelper().extract("var episodes = (.*?);[\\s]", html);
			
			array = JsonArray.class.cast(new JsonParser().parse(extractedJson));
		} catch (Exception exception) {
			throw new ProviderFetchFailedException(exception);
		}
		
		for (Object object : array) {
			JsonObject item = (JsonObject) object;
			
			String url = getSiteUrl() + item.getString(JSON_KEY_VIDEO_ITEM_PAGE_URL);
			String episode = item.getString(JSON_KEY_VIDEO_ITEM_EPISODE);
			String thumbnailImageUrl = item.getString(JSON_KEY_VIDEO_ITEM_THUMBNAIL_IMAGE_URL);
			String duration = item.getString(JSON_KEY_VIDEO_ITEM_DURATION);
			
			String itemTitle = episode;
			
			additionals.add(new AdditionalResultData(ITEM_VIDEO, new VideoItemResultData(this, url, itemTitle) //
					.thumbnail(thumbnailImageUrl) //
					.duration(duration) //
			));
		}
		
		return additionals;
	}
	
	@Override
	public String[] extractVideoPageUrl(VideoItemResultData videoItemResult) {
		List<String> urls = new ArrayList<>();
		
		String html;
		try {
			html = getHelper().downloadPageCache(Objects.requireNonNull(videoItemResult.getUrl()));
		} catch (Exception exception) {
			throw new ProviderFetchFailedException(exception);
		}
		
		String javaScriptCode = getHelper().extract("<script type=\"text\\/javascript\">[\\s]*var video = \\[\\];.*?\\} else \\{(.*?)\\}", html);
		
		Matcher playerMatcher = getHelper().regex("video\\[.*?\\] = '(.*?)';", javaScriptCode);
		while (playerMatcher.find()) {
			String playerUrl = playerMatcher.group(1);
			
			urls.add(playerUrl);
		}
		
		return urls.toArray(new String[urls.size()]);
	}
	
	@Override
	public boolean hasMoreThanOnePlayer() {
		return true;
	}
	
}