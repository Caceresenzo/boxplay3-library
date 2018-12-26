package caceresenzo.libs.boxplay.mylist.binder.implementations;

import caceresenzo.libs.boxplay.mylist.binder.ListItemBinder;
import caceresenzo.libs.json.JsonObject;
import caceresenzo.libs.json.parser.JsonParser;

/**
 * Bas extends of the {@link ListItemBinder} with parameters serializer a {@link JsonObject}.
 * 
 * @author Enzo CACERES
 *
 * @param <T>
 *            Original type item
 */
public abstract class JsonListItemBinder<T> extends ListItemBinder<JsonObject, T> {
	
	/* Json Keys */
	public static final String JSON_KEY_KIND = "kind";
	public static final String JSON_KEY_ITEM = "item";
	
	@Override
	public String convertItemToString(T item) {
		JsonObject jsonObject = new JsonObject();
		
		jsonObject.put(JSON_KEY_KIND, getKind());
		jsonObject.put(JSON_KEY_ITEM, convert(item));
		
		return jsonObject.toJsonString();
	}
	
	@Override
	public T restoreItemFromString(String string) {
		JsonObject jsonObject;
		
		try {
			jsonObject = (JsonObject) new JsonParser().parse(string);
			
			if (!jsonObject.getString(JSON_KEY_KIND).equals(getKind())) {
				throw new IllegalStateException("Tried to restore item with wrong kind.");
			}
			
			jsonObject = jsonObject.getJsonObject(JSON_KEY_ITEM);
			
			if (jsonObject == null) {
				throw new NullPointerException();
			}
		} catch (Exception exception) {
			return null;
		}
		
		return restore(jsonObject);
	}
	
}