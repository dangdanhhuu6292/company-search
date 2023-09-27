package nl.devoorkant.sbdr.business.data;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldData {
	private String recipient;
	
	private String body;
	
	private String sender;

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
}
