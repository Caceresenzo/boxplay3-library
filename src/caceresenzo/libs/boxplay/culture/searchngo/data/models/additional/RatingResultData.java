package caceresenzo.libs.boxplay.culture.searchngo.data.models.additional;

import caceresenzo.libs.boxplay.culture.searchngo.data.AdditionalResultData.DisplayableString;
import caceresenzo.libs.boxplay.culture.searchngo.data.models.SimpleData;
import caceresenzo.libs.json.JsonObject;

/**
 * Holder class, hold information about a rating.
 * 
 * @author Enzo CACERES
 */
public class RatingResultData extends SimpleData implements DisplayableString {
	
	/* Json Key */
	public static final String JSON_KEY_AVERAGE = "average";
	public static final String JSON_KEY_BEST = "best";
	public static final String JSON_KEY_VOTES = "votes";
	public static final String JSON_KEY_CONSTANTS = "constants";
	public static final String JSON_KEY_CONSTANT_NO_VALUE = "no_value";
	
	/* Constants */
	public static final String KIND = "rating";
	public static final int NO_VALUE = -1;
	
	/* Variables */
	private final float average;
	private final int best, votes;
	
	/**
	 * Create a new instance with a average rating value, the (best) max value possible, and the number of votes.
	 * 
	 * @param average
	 *            Average rating.
	 * @param best
	 *            Maximum value.
	 * @param votes
	 *            Number of people.
	 */
	public RatingResultData(float average, int best, int votes) {
		super(KIND);
		
		this.average = average;
		this.best = best;
		this.votes = votes;
	}
	
	/** @return Rating's average value. */
	public float getAverage() {
		return average;
	}
	
	/** @return Rating's best rating value. */
	public int getBest() {
		return best;
	}
	
	/** @return Rating's number of votes. */
	public int getVotes() {
		return votes;
	}
	
	@Override
	public String convertToDisplayableString() {
		return String.format("%s/%s (%s)", getAverage(), getBest(), getVotes());
	}
	
	@Override
	public JsonObject toJsonObject() {
		JsonObject jsonObject = super.toJsonObject();
		JsonObject constantsJsonObject = new JsonObject();
		
		jsonObject.put(JSON_KEY_AVERAGE, average);
		jsonObject.put(JSON_KEY_BEST, best);
		jsonObject.put(JSON_KEY_VOTES, votes);
		
		jsonObject.put(JSON_KEY_CONSTANTS, constantsJsonObject);
		constantsJsonObject.put(JSON_KEY_CONSTANT_NO_VALUE, NO_VALUE);
		
		return jsonObject;
	}
	
	@Override
	public String toString() {
		return "RatingResultData[average=" + average + ", best=" + best + ", votes=" + votes + "]";
	}
	
}