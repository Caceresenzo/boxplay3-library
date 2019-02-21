package caceresenzo.libs.boxplay.common.extractor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericJapScanChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaLelChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaNeloChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericMangaRockChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.image.manga.implementations.GenericScanMangaChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.text.novel.implementations.GenericScanMangaNovelChapterExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericAnimeUltimateVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericGoUnlimitedVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericOpenloadVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericStreamangoVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVevioVideoQualityExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVidloxVideoExtractor;
import caceresenzo.libs.boxplay.common.extractor.video.implementations.GenericVidozaVideoExtractor;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.content.ChapterItemResultData.ChapterType;
import caceresenzo.libs.string.StringUtils;

public class ContentExtractionManager {
	
	/* Statics */
	private static final Map<ExtractorType, Map<Class<? extends ContentExtractor>, ContentExtractor>> EXTRACTORS = new HashMap<>();
	
	static {
		for (ExtractorType extractorType : ExtractorType.values()) {
			EXTRACTORS.put(extractorType, new HashMap<Class<? extends ContentExtractor>, ContentExtractor>());
		}
		
		/* Video */
		bindExtractor(ExtractorType.VIDEO, GenericOpenloadVideoExtractor.class, new GenericOpenloadVideoExtractor());
		bindExtractor(ExtractorType.VIDEO, GenericVidozaVideoExtractor.class, new GenericVidozaVideoExtractor());
		bindExtractor(ExtractorType.VIDEO, GenericAnimeUltimateVideoExtractor.class, new GenericAnimeUltimateVideoExtractor());
		bindExtractor(ExtractorType.VIDEO, GenericGoUnlimitedVideoExtractor.class, new GenericGoUnlimitedVideoExtractor());
		bindExtractor(ExtractorType.VIDEO, GenericVidloxVideoExtractor.class, new GenericVidloxVideoExtractor());
		bindExtractor(ExtractorType.VIDEO, GenericStreamangoVideoExtractor.class, new GenericStreamangoVideoExtractor());
		
		/* Video with qualities */
		bindExtractor(ExtractorType.VIDEO, GenericVevioVideoQualityExtractor.class, new GenericVevioVideoQualityExtractor());
		
		/* Manga */
		bindExtractor(ExtractorType.MANGA, GenericMangaLelChapterExtractor.class, new GenericMangaLelChapterExtractor());
		bindExtractor(ExtractorType.MANGA, GenericScanMangaChapterExtractor.class, new GenericScanMangaChapterExtractor());
		bindExtractor(ExtractorType.MANGA, GenericJapScanChapterExtractor.class, new GenericJapScanChapterExtractor());
		bindExtractor(ExtractorType.MANGA, GenericMangaNeloChapterExtractor.class, new GenericMangaNeloChapterExtractor());
		bindExtractor(ExtractorType.MANGA, GenericMangaRockChapterExtractor.class, new GenericMangaRockChapterExtractor());
		
		/* Novel */
		bindExtractor(ExtractorType.NOVEL, GenericScanMangaNovelChapterExtractor.class, new GenericScanMangaNovelChapterExtractor());
	}
	
	/* Constructor */
	private ContentExtractionManager() {
		throw new IllegalStateException("This class can't be instanced.");
	}
	
	/**
	 * Bind a new extractor from a base class.
	 * 
	 * @param type
	 *            {@link ContentExtractor} extracted data type.
	 * @param extractorClass
	 *            Base {@link ContentExtractor} class.
	 * @param unusedExtractor
	 *            An instance that will not be used (only for {@link ContentExtractor#matchUrl(String)})
	 * @return Your new registered {@link ContentExtractor}.
	 */
	public static ContentExtractor bindExtractor(ExtractorType type, Class<? extends ContentExtractor> extractorClass, ContentExtractor unusedExtractor) {
		return EXTRACTORS.get(type).put(extractorClass, unusedExtractor);
	}
	
	/**
	 * Get a new extractor from a base url.
	 * 
	 * @param extractorType
	 *            Type of extractor ({@link ExtractorType}).
	 * @param baseUrl
	 *            Base url of your result/item to extract.
	 * @return A new instance of an extractor, null if not found or failed to initialize.
	 */
	public static ContentExtractor getExtractorFromBaseUrl(ExtractorType extractorType, String baseUrl) {
		if (baseUrl == null) {
			return null;
		}
		
		Class<? extends ContentExtractor> extractorClass = getExtractorClassFromBaseUrl(extractorType, baseUrl);
		
		if (extractorClass != null) {
			try {
				return extractorClass.newInstance();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			
			return null;
		}
		
		return null;
	}
	
	/**
	 * Get a {@link ContentExtractor} class used for checking or instancing.
	 * 
	 * @param extractorType
	 *            Type of extractor ({@link ExtractorType}).
	 * @param baseUrl
	 *            Base url of your result/item to extract.
	 * @return A {@link ContentExtractor} class that have matched with your baseUrl.
	 */
	private static Class<? extends ContentExtractor> getExtractorClassFromBaseUrl(ExtractorType extractorType, String baseUrl) {
		if (!StringUtils.validate(baseUrl)) {
			return null;
		}
		
		for (Entry<Class<? extends ContentExtractor>, ContentExtractor> entry : EXTRACTORS.get(extractorType).entrySet()) {
			InternetSource internetSource = entry.getValue();
			
			if (internetSource.matchUrl(baseUrl)) {
				return entry.getValue().getClass();
			}
		}
		
		return null;
	}
	
	/**
	 * Check if your baseUrl have any compatible {@link ContentExtractor} actually registered.
	 * 
	 * @param extractorType
	 *            Type of extractor ({@link ExtractorType}).
	 * @param baseUrl
	 *            Base url of your result/item to check.
	 * @return If a compatible {@link ContentExtractor} has been found.
	 */
	public static boolean hasCompatibleExtractor(ExtractorType extractorType, String baseUrl) {
		return getExtractorClassFromBaseUrl(extractorType, baseUrl) != null;
	}
	
	/**
	 * When registering/and getting a {@link ContentExtractor}, a {@link ExtractorType} is required because some site have multiple ressources on their site.
	 * 
	 * @author Enzo CACERES
	 */
	public static enum ExtractorType {
		VIDEO, MANGA, NOVEL;
		
		/**
		 * Get a low-corresponding {@link ExtractorType} from a {@link ChapterType}.
		 * 
		 * @param chapterType
		 *            Target {@link ChapterType} to convert.
		 * @return Corresponding {@link ExtractorType}, null if not found.
		 */
		public static ExtractorType fromChapterType(ChapterType chapterType) {
			switch (chapterType) {
				case IMAGE_ARRAY: {
					return MANGA;
				}
				
				case TEXT: {
					return NOVEL;
				}
				
				default: {
					return null;
				}
			}
		}
	}
	
}