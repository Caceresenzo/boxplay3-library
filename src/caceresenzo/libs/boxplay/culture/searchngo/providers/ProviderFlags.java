package caceresenzo.libs.boxplay.culture.searchngo.providers;

import java.util.EnumSet;
import java.util.Set;

import caceresenzo.libs.boxplay.culture.searchngo.subscription.ISubscribable;

public enum ProviderFlags {
	
	SUBSCRIBABLE(new FlagTester() {
		@Override
		public boolean test(ProviderManager provider) {
			return ISubscribable.class.isInstance(provider);
		}
	});
	
	private final FlagTester flagTester;
	
	private ProviderFlags(FlagTester flagTester) {
		this.flagTester = flagTester;
	}
	
	public FlagTester getFlagTester() {
		return flagTester;
	}
	
	/**
	 * Test is a {@link ProviderManager} has a set of flags
	 * 
	 * @param provider
	 *            Target provider
	 * @param flags
	 *            {@link EnumSet} of flags
	 * @return If the flags are present
	 */
	public static boolean test(ProviderManager provider, Set<ProviderFlags> flags) {
		if (flags.isEmpty()) {
			return true;
		}
		
		for (ProviderFlags flag : ProviderFlags.values()) {
			if (flag.getFlagTester().test(provider)) {
				return true;
			}
		}
		
		return false;
	}
	
	public interface FlagTester {
		
		boolean test(ProviderManager provider);
		
	}
	
}