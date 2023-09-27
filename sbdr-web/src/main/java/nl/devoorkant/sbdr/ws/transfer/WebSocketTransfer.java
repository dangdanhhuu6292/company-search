package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebSocketTransfer {
	@XmlElement	public String webSocketUri;
	
	public WebSocketTransfer() {
		
	}
	
	public WebSocketTransfer(String webSocketUri) {
		this.webSocketUri = webSocketUri;
	}
}
