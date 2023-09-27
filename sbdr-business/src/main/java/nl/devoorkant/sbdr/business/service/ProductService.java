package nl.devoorkant.sbdr.business.service;

import nl.devoorkant.sbdr.business.util.FactuurRegelAggregate;
import nl.devoorkant.sbdr.data.model.Tarief;
import nl.devoorkant.sbdr.data.util.EProduct;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductService {

	void createFacturen() throws ServiceException;

	Tarief getCurrentTarief(EProduct product) throws ServiceException;

	List<FactuurRegelAggregate> getFactuurRegelAggregate(Integer factuurId) throws ServiceException;

	void makeDonation(Integer bedrijfId, Integer managedBedrijfId, Date datum, BigDecimal amount) throws ServiceException;

	void purchaseAchterstandCheck(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException;

	/**
	 * Purchase Monitoring product for specific Bedrijf
	 * This method must be called after a free 'trail' period
	 *
	 * @param bedrijfId
	 * @param managedBedrijfId
	 * @param datum
	 * @throws ServiceException
	 */
	void purchaseMonitoring(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException;

	/**
	 * Purchase Report product for specific Bedrijf
	 * The method must be called after a free 'trail' period
	 *
	 * @param bedrijfId
	 * @param managedBedrijfId
	 * @param datum
	 * @throws ServiceException
	 */
	void purchaseRapport(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException;
	
	/**
	 * Purchase Vermelding product for specific Bedrijf
	 * This method must be called after a free 'trial' period
	 * 
	 * @param bedrijfId
	 * @param managedBedrijfId
	 * @param datum
	 * @throws ServiceException
	 */
	void purchaseVermelding(Integer bedrijfId, Integer managedBedrijfId, Date datum) throws ServiceException;

	void removeMonitoringOfBedrijfFromFactuur(Integer bedrijfId) throws ServiceException;
}