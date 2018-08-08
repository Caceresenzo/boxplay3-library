package caceresenzo.libs.boxplay.culture.searchngo.search;

import caceresenzo.libs.boxplay.culture.searchngo.search.implementations.MatchSearchStrategy;
import caceresenzo.libs.boxplay.culture.searchngo.search.implementations.TokenSearchStrategy;

/**
 * Class to create and get information about all available {@link SearchStrategy}
 * 
 * @author Enzo CACERES
 */
public enum SearchStrategyManager {
	
	TOKEN(TokenSearchStrategy.class, ProcessingSpeed.SLOW), //
	MATCH(MatchSearchStrategy.class, ProcessingSpeed.FAST); //
	
	private Class<? extends SearchStrategy> strategyClass;
	private ProcessingSpeed processingSpeed;
	
	private SearchStrategyManager(Class<? extends SearchStrategy> strategyClass) {
		this(strategyClass, ProcessingSpeed.NORMAL);
	}
	
	private SearchStrategyManager(Class<? extends SearchStrategy> strategyClass, ProcessingSpeed processingSpeed) {
		this.strategyClass = strategyClass;
		this.processingSpeed = processingSpeed;
	}
	
	/**
	 * Get the {@link SearchStrategy} theorical processing speed
	 * 
	 * @return Theorical strategy processing speed
	 */
	public ProcessingSpeed getProcessingSpeed() {
		return processingSpeed;
	}
	
	/**
	 * Create a new {@link SearchStrategy} instance
	 * 
	 * @return The {@link SearchStrategy} instance
	 */
	public SearchStrategy create() {
		return SearchStrategy.createContext(strategyClass);
	}
	
	/**
	 * Get the developper selection default search {@link SearchStrategy}
	 * 
	 * @return Default {@link SearchStrategy}
	 */
	public static SearchStrategyManager getDefaultSearchStrategy() {
		return SearchStrategyManager.TOKEN;
	}
	
	/**
	 * Enum for processing speed indication
	 * 
	 * @author Enzo CACERES
	 */
	public static enum ProcessingSpeed {
		FAST, NORMAL, SLOW;
	}
	
}