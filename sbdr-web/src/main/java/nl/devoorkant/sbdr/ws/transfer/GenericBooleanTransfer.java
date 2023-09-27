package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericBooleanTransfer {

	private boolean val;

	public GenericBooleanTransfer() {}

	public GenericBooleanTransfer(boolean val) {this.val = val;}

	@XmlElement
	public boolean isVal() {return val;}

	public void setVal(boolean val) {this.val = val;}
}