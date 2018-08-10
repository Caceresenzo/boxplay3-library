package caceresenzo.libs.boxplay.culture.searchngo.callback;

import java.util.Map;

import caceresenzo.libs.boxplay.culture.searchngo.result.SearchAndGoResult;

public interface SearchCallback {
	
	void onSearchStarting();
	
	void onSearchSorting();
	
	void onSearchFinished(Map<String, SearchAndGoResult> workmap);
	
	void onSearchFail(Exception exception);
	
}