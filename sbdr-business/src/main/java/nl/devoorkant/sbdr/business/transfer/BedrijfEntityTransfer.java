package nl.devoorkant.sbdr.business.transfer;

import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.devoorkant.sbdr.data.model.Bedrijf;
import nl.devoorkant.sbdr.idobfuscator.util.BeanCopyUtils;

@XmlRootElement
public class BedrijfEntityTransfer extends Bedrijf {
	private static final Logger LOGGER = LoggerFactory.getLogger(BedrijfEntityTransfer.class);
	
	private Integer bedrijfWrapperId;
	private Integer ciKvKDossierWrapperId;
	
	public BedrijfEntityTransfer() {
		
	}
	
	public BedrijfEntityTransfer(Bedrijf bedrijf) {
		try {
			if (bedrijf != null) {
				BeanCopyUtils.copyProperties(this, bedrijf);
				// set all not relevant properties to null
				super.setDatumVerwerktExactOnline(null);
				//super.setCIKvKDossierCikvKdossierId(null);
				super.setBedrijfStatusCode(null);
				super.setDatumWijziging(null);
				super.setAccountGuid(null);
				super.setHandmatigGewijzigd(false);
				super.setRapportCounter(null);
				// duplicate ids in wrapper elements for deserializing
				this.bedrijfWrapperId = super.getBedrijfId();
				this.ciKvKDossierWrapperId = super.getCIKvKDossierCikvKdossierId();
			}
		} catch (IllegalAccessException e) {
			LOGGER.error("Cannot create BedrijfEntityTransfer object");
		} catch (InvocationTargetException e) {
			LOGGER.error("Cannot create BedrijfEntityTransfer object");
		}
	}
	
	public Bedrijf getDataObject() {
		Bedrijf bedrijf = new Bedrijf();
		try {
			BeanCopyUtils.copyProperties(bedrijf, this);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOGGER.error("Cannot getDataObject from BedrijfEntityTransfer object");
		}
		return bedrijf;
	}

	@Override
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getCIKvKDossierCikvKdossierId() {
		return super.getCIKvKDossierCikvKdossierId();
	}

	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getCiKvKDossierWrapperId() {
		return ciKvKDossierWrapperId;
	}

	public void setCiKvKDossierWrapperId(Integer ciKvKDossierWrapperId) {
		this.ciKvKDossierWrapperId = ciKvKDossierWrapperId;
		super.setCIKvKDossierCikvKdossierId(ciKvKDossierWrapperId);
	}

	@Override
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getBedrijfId() {
		return super.getBedrijfId();
	}
	
	@XmlElement
	@XmlJavaTypeAdapter(value=IdentifierAdapter.class, type = Integer.class)
	public Integer getBedrijfWrapperId() {
		return bedrijfWrapperId;
	}

	public void setBedrijfWrapperId(Integer bedrijfId) {
		this.bedrijfWrapperId = bedrijfId;
		super.setBedrijfId(bedrijfId);
	}	
}
