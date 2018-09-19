package caceresenzo.libs.boxplay.store.video.implementations;

import java.util.List;

import caceresenzo.libs.boxplay.store.video.BaseVideoStoreElement;
import caceresenzo.libs.boxplay.store.video.implementations.series.SeriesSeasonVideoStoreElement;
import caceresenzo.libs.bytes.bitset.LongBitSet;

public class SeriesVideoStoreElement extends BaseVideoStoreElement {
	
	/* Variables */
	private final List<SeriesSeasonVideoStoreElement> seasons;
	
	/* Constructor */
	public SeriesVideoStoreElement(long id, String title, String imageUrl, LongBitSet tags, List<SeriesSeasonVideoStoreElement> seasons) {
		super(id, title, imageUrl, tags);
		
		this.seasons = seasons;
	}
	
	/**
	 * Check if this {@link SeriesVideoStoreElement} has a least one season
	 * 
	 * @return If the seasons list is not null and not empty
	 */
	public boolean hasSeasons() {
		return seasons != null && !seasons.isEmpty();
	}
	
	/**
	 * @return Actual seasons for this {@link SeriesVideoStoreElement}
	 */
	public List<SeriesSeasonVideoStoreElement> getSeasons() {
		return seasons;
	}
	
}