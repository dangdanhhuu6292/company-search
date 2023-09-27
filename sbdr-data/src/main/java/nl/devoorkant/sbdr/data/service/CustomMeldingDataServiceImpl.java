package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.CustomMelding;
import nl.devoorkant.sbdr.data.repository.CustomMeldingRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("customMeldingDataService")
public class CustomMeldingDataServiceImpl implements CustomMeldingDataService {

	@Autowired
	private CustomMeldingRepository customMeldingRepository;
	
	@Override
	public CustomMelding findById(Integer customMeldingId)
			throws DataServiceException {
		try {
			Optional<CustomMelding> customMelding = customMeldingRepository.findById(customMeldingId);
			return customMelding != null ? customMelding.get() : null;
		} catch (Exception e)
		{
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public CustomMelding findByBedrijfId(Integer customMeldingId, Integer bedrijfId)
			throws DataServiceException {
		try {
			return customMeldingRepository.findByCustomMeldingIdBedrijfId(customMeldingId, bedrijfId);
		} catch (Exception e)
		{
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public CustomMelding save(CustomMelding customMelding) throws DataServiceException {
		try {
			return customMeldingRepository.save(customMelding);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

}
