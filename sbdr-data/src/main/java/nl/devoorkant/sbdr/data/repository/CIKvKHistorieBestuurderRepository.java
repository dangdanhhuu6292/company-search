package nl.devoorkant.sbdr.data.repository;

import nl.devoorkant.sbdr.data.model.CIKvKHistorieBestuurder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CIKvKHistorieBestuurderRepository extends JpaRepository<CIKvKHistorieBestuurder, Integer>, CIKvKHistorieBestuurderRepositoryCustom {
}
