package nl.devoorkant.sbdr.data.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.model.FactuurRegel;
import nl.devoorkant.sbdr.data.repository.FactuurRegelRepository;
import nl.devoorkant.sbdr.data.repository.FactuurRepository;
import nl.devoorkant.sbdr.data.util.EProduct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

@Service("factuurDataService")
@Transactional(readOnly = true, propagation=Propagation.SUPPORTS)
public class FactuurDataServiceImpl implements FactuurDataService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FactuurDataServiceImpl.class);
	@Autowired
	FactuurRegelRepository factuurRegelRepository;
	@Autowired
	FactuurRepository factuurRepository;

	@Override
	@Transactional
	public FactuurRegel addFactuurRegel(Integer bedrijfId, Integer managedBedrijfId, String productCode, BigDecimal bedrag, Date datum) throws DataServiceException {
		FactuurRegel factuurRegel = null;

		try {
			Factuur factuur = getOrCreateFactuur(bedrijfId);

			factuurRegel = new FactuurRegel();
			factuurRegel.setFactuur(factuur);
			factuurRegel.setManagedBedrijfBedrijfId(managedBedrijfId);
			factuurRegel.setProductCode(productCode);
			factuurRegel.setBedrag(bedrag);
			factuurRegel.setDatum(datum);

			factuurRegel = factuurRegelRepository.save(factuurRegel);

			return factuurRegel;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Factuur findByFactuurId(Integer fId) throws DataServiceException {
		if(fId != null) {
			try {
				return factuurRepository.findByFactuurId(fId);
			} catch(Exception e) {
				throw new DataServiceException(e.getMessage());
			}
		} else throw new DataServiceException("Error in findByFactuurId: Parameter fId(Integer) cannot be null");
	}

	@Override
	public Factuur findByReferentie(String referentie) throws DataServiceException {
		try {
			List<Factuur> facturen = factuurRepository.findByReferentie(referentie);

			if(facturen != null && facturen.size() == 1) return facturen.get(0);
			else return null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Factuur findById(Integer factuurId) throws DataServiceException {
		try {
			Optional<Factuur> factuur = factuurRepository.findById(factuurId);
			return factuur != null ? factuur.get() : null;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Factuur> getAllFacturenToProcessExactOnline() throws DataServiceException {
		try {
			return factuurRepository.findAllFacturenToProcessExactOnline();

		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Factuur> getAllOpenFacturen(Date datumNieuweFactuur) throws DataServiceException {
		try {
			if (datumNieuweFactuur == null)
				return factuurRepository.findAllOpenFacturen();
			else
				return factuurRepository.findAllOpenFacturen(datumNieuweFactuur);

		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<Object[]> getFactuurRegelAggregate(Integer factuurId) throws DataServiceException {
		try {
			return factuurRepository.factuurRegelAggregate(factuurId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public Factuur getOpenFactuurOfBedrijf(Integer bedrijfId) throws DataServiceException {
		try {
			List<Factuur> facturen = factuurRepository.findOpenFactuurByBedrijfId(bedrijfId);
			
			if (facturen != null && facturen.size() == 1)
				return facturen.get(0);
			else
				throw new DataServiceException("Multipe rows returned of Factuur in getOpenFactuurOfBedrijf");

		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<Factuur> findAllFacturenByBedrijfId(Integer bedrijfId) throws DataServiceException {
		try {
			return factuurRepository.findAllFacturenByBedrijfId(bedrijfId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}		
	}

	@Override
	public FactuurRegel getOpenFactuurRegelOfBedrijf(Integer bedrijfId, EProduct product) throws DataServiceException {
		try {
			return factuurRepository.findFactuurRegelByProductCodeBedrijfId(product.getCode(), bedrijfId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void removeOpenFactuurRegel(FactuurRegel factuurRegel) throws DataServiceException {
		try {
			factuurRegelRepository.delete(factuurRegel);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public Factuur save(Factuur factuur) throws DataServiceException {
		try {
			return factuurRepository.save(factuur);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public List<FactuurRegel> findRapportFactuurRegelsByFactuurId(Integer factuurId) throws DataServiceException {
		try {
			return factuurRepository.findRapportFactuurRegelsByFactuurId(factuurId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<FactuurRegel> findVermeldingFactuurRegelsByFactuurId(Integer factuurId) throws DataServiceException {
		try {
			return factuurRepository.findVermeldingFactuurRegelsByFactuurId(factuurId);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}	
	
	@Override
	public List<FactuurRegel> findFactuurRegelsByFactuurId(@Param("factuurId") Integer factuurId) throws DataServiceException {
		try {
			return factuurRepository.findFactuurRegelsByFactuurId(factuurId);
		} catch (Exception e) {
			throw new DataServiceException(e.getMessage());
		}
		
	}

	@Override
	@Transactional
	public FactuurRegel saveFactuurRegel(FactuurRegel fR) throws DataServiceException {
		try {
			return factuurRegelRepository.save(fR);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	@Override
	public BigDecimal getTotalOfFactuur(Integer factuurId) throws DataServiceException{
		BigDecimal result = new BigDecimal(0);
		result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		try{
			List<FactuurRegel> regels = factuurRepository.findFactuurRegelsByFactuurId(factuurId);
			
			for (FactuurRegel regel : regels) {
				// DONATIE = No VAT
				if (regel.getProductCode().equals(EProduct.DONATIE.getCode()))
					result = result.add(regel.getBedrag());
				else {
					BigDecimal btwpct = new BigDecimal(.21);
					BigDecimal incBtw = regel.getBedrag().add(regel.getBedrag().multiply(btwpct).setScale(2, BigDecimal.ROUND_HALF_UP));
					result = result.add(incBtw);
				}
			}
			
			return result;
			
		} catch(Exception e){
			throw new DataServiceException(e.getMessage());
		}
	}

	@Transactional
	private Factuur createNewFactuur(Integer bedrijfId) throws DataServiceException {

		try {
			Factuur factuur = new Factuur();

			factuur.setBedrijfBedrijfId(bedrijfId);

			return factuurRepository.save(factuur);
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}

	private Factuur getOrCreateFactuur(Integer bedrijfId) throws DataServiceException {
		try {
			Factuur factuur = null;			
			List<Factuur> facturen = factuurRepository.findOpenFactuurByBedrijfId(bedrijfId);

			if(facturen == null || facturen.size() == 0)
				factuur = createNewFactuur(bedrijfId);
			else
				factuur = facturen.get(0); // multiple rows may not occur in PROD

			return factuur;
		} catch(Exception e) {
			throw new DataServiceException(e.getMessage());
		}
	}


}

