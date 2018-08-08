package caceresenzo.libs.boxplay.culture.searchngo.search;

/**
 * Search engine, used by Provider to sort result
 * 
 * @author Enzo CACERES
 */
public class SearchEngine {
	
	private SearchStrategy searchStrategy;
	
	/**
	 * Create a new instance of a SearchEngine but with the default {@link SearchStrategy} referenced here {@link SearchStrategyManager#getDefaultSearchStrategy()}
	 */
	public SearchEngine() {
		this(SearchStrategyManager.getDefaultSearchStrategy().create());
	}
	
	/**
	 * Create a new instance but with a specific {@link SearchStrategy}
	 * 
	 * @param searchStrategy
	 *            Your instance
	 */
	public SearchEngine(SearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}
	
	/**
	 * Get the actual search engine strategy
	 * 
	 * @return Actual strategy
	 */
	public SearchStrategy searchStrategy() {
		return searchStrategy;
	}
	
	/**
	 * Apply a new search stragety
	 * 
	 * @param searchStrategy
	 *            New instance
	 */
	public void searchStrategy(SearchStrategy searchStrategy) {
		this.searchStrategy = searchStrategy;
	}
	
	/**
	 * Apply the search strategy to a string
	 * 
	 * @param query
	 *            Search string
	 * @param string
	 *            Search source
	 * @return Searching score
	 */
	public int applySearchStrategy(String query, String string) {
		if (query == null || query.isEmpty() || string == null || string.isEmpty()) {
			return 0;
		}
		
		return searchStrategy.validateSearchQuery(query, string);
	}
	
}