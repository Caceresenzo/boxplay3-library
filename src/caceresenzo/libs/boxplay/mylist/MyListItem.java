package caceresenzo.libs.boxplay.mylist;

public class MyListItem<T> implements MyListable {
	
	private final T savedItem;
	
	public MyListItem(T savedItem) {
		this.savedItem = savedItem;
	}
	
	public T getSavedItem() {
		return savedItem;
	}
	
	@Override
	public String toString() {
		return "MyListItem[savedItem=" + savedItem + "]";
	}
	
	@Override
	public String toUniqueString() {
		return savedItem.toString();
	}
	
}