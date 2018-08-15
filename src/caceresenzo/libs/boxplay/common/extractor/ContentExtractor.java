package caceresenzo.libs.boxplay.common.extractor;

import caceresenzo.libs.boxplay.common.extractor.openload.ExtractionLogger;
import caceresenzo.libs.thread.ThreadUtils;

/**
 * Base Context Extractor class to extends
 * 
 * @author Enzo CACERES
 */
public abstract class ContentExtractor implements IExtractor {
	
	private ExtractionLogger logger = new ExtractionLogger(getClass().getSimpleName());
	
	private boolean locked = false, failed = false;
	
	/**
	 * Lock the thread until extractor code excution is unlocked
	 */
	protected void waitUntilUnlock() {
		while (isLocked()) {
			ThreadUtils.sleep(100L);
		}
		
		if (hasFailed()) {
			throw new ExtractorRuntimeException();
		}
	}
	
	/**
	 * Tell if the main code execution has been locked
	 * 
	 * @return Locked state
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Lock code execution, like waiting another thread to finish
	 * 
	 * @return Itself
	 */
	public ContentExtractor lock() {
		return locked(true);
	}
	
	/**
	 * Unlock code execution, the another thread may have finish
	 * 
	 * @return Itself
	 */
	public ContentExtractor unlock() {
		return locked(false);
	}
	
	/**
	 * Lock or unlock code execution
	 * 
	 * @param locked
	 *            New lock state
	 * @return Itself
	 */
	public ContentExtractor locked(boolean locked) {
		this.locked = locked;
		
		return this;
	}
	
	/**
	 * Get if the extractor has failed and can't continue anymore
	 * 
	 * @return True of false
	 */
	public boolean hasFailed() {
		return failed;
	}
	
	/**
	 * Tell executor if it has failed, and can't continue execution
	 * 
	 * Code will be automaticly unlocked if failed
	 * 
	 * @param hasFailed
	 *            New failed state
	 * @return Itself
	 */
	public ContentExtractor failed(boolean hasFailed) {
		this.failed = hasFailed;
		
		return unlock();
	}
	
	/**
	 * Abstract function to ovveride;
	 * 
	 * Called when an exception append
	 * 
	 * @param exception
	 *            Append exception
	 */
	public abstract void notifyException(Exception exception);
	
	/**
	 * Get the private {@link #logger} instance
	 * 
	 * @return {@link ExtractionLogger} instance
	 */
	public ExtractionLogger getLogger() {
		return logger;
	}
	
	/**
	 * Base exception for any extractors sub-exception
	 * 
	 * @author Enzo CACERES
	 */
	public static class ExtractorRuntimeException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
}