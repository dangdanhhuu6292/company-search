package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.BedrijfManaged;
import nl.devoorkant.sbdr.data.model.Notitie;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for BedrijfManaged.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface NotitieRepository extends JpaRepository<Notitie, Integer>, NotitieRepositoryCustom {

}
