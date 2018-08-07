package caceresenzo.libs.boxplay.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import caceresenzo.libs.boxplay.models.server.ServerHosting;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.parse.ParseUtils;

public class ServerFactory extends AbstractFactory {
	
	public ServerFactory() {
		;
	}
	
	@SuppressWarnings("unchecked")
	public void parseServerJson(ServerFactoryListener factoryListener, JsonObject jsonObject) {
		if (jsonObject == null) {
			factoryListener.onJsonNull();
			return;
		}
		JsonObject jsonServerArrayObject = (JsonObject) jsonObject.get(KEY_SERVERS_ROOT);
		
		if (jsonServerArrayObject == null) {
			factoryListener.onJsonMissingContent();
			return;
		}
		
		JsonObject serverList = (JsonObject) jsonServerArrayObject.get(KEY_SERVER_LIST);
		String defaultServerName = ParseUtils.parseString(jsonServerArrayObject.get(KEY_SERVER_DEFAULT), null);
		
		if (serverList == null || defaultServerName == null) {
			factoryListener.onJsonMissingContent();
			return;
		}
		
		for (Entry<?, ?> serverEntry : serverList.entrySet()) {
			String serverName = (String) serverEntry.getKey();
			HashMap<?, ?> serverData = (HashMap<?, ?>) serverEntry.getValue();
			
			String serverStartingStringUrl = ParseUtils.parseString(serverData.get(KEY_SERVER_ITEM_STARTING_STRING_URL), null);
			String serverIconUrl = ParseUtils.parseString(serverData.get(KEY_SERVER_ITEM_ICON_URL), null);
			String serverImageUrl = ParseUtils.parseString(serverData.get(KEY_SERVER_ITEM_IMAGE_URL), null);
			int serverPosition = ParseUtils.parseInt(serverData.get(KEY_SERVER_ITEM_POSITION), IMPOSSIBLE_VALUE);
			HashMap<?, ?> serverDisplayTranslation = (HashMap<?, ?>) serverData.get(KEY_SERVER_ITEM_DISPLAY_TRANSLATION);
			HashMap<?, ?> serverDescriptionTranslation = (HashMap<?, ?>) serverData.get(KEY_SERVER_ITEM_DESCRIPTION_TRANSLATION);
			
			if (serverName != null && serverStartingStringUrl != null && serverData != null) {
				try {
					ServerHosting hosting = ServerHosting //
							.instance(serverName) //
							.asDefault(defaultServerName.equals(serverName)) //
							.withStartingStringUrl(serverStartingStringUrl) //
							.withIconUrl(serverIconUrl) //
							.withImageUrl(serverImageUrl) //
							.withPosition(serverPosition) //
							.withDisplayTranslation((Map<String, String>) serverDisplayTranslation) //
							.withDescriptionTranslation((Map<String, String>) serverDescriptionTranslation) //
					; //
					
					factoryListener.onServerHostingCreated(hosting);
				} catch (Exception exception) {
					;
				}
			}
		}
	}
	
	public static interface ServerFactoryListener {
		
		void onJsonNull();
		
		void onJsonMissingContent();
		
		void onServerHostingCreated(ServerHosting hosting);
		
	}
	
}