package nl.devoorkant.sbdr.ws.transfer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericIntegerTransfer {

	private Integer val;

	public GenericIntegerTransfer() {}

	public GenericIntegerTransfer(Integer val) {this.val = val;}

	@XmlElement
	public Integer getVal() {return val;}

	public void setVal(Integer val) {this.val = val;}
}