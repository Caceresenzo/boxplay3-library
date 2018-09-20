package caceresenzo.libs.boxplay.store.video;

import caceresenzo.libs.boxplay.store.BaseStoreElement;
import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;

public class BaseVideoStoreElement extends BaseStoreElement {
	
	/* Constants */
	public static final int NO_ID = -1;
	
	/* Variables */
	protected final long id;
	protected final String title, imageUrl;
	protected final BigIntegerBitSet tags;
	
	/* Constructor */
	public BaseVideoStoreElement(long id, String title, String imageUrl, BigIntegerBitSet tags) {
		this.id = id;
		this.title = title;
		this.imageUrl = imageUrl;
		this.tags = tags;
	}
	
	/**
	 * @return Same id used in database for this object
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @return Actual video displayable title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @return Image url used as a thumbnail
	 */
	public String getImageUrl() {
		return imageUrl;
	}
	
	/**
	 * @return Unformatted {@link BigIntegerBitSet} used for tags
	 */
	public BigIntegerBitSet getTagsBitset() {
		return tags;
	}
	
	/* toString */
	@Override
	public String toString() {
		return "BaseVideoStoreElement[id=" + id + ", title=\"" + title + "\", imageUrl=\"" + imageUrl + "\", tags=" + tags + "]";
	}
	
}