package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Wachtwoord;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Wachtwoord.
 * <p/>
 * EDO - Applicatie voor het verwerken van Export Documenten
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface WachtwoordRepository extends JpaRepository<Wachtwoord, Integer>, WachtwoordRepositoryCustom {
}
