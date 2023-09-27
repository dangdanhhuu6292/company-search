package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.SupportBestand;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for SupportFile.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2015. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author Coen Hasselaar
 * @version %I%
 */
public interface SupportBestandRepository extends JpaRepository<SupportBestand, String>, SupportBestandRepositoryCustom {
}
