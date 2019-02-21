package caceresenzo.libs.boxplay.culture.searchngo.providers.implementations;

import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.AUTHORS;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.GENDERS;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.ITEM_CHAPTER;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.LAST_UPDATED;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.OTHER_NAME;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.RANK;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.RESUME;
import static caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType.THUMBNAIL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData.ChapterType;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.json.JsonArray;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.string.StringUtils;

public class MangaRockSearchAndGoMangaProvider extends SearchAndGoProvider implements IMangaContentProvider {
	
	/* Constants */
	public static final String API_VERSION = "401";
	public static final String API_BASE_URL = "https://api.mangarockhd.com/";
	public static final String API_QUERY_BASE_URL = API_BASE_URL + "query/web" + API_VERSION + "/";
	
	public static final String API_ENDPOINT_META = API_BASE_URL + "meta";
	public static final String API_ENDPOINT_SEARCH = API_QUERY_BASE_URL + "mrs_search";
	
	public static final String API_ENDPOINT_MANGA_FORMAT = API_QUERY_BASE_URL + "info?oid=%s";
	public static final String API_ENDPOINT_PAGES_FORMAT = API_QUERY_BASE_URL + "pages?oid=%s";
	
	/* Constructor */
	public MangaRockSearchAndGoMangaProvider() {
		super("Manga Rock", "https://mangarock.com");
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		Map<String, SearchAndGoResult> workmap = createEmptyWorkMap();
		
		JsonObject responseJsonObject;
		try {
			JsonObject bodyJsonObject = new JsonObject();
			bodyJsonObject.put("keywords", searchQuery);
			bodyJsonObject.put("type", "series");
			
			responseJsonObject = Webb.create().post(API_ENDPOINT_SEARCH) //
					.chromeUserAgent() //
					.ensureSuccess() //
					.body(bodyJsonObject.toJsonString()) //
					.asJsonObject().getBody() //
			;
			
			responseJsonObject = Webb.create().post(API_ENDPOINT_META) //
					.chromeUserAgent() //
					.ensureSuccess() //
					.body(responseJsonObject.get("data")) //
					.asJsonObject().getBody() //
			;
			
			ensureRequestSuccess(responseJsonObject);
		} catch (Exception exception) {
			return workmap;
		}
		
		JsonObject itemsMap = responseJsonObject.getJsonObject("data");
		for (Entry<Object, Object> entry : itemsMap.entrySet()) {
			String serieOId = (String) entry.getKey();
			JsonObject serieData = (JsonObject) entry.getValue();
			
			String name = serieData.getString("name");
			String url = String.format(API_ENDPOINT_MANGA_FORMAT, serieOId);
			String imageUrl = serieData.getString("thumbnail");
			
			int score = getHelper().getSearchEngine().applySearchStrategy(searchQuery, name);
			if (score != 0) {
				workmap.put(url, new SearchAndGoResult(this, name, url, imageUrl, SearchCapability.MANGA).score(score));
			}
		}
		
		return workmap;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		JsonObject json = getApiEndpoint(result.getUrl());
		if (json == null) {
			return additionals;
		}
		
		/* Author */
		String author = json.getString("author");
		if (StringUtils.validate(author)) {
			additionals.add(new AdditionalResultData(AUTHORS, author));
		}
		
		/* Rank */
		int rank = json.getInteger("rank", -1);
		if (rank != -1) {
			additionals.add(new AdditionalResultData(RANK, author));
		}
		
		/* Last Update */
		long lastUpdate = json.getLong("last_update", -1);
		if (rank != -1) {
			try {
				additionals.add(new AdditionalResultData(LAST_UPDATED, new SimpleDateFormat("dd-mm-yyyy hh:mm:ss").format(new Date(lastUpdate * 1000))));
			} catch (Exception exception) {
				; /* Date exception */
			}
		}
		
		/* Resume */
		String description = json.getString("description");
		if (StringUtils.validate(author)) {
			additionals.add(new AdditionalResultData(RESUME, description));
		}
		
		/* Other Name(s) */
		JsonArray aliasArray = json.getJsonArray("alias");
		if (aliasArray != null) {
			StringBuilder builder = new StringBuilder();
			
			Iterator<Object> iterator = aliasArray.iterator();
			
			while (iterator.hasNext()) {
				String alias = (String) iterator.next();
				
				builder.append(alias);
				
				if (iterator.hasNext()) {
					builder.append(", ");
				}
			}
			
			String alias = builder.toString();
			if (StringUtils.validate(alias)) {
				additionals.add(new AdditionalResultData(OTHER_NAME, alias));
			}
		}
		
		/* Another picture */
		String cover = json.getString("cover");
		if (StringUtils.validate(cover)) {
			additionals.add(new AdditionalResultData(THUMBNAIL, cover));
		}
		
		/* Genders */
		JsonArray genderJsonArray = json.getJsonArray("rich_categories");
		if (genderJsonArray != null) {
			List<CategoryResultData> categories = new ArrayList<>();
			
			for (Object object : genderJsonArray) {
				JsonObject dataJsonObject = (JsonObject) object;
				
				String name = dataJsonObject.getString("name");
				
				categories.add(new CategoryResultData(name));
			}
			
			if (!categories.isEmpty()) {
				additionals.add(new AdditionalResultData(GENDERS, categories));
			}
		}
		
		return additionals;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		List<AdditionalResultData> additionals = createEmptyAdditionalResultDataList();
		
		JsonObject json = getApiEndpoint(result.getUrl());
		if (json == null) {
			return additionals;
		}
		
		/* Chapter items */
		JsonArray itemJsonArray = json.getJsonArray("chapters");
		for (Object object : itemJsonArray) {
			JsonObject chapterJsonObject = (JsonObject) object;
			
			String objectId = chapterJsonObject.getString("oid");
			
			String url = String.format(API_ENDPOINT_PAGES_FORMAT, objectId);
			String name = chapterJsonObject.getString("name");
			
			additionals.add(new AdditionalResultData(ITEM_CHAPTER, new ChapterItemResultData(this, url, name, ChapterType.IMAGE_ARRAY)));
		}
		
		return additionals;
	}
	
	/**
	 * Fetch a API endpoint from its url and the result will be verified to avoid errors.
	 * 
	 * @param target
	 *            Target url to fetch.
	 * @return The "data" content of the item or null if anything goes wrong.
	 * @see #ensureRequestSuccess(JsonObject)
	 */
	private JsonObject getApiEndpoint(String target) {
		try {
			JsonObject responseJsonObject = (JsonObject) new JsonParser().parse(getHelper().downloadPageCache(target));
			
			ensureRequestSuccess(responseJsonObject);
			
			return responseJsonObject.getJsonObject("data");
		} catch (Exception exception) {
			return null;
		}
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return new ProviderSearchCapability( //
				ProviderSearchCapability.SearchCapability.MANGA //
		);
	}
	
	@Override
	public String extractMangaPageUrl(ChapterItemResultData chapterItemResult) {
		return chapterItemResult.getUrl();
	}
	
	/**
	 * Ensure the request success by checking the returned code.<br>
	 * If it's not 0, that mean that something fails.
	 * 
	 * @param response
	 *            Target {@link JsonObject} response to check.
	 * @throws IllegalStateException
	 *             If the returned code if not 0.
	 */
	public static void ensureRequestSuccess(JsonObject response) {
		int errorCode = response.getInteger("code");
		
		if (response.getInteger("code") != 0) {
			throw new IllegalStateException("Error " + errorCode + ": " + response.getString("data"));
		}
	}
	
}