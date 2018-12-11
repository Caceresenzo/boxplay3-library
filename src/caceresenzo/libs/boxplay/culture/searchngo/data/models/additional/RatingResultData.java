package caceresenzo.libs.boxplay.culture.searchngo.data.models.additional;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;

/**
 * Holder class, hold information about a rating
 * 
 * @author Enzo CACERES
 */
public class RatingResultData extends SimpleData implements DisplayableString {
	
	/* Constants */
	public static final int NO_VALUE = -1;
	
	/* Variables */
	private final float average;
	private final int best, votes;
	
	/**
	 * Constructor, create a new instance with a average rating value, the (best) max value possible, and the number of votes
	 * 
	 * @param average
	 *            Average rating
	 * @param best
	 *            Maximum value
	 * @param votes
	 *            Number of people
	 */
	public RatingResultData(float average, int best, int votes) {
		this.average = average;
		this.best = best;
		this.votes = votes;
	}
	
	/**
	 * Get the average rating value
	 * 
	 * @return Average value
	 */
	public float getAverage() {
		return average;
	}
	
	/**
	 * Get max rating value (best)
	 * 
	 * @return Rated on ...
	 */
	public int getBest() {
		return best;
	}
	
	/**
	 * Get the number of people that have vote
	 * 
	 * @return Number of votes
	 */
	public int getVotes() {
		return votes;
	}
	
	@Override
	public String convertToDisplayableString() {
		return String.format("%s/%s (%s)", getAverage(), getBest(), getVotes());
	}
	
	/**
	 * To String
	 */
	@Override
	public String toString() {
		return "RatingResultData[average=" + average + ", best=" + best + ", votes=" + votes + "]";
	}
	
}