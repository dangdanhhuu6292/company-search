package nl.devoorkant.sbdr.data.repository;

import javax.annotation.Resource;

import nl.devoorkant.sbdr.data.model.Applicatie;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Applicatie.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

@Resource(name="applicatieRepository")
public interface ApplicatieRepository extends JpaRepository<Applicatie, Integer> {

}
