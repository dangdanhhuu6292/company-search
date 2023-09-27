package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Product;
import nl.devoorkant.sbdr.data.model.Tarief;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Extension for ProductRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface ProductRepositoryCustom {

    /**
     * Returns all Product objects with the requested actief value
     *
     * @param actief    a Boolean containing the requested value for actief
     * @return          a List of Product objects with the requested actief value, or null if not available
     */
    @Query("FROM Product WHERE actief = :actief ORDER BY omschrijving ASC")
    List<Product> findByActief(@Param("actief") Boolean actief);

    /**
     * Returns all Product objects with the requested productCode value
     *
     * @param productCode   ProductCode of product
     * @return          	an active Product object, or null if not available
     */
    @Query("FROM Product WHERE product = :productCode AND actief = 1")
    Product findByProductCode(@Param("productCode") String productCode);

    
    /**
     * Returns a Page containing Product objects with the requested omschrijving.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Product objects, or NULL when no Product objects could be retrieved.
     */
    @Query("SELECT p FROM Product p WHERE p.omschrijving = :omschrijving")
    Page<Product> findByOmschrijving(@Param("omschrijving") String omschrijving, Pageable pageable);

    /**
     * Returns a Page containing Product objects where the omschrijving contains the requested value.<br/>
     *
     * @param omschrijving  a String containing the requested value for omschrijving
     * @param pageable      a Pageable, containing the parameters for creating the requested Page
     * @return 		        a Page with Product objects, or NULL when no Product objects could be retrieved.
     */
    @Query("SELECT p FROM Product p WHERE p.omschrijving like :omschrijving")
    Page<Product> findByOmschrijvingLike(@Param("omschrijving") String omschrijving, Pageable pageable);
    
    /**
     * Returns all Tarief objects with the requested product + valid date + active value
     *
     * @param productCode 	productCode of tarief
     * @param datum			valid date of tarief
     * @param actief    	a Boolean containing the requested value for actief
     * @return          	a List of Tarief objects, or null if not available
     */
    @Query("FROM Product WHERE product = :productCode AND geldigVanaf <= :datum AND actief = :actief")
    List<Tarief> findByProductCodeGeldigVanaf(@Param("productCode") String productCode, @Param("") Date datum, @Param("actief") Boolean actief);
    
   
    /**
     * Find all current monitorings + tarief of Bedrijf for currenct invoice (>= factuurdatumvorig + < factuurdatum)
     * 
     * @param bedrijfId
     * @param datumActive
     * @return
     */
    @Query("FROM Monitoring m, Tarief t1, Tarief t2\n" + // monitoring, start tarief, latest tarief
    	   "WHERE m.datumStart >= t1.geldigVanaf\n" +
    	   "AND (m.datumEinde is null OR (:factuurDatumVorig is null OR m.datumEinde >= :factuurDatumVorig))\n" + //  monitoring is active or 'was active'
    	   "AND m.datumStart < :factuurDatum\n" + // no future monitoring recs 
    	   "AND t1.geldigVanaf <= current_date()\n" + // tarief startdate is not in future
    	   "AND (m.datumEinde is null OR addDays(m.datumStart, t1.dagenTrial) <= :factuurDatum)\n" + // monitoring is 'payable'
    	   "AND t1.product.code = 'MON'\n" + // product is monitoring
    	   "AND t1.actief = 1\n" +
    	   "AND :factuurDatum >= t2.geldigVanaf\n" + // current tarief for specific invoice
    	   "AND t2.product.code = 'MON'\n" +
    	   "AND t2.actief = 1\n" +
    	   "AND NOT EXISTS (FROM Tarief t3 WHERE t3.actief = 1 AND t3.product.code = 'MON' AND t3.tariefId <> t1.tariefId AND t3.geldigVanaf > t1.geldigVanaf AND m.datumStart >= t3.geldigVanaf AND (m.datumEinde is null OR addDays(m.datumStart, t3.dagenTrial) <= :factuurDatum))\n" +
		   "AND NOT EXISTS (FROM Tarief t4 WHERE t4.actief = 1 AND t4.product.code = 'MON' AND t4.tariefId <> t2.tariefId AND t4.geldigVanaf > t2.geldigVanaf AND :factuurDatum >= t4.geldigVanaf)\n")
    List<Object[]> findAllActiveMonitoringTarief(@Param("factuurDatumVorig") Date factuurDatumVorig, @Param("factuurDatum") Date factuurDatum);


}
