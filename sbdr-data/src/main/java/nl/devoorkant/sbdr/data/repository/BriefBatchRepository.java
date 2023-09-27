package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.BriefBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BriefBatchRepository extends JpaRepository<BriefBatch, Integer>, BriefBatchRepositoryCustom {

}
