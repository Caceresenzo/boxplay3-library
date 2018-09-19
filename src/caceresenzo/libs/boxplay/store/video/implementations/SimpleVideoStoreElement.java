package caceresenzo.libs.boxplay.store.video.implementations;

import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.bytes.bitset.LongBitSet;

public class SimpleVideoStoreElement extends BaseVideoStoreElement {
	
	public SimpleVideoStoreElement(long id, String title, String imageUrl, LongBitSet tags) {
		super(id, title, imageUrl, tags);
	}
	
}