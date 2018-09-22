package caceresenzo.libs.boxplay.assets;

import caceresenzo.libs.io.IOUtils;

public class BoxPlayAssets {
	
	public static final String ASSETS_OPENLOAD_JQUERY = "common/extractor/video/implementations/openload/jquery.js";
	
	private BoxPlayAssets() {
		throw new IllegalStateException("This class can't be instanced");
	}
	
	public static String getRessource(String relativePath) {
		try {
			return IOUtils.readString(BoxPlayAssets.class.getResourceAsStream(relativePath), "UTF-8");
		} catch (Exception exception) {
			return null;
		}
	}
	
}