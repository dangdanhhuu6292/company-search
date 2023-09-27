package nl.devoorkant.sbdr.business.util;

public class ApiKeyTuple {
	private String key;
	private String ipAddress;
	private long expires;
	private int requests;
	
	public ApiKeyTuple(String key, String ipAddress, long expires, int requests) {
		this.key = key;
		this.ipAddress = ipAddress;
		this.expires = expires;
		this.requests = requests;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}

	public int getRequests() {
		return requests;
	}

	public void setRequests(int requests) {
		this.requests = requests;
	}
	
	
}
