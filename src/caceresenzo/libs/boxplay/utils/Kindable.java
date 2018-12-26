package caceresenzo.libs.boxplay.utils;

/**
 * Allow an object to have a kind that lust be unique and easely recognisable.<br>
 * You also need to declare a <code>public static final String KIND</code> and define his as the object's kind.
 * 
 * @author Enzo CACERES
 */
public interface Kindable {
	
	/**
	 * @return A unique kind identifier for this object.
	 */
	public String getKind();
	
}