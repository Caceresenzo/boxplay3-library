package caceresenzo.libs.boxplay.store.video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caceresenzo.libs.bytes.bitset.BigIntegerBitSet;
import caceresenzo.libs.empty.EmptyUtils;

public class TagsCorresponder {
	
	/* Constants */
	public static final int NO_TAG = -1;
	
	/* Variables */
	private final List<String> correspondances;
	
	/* Constructor */
	public TagsCorresponder(List<String> correspondances) {
		this.correspondances = correspondances;
	}
	
	/**
	 * @return Fetch tags to bit index correspondances
	 */
	public List<String> getTagsCorrespondances() {
		return correspondances;
	}
	
	/**
	 * Find a {@link List} of corresponding Tags that are enabled in the bitset
	 * 
	 * @param videoStoreElement
	 *            Target {@link BaseVideoStoreElement} you want to use
	 * @return A list of tags corresponding
	 */
	public List<String> findCorrespondances(BaseVideoStoreElement videoStoreElement) {
		return findCorrespondances(videoStoreElement.getTagsBitset());
	}
	
	/**
	 * Find a {@link List} of corresponding Tags that are enabled in the bitset
	 * 
	 * @param bitset
	 *            Source bitset to check
	 * @return A list of tags corresponding
	 */
	public List<String> findCorrespondances(BigIntegerBitSet bitset) {
		List<String> foundTags = new ArrayList<>();
		
		if (!EmptyUtils.validate(correspondances) || bitset == null || bitset.getValue().intValue() == 0) {
			return foundTags;
		}
		
		for (int index = 0; index < correspondances.size(); index++) {
			if (bitset.get(index)) {
				foundTags.add(correspondances.get(index));
			}
		}
		
		return foundTags;
	}
	
	/**
	 * Get a Tag bitset index by its name
	 * 
	 * @param tag
	 *            Targetted name
	 * @return Corresponding bitset index, or {@link #NO_TAG} ({@value #NO_TAG}) if not found
	 */
	public int findIndexByName(String tag) {
		List<Integer> indexes = getIndexesByName(Arrays.asList(tag));
		
		if (EmptyUtils.validate(indexes)) {
			return indexes.get(0);
		}
		
		return NO_TAG;
	}
	
	/**
	 * Get Tags bitset indexes by a tags list
	 * 
	 * @param itemTags
	 *            Tags list
	 * @return List of supposed bitset indexes
	 */
	public List<Integer> getIndexesByName(List<String> itemTags) {
		List<Integer> indexes = new ArrayList<>();
		
		if (!EmptyUtils.validate(itemTags)) {
			return indexes;
		}
		
		for (int index = 0; index < correspondances.size(); index++) {
			if (itemTags.contains(correspondances.get(index))) {
				indexes.add(index + 1);
			}
		}
		
		return indexes;
	}
	
}