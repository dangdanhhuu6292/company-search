package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Rol;

public interface RolDataService {
   
    /**
     * Retrieves a Rol object, based on the passed rol code.
     *
     * @param code      a String representing a Rol
     * @return          a Rol object or null when the Rol object could not be retrieved.
     * @throws DataServiceException
     */
    Rol findByCode(String code) throws DataServiceException;    
}
