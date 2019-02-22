package caceresenzo.libs.boxplay.mylist.test;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.mylist.binder.ListItemBinder;
import caceresenzo.libs.boxplay.mylist.binder.ListItemBinderManager;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.test.SimpleTestUnits;

@SuppressWarnings("all")
public class MyListTestUnits extends SimpleTestUnits {
	
	public static class BinderTestUnit extends MyListTestUnits {
		
		public static void main(String[] args) {
			SearchAndGoResult result = new SearchAndGoResult(ProviderManager.JETANIME.create(), "Hello", "http://google.com", "http://some.com/image.png", SearchCapability.MOVIE);
			
			ListItemBinder binder = new SearchAndGoResult.ItemBinder();
			
			String converted = binder.convertItemToString(result);
			
			Logger.info("Converted: %s", converted);
			
			Logger.info("Restored: %s", binder.restoreItemFromString(converted));
		}
		
	}
	
	public static class BinderAndBuilderTestUnit extends MyListTestUnits {
		
		public static void main(String[] args) {
			SearchAndGoResult result = new SearchAndGoResult(ProviderManager.JETANIME.create(), "Hello", "http://google.com", "http://some.com/image.png", SearchCapability.MOVIE);
			
			Object anObject = result;
			
			if (ListItemBinderManager.hasCorrespondingBinder(anObject.getClass())) {
				ListItemBinder binder = ListItemBinderManager.getCorrespondingBinder(anObject.getClass());
				
				String converted = binder.convertItemToString(result);
				
				Logger.info("Converted: %s", converted);
				
				Logger.info("Restored: %s", binder.restoreItemFromString(converted));
			} else {
				Logger.info("No binder found.");
			}
		}
		
	}
	
}