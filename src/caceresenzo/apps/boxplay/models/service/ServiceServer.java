package caceresenzo.apps.boxplay.models.service;

public class ServiceServer extends ServiceElement {
	
	protected ServiceServer(String identifier) {
		super(identifier);
	}
	
	@Override
	public String toString() {
		return "service//server//" + identifier;
	}
	
}