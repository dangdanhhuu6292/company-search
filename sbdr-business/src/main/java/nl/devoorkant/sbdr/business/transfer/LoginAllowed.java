package nl.devoorkant.sbdr.business.transfer;

import java.util.Date;

public class LoginAllowed {
	public static final int MAX_LOGINATTEMPTS = 5;
	public static final int MAX_BLOCKING_MINUTES = 10;
	public static final int MAX_ACTIVETOKEN_MINUTES = 20;
	
	
	public static final int LOGIN_ALLOWED = 1;
	public static final int LOGIN_NOTALLOWED_ACCOUNTDISABLED = 2;
	public static final int LOGIN_NOTALLOWED_ACCOUNTBLOCKED = 3;
	public static final int LOGIN_NOTALLOWED_NOGEBRUIKER = 4;
	public static final int LOGIN_NOTALLOWED_NOKLANT = 5;
	public static final int LOGIN_NOTALLOWED_NOBEDRIJF = 6;
	public static final int LOGIN_NOTACTIVATED = 7;
	
	private boolean isKlant = false;
	private int loginAllowed = -1;
	private Date datumLaatsteAanmeldpoging;
	private short nrAanmeldpogingen;
	
	public LoginAllowed() {
		
	}

	public int getLoginAllowed() {
		return loginAllowed;
	}

	public void setLoginAllowed(int loginAllowed) {
		this.loginAllowed = loginAllowed;
	}

	public Date getDatumLaatsteAanmeldpoging() {
		return datumLaatsteAanmeldpoging;
	}

	public void setDatumLaatsteAanmeldpoging(Date datumLaatsteAanmeldpoging) {
		this.datumLaatsteAanmeldpoging = datumLaatsteAanmeldpoging;
	}

	public short getNrAanmeldpogingen() {
		return nrAanmeldpogingen;
	}

	public void setNrAanmeldpogingen(short nrAanmeldpogingen) {
		this.nrAanmeldpogingen = nrAanmeldpogingen;
	}

	public boolean isKlant() {
		return isKlant;
	}

	public void setKlant(boolean isKlant) {
		this.isKlant = isKlant;
	}
	
	
}
