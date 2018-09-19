package caceresenzo.libs.boxplay.api.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caceresenzo.libs.empty.EmptyUtils;
import caceresenzo.libs.string.StringUtils;

public class RequestSettings {
	
	/* Constants */
	public static final int UNDEFINED = -1;
	public static final int DEFAULT_LIMIT = 10000;
	
	/* Variables */
	private final int page, limit;
	private final List<Integer> include, exclude;
	private final List<String> searchTerms;
	
	/* Constructor */
	private RequestSettings(int page, int limit, List<Integer> include, List<Integer> exclude, List<String> searchTerms) {
		this.page = page;
		this.limit = limit;
		this.include = include;
		this.exclude = exclude;
		this.searchTerms = searchTerms;
	}
	
	/**
	 * @return Target page to fetch
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * @return Limit of object to get
	 */
	public int getLimit() {
		return limit;
	}
	
	/**
	 * @return Get included tags
	 */
	public List<Integer> getInclude() {
		return include;
	}
	
	/**
	 * @return Get excluded tags
	 */
	public List<Integer> getExclude() {
		return exclude;
	}
	
	/**
	 * @return Wanted search query terms
	 */
	public List<String> getSearchTerms() {
		return searchTerms;
	}
	
	public String createUrl(String sourceUrl) {
		StringBuilder builder = new StringBuilder(sourceUrl);
		
		if (!sourceUrl.contains("?")) {
			builder.append("?");
		}
		
		if (page != UNDEFINED) {
			builder.append("&page=").append(page);
		}
		
		if (limit != UNDEFINED) {
			builder.append("&limit=").append(limit);
		}
		
		if (EmptyUtils.validate(include)) {
			builder.append("&include=").append(StringUtils.join(include, ","));
		}
		
		if (EmptyUtils.validate(exclude)) {
			builder.append("&exclude=").append(StringUtils.join(exclude, ","));
		}
		
		if (EmptyUtils.validate(searchTerms)) {
			builder.append("&search=").append(StringUtils.join(searchTerms, ","));
		}
		
		return builder.toString();
	}
	
	/**
	 * Builder for the class {@link RequestSettings}
	 * 
	 * @author Enzo CACERES
	 */
	public static class Builder {
		
		/* Variables */
		private int page = UNDEFINED, limit = DEFAULT_LIMIT;
		private List<Integer> include = new ArrayList<>(), exclude = new ArrayList<>();
		private List<String> searchTerms = new ArrayList<>();
		
		/* Constructor */
		public Builder() {
			;
		}
		
		/**
		 * @param page
		 *            New target page to set
		 * @return Itself
		 */
		public Builder page(int page) {
			this.page = page;
			
			return this;
		}
		
		/**
		 * @param limit
		 *            New limit for max object to get
		 * @return Itself
		 */
		public Builder limit(int limit) {
			this.limit = limit;
			
			return this;
		}
		
		/**
		 * @param tag
		 *            Tag to exclude
		 * @return Itself
		 */
		public Builder include(int tag) {
			return include(new Integer[] { tag });
		}
		
		/**
		 * @param tags
		 *            Tags array to exclude
		 * @return Itself
		 */
		public Builder include(Integer[] tags) {
			return include(Arrays.asList(tags));
		}
		
		/**
		 * @param tags
		 *            Tags list to exclude
		 * @return Itself
		 */
		public Builder include(List<Integer> tags) {
			this.include.addAll(tags);
			
			return this;
		}
		
		/**
		 * @param tag
		 *            Tag to exclude
		 * @return Itself
		 */
		public Builder exclude(int tag) {
			return exclude(new Integer[] { tag });
		}
		
		/**
		 * @param tags
		 *            Tags array to exclude
		 * @return Itself
		 */
		public Builder exclude(Integer[] tags) {
			return exclude(Arrays.asList(tags));
		}
		
		/**
		 * @param tags
		 *            Tags list to exclude
		 * @return Itself
		 */
		public Builder exclude(List<Integer> tags) {
			this.exclude.addAll(tags);
			
			return this;
		}
		
		/**
		 * @param term
		 *            Term to search
		 * @return Itself
		 */
		public Builder search(String term) {
			return search(new String[] { term });
		}
		
		/**
		 * @param terms
		 *            Terms array to search
		 * @return Itself
		 */
		public Builder search(String[] terms) {
			return search(Arrays.asList(terms));
		}
		
		/**
		 * @param terms
		 *            Terms list to search
		 * @return Itself
		 */
		public Builder search(List<String> terms) {
			this.searchTerms.addAll(terms);
			
			return this;
		}
		
		/**
		 * @return An instance of the {@link RequestSettings} with provided settings of this {@link Builder}
		 */
		public RequestSettings build() {
			return new RequestSettings(page, limit, include, exclude, searchTerms);
		}
	}
	
}