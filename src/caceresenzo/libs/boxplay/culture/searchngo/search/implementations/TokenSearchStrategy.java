package caceresenzo.libs.boxplay.culture.searchngo.search.implementations;

import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategy;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchStrategyManager.ProcessingSpeed;

/**
 * Only using advanced-match like the {@link String#contains(CharSequence)}, {@link String#startsWith(CharSequence)} and {@link String#endsWith(CharSequence)} function
 * 
 * He also use a token system with more advanced score system
 * 
 * @author Enzo CACERES
 */
public class TokenSearchStrategy extends SearchStrategy {
	
	@Override
	public int validateSearchQuery(String query, String string) {
		query = query.toUpperCase();
		string = string.toUpperCase();
		
		int score = 0;
		
		score += applyBonus(string, query, 2);
		
		for (String queryWord : query.split(" ")) {
			if (string.contains(queryWord)) {
				score += 1;
			} else {
				break;
			}
		}
		
		for (String queryWord : query.split(" ")) {
			String token = "";
			for (char queryToken : queryWord.toCharArray()) {
				token += queryToken;
				
				score += applyBonus(string, token, 1);
			}
		}
		
		return score;
	}
	
	@Override
	public ProcessingSpeed getProcessingSpeed() {
		return ProcessingSpeed.SLOW;
	}
	
	@Override
	public int getHighestInvalidScore() {
		return 4;
	}
	
}