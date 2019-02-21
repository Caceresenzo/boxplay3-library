package caceresenzo.libs.boxplay.culture.searchngo.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;

/**
 * Class dedicated to sorting the workmap that will be use for display for display
 * 
 * @author Enzo CACERES
 */
public class ResultScoreSorter {
	
	/**
	 * Disabled constructor, static only
	 */
	private ResultScoreSorter() {
		;
	}
	
	/**
	 * Will clear and refill the workmap sorted, removing all under-score result.
	 * 
	 * @param workmap
	 *            Target workmap.
	 * @param query
	 *            Original query string.
	 * @param searchEngine
	 *            The provider {@link SearchEngine}.
	 * @throws IllegalArgumentException
	 *             If the <code>workmap</code> is not a {@link LinkedHashMap}.
	 */
	public static void sortWorkmap(final Map<String, SearchAndGoResult> workmap, String query, SearchEngine searchEngine) {
		if (!(workmap instanceof LinkedHashMap)) {
			throw new IllegalArgumentException("The workmap must be a LinkedHashMap or it will lost his order");
		}
		
		List<String> keys = new ArrayList<>();
		
		for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
			int score = searchEngine.applySearchStrategy(query, entry.getValue().getName());
			entry.getValue().score(score);
			
			if (searchEngine.searchStrategy().isScoreEnough(score)) {
				keys.add(entry.getKey());
			}
		}
		
		Collections.sort(keys, new Comparator<String>() {
			@Override
			public int compare(String key1, String key2) {
				SearchAndGoResult result1 = workmap.get(key1);
				SearchAndGoResult result2 = workmap.get(key2);
				
				/* Sort by score */
				int compare = result2.score() - result1.score();
				
				/* Score by length */
				if (compare == 0) {
					compare = result1.getName().length() - result2.getName().length();
				}
				
				/* Sort by name */
				if (compare == 0) {
					compare = result1.getName().compareTo(result2.getName());
				}
				
				return compare;
			}
		});
		
		Map<String, SearchAndGoResult> sortedMap = new LinkedHashMap<>();
		
		for (String key : keys) {
			sortedMap.put(key, workmap.get(key));
		}
		
		workmap.clear();
		workmap.putAll(sortedMap);
	}
	
}