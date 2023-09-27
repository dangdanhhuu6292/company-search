package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Product;
import nl.devoorkant.sbdr.data.model.Tarief;

import java.util.Date;
import java.util.List;

/**
 * Data Service bean with implemented functionality for Product.
 * <p/>
 * SBDR - Applicatie voor Bedrijfskrediet Data Registratie
 * <p/>
 *
 * Defines the functionality that the ProductDataService must implement to support interaction between a Business Object and
 * the ProductRepository
 *
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface ProductDataService {
    /**
     * Retrieves a BedrijfStatus object, based on the passed gebruiker_ID.
     *
     * @param code      a String representing a Product
     * @return          a Product object or null when the Product object could not be retrieved.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Product findByPrimaryKey(String code) throws DataServiceException;

    /**
     * Finds a List of {@link nl.devoorkant.sbdr.data.model.Product} objects, based on the passed proces_ID.
     *
     * @param actief    a boolean indicating the status of Actief
     * @return          a List of {@link nl.devoorkant.sbdr.data.model.Product} objects or null when no Product objects are available.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    List<Product> findByActief(boolean actief) throws DataServiceException;

    /**
     * Saves the passed Product Entity.<br/>
     * <p/>
     * For this type of entity only updates are allowed. Creation of entities is not allowed.<br/>
     *
     * The Product Entity passed as argument will be validated {@link #validate(nl.devoorkant.sbdr.data.model.Product)} first.The validation can have two possible
     * outcomes:<br/>
     * <ol>
     * <li> Valid. The Product indicated by the code will be updated. The update can have two possible results:<br/>
     * <ol>
     * <li> Successful. The saved entity is stored as ResultObject in the Result Object.</li>
     * <li> Not successful. The reasons for the failure are stored as ValidationMessages in the ValidationObject, part of the Result Object.</li>
     * </ol>
     * <li> Not valid. The reasons are stored as ValidationMessages in the ValidationObject, part of the Result Object, and the action is aborted.</li>
     * </ol>
     *
     * @param product 	the {@link nl.devoorkant.sbdr.data.model.Product} object to save.
     * @return 			            the saved {@link nl.devoorkant.sbdr.data.model.Product} object.
     * @throws nl.devoorkant.sbdr.data.DataServiceException
     */
    Product save(Product product) throws DataServiceException;

	Product findByProductCode(String productCode) throws DataServiceException;

	List<Tarief> findTariefByProdCodeGeldigVanaf(String productCode, Date datum)
			throws DataServiceException;

	List<Object[]> findAllActiveMonitoringTarief(Date factuurDatumVorig, Date factuurDatum) throws DataServiceException;
    
}
