package caceresenzo.libs.boxplay.common.extractor.text;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;

public abstract class TextContentExtractor extends ContentExtractor {
	
	/**
	 * Abstract function to extends, used to tell the base extracted text format that has been returned by the extraction to be reprocessed by the frontend
	 * 
	 * @return Extracted Text's Format (in most cases)
	 */
	public abstract TextFormat getSupposedExtractedTextFormat();
	
	/**
	 * Extracted text output format
	 * 
	 * @author Enzo CACERES
	 */
	public enum TextFormat {
		HTML, TEXT;
	}
	
}