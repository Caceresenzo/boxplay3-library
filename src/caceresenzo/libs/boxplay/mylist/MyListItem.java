package caceresenzo.libs.boxplay.mylist;

import caceresenzo.libs.boxplay.mylist.binder.ListItemBinder;

public class MyListItem<T> implements MyListable {
	
	/* Variables */
	private final T item;
	private int position;
	
	/* Constructor */
	public MyListItem(T item, int position) {
		this.item = item;
		this.position = position;
	}
	
	/**
	 * @return Contained item.
	 */
	public T getItem() {
		return item;
	}
	
	/**
	 * Update the list position of this item.
	 * 
	 * @param position
	 *            New position.
	 * @return Itself
	 */
	public MyListItem<T> updatePosition(int position) {
		this.position = position;
		
		return this;
	}
	
	/**
	 * @return Item's position in a list.
	 */
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return "MyListItem[savedItem=" + item + "]";
	}
	
	@Override
	public String toUniqueString() {
		return item.toString();
	}
	
	@Override
	public ListItemBinder createCompatibleBinder() {
		return null;
	}
	
}