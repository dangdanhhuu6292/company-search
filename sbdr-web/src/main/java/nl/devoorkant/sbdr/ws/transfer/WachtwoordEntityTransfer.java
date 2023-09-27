package nl.devoorkant.sbdr.ws.transfer;

import java.lang.reflect.InvocationTargetException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.business.transfer.IdentifierAdapter;
import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.data.model.Wachtwoord;
import nl.devoorkant.sbdr.idobfuscator.util.BeanCopyUtils;

@XmlRootElement
public class WachtwoordEntityTransfer extends Wachtwoord {
	private static final Logger LOGGER = LoggerFactory.getLogger(WachtwoordEntityTransfer.class);
	
	public WachtwoordEntityTransfer() {
		
	}
	
	public WachtwoordEntityTransfer(Wachtwoord wachtwoord) {
		try {
			if (wachtwoord != null)
				BeanUtils.copyProperties(this, wachtwoord);
				// set not relevant properties to null
		} catch (IllegalAccessException e) {
			LOGGER.error("Cannot create WachtwoordEntityTransfer object");
		} catch (InvocationTargetException e) {
			LOGGER.error("Cannot create WachtwoordEntityTransfer object");
		}
	}
	
	public Wachtwoord getDataObject() {
		Wachtwoord wachtwoord = new Wachtwoord();
		try {
			BeanCopyUtils.copyProperties(wachtwoord, this);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("Cannot getDataObject from WachtwoordEntityTransfer object");
		}
		return wachtwoord;
	}	
	
	@Override
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type=String.class)
	public Integer getWachtwoordId() {
		return super.getWachtwoordId();
	}

	public void setWachtwoordId(Integer WachtwoordId) {
		super.setWachtwoordId(WachtwoordId);
	}	
}
