package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Gebruiker;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Gebruiker.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface GebruikerRepository extends JpaRepository<Gebruiker, Integer>, GebruikerRepositoryCustom {
    	
}
