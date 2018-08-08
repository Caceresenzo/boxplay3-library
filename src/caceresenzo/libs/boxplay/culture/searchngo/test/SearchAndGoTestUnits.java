package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public static class ExtractionTest {
		
		private static final String QUERY = "tomo";
		
		public static void main(String[] args) {
			List<SearchAndGoProvider> providers = new ArrayList<>();
			
			providers.add(ProviderManager.JETANIME.create());
			// providers.add(ProviderManager.MANGALEL.create());
			
			final List<SearchAndGoResult> results = new ArrayList<>();
			
			try {
				for (SearchAndGoProvider provider : providers) {
					Map<String, SearchAndGoResult> workmap = provider.work(QUERY);
					
					ResultScoreSorter.sortWorkmap(workmap, QUERY, provider.getHelper().getSearchEngine());
					
					for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
						results.add(entry.getValue());
						// resultsNames.add(entry.getValue().getName());
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
				Logger.$(result.getType() + " [%s]", result.score());
				Logger.$(" " + result.getUrl());
				Logger.$(" " + result.getName());
				Logger.$(" " + result.getBestImageUrl());
				
				// System.out.println(String.format("names.add(\"%s\");", result.getName()));
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
	
}