package caceresenzo.libs.boxplay.culture.searchngo.search;

import java.io.Serializable;

/**
 * A class to allow implementations of searching strategy
 * 
 * @author Enzo CACERES
 */
public abstract class SearchStrategy implements Serializable {
	
	/**
	 * Abstract function used by implementations
	 * 
	 * It is used to execute the search strategy
	 * 
	 * @param query
	 *            Search string
	 * @param string
	 *            Thing like display name
	 * @return A score for your result
	 */
	public abstract int validateSearchQuery(String query, String string);
	
	/**
	 * Get theorical {@link ProcessingSpeed} for this {@link SearchStrategy}
	 * 
	 * @return A theorical {@link ProcessingSpeed}
	 */
	public abstract SearchStrategyManager.ProcessingSpeed getProcessingSpeed();
	
	/**
	 * Abstract function used by implementations
	 * 
	 * It help the sort algorythm to valid an item or not if this score is higher
	 * 
	 * if (result.getScore() <= getHighestInvalidScore()) { ignored; }
	 * 
	 * @return An integer corresponding to the valididity of the score
	 */
	public abstract int getHighestInvalidScore();
	
	public boolean isScoreEnough(int score) {
		return score > getHighestInvalidScore();
	}
	
	/**
	 * Function to apply a bonus for these pattern: source.contains(match), source.startsWith(match) and source.endsWith(match)
	 * 
	 * @param source
	 *            The source string
	 * @param match
	 *            Query or token
	 * @param size
	 *            Bonus size
	 * @return New bonus
	 */
	protected int applyBonus(String source, String match, int size) {
		int bonus = 0;
		
		if (source.contains(match)) {
			bonus += size;
		}
		if (source.startsWith(match)) {
			bonus += size;
		}
		if (source.endsWith(match)) {
			bonus += size;
		}
		
		return bonus;
	}
	
	/**
	 * Create a new search strategy context
	 * 
	 * @param className
	 *            Class of the provider
	 * @return A new context of the strategy asked
	 * @throws IllegalArgumentException
	 *             If any error append
	 */
	public static SearchStrategy createContext(Class<? extends SearchStrategy> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception exception) {
			throw new IllegalArgumentException(exception);
		}
	}
	
}