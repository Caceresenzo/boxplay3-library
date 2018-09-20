package caceresenzo.libs.boxplay.store.video.implementations;

import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;

public class SimpleVideoStoreElement extends BaseVideoStoreElement {
	
	public SimpleVideoStoreElement(long id, String title, String imageUrl, BigIntegerBitSet tags) {
		super(id, title, imageUrl, tags);
	}
	
}