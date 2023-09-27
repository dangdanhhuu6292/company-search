package nl.devoorkant.sbdr.cir.data.repository;

import nl.devoorkant.sbdr.cir.data.model.CirAdres;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for Applicatie.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface CirAdresRepository extends JpaRepository<CirAdres, Integer>, CirAdresRepositoryCustom {

}
