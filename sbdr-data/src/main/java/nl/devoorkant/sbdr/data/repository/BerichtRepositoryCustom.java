package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.Bericht;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Extension for BerichtRepository bean.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Jan Meekel
 * @version         %I%
 */

public interface BerichtRepositoryCustom {

    @Query("FROM Bericht WHERE foutSoort = :foutSoort")
    List<Bericht> findByFoutSoort(@Param("foutSoort") String foutSoort);


}
