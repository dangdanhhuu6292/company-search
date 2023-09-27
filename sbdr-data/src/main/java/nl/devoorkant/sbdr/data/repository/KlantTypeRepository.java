package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.KlantType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for KlantType.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface KlantTypeRepository extends JpaRepository<KlantType, String>, KlantTypeRepositoryCustom {

}
