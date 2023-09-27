package nl.devoorkant.sbdr.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BedrijfManaged;
import nl.devoorkant.sbdr.data.repository.BedrijfManagedRepository;

@Service("bedrijfManagedDataService")
@Transactional(readOnly = true)
public class BedrijfManagedDataServiceImpl implements BedrijfManagedDataService {
	private static Logger LOGGER = LoggerFactory.getLogger(BedrijfManagedDataService.class);
	
	@Autowired
	private BedrijfManagedRepository bedrijfManagedRepository;	
	
    @Override
    @Transactional	
	public BedrijfManaged save(BedrijfManaged bedrijfManaged) throws DataServiceException {
		BedrijfManaged result = null;

		if (bedrijfManaged != null) {
			try {
				result = bedrijfManagedRepository.save(bedrijfManaged);
			} catch (DataServiceException e) {
				throw new DataServiceException("Error in BedrijfManaged database transaction: " + e.getMessage());
			} catch (Exception e) {
				LOGGER.error("Error saving bedrijfManaged: " + e.getMessage());
				throw new DataServiceException("Error saving BedrijfManaged");
			}

			return result;
		} else throw new DataServiceException("Cannot save BedrijfManaged as null");

	}
}
