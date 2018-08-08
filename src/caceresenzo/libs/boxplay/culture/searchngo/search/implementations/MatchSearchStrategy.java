package caceresenzo.libs.boxplay.culture.searchngo.search.implementations;

import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategy;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategyManager.ProcessingSpeed;

/**
 * Only using quick-match like the {@link String#contains(CharSequence)} function
 * 
 * @author Enzo CACERES
 */
public class MatchSearchStrategy extends SearchStrategy {
	
	@Override
	public int validateSearchQuery(String query, String string) {
		int score = 0;
		String[] parts = query.toUpperCase().split(" ");
		
		for (String part : parts) {
			if (string.toUpperCase().contains(part)) {
				score += 1;
			} else {
				break;
			}
		}
		
		return score;
	}
	
	@Override
	public ProcessingSpeed getProcessingSpeed() {
		return ProcessingSpeed.FAST;
	}
	
	@Override
	public int getHighestInvalidScore() {
		return 1;
	}
	
}