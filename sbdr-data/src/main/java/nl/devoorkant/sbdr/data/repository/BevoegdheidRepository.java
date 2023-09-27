package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Bevoegdheid;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Bevoegdheid.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface BevoegdheidRepository extends JpaRepository<Bevoegdheid, Integer>, BevoegdheidRepositoryCustom {

}
