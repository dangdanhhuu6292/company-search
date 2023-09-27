package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.BedrijfManaged;

public interface BedrijfManagedDataService {

	/**
	 * Save the state of the provided
	 * BedrijfManaged object into the data
	 * repository.
	 *
	 * @param bedrijf bedrijfManaged to save
	 *
	 * @return BedrijfManaged
	 */
	BedrijfManaged save(BedrijfManaged bedrijfManaged) throws DataServiceException;

}
