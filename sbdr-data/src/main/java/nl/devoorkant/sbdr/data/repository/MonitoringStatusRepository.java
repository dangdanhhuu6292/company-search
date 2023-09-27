package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.MonitoringStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for MonitoringStatus.
 * <p/>
 * SBDR - Stichting Bedrijfskrediet Data Registratie
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Bas Dekker
 * @version         %I%
 */

public interface MonitoringStatusRepository extends JpaRepository<MonitoringStatus, String>, MonitoringStatusRepositoryCustom {

}
