package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.content.IContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

/**
 * Fake provider to be used a cache container only
 * 
 * @author Enzo CACERES
 */
public class FakeProvider extends SearchAndGoProvider implements IContentProvider {
	
	private static FakeProvider INSTANCE;
	
	public FakeProvider() {
		super(null, null);
		
		INSTANCE = this;
	}
	
	@Override
	protected ProviderSearchCapability createSearchCapability() {
		return null;
	}
	
	@Override
	protected Map<String, SearchAndGoResult> processWork(String searchQuery) throws Exception {
		return null;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchMoreData(SearchAndGoResult result) {
		return null;
	}
	
	@Override
	protected List<AdditionalResultData> processFetchContent(SearchAndGoResult result) {
		return null;
	}
	
	@Override
	public Class<? extends ContentExtractor>[] getCompatibleExtractorClass() {
		return null;
	}
	
	public static FakeProvider getFakeProvider() {
		if (INSTANCE == null) {
			INSTANCE = new FakeProvider();
		}
		
		return INSTANCE;
	}
	
}