package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKCurator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKCuratorRepository extends JpaRepository<CIKvKCurator, Integer>, CIKvKCuratorRepositoryCustom{

}