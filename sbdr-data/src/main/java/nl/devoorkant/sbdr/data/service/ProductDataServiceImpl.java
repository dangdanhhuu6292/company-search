package nl.devoorkant.sbdr.data.service;

import nl.devoorkant.sbdr.data.DataServiceException;
import nl.devoorkant.sbdr.data.model.Product;
import nl.devoorkant.sbdr.data.model.Tarief;
import nl.devoorkant.sbdr.data.repository.TariefRepository;
import nl.devoorkant.util.StringUtil;
import nl.devoorkant.validation.ValidationConstants;
import nl.devoorkant.validation.ValidationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

@Service("productDataService")
public class ProductDataServiceImpl implements ProductDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDataServiceImpl.class);

    @Autowired
    private nl.devoorkant.sbdr.data.repository.ProductRepository productRepository;
    
    @Autowired
    private TariefRepository tariefRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Product findByPrimaryKey(String code) throws DataServiceException {

        try {
            if (StringUtil.isNotEmptyOrNull(code)) {
                Optional<Product> product = productRepository.findById(code);
                return product != null ? product.get() : null;
            } else {
                LOGGER.debug("Cannot retrieve Product without a key.");
                return null;
            }
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Product> findByActief(boolean actief) throws DataServiceException {

        try {
            return productRepository.findByActief(actief);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }

    @Override
    public Product findByProductCode(String productCode) throws DataServiceException {

        try {
            return productRepository.findByProductCode(productCode);
        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }
    }    
    
    @Override
    public List<Tarief> findTariefByProdCodeGeldigVanaf(String productCode, Date datum) throws DataServiceException {
    	try {
    		return tariefRepository.findByProductCodeGeldigVanaf(productCode, datum, true);
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    @Override
    public List<Object[]> findAllActiveMonitoringTarief(Date factuurDatumVorig, Date factuurDatum) throws DataServiceException {
    	try {
    		return productRepository.findAllActiveMonitoringTarief(factuurDatumVorig, factuurDatum);    		
    	} catch (Exception e) {
    		throw new DataServiceException(e.getMessage());
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Product save(Product Product) throws DataServiceException {
        LOGGER.info("Start.");
        Product result = null;

        try {
            if(validate(Product).isValid()) {
                result = productRepository.save(Product);

            } else {
                LOGGER.warn("Product is ongeldig en kan niet opgeslagen worden.");
            }

        } catch (Exception e) {
            throw new DataServiceException(e.getMessage());
        }

        return result;
    }

    /**
     * Checks the validity of an Product Entity.<br/>
     * <p/>
     * An Product Object must apply to the following rules:<br/>
     * <ol>
     * <li> The wachtwoordStatus must already exist (only updates are allowed)</li>
     * <li> The field omschrijving must contain a value</li>
     * </ol>
     *
     * @param Product 	the Product Entity to validate.
     * @return                  a {@link nl.devoorkant.validation.ValidationObject} containing the validation results.
     */
    private ValidationObject validate(Product Product) {
        ValidationObject loValidation = new ValidationObject();

        try {
            if(Product != null) {

                // 1. The Product must already exist.
                if (StringUtil.isEmptyOrNull(Product.getCode())) {
                    loValidation.addMessage("Alleen wijzigingen zijn toegestaan.", ValidationConstants.MessageType.INVALID);
                }

                // 2. The field omschrijving must contain a value
                if(StringUtil.isEmptyOrNull(Product.getOmschrijving())) {
                    loValidation.addMessage(Product.getOmschrijving(), "Omschrijving is verplicht.", ValidationConstants.MessageType.INVALID);
                }

            } else {
                loValidation.addMessage("Geen Product ontvangen.", ValidationConstants.MessageType.INVALID);
                LOGGER.error("No Product received.");
            }
            LOGGER.info("Number of messages = " + loValidation.getValidationMessages().size());

        } catch(Exception loEx) {
            loValidation.addMessage("Validatie mislukt", ValidationConstants.MessageType.ERROR);
            LOGGER.error("Validation failed.");
        }
        return loValidation;
    }

}
