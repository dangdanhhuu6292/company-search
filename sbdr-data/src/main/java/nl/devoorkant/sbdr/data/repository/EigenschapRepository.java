package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Eigenschap;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Eigenschap.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface EigenschapRepository extends JpaRepository<Eigenschap, String>, EigenschapRepositoryCustom {

}
