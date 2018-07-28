package caceresenzo.libs.boxplay.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import caceresenzo.libs.boxplay.factory.ServerFactory;
import caceresenzo.libs.boxplay.factory.ServerFactory.ServerFactoryListener;
import caceresenzo.libs.boxplay.models.server.ServerHosting;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonException;
import caceresenzo.libs.json.parser.JsonParser;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.network.Downloader;

public class ServerTest {
	
	public static void main(String[] args) throws IOException, JsonException {
		ServerTest serverTest = new ServerTest();
		serverTest.initialize();
		serverTest.callFactory();
	}
	
	private ServerFactory serverFactory = new ServerFactory();
	
	private List<ServerHosting> hostings;
	
	private JsonObject serverJsonData;
	
	public void initialize() throws IOException, JsonException {
		hostings = new ArrayList<ServerHosting>();
		
		String content = Downloader.getUrlContent("https://caceres.freeboxos.fr:583/share/n9npNCIdbJr1Wbq8/Android/data/boxplay_3.json");
		serverJsonData = new JsonObject((Map<?, ?>) new JsonParser().parse(new StringReader(content)));
	}
	
	public void callFactory() {
		hostings.clear();
		
		serverFactory.parseServerJson(new ServerFactoryListener() {
			@Override
			public void onJsonNull() {
				Logger.error("Json null");
			}
			
			@Override
			public void onJsonMissingContent() {
				System.out.println("onJsonMissingFileType");
			}
			
			@Override
			public void onServerHostingCreated(ServerHosting hosting) {
				hostings.add(hosting);
			}
		}, serverJsonData);
		
		for (ServerHosting hosting : hostings) {
			System.out.println(hosting.toString() + " [" + hosting.asDefault() + "]");
		}
	}
	
}
