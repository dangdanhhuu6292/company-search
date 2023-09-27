package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.MeldingBatch;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository bean for MeldingBatch.
 * <p/>
 * EDO - Applicatie voor het verwerken van MeldingBatch
 * <p/>
 * Copyright:       Copyright (c) 2014. De Voorkant B.V. <br>
 * Company:         De Voorkant B.V.
 *
 * @author          Martijn Bruinenberg
 * @version         %I%
 */

public interface MeldingBatchRepository extends JpaRepository<MeldingBatch, Integer> {
}
