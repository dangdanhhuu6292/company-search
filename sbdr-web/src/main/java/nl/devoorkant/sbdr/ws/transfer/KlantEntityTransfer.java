package nl.devoorkant.sbdr.ws.transfer;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.business.transfer.IdentifierAdapter;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Klant;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.idobfuscator.util.BeanCopyUtils;

@XmlRootElement
public class KlantEntityTransfer extends Klant {
	private static final Logger LOGGER = LoggerFactory.getLogger(KlantEntityTransfer.class);
	
	private Integer gebruikerWrapperId;
	private Integer bedrijfBedrijfWrapperId;
	
	public KlantEntityTransfer() {
		
	}
	
	public KlantEntityTransfer(Klant klant) {
		try {
			if (klant != null) {
				BeanCopyUtils.copyProperties(this, klant);
				// set not relevant properties to null
				super.setDatumAangemaakt(null);
				super.setAfdeling(null);
				super.setDatumLaatsteAanmelding(null);
				super.setDatumLaatsteAanmeldpoging(null);
				super.setDatumLaatsteLogout(null);
				super.setEmailAdres(null);
				super.setEmailAdresFacturatie(null);
				super.setFunctie(null);
				super.setNrAanmeldPogingen(null);
				super.setWachtwoordWachtwoordId(null);
				super.setActivatieReminderSent(false);
				super.setActivatieCode(null);
				super.setAkkoordIncasso(false);
				super.setBankAccountGuid(null);
				super.setBriefBatchBriefBatchId(null);
				super.setBriefStatusCode(null);
				super.setBtwnummer(null);
				super.setContactGuid(null);
				super.setDatumVerwerktExactOnline(null);
				super.setEersteFactuurVerzonden(false);
				super.setKlantStatusCode(null);
				super.setNietBtwPlichtig(false);
				super.setNrGebruikers(null);
				// duplicate ids to wrapper elements for deserializing
				this.gebruikerWrapperId = super.getGebruikerId();
				this.bedrijfBedrijfWrapperId = super.getBedrijfBedrijfId();
			}
		} catch (IllegalAccessException e) {
			LOGGER.error("Cannot create KlantEntityTransfer object");
		} catch (InvocationTargetException e) {
			LOGGER.error("Cannot create KlantEntityTransfer object");
		}
	}
	
	public Klant getDataObject() {
		Klant klant = new Klant();
		try {
			BeanCopyUtils.copyProperties(klant, this);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("Cannot getDataObject from KlantEntityTransfer object");
		}
		return klant;
	}	
	
	@Override
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getGebruikerId() {
		return super.getGebruikerId();
	}	
	
	@Override    
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
    public Integer getBedrijfBedrijfId() {
        return super.getBedrijfBedrijfId();
    }

	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getGebruikerWrapperId() {
		return this.gebruikerWrapperId;
	}
	
	public void setGebruikerWrapperId(Integer gebruikerId) {
		this.gebruikerWrapperId = gebruikerId;
		super.setGebruikerId(gebruikerId);
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getBedrijfBedrijfWrapperId() {
		return this.bedrijfBedrijfWrapperId;
	}
	
	public void setBedrijfBedrijfId(Integer bedrijfBedrijfId) {
		this.bedrijfBedrijfWrapperId = bedrijfBedrijfId;
		super.setBedrijfBedrijfId(bedrijfBedrijfId);
	}	
}
