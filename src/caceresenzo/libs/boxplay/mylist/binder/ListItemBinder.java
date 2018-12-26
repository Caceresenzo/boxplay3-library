package caceresenzo.libs.boxplay.mylist.binder;

import java.io.Serializable;

import caceresenzo.libs.boxplay.utils.Kindable;

/**
 * Item binder to save and restore from a type T object to a serializable type S.
 * 
 * @author Enzo CACERES
 *
 * @param <S>
 *            Serializable object
 * @param <T>
 *            Original type item
 */
public abstract class ListItemBinder<S extends Serializable, T> implements Kindable {
	
	/**
	 * From an item T, convert it to a simple {@link Serializable} object.
	 * 
	 * @param item
	 *            Source item to convert.
	 * @return Converted item.
	 */
	protected abstract S convert(T item);
	
	/**
	 * Same as {@link #convert(Object)}, but inversed.<br>
	 * From a {@link Serializable} object, restore the original item.
	 * 
	 * @param source
	 *            Source {@link Serializable}.
	 * @return Original item.
	 */
	protected abstract T restore(S source);
	
	/**
	 * Proxy function to call {@link #convert(Object)} indirectly.<br>
	 * This allow implementations of the class to do work before handling work.
	 * 
	 * @param item
	 *            Original item.
	 * @return Converted item as a string.
	 * @throws IllegalStateException
	 *             If the function has not been implemented by an implementations.
	 */
	public String convertItemToString(T item) {
		throw new IllegalStateException("Not implemented.");
	}
	
	/**
	 * Proxy function to call {@link #restore(Object)} indirectly.<br>
	 * This allow implementations of the class to do work before handling work.
	 * 
	 * @param string
	 *            Converted string.
	 * @return Reconstructed original item.
	 * @throws IllegalStateException
	 *             If the function has not been implemented by an implementations.
	 */
	public T restoreItemFromString(String string) {
		throw new IllegalStateException("Not implemented.");
	}
	
}