package nl.devoorkant.sbdr.data.repository;

import java.util.List;

import nl.devoorkant.sbdr.data.model.FactuurConfig;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

public interface FactuurConfigRepositoryCustom {

    @Query("FROM FactuurConfig WHERE productCode = :productCode")
    FactuurConfig findByProductCode(@Param("productCode") String productCode);
        
    @Query("FROM FactuurConfig WHERE product.actief = 1")
    List<FactuurConfig> findAllActive();
    
    @Query("FROM FactuurConfig WHERE facturatieFrequentie.code = :freq")
    List<FactuurConfig> findAllOfFrequency(@Param("freq") String freq);
    
}
