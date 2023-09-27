package nl.devoorkant.insolventie.client;

import java.util.Date;

public class CirParameters {
	private String configuratieWaarde;
	private Date tijdLaatsteSynchronisatie;
	private String username;
	private String password;
	
	public CirParameters(String configuratieWaarde, Date tijdLaatsteSynchronisatie, String username, String password) {
		this.configuratieWaarde = configuratieWaarde;
		this.tijdLaatsteSynchronisatie = tijdLaatsteSynchronisatie;
		this.username = username;
		this.password = password;
	}

	public String getConfiguratieWaarde() {
		return configuratieWaarde;
	}

	public void setConfiguratieWaarde(String configuratieWaarde) {
		this.configuratieWaarde = configuratieWaarde;
	}

	public Date getTijdLaatsteSynchronisatie() {
		return tijdLaatsteSynchronisatie;
	}

	public void setTijdLaatsteSynchronisatie(Date tijdLaatsteSynchronisatie) {
		this.tijdLaatsteSynchronisatie = tijdLaatsteSynchronisatie;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}
