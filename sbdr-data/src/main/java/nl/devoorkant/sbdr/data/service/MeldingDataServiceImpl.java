package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Melding;
import nl.devoorkant.sbdr.data.repository.MeldingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service("meldingDataService")
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class MeldingDataServiceImpl implements MeldingDataService {
	@Autowired
	MeldingRepository meldingRepository;

	@Override
	public Page<Melding> findActiveMeldingenOfBedrijf(Integer bedrijfId, Pageable pageable) throws DataServiceException {
		Page<Melding> result = null;

		try {
			if (bedrijfId != null) {
				result = meldingRepository.findActiveMeldingenOfBedrijf(bedrijfId, pageable);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findAllMeldingenOfBedrijf(Integer bedrijfId) throws DataServiceException {
		List<Melding> result = null;

		try {
			if (bedrijfId != null) {
				result = meldingRepository.findAllMeldingenOfBedrijf(bedrijfId);
			}

			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Melding> findAllFromDate(Date fromDate) throws DataServiceException {
		List<Melding> result = null;
		
		try {
			if (fromDate != null) {
				result = meldingRepository.findAllFromDate(fromDate);
			}
			
			return result;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Melding findById(Integer meldingId) throws DataServiceException {
		try {
			Optional<Melding> melding = meldingRepository.findById(meldingId);
			return melding != null ? melding.get() : null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findByReferentieNummer(Integer meldingId, Integer bedrijfId, String referentie) throws DataServiceException {
		try {
			if (meldingId != null)
				return meldingRepository.findByReferentieNummerNotMeldingId(meldingId, bedrijfId, referentie);
			else return meldingRepository.findByReferentieNummer(bedrijfId, referentie);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Melding findByReferentieNummerIntern(String referentie) throws DataServiceException {
		try {
			List<Melding> meldingen = meldingRepository.findByReferentieNummerIntern(referentie);

			if (meldingen != null && meldingen.size() == 1) return meldingen.get(0);
			else return null;
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findBygebruikerIdOfCompany(Integer gebruikerId) throws DataServiceException {
		try {
			return meldingRepository.findBygebruikerIdOfCompany(gebruikerId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findMeldingenBetweenCompaniesByMeldingId(Integer meldingId) throws DataServiceException {
		try {
			return meldingRepository.findMeldingenBetweenCompaniesByMeldingId(meldingId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findMeldingenOfDocumentByMeldingId(Integer meldingId) throws DataServiceException {
		try {
			return meldingRepository.findMeldingenOfDocumentByMeldingId(meldingId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findNewMeldingenBetweenDates(Date startDate, Date endDate) throws DataServiceException {
		try {
			return meldingRepository.findNewMeldingenBetweenDates(startDate, endDate);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findNewMeldingenOfBedrijfAboutBedrijf(Integer vanBedrijfId, Integer overBedrijfId) throws DataServiceException {
		try {
			return meldingRepository.findNewMeldingenOfBedrijfAboutBedrijf(vanBedrijfId, overBedrijfId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findOverdueNewMeldingen(int daysOverdue) throws DataServiceException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -daysOverdue);
			Date overdueDate = cal.getTime();

			return meldingRepository.findOverdueNewMeldingen(overdueDate);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Melding> findOverdueNewMeldingenOfBedrijfAboutBedrijf(Integer bedrijfIdDoor, Integer bedrijfIdOver, int daysOverdue) throws DataServiceException {
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -daysOverdue);
			Date overdueDate = cal.getTime();

			return meldingRepository.findOverdueNewMeldingenOfBedrijf(bedrijfIdDoor, bedrijfIdOver, overdueDate);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Melding save(Melding melding) throws DataServiceException {
		try {
			return meldingRepository.saveAndFlush(melding);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

}
