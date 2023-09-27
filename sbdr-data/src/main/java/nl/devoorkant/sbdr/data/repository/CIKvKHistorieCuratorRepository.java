package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieCurator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKHistorieCuratorRepository extends JpaRepository<CIKvKHistorieCurator, Integer>, CIKvKHistorieCuratorRepositoryCustom {
}
