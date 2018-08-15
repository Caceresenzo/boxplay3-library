package caceresenzo.libs.boxplay.culture.searchngo.content;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;

public interface IContentProvider {
	
	Class<? extends ContentExtractor>[] getCompatibleExtractorClass();
	
}