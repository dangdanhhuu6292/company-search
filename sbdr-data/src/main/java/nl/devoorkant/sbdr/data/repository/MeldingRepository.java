package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Melding;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Melding.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface MeldingRepository extends JpaRepository<Melding, Integer>, MeldingRepositoryCustom {

}
