package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Tarief;

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

public interface TariefRepositoryCustom {

    /**
     * Returns all Tarief objects with the requested product + valid date + active value
     *
     * @param productCode 	productCode of tarief
     * @param datum			valid date of tarief
     * @param actief    	a Boolean containing the requested value for actief
     * @return          	a List of Tarief objects, or null if not available
     */
    @Query("FROM Tarief WHERE product.code = :productCode AND geldigVanaf <= :datum AND actief = :actief ORDER BY geldigVanaf DESC")
    List<Tarief> findByProductCodeGeldigVanaf(@Param("productCode") String productCode, @Param("datum") Date datum, @Param("actief") Boolean actief);

}
