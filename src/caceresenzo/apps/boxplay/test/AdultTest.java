package caceresenzo.apps.boxplay.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import caceresenzo.apps.boxplay.factory.AdultFactory;
import caceresenzo.apps.boxplay.factory.AdultFactory.AdultFactoryListener;
import caceresenzo.apps.boxplay.factory.AdultFactory.VideoOrigin;
import caceresenzo.apps.boxplay.models.premium.adult.AdultVideo;
import caceresenzo.libs.io.IOUtils;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.logger.Logger;

public class AdultTest {
	
	public static void main(String[] args) throws IOException, JsonException {
		AdultTest serverTest = new AdultTest();
		serverTest.initialize();
		serverTest.callFactory();
	}
	
	private AdultFactory factory = new AdultFactory();
	
	private List<AdultVideo> videos;
	
	private String html;
	
	public void initialize() throws UnsupportedEncodingException, IOException {
		videos = new ArrayList<AdultVideo>();
		
		html = IOUtils.readString(this.getClass().getResourceAsStream("adult.html"), "UTF-8");
		
		// File output = new File("./output.txt");
		// output.createNewFile();
		// System.setOut(new PrintStream(new FileOutputStream(output)));
	}
	
	public void callFactory() {
		videos.clear();
		
		factory.parseHomepageHtml(new AdultFactoryListener() {
			@Override
			public void onHtmlNull() {
				Logger.error("Html null");
			}
			
			@Override
			public void onAdultVideoCreated(AdultVideo adultVideo, VideoOrigin origin) {
				videos.add(adultVideo);
			}
		}, html, true);
		
		for (AdultVideo video : videos) {
			System.out.println(video.toString());
			System.out.println("\tUrl:\t" + video.getTargetUrl());
			System.out.println("\tTitle:\t" + video.getTitle());
			System.out.println("\tImage:\t" + video.getImageUrl());
			
			System.out.println("");
		}
	}
	
}
