package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.InternetSource;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaLelChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVidozaVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.OpenloadVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.callback.ProviderSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.iterator.ByteArrayIterator;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;

/**
 * Basic Test Units
 * 
 * @author Enzo CACERES
 */
public class SearchAndGoTestUnits {
	
	private static final Map<Class<? extends ContentExtractor>, ContentExtractor> EXTRACTORS = new HashMap<>();
	
	static {
		/* Video */
		EXTRACTORS.put(OpenloadVideoExtractor.class, new FakeOpenloadVideoExtractor());
		EXTRACTORS.put(GenericVidozaVideoExtractor.class, new GenericVidozaVideoExtractor());
		
		/* Manga */
		EXTRACTORS.put(GenericMangaLelChapterExtractor.class, new GenericMangaLelChapterExtractor());
	}
	
	public static void main(String[] args) {
		;
	}
	
	public static void redirectConsoleOutput() {
		try {
			System.setOut(new PrintStream(new FileOutputStream(new File("info.log")), true, "UTF-8"));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static ContentExtractor getExtractorFromBaseUrl(String baseUrl) {
		if (baseUrl == null) {
			return null;
		}
		
		for (Entry<Class<? extends ContentExtractor>, ContentExtractor> entry : EXTRACTORS.entrySet()) {
			InternetSource internetSource = entry.getValue();
			
			if (internetSource.matchUrl(baseUrl)) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	public static class ExtractionTest {
		
		private static final String QUERY = "isekai desu ga";
		
		public static void main(String[] args) {
			
			ProviderCallback.registerProviderSearchallback(new ProviderSearchCallback() {
				@Override
				public void onProviderSorting(SearchAndGoProvider provider) {
					;
				}
				
				@Override
				public void onProviderSearchStarting(SearchAndGoProvider provider) {
					;
				}
				
				@Override
				public void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
					;
				}
				
				@Override
				public void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
					exception.printStackTrace();
				}
			});
			
			// redirectConsoleOutput();
			List<SearchAndGoProvider> providers = new ArrayList<>();
			
			// providers.add(ProviderManager.JETANIME.create());
			// providers.add(ProviderManager.VOIRFILM_BZ.create());
			// providers.add(ProviderManager.MANGALEL.create());
			// providers.add(ProviderManager.ADKAMI.create());
			providers.add(ProviderManager.SCANMANGA.create());
			// providers.add(ProviderManager.FULLSTREAM_NU.create());
			
			final List<SearchAndGoResult> results = new ArrayList<>();
			
			try {
				for (SearchAndGoProvider provider : providers) {
					Map<String, SearchAndGoResult> workmap = provider.work(QUERY);
					
					ResultScoreSorter.sortWorkmap(workmap, QUERY, provider.getHelper().getSearchEngine());
					
					for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
						results.add(entry.getValue());
						Logger.info("------------------------------------------------------------ %s %s", entry.getValue().score(), entry.getValue().getName());
					}
				}
				
				for (SearchAndGoResult searchAndGoResult : results) {
					Logger.info("type: %-10s - name: %-60s - score: %s", searchAndGoResult.getType(), searchAndGoResult.getName(), searchAndGoResult.score());
				}
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
			
			for (SearchAndGoResult result : results) {
				SearchAndGoProvider provider = result.getParentProvider();
				
				Logger.$(result.getType() + " [search score: %s]", result.score());
				Logger.$("\t" + result.getUrl());
				Logger.$("\t" + result.getName());
				Logger.$("\t" + result.getBestImageUrl());
				
				/* Data */
				Logger.$("");
				Logger.$("\tDATA:");
				
				List<AdditionalResultData> additionalResult = result.getParentProvider().fetchMoreData(result);
				for (AdditionalResultData additionalData : additionalResult) {
					Logger.$("\t- %-20s >> %s", additionalData.getType(), additionalData.convert());
				}
				
				/* Content */
				Logger.$("");
				Logger.$("\tCONTENT:");
				
				List<AdditionalResultData> additionalContent = result.getParentProvider().fetchContent(result);
				for (AdditionalResultData additionalData : additionalContent) {
					Logger.$("\t- %-20s >> %s", additionalData.getType(), additionalData.convert());
					
					if (provider instanceof IVideoContentProvider && additionalData.getData() instanceof VideoItemResultData) {
						String[] urls = ((IVideoContentProvider) provider).extractVideoPageUrl((VideoItemResultData) additionalData.getData());
						
						for (String url : urls) {
							Logger.$("\t\tIVideoContentProvider: " + url);
							
							ContentExtractor contentExtractor = getExtractorFromBaseUrl(url);
							
							Logger.$("\t\t | -> %s", contentExtractor != null ? contentExtractor.getClass().getSimpleName() : "NO_COMPATIBLE_PROVIDER");
						}
						
						Logger.$("");
					} else if (provider instanceof IMangaContentProvider && additionalData.getData() instanceof ChapterItemResultData) {
						Logger.$("IMangaContentProvider: " + ((IMangaContentProvider) provider).extractMangaPageUrl((ChapterItemResultData) additionalData.getData()));
						Logger.$("");
						
						for (String url : new GenericMangaLelChapterExtractor().getImageUrls(((IMangaContentProvider) provider).extractMangaPageUrl((ChapterItemResultData) additionalData.getData()))) {
							Logger.$(" |- Image URL: " + url);
						}
						
						Logger.$("");
					}
				}
				
				Logger.$(" ------------------------------------- ");
				
				// System.out.println(String.format("names.add(\"%s\");", result.getName()));
				
				// System.exit(0);
			}
			
			Logger.$("    ");
			Logger.$("size: " + results.size());
			Logger.$("    ");
			Logger.$("Providers: " + providers);
		}
		
	}
	
	public static class SearchTest {
		
		private static final String QUERY = "tomo";
		
		public static void main(String[] args) {
			SearchEngine searchEngine = new SearchEngine();
			
			List<String> names = new ArrayList<>();
			
			/**
			 * name.add("string to search");
			 * 
			 * ...
			 */
			
			final Map<String, Integer> scoreMap = new HashMap<>();
			
			for (String string : names) {
				int score = searchEngine.applySearchStrategy(QUERY, string);
				
				scoreMap.put(string, score);
			}
			
			Collections.sort(names, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					int compare = scoreMap.get(o2) - scoreMap.get(o1); // Sort by score
					
					if (compare == 0) {
						compare = o1.length() - o2.length(); // Score by length
					}
					
					if (compare == 0) {
						compare = o2.compareTo(o1); // Sort by name
					}
					
					return compare;
				}
			});
			
			for (String string : names) {
				int score = scoreMap.get(string);
				
				if (score <= 4) {
					continue;
				}
				
				Logger.info("QUERY: %-20s | STRING: %-50s | SCORE: %s", QUERY, string, scoreMap.get(string));
			}
		}
		
	}
	
	public static class AndroidI18nExporter {
		
		public static void main(String[] args) {
			for (AdditionalDataType resultType : AdditionalDataType.values()) {
				System.out.println(String.format("<string name=\"boxplay_culture_searchngo_search_result_data_type_%s\">%s</string>", resultType.toString().toLowerCase(), resultType.toString()));
			}
			
			for (AdditionalDataType resultType : AdditionalDataType.values()) {
				System.out.println(String.format("enumCacheTranslation.put(%s.%s, boxPlayActivity.getString(R.string.boxplay_culture_searchngo_search_result_data_type_%s));", resultType.getClass().getSimpleName(), resultType.toString(), resultType.toString().toLowerCase()));
			}
		}
		
	}
	
	public static class AdkamiSandboxTestUnit {
		
		public static void main(String[] args) {
			
			// redirectConsoleOutput();
			
			Sandbox<String, String> sandbox = new Sandbox<String, String>() {
				@Override
				public String execute(String baseUrl) {
					String[] split = baseUrl.split("https://www.youtube.com/embed/");
					
					if (split.length < 2) {
						return null;
					}
					
					baseUrl = split[1];
					byte[] decodedBytes = Base64.decodeFast(baseUrl);
					String result = "", key = "ETEfazefzeaZa13MnZEe";
					int index = 0;
					
					try {
						ByteArrayIterator iterator = new ByteArrayIterator(decodedBytes);
						while (iterator.hasNext()) {
							// int nextByte = Byte.toUnsignedInt(iterator.next()); // Too advanced for older phone
							int nextByte = iterator.next() & 0xFF; // Basicly un-sign actual byte value, thanks stackoverflow
							result += (char) ((175 ^ nextByte) - (int) key.charAt(index));
							index = index > key.length() - 2 ? 0 : index + 1;
						}
					} catch (Exception exception) {
						return null;
					}
					
					return result;
				}
			};
			
			Logger.info(sandbox.execute("https://www.youtube.com/embed/2ywbeWlHfnp0ZiASf883FX8QBjvSCdNwfGAJcUEWDQ=="));
		}
		
	}
	
	public static class RandomCodeTest {
		
		public static void main(String[] args) {
			String categoriesString = ".Drame.Fantastique";
			
			String[] genders = categoriesString.split("\\.");
			
			List<CategoryResultData> categories = new ArrayList<>();
			
			for (String gender : genders) {
				Logger.error("FOUND : " + gender);
				if (!StringUtils.validate(genders)) {
					continue;
				}
				Logger.error("-- VALIDATED: " + gender);
				
				categories.add(new CategoryResultData(gender));
			}
			
			// if (!categories.isEmpty()) {
			// additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
			// }
		}
		
	}
	
}