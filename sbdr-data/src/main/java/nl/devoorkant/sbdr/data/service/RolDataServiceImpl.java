package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Rol;
import nl.devoorkant.sbdr.data.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;

@Service("rolDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class RolDataServiceImpl implements RolDataService {

	@Autowired
	RolRepository repository;

	@Override
	public Rol findByCode(String code) throws DataServiceException {
		try {
			List<Rol> rollen = repository.findByCode(code);

			if(rollen != null && rollen.size() == 1) return rollen.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

}
