package nl.devoorkant.sbdr.data.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.ContactMoment;
import nl.devoorkant.sbdr.data.model.Notitie;
import nl.devoorkant.sbdr.data.repository.NotitieRepository;
import nl.devoorkant.sbdr.data.util.ENotitieType;

@Service("notitieDataService")
@Transactional(readOnly = true)
public class NotitieDataServiceImpl implements NotitieDataService {
	private static Logger LOGGER = LoggerFactory.getLogger(NotitieDataService.class);
	
	@Autowired
	private NotitieRepository notitieRepository;	
	
	@Override
	public Notitie findById(Integer notitieId) throws DataServiceException {
		try {
			Optional<Notitie> notitie = notitieRepository.findById(notitieId);
			return notitie != null ? notitie.get() : null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	
	
	@Override
	public Notitie findByNotitieMeldingGebruiker(Integer meldingId, Integer gebruikerId, String notitieType) throws DataServiceException {
		try {
			// notitieType == Gebruiker || notitieType == ContactMoment
			if (gebruikerId != null && (notitieType.equals(ENotitieType.CONTACT_MOMENT.getCode()) || notitieType.equals(ENotitieType.MELDING_GEBRUIKER.getCode())) )
				return notitieRepository.findByNotitieMeldingGebruikerWithGebruikerId(meldingId, gebruikerId, notitieType);
			else if (gebruikerId == null && (notitieType.equals(ENotitieType.CONTACT_MOMENT.getCode()) || notitieType.equals(ENotitieType.MELDING_GEBRUIKER.getCode())))
				return notitieRepository.findByNotitieMeldingGebruiker(meldingId, notitieType);
			else if (gebruikerId == null && notitieType.equals(ENotitieType.MELDING_ADMIN.getCode()))
				return notitieRepository.findByNotitieMeldingAdmin(meldingId, ENotitieType.MELDING_ADMIN.getCode());
			else
				return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
    @Override
    @Transactional	
	public Notitie save(Notitie notitie) throws DataServiceException {
		Notitie result = null;

		if (notitie != null) {
			try {
				result = notitieRepository.save(notitie);
			} catch (DataServiceException e) {
				throw new DataServiceException("Error in Notitie database transaction: " + e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Error saving notitie: " + e.getMessage());
				throw new DataServiceException("Error saving Notitie");
			}

			return result;
		} else throw new DataServiceException("Cannot save Notitie as null");

	}
}
