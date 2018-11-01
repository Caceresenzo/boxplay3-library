package caceresenzo.libs.boxplay.mylist.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.mylist.MyListItem;
import caceresenzo.libs.logger.Logger;

@SuppressWarnings("all")
public class MyListTestUnits {
	
	public static void main(String[] args) {
		;
	}
	
	public static class MyListSerializerTest {
		public static final String QUERY = "hell";
		
		@SuppressWarnings("unchecked")
		public static void main(String[] args) {
			List<MyListItem> items = new ArrayList<>();
			
			/* EXTRACTION */
			List<SearchAndGoProvider> providers = ProviderManager.createAll();
			
			final List<SearchAndGoResult> results = new ArrayList<>();
			
			try {
				for (SearchAndGoProvider provider : providers) {
					Map<String, SearchAndGoResult> workmap = provider.work(QUERY);
					
					ResultScoreSorter.sortWorkmap(workmap, QUERY, provider.getHelper().getSearchEngine());
					
					results.addAll(workmap.values());
				}
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
			
			for (SearchAndGoResult result : results) {
				SearchAndGoProvider provider = result.getParentProvider();
				
				items.add(new MyListItem<SearchAndGoResult>(result));
			}
			/* EXTRACTION END */
			
			File saveFile = new File("test/mylist/serialization.ser");
			try {
				saveFile.mkdirs();
				saveFile.delete();
				saveFile.createNewFile();
			} catch (IOException exception) {
				;
			}
			
			try {
				OutputStream file = new FileOutputStream(saveFile);
				OutputStream buffer = new BufferedOutputStream(file);
				ObjectOutput output = new ObjectOutputStream(buffer);
				
				try {
					output.writeObject(items);
				} finally {
					output.close();
				}
			} catch (IOException exception) {
				Logger.exception(exception);
			}
			
			List<MyListItem> recoveredItems;
			try {
				InputStream file = new FileInputStream(saveFile);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);
				
				try {
					recoveredItems = (List<MyListItem>) input.readObject();
					
					for (MyListItem item : recoveredItems) {
						System.out.println("Recovered item: " + item);
					}
				} finally {
					file.close();
					buffer.close();
					input.close();
				}
			} catch (Exception exception) {
				Logger.exception(exception);
			}
		}
		
	}
	
}