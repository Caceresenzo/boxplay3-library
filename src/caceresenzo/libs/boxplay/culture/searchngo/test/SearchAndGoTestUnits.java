package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.logger.Logger;

/**
 * Basic Test Units
 * 
 * @author Enzo CACERES
 */
public class SearchAndGoTestUnits {
	
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
	
	public static class ExtractionTest {
		
		private static final String QUERY = "one piece";
		
		public static void main(String[] args) {
			// redirectConsoleOutput();
			List<SearchAndGoProvider> providers = new ArrayList<>();
			
			providers.add(ProviderManager.JETANIME.create());
			// providers.add(ProviderManager.MANGALEL.create());
			
			// Logger.info(ProviderManager.JETANIME.create().ADDITIONAL_DATA_CORRESPONDANCE);
			// Logger.info(ProviderManager.MANGALEL.create().ADDITIONAL_DATA_CORRESPONDANCE);
			
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
				
				// if (result.getParentProvider() instanceof JetAnimeSearchAndGoAnimeProvider) {
				Logger.$("");
				Logger.$("\tDATA:");
				
				List<AdditionalResultData> additionalResult = result.getParentProvider().fetchMoreData(result);
				for (AdditionalResultData additionalData : additionalResult) {
					Logger.$("\t- TYPE: %-20s, CONTENT: %s", additionalData.getType(), additionalData.convert());
				}
				// }
				
				Logger.$("");
				Logger.$("\tDATA:");
				
				List<AdditionalResultData> additionalContent = result.getParentProvider().fetchContent(result);
				for (AdditionalResultData additionalData : additionalContent) {
					Logger.$("\t- TYPE: %-20s, CONTENT: %s", additionalData.getType(), additionalData.convert());
					
					if (provider instanceof IVideoContentProvider && additionalData.getData() instanceof VideoItemResultData) {
						Logger.$("IVideoContentProvider: " + ((IVideoContentProvider) provider).extractVideoUrl((VideoItemResultData) additionalData.getData()));
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
			
			names.sort(new Comparator<String>() {
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
				System.out.println(String.format("enumCacheTranslation.put(ResultDataType.%s, boxPlayActivity.getString(R.string.boxplay_culture_searchngo_search_result_data_type_%s));", resultType.toString(), resultType.toString().toLowerCase()));
			}
		}
		
	}
	
}