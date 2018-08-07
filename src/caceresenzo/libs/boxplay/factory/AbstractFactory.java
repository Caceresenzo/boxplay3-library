package caceresenzo.libs.boxplay.factory;

public class AbstractFactory {
	
	protected static final int IMPOSSIBLE_VALUE = Integer.MAX_VALUE;
	
	protected static final String REPLACE_VIDEO_FILE_TYPE = "__videoFileType__";
	protected static final String REPLACE_EPISODE_FORMATTED = "__episodeFormatted__";
	
	// Roots
	protected static final String KEY_SERVERS_ROOT = "servers";
	protected static final String KEY_FILES_ROOT = "files";
	
	// Server
	protected static final String KEY_SERVER_LIST = "list";
	protected static final String KEY_SERVER_DEFAULT = "default";
	protected static final String KEY_SERVER_ITEM_STARTING_STRING_URL = "starting_string_url";
	protected static final String KEY_SERVER_ITEM_ICON_URL = "icon_url";
	protected static final String KEY_SERVER_ITEM_IMAGE_URL = "image_url";
	protected static final String KEY_SERVER_ITEM_POSITION = "position";
	protected static final String KEY_SERVER_ITEM_DISPLAY_TRANSLATION = "display";
	protected static final String KEY_SERVER_ITEM_DESCRIPTION_TRANSLATION = "description";
	
	// extends Element
	protected static final String KEY_TITLE = "title";
	protected static final String KEY_LANGUAGE = "language";
	protected static final String KEY_IMAGE_URL = "image_url";
	protected static final String KEY_DEFAULT_IMAGE_URL = "default_image_url";
	protected static final String KEY_IMAGE_HD_URL = "image_hd_url";
	protected static final String KEY_URL = "url";
	protected static final String KEY_URL_FORMAT = "url_format";
	protected static final String KEY_RECOMMENDED = "recommended";
	
	// Override
	protected static final String KEY_OVERRIDE = "override";
	protected static final String KEY_OVERRIDE_URL = "direct_url";
	
	// Video
	protected static final String KEY_SEASONS = "saisons"; // TODO: CHANGE
	protected static final String KEY_SEASONS_DIGIT_SUPPORT_VALUE = "seasons_digit_support_value";
	protected static final int DEFAULT_SEASONS_DIGIT_SUPPORT_VALUE = 2;
	protected static final String KEY_EPISODES_DIGIT_SUPPORT_VALUE = "episodes_digit_support_value";
	protected static final int DEFAULT_EPISODES_DIGIT_SUPPORT_VALUE = 2;
	protected static final String KEY_AVALIABLE_PATTERN = "avaliable_pattern";
	protected static final String KEY_UNAVALIABLE_PATTERN = "unavaliable_pattern";
	
	// Music
	protected static final String KEY_MUSIC_AUTHOR = "author";
	protected static final String KEY_MUSIC_TYPE = "type";
	protected static final String KEY_MUSIC_RESSOURCES = "ressources";
	protected static final String KEY_MUSIC_GENRE = "genre";
	protected static final String KEY_MUSIC_ALBUM_NAME = "name";
	protected static final String KEY_MUSIC_ALBUM_RELEASE_DATE_STRING = "release_date_string";
	protected static final String KEY_MUSIC_ALBUM_AVAILABLE = "available";
	protected static final String KEY_MUSIC_ALBUM_MUSICS = "musics";
	protected static final String KEY_MUSIC_RESSOURCE_TITLE = "title";
	protected static final String KEY_MUSIC_RESSOURCE_TRACK_ID = "track_id";
	protected static final String KEY_MUSIC_RESSOURCE_DURATION = "duration";
	protected static final String KEY_MUSIC_RESSOURCE_AVAILABLE = "available";
	
	public static interface FactoryListener {
		
		void onJsonNull();
		
		void onJsonMissingFileType();
		
	}
}