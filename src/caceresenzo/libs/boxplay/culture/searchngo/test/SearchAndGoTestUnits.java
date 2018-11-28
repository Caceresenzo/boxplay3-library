package caceresenzo.libs.boxplay.culture.searchngo.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.common.extractor.ContentExtractionManager;
import caceresenzo.libs.boxplay.common.extractor.ContentExtractionManager.ExtractorType;
import caceresenzo.libs.boxplay.common.extractor.ContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.MangaChapterContentExtractor;
import caceresenzo.libs.boxplay.common.extractor.text.novel.NovelChapterContentExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.callback.ProviderSearchCallback;
import caceresenzo.libs.boxplay.culture.searchngo.content.image.implementations.IMangaContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.content.video.IVideoContentProvider;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalDataType;
import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.additional.CategoryResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.VideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.completed.CompletedVideoItemResultData;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderCallback;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderManager;
import caceresenzo.libs.boxplay.culture.searchngo.providers.ProviderSearchCapability.SearchCapability;
import caceresenzo.libs.boxplay.culture.searchngo.providers.SearchAndGoProvider;
import caceresenzo.libs.boxplay.culture.searchngo.result.ResultScoreSorter;
import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;
import caceresenzo.libs.boxplay.culture.searchngo.search.SearchEngine;
import caceresenzo.libs.boxplay.utils.Sandbox;
import caceresenzo.libs.cryptography.Base64;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.http.client.webb.Request;
import caceresenzo.libs.http.client.webb.Webb;
import caceresenzo.libs.iterator.ByteArrayIterator;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.stream.StreamUtils;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.thread.ThreadUtils;

/**
 * Basic Test Units
 * 
 * @author Enzo CACERES
 */
public class SearchAndGoTestUnits {
	
	public static final boolean ENABLED_MANGA_DOWNLOAD = true;
	public static final int MAX_THREAD_COUNT = 1;
	public static final String MANGA_DOWNLOAD_BASE_PATH = "C:\\Users\\cacer\\Desktop\\manga_output\\";
	public static int THREAD_COUNT = 0;
	
	public static class ExtractionTest {
		private static final String QUERY = "tokyo ghoul";
		
		public static void main(String[] args) {
			// redirectConsoleOutput();
			
			ProviderCallback.registerProviderSearchCallback(new ProviderSearchCallback() {
				@Override
				public void onProviderSorting(SearchAndGoProvider provider) {
					;
				}
				
				@Override
				public void onProviderSearchStarting(SearchAndGoProvider provider) {
					;
				}
				
				@Override
				public void onProviderSearchFinished(SearchAndGoProvider provider, Map<String, SearchAndGoResult> workmap) {
					;
				}
				
				@Override
				public void onProviderFailed(SearchAndGoProvider provider, Exception exception) {
					exception.printStackTrace();
				}
			});
			
			List<SearchAndGoProvider> providers = new ArrayList<>();
			
			providers.add(ProviderManager.JETANIME.create());
			// providers.add(ProviderManager.VOIRFILM_PRO.create());
			// providers.add(ProviderManager.MANGALEL.create());
			// providers.add(ProviderManager.ADKAMI.create());
			// providers.add(ProviderManager.SCANMANGA.create());
			// providers.add(ProviderManager.FULLSTREAM_CO.create());
			// providers.add(ProviderManager.ANIMEULTIME.create());
			// providers.add(ProviderManager.HDSS_TO.create());
			
			final List<SearchAndGoResult> results = new ArrayList<>();
			
			try {
				for (SearchAndGoProvider provider : providers) {
					Map<String, SearchAndGoResult> workmap = provider.work(QUERY);
					
					ResultScoreSorter.sortWorkmap(workmap, QUERY, provider.getHelper().getSearchEngine());
					
					for (Entry<String, SearchAndGoResult> entry : workmap.entrySet()) {
						results.add(entry.getValue());
						Logger.info("------------------------------------------------------------ %s %s", entry.getValue().score(), entry.getValue().getName());
					}
				}
				
				for (SearchAndGoResult searchAndGoResult : results) {
					Logger.info("type: %-10s - name: %-60s - score: %s", searchAndGoResult.getType(), searchAndGoResult.getName(), searchAndGoResult.score());
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
			for (SearchAndGoResult result : results) {
				SearchAndGoProvider provider = result.getParentProvider();
				
				Logger.$(result.getType() + " [search score: %s]", result.score());
				Logger.$("\t" + result.getUrl());
				Logger.$("\t" + result.getName());
				Logger.$("\t" + result.getBestImageUrl());
				if (result.hasDescription()) {
					Logger.$("\t" + result.getDescription());
				}
				
				/* Data */
				Logger.$("");
				Logger.$("\tDATA:");
				
				List<AdditionalResultData> additionalResult = result.getParentProvider().fetchMoreData(result);
				for (AdditionalResultData additionalData : additionalResult) {
					Logger.$("\t- %-20s >> %s", additionalData.getType(), additionalData.convert());
				}
				
				/* Content */
				Logger.$("");
				Logger.$("\tCONTENT:");
				
				List<AdditionalResultData> additionalContent = result.getParentProvider().fetchContent(result);
				for (AdditionalResultData additionalData : additionalContent) {
					Logger.$("\t- %-20s >> %s", additionalData.getType(), additionalData.convert());
					
					if (provider instanceof IVideoContentProvider) {
						
						String[] urls;
						if (additionalData.getData() instanceof CompletedVideoItemResultData) {
							urls = ((CompletedVideoItemResultData) additionalData.getData()).getPlayerUrlsAsArray();
						} else if (additionalData.getData() instanceof VideoItemResultData) {
							urls = ((IVideoContentProvider) provider).extractVideoPageUrl((VideoItemResultData) additionalData.getData());
						} else {
							throw new IllegalStateException("Invalid data class provided by a IVideoContentProvider: " + additionalData.getData().getClass().getSimpleName());
						}
						
						for (String url : urls) {
							Logger.$("\t\tIVideoContentProvider: " + url);
							
							ContentExtractor contentExtractor = ContentExtractionManager.getExtractorFromBaseUrl(ExtractorType.VIDEO, url);
							
							Logger.$("\t\t | -> %s", contentExtractor != null ? contentExtractor.getClass().getSimpleName() : "NO_COMPATIBLE_PROVIDER");
							
							// if (contentExtractor != null && contentExtractor instanceof VideoContentExtractor) {
							// Logger.info("\t\t\t | -> %s", ((VideoContentExtractor) contentExtractor).extractDirectVideoUrl(url));
							// }
						}
						
						Logger.$("");
					} else if (provider instanceof IMangaContentProvider && additionalData.getData() instanceof ChapterItemResultData) {
						Logger.$("IMangaContentProvider: " + ((IMangaContentProvider) provider).extractMangaPageUrl((ChapterItemResultData) additionalData.getData()));
						Logger.$("");
						
						ChapterItemResultData chapterItem = (ChapterItemResultData) additionalData.getData();
						String pageUrl = ((IMangaContentProvider) provider).extractMangaPageUrl(chapterItem);
						ContentExtractor extractor = ContentExtractionManager.getExtractorFromBaseUrl(ExtractorType.fromChapterType(chapterItem.getChapterType()), result.getUrl());
						
						if (extractor instanceof MangaChapterContentExtractor) { /* If null; it will skip */
							int pageCount = 0;
							for (String url : ((MangaChapterContentExtractor) extractor).getImageUrls(pageUrl)) {
								Logger.$(" |- Image URL: " + url);
								
								if (ENABLED_MANGA_DOWNLOAD) {
									File file = new File(MANGA_DOWNLOAD_BASE_PATH, "WEBB_" + FileUtils.replaceIllegalChar(result.getName()) + "/" + FileUtils.replaceIllegalChar(additionalData.convert()) + "/" + FileUtils.replaceIllegalChar(String.format("PAGE %s.jpg", pageCount++)));
									
									while (THREAD_COUNT >= MAX_THREAD_COUNT) {
										ThreadUtils.sleep(100L);
									}
									
									THREAD_COUNT++;
									new DownloadWorker().applyData(chapterItem, file, url).start();
								}
							}
						} else if (extractor instanceof NovelChapterContentExtractor) {
							String novel = ((NovelChapterContentExtractor) extractor).extractNovel(chapterItem);
							
							if (novel != null) {
								Logger.$(" |- Novel (cut a 200): " + StringUtils.cutIfTooLong(novel, 200));
							}
						}
						
						Logger.$("");
					}
				}
				
				Logger.$(" ------------------------------------- ");
			}
			
			Logger.$("    ");
			Logger.$("size: " + results.size());
			Logger.$("    ");
			Logger.$("Providers: " + providers);
		}
		
		public static class DownloadWorker extends Thread {
			private ChapterItemResultData chapterItem;
			private File file;
			private String url;
			
			@Override
			public void run() {
				while (true) {
					try {
						file.delete();
						file.getParentFile().mkdirs();
						
						Request request = Webb.create().get(url).ensureSuccess().readTimeout(0).connectTimeout(0);
						
						if (chapterItem.hasInitializedComplements()) {
							@SuppressWarnings("unchecked")
							Map<String, String> headers = Map.class.cast(chapterItem.getComplement(SimpleData.REQUIRE_HTTP_HEADERS_COMPLEMENT));
							
							if (headers != null && !headers.isEmpty()) {
								for (Entry<String, String> entry : headers.entrySet()) {
									request.header(entry.getKey(), entry.getValue());
								}
							}
						}
						
						InputStream stream = request.asStream().getBody();
						
						StreamUtils.copyInputStream(stream, new FileOutputStream(file));
					} catch (Exception exception) {
						Logger.exception(exception, "[Webb] Failed to download file %s (url=%s)", file.getAbsolutePath(), url);
						ThreadUtils.sleep(5000L);
						continue;
					}
					
					THREAD_COUNT--;
				}
			}
			
			public DownloadWorker applyData(ChapterItemResultData chapterItem, File file, String url) {
				this.chapterItem = chapterItem;
				this.file = file;
				this.url = url;
				
				return this;
			}
		}
		
	}
	
	public static class SearchTest {
		
		private static final String QUERY = "tomo";
		
		public static void main(String[] args) {
			SearchEngine searchEngine = new SearchEngine();
			
			List<String> names = new ArrayList<>();
			
			/**
			 * name.add("string to search");
			 * 
			 * ...
			 */
			
			final Map<String, Integer> scoreMap = new HashMap<>();
			
			for (String string : names) {
				int score = searchEngine.applySearchStrategy(QUERY, string);
				
				scoreMap.put(string, score);
			}
			
			Collections.sort(names, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					int compare = scoreMap.get(o2) - scoreMap.get(o1); // Sort by score
					
					if (compare == 0) {
						compare = o1.length() - o2.length(); // Score by length
					}
					
					if (compare == 0) {
						compare = o2.compareTo(o1); // Sort by name
					}
					
					return compare;
				}
			});
			
			for (String string : names) {
				int score = scoreMap.get(string);
				
				if (score <= 4) {
					continue;
				}
				
				Logger.info("QUERY: %-20s | STRING: %-50s | SCORE: %s", QUERY, string, scoreMap.get(string));
			}
		}
		
	}
	
	public static class AndroidI18nExporter {
		
		public static void main(String[] args) {
			for (AdditionalDataType resultType : AdditionalDataType.values()) {
				System.out.println(String.format("<string name=\"boxplay_culture_searchngo_search_result_data_type_%s\">%s</string>", resultType.toString().toLowerCase(), resultType.toString()));
			}
			
			for (AdditionalDataType resultType : AdditionalDataType.values()) {
				System.out.println(String.format("enumCacheTranslation.put(%s.%s, boxPlayApplication.getString(R.string.boxplay_culture_searchngo_search_result_data_type_%s));", resultType.getClass().getSimpleName(), resultType.toString(), resultType.toString().toLowerCase()));
			}
			
			for (SearchCapability capability : SearchCapability.values()) {
				System.out.println(String.format("<string name=\"boxplay_culture_searchngo_search_result_type_%s\">%s</string>", capability.toString().toLowerCase(), capability.toString()));
			}
			
			for (SearchCapability capability : SearchCapability.values()) {
				System.out.println(String.format("enumCacheTranslation.put(%s.%s, boxPlayApplication.getString(R.string.boxplay_culture_searchngo_search_result_type_%s));", capability.getClass().getSimpleName(), capability.toString(), capability.toString().toLowerCase()));
			}
		}
		
	}
	
	public static class AdkamiSandboxTestUnit {
		
		public static void main(String[] args) {
			
			// redirectConsoleOutput();
			
			Sandbox<String, String> sandbox = new Sandbox<String, String>() {
				@Override
				public String execute(String baseUrl) {
					String[] split = baseUrl.split("https://www.youtube.com/embed/");
					
					if (split.length < 2) {
						return null;
					}
					
					baseUrl = split[1];
					byte[] decodedBytes = Base64.decodeFast(baseUrl);
					String result = "", key = "ETEfazefzeaZa13MnZEe";
					int index = 0;
					
					try {
						ByteArrayIterator iterator = new ByteArrayIterator(decodedBytes);
						while (iterator.hasNext()) {
							// int nextByte = Byte.toUnsignedInt(iterator.next()); // Too advanced for older phone
							int nextByte = iterator.next() & 0xFF; // Basicly un-sign actual byte value, thanks stackoverflow
							result += (char) ((175 ^ nextByte) - (int) key.charAt(index));
							index = index > key.length() - 2 ? 0 : index + 1;
						}
					} catch (Exception exception) {
						return null;
					}
					
					return result;
				}
			};
			
			Logger.info(sandbox.execute("https://www.youtube.com/embed/2ywbeWlHfnp0ZiASf883FX8QBjvSCdNwfGAJcUEWDQ=="));
		}
		
	}
	
	public static class RandomCodeTest {
		
		public static void main(String[] args) {
			String categoriesString = ".Drame.Fantastique";
			
			String[] genders = categoriesString.split("\\.");
			
			List<CategoryResultData> categories = new ArrayList<>();
			
			for (String gender : genders) {
				Logger.error("FOUND : " + gender);
				if (!StringUtils.validate(genders)) {
					continue;
				}
				Logger.error("-- VALIDATED: " + gender);
				
				categories.add(new CategoryResultData(gender));
			}
			
			// if (!categories.isEmpty()) {
			// additionals.add(new AdditionalResultData(AdditionalDataType.GENDERS, categories));
			// }
		}
		
	}
	
}