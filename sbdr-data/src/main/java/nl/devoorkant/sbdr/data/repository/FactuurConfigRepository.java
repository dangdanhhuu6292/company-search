package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.FactuurConfig;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Factuur.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface FactuurConfigRepository extends JpaRepository<FactuurConfig, Integer>, FactuurConfigRepositoryCustom {

}
