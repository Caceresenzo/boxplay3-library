package caceresenzo.libs.boxplay.mylist;

import java.io.Serializable;

import caceresenzo.libs.boxplay.mylist.binder.ListItemBinder;

public interface MyListable extends Serializable {
	
	String toUniqueString();
	
	@SuppressWarnings("rawtypes")
	ListItemBinder createCompatibleBinder();
	
}