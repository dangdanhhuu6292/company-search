package nl.devoorkant.sbdr.data.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.query.Param;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Factuur;
import nl.devoorkant.sbdr.data.model.FactuurRegel;
import nl.devoorkant.sbdr.data.util.EProduct;

public interface FactuurDataService {

	FactuurRegel addFactuurRegel(Integer bedrijfId, Integer managedBedrijfId, String productCode, BigDecimal bedrag, Date datum) throws DataServiceException;

	Factuur findByFactuurId(Integer fId) throws DataServiceException;

	Factuur findByReferentie(String referentie) throws DataServiceException;

	List<FactuurRegel> findRapportFactuurRegelsByFactuurId(Integer factuurId) throws DataServiceException;
	
	List<FactuurRegel> findVermeldingFactuurRegelsByFactuurId(Integer factuurId) throws DataServiceException;

	List<FactuurRegel> findFactuurRegelsByFactuurId(@Param("factuurId") Integer factuurId) throws DataServiceException;
	
	Factuur findById(Integer factuurId) throws DataServiceException;

	List<Factuur> getAllFacturenToProcessExactOnline() throws DataServiceException;

	List<Factuur> getAllOpenFacturen(Date datumNieuweFactuur) throws DataServiceException;

	List<Object[]> getFactuurRegelAggregate(Integer factuurId) throws DataServiceException;

	Factuur getOpenFactuurOfBedrijf(Integer bedrijfId) throws DataServiceException;
	
	List<Factuur> findAllFacturenByBedrijfId(Integer bedrijfId) throws DataServiceException;

	FactuurRegel getOpenFactuurRegelOfBedrijf(Integer bedrijfId, EProduct product) throws DataServiceException;

	void removeOpenFactuurRegel(FactuurRegel factuurRegel) throws DataServiceException;

	Factuur save(Factuur factuur) throws DataServiceException;

	FactuurRegel saveFactuurRegel(FactuurRegel fR) throws DataServiceException;

	BigDecimal getTotalOfFactuur(Integer factuurId) throws DataServiceException;
}
