package nl.devoorkant.sbdr.business.transfer;

import nl.devoorkant.sbdr.data.util.ERol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class GebruikersDetails implements UserDetails {
	private boolean accountNonExpired = true;
	private boolean accountNonLocked = true;
	private boolean actionsPresent = false;
	private boolean credentialsNonExpired = true;
	private boolean enabled = true;
	private GebruikerTransfer gebruiker = null;
	private LoginAllowed loginAllowed;
	private String mobileClientKey = null;
	private String signature = null;
	private boolean isMobileUser = false;
	private boolean isBedrijfManager = false;


	public GebruikersDetails(GebruikerTransfer gebruiker) {
		this.gebruiker = gebruiker;
	}

	public String getAfdeling() {
		return gebruiker.getAfdeling();
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> grantedAuths = new ArrayList();

		if(gebruiker.getRoles() != null) {
			GrantedAuthority grantedAuth = null;

			// one user role
			Boolean auth = (Boolean) gebruiker.getRoles().get(ERol.KLANT.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.KLANT.getCode()); //("admin_klant"); // ROLE_KLANT

			auth = (Boolean) gebruiker.getRoles().get(ERol.HOOFD.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.HOOFD.getCode()); //("hoofd_klant"); // ROLE_HOOFD

			auth = (Boolean) gebruiker.getRoles().get(ERol.GEBRUIKER.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.GEBRUIKER.getCode()); //("gebruiker_klant"); // ROLE_USER

			auth = (Boolean) gebruiker.getRoles().get(ERol.MANAGED.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.MANAGED.getCode()); //("bedrijf_manager"); // ROLE_MANAGED
			
			auth = (Boolean) gebruiker.getRoles().get(ERol.SBDR.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.SBDR.getCode()); // ("admin_sbdr"); // ROLE_SBDR

			auth = (Boolean) gebruiker.getRoles().get(ERol.SBDRHOOFD.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.SBDRHOOFD.getCode()); // ("admin_sbdr_hoofd"); // ROLE_SBDR_HOOFD

			if (grantedAuth != null)
				grantedAuths.add(grantedAuth);
			
			// plus may be registratiestoegestaan role
			auth = (Boolean) gebruiker.getRoles().get(ERol.REGISTRATIESTOEGESTAAN.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.REGISTRATIESTOEGESTAAN.getCode()); // ("registraties_toegestaan"); // ROLE_REGISTRATIESTOEGESTAAN
			
			grantedAuths.add(grantedAuth);
			
			auth = (Boolean) gebruiker.getRoles().get(ERol.APITOEGESTAAN.getCode());
			if(auth != null && auth)
				grantedAuth = new SimpleGrantedAuthority(ERol.APITOEGESTAAN.getCode()); // ("api_toegestaan"); // ROLE_REGISTRATIESTOEGESTAAN
			
			grantedAuths.add(grantedAuth);
		}

		return grantedAuths;
	}

	@Override
	public String getPassword() {
		return gebruiker.getWachtwoord();
	}

	@Override
	public String getUsername() {
		return gebruiker.getGebruikersNaam();
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public Integer getBedrijfId() {
		return gebruiker.getBedrijfId();
	}

	public String getBedrijfNaam() {
		return gebruiker.getBedrijfNaam();
	}

	public String getBedrijfTelefoonNummer() {
		return gebruiker.getBedrijfTelefoonNummer();
	}

	public String getEmailAdres() {
		return gebruiker.getEmailAdres();
	}

	public String getFirstName() {
		return gebruiker.getVoornaam();
	}

	public String getFunctie() {
		return gebruiker.getFunctie();
	}

	public String getGebruikerTelefoonNummer() {
		return gebruiker.getTelefoonNummer();
	}

	public String getGeslacht() {return gebruiker.getGeslacht();}

	public String getLastName() {
		return gebruiker.getNaam();
	}

	public LoginAllowed getLoginAllowed() {
		return loginAllowed;
	}

	public void setLoginAllowed(LoginAllowed loginAllowed) {
		this.loginAllowed = loginAllowed;
	}

	public String getMobileClientKey() {
		return mobileClientKey;
	}

	public void setMobileClientKey(String mobileClientKey) {
		this.mobileClientKey = mobileClientKey;
	}

	public Integer getShowHelp() {
		return gebruiker.getShowHelp();
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Integer getUserId() {
		return gebruiker.getGebruikersId();
	}

	public boolean isActionsPresent() {
		return actionsPresent;
	}

	public void setActionsPresent(boolean actionsPresent) {
		this.actionsPresent = actionsPresent;
	}

	public boolean isAdresOk() {
		return gebruiker.isAdresOk();
	}

	public boolean isKlant() {
		return gebruiker.isKlant();
	}

	public boolean isProspect() {
		return gebruiker.isProspect();
	}

	public boolean isMobileUser() {
		return isMobileUser;
	}

	public void setMobileUser(boolean usernameIsMCK) {
		this.isMobileUser = usernameIsMCK;
	}

	public boolean isBedrijfManager() {
		return isBedrijfManager;
	}

	public void setBedrijfManager(boolean isBedrijfManager) {
		this.isBedrijfManager = isBedrijfManager;
	}	
}
