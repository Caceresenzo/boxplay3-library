package caceresenzo.libs.boxplay.store.video.implementations.series;

import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.bytes.bitset.LongBitSet;

public class SeriesSeasonVideoStoreElement extends BaseVideoStoreElement {
	
	public SeriesSeasonVideoStoreElement(long id, String title, String imageUrl, LongBitSet tags) {
		super(id, title, imageUrl, tags);
	}
	
}