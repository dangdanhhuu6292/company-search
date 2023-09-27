package nl.devoorkant.sbdr.business.transfer;

import javax.xml.bind.annotation.XmlElement;
import java.util.Set;

public class KvkBestuurderTransfer {
	private String aanhef;
	private String naam;
	private Set<KvkBestuurderFunctieTransfer> functies;

	public KvkBestuurderTransfer() {

	}

	public KvkBestuurderTransfer(String aanhef, String naam, Set<KvkBestuurderFunctieTransfer> functies) {
		this.aanhef = aanhef;
		this.naam = naam;
		this.functies = functies;
	}

	@XmlElement
	public String getAanhef() {
		return aanhef;
	}

	public void setAanhef(String aanhef) {
		this.aanhef = aanhef;
	}

	@XmlElement
	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	@XmlElement
	public Set<KvkBestuurderFunctieTransfer> getFuncties() {
		return functies;
	}

	public void setFuncties(Set<KvkBestuurderFunctieTransfer> functies) {
		this.functies = functies;
	}
}
