package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.culture.searchngo.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.logger.Logger;

public class SearchAndGoTestUnits {
	
	public static void main(String[] args) {
		;
	}
	
	public static class ExtractionTest {
		
		public static void main(String[] args) {
			SearchAndGoProvider provider = ProviderManager.JETANIME.create();
			
			Map<String, SearchAndGoResult> workmap = provider.work("idol");
			
			for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
				Logger.$(entry.getKey());
				Logger.$("    " + entry.getValue().getUrl());
				Logger.$("    " + entry.getValue().getName());
				Logger.$("    " + entry.getValue().getBestImageUrl());
			}

			Logger.$("    ");
			Logger.$("Site: " + workmap.size());
		}
		
	}
	
}